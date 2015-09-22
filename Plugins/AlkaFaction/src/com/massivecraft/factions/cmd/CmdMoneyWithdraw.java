package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdMoneyWithdraw extends FCommand {
    public CmdMoneyWithdraw() {
        this.aliases.add("w");
        this.aliases.add("withdraw");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.MONEY_WITHDRAW.node;
        this.setHelpShort("withdraw money");

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final double amount = this.argAsDouble(0, 0d);
        final EconomyParticipator faction = this.argAsFaction(1, this.myFaction);
        if (faction == null) return;
        final boolean success = Econ.transferMoney(this.fme, faction, this.fme, amount);

        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse("%s withdrew %s from the faction bank: %s", this.fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
        }
    }
}
