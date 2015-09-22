package fr.thisismac.level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {
	

	Main plugin;
	public LevelCommand(Main plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;

		Player p = (Player)sender;
		PlayerManager pl = plugin.getPlayer(p.getName());
		
		if(args.length == 0) {
				p.sendMessage("[REP-P-" + p.getName() + "]" + pl.getLevel() + ":" + pl.getxP());
		}
		else if(args.length == 1) {
					if(plugin.getPlayer(args[0]) != null) {
						p.sendMessage(plugin.prefix + ChatColor.GREEN + args[0]  + " est niveau " + plugin.getPlayer(args[0]).getLevel() + " avec " + plugin.getPlayer(args[0]).getxP() + " d'xp !");
					}
					else {
						p.sendMessage(plugin.prefix + ChatColor.RED + "Le joueur que vous recherchez n'existe pas.");
					}
		}
		else if(args.length == 4) {
			if(args[0].equalsIgnoreCase("set")) {
					if(plugin.getPlayer(args[1]) != null && p.isOp()) {
						plugin.getPlayer(args[1]).setLevel(Integer.valueOf(args[2]));
						plugin.getPlayer(args[1]).setXP(Integer.parseInt(args[3]));
						p.sendMessage(plugin.prefix + ChatColor.GREEN + args[1]  + " est désormais " + args[2] + " avec " + args[3] + " xp");
					}
					else {
						p.sendMessage(plugin.prefix + ChatColor.RED + "Le joueur que vous recherchez n'existe pas.");
					}
				}
				else {
					p.sendMessage(plugin.prefix + ChatColor.RED + "Vous n'avez pas la permission pour définir le niveau de quelqu'un.");
				}
		}
			
		return true;
	}


}