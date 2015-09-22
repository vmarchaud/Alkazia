package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdLeave extends FCommand {

    public CmdLeave() {
        super();
        this.aliases.add("leave");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.LEAVE.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        this.fme.leave(true);
    }

}
