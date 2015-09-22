package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdCapeGet extends CapeCommand {
    public CmdCapeGet() {
        this.aliases.add("get");
        this.permission = Permission.CAPE_GET.node;
    }

    @Override
    public void perform() {
        if (this.currentCape == null) {
            this.msg("<h>%s <i>has no cape set.", this.capeFaction.describeTo(this.fme, true));
        } else {
            this.msg("<i>The cape of <h>%s <i>is \"<h>%s<i>\".", this.capeFaction.describeTo(this.fme, true), this.currentCape);
        }
    }
}
