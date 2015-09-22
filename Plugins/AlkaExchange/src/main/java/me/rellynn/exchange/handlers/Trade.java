package me.rellynn.exchange.handlers;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.rellynn.exchange.ExchangePlugin;
import me.rellynn.exchange.TradeHolder;
import me.rellynn.exchange.utils.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;

/**
 * Trade.java
 *
 * @author Rellynn
 */
public class Trade {

    /**
     * Contient tous les échanges
     */
    private static List<Trade> allTrades = new ArrayList<>();
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private static ItemStack ACCEPT_ITEM = new ItemFactory(Material.WOOL, DyeColor.LIME.getData()).setTitle(ChatColor.GREEN + "Confirmer mon offre").build();
    private static ItemStack CONFIRM_ITEM = new ItemFactory(Material.WOOL, DyeColor.LIME.getData()).setTitle(ChatColor.GREEN + "Accepter l'échange").build();
    public boolean aConfirm;
    public boolean bConfirm;
    private State state;
    private Player player;
    private Player target;
    private Inventory aInventory;
    private Inventory bInventory;

    /**
     * Permet de créer un échange
     *
     * @param player Le joueur à l'origine de l'échange
     * @param target Le joueur qui doit accepter l'échange
     */
    public Trade(Player player, Player target) {
        this.player = player;
        this.target = target;
        this.state = State.WAITING;
        allTrades.add(this);
    }

    /**
     * @return Tous les échanges
     */
    public static List<Trade> getAllTrades() {
        return allTrades;
    }

    /**
     * Recherche l'échange ouvert d'un joueur
     *
     * @param player Le joueur
     * @return L'échange ou NULL
     */
    public static Trade getPlayerTrade(Player player) {
        for (Trade trade : getAllTrades()) {
            if (trade.getPlayer() == player || trade.getTarget() == player) {
                return trade;
            }
        }
        return null;
    }

    /**
     * @return Le joueur à l'origine de l'échange
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Le joueur qui a accepté l'échange
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Permet de retourner le deuxième joueur de l'échange
     *
     * @param who Le premier joueur
     * @return Le deuxième joueur
     */
    public Player getTargetPlayer(Player who) {
        return who == player ? target : player;
    }

    /**
     * Permet de vérifier l'état de l'échange
     *
     * @param state L'état à vérifier
     * @return true Si l'état est vérifié et égal
     */
    public boolean isState(State state) {
        return this.state == state;
    }

    /**
     * Permet de créer les inventaires et d'initialiser l'échange
     */
    public void create() {
        state = State.PLAYER;
        aInventory = Bukkit.createInventory(new TradeHolder(this), 9, "Proposition de " + player.getName());
        bInventory = Bukkit.createInventory(new TradeHolder(this), 9, "Proposition de " + target.getName());
        aInventory.setItem(8, ACCEPT_ITEM);
        bInventory.setItem(8, ACCEPT_ITEM);
        player.openInventory(aInventory);
        target.openInventory(bInventory);
    }

    /**
     * Permet de rafraîchir l'échange
     */
    public void refresh() {
        aConfirm = false;
        bConfirm = false;
    }

    /**
     * Permet d'annuler l'échange
     *
     * @param removeFromList true Si l'échange doit être enlevé de la liste
     */
    public void cancel(boolean removeFromList) {
        if (state != State.WAITING && state != State.FINISH) {
            state = State.FINISH;
            for (int i = 0; i < 8; i++) {
                ItemStack item_a = aInventory.getItem(i);
                ItemStack item_b = bInventory.getItem(i);
                if (item_a != null && item_a.getType() != Material.AIR) {
                    player.getInventory().addItem(item_a);
                }
                if (item_b != null && item_b.getType() != Material.AIR) {
                    target.getInventory().addItem(item_b);
                }
            }
            target.closeInventory();
            player.closeInventory();
            player.updateInventory();
            target.updateInventory();
        }

        if (removeFromList)
            allTrades.remove(this);
    }

    /**
     * Permet d'accepter l'échange
     *
     * @param who Le joueur qui accepte l'échange
     * @return true Si l'acceptation a réussie
     */
    public boolean accept(Player who) {
        try {
            Field field = getClass().getField(who == player ? "aConfirm" : "bConfirm");
            field.setAccessible(true);
            if (field.getBoolean(this)) {
                return false;
            }
            field.set(this, true);
            if (aConfirm && bConfirm) {
                if (state == State.PLAYER) {
                    state = State.TRANSITION;
                    aConfirm = bConfirm = false;
                    aInventory.setItem(8, CONFIRM_ITEM);
                    bInventory.setItem(8, CONFIRM_ITEM);
                    player.openInventory(bInventory);
                    target.openInventory(aInventory);
                    state = State.TARGET;
                } else if (state == State.TARGET) {
                    state = State.FINISH;
                    List<ItemStack> aItems = new ArrayList<>();
                    List<ItemStack> bItems = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        ItemStack item_a = aInventory.getItem(i);
                        ItemStack item_b = bInventory.getItem(i);
                        if (item_a != null && item_a.getType() != Material.AIR) {
                            target.getInventory().addItem(item_a);
                            aItems.add(item_a);
                        }
                        if (item_b != null && item_b.getType() != Material.AIR) {
                            player.getInventory().addItem(item_b);
                            bItems.add(item_b);
                        }
                    }
                    aInventory = bInventory = null;
                    target.closeInventory();
                    player.closeInventory();
                    target.updateInventory();
                    player.updateInventory();
                    ExchangePlugin.instance().getLogger().info("{\n    \"time\": \"" + DATE_FORMAT.format(new Date()) + "\",\n    \"player\": {\n        \"name\": \"" + player.getName() + "\",\n        \"items\": " + JSONArray.toJSONString(aItems) + "\n    },\n    \"target\": {\n        \"name\": \"" + target.getName() + "\",\n        \"items\": " + JSONArray.toJSONString(bItems) + "\n    }\n}\n");
                    allTrades.remove(this);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public enum State {
        WAITING, PLAYER, TARGET, TRANSITION, FINISH
    }

}
