package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.TerritoryAccess;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdAccess extends FCommand {
    public CmdAccess() {
        super();
        this.aliases.add("access");

        this.optionalArgs.put("view|p|f|player|faction", "view");
        this.optionalArgs.put("name", "you");

        this.setHelpShort("view or grant access for the claimed territory you are in");

        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        String type = this.argAsString(0);
        type = type == null ? "" : type.toLowerCase();
        final FLocation loc = new FLocation(this.me.getLocation());

        final TerritoryAccess territory = Board.getTerritoryAccessAt(loc);
        final Faction locFaction = territory.getHostFaction();
        final boolean accessAny = Permission.ACCESS_ANY.has(this.sender, false);

        if (type.isEmpty() || type.equals("view")) {
            if (!accessAny && !Permission.ACCESS_VIEW.has(this.sender, true)) return;
            if (!accessAny && !territory.doesHostFactionMatch(this.fme)) {
                this.msg("<b>This territory isn't controlled by your faction, so you can't view the access list.");
                return;
            }
            this.showAccessList(territory, locFaction);
            return;
        }

        if (!accessAny && !Permission.ACCESS.has(this.sender, true)) return;
        if (!accessAny && !FPerm.ACCESS.has(this.fme, locFaction, true)) return;

        boolean doPlayer = true;
        if (type.equals("f") || type.equals("faction")) {
            doPlayer = false;
        } else if (!type.equals("p") && !type.equals("player")) {
            this.msg("<b>You must specify \"p\" or \"player\" to indicate a player or \"f\" or \"faction\" to indicate a faction.");
            this.msg("<b>ex. /f access p SomePlayer  -or-  /f access f SomeFaction");
            this.msg("<b>Alternately, you can use the command with nothing (or \"view\") specified to simply view the access list.");
            return;
        }

        String target = "";
        boolean added;

        if (doPlayer) {
            final FPlayer targetPlayer = this.argAsBestFPlayerMatch(1, this.fme);
            if (targetPlayer == null) return;
            added = territory.toggleFPlayer(targetPlayer);
            target = "Player \"" + targetPlayer.getName() + "\"";
        } else {
            final Faction targetFaction = this.argAsFaction(1, this.myFaction);
            if (targetFaction == null) return;
            added = territory.toggleFaction(targetFaction);
            target = "Faction \"" + targetFaction.getTag() + "\"";
        }

        this.msg("<i>%s has been %s<i> the access list for this territory.", target, TextUtil.parseColor(added ? "<lime>added to" : "<rose>removed from"));
        SpoutFeatures.updateAccessInfoLoc(loc);
        this.showAccessList(territory, locFaction);
    }

    private void showAccessList(final TerritoryAccess territory, final Faction locFaction) {
        this.msg("<i>Host faction %s has %s<i> in this territory.", locFaction.getTag(), TextUtil.parseColor(territory.isHostFactionAllowed() ? "<lime>normal access" : "<rose>restricted access"));

        final String players = territory.fplayerList();
        final String factions = territory.factionList();

        if (factions.isEmpty()) {
            this.msg("No factions have been explicitly granted access.");
        } else {
            this.msg("Factions with explicit access: " + factions);
        }

        if (players.isEmpty()) {
            this.msg("No players have been explicitly granted access.");
        } else {
            this.msg("Players with explicit access: " + players);
        }
    }
}
