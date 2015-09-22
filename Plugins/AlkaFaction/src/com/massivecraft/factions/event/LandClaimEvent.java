package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class LandClaimEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final FLocation location;
    private final Faction faction;
    private final FPlayer fplayer;

    public LandClaimEvent(final FLocation loc, final Faction f, final FPlayer p) {
        this.cancelled = false;
        this.location = loc;
        this.faction = f;
        this.fplayer = p;
    }

    @Override
    public HandlerList getHandlers() {
        return LandClaimEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return LandClaimEvent.handlers;
    }

    public FLocation getLocation() {
        return this.location;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public String getFactionId() {
        return this.faction.getId();
    }

    public String getFactionTag() {
        return this.faction.getTag();
    }

    public FPlayer getFPlayer() {
        return this.fplayer;
    }

    public Player getPlayer() {
        return this.fplayer.getPlayer();
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
