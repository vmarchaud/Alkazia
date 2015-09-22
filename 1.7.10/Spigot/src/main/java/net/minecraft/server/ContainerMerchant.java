package net.minecraft.server;

import org.bukkit.craftbukkit.inventory.CraftInventoryView; // CraftBukkit

public class ContainerMerchant extends Container {

	private IMerchant merchant;
	private InventoryMerchant f;
	private final World g;

	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;

	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity == null) {
			bukkitEntity = new CraftInventoryView(player.player.getBukkitEntity(), new org.bukkit.craftbukkit.inventory.CraftInventoryMerchant(getMerchantInventory()), this);
		}
		return bukkitEntity;
	}

	// CraftBukkit end

	public ContainerMerchant(PlayerInventory playerinventory, IMerchant imerchant, World world) {
		merchant = imerchant;
		g = world;
		f = new InventoryMerchant(playerinventory.player, imerchant);
		this.a(new Slot(f, 0, 36, 53));
		this.a(new Slot(f, 1, 62, 53));
		this.a(new SlotMerchantResult(playerinventory.player, imerchant, f, 2, 120, 53));
		player = playerinventory; // CraftBukkit - save player

		int i;

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
		}
	}

	public InventoryMerchant getMerchantInventory() {
		return f;
	}

	@Override
	public void addSlotListener(ICrafting icrafting) {
		super.addSlotListener(icrafting);
	}

	@Override
	public void b() {
		super.b();
	}

	@Override
	public void a(IInventory iinventory) {
		f.h();
		super.a(iinventory);
	}

	public void e(int i) {
		f.c(i);
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return merchant.b() == entityhuman;
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i == 2) {
				if (!this.a(itemstack1, 3, 39, true))
					return null;

				slot.a(itemstack1, itemstack);
			} else if (i != 0 && i != 1) {
				if (i >= 3 && i < 30) {
					if (!this.a(itemstack1, 30, 39, false))
						return null;
				} else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false))
					return null;
			} else if (!this.a(itemstack1, 3, 39, false))
				return null;

			if (itemstack1.count == 0) {
				slot.set((ItemStack) null);
			} else {
				slot.f();
			}

			if (itemstack1.count == itemstack.count)
				return null;

			slot.a(entityhuman, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void b(EntityHuman entityhuman) {
		super.b(entityhuman);
		merchant.a_((EntityHuman) null);
		super.b(entityhuman);
		if (!g.isStatic) {
			ItemStack itemstack = f.splitWithoutUpdate(0);

			if (itemstack != null) {
				entityhuman.drop(itemstack, false);
			}

			itemstack = f.splitWithoutUpdate(1);
			if (itemstack != null) {
				entityhuman.drop(itemstack, false);
			}
		}
	}
}
