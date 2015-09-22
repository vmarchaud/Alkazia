package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class InventoryCraftResult implements IInventory {

	private ItemStack[] items = new ItemStack[1];

	// CraftBukkit start
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return items;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return null; // Result slots don't get an owner
	}

	// Don't need a transaction; the InventoryCrafting keeps track of it for us
	@Override
	public void onOpen(CraftHumanEntity who) {
	}

	@Override
	public void onClose(CraftHumanEntity who) {
	}

	@Override
	public java.util.List<HumanEntity> getViewers() {
		return new java.util.ArrayList<HumanEntity>();
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	public InventoryCraftResult() {
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int i) {
		return items[0];
	}

	@Override
	public String getInventoryName() {
		return "Result";
	}

	@Override
	public boolean k_() {
		return false;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (items[0] != null) {
			ItemStack itemstack = items[0];

			items[0] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (items[0] != null) {
			ItemStack itemstack = items[0];

			items[0] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		items[0] = itemstack;
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public void update() {
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return true;
	}

	@Override
	public void startOpen() {
	}

	@Override
	public void closeContainer() {
	}

	@Override
	public boolean b(int i, ItemStack itemstack) {
		return true;
	}
}
