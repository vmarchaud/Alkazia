package com.massivecraft.factions.util;

import java.util.ArrayList;
import java.util.ListIterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Rel;

public class AutoLeaveProcessTask extends BukkitRunnable {
    private transient boolean readyToGo = false;
    private transient boolean finished = false;
    private transient ArrayList<FPlayer> fplayers;
    private transient ListIterator<FPlayer> iterator;
    private transient double toleranceMillis;

    public AutoLeaveProcessTask() {
        this.fplayers = new ArrayList<FPlayer>(FPlayers.i.get());
        this.iterator = this.fplayers.listIterator();
        this.toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;
        this.readyToGo = true;
        this.finished = false;
    }

    @Override
    public void run() {
        if (Conf.autoLeaveAfterDaysOfInactivity <= 0.0 || Conf.autoLeaveRoutineMaxMillisecondsPerTick <= 0.0) {
            this.stop();
            return;
        }

        if (!this.readyToGo) return;
        // this is set so it only does one iteration at a time, no matter how frequently the timer fires
        this.readyToGo = false;
        // and this is tracked to keep one iteration from dragging on too long and possibly choking the system if there are a very large number of players to go through
        final long loopStartTime = System.currentTimeMillis();

        while (this.iterator.hasNext()) {
            final long now = System.currentTimeMillis();

            // if this iteration has been running for maximum time, stop to take a breather until next tick
            if (now > loopStartTime + Conf.autoLeaveRoutineMaxMillisecondsPerTick) {
                this.readyToGo = true;
                return;
            }

            final FPlayer fplayer = this.iterator.next();
            if (fplayer.isOffline() && now - fplayer.getLastLoginTime() > this.toleranceMillis) {
                if (Conf.logFactionLeave || Conf.logFactionKick) {
                    P.p.log("Player " + fplayer.getName() + " was auto-removed due to inactivity.");
                }

                // if player is faction leader, sort out the faction since he's going away
                if (fplayer.getRole() == Rel.LEADER) {
                    final Faction faction = fplayer.getFaction();
                    if (faction != null) {
                        fplayer.getFaction().promoteNewLeader();
                    }
                }

                fplayer.leave(false);
                this.iterator.remove(); // go ahead and remove this list's link to the FPlayer object
                fplayer.detach();
            }
        }

        // looks like we've finished
        this.stop();
    }

    // we're done, shut down
    public void stop() {
        this.readyToGo = false;
        this.finished = true;

        this.cancel();
    }

    public boolean isFinished() {
        return this.finished;
    }
}
