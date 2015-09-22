package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdOfficer extends FCommand {

    public CmdOfficer() {
        super();
        this.aliases.add("officer");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.OFFICER.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) return;

        final boolean permAny = Permission.OFFICER_ANY.has(this.sender, false);
        final Faction targetFaction = you.getFaction();

        if (targetFaction != this.myFaction && !permAny) {
            this.msg("%s<b> is not a member in your faction.", you.describeTo(this.fme, true));
            return;
        }

        if (this.fme != null && this.fme.getRole() != Rel.LEADER && !permAny) {
            this.msg("<b>You are not the faction leader.");
            return;
        }

        if (you == this.fme && !permAny) {
            this.msg("<b>The target player musn't be yourself.");
            return;
        }

        if (you.getRole() == Rel.LEADER) {
            this.msg("<b>The target player is a faction leader. Demote them first.");
            return;
        }

        if (you.getRole() == Rel.OFFICER) {
            // Revoke
            you.setRole(Rel.MEMBER);
            targetFaction.msg("%s<i> is no longer officer in your faction.", you.describeTo(targetFaction, true));
            this.msg("<i>You have removed officer status from %s<i>.", you.describeTo(this.fme, true));
        } else {
            // Give
            you.setRole(Rel.OFFICER);
            targetFaction.msg("%s<i> was promoted to officer in your faction.", you.describeTo(targetFaction, true));
            this.msg("<i>You have promoted %s<i> to officer.", you.describeTo(this.fme, true));
        }
    }

}
