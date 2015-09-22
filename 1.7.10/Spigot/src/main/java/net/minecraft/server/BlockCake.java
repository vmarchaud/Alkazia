package net.minecraft.server;

import java.util.Random;

public class BlockCake extends Block {

	protected BlockCake() {
		super(Material.CAKE);
		this.a(true);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k);
		float f = 0.0625F;
		float f1 = (1 + l * 2) / 16.0F;
		float f2 = 0.5F;

		this.setBounds(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
	}

	@Override
	public void g() {
		float f = 0.0625F;
		float f1 = 0.5F;

		this.setBounds(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		int l = world.getData(i, j, k);
		float f = 0.0625F;
		float f1 = (1 + l * 2) / 16.0F;
		float f2 = 0.5F;

		return AxisAlignedBB.a(i + f1, j, k + f, i + 1 - f, j + f2 - f, k + 1 - f);
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
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		this.b(world, i, j, k, entityhuman);
		return true;
	}

	@Override
	public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
		this.b(world, i, j, k, entityhuman);
	}

	private void b(World world, int i, int j, int k, EntityHuman entityhuman) {
		if (entityhuman.g(false)) {
			// CraftBukkit start
			int oldFoodLevel = entityhuman.getFoodData().foodLevel;

			org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, 2 + oldFoodLevel);

			if (!event.isCancelled()) {
				entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
			}

			((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
			// CraftBukkit end
			int l = world.getData(i, j, k) + 1;

			if (l >= 6) {
				world.setAir(i, j, k);
			} else {
				world.setData(i, j, k, l, 2);
			}
		}
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		return !super.canPlace(world, i, j, k) ? false : this.j(world, i, j, k);
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		if (!this.j(world, i, j, k)) {
			world.setAir(i, j, k);
		}
	}

	@Override
	public boolean j(World world, int i, int j, int k) {
		return world.getType(i, j - 1, k).getMaterial().isBuildable();
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return null;
	}
}
