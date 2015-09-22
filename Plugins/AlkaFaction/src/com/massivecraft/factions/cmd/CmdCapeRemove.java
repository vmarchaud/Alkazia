package com.massivecraft.factions.cmd;

import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.RelationUtil;

public class CmdCapeRemove extends CapeCommand {

    public CmdCapeRemove() {
        this.aliases.add("rm");
        this.aliases.add("rem");
        this.aliases.add("remove");
        this.aliases.add("del");
        this.aliases.add("delete");
        this.permission = Permission.CAPE_REMOVE.node;
    }

    @Override
    public void perform() {
        if (this.currentCape == null) {
            this.msg("<h>%s <i>has no cape set.", this.capeFaction.describeTo(this.fme, true));
        } else {
            this.capeFaction.setCape(null);
            SpoutFeatures.updateCape(this.capeFaction, null);
            this.msg("<h>%s <i>removed the cape from <h>%s<i>.", RelationUtil.describeThatToMe(this.fme, this.fme, true), this.capeFaction.describeTo(this.fme));
            this.capeFaction.msg("<h>%s <i>removed the cape from <h>%s<i>.", RelationUtil.describeThatToMe(this.fme, this.capeFaction, true), this.capeFaction.describeTo(this.capeFaction));
        }
    }
}
