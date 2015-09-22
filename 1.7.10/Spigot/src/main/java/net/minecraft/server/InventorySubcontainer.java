package net.minecraft.server;

import java.util.ArrayList;
import java.util.List;

public abstract class InventorySubcontainer implements IInventory { // CraftBukkit - abstract

	private String a;
	private int b;
	protected ItemStack[] items; // CraftBukkit - protected
	private List d;
	private boolean e;

	public InventorySubcontainer(String s, boolean flag, int i) {
		a = s;
		e = flag;
		b = i;
		items = new ItemStack[i];
	}

	public void a(IInventoryListener iinventorylistener) {
		if (d == null) {
			d = new ArrayList();
		}

		d.add(iinventorylistener);
	}

	public void b(IInventoryListener iinventorylistener) {
		d.remove(iinventorylistener);
	}

	@Override
	public ItemStack getItem(int i) {
		return i >= 0 && i < items.length ? items[i] : null;
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

	@Override
	public void setItem(int i, ItemStack itemstack) {
		items[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize()) {
			itemstack.count = getMaxStackSize();
		}

		update();
	}

	@Override
	public int getSize() {
		return b;
	}

	@Override
	public String getInventoryName() {
		return a;
	}

	@Override
	public boolean k_() {
		return e;
	}

	public void a(String s) {
		e = true;
		a = s;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void update() {
		if (d != null) {
			for (int i = 0; i < d.size(); ++i) {
				((IInventoryListener) d.get(i)).a(this);
			}
		}
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
