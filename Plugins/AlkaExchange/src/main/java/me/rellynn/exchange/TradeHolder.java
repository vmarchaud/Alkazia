package me.rellynn.exchange;

import me.rellynn.exchange.handlers.Trade;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * TradeHolder.java
 *
 * @author Rellynn
 */
public class TradeHolder implements InventoryHolder {

    private Trade trade;

    /**
     * Permet d'enregistrer l'inventaire au près d'un holder
     */
    public TradeHolder(Trade trade) {
        this.trade = trade;
    }

    /**
     * @return L'échange associé au holder
     */
    public Trade getTrade() {
        return trade;
    }

    /**
     * @return NULL
     */
    @Override
    public Inventory getInventory() {
        return null;
    }
}
