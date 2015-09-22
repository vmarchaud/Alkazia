package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

// CraftBukkit end

public abstract class EntityMinecartContainer extends EntityMinecartAbstract implements IInventory {

	private ItemStack[] items = new ItemStack[27]; // CraftBukkit - 36 -> 27
	private boolean b = true;

	// CraftBukkit start
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
	public InventoryHolder getOwner() {
		org.bukkit.entity.Entity cart = getBukkitEntity();
		if (cart instanceof InventoryHolder)
			return (InventoryHolder) cart;
		return null;
	}

	@Override
	public void setMaxStackSize(int size) {
		maxStack = size;
	}

	// CraftBukkit end

	public EntityMinecartContainer(World world) {
		super(world);
	}

	public EntityMinecartContainer(World world, double d0, double d1, double d2) {
		super(world, d0, d1, d2);
	}

	@Override
	public void a(DamageSource damagesource) {
		super.a(damagesource);

		for (int i = 0; i < getSize(); ++i) {
			ItemStack itemstack = getItem(i);

			if (itemstack != null) {
				float f = random.nextFloat() * 0.8F + 0.1F;
				float f1 = random.nextFloat() * 0.8F + 0.1F;
				float f2 = random.nextFloat() * 0.8F + 0.1F;

				while (itemstack.count > 0) {
					int j = random.nextInt(21) + 10;

					if (j > itemstack.count) {
						j = itemstack.count;
					}

					itemstack.count -= j;
					EntityItem entityitem = new EntityItem(world, locX + f, locY + f1, locZ + f2, new ItemStack(itemstack.getItem(), j, itemstack.getData()));
					float f3 = 0.05F;

					entityitem.motX = (float) random.nextGaussian() * f3;
					entityitem.motY = (float) random.nextGaussian() * f3 + 0.2F;
					entityitem.motZ = (float) random.nextGaussian() * f3;
					world.addEntity(entityitem);
				}
			}
		}
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
	public void update() {
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		return dead ? false : entityhuman.f(this) <= 64.0D;
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
	public String getInventoryName() {
		return k_() ? u() : "container.minecart";
	}

	@Override
	public int getMaxStackSize() {
		return maxStack; // CraftBukkit
	}

	@Override
	public void b(int i) {
		// Spigot Start
		for (HumanEntity human : new java.util.ArrayList<HumanEntity>(transaction)) {
			human.closeInventory();
		}
		// Spigot End
		b = false;
		super.b(i);
	}

	@Override
	public void die() {
		if (b) {
			for (int i = 0; i < getSize(); ++i) {
				ItemStack itemstack = getItem(i);

				if (itemstack != null) {
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					float f2 = random.nextFloat() * 0.8F + 0.1F;

					while (itemstack.count > 0) {
						int j = random.nextInt(21) + 10;

						if (j > itemstack.count) {
							j = itemstack.count;
						}

						itemstack.count -= j;
						EntityItem entityitem = new EntityItem(world, locX + f, locY + f1, locZ + f2, new ItemStack(itemstack.getItem(), j, itemstack.getData()));

						if (itemstack.hasTag()) {
							entityitem.getItemStack().setTag((NBTTagCompound) itemstack.getTag().clone());
						}

						float f3 = 0.05F;

						entityitem.motX = (float) random.nextGaussian() * f3;
						entityitem.motY = (float) random.nextGaussian() * f3 + 0.2F;
						entityitem.motZ = (float) random.nextGaussian() * f3;
						world.addEntity(entityitem);
					}
				}
			}
		}

		super.die();
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
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
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

		items = new ItemStack[getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			int j = nbttagcompound1.getByte("Slot") & 255;

			if (j >= 0 && j < items.length) {
				items[j] = ItemStack.createStack(nbttagcompound1);
			}
		}
	}

	@Override
	public boolean c(EntityHuman entityhuman) {
		if (!world.isStatic) {
			entityhuman.openContainer(this);
		}

		return true;
	}

	@Override
	protected void i() {
		int i = 15 - Container.b(this);
		float f = 0.98F + i * 0.001F;

		motX *= f;
		motY *= 0.0D;
		motZ *= f;
	}
}
