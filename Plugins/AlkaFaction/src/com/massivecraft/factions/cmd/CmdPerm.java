package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.Lang;

public class CmdPerm extends FCommand {

    public CmdPerm() {
        super();
        this.aliases.add("perm");

        this.optionalArgs.put("faction", "your");
        this.optionalArgs.put("perm", "all");
        this.optionalArgs.put("relation", "read");
        this.optionalArgs.put("yes/no", "read");

        this.permission = Permission.PERM.node;
        this.disableOnLock = true;

        this.errorOnToManyArgs = false;

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
        if (faction == null) {
            if (this.senderIsConsole) {
                this.msg(Lang.commandToFewArgs);
                this.sender.sendMessage(this.getUseageTemplate());
            }
            return;
        }

        if (!this.argIsSet(1)) {
            this.msg(this.p.txt.titleize("Perms for " + faction.describeTo(this.fme, true)));
            this.msg(FPerm.getStateHeaders());
            for (final FPerm perm : FPerm.values()) {
                this.msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
            }
            return;
        }

        final FPerm perm = this.argAsFactionPerm(1);
        if (perm == null) return;
        if (!this.argIsSet(2)) {
            this.msg(this.p.txt.titleize("Perm for " + faction.describeTo(this.fme, true)));
            this.msg(FPerm.getStateHeaders());
            this.msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
            return;
        }

        // Do the sender have the right to change perms for this faction?
        if (!FPerm.PERMS.has(this.sender, faction, true)) return;

        final Rel rel = this.argAsRel(2);
        if (rel == null) return;

        final Boolean val = this.argAsBool(3, null);
        if (val == null) return;

        // Do the change
        faction.setRelationPermitted(perm, rel, val);

        // The following is to make sure the leader always has the right to change perms if that is our goal.
        if (perm == FPerm.PERMS && FPerm.PERMS.getDefault().contains(Rel.LEADER)) {
            faction.setRelationPermitted(FPerm.PERMS, Rel.LEADER, true);
        }

        this.msg(this.p.txt.titleize("Perm for " + faction.describeTo(this.fme, true)));
        this.msg(FPerm.getStateHeaders());
        this.msg(perm.getStateInfo(faction.getPermittedRelations(perm), true));
    }

}
