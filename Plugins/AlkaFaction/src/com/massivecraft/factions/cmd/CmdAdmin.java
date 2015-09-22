package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdAdmin extends FCommand {
    public CmdAdmin() {
        super();
        this.aliases.add("admin");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.ADMIN.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        this.fme.setHasAdminMode(this.argAsBool(0, !this.fme.hasAdminMode()));

        if (this.fme.hasAdminMode()) {
            this.fme.msg("<i>You have enabled admin bypass mode.");
            P.p.log(this.fme.getName() + " has ENABLED admin bypass mode.");
        } else {
            this.fme.msg("<i>You have disabled admin bypass mode.");
            P.p.log(this.fme.getName() + " DISABLED admin bypass mode.");
        }
    }
}
