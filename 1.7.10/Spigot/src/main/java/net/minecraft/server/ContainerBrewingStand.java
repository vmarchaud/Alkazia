package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;

// CraftBukkit end

public class ContainerBrewingStand extends Container {

	private TileEntityBrewingStand brewingStand;
	private final Slot f;
	private int g;
	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;

	// CraftBukkit end

	public ContainerBrewingStand(PlayerInventory playerinventory, TileEntityBrewingStand tileentitybrewingstand) {
		player = playerinventory; // CraftBukkit
		brewingStand = tileentitybrewingstand;
		this.a(new SlotPotionBottle(playerinventory.player, tileentitybrewingstand, 0, 56, 46));
		this.a(new SlotPotionBottle(playerinventory.player, tileentitybrewingstand, 1, 79, 53));
		this.a(new SlotPotionBottle(playerinventory.player, tileentitybrewingstand, 2, 102, 46));
		f = this.a(new SlotBrewing(this, tileentitybrewingstand, 3, 79, 17));

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

	@Override
	public void addSlotListener(ICrafting icrafting) {
		super.addSlotListener(icrafting);
		icrafting.setContainerData(this, 0, brewingStand.i());
	}

	@Override
	public void b() {
		super.b();

		for (int i = 0; i < listeners.size(); ++i) {
			ICrafting icrafting = (ICrafting) listeners.get(i);

			if (g != brewingStand.i()) {
				icrafting.setContainerData(this, 0, brewingStand.i());
			}
		}

		g = brewingStand.i();
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (!checkReachable)
			return true; // CraftBukkit
		return brewingStand.a(entityhuman);
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if ((i < 0 || i > 2) && i != 3) {
				if (!f.hasItem() && f.isAllowed(itemstack1)) {
					if (!this.a(itemstack1, 3, 4, false))
						return null;
				} else if (SlotPotionBottle.b_(itemstack)) {
					if (!this.a(itemstack1, 0, 3, false))
						return null;
				} else if (i >= 4 && i < 31) {
					if (!this.a(itemstack1, 31, 40, false))
						return null;
				} else if (i >= 31 && i < 40) {
					if (!this.a(itemstack1, 4, 31, false))
						return null;
				} else if (!this.a(itemstack1, 4, 40, false))
					return null;
			} else {
				if (!this.a(itemstack1, 4, 40, true))
					return null;

				slot.a(itemstack1, itemstack);
			}

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

	// CraftBukkit start
	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity != null)
			return bukkitEntity;

		CraftInventoryBrewer inventory = new CraftInventoryBrewer(brewingStand);
		bukkitEntity = new CraftInventoryView(player.player.getBukkitEntity(), inventory, this);
		return bukkitEntity;
	}
	// CraftBukkit end
}
