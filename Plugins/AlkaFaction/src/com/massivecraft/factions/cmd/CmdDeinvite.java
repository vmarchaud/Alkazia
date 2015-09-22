package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite extends FCommand {

    public CmdDeinvite() {
        super();
        this.aliases.add("deinvite");
        this.aliases.add("deinv");

        this.requiredArgs.add("player");
        //this.optionalArgs.put("", "");

        this.permission = Permission.DEINVITE.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) return;

        if (you.getFaction() == this.myFaction) {
            this.msg("%s<i> is already a member of %s", you.getName(), this.myFaction.getTag());
            this.msg("<i>You might want to: %s", this.p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }

        this.myFaction.deinvite(you);

        you.msg("%s<i> revoked your invitation to <h>%s<i>.", this.fme.describeTo(you), this.myFaction.describeTo(you));

        this.myFaction.msg("%s<i> revoked %s's<i> invitation.", this.fme.describeTo(this.myFaction), you.describeTo(this.myFaction));
    }

}
