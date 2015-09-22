package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Rel;

public class CmdRelationEnemy extends FRelationCommand {
    public CmdRelationEnemy() {
        this.aliases.add("enemy");
        this.targetRelation = Rel.ENEMY;
    }
}
