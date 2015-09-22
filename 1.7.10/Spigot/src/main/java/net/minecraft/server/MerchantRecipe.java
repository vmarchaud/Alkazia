package net.minecraft.server;

public class MerchantRecipe {

	private ItemStack buyingItem1;
	private ItemStack buyingItem2;
	private ItemStack sellingItem;
	public int uses; // Spigot - protocol patch
	public int maxUses; // Spigot - protocol patch

	public MerchantRecipe(NBTTagCompound nbttagcompound) {
		this.a(nbttagcompound);
	}

	public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2) {
		buyingItem1 = itemstack;
		buyingItem2 = itemstack1;
		sellingItem = itemstack2;
		maxUses = 7;
	}

	public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1) {
		this(itemstack, (ItemStack) null, itemstack1);
	}

	public MerchantRecipe(ItemStack itemstack, Item item) {
		this(itemstack, new ItemStack(item));
	}

	public ItemStack getBuyItem1() {
		return buyingItem1;
	}

	public ItemStack getBuyItem2() {
		return buyingItem2;
	}

	public boolean hasSecondItem() {
		return buyingItem2 != null;
	}

	public ItemStack getBuyItem3() {
		return sellingItem;
	}

	public boolean a(MerchantRecipe merchantrecipe) {
		return buyingItem1.getItem() == merchantrecipe.buyingItem1.getItem() && sellingItem.getItem() == merchantrecipe.sellingItem.getItem() ? buyingItem2 == null && merchantrecipe.buyingItem2 == null || buyingItem2 != null && merchantrecipe.buyingItem2 != null && buyingItem2.getItem() == merchantrecipe.buyingItem2.getItem() : false;
	}

	public boolean b(MerchantRecipe merchantrecipe) {
		return this.a(merchantrecipe) && (buyingItem1.count < merchantrecipe.buyingItem1.count || buyingItem2 != null && buyingItem2.count < merchantrecipe.buyingItem2.count);
	}

	public void f() {
		++uses;
	}

	public void a(int i) {
		maxUses += i;
	}

	public boolean g() {
		return uses >= maxUses;
	}

	public void a(NBTTagCompound nbttagcompound) {
		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("buy");

		buyingItem1 = ItemStack.createStack(nbttagcompound1);
		NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("sell");

		sellingItem = ItemStack.createStack(nbttagcompound2);
		if (nbttagcompound.hasKeyOfType("buyB", 10)) {
			buyingItem2 = ItemStack.createStack(nbttagcompound.getCompound("buyB"));
		}

		if (nbttagcompound.hasKeyOfType("uses", 99)) {
			uses = nbttagcompound.getInt("uses");
		}

		if (nbttagcompound.hasKeyOfType("maxUses", 99)) {
			maxUses = nbttagcompound.getInt("maxUses");
		} else {
			maxUses = 7;
		}
	}

	public NBTTagCompound i() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		nbttagcompound.set("buy", buyingItem1.save(new NBTTagCompound()));
		nbttagcompound.set("sell", sellingItem.save(new NBTTagCompound()));
		if (buyingItem2 != null) {
			nbttagcompound.set("buyB", buyingItem2.save(new NBTTagCompound()));
		}

		nbttagcompound.setInt("uses", uses);
		nbttagcompound.setInt("maxUses", maxUses);
		return nbttagcompound;
	}
}
