package com.massivecraft.factions.integration.herochat;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Rel;

public class FactionChannel extends FactionsChannelAbstract {
    public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER);

    @Override
    public Set<Rel> getTargetRelations() {
        return FactionChannel.targetRelations;
    }

    @Override
    public String getName() {
        return Conf.herochatFactionName;
    }

    @Override
    public String getNick() {
        return Conf.herochatFactionNick;
    }

    @Override
    public void setNick(final String nick) {
        Conf.herochatFactionNick = nick;
    }

    @Override
    public String getFormat() {
        return Conf.herochatFactionFormat;
    }

    @Override
    public void setFormat(final String format) {
        Conf.herochatFactionFormat = format;
    }

    @Override
    public ChatColor getColor() {
        return Conf.herochatFactionColor;
    }

    @Override
    public void setColor(final ChatColor color) {
        Conf.herochatFactionColor = color;
    }

    @Override
    public int getDistance() {
        return Conf.herochatFactionDistance;
    }

    @Override
    public void setDistance(final int distance) {
        Conf.herochatFactionDistance = distance;
    }

    @Override
    public void addWorld(final String world) {
        Conf.herochatFactionWorlds.add(world);
    }

    @Override
    public Set<String> getWorlds() {
        return new HashSet<String>(Conf.herochatFactionWorlds);
    }

    @Override
    public void setWorlds(final Set<String> worlds) {
        Conf.herochatFactionWorlds = worlds;
    }

    @Override
    public boolean isShortcutAllowed() {
        return Conf.herochatFactionIsShortcutAllowed;
    }

    @Override
    public void setShortcutAllowed(final boolean shortcutAllowed) {
        Conf.herochatFactionIsShortcutAllowed = shortcutAllowed;
    }

    @Override
    public boolean isCrossWorld() {
        return Conf.herochatFactionCrossWorld;
    }

    @Override
    public void setCrossWorld(final boolean crossWorld) {
        Conf.herochatFactionCrossWorld = crossWorld;
    }

    @Override
    public boolean isMuted() {
        return Conf.herochatFactionMuted;
    }

    @Override
    public void setMuted(final boolean value) {
        Conf.herochatFactionMuted = value;
    }
}
