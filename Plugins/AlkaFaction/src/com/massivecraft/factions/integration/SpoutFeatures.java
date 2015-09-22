package com.massivecraft.factions.integration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.HealthBarUtil;

public class SpoutFeatures {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private static SpoutMainListener mainListener;

    private static boolean enabled = false;

    public static boolean isEnabled() {
        return SpoutFeatures.enabled;
    }

    // -------------------------------------------- //
    // SETUP AND AVAILABILITY
    // -------------------------------------------- //

    public static boolean setup() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Spout");
        if (plugin == null || !plugin.isEnabled()) {
            if (SpoutFeatures.enabled == false) return false;
            SpoutFeatures.enabled = false;
            return false;
        }

        if (SpoutFeatures.enabled == true) return true;
        SpoutFeatures.enabled = true;

        P.p.log("Found and will use features of " + plugin.getDescription().getFullName());
        SpoutFeatures.mainListener = new SpoutMainListener();
        Bukkit.getPluginManager().registerEvents(SpoutFeatures.mainListener, P.p);

        return true;
    }

    // -------------------------------------------- //
    // CAPES
    // -------------------------------------------- //
    // Capes look the same to everyone.

    public static void updateCape(final Object ofrom, final Object oto, final boolean onlyIfDifferent) {
        // Enabled and non-null?
        if (!SpoutFeatures.isEnabled()) return;
        if (!Conf.spoutCapes) return;

        final Set<Player> fromPlayers = SpoutFeatures.getPlayersFromObject(ofrom);
        final Set<Player> toPlayers = SpoutFeatures.getPlayersFromObject(oto);

        for (final Player player : fromPlayers) {
            final FPlayer fplayer = FPlayers.i.get(player);
            final SpoutPlayer splayer = SpoutManager.getPlayer(player);
            final Faction faction = fplayer.getFaction();

            String cape = faction.getCape();
            if (cape == null) {
                cape = "http://s3.amazonaws.com/MinecraftCloaks/" + player.getName() + ".png";
            }

            for (final Player playerTo : toPlayers) {
                final SpoutPlayer splayerTo = SpoutManager.getPlayer(playerTo);

                final boolean skip = onlyIfDifferent && cape.equals(splayer.getCape(splayerTo));
                //Bukkit.getConsoleSender().sendMessage(P.p.txt.parse("<i>CAPE SKIP:<h>%s <i>FROM <h>%s <i>TO <h>%s <i>URL <h>%s", String.valueOf(skip), player.getDisplayName(), playerTo.getDisplayName(), cape));
                if (skip) {
                    continue;
                    //Bukkit.getConsoleSender().sendMessage(P.p.txt.parse("<i>CAPE FROM <h>%s <i>TO <h>%s <i>URL <h>%s", player.getDisplayName(), playerTo.getDisplayName(), cape));
                }

                // Set the cape
                try {
                    splayer.setCapeFor(splayerTo, cape);
                } catch (final Exception e) {

                }
            }
        }
    }

    public static void updateCape(final Object ofrom, final Object oto) {
        SpoutFeatures.updateCape(ofrom, oto, true);
    }

    public static void updateCapeShortly(final Object ofrom, final Object oto) {
        P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
            @Override
            public void run() {
                SpoutFeatures.updateCape(ofrom, oto, false);
            }
        }, 5);
    }

    // -------------------------------------------- //
    // TITLE
    // -------------------------------------------- //

    public static void updateTitle(final Object ofrom, final Object oto, final boolean onlyIfDifferent) {
        // Enabled and non-null?
        if (!SpoutFeatures.isEnabled()) return;
        if (!(Conf.spoutFactionTagsOverNames || Conf.spoutFactionTitlesOverNames || Conf.spoutHealthBarUnderNames)) return;

        final Set<Player> fromPlayers = SpoutFeatures.getPlayersFromObject(ofrom);
        final Set<Player> toPlayers = SpoutFeatures.getPlayersFromObject(oto);

        for (final Player player : fromPlayers) {
            final FPlayer fplayer = FPlayers.i.get(player);
            final SpoutPlayer splayer = SpoutManager.getPlayer(player);
            final Faction faction = fplayer.getFaction();

            for (final Player playerTo : toPlayers) {
                final FPlayer fplayerTo = FPlayers.i.get(playerTo);
                final SpoutPlayer splayerTo = SpoutManager.getPlayer(playerTo);
                final Faction factionTo = fplayerTo.getFaction();

                final ChatColor relationColor = faction.getRelationTo(factionTo).getColor();

                final String title = SpoutFeatures.generateTitle(player, fplayer, faction, relationColor);

                final boolean skip = onlyIfDifferent && title.equals(splayer.getTitleFor(splayerTo));
                //Bukkit.getConsoleSender().sendMessage(P.p.txt.parse("<i>TITLE SKIP:<h>%s <i>FROM <h>%s <i>TO <h>%s <i>TITLE <h>%s", String.valueOf(skip), player.getDisplayName(), playerTo.getDisplayName(), title));
                if (skip) {
                    continue;
                    //Bukkit.getConsoleSender().sendMessage(P.p.txt.parse("<i>TITLE FROM <h>%s <i>TO <h>%s <i>TITLE <h>%s", player.getDisplayName(), playerTo.getDisplayName(), title));
                }

                splayer.setTitleFor(splayerTo, title);
            }
        }
    }

    public static void updateTitle(final Object ofrom, final Object oto) {
        SpoutFeatures.updateTitle(ofrom, oto, true);
    }

    public static void updateTitleShortly(final Object ofrom, final Object oto) {
        P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
            @Override
            public void run() {
                SpoutFeatures.updateTitle(ofrom, oto, false);
            }
        }, 5);
    }

    public static String generateTitle(final Player player, final FPlayer fplayer, final Faction faction, final ChatColor relationColor) {
        String ret = null;

        ret = player.getDisplayName();

        if (faction.isNormal()) {
            String addTag = "";
            if (Conf.spoutFactionTagsOverNames) {
                addTag += relationColor.toString() + fplayer.getRole().getPrefix() + faction.getTag();
            }

            if (Conf.spoutFactionTitlesOverNames && !fplayer.getTitle().isEmpty()) {
                addTag += (addTag.isEmpty() ? "" : " ") + fplayer.getTitle();
            }

            ret = addTag + "\n" + ret;
        }

        if (Conf.spoutHealthBarUnderNames) {
            ret += "\n";
            ret += HealthBarUtil.getHealthbar(player.getHealthScale() / 20d);
        }

        return ret;
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public static Set<Player> getPlayersFromObject(final Object o) {
        final Set<Player> ret = new HashSet<Player>();
        if (o instanceof Player) {
            ret.add((Player) o);
        } else if (o instanceof FPlayer) {
            final FPlayer fplayer = (FPlayer) o;
            final Player player = fplayer.getPlayer();
            if (player != null) {
                ret.add(player);
            }
        } else if (o instanceof Faction) {
            ret.addAll(((Faction) o).getOnlinePlayers());
        } else {
            ret.addAll((Collection<? extends Player>) Arrays.asList(Bukkit.getOnlinePlayers()));
        }

        return ret;
    }

    // -------------------------------------------- //
    // TERRITORY DISPLAY
    // -------------------------------------------- //

    // update displayed current territory for all players inside a specified chunk; if specified chunk is null, then simply update everyone online
    public static void updateTerritoryDisplayLoc(final FLocation fLoc) {
        if (!SpoutFeatures.isEnabled()) return;

        final Set<FPlayer> players = FPlayers.i.getOnline();

        for (final FPlayer player : players)
            if (fLoc == null) {
                SpoutFeatures.mainListener.updateTerritoryDisplay(player, false);
            } else if (player.getLastStoodAt().equals(fLoc)) {
                SpoutFeatures.mainListener.updateTerritoryDisplay(player, true);
            }
    }

    // update displayed current territory for specified player; returns false if unsuccessful
    public static boolean updateTerritoryDisplay(final FPlayer player) {
        if (!SpoutFeatures.isEnabled()) return false;
        return SpoutFeatures.mainListener.updateTerritoryDisplay(player, true);
    }

    // update access info for all players inside a specified chunk; if specified chunk is null, then simply update everyone online
    public static void updateAccessInfoLoc(final FLocation fLoc) {
        if (!SpoutFeatures.isEnabled()) return;

        final Set<FPlayer> players = FPlayers.i.getOnline();

        for (final FPlayer player : players)
            if (fLoc == null || player.getLastStoodAt().equals(fLoc)) {
                SpoutFeatures.mainListener.updateAccessInfo(player);
            }
    }

    // update owner list for specified player
    public static boolean updateAccessInfo(final FPlayer player) {
        if (!SpoutFeatures.isEnabled()) return false;
        return SpoutFeatures.mainListener.updateAccessInfo(player);
    }

    public static void playerDisconnect(final FPlayer player) {
        if (!SpoutFeatures.isEnabled()) return;
        SpoutFeatures.mainListener.removeTerritoryLabels(player.getName());
    }
}
