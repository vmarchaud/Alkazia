package fr.thisismac.level;

import java.util.Arrays;
import java.util.List;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class Manager {
	
	Main plugin;
	
	public Manager(Main plugin){
		this.plugin = plugin;
	}
	
	public int getPlayersToKill(int level){//Return the number of players to kill
		if(level < 50) return level*2;
		if(level < 100) return level*3;
		if(level < 150) return level*4;
		return level*5;
	}
	
	public boolean hasPowerLevel(int level){//Return if the player has got a special level like 10, 20, 30...
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
				if(numbers1.contains((level))){
					return true;
				}
		return false;
	}
	
	
	public String isObject1Null(int forLevel){//Check if the object1 is null
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".objet_special1.nom");
	}
	
	public String isPermissionNull(int forLevel){//Check if the object1 is null
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".permission.nom");
	}
	
	public String getPermission(int forLevel){//Check if the object1 is null
		return plugin.levelsConfig.getString("Niveau_" + forLevel + ".permission.adresse");
	}
	
	public String getPermissionName2(int forLevel){//Check if the object1 is null
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".permission.nom");
	}
	
	public String getPermissionName(int forLevel){//Check if the object1 is null
		return plugin.levelsConfig.getString("Niveau_" + forLevel + ".permission.adresse");
	}
	
	public String isObject2Null(int forLevel){//Check if the object2 is null
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".objet_special2.nom");
	}

	
	public int getSpecialObjectId1(int forLevel){//Return the special object's id
		return plugin.levelsConfig.getInt("Niveau_" + forLevel + ".objet_special1.id");
	}
	
	public String getSpecialObjectName1(int forLevel){//Return the special object's name
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".objet_special1.nom");
	}

	public int getSpecialObjectId2(int forLevel){//Return the special object's id
		return plugin.levelsConfig.getInt("Niveau_" + forLevel + ".objet_special2.id");
	}
	
	public String getSpecialObjectName2(int forLevel){//Return the special object's name
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		int nextSuperLevel = 0;
		for(int i = forLevel ; i < 200 ; i++){
			if(numbers1.contains(i)){
				nextSuperLevel = i;
				break;
			}
		}
		return plugin.levelsConfig.getString("Niveau_" + nextSuperLevel + ".objet_special2.nom");
	}
	
	public boolean hasThePlayer(String playerName){//Check if the player is on the ArrayList players
		if(plugin.players.contains(playerName)) return true;
		return false;
	}
	
	public void addPlayer(String playerName){//Adding the player at the players ArrayList
		plugin.players.add(new PlayerManager(playerName, 1, 1));
	}
	
	public int getMoney(int forLevel){//Return the money to have
		return plugin.levelsConfig.getInt("Niveau_" + forLevel + ".argent");
	}
	
	
	
	 public int getNextSuperLevel(int forLevel){
		  Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		  List<Integer> numbers1 = Arrays.asList(numbers);
		  int nextSuperLevel = 0;
		  for(int i = forLevel ; i < 200 ; i++){
		   if(numbers1.contains(i)){
		    nextSuperLevel = i;
		    break;
		   }
		  }
		  return nextSuperLevel;
		 }
	
	 public void addXP(PlayerManager p, int x) {
		 p.addXP(x);
		 checkForUp(p);
	 }

		private void checkForUp(final PlayerManager p) {
			
			if(p.getxP() >= getXPNeededFor(p.getLevel()) && p.getLevel() != 100) {
				p.addLevel();
				p.resetXP();
				Player pl = Bukkit.getPlayer(p.getPlayerName());
				pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
				TitleManager.sendTitle(pl, "", ChatColor.GREEN + " >> Bravo ! Tu es désormais niveau " + p.getLevel() + " <<");
				pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
				
				if(hasPowerLevel(p.getLevel())) { 
					Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable(){
						@Override
						public void run() {
							checkForReward(p); 
						}
					}, 40);
				}
			}
	 
		}

		@SuppressWarnings("deprecation")
		private void checkForReward(PlayerManager p) {
			// Check for permission
			if(!isPermissionNull(p.getLevel()).equals("no")) {
				PermissionManager pex = PermissionsEx.getPermissionManager();
				PermissionUser userPermission = pex.getUser(p.getPlayerName());
				
				if(plugin.manager.getPermission(p.getLevel()) != null && !userPermission.has(plugin.manager.getPermission(p.getLevel()))) {
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "PEX user " + p.getPlayerName() +  " add " + plugin.manager.getPermission(p.getLevel()));
				}
				
				if(plugin.manager.getPermissionName2(p.getLevel()).contains("home")) {
					TitleManager.sendTitle(Bukkit.getPlayer(p.getPlayerName()), "", ChatColor.GOLD + ">> Tu viens de débloquer la permission d'avoir " + plugin.manager.getPermissionName2(p.getLevel()) + " <<");
				}
				else {
					TitleManager.sendTitle(Bukkit.getPlayer(p.getPlayerName()), "", ChatColor.GOLD + ">> Tu viens de débloquer la permission d'utiliser le " + plugin.manager.getPermissionName2(p.getLevel()) + " <<");
				}
			}
			
			//Check for item 1
			if(!isObject1Null(p.getLevel()).equals("no")) {
				Player pl = Bukkit.getPlayer(p.getPlayerName());
				
				pl.getInventory().addItem(new ItemStack(Material.getMaterial(plugin.manager.getSpecialObjectId1(p.getLevel()))));
				pl.updateInventory();

				TitleManager.sendTitle(pl, "", ChatColor.GREEN + ">> Tu viens de gagner en passant de niveau : " + getSpecialObjectName1(p.getLevel()) + " ! <<");
			}
			
			//Check for item 2
			if(!isObject2Null(p.getLevel()).equals("no")) {
				Player pl = Bukkit.getPlayer(p.getPlayerName());
				
				pl.getInventory().addItem(new ItemStack(Material.getMaterial(plugin.manager.getSpecialObjectId2(p.getLevel()))));
				pl.updateInventory();

				TitleManager.sendTitle(pl, "", ChatColor.GREEN + ">> Tu viens de gagner en passant de niveau : " + getSpecialObjectName2(p.getLevel()) + " ! <<");
			}
			
			
		}

		public int getXPNeededFor(int level) {
	    	if(level == 0) {
	    		return 0;
	    	}
	    	else if(level == 1) {
				return 35;
			}
			else if(level == 2) {
				return 40;
			}
			else if(level == 3) {
				return 46;
			}
			else if(level == 4) {
				return 53;
			}
			else if(level == 5) {
				return 61;
			}
			else if(level == 6) {
				return 71;
			}
			else if(level == 7) {
				return 82;
			}
			else if(level == 8) {
				return 95;
			}
			else if(level == 9) {
				return 110;
			}
			else if(level == 10) {
				return 128;
			}
			else if(level == 11) {
				return 149;
			}
			else if(level == 12) {
				return 173;
			}
			else if(level == 13) {
				return 201;
			}
			else if(level == 14) {
				return 234;
			}
			else if(level == 15) {
				return 273;
			}
			else if(level == 16) {
				return 318;
			}
			else if(level == 17) {
				return 371;
			}
			else if(level == 18) {
				return 432;
			}
			else if(level == 19) {
				return 504;
			}
			else if(level == 20) {
				return 588;
			}
			else if(level == 21) {
				return 686;
			}
			else if(level == 22) {
				return 800;
			}
			else if(level == 23) {
				return 933;
			}
			else if(level == 24) {
				return 1088;
			}
			else if(level == 25) {
				return 1269;
			}
			else if(level == 26) {
				return 1480;
			}
			else if(level == 27) {
				return 1726;
			}
			else if(level == 28) {
				return 2013;
			}
			else if(level == 29) {
				return 2348;
			}
			else if(level == 30) {
				return 2739;
			}
			else if(level == 31) {
				return 3195;
			}
			else if(level == 32) {
				return 3727;
			}
			else if(level == 33) {
				return 4348;
			}
			else if(level == 34) {
				return 5072;
			}
			else if(level == 35) {
				return 5917;
			}
			else if(level == 36) {
				return 6903;
			}
			else if(level == 37) {
				return 8053;
			}
			else if(level == 38) {
				return 9395;
			}
			else if(level == 39) {
				return 10960;
			}
			else if(level == 40) {
				return 11786;
			}
			else if(level == 41) {
				return 12917;
			}
			else if(level == 42) {
				return 14403;
			}
			else if(level == 43) {
				return 17303;
			}
			else if(level == 44) {
				return 20686;
			}
			else if(level == 45) {
				return 23633;
			}
			else if(level == 46) {
				return 27238;
			}
			else if(level == 47) {
				return 31611;
			}
			else if(level == 48) {
				return 43879;
			}
			else if(level == 49) {
				return 47192;
			}
			else if(level == 50) {
				return 52724;
			}
			else if(level == 51) {
				return 58678;
			}
			else if(level == 52) {
				return 64291;
			}
			else if(level == 53) {
				return 71839;
			}
			else if(level == 54) {
				return 79645;
			}
			else if(level == 55) {
				return 87085;
			}
			else if(level == 56) {
				return 99599;
			}
			else if(level == 57) {
				return 110698;
			}
			else if(level == 58) {
				return 120981;
			}
			else if(level == 59) {
				return 130144;
			}
			else if(level == 60) {
				return 141001;
			}
			else if(level == 61) {
				return 152501;
			}
			else if(level == 62) {
				return 164751;
			}
			else if(level == 63) {
				return 176042;
			}
			else if(level == 64) {
				return 188882;
			}
			else if(level == 65) {
				return 200029;
			}
			else if(level == 66) {
				return 224533;
			}
			else if(level == 67) {
				return 248788;
			}
			else if(level == 68) {
				return 272586;
			}
			else if(level == 69) {
				return 294183;
			}
			else if(level == 70) {
				return 315380;
			}
			else if(level == 71) {
				return 338610;
			}
			else if(level == 72) {
				return 358045;
			}
			else if(level == 73) {
				return 379719;
			}
			else if(level == 74) {
				return 399672;
			}
			else if(level == 75) {
				return 429117;
			}
			else if(level == 76) {
				return 454636;
			}
			else if(level == 77) {
				return 486408;
			}
			else if(level == 78) {
				return 510476;
			}
			else if(level == 79) {
				return 535055;
			}
			else if(level == 80) {
				return 572897;
			}
			else if(level == 81) {
				return 600713;
			}
			else if(level == 82) {
				return 630665;
			}
			else if(level == 83) {
				return 670942;
			}
			else if(level == 84) {
				return 700432;
			}
			else if(level == 85) {
				return 725846;
			}
			else if(level == 86) {
				return 752654;
			}
			else if(level == 87) {
				return 783645;
			}
			else if(level == 88) {
				return 805975;
			}
			else if(level == 89) {
				return 824456;
			}
			else if(level == 90) {
				return 853032;
			}
			else if(level == 91) {
				return 876870;
			}
			else if(level == 92) {
				return 899848;
			}
			else if(level == 93) {
				return 925851;
			}
			else if(level == 94) {
				return 945261;
			}
			else if(level == 95) {
				return 960446;
			}
			else if(level == 96) {
				return 984316;
			}
			else if(level == 97) {
				return 83682363;
			}
			else if(level == 98) {
				return 97629423;
			}
			else if(level == 99) {
				return 1000000;
			}
			else if(level == 100) {
				return 1000000000;
			}
			return 0;

		}
}
