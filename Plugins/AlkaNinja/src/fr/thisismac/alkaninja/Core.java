package fr.thisismac.alkaninja;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Core extends JavaPlugin implements Listener {
	
	/**
	 * @author Mac' 
	 * website : http://thisismac.fr
	 * @since 12/06/14
	 * Ninja plugin developped for Alkazia server (http://alkazia.fr)
	 * It add effect and auto-sneaking to a player that hold an special armor.
	 */
	
	private ArrayList<String> sneakers = new ArrayList();
	
	@Override
	public void onEnable() {
		System.out.println("[AlkaNinja] Ready for the show !");
		getServer().getPluginManager().registerEvents(this, this);

		BukkitTask task = getServer().getScheduler().runTaskTimer(this,
				new Runnable() {
					public void run() {
						int x = 0;
						if(sneakers.isEmpty()) return;
						
						for(String s : sneakers) {
							Bukkit.getPlayer(s).setSneaking(true);
						}
						
					}
				}, 10L, 10L);
	}

	@Override
	public void onDisable() {
		System.out.println("[AlkaNinja] Goodbye");
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(sneakers.contains(p.getName())) {
				removeFromNinja(p);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onSneakEvent(PlayerToggleSneakEvent event) {
		if (!sneakers.contains(event.getPlayer().getName())) {
			checkPlayer(event.getPlayer());
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (sneakers.contains(event.getPlayer().getName())) {
			removeFromNinja(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (sneakers.contains(event.getPlayer().getName())) {
			removeFromNinja(event.getPlayer());
		}
		
		removeEffect(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCloseInv(InventoryClickEvent event) {
		if (sneakers.contains(event.getWhoClicked().getName())) {
			checkPlayer((Player)event.getWhoClicked());
		}
	}

	/**
	 * Check if a player has a ninja armor or not
	 * It will remove effect and auto-sneak if he didnt
	 * @param Player p
	 */
	public void checkPlayer(Player p) {
		PlayerInventory inv = p.getInventory();

		ItemStack boots = inv.getBoots();
		ItemStack helmet = inv.getHelmet();
		ItemStack plate = inv.getChestplate();
		ItemStack legs = inv.getLeggings();

		if (helmet != null && plate != null && legs != null && boots != null) {
			if (helmet.getType() == Material.NINJA_HELMET && plate.getType() == Material.NINJA_CHESTPLATE && legs.getType() == Material.NINJA_LEGGINGS && boots.getType() == Material.NINJA_BOOTS) {
				if (!sneakers.contains(p.getName())) {
					sneakers.add(p.getName());
					addEffect(p);
					p.sendMessage(ChatColor.GOLD + "Vous êtes désormais, un NINJA !");
				}
			}
			else {
				removeFromNinja(p);
			}
		} 
		else if (helmet == null || plate != null || legs != null || boots != null) {
			removeFromNinja(p);
		}
	}
	
	/**
	 * Remove a player from sneakers group, remove his effect and send him a message to prevent him.
	 * @param Player p
	 */
	public void removeFromNinja(Player p) {
			if(sneakers.contains(p.getName())) {
				sneakers.remove(p.getName());
				removeEffect(p);
				p.setSneaking(false);
				p.sendMessage(ChatColor.GOLD + "Vous n'êtes plus un NINJA !");
			}
	}

	/**
	 * Remove speed and jump effect from the player
	 * @param Player p
	 */
	public void removeEffect(Player p) {
		p.removePotionEffect(PotionEffectType.JUMP);
		p.removePotionEffect(PotionEffectType.SPEED);
		p.removePotionEffect(PotionEffectType.FALL);
	}

	/**
	 * Add speed and jump effect to the player
	 * @param Player p
	 */
	public void addEffect(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,Integer.MAX_VALUE, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FALL,Integer.MAX_VALUE, 0));
	}
}
