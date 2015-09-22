package net.minecraft.server;

public class BlockAnvil extends BlockFalling {

	public static final String[] a = new String[] { "intact", "slightlyDamaged", "veryDamaged" };
	private static final String[] N = new String[] { "anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2" };

	protected BlockAnvil() {
		super(Material.HEAVY);
		this.g(0);
		this.a(CreativeModeTab.c);
	}

	// Spigot start
	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		updateShape(world, i, j, k);
		return super.a(world, i, j, k);
	}

	// Spigot end

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
		int l = MathHelper.floor(entityliving.yaw * 4.0F / 360.0F + 0.5D) & 3;
		int i1 = world.getData(i, j, k) >> 2;

		++l;
		l %= 4;
		if (l == 0) {
			world.setData(i, j, k, 2 | i1 << 2, 2);
		}

		if (l == 1) {
			world.setData(i, j, k, 3 | i1 << 2, 2);
		}

		if (l == 2) {
			world.setData(i, j, k, 0 | i1 << 2, 2);
		}

		if (l == 3) {
			world.setData(i, j, k, 1 | i1 << 2, 2);
		}
	}

	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		if (world.isStatic)
			return true;
		else {
			entityhuman.openAnvil(i, j, k);
			return true;
		}
	}

	@Override
	public int b() {
		return 35;
	}

	@Override
	public int getDropData(int i) {
		return i >> 2;
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k) & 3;

		if (l != 3 && l != 1) {
			this.setBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
		} else {
			this.setBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
		}
	}

	@Override
	protected void a(EntityFallingBlock entityfallingblock) {
		entityfallingblock.a(true);
	}

	@Override
	public void a(World world, int i, int j, int k, int l) {
		world.triggerEffect(1022, i, j, k, 0);
	}
}
