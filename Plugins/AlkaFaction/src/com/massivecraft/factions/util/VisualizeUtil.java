package com.massivecraft.factions.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

// TODO: Only send blocks in visual range
// TODO: Only send blocks that where changed when clearing?
// TODO: Create packed queue to avoid freezes. 

public class VisualizeUtil {
    protected static Map<String, Set<Location>> playerLocations = new HashMap<String, Set<Location>>();

    public static Set<Location> getPlayerLocations(final Player player) {
        return VisualizeUtil.getPlayerLocations(player.getName());
    }

    public static Set<Location> getPlayerLocations(final String playerName) {
        Set<Location> ret = VisualizeUtil.playerLocations.get(playerName);
        if (ret == null) {
            ret = new HashSet<Location>();
            VisualizeUtil.playerLocations.put(playerName, ret);
        }
        return ret;
    }

    // -------------------------------------------- //
    // SINGLE
    // -------------------------------------------- //

    public static void addLocation(final Player player, final Location location, final int typeId, final byte data) {
        VisualizeUtil.getPlayerLocations(player).add(location);
        player.sendBlockChange(location, typeId, data);
    }

    public static void addLocation(final Player player, final Location location, final int typeId) {
        VisualizeUtil.getPlayerLocations(player).add(location);
        player.sendBlockChange(location, typeId, (byte) 0);
    }

    // -------------------------------------------- //
    // MANY
    // -------------------------------------------- //

    public static void addLocations(final Player player, final Map<Location, Integer> locationMaterialIds) {
        final Set<Location> ploc = VisualizeUtil.getPlayerLocations(player);
        for (final Entry<Location, Integer> entry : locationMaterialIds.entrySet()) {
            ploc.add(entry.getKey());
            player.sendBlockChange(entry.getKey(), entry.getValue(), (byte) 0);
        }
    }

    public static void addLocations(final Player player, final Collection<Location> locations, final int typeId) {
        final Set<Location> ploc = VisualizeUtil.getPlayerLocations(player);
        for (final Location location : locations) {
            ploc.add(location);
            player.sendBlockChange(location, typeId, (byte) 0);
        }
    }

    public static void addBlocks(final Player player, final Collection<Block> blocks, final int typeId) {
        final Set<Location> ploc = VisualizeUtil.getPlayerLocations(player);
        for (final Block block : blocks) {
            final Location location = block.getLocation();
            ploc.add(location);
            player.sendBlockChange(location, typeId, (byte) 0);
        }
    }

    // -------------------------------------------- //
    // CLEAR
    // -------------------------------------------- //

    public static void clear(final Player player) {
        final Set<Location> locations = VisualizeUtil.getPlayerLocations(player);
        if (locations == null) return;
        for (final Location location : locations) {
            final Block block = location.getWorld().getBlockAt(location);
            player.sendBlockChange(location, block.getTypeId(), block.getData());
        }
        locations.clear();
    }

}
