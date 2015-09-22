package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdPower extends FCommand {

    public CmdPower() {
        super();
        this.aliases.add("power");
        this.aliases.add("pow");

        //this.requiredArgs.add("faction tag");
        this.optionalArgs.put("player", "you");

        this.permission = Permission.POWER.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer target = this.argAsBestFPlayerMatch(0, this.fme);
        if (target == null) return;

        if (target != this.fme && !Permission.POWER_ANY.has(this.sender, true)) return;

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostPower, "to show player power info", "for showing player power info")) return;

        final double powerBoost = target.getPowerBoost();
        final String boost = powerBoost == 0.0 ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
        this.msg("%s<a> - Power / Maxpower: <i>%d / %d %s", target.describeTo(this.fme, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
    }

}
