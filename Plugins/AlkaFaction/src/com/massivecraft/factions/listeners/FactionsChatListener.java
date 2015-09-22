package com.massivecraft.factions.listeners;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Rel;


public class FactionsChatListener implements Listener {
    public P p;

    public FactionsChatListener(final P p) {
        this.p = p;
    }

    public static Field fieldRegisteredListenerDotPriority;
    public static final Pattern parsePattern;
    static {
        try {
            FactionsChatListener.fieldRegisteredListenerDotPriority = RegisteredListener.class.getDeclaredField("priority");
            FactionsChatListener.fieldRegisteredListenerDotPriority.setAccessible(true);
        } catch (final Exception e) {
            P.p.log(Level.SEVERE, "A reflection trick is broken! This will lead to glitchy relation-colored-chat.");
        }

        parsePattern = Pattern.compile("[{\\[]factions?_([a-zA-Z_]+)[}\\]]");
    }

    /**
     * We offer an optional and very simple chat formating functionality.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void lowPlayerChatEvent(final AsyncPlayerChatEvent event) {
        if (Conf.chatSetFormat) {
            event.setFormat(Conf.chatSetFormatTo);
        }
    }

    // this is for handling insertion of the player's faction tag, set at highest priority to give other plugins a chance to modify chat first

    /**
     * At the Highest event priority we apply chat formating.
     * Relation colored faction tags may or may not be disabled (Conf.chatParseTagsColored)
     * If color is disabled it works flawlessly.
     * If however color is enabled we face a limitation in Bukkit.
     * Bukkit does not support the same message looking different for each recipient.
     * The method we use to get around this is a bit hacky:
     * 1. We cancel the chat event on EventPriority.HIGHEST
     * 2. We trigger EventPriority.MONITOR manually without relation color.
     * 3. We log in console the way it's usually done (as in nms.NetServerHandler line~793).
     * 4. We send out the messages to each player with relation color.
     * The side effect is that other plugins at EventPriority.HIGHEST may experience the event as cancelled. 
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        // Should we even parse?
        if (!Conf.chatParseTags) return;
        if (Conf.chatTagHandledByAnotherPlugin) return;

        final Player from = event.getPlayer();
        final FPlayer fpfrom = FPlayers.i.get(from);
        final String format = event.getFormat();
        final String message = event.getMessage();

        final String formatWithoutColor = FactionsChatListener.parseTags(format, from, fpfrom);

        if (!Conf.chatParseTagsColored) {
            // The case without color is really this simple (:
            event.setFormat(formatWithoutColor);
            return;
        }

        // So you want color eh? You monster :O

        // 1. We cancel the chat event on EventPriority.HIGHEST
        event.setCancelled(true);

        // 2. We trigger EventPriority.MONITOR manually without relation color.
        final AsyncPlayerChatEvent monitorOnlyEvent = new AsyncPlayerChatEvent(false, from, message, new HashSet<Player>());
        monitorOnlyEvent.setFormat(formatWithoutColor);
        FactionsChatListener.callEventAtMonitorOnly(monitorOnlyEvent);

        // 3. We log in console the way it's usually done (as in nms.NetServerHandler line~793).
        Bukkit.getConsoleSender().sendMessage(String.format(monitorOnlyEvent.getFormat(), monitorOnlyEvent.getPlayer().getDisplayName(), monitorOnlyEvent.getMessage()));

        // 4. We send out the messages to each player with relation color.
        for (final Player to : event.getRecipients()) {
            final FPlayer fpto = FPlayers.i.get(to);
            final String formatWithColor = FactionsChatListener.parseTags(format, from, fpfrom, to, fpto);
            to.sendMessage(String.format(formatWithColor, from.getDisplayName(), message));
        }
    }

    /**
     * This is some nasty woodo - I know :/
     * I should make a pull request to Bukkit and CraftBukkit to support this feature natively
     */
    public static void callEventAtMonitorOnly(final Event event) {
        synchronized (Bukkit.getPluginManager()) {
            final HandlerList handlers = event.getHandlers();
            final RegisteredListener[] listeners = handlers.getRegisteredListeners();

            for (final RegisteredListener registration : listeners) {
                try {
                    final EventPriority priority = (EventPriority) FactionsChatListener.fieldRegisteredListenerDotPriority.get(registration);
                    if (priority != EventPriority.MONITOR) {
                        continue;
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    continue;
                }

                // This rest is almost copy pasted from SimplePluginManager in Bukkit:

                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }

                try {
                    registration.callEvent(event);
                } catch (final AuthorNagException ex) {
                    final Plugin plugin = registration.getPlugin();

                    if (plugin.isNaggable()) {
                        plugin.setNaggable(false);

                        String author = "<NoAuthorGiven>";

                        if (plugin.getDescription().getAuthors().size() > 0) {
                            author = plugin.getDescription().getAuthors().get(0);
                        }
                        Bukkit.getServer().getLogger().log(Level.SEVERE, String.format("Nag author: '%s' of '%s' about the following: %s", author, plugin.getDescription().getName(), ex.getMessage()));
                    }
                } catch (final Throwable ex) {
                    Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex);
                }
            }
        }
    }

    public static String parseTags(final String str, final Player from) {
        final FPlayer fpfrom = FPlayers.i.get(from);
        return FactionsChatListener.parseTags(str, from, fpfrom, null, null);
    }

    public static String parseTags(final String str, final Player from, final FPlayer fpfrom) {
        return FactionsChatListener.parseTags(str, from, fpfrom, null, null);
    }

    public static String parseTags(final String str, final Player from, final Player to) {
        final FPlayer fpfrom = FPlayers.i.get(from);
        final FPlayer fpto = FPlayers.i.get(to);
        return FactionsChatListener.parseTags(str, from, fpfrom, to, fpto);
    }

    public static String parseTags(final String str, final Player from, final FPlayer fpfrom, final Player to, final FPlayer fpto) {
        final StringBuffer ret = new StringBuffer();

        final Matcher matcher = FactionsChatListener.parsePattern.matcher(str);
        while (matcher.find()) {
            final String[] parts = matcher.group(1).toLowerCase().split("_");
            final List<String> args = new ArrayList<String>(Arrays.asList(parts));
            final String tag = args.remove(0);
            matcher.appendReplacement(ret, FactionsChatListener.produceTag(tag, args, from, fpfrom, to, fpto));
        }
        matcher.appendTail(ret);

        return ret.toString();
    }
    
    public static String getTagFrom(final String from, final String to) {
    	final FPlayer fpfrom = FPlayers.i.get(from);
    	final FPlayer fpto = FPlayers.i.get(to);
    	return fpfrom.getRelationTo(fpto).getColor().toString();
    	
    }
    public static String produceTag(final String tag, final List<String> args, final Player from, final FPlayer fpfrom, final Player to, final FPlayer fpto) {
        String ret = "";
        if (tag.equals("relcolor")) {
            if (fpto == null) {
                ret = Rel.NEUTRAL.getColor().toString();
            } else {
                ret = fpfrom.getRelationTo(fpto).getColor().toString();
            }
        } else if (tag.startsWith("roleprefix")) {
            ret = fpfrom.getRole().getPrefix();
        } else if (tag.equals("title")) {
            ret = fpfrom.getTitle();
        } else if (tag.equals("tag")) {
            if (fpfrom.hasFaction()) {
                ret = fpfrom.getFaction().getTag();
            }
        } else if (tag.startsWith("tagforce")) {
            ret = fpfrom.getFaction().getTag();
        } else if (tag.equals("levelf")) {
            ret = fpfrom.hasFaction() ? " {" + String.valueOf(fpfrom.getFaction().getLevel().getLevel()) + "} " : null;
            // End AlkaziaFactions
        } 

        if (ret == null) {
            ret = "";
        }

        return FactionsChatListener.applyFormatsByName(ret, args);
    }

    public static String applyFormatsByName(String str, final List<String> formatNames) {
        if (str.length() == 0) return str;
        for (final String formatName : formatNames) {
            final String format = Conf.chatSingleFormats.get(formatName);
            try {
                str = String.format(format, str);
            } catch (final Exception e) {}
        }
        return str;
    }

}
