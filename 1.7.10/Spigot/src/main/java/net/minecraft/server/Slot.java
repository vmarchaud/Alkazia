package net.minecraft.server;

public class Slot {

	public final int index; // CraftBukkit - private -> public
	public final IInventory inventory;
	public int rawSlotIndex;
	public int h;
	public int i;

	public Slot(IInventory iinventory, int i, int j, int k) {
		inventory = iinventory;
		index = i;
		h = j;
		this.i = k;
	}

	public void a(ItemStack itemstack, ItemStack itemstack1) {
		if (itemstack != null && itemstack1 != null) {
			if (itemstack.getItem() == itemstack1.getItem()) {
				int i = itemstack1.count - itemstack.count;

				if (i > 0) {
					this.a(itemstack, i);
				}
			}
		}
	}

	protected void a(ItemStack itemstack, int i) {
	}

	protected void b(ItemStack itemstack) {
	}

	public void a(EntityHuman entityhuman, ItemStack itemstack) {
		f();
	}

	public boolean isAllowed(ItemStack itemstack) {
		return true;
	}

	public ItemStack getItem() {
		return inventory.getItem(index);
	}

	public boolean hasItem() {
		return getItem() != null;
	}

	public void set(ItemStack itemstack) {
		inventory.setItem(index, itemstack);
		f();
	}

	public void f() {
		inventory.update();
	}

	public int getMaxStackSize() {
		return inventory.getMaxStackSize();
	}

	public ItemStack a(int i) {
		return inventory.splitStack(index, i);
	}

	public boolean a(IInventory iinventory, int i) {
		return iinventory == inventory && i == index;
	}

	public boolean isAllowed(EntityHuman entityhuman) {
		return true;
	}
}
