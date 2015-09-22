package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
// CraftBukkit end
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class TileEntityFurnace extends TileEntity implements IWorldInventory {

	private static final int[] k = new int[] { 0 };
	private static final int[] l = new int[] { 2, 1 };
	private static final int[] m = new int[] { 1 };
	private ItemStack[] items = new ItemStack[3];
	public int burnTime;
	public int ticksForCurrentFuel;
	public int cookTime;
	private String o;

	// CraftBukkit start - add fields and methods
	private int lastTick = MinecraftServer.currentTick;
	private int maxStack = MAX_STACK;
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

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
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	public TileEntityFurnace() {
	}

	@Override
	public int getSize() {
		return items.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return items[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (items[i] != null) {
			ItemStack itemstack;

			if (items[i].count <= j) {
				itemstack = items[i];
				items[i] = null;
				return itemstack;
			} else {
				itemstack = items[i].a(j);
				if (items[i].count == 0) {
					items[i] = null;
				}

				return itemstack;
			}
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (items[i] != null) {
			ItemStack itemstack = items[i];

			items[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		items[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize()) {
			itemstack.count = getMaxStackSize();
		}
	}

	@Override
	public String getInventoryName() {
		return k_() ? o : "container.furnace";
	}

	@Override
	public boolean k_() {
		return o != null && o.length() > 0;
	}

	public void a(String s) {
		o = s;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		items = new ItemStack[getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < items.length) {
				items[b0] = ItemStack.createStack(nbttagcompound1);
			}
		}

		burnTime = nbttagcompound.getShort("BurnTime");
		cookTime = nbttagcompound.getShort("CookTime");
		ticksForCurrentFuel = fuelTime(items[1]);
		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			o = nbttagcompound.getString("CustomName");
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("BurnTime", (short) burnTime);
		nbttagcompound.setShort("CookTime", (short) cookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();

				nbttagcompound1.setByte("Slot", (byte) i);
				items[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}
		}

		nbttagcompound.set("Items", nbttaglist);
		if (k_()) {
			nbttagcompound.setString("CustomName", o);
		}
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	public boolean isBurning() {
		return burnTime > 0;
	}

	@Override
	public void h() {
		boolean flag = burnTime > 0;
		boolean flag1 = false;

		// CraftBukkit start - Use wall time instead of ticks for cooking
		int elapsedTicks = MinecraftServer.currentTick - lastTick;
		lastTick = MinecraftServer.currentTick;

		// CraftBukkit - moved from below
		if (isBurning() && canBurn()) {
			cookTime += elapsedTicks;
			if (cookTime >= 200) {
				cookTime %= 200;
				burn();
				flag1 = true;
			}
		} else {
			cookTime = 0;
		}
		// CraftBukkit end

		if (burnTime > 0) {
			burnTime -= elapsedTicks; // CraftBukkit - use elapsedTicks in place of constant
		}

		if (!world.isStatic) {
			if (burnTime != 0 || items[1] != null && items[0] != null) {
				// CraftBukkit start - Handle multiple elapsed ticks
				if (burnTime <= 0 && canBurn()) { // CraftBukkit - == to <=
					CraftItemStack fuel = CraftItemStack.asCraftMirror(items[1]);

					FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(world.getWorld().getBlockAt(x, y, z), fuel, fuelTime(items[1]));
					world.getServer().getPluginManager().callEvent(furnaceBurnEvent);

					if (furnaceBurnEvent.isCancelled())
						return;

					ticksForCurrentFuel = furnaceBurnEvent.getBurnTime();
					burnTime += ticksForCurrentFuel;
					if (burnTime > 0 && furnaceBurnEvent.isBurning()) {
						// CraftBukkit end
						flag1 = true;
						if (items[1] != null) {
							--items[1].count;
							if (items[1].count == 0) {
								Item item = items[1].getItem().t();

								items[1] = item != null ? new ItemStack(item) : null;
							}
						}
					}
				}

				/* CraftBukkit start - Moved up
				if (this.isBurning() && this.canBurn()) {
				    ++this.cookTime;
				    if (this.cookTime == 200) {
				        this.cookTime = 0;
				        this.burn();
				        flag1 = true;
				    }
				} else {
				    this.cookTime = 0;
				}
				*/
			}

			if (flag != burnTime > 0) {
				flag1 = true;
				BlockFurnace.a(burnTime > 0, world, x, y, z);
			}
		}

		if (flag1) {
			update();
		}
	}

	private boolean canBurn() {
		if (items[0] == null)
			return false;
		else {
			ItemStack itemstack = RecipesFurnace.getInstance().getResult(items[0]);

			// CraftBukkit - consider resultant count instead of current count
			return itemstack == null ? false : items[2] == null ? true : !items[2].doMaterialsMatch(itemstack) ? false : items[2].count + itemstack.count <= getMaxStackSize() && items[2].count < items[2].getMaxStackSize() ? true : items[2].count + itemstack.count <= itemstack.getMaxStackSize();
		}
	}

	public void burn() {
		if (canBurn()) {
			ItemStack itemstack = RecipesFurnace.getInstance().getResult(items[0]);

			// CraftBukkit start - fire FurnaceSmeltEvent
			CraftItemStack source = CraftItemStack.asCraftMirror(items[0]);
			org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack);

			FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(world.getWorld().getBlockAt(x, y, z), source, result);
			world.getServer().getPluginManager().callEvent(furnaceSmeltEvent);

			if (furnaceSmeltEvent.isCancelled())
				return;

			result = furnaceSmeltEvent.getResult();
			itemstack = CraftItemStack.asNMSCopy(result);

			if (itemstack != null) {
				if (items[2] == null) {
					items[2] = itemstack;
				} else if (CraftItemStack.asCraftMirror(items[2]).isSimilar(result)) {
					items[2].count += itemstack.count;
				} else
					return;
			}
			// CraftBukkit end

			--items[0].count;
			if (items[0].count <= 0) {
				items[0] = null;
			}
		}
	}

	public static int fuelTime(ItemStack itemstack) {
		if (itemstack == null)
			return 0;
		else {
			Item item = itemstack.getItem();

			if (item instanceof ItemBlock && Block.setSound(item) != Blocks.AIR) {
				Block block = Block.setSound(item);

				if (block == Blocks.WOOD_STEP)
					return 150;

				if (block.getMaterial() == Material.WOOD)
					return 300;

				if (block == Blocks.COAL_BLOCK)
					return 16000;
			}

			return item instanceof ItemTool && ((ItemTool) item).j().equals("WOOD") ? 200 : item instanceof ItemSword && ((ItemSword) item).j().equals("WOOD") ? 200 : item instanceof ItemHoe && ((ItemHoe) item).i().equals("WOOD") ? 200 : item == Items.STICK ? 100 : item == Items.COAL ? 1600 : item == Items.LAVA_BUCKET ? 20000 : item == Item.getItemOf(Blocks.SAPLING) ? 100
					: item == Items.BLAZE_ROD ? 2400 : 0;
		}
	}

	public static boolean isFuel(ItemStack itemstack) {
		return fuelTime(itemstack) > 0;
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return world.getTileEntity(x, y, z) != this ? false : entityhuman.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
	}

	@Override
	public void startOpen() {
	}

	@Override
	public void closeContainer() {
	}

	@Override
	public boolean b(int i, ItemStack itemstack) {
		return i == 2 ? false : i == 1 ? isFuel(itemstack) : true;
	}

	@Override
	public int[] getSlotsForFace(int i) {
		return i == 0 ? l : i == 1 ? k : m;
	}

	@Override
	public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, int j) {
		return this.b(i, itemstack);
	}

	@Override
	public boolean canTakeItemThroughFace(int i, ItemStack itemstack, int j) {
		return j != 0 || i != 1 || itemstack.getItem() == Items.BUCKET;
	}
}
