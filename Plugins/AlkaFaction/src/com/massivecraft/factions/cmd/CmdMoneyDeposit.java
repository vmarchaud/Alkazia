package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;

public class CmdMoneyDeposit extends FCommand {

    public CmdMoneyDeposit() {
        super();
        this.aliases.add("d");
        this.aliases.add("deposit");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.MONEY_DEPOSIT.node;
        this.setHelpShort("deposit money");

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
        final boolean success = Econ.transferMoney(this.fme, this.fme, faction, amount);

        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse("%s deposited %s in the faction bank: %s", this.fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
        }
    }

}
