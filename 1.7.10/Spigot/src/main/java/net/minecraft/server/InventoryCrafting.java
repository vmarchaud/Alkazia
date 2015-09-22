package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

// CraftBukkit end

public class InventoryCrafting implements IInventory {

	private ItemStack[] items;
	private int b;
	private Container c;

	// CraftBukkit start - add fields
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	public IRecipe currentRecipe;
	public IInventory resultInventory;
	private EntityHuman owner;
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return items;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		transaction.add(who);
	}

	public InventoryType getInvType() {
		return items.length == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
	}

	@Override
	public void onClose(CraftHumanEntity who) {
		transaction.remove(who);
	}

	@Override
	public List<HumanEntity> getViewers() {
		return transaction;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return owner.getBukkitEntity();
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
		resultInventory.setMaxStackSize(size);
	}

	public InventoryCrafting(Container container, int i, int j, EntityHuman player) {
		this(container, i, j);
		owner = player;
	}

	// CraftBukkit end

	public InventoryCrafting(Container container, int i, int j) {
		int k = i * j;

		items = new ItemStack[k];
		c = container;
		b = i;
	}

	@Override
	public int getSize() {
		return items.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= getSize() ? null : items[i];
	}

	public ItemStack b(int i, int j) {
		if (i >= 0 && i < b) {
			int k = i + j * b;

			return getItem(k);
		} else
			return null;
	}

	@Override
	public String getInventoryName() {
		return "container.crafting";
	}

	@Override
	public boolean k_() {
		return false;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (items[i] != null) {
			ItemStack itemstack = items[i];

			items[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (items[i] != null) {
			ItemStack itemstack;

			if (items[i].count <= j) {
				itemstack = items[i];
				items[i] = null;
				c.a(this);
				return itemstack;
			} else {
				itemstack = items[i].a(j);
				if (items[i].count == 0) {
					items[i] = null;
				}

				c.a(this);
				return itemstack;
			}
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		items[i] = itemstack;
		c.a(this);
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
