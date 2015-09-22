package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityEnderman;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

public class CraftEnderman extends CraftMonster implements Enderman {
	public CraftEnderman(CraftServer server, EntityEnderman entity) {
		super(server, entity);
	}

	@Override
	public MaterialData getCarriedMaterial() {
		return CraftMagicNumbers.getMaterial(getHandle().getCarried()).getNewData((byte) getHandle().getCarriedData());
	}

	@Override
	public void setCarriedMaterial(MaterialData data) {
		getHandle().setCarried(CraftMagicNumbers.getBlock(data.getItemTypeId()));
		getHandle().setCarriedData(data.getData());
	}

	@Override
	public EntityEnderman getHandle() {
		return (EntityEnderman) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderman";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDERMAN;
	}
}
