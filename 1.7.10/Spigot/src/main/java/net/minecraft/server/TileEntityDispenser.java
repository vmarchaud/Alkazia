package net.minecraft.server;

// CraftBukkit start
import java.util.List;
import java.util.Random;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class TileEntityDispenser extends TileEntity implements IInventory {

	private ItemStack[] items = new ItemStack[9];
	private Random j = new Random();
	protected String a;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return items;
	}

	@Override
	public void onOpen(CraftHumanEntity who) {
		transaction.add(who);
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
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	public TileEntityDispenser() {
	}

	@Override
	public int getSize() {
		return 9;
	}

	@Override
	public ItemStack getItem(int i) {
		return items[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (items[i] != null) {
			ItemStack itemstack;

			if (items[i].count <= j) {
				itemstack = items[i];
				items[i] = null;
				update();
				return itemstack;
			} else {
				itemstack = items[i].a(j);
				if (items[i].count == 0) {
					items[i] = null;
				}

				update();
				return itemstack;
			}
		} else
			return null;
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

	public int i() {
		int i = -1;
		int j = 1;

		for (int k = 0; k < items.length; ++k) {
			if (items[k] != null && this.j.nextInt(j++) == 0) {
				if (items[k].count == 0) {
					continue; // CraftBukkit
				}
				i = k;
			}
		}

		return i;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		items[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize()) {
			itemstack.count = getMaxStackSize();
		}

		update();
	}

	public int addItem(ItemStack itemstack) {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] == null || items[i].getItem() == null) {
				setItem(i, itemstack);
				return i;
			}
		}

		return -1;
	}

	@Override
	public String getInventoryName() {
		return k_() ? a : "container.dispenser";
	}

	public void a(String s) {
		a = s;
	}

	@Override
	public boolean k_() {
		return a != null;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		items = new ItemStack[getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < items.length) {
				items[j] = ItemStack.createStack(nbttagcompound1);
			}
		}

		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			a = nbttagcompound.getString("CustomName");
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();

				nbttagcompound1.setByte("Slot", (byte) i);
				items[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}
		}

		nbttagcompound.set("Items", nbttaglist);
		if (k_()) {
			nbttagcompound.setString("CustomName", a);
		}
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return world.getTileEntity(x, y, z) != this ? false : entityhuman.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
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
