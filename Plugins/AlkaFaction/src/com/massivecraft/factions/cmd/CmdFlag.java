package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.Lang;

public class CmdFlag extends FCommand {

    public CmdFlag() {
        super();
        this.aliases.add("flag");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");
        this.optionalArgs.put("flag", "all");
        this.optionalArgs.put("yes/no", "read");

        this.permission = Permission.FLAG.node;
        this.disableOnLock = true;

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
            this.msg(this.p.txt.titleize("Flags for " + faction.describeTo(this.fme, true)));
            for (final FFlag flag : FFlag.values()) {
                this.msg(flag.getStateInfo(faction.getFlag(flag), true));
            }
            return;
        }

        final FFlag flag = this.argAsFactionFlag(1);
        if (flag == null) return;
        if (!this.argIsSet(2)) {
            this.msg(this.p.txt.titleize("Flag for " + faction.describeTo(this.fme, true)));
            this.msg(flag.getStateInfo(faction.getFlag(flag), true));
            return;
        }

        final Boolean targetValue = this.argAsBool(2);
        if (targetValue == null) return;

        // Do the sender have the right to change flags?
        if (!Permission.FLAG_SET.has(this.sender, true)) return;

        // Do the change
        this.msg(this.p.txt.titleize("Flag for " + faction.describeTo(this.fme, true)));
        faction.setFlag(flag, targetValue);
        this.msg(flag.getStateInfo(faction.getFlag(flag), true));
    }

}
