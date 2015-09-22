package com.massivecraft.factions.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/*
 * This class provides a lazy-load Location, so that World doesn't need to be initialized
 * yet when an object of this class is created, only when the Location is first accessed.
 */

public class LazyLocation {
    private Location location = null;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LazyLocation(final Location loc) {
        this.setLocation(loc);
    }

    public LazyLocation(final String worldName, final double x, final double y, final double z) {
        this(worldName, x, y, z, 0, 0);
    }

    public LazyLocation(final String worldName, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    // This returns the actual Location
    public final Location getLocation() {
        // make sure Location is initialized before returning it
        this.initLocation();
        return this.location;
    }

    // change the Location
    public final void setLocation(final Location loc) {
        this.location = loc;
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    // This initializes the Location
    private void initLocation() {
        // if location is already initialized, simply return
        if (this.location != null) return;

        // get World; hopefully it's initialized at this point
        final World world = Bukkit.getWorld(this.worldName);
        if (world == null) return;

        // store the Location for future calls, and pass it on
        this.location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public final String getWorldName() {
        return this.worldName;
    }

    public final double getX() {
        return this.x;
    }

    public final double getY() {
        return this.y;
    }

    public final double getZ() {
        return this.z;
    }

    public final double getPitch() {
        return this.pitch;
    }

    public final double getYaw() {
        return this.yaw;
    }
}
