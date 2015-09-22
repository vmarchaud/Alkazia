package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;

public class CmdTag extends FCommand {

    public CmdTag() {
        this.aliases.add("tag");

        this.requiredArgs.add("new tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.TAG.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = true;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final String tag = this.argAsString(0);

        // TODO does not first test cover selfcase?
        if (Factions.i.isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(this.myFaction.getComparisonTag())) {
            this.msg("<b>That tag is already taken");
            return;
        }

        final ArrayList<String> errors = new ArrayList<String>();
        errors.addAll(Factions.validateTag(tag));
        if (errors.size() > 0) {
            this.sendMessage(errors);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!this.canAffordCommand(Conf.econCostTag, "to change the faction tag")) return;

        // trigger the faction rename event (cancellable)
        final FactionRenameEvent renameEvent = new FactionRenameEvent(this.fme, tag);
        Bukkit.getServer().getPluginManager().callEvent(renameEvent);
        if (renameEvent.isCancelled()) return;

        // then make 'em pay (if applicable)
        if (!this.payForCommand(Conf.econCostTag, "to change the faction tag", "for changing the faction tag")) return;

        final String oldtag = this.myFaction.getTag();
        this.myFaction.setTag(tag);

        // Inform
        this.myFaction.msg("%s<i> changed your faction tag to %s", this.fme.describeTo(this.myFaction, true), this.myFaction.getTag(this.myFaction));
        for (final Faction faction : Factions.i.get()) {
            if (faction == this.myFaction) {
                continue;
            }
            faction.msg("<i>The faction %s<i> changed their name to %s.", this.fme.getColorTo(faction) + oldtag, this.myFaction.getTag(faction));
        }

        if (Conf.spoutFactionTagsOverNames) {
            SpoutFeatures.updateTitle(this.myFaction, null);
        }
    }

}
