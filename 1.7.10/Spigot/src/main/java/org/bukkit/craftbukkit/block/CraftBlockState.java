package org.bukkit.craftbukkit.block;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CraftBlockState implements BlockState {
	private final CraftWorld world;
	private final CraftChunk chunk;
	private final int x;
	private final int y;
	private final int z;
	protected int type;
	protected MaterialData data;
	protected int flag;
	protected final byte light;

	public CraftBlockState(final Block block) {
		world = (CraftWorld) block.getWorld();
		x = block.getX();
		y = block.getY();
		z = block.getZ();
		type = block.getTypeId();
		light = block.getLightLevel();
		chunk = (CraftChunk) block.getChunk();
		flag = 3;

		createData(block.getData());
	}

	public CraftBlockState(final Block block, int flag) {
		this(block);
		this.flag = flag;
	}

	public static CraftBlockState getBlockState(net.minecraft.server.World world, int x, int y, int z) {
		return new CraftBlockState(world.getWorld().getBlockAt(x, y, z));
	}

	public static CraftBlockState getBlockState(net.minecraft.server.World world, int x, int y, int z, int flag) {
		return new CraftBlockState(world.getWorld().getBlockAt(x, y, z), flag);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public Chunk getChunk() {
		return chunk;
	}

	@Override
	public void setData(final MaterialData data) {
		Material mat = getType();

		if (mat == null || mat.getData() == null) {
			this.data = data;
		} else {
			if (data.getClass() == mat.getData() || data.getClass() == MaterialData.class) {
				this.data = data;
			} else
				throw new IllegalArgumentException("Provided data is not of type " + mat.getData().getName() + ", found " + data.getClass().getName());
		}
	}

	@Override
	public MaterialData getData() {
		return data;
	}

	@Override
	public void setType(final Material type) {
		setTypeId(type.getId());
	}

	@Override
	public boolean setTypeId(final int type) {
		if (this.type != type) {
			this.type = type;

			createData((byte) 0);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.getMaterial(getTypeId());
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	@Override
	public int getTypeId() {
		return type;
	}

	@Override
	public byte getLightLevel() {
		return light;
	}

	@Override
	public Block getBlock() {
		return world.getBlockAt(x, y, z);
	}

	@Override
	public boolean update() {
		return update(false);
	}

	@Override
	public boolean update(boolean force) {
		return update(force, true);
	}

	@Override
	public boolean update(boolean force, boolean applyPhysics) {
		Block block = getBlock();

		if (block.getType() != getType()) {
			if (force) {
				block.setTypeId(getTypeId(), applyPhysics);
			} else
				return false;
		}

		block.setData(getRawData(), applyPhysics);
		world.getHandle().notify(x, y, z);

		return true;
	}

	private void createData(final byte data) {
		Material mat = getType();
		if (mat == null || mat.getData() == null) {
			this.data = new MaterialData(type, data);
		} else {
			this.data = mat.getNewData(data);
		}
	}

	@Override
	public byte getRawData() {
		return data.getData();
	}

	@Override
	public Location getLocation() {
		return new Location(world, x, y, z);
	}

	@Override
	public Location getLocation(Location loc) {
		if (loc != null) {
			loc.setWorld(world);
			loc.setX(x);
			loc.setY(y);
			loc.setZ(z);
			loc.setYaw(0);
			loc.setPitch(0);
		}

		return loc;
	}

	@Override
	public void setRawData(byte data) {
		this.data.setData(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CraftBlockState other = (CraftBlockState) obj;
		if (world != other.world && (world == null || !world.equals(other.world)))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		if (type != other.type)
			return false;
		if (data != other.data && (data == null || !data.equals(other.data)))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + (world != null ? world.hashCode() : 0);
		hash = 73 * hash + x;
		hash = 73 * hash + y;
		hash = 73 * hash + z;
		hash = 73 * hash + type;
		hash = 73 * hash + (data != null ? data.hashCode() : 0);
		return hash;
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		chunk.getCraftWorld().getBlockMetadata().setMetadata(getBlock(), metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return chunk.getCraftWorld().getBlockMetadata().getMetadata(getBlock(), metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey) {
		return chunk.getCraftWorld().getBlockMetadata().hasMetadata(getBlock(), metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		chunk.getCraftWorld().getBlockMetadata().removeMetadata(getBlock(), metadataKey, owningPlugin);
	}
}