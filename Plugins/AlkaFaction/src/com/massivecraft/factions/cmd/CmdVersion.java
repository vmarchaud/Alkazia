package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdVersion extends FCommand {
    public CmdVersion() {
        this.aliases.add("version");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.VERSION.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        // AlkaziaFactions
        this.msg("<i>Factions modifié par Rellynn pour Alkazia.");
        // End AlkaziaFactions
    }
}
