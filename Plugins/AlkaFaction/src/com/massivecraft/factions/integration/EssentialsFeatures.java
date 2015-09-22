package com.massivecraft.factions.integration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.EssentialsLocalChatEvent;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

/*
 * This Essentials integration handler is for newer 3.x.x versions of Essentials which don't have "IEssentialsChatListener"
 * If an older version is detected in the setup() method below, handling is passed off to EssentialsOldVersionFeatures
 */

// silence deprecation warnings with this old interface
@SuppressWarnings("deprecation")
public class EssentialsFeatures {
    private static EssentialsChat essChat;
    private static IEssentials essentials;

    public static void setup() {
        // integrate main essentials plugin
        // TODO: this is the old Essentials method not supported in 3.0... probably needs to eventually be moved to EssentialsOldVersionFeatures and new method implemented
        if (EssentialsFeatures.essentials == null) {
            final Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
            if (ess != null && ess.isEnabled()) {
                EssentialsFeatures.essentials = (IEssentials) ess;
            }
        }

        // integrate chat
        if (EssentialsFeatures.essChat != null) return;

        final Plugin test = Bukkit.getServer().getPluginManager().getPlugin("EssentialsChat");
        if (test == null || !test.isEnabled()) return;

        EssentialsFeatures.essChat = (EssentialsChat) test;

        // try newer Essentials 3.x integration method
        try {
            Class.forName("com.earth2me.essentials.chat.EssentialsLocalChatEvent");
            EssentialsFeatures.integrateChat(EssentialsFeatures.essChat);
        } catch (final ClassNotFoundException ex) {
            // no? try older Essentials 2.x integration method
            try {
                EssentialsOldVersionFeatures.integrateChat(EssentialsFeatures.essChat);
            } catch (final NoClassDefFoundError ex2) { /* no known integration method, then */}
        }
    }

    public static void unhookChat() {
        if (EssentialsFeatures.essChat == null) return;

        try {
            EssentialsOldVersionFeatures.unhookChat();
        } catch (final NoClassDefFoundError ex) {}
    }

    // return false if feature is disabled or Essentials isn't available
    public static boolean handleTeleport(final Player player, final Location loc) {
        if (!Conf.homesTeleportCommandEssentialsIntegration || EssentialsFeatures.essentials == null) return false;

        final Teleport teleport = EssentialsFeatures.essentials.getUser(player).getTeleport();
        final Trade trade = new Trade(Conf.econCostHome, EssentialsFeatures.essentials);
        try {
            teleport.teleport(loc, trade);
        } catch (final Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }

    public static void integrateChat(final EssentialsChat instance) {
        EssentialsFeatures.essChat = instance;
        try {
            Bukkit.getServer().getPluginManager().registerEvents(new LocalChatListener(), P.p);
            P.p.log("Found and will integrate chat with newer " + EssentialsFeatures.essChat.getDescription().getFullName());
        } catch (final NoSuchMethodError ex) {
            EssentialsFeatures.essChat = null;
        }
    }

    private static class LocalChatListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL)
        public void onPlayerChat(final EssentialsLocalChatEvent event) {
            final Player speaker = event.getPlayer();
            String format = event.getFormat();

            format = FactionsChatListener.parseTags(format, speaker);

            event.setFormat(format);
            // NOTE: above doesn't do relation coloring. if/when we can get a local recipient list from EssentialsLocalChatEvent, we'll probably
            // want to pass it on to FactionsPlayerListener.onPlayerChat(PlayerChatEvent event) rather than duplicating code
        }
    }
}
