package com.massivecraft.factions.zcore.util;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;

// http://mc.kev009.com/Protocol
// -----------------------------
// Smoke Directions 
// -----------------------------
// Direction ID    Direction
//            0    South - East
//            1    South
//            2    South - West
//            3    East
//            4    (Up or middle ?)
//            5    West
//            6    North - East
//            7    North
//            8    North - West
//-----------------------------

public class SmokeUtil {
    public static Random random = new Random();

    // -------------------------------------------- //
    // Spawn once
    // -------------------------------------------- //

    // Single ========
    public static void spawnSingle(final Location location, final int direction) {
        if (location == null) return;
        location.getWorld().playEffect(location.clone(), Effect.SMOKE, direction);
    }

    public static void spawnSingle(final Location location) {
        SmokeUtil.spawnSingle(location, 4);
    }

    public static void spawnSingleRandom(final Location location) {
        SmokeUtil.spawnSingle(location, SmokeUtil.random.nextInt(9));
    }

    // Simple Cloud ========
    public static void spawnCloudSimple(final Location location) {
        for (int i = 0; i <= 8; i++) {
            SmokeUtil.spawnSingle(location, i);
        }
    }

    public static void spawnCloudSimple(final Collection<Location> locations) {
        for (final Location location : locations) {
            SmokeUtil.spawnCloudSimple(location);
        }
    }

    // Random Cloud ========
    public static void spawnCloudRandom(final Location location, final float thickness) {
        final int singles = (int) Math.floor(thickness * 9);
        for (int i = 0; i < singles; i++) {
            SmokeUtil.spawnSingleRandom(location.clone());
        }
    }

    public static void spawnCloudRandom(final Collection<Location> locations, final float thickness) {
        for (final Location location : locations) {
            SmokeUtil.spawnCloudRandom(location, thickness);
        }
    }

    // -------------------------------------------- //
    // Attach continuous effects to or locations
    // -------------------------------------------- //

    // TODO

}
