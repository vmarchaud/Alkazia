package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdKick extends FCommand {

    public CmdKick() {
        super();
        this.aliases.add("kick");

        this.requiredArgs.add("player");
        //this.optionalArgs.put("", "");

        this.permission = Permission.KICK.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) return;

        if (this.fme == you) {
            this.msg("<b>You cannot kick yourself.");
            this.msg("<i>You might want to: %s", this.p.cmdBase.cmdLeave.getUseageTemplate(false));
            return;
        }

        if (you.getRole() == Rel.LEADER && !(this.senderIsConsole || this.fme.hasAdminMode())) {
            this.msg("<b>The leader can not be kicked.");
            return;
        }

        if (!Conf.canLeaveWithNegativePower && you.getPower() < 0) {
            this.msg("<b>You cannot kick that member until their power is positive.");
            return;
        }

        final Faction yourFaction = you.getFaction();

        if (this.fme != null && !FPerm.KICK.has(this.fme, yourFaction)) return;

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!this.canAffordCommand(Conf.econCostKick, "to kick someone from the faction")) return;

        // trigger the leave event (cancellable) [reason:kicked]
        final FPlayerLeaveEvent event = new FPlayerLeaveEvent(you, you.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // then make 'em pay (if applicable)
        if (!this.payForCommand(Conf.econCostKick, "to kick someone from the faction", "for kicking someone from the faction")) return;

        yourFaction.msg("%s<i> kicked %s<i> from the faction! :O", this.fme.describeTo(yourFaction, true), you.describeTo(yourFaction, true));
        you.msg("%s<i> kicked you from %s<i>! :O", this.fme.describeTo(you, true), yourFaction.describeTo(you));
        if (yourFaction != this.myFaction) {
            this.fme.msg("<i>You kicked %s<i> from the faction %s<i>!", you.describeTo(this.fme), yourFaction.describeTo(this.fme));
        }

        if (Conf.logFactionKick) {
            P.p.log((this.senderIsConsole ? "A console command" : this.fme.getName()) + " kicked " + you.getName() + " from the faction: " + yourFaction.getTag());
        }

        if (you.getRole() == Rel.LEADER) {
            yourFaction.promoteNewLeader();
        }

        yourFaction.deinvite(you);
        you.resetFactionData();
    }

}
