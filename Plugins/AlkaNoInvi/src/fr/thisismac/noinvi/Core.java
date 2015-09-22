package fr.thisismac.noinvi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class Core extends JavaPlugin  implements Listener
{
   Logger logger = Bukkit.getLogger();
   
  public void onDisable() {
	  logger.log(Level.INFO, "[AlkaNoInvi] Stopping ..");
  }
  public void onEnable() {
	 logger.log(Level.INFO, "[AlkaNoInvi] Starting ..");
	 getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onAttack(EntityDamageByEntityEvent e)
  {
	  Player p;
	  if(e.getDamager() instanceof Player) {
		  p = (Player)e.getDamager();
		  if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			  p.removePotionEffect(PotionEffectType.INVISIBILITY);
			  p.sendMessage(ChatColor.GOLD + "Vous n'êtes plus invisible car vous avez attaqué quelqu'un.");
		  }
	  }
  }
  
  @EventHandler
  public void onPotionSplash(PotionSplashEvent e)
  {
	  if(!e.getAffectedEntities().isEmpty()) {
		  if(e.getPotion().getShooter() instanceof Player)  {
			  Player p = (Player)e.getPotion().getShooter(); 
		  if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			  p.removePotionEffect(PotionEffectType.INVISIBILITY);
			  p.sendMessage(ChatColor.GOLD + "Vous n'êtes plus invisible car vous avez attaqué quelqu'un.");
		  }
		  }
	  }
  }

}