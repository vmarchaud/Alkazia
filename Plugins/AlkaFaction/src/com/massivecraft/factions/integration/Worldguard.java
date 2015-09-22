package com.massivecraft.factions.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.P;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/*
 *  Worldguard Region Checking
 *  Author: Spathizilla
 */

public class Worldguard {
    private static WorldGuardPlugin wg;
    private static boolean enabled = false;

    public static void init(final Plugin plugin) {
        final Plugin wgplug = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgplug == null || !(wgplug instanceof WorldGuardPlugin)) {
            Worldguard.enabled = false;
            Worldguard.wg = null;
            P.p.log("Could not hook to WorldGuard. WorldGuard checks are disabled.");
        } else {
            Worldguard.wg = (WorldGuardPlugin) wgplug;
            Worldguard.enabled = true;
            P.p.log("Successfully hooked to WorldGuard.");
        }
    }

    public static boolean isEnabled() {
        return Worldguard.enabled;
    }

    // PVP Flag check 
    // Returns:
    //   True: PVP is allowed
    //   False: PVP is disallowed
    public static boolean isPVP(final Player player) {
        if (!Worldguard.enabled) // No WG hooks so we'll always bypass this check.
        return true;

        final Location loc = player.getLocation();
        final World world = loc.getWorld();
        final Vector pt = BukkitUtil.toVector(loc);

        final RegionManager regionManager = Worldguard.wg.getRegionManager(world);
        final ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
        return set.allows(DefaultFlag.PVP);
    }

    // Check for Regions in chunk the chunk
    // Returns:
    //   True: Regions found within chunk
    //   False: No regions found within chunk
    public static boolean checkForRegionsInChunk(final Location loc) {
        if (!Worldguard.enabled) // No WG hooks so we'll always bypass this check.
        return false;

        final World world = loc.getWorld();
        final Chunk chunk = world.getChunkAt(loc);
        final int minChunkX = chunk.getX() << 4;
        final int minChunkZ = chunk.getZ() << 4;
        final int maxChunkX = minChunkX + 15;
        final int maxChunkZ = minChunkZ + 15;

        final int worldHeight = world.getMaxHeight(); // Allow for heights other than default

        final BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
        final BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);

        final RegionManager regionManager = Worldguard.wg.getRegionManager(world);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);
        final Map<String, ProtectedRegion> allregions = regionManager.getRegions();
        List<ProtectedRegion> allregionslist = new ArrayList<ProtectedRegion>(allregions.values());
        List<ProtectedRegion> overlaps;
        boolean foundregions = false;

        try {
            overlaps = region.getIntersectingRegions(allregionslist);
            if (overlaps == null || overlaps.isEmpty()) {
                foundregions = false;
            } else {
                foundregions = true;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        region = null;
        allregionslist = null;
        overlaps = null;

        return foundregions;
    }
}