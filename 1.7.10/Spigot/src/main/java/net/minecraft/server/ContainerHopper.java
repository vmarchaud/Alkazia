package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;

// CraftBukkit end

public class ContainerHopper extends Container {

	private final IInventory hopper;

	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;

	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity != null)
			return bukkitEntity;

		CraftInventory inventory = new CraftInventory(hopper);
		bukkitEntity = new CraftInventoryView(player.player.getBukkitEntity(), inventory, this);
		return bukkitEntity;
	}

	// CraftBukkit end

	public ContainerHopper(PlayerInventory playerinventory, IInventory iinventory) {
		hopper = iinventory;
		player = playerinventory; // CraftBukkit - save player
		iinventory.startOpen();
		byte b0 = 51;

		int i;

		for (i = 0; i < iinventory.getSize(); ++i) {
			this.a(new Slot(iinventory, i, 44 + i * 18, 20));
		}

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.a(new Slot(playerinventory, i, 8 + i * 18, 58 + b0));
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (!checkReachable)
			return true; // CraftBukkit
		return hopper.a(entityhuman);
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i < hopper.getSize()) {
				if (!this.a(itemstack1, hopper.getSize(), c.size(), true))
					return null;
			} else if (!this.a(itemstack1, 0, hopper.getSize(), false))
				return null;

			if (itemstack1.count == 0) {
				slot.set((ItemStack) null);
			} else {
				slot.f();
			}
		}

		return itemstack;
	}

	@Override
	public void b(EntityHuman entityhuman) {
		super.b(entityhuman);
		hopper.closeContainer();
	}
}
