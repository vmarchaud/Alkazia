package net.minecraft.server;

import java.util.Random;

public class BlockSnow extends Block {

	protected BlockSnow() {
		super(Material.PACKED_ICE);
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.a(true);
		this.a(CreativeModeTab.c);
		this.b(0);
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		int l = world.getData(i, j, k) & 7;
		float f = 0.125F;

		return AxisAlignedBB.a(i + minX, j + minY, k + minZ, i + maxX, j + l * f, k + maxZ);
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
	public void g() {
		this.b(0);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		this.b(iblockaccess.getData(i, j, k));
	}

	protected void b(int i) {
		int j = i & 7;
		float f = 2 * (1 + j) / 16.0F;

		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		Block block = world.getType(i, j - 1, k);

		return block != Blocks.ICE && block != Blocks.PACKED_ICE ? block.getMaterial() == Material.LEAVES ? true : block == this && (world.getData(i, j - 1, k) & 7) == 7 ? true : block.c() && block.material.isSolid() : false;
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		this.m(world, i, j, k);
	}

	private boolean m(World world, int i, int j, int k) {
		if (!this.canPlace(world, i, j, k)) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
			return false;
		} else
			return true;
	}

	@Override
	public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
		int i1 = l & 7;

		this.a(world, i, j, k, new ItemStack(Items.SNOW_BALL, i1 + 1, 0));
		world.setAir(i, j, k);
		entityhuman.a(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)], 1);
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.SNOW_BALL;
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		if (world.b(EnumSkyBlock.BLOCK, i, j, k) > 11) {
			// CraftBukkit start
			if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(i, j, k), Blocks.AIR).isCancelled())
				return;

			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
		}
	}
}
