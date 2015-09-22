package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class LandUnclaimAllEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Faction faction;
    private final FPlayer fplayer;

    public LandUnclaimAllEvent(final Faction f, final FPlayer p) {
        this.faction = f;
        this.fplayer = p;
    }

    @Override
    public HandlerList getHandlers() {
        return LandUnclaimAllEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return LandUnclaimAllEvent.handlers;
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
}
