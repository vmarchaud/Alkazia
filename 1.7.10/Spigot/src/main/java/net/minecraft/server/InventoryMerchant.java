package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class InventoryMerchant implements IInventory {

	private final IMerchant merchant;
	private ItemStack[] itemsInSlots = new ItemStack[3];
	private final EntityHuman player;
	private MerchantRecipe recipe;
	private int e;

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return itemsInSlots;
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
	public void setMaxStackSize(int i) {
		maxStack = i;
	}

	@Override
	public org.bukkit.inventory.InventoryHolder getOwner() {
		return player.getBukkitEntity();
	}

	// CraftBukkit end

	public InventoryMerchant(EntityHuman entityhuman, IMerchant imerchant) {
		player = entityhuman;
		merchant = imerchant;
	}

	@Override
	public int getSize() {
		return itemsInSlots.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return itemsInSlots[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (itemsInSlots[i] != null) {
			ItemStack itemstack;

			if (i == 2) {
				itemstack = itemsInSlots[i];
				itemsInSlots[i] = null;
				return itemstack;
			} else if (itemsInSlots[i].count <= j) {
				itemstack = itemsInSlots[i];
				itemsInSlots[i] = null;
				if (d(i)) {
					h();
				}

				return itemstack;
			} else {
				itemstack = itemsInSlots[i].a(j);
				if (itemsInSlots[i].count == 0) {
					itemsInSlots[i] = null;
				}

				if (d(i)) {
					h();
				}

				return itemstack;
			}
		} else
			return null;
	}

	private boolean d(int i) {
		return i == 0 || i == 1;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (itemsInSlots[i] != null) {
			ItemStack itemstack = itemsInSlots[i];

			itemsInSlots[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		itemsInSlots[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize()) {
			itemstack.count = getMaxStackSize();
		}

		if (d(i)) {
			h();
		}
	}

	@Override
	public String getInventoryName() {
		return "mob.villager";
	}

	@Override
	public boolean k_() {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return merchant.b() == entityhuman;
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

	@Override
	public void update() {
		h();
	}

	public void h() {
		recipe = null;
		ItemStack itemstack = itemsInSlots[0];
		ItemStack itemstack1 = itemsInSlots[1];

		if (itemstack == null) {
			itemstack = itemstack1;
			itemstack1 = null;
		}

		if (itemstack == null) {
			setItem(2, (ItemStack) null);
		} else {
			MerchantRecipeList merchantrecipelist = merchant.getOffers(player);

			if (merchantrecipelist != null) {
				MerchantRecipe merchantrecipe = merchantrecipelist.a(itemstack, itemstack1, e);

				if (merchantrecipe != null && !merchantrecipe.g()) {
					recipe = merchantrecipe;
					setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
				} else if (itemstack1 != null) {
					merchantrecipe = merchantrecipelist.a(itemstack1, itemstack, e);
					if (merchantrecipe != null && !merchantrecipe.g()) {
						recipe = merchantrecipe;
						setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
					} else {
						setItem(2, (ItemStack) null);
					}
				} else {
					setItem(2, (ItemStack) null);
				}
			}
		}

		merchant.a_(getItem(2));
	}

	public MerchantRecipe getRecipe() {
		return recipe;
	}

	public void c(int i) {
		e = i;
		h();
	}
}
