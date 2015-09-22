package fr.thisismac.level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;


public class LevelListeners implements Listener {

	Main plugin;

	public LevelListeners(Main plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		plugin.saveData(plugin.getPlayer(e.getPlayer().getName()));
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if(event.getPlayer() != null){//Check if the Player is true
			Player p = (Player)event.getPlayer();

			boolean isIn = false;
			int level = 1;

			for(PlayerManager player : plugin.players) {
				if(player.getPlayerName().equals(p.getName())) {
					isIn = true;
					level = player.getLevel();
				}
			}

			if(isIn == false){//If the ArrayList players contains the player's name
				plugin.manager.addPlayer(p.getName());//Adding the player on the players ArrayList
				plugin.insertDatas(p.getName());
				p.sendMessage(plugin.prefix + ChatColor.GREEN + ">> Bienvenue sur Alkazia ! Vous commencez l'aventure au niveau 1, pour plus d'infos, allez sur alkazia.net onglet Wiki des plugins<<");
			} 
			
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {

		if(event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player){

			Player damager = (Player) event.getEntity().getKiller();
			Player death = (Player) event.getEntity();
			if(isHoldingArmor(death.getInventory().getArmorContents())) {
				if(plugin.getPlayer(damager.getName()).getKill() < 10) {
					plugin.manager.addXP(plugin.getPlayer(damager.getName()), 5); 
					plugin.getPlayer(damager.getName()).addKill();
				}
			}
				
			
		}
	}
	
	private boolean isHoldingArmor(ItemStack[] armor) {
        return armor[0].getType() != Material.AIR && armor[1].getType() != Material.AIR  && armor[2].getType() != Material.AIR && armor[3].getType() != Material.AIR;
    }
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event){

		if(event.getBlock().getType() == Material.METEOR_ORE  && !event.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
			for(PlayerManager player : plugin.players){
				if(player.getPlayerName().equalsIgnoreCase(event.getPlayer().getName())) {
					plugin.manager.addXP(player, 30);
				}
			}
		}
		
		if(event.getBlock().getType() == Material.POWDER_BLOCK && !event.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH) ) {
			for(PlayerManager player : plugin.players) {
				if(player.getPlayerName().equalsIgnoreCase(event.getPlayer().getName())) {
					plugin.manager.addXP(player, 30);
				}
			}
		}
		
	}

}









/*				if(killer.getPlayerName().equalsIgnoreCase(damager.getName())){//Check killer's name
killer.kills++;
//plugin.manager.getPlayersToKill(killer.getLevel() + 1)
if(killer.level == 1 || (plugin.manager.getPlayersToKill(killer.getLevel()) - killer.getKills()) == 0){//Check if the player has killed as much players as he has to
	//Account account = new Accounts().get(damager.getName());//Call an account for iConomy
	Account account = null;
	PermissionManager pex = PermissionsEx.getPermissionManager();
	PermissionUser userPermission = pex.getUser(killer.getPlayerName());//Call an account for PEX
	if(plugin.manager.hasPowerLevel(killer.getLevel())){//Check the status' level

		if(plugin.manager.isObject1Null((killer.getLevel() + 1)).equals("no") && plugin.manager.isObject2Null((killer.getLevel() + 1)).equals("no")){//The object 1 and 2 are null (id = 0)
			damager.sendMessage(ChatColor.GREEN + "----- Niveau -----");
			plugin.manager.addLevel(killer.getPlayerName());//Adding a level at the killer
			killer.kills = 0;
			try{
				account.getHoldings().add(plugin.manager.getMoney(killer.getLevel() + 1));
			}catch(NullPointerException e){damager.sendMessage("Contactez votre administrateur !" + ChatColor.RED + " Erreur : Description des gains introuvables.");}
			if(!plugin.manager.isPermissionNull(killer.getLevel()).equals("no")){
				userPermission.addPermission(plugin.manager.getPermission((killer.getLevel())));
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO");
				damager.sendMessage(ChatColor.GREEN + "Et pour finir, la permission : " + plugin.manager.getPermissionName2(killer.getLevel()) + " !");
			}else{
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO");
			}
			//plugin.setScore(damager, damager.getLevel());
		}
		else if(!plugin.manager.isObject1Null((killer.getLevel() + 1)).equals("no") && plugin.manager.isObject2Null((killer.getLevel() + 1)).equals("no")){//The object 2 is null (id = 0)
			damager.sendMessage(ChatColor.GREEN + "----- Niveau -----");
			plugin.manager.addLevel(killer.getPlayerName());//Adding a level at the killer
			killer.kills = 0;
			try{
				account.getHoldings().add(plugin.manager.getMoney(killer.getLevel()) + 1);
			}catch(NullPointerException e){damager.sendMessage("Contactez votre administrateur !");}
			damager.getInventory().addItem(new ItemStack(Material.getMaterial(plugin.manager.getSpecialObjectId1(killer.getLevel()))));
			damager.updateInventory();
			if(!plugin.manager.isPermissionNull(killer.getLevel()).equals("no")){
				userPermission.addPermission(plugin.manager.getPermission((killer.getLevel())));
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO, et " + plugin.manager.getSpecialObjectName1(killer.getLevel()));
				damager.sendMessage(ChatColor.GREEN + "Et pour finir, la permission : " + plugin.manager.getPermissionName2(killer.getLevel()) + " !");
			}else{
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO, et " + plugin.manager.getSpecialObjectName1(killer.getLevel()));
			}
			//plugin.setScore(damager, damager.getLevel());
		}
		else if(!plugin.manager.isObject1Null((killer.getLevel() + 1)).equals("no") && !plugin.manager.isObject2Null((killer.getLevel() + 1)).equals("no")){//The object 1 and 2 are OK
			damager.sendMessage(ChatColor.GREEN + "----- Niveau -----");
			plugin.manager.addLevel(killer.getPlayerName());//Adding a level at the killer
			killer.kills = 0;
			try{
				account.getHoldings().add(plugin.manager.getMoney(killer.getLevel() + 1));
			}catch(NullPointerException e){damager.sendMessage("Contactez votre administrateur !");}
			damager.getInventory().addItem(new ItemStack(Material.getMaterial(plugin.manager.getSpecialObjectId1(killer.getLevel()))));
			damager.getInventory().addItem(new ItemStack(Material.getMaterial(plugin.manager.getSpecialObjectId2(killer.getLevel()))));
			damager.updateInventory();
			if(!plugin.manager.isPermissionNull(killer.getLevel()).equals("no")){
				userPermission.addPermission(plugin.manager.getPermission((killer.getLevel())));
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO, " + plugin.manager.getSpecialObjectName1(killer.getLevel()) + " ainsi que " + plugin.manager.getSpecialObjectName2(killer.getLevel()) + " !");
				damager.sendMessage(ChatColor.GREEN + "Et pour finir, la permission : " + plugin.manager.getPermissionName2(killer.getLevel()) + " !");
			}else{
				damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO, " + plugin.manager.getSpecialObjectName1(killer.getLevel()) + " ainsi que " + plugin.manager.getSpecialObjectName2(killer.getLevel()) + " !");
			}
			//plugin.setScore(damager, damager.getLevel());
		}
	}else{//The player will not have a special level
		damager.sendMessage(ChatColor.GREEN + "----- Niveau -----");
		plugin.manager.addLevel(killer.getPlayerName());//Adding a level at the killer
		killer.kills = 0;
		try{
			account.getHoldings().add(plugin.manager.getMoney(killer.getLevel() + 1));
		}catch(NullPointerException e){damager.sendMessage("Contactez votre administrateur !" + ChatColor.RED + " Erreur : Description des gains introuvables.");}
		damager.sendMessage(ChatColor.GREEN + "Félicitation ! Vous passez au niveau " + killer.getLevel() + " ! Vous gagnez "+ plugin.manager.getMoney(killer.getLevel()) + " PO");
		//plugin.setScore(damager, damager.getLevel());
	}
}
}*/
