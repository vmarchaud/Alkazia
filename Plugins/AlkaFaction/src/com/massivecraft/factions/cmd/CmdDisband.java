package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdDisband extends FCommand {
    public CmdDisband() {
        super();
        this.aliases.add("disband");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.DISBAND.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        // The faction, default to your own.. but null if console sender.
        final Faction faction = this.argAsFaction(0, this.fme == null ? null : this.myFaction);
        if (faction == null) return;

        if (!FPerm.DISBAND.has(this.sender, faction, true)) return;

        if (faction.getFlag(FFlag.PERMANENT)) {
            this.msg("<i>This faction is designated as permanent, so you cannot disband it.");
            return;
        }

        final FactionDisbandEvent disbandEvent = new FactionDisbandEvent(this.me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) return;

        // Send FPlayerLeaveEvent for each player in the faction
        for (final FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
        }

        // Inform all players
        for (final FPlayer fplayer : FPlayers.i.getOnline()) {
            final String who = this.senderIsConsole ? "A server admin" : this.fme.describeTo(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg("<h>%s<i> disbanded your faction.", who);
            } else {
                fplayer.msg("<h>%s<i> disbanded the faction %s.", who, faction.getTag(fplayer));
            }
        }
        if (Conf.logFactionDisband) {
            P.p.log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (this.senderIsConsole ? "console command" : this.fme.getName()) + ".");
        }

        if (Econ.shouldBeUsed() && !this.senderIsConsole) {
            //Give all the faction's money to the disbander
            final double amount = Econ.getBalance(faction.getAccountId());
            Econ.transferMoney(this.fme, faction, this.fme, amount, false);

            if (amount > 0.0) {
                final String amountString = Econ.moneyString(amount);
                this.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
                P.p.log(this.fme.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
            }
        }
        
        faction.detach();

        SpoutFeatures.updateTitle(null, null);
        SpoutFeatures.updateCape(null, null);
    }
}
