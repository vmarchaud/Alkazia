package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;

public class FactionCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final String factionTag;
    private final Player sender;
    private boolean cancelled;

    public FactionCreateEvent(final Player sender, final String tag) {
        this.factionTag = tag;
        this.sender = sender;
        this.cancelled = false;
    }

    public FPlayer getFPlayer() {
        return FPlayers.i.get(this.sender);
    }

    public String getFactionId() {
        return Factions.i.getNextId();
    }

    public String getFactionTag() {
        return this.factionTag;
    }

    @Override
    public HandlerList getHandlers() {
        return FactionCreateEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return FactionCreateEvent.handlers;
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