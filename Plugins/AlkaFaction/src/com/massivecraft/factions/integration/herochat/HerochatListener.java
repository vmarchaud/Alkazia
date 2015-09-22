package com.massivecraft.factions.integration.herochat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Herochat;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

public class HerochatListener implements Listener {
    P p;

    public HerochatListener(final P p) {
        this.p = p;
        Herochat.getChannelManager().addChannel(new FactionChannel());
        Herochat.getChannelManager().addChannel(new AlliesChannel());
    }

    /**
     * Due to limitations in the new version of Herochat we can not offer relation colored tags.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChannelChatEvent(final ChannelChatEvent event) {
        // Should we even parse?
        if (!Conf.chatParseTags) return;
        if (Conf.chatTagHandledByAnotherPlugin) return;

        final Player from = event.getSender().getPlayer();
        final FPlayer fpfrom = FPlayers.i.get(from);
        String format = event.getFormat();

        format = format.replaceAll("&r", "§r");

        final String formatWithoutColor = FactionsChatListener.parseTags(format, from, fpfrom);

        event.setFormat(formatWithoutColor);
    }
}
