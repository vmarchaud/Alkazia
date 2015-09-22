package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class ContainerAnvilInventory extends InventorySubcontainer { // CraftBukkit - public

	final ContainerAnvil a;

	// CraftBukkit start
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	public org.bukkit.entity.Player player;
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
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return player;
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	ContainerAnvilInventory(ContainerAnvil containeranvil, String s, boolean flag, int i) {
		super(s, flag, i);
		a = containeranvil;
	}

	// CraftBukkit start - override inherited maxStack from InventorySubcontainer
	@Override
	public int getMaxStackSize() {
		return maxStack;
	}

	// CraftBukkit end

	@Override
	public void update() {
		super.update();
		a.a(this);
	}
}
