package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockCactus extends Block {

	protected BlockCactus() {
		super(Material.CACTUS);
		this.a(true);
		this.a(CreativeModeTab.c);
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		if (world.isEmpty(i, j + 1, k)) {
			int l;

			for (l = 1; world.getType(i, j - l, k) == this; ++l) {
				;
			}

			if (l < world.paperSpigotConfig.cactusMaxHeight) { // PaperSpigot - Configurable max growth height for cactus blocks
				int i1 = world.getData(i, j, k);

				if (i1 >= (byte) range(3, world.growthOdds / world.spigotConfig.cactusModifier * 15 + 0.5F, 15)) { // Spigot
					CraftEventFactory.handleBlockGrowEvent(world, i, j + 1, k, this, 0); // CraftBukkit
					world.setData(i, j, k, 0, 4);
					doPhysics(world, i, j + 1, k, this);
				} else {
					world.setData(i, j, k, i1 + 1, 4);
				}
			}
		}
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		float f = 0.0625F;

		return AxisAlignedBB.a(i + f, j, k + f, i + 1 - f, j + 1 - f, k + 1 - f);
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public int b() {
		return 13;
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		return !super.canPlace(world, i, j, k) ? false : this.j(world, i, j, k);
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		if (!this.j(world, i, j, k)) {
			world.setAir(i, j, k, true);
		}
	}

	@Override
	public boolean j(World world, int i, int j, int k) {
		if (world.getType(i - 1, j, k).getMaterial().isBuildable())
			return false;
		else if (world.getType(i + 1, j, k).getMaterial().isBuildable())
			return false;
		else if (world.getType(i, j, k - 1).getMaterial().isBuildable())
			return false;
		else if (world.getType(i, j, k + 1).getMaterial().isBuildable())
			return false;
		else {
			Block block = world.getType(i, j - 1, k);

			return block == Blocks.CACTUS || block == Blocks.SAND;
		}
	}

	@Override
	public void a(World world, int i, int j, int k, Entity entity) {
		CraftEventFactory.blockDamage = world.getWorld().getBlockAt(i, j, k); // CraftBukkit
		entity.damageEntity(DamageSource.CACTUS, 1.0F);
		CraftEventFactory.blockDamage = null; // CraftBukkit
	}
}
