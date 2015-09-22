package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdUnclaim extends FCommand {
    public CmdUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.UNCLAIM.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FLocation flocation = new FLocation(this.fme);
        final Faction otherFaction = Board.getFactionAt(flocation);

        if (!FPerm.TERRITORY.has(this.sender, otherFaction, true)) return;

        final LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, this.fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) return;

        //String moneyBack = "<i>";
        if (Econ.shouldBeUsed()) {
            final double refund = Econ.calculateClaimRefund(this.myFaction.getLandRounded());

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!Econ.modifyMoney(this.myFaction, refund, "to unclaim this land", "for unclaiming this land")) return;
            } else if (!Econ.modifyMoney(this.fme, refund, "to unclaim this land", "for unclaiming this land")) return;
        }

        // AlkaziaFactions
        if (this.myFaction.isNormal() && this.myFaction.hasHome() && this.myFaction.getHome().getChunk() == this.me.getLocation().getChunk()) {
            this.myFaction.getFactionBlock().setType(Conf.factionBlockReplacementMaterial);
        }
        // End AlkaziaFactions

        Board.removeAt(flocation);
        SpoutFeatures.updateTerritoryDisplayLoc(flocation);
        this.myFaction.msg("%s<i> unclaimed some land.", this.fme.describeTo(this.myFaction, true));

        if (Conf.logLandUnclaims) {
            P.p.log(this.fme.getName() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
        }
    }

}
