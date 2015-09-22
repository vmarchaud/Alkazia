package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdDemote extends FCommand {

    public CmdDemote() {
        super();
        this.aliases.add("demote");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.DEMOTE.node;
        this.disableOnLock = true;

        //To demote someone from member -> recruit you must be an officer.
        //To demote someone from officer -> member you must be a leader.
        //We'll handle this internally
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) return;

        if (you.getFaction() != this.myFaction) {
            this.msg("%s<b> is not a member in your faction.", you.describeTo(this.fme, true));
            return;
        }

        if (you == this.fme) {
            this.msg("<b>The target player mustn't be yourself.");
            return;
        }

        if (you.getRole() == Rel.MEMBER) {
            if (!this.fme.getRole().isAtLeast(Rel.OFFICER)) {
                this.msg("<b>You must be an officer to demote a member to recruit.");
                return;
            }
            you.setRole(Rel.RECRUIT);
            this.myFaction.msg("%s<i> was demoted to being a recruit in your faction.", you.describeTo(this.myFaction, true));
        } else if (you.getRole() == Rel.OFFICER) {
            if (!this.fme.getRole().isAtLeast(Rel.LEADER)) {
                this.msg("<b>You must be the leader to demote an officer to member.");
                return;
            }
            you.setRole(Rel.MEMBER);
            this.myFaction.msg("%s<i> was demoted to being a member in your faction.", you.describeTo(this.myFaction, true));
        }
    }

}
