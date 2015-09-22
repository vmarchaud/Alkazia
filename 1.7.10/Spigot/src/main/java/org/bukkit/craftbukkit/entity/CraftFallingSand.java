package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFallingBlock;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingSand;

public class CraftFallingSand extends CraftEntity implements FallingSand {

	public CraftFallingSand(CraftServer server, EntityFallingBlock entity) {
		super(server, entity);
	}

	@Override
	public EntityFallingBlock getHandle() {
		return (EntityFallingBlock) entity;
	}

	@Override
	public String toString() {
		return "CraftFallingSand";
	}

	@Override
	public EntityType getType() {
		return EntityType.FALLING_BLOCK;
	}

	@Override
	public Material getMaterial() {
		return Material.getMaterial(getBlockId());
	}

	@Override
	public int getBlockId() {
		return CraftMagicNumbers.getId(getHandle().id);
	}

	@Override
	public byte getBlockData() {
		return (byte) getHandle().data;
	}

	@Override
	public boolean getDropItem() {
		return getHandle().dropItem;
	}

	@Override
	public void setDropItem(boolean drop) {
		getHandle().dropItem = drop;
	}
}
