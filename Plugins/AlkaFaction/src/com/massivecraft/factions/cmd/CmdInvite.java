package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdInvite extends FCommand {
    public CmdInvite() {
        super();
        this.aliases.add("invite");
        this.aliases.add("inv");

        this.requiredArgs.add("player");
        //this.optionalArgs.put("", "");

        this.permission = Permission.INVITE.node;
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
            this.msg("<i>You might want to: " + this.p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }

        if (this.fme != null && !FPerm.INVITE.has(this.fme, this.myFaction)) return;

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostInvite, "to invite someone", "for inviting someone")) return;

        // AlkaziaFactions
        if (this.myFaction.getFPlayers().size() >= this.myFaction.getLevel().getMaxMembers()) {
            this.fme.sendMessage(ChatColor.RED + "Votre faction a atteint la limite de membres maximum (" + this.myFaction.getLevel().getMaxMembers() + "). Pour plus de liberté, montez votre clan en niveau.");
            return;
        }
        // End AlkaziaFactions

        this.myFaction.invite(you);

        you.msg("%s<i> invited you to %s", this.fme.describeTo(you, true), this.myFaction.describeTo(you));
        this.myFaction.msg("%s<i> invited %s<i> to your faction.", this.fme.describeTo(this.myFaction, true), you.describeTo(this.myFaction));
    }

}
