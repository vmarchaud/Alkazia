package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;

// CraftBukkit end

public class ShapedRecipes implements IRecipe {

	private int width;
	private int height;
	private ItemStack[] items;
	public ItemStack result; // Spigot
	private boolean e;

	public ShapedRecipes(int i, int j, ItemStack[] aitemstack, ItemStack itemstack) {
		width = i;
		height = j;
		items = aitemstack;
		result = itemstack;
	}

	// CraftBukkit start
	@Override
	public org.bukkit.inventory.ShapedRecipe toBukkitRecipe() {
		CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
		CraftShapedRecipe recipe = new CraftShapedRecipe(result, this);
		switch (height) {
		case 1:
			switch (width) {
			case 1:
				recipe.shape("a");
				break;
			case 2:
				recipe.shape("ab");
				break;
			case 3:
				recipe.shape("abc");
				break;
			}
			break;
		case 2:
			switch (width) {
			case 1:
				recipe.shape("a", "b");
				break;
			case 2:
				recipe.shape("ab", "cd");
				break;
			case 3:
				recipe.shape("abc", "def");
				break;
			}
			break;
		case 3:
			switch (width) {
			case 1:
				recipe.shape("a", "b", "c");
				break;
			case 2:
				recipe.shape("ab", "cd", "ef");
				break;
			case 3:
				recipe.shape("abc", "def", "ghi");
				break;
			}
			break;
		}
		char c = 'a';
		for (ItemStack stack : items) {
			if (stack != null) {
				recipe.setIngredient(c, org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(stack.getItem()), stack.getData());
			}
			c++;
		}
		return recipe;
	}

	// CraftBukkit end

	@Override
	public ItemStack b() {
		return result;
	}

	@Override
	public boolean a(InventoryCrafting inventorycrafting, World world) {
		for (int i = 0; i <= 3 - width; ++i) {
			for (int j = 0; j <= 3 - height; ++j) {
				if (this.a(inventorycrafting, i, j, true))
					return true;

				if (this.a(inventorycrafting, i, j, false))
					return true;
			}
		}

		return false;
	}

	private boolean a(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
		for (int k = 0; k < 3; ++k) {
			for (int l = 0; l < 3; ++l) {
				int i1 = k - i;
				int j1 = l - j;
				ItemStack itemstack = null;

				if (i1 >= 0 && j1 >= 0 && i1 < width && j1 < height) {
					if (flag) {
						itemstack = items[width - i1 - 1 + j1 * width];
					} else {
						itemstack = items[i1 + j1 * width];
					}
				}

				ItemStack itemstack1 = inventorycrafting.b(k, l);

				if (itemstack1 != null || itemstack != null) {
					if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
						return false;

					if (itemstack.getItem() != itemstack1.getItem())
						return false;

					if (itemstack.getData() != 32767 && itemstack.getData() != itemstack1.getData())
						return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack a(InventoryCrafting inventorycrafting) {
		ItemStack itemstack = b().cloneItemStack();

		if (e) {
			for (int i = 0; i < inventorycrafting.getSize(); ++i) {
				ItemStack itemstack1 = inventorycrafting.getItem(i);

				if (itemstack1 != null && itemstack1.hasTag()) {
					itemstack.setTag((NBTTagCompound) itemstack1.tag.clone());
				}
			}
		}

		return itemstack;
	}

	@Override
	public int a() {
		return width * height;
	}

	public ShapedRecipes c() {
		e = true;
		return this;
	}

	// Spigot start
	@Override
	public java.util.List<ItemStack> getIngredients() {
		return java.util.Arrays.asList(items);
	}
	// Spigot end
}
