package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SpiralTask;

public class CmdClaim extends FCommand {

    public CmdClaim() {
        super();
        this.aliases.add("claim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");
        this.optionalArgs.put("radius", "1");

        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        // Read and validate input
        final Faction forFaction = this.argAsFaction(0, this.myFaction);
        final int radius = this.argAsInt(1, 1);

        if (radius < 1) {
            this.msg("<b>If you specify a radius, it must be at least 1.");
            return;
        }

        if (radius < 2) {
            this.fme.attemptClaim(forFaction, this.me.getLocation(), true);
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(this.sender, false)) {
                this.msg("<b>You do not have permission to claim in a radius.");
                return;
            }

            new SpiralTask(new FLocation(this.me), radius) {
                private int failCount = 0;
                private final int limit = Conf.radiusClaimFailureLimit - 1;

                @Override
                public boolean work() {
                    final boolean success = CmdClaim.this.fme.attemptClaim(forFaction, this.currentLocation(), true);
                    if (success) {
                        this.failCount = 0;
                    } else if (!success && this.failCount++ >= this.limit) {
                        this.stop();
                        return false;
                    }

                    return true;
                }
            };
        }
    }
}
