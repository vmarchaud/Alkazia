package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

// CraftBukkit end

public class TileEntityBeacon extends TileEntity implements IInventory {

	public static final MobEffectList[][] a = new MobEffectList[][] { { MobEffectList.FASTER_MOVEMENT, MobEffectList.FASTER_DIG }, { MobEffectList.RESISTANCE, MobEffectList.JUMP }, { MobEffectList.INCREASE_DAMAGE }, { MobEffectList.REGENERATION } };
	private boolean k;
	private int l = -1;
	private int m;
	private int n;
	private ItemStack inventorySlot;
	private String p;
	// CraftBukkit start - add fields and methods
	public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
	private int maxStack = MAX_STACK;

	@Override
	public ItemStack[] getContents() {
		return new ItemStack[] { inventorySlot };
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

	public TileEntityBeacon() {
	}

	@Override
	public void h() {
		if (true || world.getTime() % 80L == 0L) { // PaperSpigot - controlled by Improved Tick handling
			y();
			x();
		}
	}

	private void x() {
		if (k && l > 0 && !world.isStatic && m > 0) {
			double d0 = l * 10 + 10;
			byte b0 = 0;

			if (l >= 4 && m == n) {
				b0 = 1;
			}

			AxisAlignedBB axisalignedbb = AxisAlignedBB.a(x, y, z, x + 1, y + 1, z + 1).grow(d0, d0, d0);

			axisalignedbb.e = world.getHeight();
			List list = world.a(EntityHuman.class, axisalignedbb);
			Iterator iterator = list.iterator();

			EntityHuman entityhuman;

			while (iterator.hasNext()) {
				entityhuman = (EntityHuman) iterator.next();
				entityhuman.addEffect(new MobEffect(m, 180, b0, true));
			}

			if (l >= 4 && m != n && n > 0) {
				iterator = list.iterator();

				while (iterator.hasNext()) {
					entityhuman = (EntityHuman) iterator.next();
					entityhuman.addEffect(new MobEffect(n, 180, 0, true));
				}
			}
		}
	}

	private void y() {
		int i = l;

		if (!world.i(x, y + 1, z)) {
			k = false;
			l = 0;
		} else {
			k = true;
			l = 0;

			for (int j = 1; j <= 4; l = j++) {
				int k = y - j;

				if (k < 0) {
					break;
				}

				boolean flag = true;

				for (int l = x - j; l <= x + j && flag; ++l) {
					for (int i1 = z - j; i1 <= z + j; ++i1) {
						Block block = world.getType(l, k, i1);

						if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
							flag = false;
							break;
						}
					}
				}

				if (!flag) {
					break;
				}
			}

			if (l == 0) {
				k = false;
			}
		}

		if (!world.isStatic && l == 4 && i < l) {
			Iterator iterator = world.a(EntityHuman.class, AxisAlignedBB.a(x, y, z, x, y - 4, z).grow(10.0D, 5.0D, 10.0D)).iterator();

			while (iterator.hasNext()) {
				EntityHuman entityhuman = (EntityHuman) iterator.next();

				entityhuman.a(AchievementList.K);
			}
		}
	}

	public int j() {
		return m;
	}

	public int k() {
		return n;
	}

	public int l() {
		return l;
	}

	public void d(int i) {
		m = 0;

		for (int j = 0; j < l && j < 3; ++j) {
			MobEffectList[] amobeffectlist = a[j];
			int k = amobeffectlist.length;

			for (int l = 0; l < k; ++l) {
				MobEffectList mobeffectlist = amobeffectlist[l];

				if (mobeffectlist.id == i) {
					m = i;
					return;
				}
			}
		}
	}

	public void e(int i) {
		n = 0;
		if (l >= 4) {
			for (int j = 0; j < 4; ++j) {
				MobEffectList[] amobeffectlist = a[j];
				int k = amobeffectlist.length;

				for (int l = 0; l < k; ++l) {
					MobEffectList mobeffectlist = amobeffectlist[l];

					if (mobeffectlist.id == i) {
						n = i;
						return;
					}
				}
			}
		}
	}

	@Override
	public Packet getUpdatePacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		this.b(nbttagcompound);
		return new PacketPlayOutTileEntityData(x, y, z, 3, nbttagcompound);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		m = nbttagcompound.getInt("Primary");
		n = nbttagcompound.getInt("Secondary");
		l = nbttagcompound.getInt("Levels");
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("Primary", m);
		nbttagcompound.setInt("Secondary", n);
		nbttagcompound.setInt("Levels", l);
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public ItemStack getItem(int i) {
		return i == 0 ? inventorySlot : null;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		if (i == 0 && inventorySlot != null) {
			if (j >= inventorySlot.count) {
				ItemStack itemstack = inventorySlot;

				inventorySlot = null;
				return itemstack;
			} else {
				inventorySlot.count -= j;
				return new ItemStack(inventorySlot.getItem(), j, inventorySlot.getData());
			}
		} else
			return null;
	}

	@Override
	public ItemStack splitWithoutUpdate(int i) {
		if (i == 0 && inventorySlot != null) {
			ItemStack itemstack = inventorySlot;

			inventorySlot = null;
			return itemstack;
		} else
			return null;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		if (i == 0) {
			inventorySlot = itemstack;
		}
	}

	@Override
	public String getInventoryName() {
		return k_() ? p : "container.beacon";
	}

	@Override
	public boolean k_() {
		return p != null && p.length() > 0;
	}

	public void a(String s) {
		p = s;
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
		return itemstack.getItem() == Items.EMERALD || itemstack.getItem() == Items.DIAMOND || itemstack.getItem() == Items.GOLD_INGOT || itemstack.getItem() == Items.IRON_INGOT;
	}
}
