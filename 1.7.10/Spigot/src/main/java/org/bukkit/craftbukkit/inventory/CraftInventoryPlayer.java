package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.PacketPlayOutHeldItemSlot;
import net.minecraft.server.PlayerInventory;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class CraftInventoryPlayer extends CraftInventory implements org.bukkit.inventory.PlayerInventory, EntityEquipment {
	public CraftInventoryPlayer(net.minecraft.server.PlayerInventory inventory) {
		super(inventory);
	}

	@Override
	public PlayerInventory getInventory() {
		return (PlayerInventory) inventory;
	}

	@Override
	public int getSize() {
		return super.getSize() - 4;
	}

	@Override
	public ItemStack getItemInHand() {
		return CraftItemStack.asCraftMirror(getInventory().getItemInHand());
	}

	@Override
	public void setItemInHand(ItemStack stack) {
		setItem(getHeldItemSlot(), stack);
	}

	@Override
	public int getHeldItemSlot() {
		return getInventory().itemInHandIndex;
	}

	@Override
	public void setHeldItemSlot(int slot) {
		Validate.isTrue(slot >= 0 && slot < PlayerInventory.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
		getInventory().itemInHandIndex = slot;
		((CraftPlayer) getHolder()).getHandle().playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(slot));
	}

	@Override
	public ItemStack getHelmet() {
		return getItem(getSize() + 3);
	}

	@Override
	public ItemStack getChestplate() {
		return getItem(getSize() + 2);
	}

	@Override
	public ItemStack getLeggings() {
		return getItem(getSize() + 1);
	}

	@Override
	public ItemStack getBoots() {
		return getItem(getSize() + 0);
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		setItem(getSize() + 3, helmet);
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		setItem(getSize() + 2, chestplate);
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		setItem(getSize() + 1, leggings);
	}

	@Override
	public void setBoots(ItemStack boots) {
		setItem(getSize() + 0, boots);
	}

	@Override
	public ItemStack[] getArmorContents() {
		net.minecraft.server.ItemStack[] mcItems = getInventory().getArmorContents();
		ItemStack[] ret = new ItemStack[mcItems.length];

		for (int i = 0; i < mcItems.length; i++) {
			ret[i] = CraftItemStack.asCraftMirror(mcItems[i]);
		}
		return ret;
	}

	@Override
	public void setArmorContents(ItemStack[] items) {
		int cnt = getSize();

		if (items == null) {
			items = new ItemStack[4];
		}
		for (ItemStack item : items) {
			if (item == null || item.getTypeId() == 0) {
				clear(cnt++);
			} else {
				setItem(cnt++, item);
			}
		}
	}

	@Override
	public int clear(int id, int data) {
		int count = 0;
		ItemStack[] items = getContents();
		ItemStack[] armor = getArmorContents();
		int armorSlot = getSize();

		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null) {
				continue;
			}
			if (id > -1 && item.getTypeId() != id) {
				continue;
			}
			if (data > -1 && item.getData().getData() != data) {
				continue;
			}

			count += item.getAmount();
			setItem(i, null);
		}

		for (ItemStack item : armor) {
			if (item == null) {
				continue;
			}
			if (id > -1 && item.getTypeId() != id) {
				continue;
			}
			if (data > -1 && item.getData().getData() != data) {
				continue;
			}

			count += item.getAmount();
			setItem(armorSlot++, null);
		}
		return count;
	}

	@Override
	public HumanEntity getHolder() {
		return (HumanEntity) inventory.getOwner();
	}

	@Override
	public float getItemInHandDropChance() {
		return 1;
	}

	@Override
	public void setItemInHandDropChance(float chance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getHelmetDropChance() {
		return 1;
	}

	@Override
	public void setHelmetDropChance(float chance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getChestplateDropChance() {
		return 1;
	}

	@Override
	public void setChestplateDropChance(float chance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getLeggingsDropChance() {
		return 1;
	}

	@Override
	public void setLeggingsDropChance(float chance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getBootsDropChance() {
		return 1;
	}

	@Override
	public void setBootsDropChance(float chance) {
		throw new UnsupportedOperationException();
	}
}
