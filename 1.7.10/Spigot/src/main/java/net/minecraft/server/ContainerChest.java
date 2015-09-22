package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;

// CraftBukkit end

public class ContainerChest extends Container {

	public IInventory container; // CraftBukkit - private->public
	private int f;
	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;

	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity != null)
			return bukkitEntity;

		CraftInventory inventory;
		if (container instanceof PlayerInventory) {
			inventory = new org.bukkit.craftbukkit.inventory.CraftInventoryPlayer((PlayerInventory) container);
		} else if (container instanceof InventoryLargeChest) {
			inventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) container);
		} else {
			inventory = new CraftInventory(container);
		}

		bukkitEntity = new CraftInventoryView(player.player.getBukkitEntity(), inventory, this);
		return bukkitEntity;
	}

	// CraftBukkit end

	public ContainerChest(IInventory iinventory, IInventory iinventory1) {
		container = iinventory1;
		f = iinventory1.getSize() / 9;
		iinventory1.startOpen();
		int i = (f - 4) * 18;
		// CraftBukkit start - Save player
		// TODO: Should we check to make sure it really is an InventoryPlayer?
		player = (PlayerInventory) iinventory;
		// CraftBukkit end

		int j;
		int k;

		for (j = 0; j < f; ++j) {
			for (k = 0; k < 9; ++k) {
				this.a(new Slot(iinventory1, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (j = 0; j < 3; ++j) {
			for (k = 0; k < 9; ++k) {
				this.a(new Slot(iinventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}

		for (j = 0; j < 9; ++j) {
			this.a(new Slot(iinventory, j, 8 + j * 18, 161 + i));
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (!checkReachable)
			return true; // CraftBukkit
		return container.a(entityhuman);
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i < f * 9) {
				if (!this.a(itemstack1, f * 9, c.size(), true))
					return null;
			} else if (!this.a(itemstack1, 0, f * 9, false))
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
		container.closeContainer();
	}

	public IInventory e() {
		return container;
	}
}
