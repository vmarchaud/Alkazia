package me.rellynn.plugins.meteor;

import org.bukkit.Material;

/**
 * ChunkBlock.java
 *
 * @author Rellynn
 */
public class ChunkBlock {

    private int x;
    private int y;
    private int z;
    private Material material;
    private byte data;

    public ChunkBlock(final int x, final int y, final int z, final Material material, final byte data) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.material = material;
	this.data = data;
    }

    public byte getData() {
	return this.data;
    }

    public Material getMaterial() {
	return this.material;
    }

    public int getX() {
	return this.x;
    }

    public int getY() {
	return this.y;
    }

    public int getZ() {
	return this.z;
    }

    public void setData(final byte data) {
	this.data = data;
    }

    public void setMaterial(final Material material) {
	this.material = material;
    }

    public void setX(final int x) {
	this.x = x;
    }

    public void setY(final int y) {
	this.y = y;
    }

    public void setZ(final int z) {
	this.z = z;
    }
}
