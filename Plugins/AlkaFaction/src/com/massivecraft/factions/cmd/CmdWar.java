package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdWar extends FCommand {

    public CmdWar() {
        super();
        this.aliases.add("war");
        this.aliases.add("w");


        this.permission = Permission.WAR.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
    	
        
    }

}
