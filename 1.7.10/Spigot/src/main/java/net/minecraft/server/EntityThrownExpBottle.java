package net.minecraft.server;

public class EntityThrownExpBottle extends EntityProjectile {

	public EntityThrownExpBottle(World world) {
		super(world);
	}

	public EntityThrownExpBottle(World world, EntityLiving entityliving) {
		super(world, entityliving);
	}

	public EntityThrownExpBottle(World world, double d0, double d1, double d2) {
		super(world, d0, d1, d2);
	}

	@Override
	protected float i() {
		return 0.07F;
	}

	@Override
	protected float e() {
		return 0.7F;
	}

	@Override
	protected float f() {
		return -20.0F;
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (!world.isStatic) {
			// CraftBukkit - moved to after event
			// this.world.triggerEffect(2002, (int) Math.round(this.locX), (int) Math.round(this.locY), (int) Math.round(this.locZ), 0);
			int i = 3 + world.random.nextInt(5) + world.random.nextInt(5);

			// CraftBukkit start
			org.bukkit.event.entity.ExpBottleEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callExpBottleEvent(this, i);
			i = event.getExperience();
			if (event.getShowEffect()) {
				world.triggerEffect(2002, (int) Math.round(locX), (int) Math.round(locY), (int) Math.round(locZ), 0);
			}
			// CraftBukkit end

			while (i > 0) {
				int j = EntityExperienceOrb.getOrbValue(i);

				i -= j;
				world.addEntity(new EntityExperienceOrb(world, locX, locY, locZ, j));
			}

			die();
		}
	}
}
