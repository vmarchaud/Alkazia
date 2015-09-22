package com.massivecraft.factions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.util.MiscUtil;

public class FLocation {

    private String worldName = "world";
    private int x = 0;
    private int z = 0;

    // TODO: It would be wise to ask yourself is there is a better solution to creating loads of new object
    // This object is create many times.

    //----------------------------------------------//
    // Constructors
    //----------------------------------------------//

    public FLocation() {

    }

    public FLocation(final String worldName, final int x, final int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public FLocation(final Location location) {
        this(location.getWorld().getName(), FLocation.blockToChunk(location.getBlockX()), FLocation.blockToChunk(location.getBlockZ()));
    }

    public FLocation(final Player player) {
        this(player.getLocation());
    }

    public FLocation(final FPlayer fplayer) {
        this(fplayer.getPlayer());
    }

    public FLocation(final Block block) {
        this(block.getLocation());
    }

    //----------------------------------------------//
    // Getters and Setters
    //----------------------------------------------//

    public String getWorldName() {
        return this.worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public void setWorldName(final String worldName) {
        this.worldName = worldName;
    }

    public long getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public long getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    public String getCoordString() {
        return "" + this.x + "," + this.z;
    }

    @Override
    public String toString() {
        return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
    }

    //----------------------------------------------//
    // Block/Chunk/Region Value Transformation
    //----------------------------------------------//

    // bit-shifting is used because it's much faster than standard division and multiplication
    public static int blockToChunk(final int blockVal) { // 1 chunk is 16x16 blocks
        return blockVal >> 4; // ">> 4" == "/ 16"
    }

    public static int blockToRegion(final int blockVal) { // 1 region is 512x512 blocks
        return blockVal >> 9; // ">> 9" == "/ 512"
    }

    public static int chunkToRegion(final int chunkVal) { // 1 region is 32x32 chunks
        return chunkVal >> 5; // ">> 5" == "/ 32"
    }

    public static int chunkToBlock(final int chunkVal) {
        return chunkVal << 4; // "<< 4" == "* 16"
    }

    public static int regionToBlock(final int regionVal) {
        return regionVal << 9; // "<< 9" == "* 512"
    }

    public static int regionToChunk(final int regionVal) {
        return regionVal << 5; // "<< 5" == "* 32"
    }

    //----------------------------------------------//
    // Misc Geometry
    //----------------------------------------------//

    public FLocation getRelative(final int dx, final int dz) {
        return new FLocation(this.worldName, this.x + dx, this.z + dz);
    }

    public double getDistanceTo(final FLocation that) {
        final double dx = that.x - this.x;
        final double dz = that.z - this.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    //----------------------------------------------//
    // Some Geometry
    //----------------------------------------------//
    public Set<FLocation> getCircle(final double radius) {
        final Set<FLocation> ret = new LinkedHashSet<FLocation>();
        if (radius <= 0) return ret;

        final int xfrom = (int) Math.floor(this.x - radius);
        final int xto = (int) Math.ceil(this.x + radius);
        final int zfrom = (int) Math.floor(this.z - radius);
        final int zto = (int) Math.ceil(this.z + radius);

        for (int x = xfrom; x <= xto; x++) {
            for (int z = zfrom; z <= zto; z++) {
                final FLocation potential = new FLocation(this.worldName, x, z);
                if (this.getDistanceTo(potential) <= radius) {
                    ret.add(potential);
                }
            }
        }

        return ret;
    }

    public static HashSet<FLocation> getArea(final FLocation from, final FLocation to) {
        final HashSet<FLocation> ret = new HashSet<FLocation>();

        for (final long x : MiscUtil.range(from.getX(), to.getX())) {
            for (final long z : MiscUtil.range(from.getZ(), to.getZ())) {
                ret.add(new FLocation(from.getWorldName(), (int) x, (int) z));
            }
        }

        return ret;
    }

    //----------------------------------------------//
    // Comparison
    //----------------------------------------------//

    @Override
    public int hashCode() {
        // should be fast, with good range and few hash collisions: (x * 512) + z + worldName.hashCode
        return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof FLocation)) return false;

        final FLocation that = (FLocation) obj;
        return this.x == that.x && this.z == that.z && (this.worldName == null ? that.worldName == null : this.worldName.equals(that.worldName));
    }
}