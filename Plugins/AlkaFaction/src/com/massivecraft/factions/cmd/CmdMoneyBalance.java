package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdMoneyBalance extends FCommand {
    public CmdMoneyBalance() {
        super();
        this.aliases.add("b");
        this.aliases.add("balance");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.MONEY_BALANCE.node;
        this.setHelpShort("show faction balance");

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        Faction faction = this.myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
        }

        if (faction == null) return;
        if (faction != this.myFaction && !Permission.MONEY_BALANCE_ANY.has(this.sender, true)) return;

        Econ.sendBalanceInfo(this.fme, faction);
    }

}
