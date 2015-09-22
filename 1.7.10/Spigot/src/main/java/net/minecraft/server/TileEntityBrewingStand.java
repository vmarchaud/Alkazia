package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.BrewEvent;

// CraftBukkit end

public class TileEntityBrewingStand extends TileEntity implements IWorldInventory {

	private static final int[] a = new int[] { 3 };
	private static final int[] i = new int[] { 0, 1, 2 };
	public ItemStack[] items = new ItemStack[4]; // CraftBukkit - private -> public
	public int brewTime; // CraftBukkit - private -> public
	private int l;
	private Item m;
	private String n;
	private int lastTick = MinecraftServer.currentTick; // CraftBukkit - add field

	public TileEntityBrewingStand() {
	}

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = 64;

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
	public ItemStack[] getContents() {
		return items;
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	@Override
	public String getInventoryName() {
		return k_() ? n : "container.brewing";
	}

	@Override
	public boolean k_() {
		return n != null && n.length() > 0;
	}

	public void a(String s) {
		n = s;
	}

	@Override
	public int getSize() {
		return items.length;
	}

	@Override
	public void h() {
		// CraftBukkit start - Use wall time instead of ticks for brewing
		int elapsedTicks = MinecraftServer.currentTick - lastTick;
		lastTick = MinecraftServer.currentTick;

		if (brewTime > 0) {
			brewTime -= elapsedTicks;
			if (brewTime <= 0) { // == -> <=
				// CraftBukkit end
				l();
				update();
			} else if (!k()) {
				brewTime = 0;
				update();
			} else if (m != items[3].getItem()) {
				brewTime = 0;
				update();
			}
		} else if (k()) {
			brewTime = 400;
			m = items[3].getItem();
		}

		int i = j();

		if (i != l) {
			l = i;
			world.setData(x, y, z, i, 2);
		}

		super.h();
	}

	public int i() {
		return brewTime;
	}

	private boolean k() {
		if (items[3] != null && items[3].count > 0) {
			ItemStack itemstack = items[3];

			if (!itemstack.getItem().m(itemstack))
				return false;
			else {
				boolean flag = false;

				for (int i = 0; i < 3; ++i) {
					if (items[i] != null && items[i].getItem() == Items.POTION) {
						int j = items[i].getData();
						int k = this.c(j, itemstack);

						if (!ItemPotion.g(j) && ItemPotion.g(k)) {
							flag = true;
							break;
						}

						List list = Items.POTION.c(j);
						List list1 = Items.POTION.c(k);

						if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
							flag = true;
							break;
						}
					}
				}

				return flag;
			}
		} else
			return false;
	}

	private void l() {
		if (k()) {
			ItemStack itemstack = items[3];

			// CraftBukkit start
			if (getOwner() != null) {
				BrewEvent event = new BrewEvent(world.getWorld().getBlockAt(x, y, z), (org.bukkit.inventory.BrewerInventory) getOwner().getInventory());
				org.bukkit.Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
			}
			// CraftBukkit end

			for (int i = 0; i < 3; ++i) {
				if (items[i] != null && items[i].getItem() == Items.POTION) {
					int j = items[i].getData();
					int k = this.c(j, itemstack);
					List list = Items.POTION.c(j);
					List list1 = Items.POTION.c(k);

					if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null)) {
						if (j != k) {
							items[i].setData(k);
						}
					} else if (!ItemPotion.g(j) && ItemPotion.g(k)) {
						items[i].setData(k);
					}
				}
			}

			if (itemstack.getItem().u()) {
				items[3] = new ItemStack(itemstack.getItem().t());
			} else {
				--items[3].count;
				if (items[3].count <= 0) {
					items[3] = null;
				}
			}
		}
	}

	private int c(int i, ItemStack itemstack) {
		return itemstack == null ? i : itemstack.getItem().m(itemstack) ? PotionBrewer.a(i, itemstack.getItem().i(itemstack)) : i;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		items = new ItemStack[getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < items.length) {
				items[b0] = ItemStack.createStack(nbttagcompound1);
			}
		}

		brewTime = nbttagcompound.getShort("BrewTime");
		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			n = nbttagcompound.getString("CustomName");
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("BrewTime", (short) brewTime);
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
			nbttagcompound.setString("CustomName", n);
		}
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= 0 && i < items.length ? items[i] : null;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (i >= 0 && i < items.length) {
			ItemStack itemstack = items[i];

			items[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (i >= 0 && i < items.length) {
			ItemStack itemstack = items[i];

			items[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		if (i >= 0 && i < items.length) {
			items[i] = itemstack;
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
		return i == 3 ? itemstack.getItem().m(itemstack) : itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.GLASS_BOTTLE;
	}

	public int j() {
		int i = 0;

		for (int j = 0; j < 3; ++j) {
			if (items[j] != null) {
				i |= 1 << j;
			}
		}

		return i;
	}

	@Override
	public int[] getSlotsForFace(int i) {
		return i == 1 ? a : TileEntityBrewingStand.i; // CraftBukkit - decompilation error
	}

	@Override
	public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, int j) {
		return this.b(i, itemstack);
	}

	@Override
	public boolean canTakeItemThroughFace(int i, ItemStack itemstack, int j) {
		return true;
	}
}
