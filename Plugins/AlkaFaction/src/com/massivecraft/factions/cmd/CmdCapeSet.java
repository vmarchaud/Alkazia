package com.massivecraft.factions.cmd;

import java.net.URL;

import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.RelationUtil;

public class CmdCapeSet extends CapeCommand {

    public CmdCapeSet() {
        this.aliases.add("set");
        this.requiredArgs.add("url");
        this.permission = Permission.CAPE_SET.node;
    }

    @Override
    public void perform() {
        final String newCape = this.argAsString(0);

        if (CmdCapeSet.isUrlValid(newCape)) {
            this.capeFaction.setCape(newCape);
            SpoutFeatures.updateCape(this.capeFaction, null);
            this.msg("<h>%s <i>set the cape of <h>%s<i> to \"<h>%s<i>\".", RelationUtil.describeThatToMe(this.fme, this.fme, true), this.capeFaction.describeTo(this.fme), newCape);
            this.capeFaction.msg("<h>%s <i>set the cape of <h>%s<i> to \"<h>%s<i>\".", RelationUtil.describeThatToMe(this.fme, this.capeFaction, true), this.capeFaction.describeTo(this.capeFaction), newCape);
        } else {
            this.msg("<i>\"<h>%s<i>\" is not a valid URL.", newCape);
        }
    }

    public static boolean isUrlValid(final String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
