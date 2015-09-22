package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public abstract class BlockPressurePlateAbstract extends Block {

	private String a;

	protected BlockPressurePlateAbstract(String s, Material material) {
		super(material);
		a = s;
		this.a(CreativeModeTab.d);
		this.a(true);
		this.b(this.d(15));
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		this.b(iblockaccess.getData(i, j, k));
	}

	protected void b(int i) {
		boolean flag = this.c(i) > 0;
		float f = 0.0625F;

		if (flag) {
			this.setBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
		} else {
			this.setBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
		}
	}

	@Override
	public int a(World world) {
		return 20;
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		return null;
	}

	@Override
	public boolean c() {
		return false;
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
	public boolean canPlace(World world, int i, int j, int k) {
		return World.a(world, i, j - 1, k) || BlockFence.a(world.getType(i, j - 1, k));
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		boolean flag = false;

		if (!World.a(world, i, j - 1, k) && !BlockFence.a(world.getType(i, j - 1, k))) {
			flag = true;
		}

		if (flag) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
		}
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		if (!world.isStatic) {
			int l = this.c(world.getData(i, j, k));

			if (l > 0) {
				this.a(world, i, j, k, l);
			}
		}
	}

	@Override
	public void a(World world, int i, int j, int k, Entity entity) {
		if (!world.isStatic) {
			int l = this.c(world.getData(i, j, k));

			if (l == 0) {
				this.a(world, i, j, k, l);
			}
		}
	}

	protected void a(World world, int i, int j, int k, int l) {
		int i1 = e(world, i, j, k);
		boolean flag = l > 0;
		boolean flag1 = i1 > 0;

		// CraftBukkit start - Interact Pressure Plate
		org.bukkit.World bworld = world.getWorld();
		org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();

		if (flag != flag1) {
			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bworld.getBlockAt(i, j, k), l, i1);
			manager.callEvent(eventRedstone);

			flag1 = eventRedstone.getNewCurrent() > 0;
			i1 = eventRedstone.getNewCurrent();
		}
		// CraftBukkit end

		if (l != i1) {
			world.setData(i, j, k, this.d(i1), 2);
			a_(world, i, j, k);
			world.c(i, j, k, i, j, k);
		}

		if (!flag1 && flag) {
			world.makeSound(i + 0.5D, j + 0.1D, k + 0.5D, "random.click", 0.3F, 0.5F);
		} else if (flag1 && !flag) {
			world.makeSound(i + 0.5D, j + 0.1D, k + 0.5D, "random.click", 0.3F, 0.6F);
		}

		if (flag1) {
			world.a(i, j, k, this, this.a(world));
		}
	}

	protected AxisAlignedBB a(int i, int j, int k) {
		float f = 0.125F;

		return AxisAlignedBB.a(i + f, j, k + f, i + 1 - f, j + 0.25D, k + 1 - f);
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		if (this.c(l) > 0) {
			a_(world, i, j, k);
		}

		super.remove(world, i, j, k, block, l);
	}

	protected void a_(World world, int i, int j, int k) {
		world.applyPhysics(i, j, k, this);
		world.applyPhysics(i, j - 1, k, this);
	}

	@Override
	public int b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return this.c(iblockaccess.getData(i, j, k));
	}

	@Override
	public int c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return l == 1 ? this.c(iblockaccess.getData(i, j, k)) : 0;
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public void g() {
		float f = 0.5F;
		float f1 = 0.125F;
		float f2 = 0.5F;

		this.setBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
	}

	@Override
	public int h() {
		return 1;
	}

	protected abstract int e(World world, int i, int j, int k);

	protected abstract int c(int i);

	protected abstract int d(int i);
}
