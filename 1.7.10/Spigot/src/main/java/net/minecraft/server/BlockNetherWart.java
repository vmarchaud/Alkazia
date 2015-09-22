package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockPlant {

	protected BlockNetherWart() {
		this.a(true);
		float f = 0.5F;

		this.setBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		this.a((CreativeModeTab) null);
	}

	@Override
	protected boolean a(Block block) {
		return block == Blocks.SOUL_SAND;
	}

	@Override
	public boolean j(World world, int i, int j, int k) {
		return this.a(world.getType(i, j - 1, k));
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		int l = world.getData(i, j, k);

		if (l < 3 && random.nextInt(10) == 0) {
			++l;
			org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, i, j, k, this, l); // CraftBukkit
		}

		super.a(world, i, j, k, random);
	}

	@Override
	public int b() {
		return 6;
	}

	@Override
	public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
		if (!world.isStatic) {
			int j1 = 1;

			if (l >= 3) {
				j1 = 2 + world.random.nextInt(3);
				if (i1 > 0) {
					j1 += world.random.nextInt(i1 + 1);
				}
			}

			for (int k1 = 0; k1 < j1; ++k1) {
				this.a(world, i, j, k, new ItemStack(Items.NETHER_STALK));
			}
		}
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return null;
	}

	@Override
	public int a(Random random) {
		return 0;
	}
}
