package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;

public class CmdMap extends FCommand {
    public CmdMap() {
        super();
        this.aliases.add("map");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "once");

        this.permission = Permission.MAP.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        if (this.argIsSet(0)) {
            if (this.argAsBool(0, !this.fme.isMapAutoUpdating())) {
                // Turn on

                // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
                if (!this.payForCommand(Conf.econCostMap, "to show the map", "for showing the map")) return;

                this.fme.setMapAutoUpdating(true);
                this.msg("<i>Map auto update <green>ENABLED.");

                // And show the map once
                this.showMap();
            } else {
                // Turn off
                this.fme.setMapAutoUpdating(false);
                this.msg("<i>Map auto update <red>DISABLED.");
            }
        } else {
            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!this.payForCommand(Conf.econCostMap, "to show the map", "for showing the map")) return;

            this.showMap();
        }
    }

    public void showMap() {
        this.sendMessage(Board.getMap(this.myFaction, new FLocation(this.fme), this.fme.getPlayer().getLocation().getYaw()));
    }

}
