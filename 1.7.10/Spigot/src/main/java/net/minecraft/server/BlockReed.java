package net.minecraft.server;

import java.util.Random;

public class BlockReed extends Block {

	protected BlockReed() {
		super(Material.PLANT);
		float f = 0.375F;

		this.setBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
		this.a(true);
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		if (world.getType(i, j - 1, k) == Blocks.SUGAR_CANE_BLOCK || e(world, i, j, k)) {
			if (world.isEmpty(i, j + 1, k)) {
				int l;

				for (l = 1; world.getType(i, j - l, k) == this; ++l) {
					;
				}

				if (l < world.paperSpigotConfig.reedMaxHeight) { // PaperSpigot - Configurable max growth height for reed blocks
					int i1 = world.getData(i, j, k);

					if (i1 >= (byte) range(3, world.growthOdds / world.spigotConfig.caneModifier * 15 + 0.5F, 15)) { // Spigot
						org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, i, j + 1, k, this, 0); // CraftBukkit
						world.setData(i, j, k, 0, 4);
					} else {
						world.setData(i, j, k, i1 + 1, 4);
					}
				}
			}
		}
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		Block block = world.getType(i, j - 1, k);

		return block == this ? true : block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND ? false : world.getType(i - 1, j - 1, k).getMaterial() == Material.WATER ? true : world.getType(i + 1, j - 1, k).getMaterial() == Material.WATER ? true : world.getType(i, j - 1, k - 1).getMaterial() == Material.WATER ? true : world.getType(i, j - 1, k + 1).getMaterial() == Material.WATER;
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		e(world, i, j, k);
	}

	protected final boolean e(World world, int i, int j, int k) {
		if (!this.j(world, i, j, k)) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
			return false;
		} else
			return true;
	}

	@Override
	public boolean j(World world, int i, int j, int k) {
		return this.canPlace(world, i, j, k);
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		return null;
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.SUGAR_CANE;
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
	public int b() {
		return 1;
	}
}
