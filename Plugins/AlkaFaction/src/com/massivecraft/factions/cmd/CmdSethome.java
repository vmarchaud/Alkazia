package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdSethome extends FCommand {
    public CmdSethome() {
        this.aliases.add("sethome");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.SETHOME.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        if (!Conf.homesEnabled) {
            this.fme.msg("<b>Sorry, Faction homes are disabled on this server.");
            return;
        }

        final Faction faction = this.argAsFaction(0, this.myFaction);
        if (faction == null) return;

        // Can the player set the home for this faction?
        if (!FPerm.SETHOME.has(this.sender, faction, true)) return;

        // Can the player set the faction home HERE?
        if (!this.fme.hasAdminMode() && Conf.homesMustBeInClaimedTerritory && Board.getFactionAt(new FLocation(this.me)) != faction) {
            this.fme.msg("<b>Votre home doit être établi dans un de vos claims");
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostSethome, "to set the faction home", "for setting the faction home")) return;

        // AlkaziaFactions
        final int minutes = (int) (15 - (System.currentTimeMillis() - faction.getLastEnemy()) / 1000 / 60);
        if (minutes <= 15 && minutes >= 0) {
            this.fme.msg("<b>Vous ne pouvez pas redéfinir le home faction pendant les " + minutes + " minute" + (minutes > 1 ? "s" : "") + " suivante" + (minutes > 1 ? "s" : "") + ".");
            return;
        }
        final Location newHome = this.me.getLocation();
        if (newHome.clone().subtract(0, 1, 0).getBlock().getType() == Material.BEDROCK) {
            this.fme.msg("<b>Vous ne pouvez pas définir votre home sur de la bedrock.");
            return;
        }
        final Location home = faction.getHome();
        if (faction.hasHome() && home != newHome) {
            faction.getFactionBlock().setType(Conf.factionBlockReplacementMaterial);
        }
        newHome.getBlock().breakNaturally();
        newHome.clone().add(0, 1, 0).getBlock().breakNaturally();
        newHome.clone().subtract(0, 1, 0).getBlock().setType(faction.isBrokenFactionBlock() ? Conf.brokenFactionBlockMaterial : Conf.factionBlockMaterial);
        faction.setHome(newHome);
        // End AlkaziaFactions

        faction.msg("%s<i> a établi le home de votre faction. Rejoignez le avec :", this.fme.describeTo(this.myFaction, true));
        faction.sendMessage(this.p.cmdBase.cmdHome.getUseageTemplate());
        if (faction != this.myFaction) {
            this.fme.msg("<b>You have set the home for the " + faction.getTag(this.fme) + "<i> faction.");
        }
    }

}
