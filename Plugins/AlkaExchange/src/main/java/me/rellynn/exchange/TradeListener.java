package me.rellynn.exchange;

import me.rellynn.exchange.handlers.Trade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

/**
 * TradeListener.java
 *
 * @author Rellynn
 */
public class TradeListener implements Listener {

    /**
     * Quand un joueur qui est en échange quitte le jeu
     *
     * @param event L'évenement
     */
    @EventHandler
    public void onTraderQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Trade trade = Trade.getPlayerTrade(player);
        if (trade == null || trade.isState(Trade.State.WAITING) || trade.isState(Trade.State.FINISH)) {
            return;
        }
        Player target = trade.getTargetPlayer(player);
        player.sendMessage(ChatColor.RED + "Vous avez annulé l'échange.");
        target.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " a annulé l'échange !");
        trade.cancel(true);
    }

    /**
     * Quand un joueur qui est en échange se prend des dégats
     *
     * @param event L'évenement
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTraderPickupDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Trade trade = Trade.getPlayerTrade(player);
        if (trade == null || trade.isState(Trade.State.WAITING) || trade.isState(Trade.State.FINISH)) {
            return;
        }
        Player target = trade.getTargetPlayer(player);
        player.sendMessage(ChatColor.RED + "Vous avez annulé l'échange.");
        target.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " a annulé l'échange !");
        trade.cancel(true);
    }

    /**
     * Quand un joueur qui est en échange ferme l'inventaire
     *
     * @param event L'évenement
     */
    @EventHandler
    public void onTradeFinish(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof TradeHolder)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        Trade trade = ((TradeHolder) inventory.getHolder()).getTrade();
        if (trade.isState(Trade.State.WAITING) || trade.isState(Trade.State.FINISH) || trade.isState(Trade.State.TRANSITION)) {
            return;
        }
        Player target = trade.getTargetPlayer(player);
        player.sendMessage(ChatColor.RED + "Vous avez annulé l'échange.");
        target.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " a annulé l'échange !");
        trade.cancel(true);
    }

    /**
     * Quand un joueur qui est en échange déplace des items
     *
     * @param event L'évenement
     */
    @EventHandler
    public void onTraderDragItem(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof TradeHolder)) {
            return;
        }
        Trade trade = ((TradeHolder) inventory.getHolder()).getTrade();
        if (trade.isState(Trade.State.WAITING)) {
            return;
        } else if (trade.isState(Trade.State.TARGET) || trade.isState(Trade.State.TRANSITION) || trade.isState(Trade.State.FINISH) || event.getInventorySlots().contains(8)) {
            event.setResult(Event.Result.DENY);
            return;
        }
        trade.refresh();
    }

    /**
     * Quand un joueur qui est en échange déplace des items
     *
     * @param event L'évenement
     */
    @EventHandler
    public void onTraderClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        } else if (event.getSlot() < 0) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof TradeHolder)) {
            return;
        }
        Trade trade = ((TradeHolder) inventory.getHolder()).getTrade();
        if (trade.isState(Trade.State.WAITING)) {
            return;
        } else if (trade.isState(Trade.State.TRANSITION) || trade.isState(Trade.State.FINISH)) {
            event.setResult(Event.Result.DENY);
            return;
        } else if (trade.isState(Trade.State.PLAYER)) {
            if (event.getSlot() == event.getRawSlot() && event.getSlot() == 8) {
                Player target = trade.getTargetPlayer(player);
                if (!trade.accept(player)) {
                    player.sendMessage(ChatColor.RED + "Vous avez déjà accepté l'échange.");
                } else {
                    target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " a accepté l'échange.");
                    player.sendMessage(ChatColor.GREEN + "Vous avez accepté l'échange.");
                }
                event.setResult(Event.Result.DENY);
                return;
            }
        } else if (trade.isState(Trade.State.TARGET)) {
            if (event.getSlot() == event.getRawSlot() && event.getSlot() == 8) {
                Player target = trade.getTargetPlayer(player);
                if (!trade.accept(player)) {
                    player.sendMessage(ChatColor.RED + "Vous avez déjà confirmé l'échange.");
                } else {
                    target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " a confirmé l'échange.");
                    player.sendMessage(ChatColor.GREEN + "Vous avez confirmé l'échange.");
                }
            }
            event.setResult(Event.Result.DENY);
            return;
        }
        trade.refresh();
    }

}
