package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdPromote extends FCommand {

    public CmdPromote() {
        super();
        this.aliases.add("promote");

        this.requiredArgs.add("player name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.PROMOTE.node;
        this.disableOnLock = true;

        //To promote someone from recruit -> member you must be an officer.
        //To promote someone from member -> officer you must be a leader.
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

        if (you.getRole() == Rel.RECRUIT) {
            if (!this.fme.getRole().isAtLeast(Rel.OFFICER)) {
                this.msg("<b>You must be an officer to promote someone to member.");
                return;
            }
            you.setRole(Rel.MEMBER);
            this.myFaction.msg("%s<i> was promoted to being a member of your faction.", you.describeTo(this.myFaction, true));
        } else if (you.getRole() == Rel.MEMBER) {
            if (!this.fme.getRole().isAtLeast(Rel.LEADER)) {
                this.msg("<b>You must be the leader to promote someone to officer.");
                return;
            }
            // Give
            you.setRole(Rel.OFFICER);
            this.myFaction.msg("%s<i> was promoted to being a officer in your faction.", you.describeTo(this.myFaction, true));
        }
    }

}
