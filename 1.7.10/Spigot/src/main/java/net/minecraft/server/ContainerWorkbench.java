package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;

// CraftBukkit end

public class ContainerWorkbench extends Container {

	public InventoryCrafting craftInventory; // CraftBukkit - move initialization into constructor
	public IInventory resultInventory; // CraftBukkit - move initialization into constructor
	private World g;
	private int h;
	private int i;
	private int j;
	// CraftBukkit start
	private CraftInventoryView bukkitEntity = null;
	private PlayerInventory player;

	// CraftBukkit end

	public ContainerWorkbench(PlayerInventory playerinventory, World world, int i, int j, int k) {
		// CraftBukkit start - Switched order of IInventory construction and stored player
		resultInventory = new InventoryCraftResult();
		craftInventory = new InventoryCrafting(this, 3, 3, playerinventory.player); // CraftBukkit - pass player
		craftInventory.resultInventory = resultInventory;
		player = playerinventory;
		// CraftBukkit end
		g = world;
		h = i;
		this.i = j;
		this.j = k;
		this.a(new SlotResult(playerinventory.player, craftInventory, resultInventory, 0, 124, 35));

		int l;
		int i1;

		for (l = 0; l < 3; ++l) {
			for (i1 = 0; i1 < 3; ++i1) {
				this.a(new Slot(craftInventory, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
			}
		}

		for (l = 0; l < 3; ++l) {
			for (i1 = 0; i1 < 9; ++i1) {
				this.a(new Slot(playerinventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
			}
		}

		for (l = 0; l < 9; ++l) {
			this.a(new Slot(playerinventory, l, 8 + l * 18, 142));
		}

		this.a(craftInventory);
	}

	@Override
	public void a(IInventory iinventory) {
		// CraftBukkit start
		CraftingManager.getInstance().lastCraftView = getBukkitView();
		ItemStack craftResult = CraftingManager.getInstance().craft(craftInventory, g);
		resultInventory.setItem(0, craftResult);
		if (super.listeners.size() < 1)
			return;

		EntityPlayer player = (EntityPlayer) super.listeners.get(0); // TODO: Is this _always_ correct? Seems like it.
		player.playerConnection.sendPacket(new PacketPlayOutSetSlot(player.activeContainer.windowId, 0, craftResult));
		// CraftBukkit end
	}

	@Override
	public void b(EntityHuman entityhuman) {
		super.b(entityhuman);
		if (!g.isStatic) {
			for (int i = 0; i < 9; ++i) {
				ItemStack itemstack = craftInventory.splitWithoutUpdate(i);

				if (itemstack != null) {
					entityhuman.drop(itemstack, false);
				}
			}
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (!checkReachable)
			return true; // CraftBukkit
		return g.getType(h, i, j) != Blocks.WORKBENCH ? false : entityhuman.e(h + 0.5D, i + 0.5D, j + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack b(EntityHuman entityhuman, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) c.get(i);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();

			itemstack = itemstack1.cloneItemStack();
			if (i == 0) {
				if (!this.a(itemstack1, 10, 46, true))
					return null;

				slot.a(itemstack1, itemstack);
			} else if (i >= 10 && i < 37) {
				if (!this.a(itemstack1, 37, 46, false))
					return null;
			} else if (i >= 37 && i < 46) {
				if (!this.a(itemstack1, 10, 37, false))
					return null;
			} else if (!this.a(itemstack1, 10, 46, false))
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
	public boolean a(ItemStack itemstack, Slot slot) {
		return slot.inventory != resultInventory && super.a(itemstack, slot);
	}

	// CraftBukkit start
	@Override
	public CraftInventoryView getBukkitView() {
		if (bukkitEntity != null)
			return bukkitEntity;

		CraftInventoryCrafting inventory = new CraftInventoryCrafting(craftInventory, resultInventory);
		bukkitEntity = new CraftInventoryView(player.player.getBukkitEntity(), inventory, this);
		return bukkitEntity;
	}
	// CraftBukkit end
}
