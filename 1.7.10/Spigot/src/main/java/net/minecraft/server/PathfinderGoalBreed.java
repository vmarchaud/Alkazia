package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PathfinderGoalBreed extends PathfinderGoal {

	private EntityAnimal d;
	World a;
	private EntityAnimal e;
	int b;
	double c;

	public PathfinderGoalBreed(EntityAnimal entityanimal, double d0) {
		d = entityanimal;
		a = entityanimal.world;
		c = d0;
		this.a(3);
	}

	@Override
	public boolean a() {
		if (!d.ce())
			return false;
		else {
			e = f();
			return e != null;
		}
	}

	@Override
	public boolean b() {
		return e.isAlive() && e.ce() && b < 60;
	}

	@Override
	public void d() {
		e = null;
		b = 0;
	}

	@Override
	public void e() {
		d.getControllerLook().a(e, 10.0F, d.x());
		d.getNavigation().a(e, c);
		++b;
		if (b >= 60 && d.f(e) < 9.0D) {
			g();
		}
	}

	private EntityAnimal f() {
		float f = 8.0F;
		List list = a.a(d.getClass(), d.boundingBox.grow(f, f, f));
		double d0 = Double.MAX_VALUE;
		EntityAnimal entityanimal = null;
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			EntityAnimal entityanimal1 = (EntityAnimal) iterator.next();

			if (d.mate(entityanimal1) && d.f(entityanimal1) < d0) {
				entityanimal = entityanimal1;
				d0 = d.f(entityanimal1);
			}
		}

		return entityanimal;
	}

	private void g() {
		EntityAgeable entityageable = d.createChild(e);

		if (entityageable != null) {
			// CraftBukkit start - set persistence for tame animals
			if (entityageable instanceof EntityTameableAnimal && ((EntityTameableAnimal) entityageable).isTamed()) {
				entityageable.persistent = true;
			}
			// CraftBukkit end
			EntityHuman entityhuman = d.cd();

			if (entityhuman == null && e.cd() != null) {
				entityhuman = e.cd();
			}

			if (entityhuman != null) {
				entityhuman.a(StatisticList.x);
				if (d instanceof EntityCow) {
					entityhuman.a(AchievementList.H);
				}
			}

			d.setAge(6000);
			e.setAge(6000);
			d.cf();
			e.cf();
			entityageable.setAge(-24000);
			entityageable.setPositionRotation(d.locX, d.locY, d.locZ, 0.0F, 0.0F);
			a.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.BREEDING); // CraftBukkit - added SpawnReason
			Random random = d.aI();

			for (int i = 0; i < 7; ++i) {
				double d0 = random.nextGaussian() * 0.02D;
				double d1 = random.nextGaussian() * 0.02D;
				double d2 = random.nextGaussian() * 0.02D;

				a.addParticle("heart", d.locX + random.nextFloat() * d.width * 2.0F - d.width, d.locY + 0.5D + random.nextFloat() * d.length, d.locZ + random.nextFloat() * d.width * 2.0F - d.width, d0, d1, d2);
			}

			if (a.getGameRules().getBoolean("doMobLoot")) {
				a.addEntity(new EntityExperienceOrb(a, d.locX, d.locY, d.locZ, random.nextInt(7) + 1));
			}
		}
	}
}
