package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdOpen extends FCommand {
    public CmdOpen() {
        super();
        this.aliases.add("open");

        //this.requiredArgs.add("");
        this.optionalArgs.put("yes/no", "flip");

        this.permission = Permission.OPEN.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostOpen, "to open or close the faction", "for opening or closing the faction")) return;

        this.myFaction.setOpen(this.argAsBool(0, !this.myFaction.getOpen()));

        final String open = this.myFaction.getOpen() ? "open" : "closed";

        // Inform
        this.myFaction.msg("%s<i> changed the faction to <h>%s<i>.", this.fme.describeTo(this.myFaction, true), open);
        for (final Faction faction : Factions.i.get()) {
            if (faction == this.myFaction) {
                continue;
            }
            faction.msg("<i>The faction %s<i> is now %s", this.myFaction.getTag(faction), open);
        }
    }

}
