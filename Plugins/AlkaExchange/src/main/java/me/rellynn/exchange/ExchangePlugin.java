package me.rellynn.exchange;

import me.rellynn.exchange.handlers.Trade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * ExchangePlugin.java
 *
 * @author Rellynn
 */
public class ExchangePlugin extends JavaPlugin {

    private static ExchangePlugin instance;

    /**
     * @return L'instance de cette classe
     */
    public static ExchangePlugin instance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Logger logger = getLogger();
        logger.setUseParentHandlers(false);
        try {
            getDataFolder().mkdir();
            FileHandler handler = new FileHandler(getDataFolder() + File.separator + "trades.log");
            handler.setFormatter(new SimpleJSONLogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
            setEnabled(false);
            return;
        }
        getServer().getPluginManager().registerEvents(new TradeListener(), this);
    }

    @Override
    public void onDisable() {
        for (Trade trade : Trade.getAllTrades()) {
            if (trade != null) {
                trade.cancel(false);
            }
        }
        Trade.getAllTrades().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("echange")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "Commandes disponibles :");
                sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "/echange joueur" + ChatColor.YELLOW + " - " + "échanger des items avec un joueur");
                sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "/echange oui" + ChatColor.YELLOW + " - " + "accepter une demande d'échange");
                sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "/echange non" + ChatColor.YELLOW + " - " + "refuser une demande d'échange");
            } else if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
                    return true;
                }
                final Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("oui")) {
                    Trade trade = Trade.getPlayerTrade(player);
                    if (trade == null) {
                        player.sendMessage(ChatColor.RED + "Vous n'avez actuellement aucune demande d'échange.");
                        return true;
                    }
                    player.sendMessage(ChatColor.GRAY + "Pour annuler l'échange, appuyez sur Echap.");
                    trade.getPlayer().sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " a accepté la demande d'échange.");
                    trade.getPlayer().sendMessage(ChatColor.GRAY + "Pour annuler l'échange, appuyez sur Echap.");
                    trade.create();
                } else if (args[0].equalsIgnoreCase("non")) {
                    Trade trade = Trade.getPlayerTrade(player);
                    if (trade == null) {
                        player.sendMessage(ChatColor.RED + "Vous n'avez actuellement aucune demande d'échange.");
                        return true;
                    }
                    trade.cancel(true);
                    player.sendMessage(ChatColor.RED + "Vous avez refusé la demande d'échange de " + ChatColor.DARK_RED + trade.getPlayer().getName() + ChatColor.RED + ".");
                    trade.getPlayer().sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " a refusé votre demande d'échange.");
                } else {
                    if (args[0].equalsIgnoreCase(player.getName())) {
                        player.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous demander vous-même en échange.");
                        return true;
                    }
                    final Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Le joueur " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " doit être connecté.");
                        return true;
                    }
                    final Trade trade = Trade.getPlayerTrade(target);
                    if (trade != null) {
                        player.sendMessage(ChatColor.RED + "Le joueur " + ChatColor.DARK_RED + args[0] + ChatColor.RED + " est a déjà une demande d'échange en cours.");
                        return true;
                    }
                    target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " vous envoie une demande d'échange.");
                    target.sendMessage(ChatColor.GRAY + "Vous avez 1mn pour accepter ou refuser la demande.");
                    target.sendMessage(ChatColor.GRAY + "Tapez /echange oui pour l'accepter");
                    target.sendMessage(ChatColor.GRAY + "ou /echange non pour la refuser");
                    player.sendMessage(ChatColor.GREEN + "Votre demande d'échange a été envoyée à " + ChatColor.AQUA + target.getName());
                    final Trade newTrade = new Trade(player, target);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (newTrade.isState(Trade.State.WAITING)) {
                                newTrade.cancel(true);
                                target.sendMessage(ChatColor.RED + "La demande d'échange a expirée.");
                                player.sendMessage(ChatColor.RED + "Votre demande d'échange à " + ChatColor.DARK_RED + target.getName() + ChatColor.RED + " a expiré.");
                            }
                        }
                    }.runTaskLater(this, 1200L);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Permet de gérer le formatteur de logs
     */
    private class SimpleJSONLogFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String msg = formatMessage(record).replace("[AlkaExchange] ", "");
            if (!msg.startsWith("{")) {
                return "";
            }
            return msg;
        }

    }

}
