package com.massivecraft.factions.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;

public class FactionsAppearanceListener implements Listener {
    public P p;

    public FactionsAppearanceListener(final P p) {
        this.p = p;
    }

    // -------------------------------------------- //
    // FULL TWO-WAYS
    // -------------------------------------------- //

    public void fullTwoWay(final Player player) {
        SpoutFeatures.updateTitleShortly(player, null);
        SpoutFeatures.updateTitleShortly(null, player);
        SpoutFeatures.updateCapeShortly(player, null);
        SpoutFeatures.updateCapeShortly(null, player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.fullTwoWay(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
        this.fullTwoWay(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        this.fullTwoWay(event.getPlayer());
    }

    // -------------------------------------------- //
    // HEALTH BAR
    // -------------------------------------------- //

    public static void possiblyUpdateHealthBar(final Entity entity) {
        if (!Conf.spoutHealthBarUnderNames) return;
        if (!(entity instanceof Player)) return;
        final Player player = (Player) entity;
        SpoutFeatures.updateTitle(player, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorEntityDamageEvent(final EntityDamageEvent event) {
        FactionsAppearanceListener.possiblyUpdateHealthBar(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorEntityRegainHealthEvent(final EntityRegainHealthEvent event) {
        FactionsAppearanceListener.possiblyUpdateHealthBar(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void monitorPlayerRespawnEvent(final PlayerRespawnEvent event) {
        FactionsAppearanceListener.possiblyUpdateHealthBar(event.getPlayer());
    }

}
