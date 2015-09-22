package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.block.EntityBlockFormEvent;

// CraftBukkit end

public class EntitySnowman extends EntityGolem implements IRangedEntity {

	public EntitySnowman(World world) {
		super(world);
		this.a(0.4F, 1.8F);
		getNavigation().a(true);
		goalSelector.a(1, new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
		goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, true, false, IMonster.a));
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.20000000298023224D);
	}

	@Override
	public void e() {
		super.e();
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locY);
		int k = MathHelper.floor(locZ);

		if (L()) {
			damageEntity(DamageSource.DROWN, 1.0F);
		}

		if (world.getBiome(i, k).a(i, j, k) > 1.0F) {
			damageEntity(CraftEventFactory.MELTING, 1.0F); // CraftBukkit - DamageSource.BURN -> CraftEventFactory.MELTING
		}

		for (int l = 0; l < 4; ++l) {
			i = MathHelper.floor(locX + (l % 2 * 2 - 1) * 0.25F);
			j = MathHelper.floor(locY);
			k = MathHelper.floor(locZ + (l / 2 % 2 * 2 - 1) * 0.25F);
			if (world.getType(i, j, k).getMaterial() == Material.AIR && world.getBiome(i, k).a(i, j, k) < 0.8F && Blocks.SNOW.canPlace(world, i, j, k)) {
				// CraftBukkit start
				org.bukkit.block.BlockState blockState = world.getWorld().getBlockAt(i, j, k).getState();
				blockState.setType(CraftMagicNumbers.getMaterial(Blocks.SNOW));

				EntityBlockFormEvent event = new EntityBlockFormEvent(getBukkitEntity(), blockState.getBlock(), blockState);
				world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					blockState.update(true);
				}
				// CraftBukkit end
			}
		}
	}

	@Override
	protected Item getLoot() {
		return Items.SNOW_BALL;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(16);

		for (int k = 0; k < j; ++k) {
			this.a(Items.SNOW_BALL, 1);
		}
	}

	@Override
	public void a(EntityLiving entityliving, float f) {
		EntitySnowball entitysnowball = new EntitySnowball(world, this);
		double d0 = entityliving.locX - locX;
		double d1 = entityliving.locY + entityliving.getHeadHeight() - 1.100000023841858D - entitysnowball.locY;
		double d2 = entityliving.locZ - locZ;
		float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;

		entitysnowball.shoot(d0, d1 + f1, d2, 1.6F, 12.0F);
		makeSound("random.bow", 1.0F, 1.0F / (aI().nextFloat() * 0.4F + 0.8F));
		world.addEntity(entitysnowball);
	}
}
