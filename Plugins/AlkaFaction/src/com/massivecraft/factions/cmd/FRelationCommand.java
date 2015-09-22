package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public abstract class FRelationCommand extends FCommand {
    public Rel targetRelation;

    public FRelationCommand() {
        super();
        this.requiredArgs.add("faction");
        //this.optionalArgs.put("", "");

        this.permission = Permission.RELATION.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final Faction them = this.argAsFaction(0);
        if (them == null) return;

        /*if ( ! them.isNormal())
        {
        	msg("<b>Nope! You can't.");
        	return;
        }*/

        if (them == this.myFaction) {
            this.msg("<b>Nope! You can't declare a relation to yourself :)");
            return;
        }

        if (this.myFaction.getRelationWish(them) == this.targetRelation) {
            this.msg("<b>You already have that relation wish set with %s.", them.getTag());
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(this.targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish")) return;

        // try to set the new relation
        final Rel oldRelation = this.myFaction.getRelationTo(them, true);
        this.myFaction.setRelationWish(them, this.targetRelation);
        final Rel currentRelation = this.myFaction.getRelationTo(them, true);

        // if the relation change was successful
        if (this.targetRelation == currentRelation) {
            // trigger the faction relation event
            final FactionRelationEvent relationEvent = new FactionRelationEvent(this.myFaction, them, oldRelation, currentRelation);
            Bukkit.getServer().getPluginManager().callEvent(relationEvent);

            them.msg("%s<i> is now %s.", this.myFaction.describeTo(them, true), this.targetRelation.getDescFactionOne());
            this.myFaction.msg("%s<i> is now %s.", them.describeTo(this.myFaction, true), this.targetRelation.getDescFactionOne());
        }
        // inform the other faction of your request
        else {
            them.msg("%s<i> wishes to be %s.", this.myFaction.describeTo(them, true), this.targetRelation.getColor() + this.targetRelation.getDescFactionOne());
            them.msg("<i>Type <c>/" + Conf.baseCommandAliases.get(0) + " " + this.targetRelation + " " + this.myFaction.getTag() + "<i> to accept.");
            this.myFaction.msg("%s<i> were informed that you wish to be %s<i>.", them.describeTo(this.myFaction, true), this.targetRelation.getColor() + this.targetRelation.getDescFactionOne());
        }

        // TODO: The ally case should work!!
        //   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
        if (this.targetRelation != Rel.TRUCE && them.getFlag(FFlag.PEACEFUL)) {
            them.msg("<i>This will have no effect while your faction is peaceful.");
            this.myFaction.msg("<i>This will have no effect while their faction is peaceful.");
        }

        if (this.targetRelation != Rel.TRUCE && this.myFaction.getFlag(FFlag.PEACEFUL)) {
            them.msg("<i>This will have no effect while their faction is peaceful.");
            this.myFaction.msg("<i>This will have no effect while your faction is peaceful.");
        }

        SpoutFeatures.updateTitle(this.myFaction, them);
        SpoutFeatures.updateTitle(them, this.myFaction);
        SpoutFeatures.updateTerritoryDisplayLoc(null);
    }
}
