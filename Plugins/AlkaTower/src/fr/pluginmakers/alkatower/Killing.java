package fr.pluginmakers.alkatower;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class Killing implements Listener {
    private final Main plugin;

    public Killing(final Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage().replace("/", "");
        final String[] args = message.split(" ");
        final Date date = new Date();
        if (args.length == 2 && args[0].contains("warp") && args[1].equalsIgnoreCase("samedi")) if (event.getPlayer().isOp() || date.getDay() == 6 && date.getHours() >= 20 && date.getHours() <= 22 && (date.getHours() == 20 && date.getMinutes() >= 55 || date.getHours() == 22 && date.getMinutes() < 10)) {
            // do nothing
        } else {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Ce warp n'est accessible que le samedi soir de 20h55 à 22h10.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        if (Tower.enable) {
            final Player player = e.getEntity();
            final Player killer = e.getEntity().getKiller();
            if (!this.plugin.players.containsKey(player.getName())) {
                this.plugin.players.put(player.getName(), 0);
                this.plugin.times.put(player.getName(), 0);
            }
            if (killer != null) if (this.plugin.c.contains(killer.getLocation())) this.plugin.addPoints(killer.getName(), 5);
        }
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent e) {
        if (Tower.enable) if (this.plugin.players.containsKey(e.getPlayer().getName())) {
            this.plugin.players.put(e.getPlayer().getName(), 0);
            this.plugin.times.put(e.getPlayer().getName(), 0);
        }
    }

    public void scheduleTasks(final Plugin plug) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plug, new Runnable() {
            @Override
            public void run() {
                if (Tower.enable) {
                    final Cuboid c = Tower.saturday ? Killing.this.plugin.sc : Killing.this.plugin.c;
                    for (final Player p : Killing.this.plugin.getServer().getOnlinePlayers())
                        if (p.getHealth() > 0) if (c.contains(p.getLocation())) {
                            Killing.this.plugin.addPoints(p.getName(), 1);
                            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                                p.sendMessage(ChatColor.GOLD + "Vous ne pouvez pas participer à la capture de tour en étant invisible.");
                            }
                        }
                }
            }
        }, 20, 20);
    }
}
