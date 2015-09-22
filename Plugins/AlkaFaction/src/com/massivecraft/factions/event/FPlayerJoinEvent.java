package com.massivecraft.factions.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    FPlayer fplayer;
    Faction faction;
    PlayerJoinReason reason;
    boolean cancelled = false;

    public enum PlayerJoinReason {
        CREATE, LEADER, COMMAND
    }

    public FPlayerJoinEvent(final FPlayer fp, final Faction f, final PlayerJoinReason r) {
        this.fplayer = fp;
        this.faction = f;
        this.reason = r;
    }

    public FPlayer getFPlayer() {
        return this.fplayer;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public PlayerJoinReason getReason() {
        return this.reason;
    }

    @Override
    public HandlerList getHandlers() {
        return FPlayerJoinEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return FPlayerJoinEvent.handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean c) {
        this.cancelled = c;
    }
}