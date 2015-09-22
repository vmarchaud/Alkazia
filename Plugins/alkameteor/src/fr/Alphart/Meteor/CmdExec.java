package fr.Alphart.Meteor;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.thisismac.level.PlayerManager;

public class CmdExec implements CommandExecutor {
    private final MeteorPlugin plug;
    private final Random random = new Random();
	private String prefix = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Alkazia" + ChatColor.DARK_RED + "] " + ChatColor.RESET;

    public CmdExec(final MeteorPlugin plug) {
	this.plug = plug;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] arg3) {
    	if (this.plug.getMeteor() != null) {
		 final StringBuilder message = new StringBuilder();
		 
		if(arg3.length == 0) {
			int remainingDurationInSecs = 0;
		    if (this.plug.getMeteor().getElapsedDuration() > this.plug.getMeteor().getPrespawnDuration()) remainingDurationInSecs = 7200 - this.plug.getMeteor().getElapsedSeconds() + this.plug.getMeteor().getPrespawnDuration() * 60;
		    else if (this.plug.getMeteor().getElapsedSeconds() == -1) remainingDurationInSecs = this.plug.getMeteor().getCoolActivation().getRemainingSeconds() + this.plug.getMeteor().getPrespawnDuration() * 60;
		    else remainingDurationInSecs = this.plug.getMeteor().getPrespawnDuration() * 60 - this.plug.getMeteor().getElapsedSeconds();
		    
		    if(this.plug.getMeteor().isSpawned() && this.plug.getMeteor().getElapsedDuration() <= this.plug.getMeteor().getPrespawnDuration() + 10) {
		    	message.append("&aLa météorite est vers &eX: " + this.plug.getMeteor().getSpawnLocation().getBlockX() + "&a, &eZ: " + this.plug.getMeteor().getSpawnLocation().getBlockZ() + "&a il y'a déjà &e" + plug.getMeteor().getElapsedDuration() + " &aminutes.");
		    }
		    else {
		    	final int remainingHours = remainingDurationInSecs / 3600;
			    final int remainingMins = remainingDurationInSecs / 60 % 60;
			    final int remainingSecs = remainingDurationInSecs % 60;
			    
			    message.append("&eLa météorite va s'écraser dans&a ");
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
		
	    
	    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message.toString()));
	    return true;
	}
   

	return true;
    }
}
