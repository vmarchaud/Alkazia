package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdTitle extends FCommand {
    public CmdTitle() {
        this.aliases.add("title");

        this.requiredArgs.add("player");
        this.optionalArgs.put("title", "");

        this.permission = Permission.TITLE.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) return;

        this.args.remove(0);
        final String title = TextUtil.implode(this.args, " ");

        if (!this.canIAdministerYou(this.fme, you)) return;

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostTitle, "to change a players title", "for changing a players title")) return;

        you.setTitle(title);

        // Inform
        this.myFaction.msg("%s<i> changed a title: %s", this.fme.describeTo(this.myFaction, true), you.describeTo(this.myFaction, true));

        if (Conf.spoutFactionTitlesOverNames) {
            SpoutFeatures.updateTitle(this.me, null);
        }
    }

}
