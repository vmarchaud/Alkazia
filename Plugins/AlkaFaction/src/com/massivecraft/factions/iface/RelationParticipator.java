package com.massivecraft.factions.iface;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Rel;

public interface RelationParticipator {
    public String describeTo(final RelationParticipator observer);

    public String describeTo(final RelationParticipator observer, final boolean ucfirst);

    public Rel getRelationTo(final RelationParticipator observer);

    public Rel getRelationTo(final RelationParticipator observer, final boolean ignorePeaceful);

    public ChatColor getColorTo(final RelationParticipator observer);
}
