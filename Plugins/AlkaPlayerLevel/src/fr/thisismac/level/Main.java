package fr.thisismac.level;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Main extends JavaPlugin {

	Manager manager;
	
	private ConsoleCommandSender console;
	private World world = Bukkit.getWorld("world");

	Scoreboard board;
	Objective objective;
	
	private static Main main;

    public final String prefix =  ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Alkazia" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
	
	public ArrayList<PlayerManager>players = new ArrayList<PlayerManager>();

    private Connection conn;
    private ResultSet result;

	File levelsFile;
	FileConfiguration levelsConfig; 

	public void onDisable() {
		console.sendMessage(ChatColor.RED + "[AlkaLevel] Sauvegarde en cours...");
		saveDatas();//Save contents
		console.sendMessage(ChatColor.GREEN + "[AlkaLevel] Sauvegarde terminée !");
	}

	public void onEnable() {
		console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[AlkaLevel] Chargement en cours...");
		main = this;
		manager = new Manager(main);
		saveDefaultConfig();
		getCommand("level").setExecutor(new LevelCommand(main));
		getServer().getPluginManager().registerEvents(new LevelListeners(main), main);
		setupData();//Setup contents
		connect();
		loadDatas();//Load contents
		insertDatas("antoine");
		saveDatas();
		
		PlaceHolderLevels placeholder = new PlaceHolderLevels(this);
		placeholder.init();
		PlaceHolderLevels2 placeholder2 = new PlaceHolderLevels2(this);
		placeholder2.init();
		
		console.sendMessage(ChatColor.GREEN + "[AlkaLevel] Charge !");
	}

	private void saveDataScheduler() {
		BukkitTask task = getServer().getScheduler().runTaskTimer(this,
				new Runnable() {
					public void run() {
						if(!players.isEmpty()) {
							saveDatas();
						}
					}
				}, 5 * 20, 5 * 20);
		
	}

	public void connect() {
		 
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
 
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+this.getConfig().getString("bdd.ip")+":"+this.getConfig().getString("bdd.port")+"/"+this.getConfig().getString("bdd.bdd"), this.getConfig().getString("bdd.user"), this.getConfig().getString("bdd.passwd"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
    }
	
	public void setupData(){//Setup data and levels files.

		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}

		levelsFile = new File(getDataFolder() + File.separator + "levels.yml");
		levelsConfig = YamlConfiguration.loadConfiguration(levelsFile);

		if(!levelsFile.exists()){
			setupLevelsFile();
		}

	}

	public void setupLevelsFile(){//System to setup the Levels' file
		Integer[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
		List<Integer> numbers1 = Arrays.asList(numbers);
		for(int i = 1; i < 110; i++){
			if(numbers1.contains(i)){
				levelsConfig.set("Niveau_" + i + ".argent", 0);
				levelsConfig.set("Niveau_" + i + ".permission.adresse", "permissionExact");
				levelsConfig.set("Niveau_" + i + ".permission.nom", "no");//No determinate if the permission will be use
				levelsConfig.set("Niveau_" + i + ".objet_special1.id", 0);
				levelsConfig.set("Niveau_" + i + ".objet_special1.nom", "no");//No determinate if the object will be use
				levelsConfig.set("Niveau_" + i + ".objet_special2.id", 0);
				levelsConfig.set("Niveau_" + i + ".objet_special2.nom", "no");//No determinate if the object will be use
			}
		}
		for(int i = 1; i <= 100; i++){
			if(!numbers1.contains(i)){
				levelsConfig.set("Niveau_" + i + ".argent", "argent_entier");
			}
		}

		try{
			levelsConfig.save(levelsFile);
		}catch(IOException e){e.printStackTrace();}
	}

	public void insertDatas(String name) {//Insert a content.
		try{
        Statement state = conn.createStatement();
        state.executeUpdate("INSERT INTO "+this.getConfig().getString("bdd.tables.name")+" ("+this.getConfig().getString("bdd.tables.pseudo")+", "+this.getConfig().getString("bdd.tables.xp")+", "+this.getConfig().getString("bdd.tables.niveau")+") VALUES ('"+name+"', 0, 0)");
        state.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
	
	public void loadDatas(){//Load contents.
		if(!levelsFile.exists())setupLevelsFile();
		
		try {
			Statement state = conn.createStatement();
			result = state.executeQuery("SELECT * FROM "+this.getConfig().getString("bdd.tables.name")+" WHERE 1");
           
			try {    
				while(result.next()){
					if (result.getString(1) != null) {
						
					String key = result.getString(1);
					int xp = result.getInt(2);
					int levels = result.getInt(3);
					players.add(new PlayerManager(key, levels, xp));	
                   }
                 }
                       
			} catch (SQLException e) {
				e.printStackTrace();
            	}
			state.close();
        } catch (SQLException e) {
           
            e.printStackTrace();
        }
	}

	public void saveDatas(){//Save PlayerManager's contents
		if(players.isEmpty()) return;//Check if the ArrayList is empty

		for(PlayerManager player : players){
			if(player != null && Bukkit.getPlayer(player.getPlayerName()) != null && Bukkit.getPlayer(player.getPlayerName()).isOnline()) {
				try{
			        Statement state = conn.createStatement();
			        state.executeUpdate("UPDATE "+this.getConfig().getString("bdd.tables.name") +" SET "+this.getConfig().getString("bdd.tables.xp")+" = "+player.getxP()+", "+this.getConfig().getString("bdd.tables.niveau")+" = "+player.getLevel()+" WHERE "+this.getConfig().getString("bdd.tables.pseudo")+" = '"+player.getPlayerName()+"'");
			        state.close();
			        }catch(SQLException e){
			            e.printStackTrace();
			        }
			}
		}

	}
	
	public void saveData(PlayerManager player){
		if(!players.contains(player) || player == null) return;
				try{
			        Statement state = conn.createStatement();
			        state.executeUpdate("UPDATE "+this.getConfig().getString("bdd.tables.name") +" SET "+this.getConfig().getString("bdd.tables.xp")+" = "+player.getxP()+", "+this.getConfig().getString("bdd.tables.niveau")+" = "+player.getLevel()+" WHERE "+this.getConfig().getString("bdd.tables.pseudo")+" = '"+player.getPlayerName()+"'");
			        state.close();
			        }catch(SQLException e)	{
			            e.printStackTrace();
			    }
	}

	public PlayerManager getPlayer(String name) {
		for(PlayerManager p : players) {
			if(p.getPlayerName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}
	
	public Manager getManager() {
		return manager;
	}
	
}
