package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;

public class CmdJoin extends FCommand {
    public CmdJoin() {
        super();
        this.aliases.add("join");

        this.requiredArgs.add("faction");
        this.optionalArgs.put("player", "you");

        this.permission = Permission.JOIN.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final Faction faction = this.argAsFaction(0);
        if (faction == null) return;

        final FPlayer fplayer = this.argAsBestFPlayerMatch(1, this.fme, false);
        final boolean samePlayer = fplayer == this.fme;

        if (!samePlayer && !Permission.JOIN_OTHERS.has(this.sender, false)) {
            this.msg("<b>You do not have permission to move other players into a faction.");
            return;
        }

        if (faction == fplayer.getFaction()) {
            this.msg("<b>%s %s already a member of %s", fplayer.describeTo(this.fme, true), samePlayer ? "are" : "is", faction.getTag(this.fme));
            return;
        }

        if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= Conf.factionMemberLimit) {
            this.msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(this.fme), Conf.factionMemberLimit, fplayer.describeTo(this.fme, false));
            return;
        }
        
     // AlkaziaFactions
        if (this.myFaction.getFPlayers().size() >= this.myFaction.getLevel().getMaxMembers()) {
            this.fme.sendMessage(ChatColor.RED + "La faction que vous voulez rejoindre est full (" + this.myFaction.getLevel().getMaxMembers() + " membres max pour son level)");
            return;
        }
        // End AlkaziaFactions

        if (fplayer.hasFaction()) {
            this.msg("<b>%s must leave %s current faction first.", fplayer.describeTo(this.fme, true), samePlayer ? "your" : "their");
            return;
        }

        if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0) {
            this.msg("<b>%s cannot join a faction with a negative power level.", fplayer.describeTo(this.fme, true));
            return;
        }

        if (!(faction.getOpen() || faction.isInvited(fplayer) || this.fme.hasAdminMode() || Permission.JOIN_ANY.has(this.sender, false))) {
            this.msg("<i>This faction requires invitation.");
            if (samePlayer) {
                faction.msg("%s<i> tried to join your faction.", fplayer.describeTo(faction, true));
            }
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
        if (samePlayer && !this.canAffordCommand(Conf.econCostJoin, "to join a faction")) return;

        // trigger the join event (cancellable)
        final FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.i.get(this.me), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
        Bukkit.getServer().getPluginManager().callEvent(joinEvent);
        if (joinEvent.isCancelled()) return;

        // then make 'em pay (if applicable)
        if (samePlayer && !this.payForCommand(Conf.econCostJoin, "to join a faction", "for joining a faction")) return;

        if (!samePlayer) {
            fplayer.msg("<i>%s moved you into the faction %s.", this.fme.describeTo(fplayer, true), faction.getTag(fplayer));
        }
        faction.msg("<i>%s joined your faction.", fplayer.describeTo(faction, true));
        this.fme.msg("<i>%s successfully joined %s.", fplayer.describeTo(this.fme, true), faction.getTag(this.fme));

        fplayer.resetFactionData();
        fplayer.setFaction(faction);
        fplayer.setRole(Conf.factionRankDefault); // They have just joined a faction, start them out on the lowest rank (default config).

        faction.deinvite(fplayer);

        if (Conf.logFactionJoin) if (samePlayer) {
            P.p.log("%s joined the faction %s.", fplayer.getName(), faction.getTag());
        } else {
            P.p.log("%s moved the player %s into the faction %s.", this.fme.getName(), fplayer.getName(), faction.getTag());
        }
    }
}
