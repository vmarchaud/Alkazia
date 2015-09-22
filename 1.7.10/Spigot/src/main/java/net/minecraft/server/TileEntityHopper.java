package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;

// CraftBukkit end

public class TileEntityHopper extends TileEntity implements IHopper {

	private ItemStack[] a = new ItemStack[5];
	private String i;
	private int j = -1;
    public static boolean isIronChest = false;

	// Spigot start
	private long nextTick = -1; // Next tick this hopper will be ticked.
	private long lastTick = -1; // Last tick this hopper was polled.

	// If this hopper is not cooling down, assaign a visible tick for next time.
	public void makeTick() {
		if (!j()) {
			this.c(0);
		}
	}

	// Contents changed, so make this hopper active.
	public void scheduleHopperTick() {
		if (world != null && world.spigotConfig.altHopperTicking) {
			makeTick();
		}
	}

	// Called after this hopper is assaigned a world or when altHopperTicking is turned
	// on from reload.
	public void convertToScheduling() {
		// j is the cooldown in ticks
		this.c(j);
	}

	// Called when alt hopper ticking is turned off from the reload command
	public void convertToPolling() {
		long cooldownDiff;
		if (lastTick == world.getTime()) {
			cooldownDiff = nextTick - world.getTime();
		} else {
			cooldownDiff = nextTick - world.getTime() + 1;
		}
		this.c((int) Math.max(0, Math.min(cooldownDiff, Integer.MAX_VALUE)));
	}

	// Spigot end

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return a;
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

	public TileEntityHopper() {
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		a = new ItemStack[getSize()];
		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			i = nbttagcompound.getString("CustomName");
		}

		j = nbttagcompound.getInt("TransferCooldown");

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < a.length) {
				a[b0] = ItemStack.createStack(nbttagcompound1);
			}
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < a.length; ++i) {
			if (a[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();

				nbttagcompound1.setByte("Slot", (byte) i);
				a[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}
		}

		nbttagcompound.set("Items", nbttaglist);
		// Spigot start - Need to write the correct cooldown to disk. We convert from long to int on saving.
		if (world != null && world.spigotConfig.altHopperTicking) {
			long cooldownDiff;
			if (lastTick == world.getTime()) {
				cooldownDiff = nextTick - world.getTime();
			} else {
				cooldownDiff = nextTick - world.getTime() + 1;
			}
			nbttagcompound.setInt("TransferCooldown", (int) Math.max(0, Math.min(cooldownDiff, Integer.MAX_VALUE)));
		} else {
			// j is the cooldown in ticks.
			nbttagcompound.setInt("TransferCooldown", j);
		}
		// Spigot end
		if (k_()) {
			nbttagcompound.setString("CustomName", i);
		}
	}

	@Override
	public void update() {
		super.update();
		// Spigot start - The contents have changed, so make this hopper active.
		scheduleHopperTick();
		// Spigot end
	}

	@Override
	public int getSize() {
		return a.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return a[i];
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (a[i] != null) {
			ItemStack itemstack;

			if (a[i].count <= j) {
				itemstack = a[i];
				a[i] = null;
				return itemstack;
			} else {
				itemstack = a[i].a(j);
				if (a[i].count == 0) {
					a[i] = null;
				}

				return itemstack;
			}
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (a[i] != null) {
			ItemStack itemstack = a[i];

			a[i] = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		a[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize()) {
			itemstack.count = getMaxStackSize();
		}
	}

	@Override
	public String getInventoryName() {
		return k_() ? i : "container.hopper";
	}

	@Override
	public boolean k_() {
		return i != null && i.length() > 0;
	}

	public void a(String s) {
		i = s;
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
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
		return true;
	}

	@Override
	public void h() {
		if (world != null && !world.isStatic) {
			// Spigot start
			if (world.spigotConfig.altHopperTicking) {
				lastTick = world.getTime();
				if (nextTick == world.getTime()) {
					// Method that does the pushing and pulling.
					i();
				}
			} else {
				--j;
				if (!j()) {
					this.c(0);
					i();
				}
			}
			// Spigot end
		}
	}

	public boolean i() {
		if (world != null && !world.isStatic) {
			if (!j() && BlockHopper.c(p())) {
				boolean flag = false;

				if (!k()) {
					flag = y();
				}

				if (!l()) {
					flag = suckInItems(this) || flag;
				}

				if (flag) {
					this.c(world.spigotConfig.hopperTransfer); // Spigot
					update();
					return true;
				}
			}

			// Spigot start
			if (!world.spigotConfig.altHopperTicking && !j()) {
				this.c(world.spigotConfig.hopperCheck);
			}
			// Spigot end
			return false;
		} else
			return false;
	}

	private boolean k() {
		ItemStack[] aitemstack = a;
		int i = aitemstack.length;

		for (int j = 0; j < i; ++j) {
			ItemStack itemstack = aitemstack[j];

			if (itemstack != null)
				return false;
		}

		return true;
	}

	private boolean l() {
		ItemStack[] aitemstack = a;
		int i = aitemstack.length;

		for (int j = 0; j < i; ++j) {
			ItemStack itemstack = aitemstack[j];

			if (itemstack == null || itemstack.count != itemstack.getMaxStackSize())
				return false;
		}

		return true;
	}

	private boolean y() {
		IInventory iinventory = z();

		if (iinventory == null)
			return false;
		else {
			int i = Facing.OPPOSITE_FACING[BlockHopper.b(p())];

			if (this.a(iinventory, i))
				return false;
			else {
				for (int j = 0; j < getSize(); ++j) {
					if (getItem(j) != null) {
						ItemStack itemstack = getItem(j).cloneItemStack();
						// CraftBukkit start - Call event when pushing items into other inventories
						CraftItemStack oitemstack = CraftItemStack.asCraftMirror(splitStack(j, world.spigotConfig.hopperAmount)); // Spigot

						Inventory destinationInventory;
						// Have to special case large chests as they work oddly
						if (iinventory instanceof InventoryLargeChest) {
							destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
						} else if(isIronChest) {
	                    	destinationInventory = null;
	                    } else {
							destinationInventory = iinventory.getOwner().getInventory();
						}

						InventoryMoveItemEvent event = new InventoryMoveItemEvent(getOwner().getInventory(), oitemstack.clone(), destinationInventory, true);
						getWorld().getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							setItem(j, itemstack);
							this.c(world.spigotConfig.hopperTransfer); // Spigot
							return false;
						}
						int origCount = event.getItem().getAmount(); // Spigot
						ItemStack itemstack1 = addItem(iinventory, CraftItemStack.asNMSCopy(event.getItem()), i);
						if (itemstack1 == null || itemstack1.count == 0) {
							if (event.getItem().equals(oitemstack)) {
								iinventory.update();
							} else {
								setItem(j, itemstack);
							}
							// CraftBukkit end
							return true;
						}
						itemstack.count -= origCount - itemstack1.count; // Spigot
						setItem(j, itemstack);
					}
				}

				return false;
			}
		}
	}

	private boolean a(IInventory iinventory, int i) {
		if (iinventory instanceof IWorldInventory && i > -1) {
			IWorldInventory iworldinventory = (IWorldInventory) iinventory;
			int[] aint = iworldinventory.getSlotsForFace(i);

			for (int j = 0; j < aint.length; ++j) {
				ItemStack itemstack = iworldinventory.getItem(aint[j]);

				if (itemstack == null || itemstack.count != itemstack.getMaxStackSize())
					return false;
			}
		} else {
			int k = iinventory.getSize();

			for (int l = 0; l < k; ++l) {
				ItemStack itemstack1 = iinventory.getItem(l);

				if (itemstack1 == null || itemstack1.count != itemstack1.getMaxStackSize())
					return false;
			}
		}

		return true;
	}

	private static boolean b(IInventory iinventory, int i) {
		if (iinventory instanceof IWorldInventory && i > -1) {
			IWorldInventory iworldinventory = (IWorldInventory) iinventory;
			int[] aint = iworldinventory.getSlotsForFace(i);

			for (int j = 0; j < aint.length; ++j) {
				if (iworldinventory.getItem(aint[j]) != null)
					return false;
			}
		} else {
			int k = iinventory.getSize();

			for (int l = 0; l < k; ++l) {
				if (iinventory.getItem(l) != null)
					return false;
			}
		}

		return true;
	}

	public static boolean suckInItems(IHopper ihopper) {
		IInventory iinventory = getSourceInventory(ihopper);

		if (iinventory != null) {
			byte b0 = 0;

			if (b(iinventory, b0))
				return false;

			if (iinventory instanceof IWorldInventory && b0 > -1) {
				IWorldInventory iworldinventory = (IWorldInventory) iinventory;
				int[] aint = iworldinventory.getSlotsForFace(b0);

				for (int i = 0; i < aint.length; ++i) {
					if (tryTakeInItemFromSlot(ihopper, iinventory, aint[i], b0))
						return true;
				}
			} else {
				int j = iinventory.getSize();

				for (int k = 0; k < j; ++k) {
					if (tryTakeInItemFromSlot(ihopper, iinventory, k, b0))
						return true;
				}
			}
		} else {
			EntityItem entityitem = getEntityItemAt(ihopper.getWorld(), ihopper.x(), ihopper.aD() + 1.0D, ihopper.aE());

			if (entityitem != null)
				return addEntityItem(ihopper, entityitem);
		}

		return false;
	}

	private static boolean tryTakeInItemFromSlot(IHopper ihopper, IInventory iinventory, int i, int j) {
		ItemStack itemstack = iinventory.getItem(i);

		if (itemstack != null && canTakeItemFromInventory(iinventory, itemstack, i, j)) {
			ItemStack itemstack1 = itemstack.cloneItemStack();
			// CraftBukkit start - Call event on collection of items from inventories into the hopper
			CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.splitStack(i, ihopper.getWorld().spigotConfig.hopperAmount)); // Spigot

			Inventory sourceInventory;
			// Have to special case large chests as they work oddly
			if (iinventory instanceof InventoryLargeChest) {
				sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
			} else if(isIronChest) {
            	sourceInventory = null;
            } else {
				sourceInventory = iinventory.getOwner().getInventory();
			}

			InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(), ihopper.getOwner().getInventory(), false);

			ihopper.getWorld().getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				iinventory.setItem(i, itemstack1);

				if (ihopper instanceof TileEntityHopper) {
					((TileEntityHopper) ihopper).c(ihopper.getWorld().spigotConfig.hopperTransfer); // Spigot
				} else if (ihopper instanceof EntityMinecartHopper) {
					((EntityMinecartHopper) ihopper).l(ihopper.getWorld().spigotConfig.hopperTransfer / 2); // Spigot
				}

				return false;
			}
			int origCount = event.getItem().getAmount(); // Spigot
			ItemStack itemstack2 = addItem(ihopper, CraftItemStack.asNMSCopy(event.getItem()), -1);

			if (itemstack2 == null || itemstack2.count == 0) {
				if (event.getItem().equals(oitemstack)) {
					iinventory.update();
				} else {
					iinventory.setItem(i, itemstack1);
				}
				// CraftBukkit end

				return true;
			}
			itemstack1.count -= origCount - itemstack2.count; // Spigot

			iinventory.setItem(i, itemstack1);
		}

		return false;
	}

	public static boolean addEntityItem(IInventory iinventory, EntityItem entityitem) {
		boolean flag = false;

		if (entityitem == null)
			return false;
		else {
			// CraftBukkit start
			InventoryPickupItemEvent event = new InventoryPickupItemEvent(iinventory.getOwner().getInventory(), (org.bukkit.entity.Item) entityitem.getBukkitEntity());
			entityitem.world.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled())
				return false;

			ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
			ItemStack itemstack1 = addItem(iinventory, itemstack, -1);

			if (itemstack1 != null && itemstack1.count != 0) {
				entityitem.setItemStack(itemstack1);
			} else {
				flag = true;
				entityitem.die();
			}

			return flag;
		}
	}

	public static ItemStack addItem(IInventory iinventory, ItemStack itemstack, int i) {
		if (iinventory instanceof IWorldInventory && i > -1) {
			IWorldInventory iworldinventory = (IWorldInventory) iinventory;
			int[] aint = iworldinventory.getSlotsForFace(i);

			for (int j = 0; j < aint.length && itemstack != null && itemstack.count > 0; ++j) {
				itemstack = tryMoveInItem(iinventory, itemstack, aint[j], i);
			}
		} else {
			int k = iinventory.getSize();

			for (int l = 0; l < k && itemstack != null && itemstack.count > 0; ++l) {
				itemstack = tryMoveInItem(iinventory, itemstack, l, i);
			}
		}

		if (itemstack != null && itemstack.count == 0) {
			itemstack = null;
		}

		return itemstack;
	}

	private static boolean canPlaceItemInInventory(IInventory iinventory, ItemStack itemstack, int i, int j) {
		return !iinventory.b(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, j);
	}

	private static boolean canTakeItemFromInventory(IInventory iinventory, ItemStack itemstack, int i, int j) {
		return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, j);
	}

	private static ItemStack tryMoveInItem(IInventory iinventory, ItemStack itemstack, int i, int j) {
		ItemStack itemstack1 = iinventory.getItem(i);

		if (canPlaceItemInInventory(iinventory, itemstack, i, j)) {
			boolean flag = false;

			if (itemstack1 == null) {
				iinventory.setItem(i, itemstack);
				itemstack = null;
				flag = true;
			} else if (canMergeItems(itemstack1, itemstack)) {
				int k = itemstack.getMaxStackSize() - itemstack1.count;
				int l = Math.min(itemstack.count, k);

				itemstack.count -= l;
				itemstack1.count += l;
				flag = l > 0;
			}

			if (flag) {
				if (iinventory instanceof TileEntityHopper) {
					((TileEntityHopper) iinventory).c(((TileEntityHopper) iinventory).world.spigotConfig.hopperTransfer); // Spigot
					iinventory.update();
				}

				iinventory.update();
			}
		}

		return itemstack;
	}

	private IInventory z() {
		int i = BlockHopper.b(p());

		return getInventoryAt(getWorld(), x + Facing.b[i], y + Facing.c[i], z + Facing.d[i]);
	}

	public static IInventory getSourceInventory(IHopper ihopper) {
		return getInventoryAt(ihopper.getWorld(), ihopper.x(), ihopper.aD() + 1.0D, ihopper.aE());
	}

	public static EntityItem getEntityItemAt(World world, double d0, double d1, double d2) {
		List list = world.a(EntityItem.class, AxisAlignedBB.a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.a);

		return list.size() > 0 ? (EntityItem) list.get(0) : null;
	}

	public static IInventory getInventoryAt(World world, double d0, double d1, double d2) {
		IInventory iinventory = null;
		int i = MathHelper.floor(d0);
		int j = MathHelper.floor(d1);
		int k = MathHelper.floor(d2);
		if (!world.isLoaded(i, j, k))
			return null; // Spigot
		TileEntity tileentity = world.getTileEntity(i, j, k);

		if (tileentity != null && tileentity instanceof IInventory) {
			iinventory = (IInventory) tileentity;
			if (iinventory instanceof TileEntityChest) {
				Block block = world.getType(i, j, k);

				if (block instanceof BlockChest) {
					iinventory = ((BlockChest) block).m(world, i, j, k);
				}
			}
			
			 else if(iinventory instanceof TileEntityIronChest) {
	            	isIronChest = true;
	            	// alkazia fix for iron chest
	            }
		}

		if (iinventory == null) {
			List list = world.getEntities((Entity) null, AxisAlignedBB.a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.c);

			if (list != null && list.size() > 0) {
				iinventory = (IInventory) list.get(world.random.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	private static boolean canMergeItems(ItemStack itemstack, ItemStack itemstack1) {
		return itemstack.getItem() != itemstack1.getItem() ? false : itemstack.getData() != itemstack1.getData() ? false : itemstack.count > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1);
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double aD() {
		return y;
	}

	@Override
	public double aE() {
		return z;
	}

	public void c(int i) {
		// Spigot start - i is the delay for which this hopper will be ticked next.
		// i of 1 or below implies a tick next tick.
		if (world != null && world.spigotConfig.altHopperTicking) {
			if (i <= 0) {
				i = 1;
			}
			if (lastTick == world.getTime()) {
				nextTick = world.getTime() + i;
			} else {
				nextTick = world.getTime() + i - 1;
			}
		} else {
			j = i;
		}
		// Spigot end
	}

	public boolean j() {
		// Spigot start - Return whether this hopper is cooling down.
		if (world != null && world.spigotConfig.altHopperTicking) {
			if (lastTick == world.getTime())
				return nextTick > world.getTime();
				else
					return nextTick >= world.getTime();
		} else
			return j > 0;
	}
}
