package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdAutoClaim extends FCommand {
    public CmdAutoClaim() {
        super();
        this.aliases.add("autoclaim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.AUTOCLAIM.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final Faction forFaction = this.argAsFaction(0, this.myFaction);
        if (forFaction == null || forFaction == this.fme.getAutoClaimFor()) {
            this.fme.setAutoClaimFor(null);
            this.msg("<i>Auto-claiming of land disabled.");
            return;
        }

        if (!FPerm.TERRITORY.has(this.fme, forFaction, true)) return;

        this.fme.setAutoClaimFor(forFaction);

        this.msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeTo(this.fme));
        this.fme.attemptClaim(forFaction, this.me.getLocation(), true);
    }

}