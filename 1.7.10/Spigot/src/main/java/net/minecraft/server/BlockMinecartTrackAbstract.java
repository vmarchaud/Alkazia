package net.minecraft.server;

import java.util.Random;

public abstract class BlockMinecartTrackAbstract extends Block {

	protected final boolean a;

	public static final boolean b_(World world, int i, int j, int k) {
		return a(world.getType(i, j, k));
	}

	public static final boolean a(Block block) {
		return block == Blocks.RAILS || block == Blocks.GOLDEN_RAIL || block == Blocks.DETECTOR_RAIL || block == Blocks.ACTIVATOR_RAIL;
	}

	protected BlockMinecartTrackAbstract(boolean flag) {
		super(Material.ORIENTABLE);
		a = flag;
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.a(CreativeModeTab.e);
	}

	public boolean e() {
		return a;
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
	public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
		updateShape(world, i, j, k);
		return super.a(world, i, j, k, vec3d, vec3d1);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k);

		if (l >= 2 && l <= 5) {
			this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
		} else {
			this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		}
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public int b() {
		return 9;
	}

	@Override
	public int a(Random random) {
		return 1;
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		return World.a(world, i, j - 1, k);
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
		if (!world.isStatic) {
			this.a(world, i, j, k, true);
			if (a) {
				doPhysics(world, i, j, k, this);
			}
		}
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		if (!world.isStatic) {
			int l = world.getData(i, j, k);
			int i1 = l;

			if (a) {
				i1 = l & 7;
			}

			boolean flag = false;

			if (!World.a(world, i, j - 1, k)) {
				flag = true;
			}

			if (i1 == 2 && !World.a(world, i + 1, j, k)) {
				flag = true;
			}

			if (i1 == 3 && !World.a(world, i - 1, j, k)) {
				flag = true;
			}

			if (i1 == 4 && !World.a(world, i, j, k - 1)) {
				flag = true;
			}

			if (i1 == 5 && !World.a(world, i, j, k + 1)) {
				flag = true;
			}

			if (flag) {
				// PaperSpigot start - Rails dupe workaround
				if (world.getType(i, j, k).getMaterial() != Material.AIR) {
					this.b(world, i, j, k, world.getData(i, j, k), 0);
					world.setAir(i, j, k);
				}
				// PaperSpigot end
			} else {
				this.a(world, i, j, k, l, i1, block);
			}
		}
	}

	protected void a(World world, int i, int j, int k, int l, int i1, Block block) {
	}

	protected void a(World world, int i, int j, int k, boolean flag) {
		if (!world.isStatic) {
			new MinecartTrackLogic(this, world, i, j, k).a(world.isBlockIndirectlyPowered(i, j, k), flag);
		}
	}

	@Override
	public int h() {
		return 0;
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		int i1 = l;

		if (a) {
			i1 = l & 7;
		}

		super.remove(world, i, j, k, block, l);
		if (i1 == 2 || i1 == 3 || i1 == 4 || i1 == 5) {
			world.applyPhysics(i, j + 1, k, block);
		}

		if (a) {
			world.applyPhysics(i, j, k, block);
			world.applyPhysics(i, j - 1, k, block);
		}
	}
}
