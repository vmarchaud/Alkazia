package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class PlayerInventory implements IInventory {

	public ItemStack[] items = new ItemStack[36];
	public ItemStack[] armor = new ItemStack[4];
	public int itemInHandIndex;
	public EntityHuman player;
	private ItemStack g;
	public boolean e;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return items;
	}

	public ItemStack[] getArmorContents() {
		return armor;
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
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return player.getBukkitEntity();
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	public PlayerInventory(EntityHuman entityhuman) {
		player = entityhuman;
	}

	public ItemStack getItemInHand() {
		return itemInHandIndex < 9 && itemInHandIndex >= 0 ? items[itemInHandIndex] : null;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private int c(Item item) {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null && items[i].getItem() == item)
				return i;
		}

		return -1;
	}

	private int firstPartial(ItemStack itemstack) {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null && items[i].getItem() == itemstack.getItem() && items[i].isStackable() && items[i].count < items[i].getMaxStackSize() && items[i].count < getMaxStackSize() && (!items[i].usesData() || items[i].getData() == itemstack.getData()) && ItemStack.equals(items[i], itemstack))
				return i;
		}

		return -1;
	}

	// CraftBukkit start - Watch method above! :D
	public int canHold(ItemStack itemstack) {
		int remains = itemstack.count;
		for (int i = 0; i < items.length; ++i) {
			if (items[i] == null)
				return itemstack.count;

			// Taken from firstPartial(ItemStack)
			if (items[i] != null && items[i].getItem() == itemstack.getItem() && items[i].isStackable() && items[i].count < items[i].getMaxStackSize() && items[i].count < getMaxStackSize() && (!items[i].usesData() || items[i].getData() == itemstack.getData()) && ItemStack.equals(items[i], itemstack)) {
				remains -= (items[i].getMaxStackSize() < getMaxStackSize() ? items[i].getMaxStackSize() : getMaxStackSize()) - items[i].count;
			}
			if (remains <= 0)
				return itemstack.count;
		}
		return itemstack.count - remains;
	}

	// CraftBukkit end

	public int getFirstEmptySlotIndex() {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] == null)
				return i;
		}

		return -1;
	}

	public int a(Item item, int i) {
		int j = 0;

		int k;
		ItemStack itemstack;

		for (k = 0; k < items.length; ++k) {
			itemstack = items[k];
			if (itemstack != null && (item == null || itemstack.getItem() == item) && (i <= -1 || itemstack.getData() == i)) {
				j += itemstack.count;
				items[k] = null;
			}
		}

		for (k = 0; k < armor.length; ++k) {
			itemstack = armor[k];
			if (itemstack != null && (item == null || itemstack.getItem() == item) && (i <= -1 || itemstack.getData() == i)) {
				j += itemstack.count;
				armor[k] = null;
			}
		}

		if (g != null) {
			if (item != null && g.getItem() != item)
				return j;

			if (i > -1 && g.getData() != i)
				return j;

			j += g.count;
			setCarried((ItemStack) null);
		}

		return j;
	}

	private int e(ItemStack itemstack) {
		Item item = itemstack.getItem();
		int i = itemstack.count;
		int j;

		if (itemstack.getMaxStackSize() == 1) {
			j = getFirstEmptySlotIndex();
			if (j < 0)
				return i;
			else {
				if (items[j] == null) {
					items[j] = ItemStack.b(itemstack);
				}

				return 0;
			}
		} else {
			j = firstPartial(itemstack);
			if (j < 0) {
				j = getFirstEmptySlotIndex();
			}

			if (j < 0)
				return i;
			else {
				if (items[j] == null) {
					items[j] = new ItemStack(item, 0, itemstack.getData());
					if (itemstack.hasTag()) {
						items[j].setTag((NBTTagCompound) itemstack.getTag().clone());
					}
				}

				int k = i;

				if (i > items[j].getMaxStackSize() - items[j].count) {
					k = items[j].getMaxStackSize() - items[j].count;
				}

				if (k > getMaxStackSize() - items[j].count) {
					k = getMaxStackSize() - items[j].count;
				}

				if (k == 0)
					return i;
				else {
					i -= k;
					items[j].count += k;
					items[j].c = 5;
					return i;
				}
			}
		}
	}

	public void k() {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null) {
				items[i].a(player.world, player, i, itemInHandIndex == i);
			}
		}
	}

	public boolean a(Item item) {
		int i = this.c(item);

		if (i < 0)
			return false;
		else {
			if (--items[i].count <= 0) {
				items[i] = null;
			}

			return true;
		}
	}

	public boolean b(Item item) {
		int i = this.c(item);

		return i >= 0;
	}

	public boolean pickup(ItemStack itemstack) {
		if (itemstack != null && itemstack.count != 0 && itemstack.getItem() != null) {
			try {
				int i;

				if (itemstack.i()) {
					i = getFirstEmptySlotIndex();
					if (i >= 0) {
						items[i] = ItemStack.b(itemstack);
						items[i].c = 5;
						itemstack.count = 0;
						return true;
					} else if (player.abilities.canInstantlyBuild) {
						itemstack.count = 0;
						return true;
					} else
						return false;
				} else {
					do {
						i = itemstack.count;
						itemstack.count = e(itemstack);
					} while (itemstack.count > 0 && itemstack.count < i);

					if (itemstack.count == i && player.abilities.canInstantlyBuild) {
						itemstack.count = 0;
						return true;
					} else
						return itemstack.count < i;
				}
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
				CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");

				crashreportsystemdetails.a("Item ID", Integer.valueOf(Item.getId(itemstack.getItem())));
				crashreportsystemdetails.a("Item data", Integer.valueOf(itemstack.getData()));
				crashreportsystemdetails.a("Item name", new CrashReportItemName(this, itemstack));
				throw new ReportedException(crashreport);
			}
		} else
			return false;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		ItemStack[] aitemstack = items;

		if (i >= items.length) {
			aitemstack = armor;
			i -= items.length;
		}

		if (aitemstack[i] != null) {
			ItemStack itemstack;

			if (aitemstack[i].count <= j) {
				itemstack = aitemstack[i];
				aitemstack[i] = null;
				return itemstack;
			} else {
				itemstack = aitemstack[i].a(j);
				if (aitemstack[i].count == 0) {
					aitemstack[i] = null;
				}

				return itemstack;
			}
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		ItemStack[] aitemstack = items;

		if (i >= items.length) {
			aitemstack = armor;
			i -= items.length;
		}

		if (aitemstack[i] != null) {
			ItemStack itemstack = aitemstack[i];

			aitemstack[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		ItemStack[] aitemstack = items;

		if (i >= aitemstack.length) {
			i -= aitemstack.length;
			aitemstack = armor;
		}

		aitemstack[i] = itemstack;
	}

	public float a(Block block) {
		float f = 1.0F;

		if (items[itemInHandIndex] != null) {
			f *= items[itemInHandIndex].a(block);
		}

		return f;
	}

	public NBTTagList a(NBTTagList nbttaglist) {
		int i;
		NBTTagCompound nbttagcompound;

		for (i = 0; i < items.length; ++i) {
			if (items[i] != null) {
				nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				items[i].save(nbttagcompound);
				nbttaglist.add(nbttagcompound);
			}
		}

		for (i = 0; i < armor.length; ++i) {
			if (armor[i] != null) {
				nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) (i + 100));
				armor[i].save(nbttagcompound);
				nbttaglist.add(nbttagcompound);
			}
		}

		return nbttaglist;
	}

	public void b(NBTTagList nbttaglist) {
		items = new ItemStack[36];
		armor = new ItemStack[4];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.get(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.createStack(nbttagcompound);

			if (itemstack != null) {
				if (j >= 0 && j < items.length) {
					items[j] = itemstack;
				}

				if (j >= 100 && j < armor.length + 100) {
					armor[j - 100] = itemstack;
				}
			}
		}
	}

	@Override
	public int getSize() {
		return items.length + 4;
	}

	@Override
	public ItemStack getItem(int i) {
		ItemStack[] aitemstack = items;

		if (i >= aitemstack.length) {
			i -= aitemstack.length;
			aitemstack = armor;
		}

		return aitemstack[i];
	}

	@Override
	public String getInventoryName() {
		return "container.inventory";
	}

	@Override
	public boolean k_() {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	public boolean b(Block block) {
		if (block.getMaterial().isAlwaysDestroyable())
			return true;
		else {
			ItemStack itemstack = getItem(itemInHandIndex);

			return itemstack != null ? itemstack.b(block) : false;
		}
	}

	public ItemStack d(int i) {
		return armor[i];
	}

	public int l() {
		int i = 0;

		for (int j = 0; j < armor.length; ++j) {
			if (armor[j] != null && armor[j].getItem() instanceof ItemArmor) {
				int k = ((ItemArmor) armor[j].getItem()).c;

				i += k;
			}
		}

		return i;
	}

	public void a(float f) {
		f /= 4.0F;
		if (f < 1.0F) {
			f = 1.0F;
		}

		for (int i = 0; i < armor.length; ++i) {
			if (armor[i] != null && armor[i].getItem() instanceof ItemArmor) {
				armor[i].damage((int) f, player);
				if (armor[i].count == 0) {
					armor[i] = null;
				}
			}
		}
	}

	public void m() {
		int i;

		for (i = 0; i < items.length; ++i) {
			if (items[i] != null) {
				player.a(items[i], true, false);
				items[i] = null;
			}
		}

		for (i = 0; i < armor.length; ++i) {
			if (armor[i] != null) {
				player.a(armor[i], true, false);
				armor[i] = null;
			}
		}
	}

	@Override
	public void update() {
		e = true;
	}

	public void setCarried(ItemStack itemstack) {
		g = itemstack;
	}

	public ItemStack getCarried() {
		// CraftBukkit start
		if (g != null && g.count == 0) {
			setCarried(null);
		}
		// CraftBukkit end
		return g;
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return player.dead ? false : entityhuman.f(player) <= 64.0D;
	}

	public boolean c(ItemStack itemstack) {
		int i;

		for (i = 0; i < armor.length; ++i) {
			if (armor[i] != null && armor[i].doMaterialsMatch(itemstack))
				return true;
		}

		for (i = 0; i < items.length; ++i) {
			if (items[i] != null && items[i].doMaterialsMatch(itemstack))
				return true;
		}

		return false;
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

	public void b(PlayerInventory playerinventory) {
		int i;

		for (i = 0; i < items.length; ++i) {
			items[i] = ItemStack.b(playerinventory.items[i]);
		}

		for (i = 0; i < armor.length; ++i) {
			armor[i] = ItemStack.b(playerinventory.armor[i]);
		}

		itemInHandIndex = playerinventory.itemInHandIndex;
	}
}
