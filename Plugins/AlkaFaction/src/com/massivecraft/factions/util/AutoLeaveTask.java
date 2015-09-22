package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;

public class AutoLeaveTask implements Runnable {
    private static AutoLeaveProcessTask task;
    double rate;

    public AutoLeaveTask() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    @Override
    public synchronized void run() {
        if (AutoLeaveTask.task != null && !AutoLeaveTask.task.isFinished()) return;

        AutoLeaveTask.task = new AutoLeaveProcessTask();
        AutoLeaveTask.task.runTaskTimer(P.p, 1, 1);

        // maybe setting has been changed? if so, restart this task at new rate
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            P.p.startAutoLeaveTask(true);
        }
    }
}
