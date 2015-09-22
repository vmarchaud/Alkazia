package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Rel;

public class CmdRelationTruce extends FRelationCommand {
    public CmdRelationTruce() {
        this.aliases.add("truce");
        this.targetRelation = Rel.TRUCE;
    }
}
