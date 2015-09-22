package com.massivecraft.factions.iface;

public interface EconomyParticipator extends RelationParticipator {
    public String getAccountId();

    public void msg(final String str, final Object... args);
}