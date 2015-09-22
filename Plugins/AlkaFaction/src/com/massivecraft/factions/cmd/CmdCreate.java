package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Levels;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.holder.FactionHolder;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdCreate extends FCommand {
    public CmdCreate() {
        super();
        this.aliases.add("create");

        this.requiredArgs.add("faction tag");
        //this.optionalArgs.put("", "");

        this.permission = Permission.CREATE.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final String tag = this.argAsString(0);

        if (this.fme.hasFaction()) {
            this.msg("<b>You must leave your current faction first.");
            return;
        }

        if (Factions.i.isTagTaken(tag)) {
            this.msg("<b>That tag is already in use.");
            return;
        }

        final ArrayList<String> tagValidationErrors = Factions.validateTag(tag);
        if (tagValidationErrors.size() > 0) {
            this.sendMessage(tagValidationErrors);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (!this.canAffordCommand(Conf.econCostCreate, "to create a new faction")) return;

        // trigger the faction creation event (cancellable)
        final FactionCreateEvent createEvent = new FactionCreateEvent(this.me, tag);
        Bukkit.getServer().getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) return;

        // then make 'em pay (if applicable)
        if (!this.payForCommand(Conf.econCostCreate, "to create a new faction", "for creating a new faction")) return;

        final Faction faction = Factions.i.create();

        // TODO: Why would this even happen??? Auto increment clash??
        if (faction == null) {
            this.msg("<b>There was an internal error while trying to create your faction. Please try again.");
            return;
        }

        // AlkaziaFactions
        faction.setInventory(Bukkit.createInventory(new FactionHolder(), 54, tag));
        faction.setLevel(Levels.i.get("0"));
        // End AlkaziaFactions

        // finish setting up the Faction
        faction.setTag(tag);

        // trigger the faction join event for the creator
        final FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.i.get(this.me), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        // join event cannot be cancelled or you'll have an empty faction

        // finish setting up the FPlayer
        this.fme.setRole(Rel.LEADER);
        this.fme.setFaction(faction);

        for (final FPlayer follower : FPlayers.i.getOnline()) {
            follower.msg("%s<i> created a new faction %s", this.fme.describeTo(follower, true), faction.getTag(follower));
        }

        this.msg("<i>You should now: %s", this.p.cmdBase.cmdDescription.getUseageTemplate());

        if (Conf.logFactionCreate) {
            P.p.log(this.fme.getName() + " created a new faction: " + tag);
        }
    }

}
