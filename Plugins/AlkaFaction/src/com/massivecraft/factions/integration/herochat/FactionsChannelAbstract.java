package com.massivecraft.factions.integration.herochat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.ChannelStorage;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.MessageFormatSupplier;
import com.dthielke.herochat.MessageNotFoundException;
import com.dthielke.herochat.util.Messaging;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;

public abstract class FactionsChannelAbstract implements Channel {
    private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
    private final ChannelStorage storage = Herochat.getChannelManager().getStorage();
    private final MessageFormatSupplier formatSupplier = Herochat.getChannelManager();

    public FactionsChannelAbstract() {

    }

    @Override
    public boolean addMember(final Chatter chatter, final boolean announce, final boolean flagUpdate) {
        if (chatter.hasChannel(this)) return false;
        chatter.addChannel(this, announce, flagUpdate);
        return true;
    }

    @Override
    public boolean kickMember(final Chatter chatter, final boolean announce) {
        if (!chatter.hasChannel(this)) return false;
        this.removeMember(chatter, false, true);

        if (announce) {
            try {
                this.announce(Herochat.getMessage("channel_kick").replace("$1", chatter.getPlayer().getDisplayName()));
            } catch (final MessageNotFoundException e) {
                Herochat.severe("Messages.properties is missing: channel_kick");
            }
        }

        return true;
    }

    @Override
    public boolean removeMember(final Chatter chatter, final boolean announce, final boolean flagUpdate) {
        if (!chatter.hasChannel(this)) return false;
        chatter.removeChannel(this, announce, flagUpdate);
        return true;
    }

    @Override
    public Set<Chatter> getMembers() {
        final Set<Chatter> ret = new HashSet<Chatter>();
        for (final Chatter chatter : Herochat.getChatterManager().getChatters())
            if (chatter.hasChannel(this)) {
                ret.add(chatter);
            }
        return ret;
    }

    @Override
    public void announce(String message) {
        message = this.applyFormat(this.formatSupplier.getAnnounceFormat(), "").replace("%2$s", message);
        for (final Chatter member : this.getMembers()) {
            member.getPlayer().sendMessage(message);
        }
        Herochat.logChat(ChatColor.stripColor(message));
    }

    @Override
    public String applyFormat(String format, final String originalFormat) {
        format = format.replace("{default}", this.formatSupplier.getStandardFormat());
        format = format.replace("{name}", this.getName());
        format = format.replace("{nick}", this.getNick());
        format = format.replace("{color}", this.getColor().toString());
        format = format.replace("{msg}", "%2$s");

        final Matcher matcher = FactionsChannelAbstract.msgPattern.matcher(originalFormat);
        if (matcher.matches() && matcher.groupCount() == 3) {
            format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
        } else {
            format = format.replace("{sender}", "%1$s");
        }

        format = format.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
        return format;
    }

    @Override
    public String applyFormat(String format, final String originalFormat, final Player sender) {
        format = this.applyFormat(format, originalFormat);
        format = format.replace("{plainsender}", sender.getName());
        format = format.replace("{world}", sender.getWorld().getName());
        final Chat chat = Herochat.getChatService();
        if (chat != null) {
            try {
                final String prefix = chat.getPlayerPrefix(sender);
                final String suffix = chat.getPlayerSuffix(sender);
                final String group = chat.getPrimaryGroup(sender);
                final String groupPrefix = group == null ? "" : chat.getGroupPrefix(sender.getWorld(), group);
                final String groupSuffix = group == null ? "" : chat.getGroupSuffix(sender.getWorld(), group);
                format = format.replace("{prefix}", prefix == null ? "" : prefix.replace("%", "%%"));
                format = format.replace("{suffix}", suffix == null ? "" : suffix.replace("%", "%%"));
                format = format.replace("{group}", group == null ? "" : group.replace("%", "%%"));
                format = format.replace("{groupprefix}", groupPrefix == null ? "" : groupPrefix.replace("%", "%%"));
                format = format.replace("{groupsuffix}", groupSuffix == null ? "" : groupSuffix.replace("%", "%%"));
            } catch (final UnsupportedOperationException ignored) {}
        } else {
            format = format.replace("{prefix}", "");
            format = format.replace("{suffix}", "");
            format = format.replace("{group}", "");
            format = format.replace("{groupprefix}", "");
            format = format.replace("{groupsuffix}", "");
        }
        format = format.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
        return format;
    }

    @Override
    public void emote(final Chatter sender, String message) {
        message = this.applyFormat(this.formatSupplier.getEmoteFormat(), "").replace("%2$s", message);
        final Set<Player> recipients = new HashSet<Player>();
        for (final Chatter member : this.getMembers()) {
            recipients.add(member.getPlayer());
        }

        this.trimRecipients(recipients, sender);

        final Player player = sender.getPlayer();

        if (!this.isMessageHeard(recipients, sender)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        Messaging.send(player, Herochat.getMessage("channel_alone"));
                    } catch (final MessageNotFoundException e) {
                        Herochat.severe("Messages.properties is missing: channel_alone");
                    }
                }
            }, 1L);
        } else {
            for (final Player p : recipients) {
                p.sendMessage(message);
            }
        }
    }

    @Override
    public boolean isMuted(final String name) {
        if (this.isMuted()) return true;
        return this.getMutes().contains(name.toLowerCase());
    }

    public abstract Set<Rel> getTargetRelations();

    public Set<Player> getRecipients(final Player sender) {
        final Set<Player> ret = new HashSet<Player>();

        final FPlayer fpsender = FPlayers.i.get(sender);
        final Faction faction = fpsender.getFaction();
        ret.addAll(faction.getOnlinePlayers());

        for (final FPlayer fplayer : FPlayers.i.getOnline())
            if (this.getTargetRelations().contains(faction.getRelationTo(fplayer))) {
                ret.add(fplayer.getPlayer());
            }

        return ret;
    }

    @Override
    public void processChat(final ChannelChatEvent event) {
        final Player player = event.getSender().getPlayer();

        final String format = this.applyFormat(event.getFormat(), event.getBukkitFormat(), player);

        final Chatter sender = Herochat.getChatterManager().getChatter(player);
        final Set<Player> recipients = this.getRecipients(player);

        this.trimRecipients(recipients, sender);
        final String msg = String.format(format, player.getDisplayName(), event.getMessage());
        if (!this.isMessageHeard(recipients, sender)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        Messaging.send(player, Herochat.getMessage("channel_alone"));
                    } catch (final MessageNotFoundException e) {
                        Herochat.severe("Messages.properties is missing: channel_alone");
                    }
                }
            }, 1L);
        }

        for (final Player recipient : recipients) {
            recipient.sendMessage(msg);
        }

        Herochat.logChat(msg);
    }

    /*@Override
    public void processChat(ChannelChatEvent event)
    {
    	final Player player = event.getSender().getPlayer();

    	String format = applyFormat(event.getFormat(), event.getBukkitFormat(), player);

    	Chatter sender = Herochat.getChatterManager().getChatter(player);
    	Set<Player> recipients = new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));

    	trimRecipients(recipients, sender);
    	if (!isMessageHeard(recipients, sender))
    	{
    		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable()
    		{
    			public void run()
    			{
    				try
    				{
    					Messaging.send(player, Herochat.getMessage("channel_alone"));
    				}
    				catch (MessageNotFoundException e)
    				{
    					Herochat.severe("Messages.properties is missing: channel_alone");
    				}
    			}
    		}, 1L);
    	}
    	
    	FPlayer fplayer = FPlayers.i.get(player);
    	
    	String formatWithoutColor = FactionsChatListener.parseTags(format, player, fplayer);
    	
    	//String msg = String.format(format, player.getDisplayName(), event.getMessage());
    	

    	for (Player recipient : recipients)
    	{
    		String finalFormat;
    		if ( ! Conf.chatParseTags || Conf.chatTagHandledByAnotherPlugin)
    		{
    			finalFormat = format;
    		}
    		else if (! Conf.chatParseTagsColored)
    		{
    			finalFormat = formatWithoutColor;
    		}
    		else
    		{
    			FPlayer frecipient = FPlayers.i.get(recipient);
    			finalFormat = FactionsChatListener.parseTags(format, player, fplayer, recipient, frecipient);
    		}
    		String msg = String.format(finalFormat, player.getDisplayName(), event.getMessage());
    		recipient.sendMessage(msg);
    	}

    	Herochat.logChat(String.format(formatWithoutColor, player.getDisplayName(), event.getMessage()));
    }*/

    public boolean isMessageHeard(final Set<Player> recipients, final Chatter sender) {
        if (!this.isLocal()) return true;

        final Player senderPlayer = sender.getPlayer();
        for (final Player recipient : recipients) {
            if (recipient.equals(senderPlayer)) {
                continue;
            }
            if (recipient.hasPermission("herochat.admin.stealth")) {
                continue;
            }
            return true;
        }

        return false;
    }

    public void trimRecipients(final Set<Player> recipients, final Chatter sender) {
        final World world = sender.getPlayer().getWorld();

        final Set<Chatter> members = this.getMembers();
        final Iterator<Player> iterator = recipients.iterator();
        while (iterator.hasNext()) {
            final Chatter recipient = Herochat.getChatterManager().getChatter(iterator.next());
            if (recipient == null) {
                continue;
            }
            final World recipientWorld = recipient.getPlayer().getWorld();

            if (!members.contains(recipient)) {
                iterator.remove();
            } else if (this.isLocal() && !sender.isInRange(recipient, this.getDistance())) {
                iterator.remove();
            } else if (!this.hasWorld(recipientWorld)) {
                iterator.remove();
            } else if (recipient.isIgnoring(sender)) {
                iterator.remove();
            } else if (!this.isCrossWorld() && !world.equals(recipientWorld)) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (!(other instanceof Channel)) return false;
        final Channel channel = (Channel) other;
        return this.getName().equalsIgnoreCase(channel.getName()) || this.getName().equalsIgnoreCase(channel.getNick());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.getName() == null ? 0 : this.getName().toLowerCase().hashCode());
        result = prime * result + (this.getNick() == null ? 0 : this.getNick().toLowerCase().hashCode());
        return result;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public void setPassword(final String password) {}

    @Override
    public boolean isVerbose() {
        return false;
    }

    @Override
    public void setVerbose(final boolean verbose) {}

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return this.getDistance() != 0;
    }

    @Override
    public void attachStorage(final ChannelStorage storage) {}

    @Override
    public boolean banMember(final Chatter chatter, final boolean announce) {
        return false;
    }

    @Override
    public Set<String> getBans() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getModerators() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getMutes() {
        return Collections.emptySet();
    }

    @Override
    public ChannelStorage getStorage() {
        return this.storage;
    }

    @Override
    public boolean hasWorld(final String world) {
        return this.getWorlds().isEmpty() || this.getWorlds().contains(world);
    }

    @Override
    public boolean hasWorld(final World world) {
        return this.hasWorld(world.getName());
    }

    @Override
    public boolean isBanned(final String name) {
        return this.getBans().contains(name.toLowerCase());
    }

    @Override
    public boolean isMember(final Chatter chatter) {
        return this.getMembers().contains(chatter);
    }

    @Override
    public boolean isModerator(final String name) {
        return this.getModerators().contains(name.toLowerCase());
    }

    @Override
    public void onFocusGain(final Chatter chatter) {}

    @Override
    public void onFocusLoss(final Chatter chatter) {}

    @Override
    public void removeWorld(final String world) {
        this.getWorlds().remove(world);
    }

    @Override
    public void setBanned(final String name, final boolean banned) {}

    @Override
    public void setBans(final Set<String> bans) {}

    @Override
    public void setModerator(final String name, final boolean moderator) {}

    @Override
    public void setModerators(final Set<String> moderators) {}

    @Override
    public void setMuted(final String name, final boolean muted) {}

    @Override
    public void setMutes(final Set<String> mutes) {}
}
