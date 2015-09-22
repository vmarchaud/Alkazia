package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdLock extends FCommand {

    // TODO: This solution needs refactoring.
    /*
       factions.lock:
       description: use the /f lock [on/off] command to temporarily lock the data files from being overwritten
       default: op
     */

    public CmdLock() {
        super();
        this.aliases.add("lock");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.LOCK.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        this.p.setLocked(this.argAsBool(0, !this.p.getLocked()));

        if (this.p.getLocked()) {
            this.msg("<i>Factions is now locked");
        } else {
            this.msg("<i>Factions in now unlocked");
        }
    }

}
