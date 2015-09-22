package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockSign extends BlockContainer {

	private Class a;
	private boolean b;

	protected BlockSign(Class oclass, boolean flag) {
		super(Material.WOOD);
		b = flag;
		a = oclass;
		float f = 0.25F;
		float f1 = 1.0F;

		this.setBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		return null;
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		if (!b) {
			int l = iblockaccess.getData(i, j, k);
			float f = 0.28125F;
			float f1 = 0.78125F;
			float f2 = 0.0F;
			float f3 = 1.0F;
			float f4 = 0.125F;

			this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			if (l == 2) {
				this.setBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
			}

			if (l == 3) {
				this.setBounds(f2, f, 0.0F, f3, f1, f4);
			}

			if (l == 4) {
				this.setBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
			}

			if (l == 5) {
				this.setBounds(0.0F, f, f2, f4, f1, f3);
			}
		}
	}

	@Override
	public int b() {
		return -1;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
		return true;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public TileEntity a(World world, int i) {
		try {
			return (TileEntity) a.newInstance();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.SIGN;
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		boolean flag = false;

		if (b) {
			if (!world.getType(i, j - 1, k).getMaterial().isBuildable()) {
				flag = true;
			}
		} else {
			int l = world.getData(i, j, k);

			flag = true;
			if (l == 2 && world.getType(i, j, k + 1).getMaterial().isBuildable()) {
				flag = false;
			}

			if (l == 3 && world.getType(i, j, k - 1).getMaterial().isBuildable()) {
				flag = false;
			}

			if (l == 4 && world.getType(i + 1, j, k).getMaterial().isBuildable()) {
				flag = false;
			}

			if (l == 5 && world.getType(i - 1, j, k).getMaterial().isBuildable()) {
				flag = false;
			}
		}

		if (flag) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
		}

		super.doPhysics(world, i, j, k, block);

		// CraftBukkit start
		if (block != null && block.isPowerSource()) {
			org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(i, j, k);
			int power = bukkitBlock.getBlockPower();

			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, power, power);
			world.getServer().getPluginManager().callEvent(eventRedstone);
		}
		// CraftBukkit end
	}
}
