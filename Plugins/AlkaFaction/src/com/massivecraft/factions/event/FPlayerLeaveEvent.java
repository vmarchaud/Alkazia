package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerLeaveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerLeaveReason reason;
    FPlayer FPlayer;
    Faction Faction;
    boolean cancelled = false;

    public enum PlayerLeaveReason {
        KICKED, DISBAND, RESET, JOINOTHER, LEAVE
    }

    public FPlayerLeaveEvent(final FPlayer p, final Faction f, final PlayerLeaveReason r) {
        this.FPlayer = p;
        this.Faction = f;
        this.reason = r;
    }

    @Override
    public HandlerList getHandlers() {
        return FPlayerLeaveEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return FPlayerLeaveEvent.handlers;
    }

    public PlayerLeaveReason getReason() {
        return this.reason;
    }

    public FPlayer getFPlayer() {
        return this.FPlayer;
    }

    public Faction getFaction() {
        return this.Faction;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean c) {
        if (this.reason == PlayerLeaveReason.DISBAND || this.reason == PlayerLeaveReason.RESET) {
            this.cancelled = false;
            return;
        }
        this.cancelled = c;
    }
}