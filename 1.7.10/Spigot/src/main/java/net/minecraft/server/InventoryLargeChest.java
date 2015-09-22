package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class InventoryLargeChest implements IInventory {

	private String a;
	public IInventory left; // CraftBukkit - private -> public
	public IInventory right; // CraftBukkit - private -> public

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

	@Override
	public ItemStack[] getContents() {
		ItemStack[] result = new ItemStack[getSize()];
		for (int i = 0; i < result.length; i++) {
			result[i] = getItem(i);
		}
		return result;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		left.onOpen(who);
		right.onOpen(who);
		transaction.add(who);
	}

	@Override
	public void onClose(CraftHumanEntity who) {
		left.onClose(who);
		right.onClose(who);
		transaction.remove(who);
	}

	@Override
	public List<HumanEntity> getViewers() {
		return transaction;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return null; // This method won't be called since CraftInventoryDoubleChest doesn't defer to here
	}

	@Override
	public void setMaxStackSize(int size) {
		left.setMaxStackSize(size);
		right.setMaxStackSize(size);
	}

	// CraftBukkit end

	public InventoryLargeChest(String s, IInventory iinventory, IInventory iinventory1) {
		a = s;
		if (iinventory == null) {
			iinventory = iinventory1;
		}

		if (iinventory1 == null) {
			iinventory1 = iinventory;
		}

		left = iinventory;
		right = iinventory1;
	}

	@Override
	public int getSize() {
		return left.getSize() + right.getSize();
	}

	public boolean a(IInventory iinventory) {
		return left == iinventory || right == iinventory;
	}

	@Override
	public String getInventoryName() {
		return left.k_() ? left.getInventoryName() : right.k_() ? right.getInventoryName() : a;
	}

	@Override
	public boolean k_() {
		return left.k_() || right.k_();
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= left.getSize() ? right.getItem(i - left.getSize()) : left.getItem(i);
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		return i >= left.getSize() ? right.splitStack(i - left.getSize(), j) : left.splitStack(i, j);
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		return i >= left.getSize() ? right.splitWithoutUpdate(i - left.getSize()) : left.splitWithoutUpdate(i);
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		if (i >= left.getSize()) {
			right.setItem(i - left.getSize(), itemstack);
		} else {
			left.setItem(i, itemstack);
		}
	}

	@Override
	public int getMaxStackSize() {
		return Math.min(left.getMaxStackSize(), right.getMaxStackSize()); // CraftBukkit - check both sides
	}

	@Override
	public void update() {
		left.update();
		right.update();
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return left.a(entityhuman) && right.a(entityhuman);
	}

	@Override
	public void startOpen() {
		left.startOpen();
		right.startOpen();
	}

	@Override
	public void closeContainer() {
		left.closeContainer();
		right.closeContainer();
	}

	@Override
	public boolean b(int i, ItemStack itemstack) {
		return true;
	}
}
