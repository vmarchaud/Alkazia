package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask implements Runnable {
    static private boolean running = false;

    MPlugin p;

    public SaveTask(final MPlugin p) {
        this.p = p;
    }

    @Override
    public void run() {
        if (!this.p.getAutoSave() || SaveTask.running) return;
        SaveTask.running = true;
        this.p.preAutoSave();
        EM.saveAllToDisc();
        this.p.postAutoSave();
        SaveTask.running = false;
    }
}
