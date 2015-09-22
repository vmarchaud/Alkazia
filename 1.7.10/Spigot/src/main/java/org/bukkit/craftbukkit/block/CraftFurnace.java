package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityFurnace;

import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryFurnace;
import org.bukkit.inventory.FurnaceInventory;

public class CraftFurnace extends CraftBlockState implements Furnace {
	private final TileEntityFurnace furnace;

	public CraftFurnace(final Block block) {
		super(block);

		furnace = (TileEntityFurnace) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
	}

	@Override
	public FurnaceInventory getInventory() {
		return new CraftInventoryFurnace(furnace);
	}

	@Override
	public boolean update(boolean force, boolean applyPhysics) {
		boolean result = super.update(force, applyPhysics);

		if (result) {
			furnace.update();
		}

		return result;
	}

	@Override
	public short getBurnTime() {
		return (short) furnace.burnTime;
	}

	@Override
	public void setBurnTime(short burnTime) {
		furnace.burnTime = burnTime;
	}

	@Override
	public short getCookTime() {
		return (short) furnace.cookTime;
	}

	@Override
	public void setCookTime(short cookTime) {
		furnace.cookTime = cookTime;
	}
}
