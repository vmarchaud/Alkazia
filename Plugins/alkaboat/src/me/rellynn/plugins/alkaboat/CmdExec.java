package me.rellynn.plugins.alkaboat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdExec implements CommandExecutor {
    private final BoatPlugin plug;

	private String prefix = ChatColor.DARK_RED + "[" + ChatColor.GOLD
			+ "Alkazia" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
	
    public CmdExec(final BoatPlugin plug) {
	this.plug = plug;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] arg3) {
	if (this.plug.getBoat() != null) {
		 final StringBuilder message = new StringBuilder();
		
		if(arg3.length == 0) {
		    int remainingDurationInSecs = 0;
		    if (this.plug.getBoat().getElapsedDuration() > this.plug.getBoat().getPrespawnDuration()) remainingDurationInSecs = 14400 - this.plug.getBoat().getElapsedSeconds() + this.plug.getBoat().getPrespawnDuration() * 60;
		    else if (this.plug.getBoat().getElapsedSeconds() == -1) remainingDurationInSecs = this.plug.getBoat().getCoolActivation().getRemainingSeconds() + this.plug.getBoat().getPrespawnDuration() * 60;
		    else remainingDurationInSecs = this.plug.getBoat().getPrespawnDuration() * 60 - this.plug.getBoat().getElapsedSeconds();
		    
		    if(this.plug.getBoat().isSpawned() && this.plug.getBoat().getElapsedDuration() >= this.plug.getBoat().getPrespawnDuration() && this.plug.getBoat().getElapsedDuration() <= this.plug.getBoat().getPrespawnDuration() + 10) {
		    	message.append("&aLe bateau a apparu en &eX: " + this.plug.getBoat().getSpawnLocation().getBlockX() + "&a, &eZ: " + this.plug.getBoat().getSpawnLocation().getBlockZ() + "&a il y'a déjà &e" + plug.getBoat().getElapsedDuration() + " &aminutes.");
		    }
		    else {
			    final int remainingHours = remainingDurationInSecs / 3600;
			    final int remainingMins = remainingDurationInSecs / 60 % 60;
			    final int remainingSecs = remainingDurationInSecs % 60;
		
			    message.append("&eLe bateau va s'échouer dans&a ");
			    if (remainingHours > 0) {
				message.append(remainingHours);
				message.append(remainingHours == 1 ? " heure" : " heures");
			    }
			    if (remainingHours > 0 && remainingMins > 0) message.append("&e" + (remainingSecs > 0 ? "," : " et") + " &a");
			    if (remainingMins > 0) {
				message.append(remainingMins);
				message.append(remainingMins == 1 ? " minute" : " minutes");
			    }
			    if (remainingMins > 0 && remainingSecs > 0) message.append(" &eet &a");
			    if (remainingSecs > 0) {
				message.append(remainingSecs);
				message.append(remainingSecs == 1 ? " seconde" : " secondes");
			    }
		    }
		}
		/*
		if(arg3.length == 1 && arg3[0].contains("go") && sender instanceof Player) {
			if(plug.getBoat().getElapsedDuration() > plug.getBoat().getPrespawnDuration() + 2
					&& plug.getBoat().isSpawned()
					&& plug.getBoat().getElapsedDuration() < plug.getBoat().getPrespawnDuration() + 10) {
			Player player = (Player)sender;

			Location meteorLoc = plug.getBoat().getSpawnLocation();
			
					double amount = 25 + plug.getLevel().getPlayer(player.getName()).getLevel();

					Location loc = plug.getBoat().findPosition();
					if(loc == null) message.append(ChatColor.RED + ">> Aucun point de téléportation n'a été trouvé, veuillez ré-essayer <<");
					else {
						
						if(plug.getEco().getBalance(player.getName()) >= amount) {
							player.teleport(loc);
							player.getInventory().addItem(new ItemStack(Material.BOAT));
							player.playSound(loc, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
							message.append(ChatColor.GREEN + ">> Vous avez été téléporté à environ 500 block du bateau par un magicien qui vous as pris " + amount + " dollars <<");
							plug.getEco().withdrawPlayer(player.getName(), amount);
						}
						else {
							message.append(ChatColor.RED + ">> Vous n'avez pas assez d'argent pour vous téléportez <<");
						}
						
					}
			}
			else {
				message.append(ChatColor.RED + ">> La météorite n'est pas encore apparu ou il faut attendre deux minutes après le spawn pour s'y rendre <<");
			}
				
		}*/
	    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message.toString()));
	}

	return true;
    }
}
