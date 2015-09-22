package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class TileEntityChest extends TileEntity implements IInventory {

	private ItemStack[] items = new ItemStack[27]; // CraftBukkit - 36 -> 27
	public boolean a;
	public TileEntityChest i;
	public TileEntityChest j;
	public TileEntityChest k;
	public TileEntityChest l;
	public float m;
	public float n;
	public int o;
	private int ticks;
	private int r = -1;
	private String s;

	public TileEntityChest() {
	}

	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
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
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	@Override
	public int getSize() {
		return 27;
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
				update();
				return itemstack;
			} else {
				itemstack = items[i].a(j);
				if (items[i].count == 0) {
					items[i] = null;
				}

				update();
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

		update();
	}

	@Override
	public String getInventoryName() {
		return k_() ? s : "container.chest";
	}

	@Override
	public boolean k_() {
		return s != null && s.length() > 0;
	}

	public void a(String s) {
		this.s = s;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		items = new ItemStack[getSize()];
		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			s = nbttagcompound.getString("CustomName");
		}

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < items.length) {
				items[j] = ItemStack.createStack(nbttagcompound1);
			}
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
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
			nbttagcompound.setString("CustomName", s);
		}
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (world == null)
			return true; // CraftBukkit
		return world.getTileEntity(x, y, z) != this ? false : entityhuman.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
	}

	@Override
	public void u() {
		super.u();
		a = false;
	}

	private void a(TileEntityChest tileentitychest, int i) {
		if (tileentitychest.r()) {
			a = false;
		} else if (a) {
			switch (i) {
			case 0:
				if (l != tileentitychest) {
					a = false;
				}
				break;

			case 1:
				if (k != tileentitychest) {
					a = false;
				}
				break;

			case 2:
				if (this.i != tileentitychest) {
					a = false;
				}
				break;

			case 3:
				if (j != tileentitychest) {
					a = false;
				}
			}
		}
	}

	public void i() {
		if (!a) {
			a = true;
			i = null;
			j = null;
			k = null;
			l = null;
			if (this.a(x - 1, y, z)) {
				k = (TileEntityChest) world.getTileEntity(x - 1, y, z);
			}

			if (this.a(x + 1, y, z)) {
				j = (TileEntityChest) world.getTileEntity(x + 1, y, z);
			}

			if (this.a(x, y, z - 1)) {
				i = (TileEntityChest) world.getTileEntity(x, y, z - 1);
			}

			if (this.a(x, y, z + 1)) {
				l = (TileEntityChest) world.getTileEntity(x, y, z + 1);
			}

			if (i != null) {
				i.a(this, 0);
			}

			if (l != null) {
				l.a(this, 2);
			}

			if (j != null) {
				j.a(this, 1);
			}

			if (k != null) {
				k.a(this, 3);
			}
		}
	}

	private boolean a(int i, int j, int k) {
		if (world == null)
			return false;
		else {
			Block block = world.getType(i, j, k);

			return block instanceof BlockChest && ((BlockChest) block).a == j();
		}
	}

	@Override
	public void h() {
		super.h();
		if (world == null)
			return; // CraftBukkit
		i();
		++ticks;
		float f;

		if (!world.isStatic && o != 0 && (ticks + x + y + z) % 10 == 0) { // PaperSpigot Reduced 200 -> 10 interval due to reduced tick rate from Improved Tick Handling
			o = 0;
			f = 5.0F;
			List list = world.a(EntityHuman.class, AxisAlignedBB.a(x - f, y - f, z - f, x + 1 + f, y + 1 + f, z + 1 + f));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityHuman entityhuman = (EntityHuman) iterator.next();

				if (entityhuman.activeContainer instanceof ContainerChest) {
					IInventory iinventory = ((ContainerChest) entityhuman.activeContainer).e();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).a(this)) {
						++o;
					}
				}
			}
		}

		n = m;

		// PaperSpigot start - Move chest sound handling out of the tick loop
		/*
		f = 0.1F;
		double d0;

		if (this.o > 0 && this.m == 0.0F && this.i == null && this.k == null) {
		    double d1 = (double) this.x + 0.5D;

		    d0 = (double) this.z + 0.5D;
		    if (this.l != null) {
		        d0 += 0.5D;
		    }

		    if (this.j != null) {
		        d1 += 0.5D;
		    }

		    this.world.makeSound(d1, (double) this.y + 0.5D, d0, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (this.o == 0 && this.m > 0.0F || this.o > 0 && this.m < 1.0F) {
		    float f1 = this.m;

		    if (this.o > 0) {
		        this.m += f;
		    } else {
		        this.m -= f;
		    }

		    if (this.m > 1.0F) {
		        this.m = 1.0F;
		    }

		    float f2 = 0.5F;

		    if (this.m < f2 && f1 >= f2 && this.i == null && this.k == null) {
		        d0 = (double) this.x + 0.5D;
		        double d2 = (double) this.z + 0.5D;

		        if (this.l != null) {
		            d2 += 0.5D;
		        }

		        if (this.j != null) {
		            d0 += 0.5D;
		        }

		        this.world.makeSound(d0, (double) this.y + 0.5D, d2, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		    }

		    if (this.m < 0.0F) {
		        this.m = 0.0F;
		    }
		}
		*/
		// PaperSpigot end
	}

	@Override
	public boolean c(int i, int j) {
		if (i == 1) {
			o = j;
			return true;
		} else
			return super.c(i, j);
	}

	@Override
	public void startOpen() {
		if (o < 0) {
			o = 0;
		}

		int oldPower = Math.max(0, Math.min(15, o)); // CraftBukkit - Get power before new viewer is added

		++o;
		if (world == null)
			return; // CraftBukkit
		world.playBlockAction(x, y, z, q(), 1, o);

		// PaperSpigot start - Move chest open sound handling down to here
		i();
		double d0;

		if (o > 0 && m == 0.0F && i == null && k == null) {
			double d1 = x + 0.5D;

			d0 = z + 0.5D;
			if (l != null) {
				d0 += 0.5D;
			}

			if (j != null) {
				d1 += 0.5D;
			}

			world.makeSound(d1, y + 0.5D, d0, "random.chestopen", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}
		// PaperSpigot end

		// CraftBukkit start - Call redstone event
		if (q() == Blocks.TRAPPED_CHEST) {
			int newPower = Math.max(0, Math.min(15, o));

			if (oldPower != newPower) {
				org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, x, y, z, oldPower, newPower);
			}
		}
		// CraftBukkit end

		world.applyPhysics(x, y, z, q());
		world.applyPhysics(x, y - 1, z, q());
	}

	@Override
	public void closeContainer() {
		if (q() instanceof BlockChest) {
			int oldPower = Math.max(0, Math.min(15, o)); // CraftBukkit - Get power before new viewer is added

			--o;
			if (world == null)
				return; // CraftBukkit
			world.playBlockAction(x, y, z, q(), 1, o);

			// PaperSpigot start - Move chest close sound handling down to here
			i();
			double d0;

			if (o == 0 && i == null && k == null) {
				d0 = x + 0.5D;
				double d2 = z + 0.5D;

				if (l != null) {
					d2 += 0.5D;
				}

				if (j != null) {
					d0 += 0.5D;
				}

				world.makeSound(d0, y + 0.5D, d2, "random.chestclosed", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			}
			// PaperSpigot end

			// CraftBukkit start - Call redstone event
			if (q() == Blocks.TRAPPED_CHEST) {
				int newPower = Math.max(0, Math.min(15, o));

				if (oldPower != newPower) {
					org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, x, y, z, oldPower, newPower);
				}
			}
			// CraftBukkit end

			world.applyPhysics(x, y, z, q());
			world.applyPhysics(x, y - 1, z, q());
		}
	}

	@Override
	public boolean b(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void s() {
		super.s();
		u();
		i();
	}

	public int j() {
		if (r == -1) {
			if (world == null || !(q() instanceof BlockChest))
				return 0;

			r = ((BlockChest) q()).a;
		}

		return r;
	}
}
