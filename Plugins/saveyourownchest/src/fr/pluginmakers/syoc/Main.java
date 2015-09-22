package fr.pluginmakers.syoc;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	File dataFile;
	FileConfiguration data;

	public void onDisable(){
		try {
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.getOpenInventory().close();
			}
			data.save(dataFile);
		} catch (IOException e) {
			getLogger().warning(" :( Here are the errors which appear during the saving of the enderchests' content. I'm really sorry about that...");
			e.printStackTrace();
		}
	}

	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		setupData();
	}

	public void setupData(){
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
		dataFile = new File(getDataFolder() + File.separator + "data.yml");
		data = YamlConfiguration.loadConfiguration(dataFile);
		try {
			data.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void PlayerCloseEnderChest(InventoryCloseEvent  e){
		if(e.getInventory().getType() == InventoryType.ENDER_CHEST){
			Player p = (Player) e.getPlayer();
			ItemStack[] iv = p.getPlayer().getEnderChest().getContents();
			data.set(p.getName() + ".size", iv.length);
			for(int i = 0 ; i < iv.length ; i++){
				data.set(p.getName() + ".items." + i, iv[i]);
			}
		}

	}

	@EventHandler
	public void PlayerOpenEnderChest(InventoryOpenEvent  e){
		if(e.getInventory().getType() == InventoryType.ENDER_CHEST){
			Player p = (Player) e.getPlayer();
			Inventory iv = p.getPlayer().getEnderChest();
			if(data.contains(p.getName())){
				iv.clear();
				for(int i = 0 ; i < data.getInt(p.getName() + ".size") ; i++){
					if(data.getItemStack(p.getName() + ".items." + i) != null){
						iv.addItem(new ItemStack(data.getItemStack(p.getName() + ".items." + i)));
					}
				}	
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if(!(sender instanceof Player)){
			if(label.equalsIgnoreCase("saveenderchests")){
				try {
					data.save(dataFile);
				} catch (IOException e) {
					getLogger().warning(" :( Here are the errors which appear during the saving of the enderchests' content. I'm really sorry about that...");
					e.printStackTrace();
				}
				getLogger().info("Sauvegardes effectives !");
			}
			return true;
		}

		Player p = (Player)sender;

		if(args.length != 0) return false;

		if(label.equalsIgnoreCase("saveenderchests") && p.hasPermission("SaveYourOwnChest")){
			try {
				data.save(dataFile);
				p.sendMessage("[SaveYourOwnChest]" + ChatColor.GREEN + " Le contenu des enderchests des joueurs vient d'être sauvegardé.");
			} catch (IOException e) {
				p.sendMessage("[SaveYourOwnChest]" + ChatColor.RED + " :( Attention, une erreur est survenue durant la sauvegarde. Regardez les logs.");
				e.printStackTrace();
			}
		}

		return true;
	}



}
