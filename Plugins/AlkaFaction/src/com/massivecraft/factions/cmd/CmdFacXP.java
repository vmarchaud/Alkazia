package com.massivecraft.factions.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class CmdFacXP implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length >= 1 || sender instanceof Player) {
            final FPlayer fplayer = args.length == 0 ? FPlayers.i.get((Player) sender) : FPlayers.i.getBestIdMatch(args[0]);
            if (fplayer == null) return true;
            final Faction faction = fplayer.getFaction();
            if (fplayer.hasFaction()) {
                sender.sendMessage("[REP-F-" + fplayer.getName() + "]" + faction.getTag() + ":" + faction.getLevel().getLevel() + ":" + faction.getXP());
            }
            return true;
        }
        return false;
    }
}
