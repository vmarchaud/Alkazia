package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.RelationUtil;

public class CmdLeader extends FCommand {
    public CmdLeader() {
        super();
        this.aliases.add("leader");

        this.requiredArgs.add("player");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.LEADER.node;
        this.disableOnLock = true;

        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final FPlayer newLeader = this.argAsBestFPlayerMatch(0);
        if (newLeader == null) return;

        final Faction targetFaction = this.argAsFaction(1, this.myFaction);
        if (targetFaction == null) return;

        final FPlayer targetFactionCurrentLeader = targetFaction.getFPlayerLeader();

        // We now have fplayer and the target faction
        if (this.senderIsConsole || this.fme.hasAdminMode() || Permission.LEADER_ANY.has(this.sender, false)) {
            // Do whatever you wish
        } else {
            // Follow the standard rules
            if (this.fme.getRole() != Rel.LEADER || targetFaction != this.myFaction) {
                this.sender.sendMessage(this.p.txt.parse("<b>You must be leader of the faction to %s.", this.getHelpShort()));
                return;
            }

            if (newLeader.getFaction() != this.myFaction) {
                this.msg("%s<i> is not a member in the faction.", newLeader.describeTo(this.fme, true));
                return;
            }

            if (newLeader == this.fme) {
                this.msg("<b>The target player musn't be yourself.");
                return;
            }
        }

        // only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
        if (newLeader.getFaction() != targetFaction) {
            final FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.i.get(this.me), targetFaction, FPlayerJoinEvent.PlayerJoinReason.LEADER);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
        }

        // if target player is currently leader, demote and replace him
        if (targetFactionCurrentLeader == newLeader) {
            targetFaction.promoteNewLeader();
            this.msg("<i>You have demoted %s<i> from the position of faction leader.", newLeader.describeTo(this.fme, true));
            newLeader.msg("<i>You have been demoted from the position of faction leader by %s<i>.", this.senderIsConsole ? "a server admin" : this.fme.describeTo(newLeader, true));
            return;
        }

        // Perform the switching
        if (targetFactionCurrentLeader != null) {
            targetFactionCurrentLeader.setRole(Rel.OFFICER);
        }
        newLeader.setFaction(targetFaction);
        newLeader.setRole(Rel.LEADER);
        this.msg("<i>You have promoted %s<i> to the position of faction leader.", newLeader.describeTo(this.fme, true));

        // Inform all players
        for (final FPlayer fplayer : FPlayers.i.getOnline()) {
            fplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", this.senderIsConsole ? "A server admin" : RelationUtil.describeThatToMe(this.fme, fplayer, true), newLeader.describeTo(fplayer), targetFaction.describeTo(fplayer));
        }
    }
}
