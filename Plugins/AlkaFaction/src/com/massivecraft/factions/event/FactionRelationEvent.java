package com.massivecraft.factions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;

public class FactionRelationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Faction fsender;
    private final Faction ftarget;
    private final Rel foldrel;
    private final Rel frel;

    public FactionRelationEvent(final Faction sender, final Faction target, final Rel oldrel, final Rel rel) {
        this.fsender = sender;
        this.ftarget = target;
        this.foldrel = oldrel;
        this.frel = rel;
    }

    @Override
    public HandlerList getHandlers() {
        return FactionRelationEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return FactionRelationEvent.handlers;
    }

    public Rel getOldRelation() {
        return this.foldrel;
    }

    public Rel getRelation() {
        return this.frel;
    }

    public Faction getFaction() {
        return this.fsender;
    }

    public Faction getTargetFaction() {
        return this.ftarget;
    }
}
