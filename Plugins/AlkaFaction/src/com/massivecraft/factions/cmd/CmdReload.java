package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdReload extends FCommand {

    public CmdReload() {
        super();
        this.aliases.add("reload");

        //this.requiredArgs.add("");
        this.optionalArgs.put("file", "all");

        this.permission = Permission.RELOAD.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final long timeInitStart = System.currentTimeMillis();
        final String file = this.argAsString(0, "all").toLowerCase();

        String fileName;

        if (file.startsWith("c")) {
            Conf.load();
            fileName = "conf.json";
        } else if (file.startsWith("b")) {
            Board.load();
            fileName = "board.json";
        } else if (file.startsWith("f")) {
            Factions.i.loadFromDisc();
            fileName = "factions.json";
        } else if (file.startsWith("p")) {
            FPlayers.i.loadFromDisc();
            fileName = "players.json";
        } else if (file.startsWith("a")) {
            fileName = "all";
            Conf.load();
            FPlayers.i.loadFromDisc();
            Factions.i.loadFromDisc();
            Board.load();
        } else {
            P.p.log("RELOAD CANCELLED - SPECIFIED FILE INVALID");
            this.msg("<b>Invalid file specified. <i>Valid files: all, conf, board, factions, players");
            return;
        }

        final long timeReload = System.currentTimeMillis() - timeInitStart;

        this.msg("<i>Reloaded <h>%s <i>from disk, took <h>%dms<i>.", fileName, timeReload);
    }

}
