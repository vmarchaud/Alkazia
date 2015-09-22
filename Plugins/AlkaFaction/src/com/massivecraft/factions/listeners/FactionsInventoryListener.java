package com.massivecraft.factions.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import com.massivecraft.factions.P;
import com.massivecraft.factions.holder.FactionHolder;

public class FactionsInventoryListener implements Listener {
    public P p;

    public FactionsInventoryListener(final P p) {
        this.p = p;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof FactionHolder && (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getSlot() != event.getRawSlot() || event.getAction().name().contains("PLACE") && event.getSlot() == event.getRawSlot())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof FactionHolder) {
            event.setCancelled(true);
        }
    }
}
