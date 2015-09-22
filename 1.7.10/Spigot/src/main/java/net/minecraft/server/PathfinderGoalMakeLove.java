package net.minecraft.server;

public class PathfinderGoalMakeLove extends PathfinderGoal {

	private EntityVillager b;
	private EntityVillager c;
	private World d;
	private int e;
	Village a;

	public PathfinderGoalMakeLove(EntityVillager entityvillager) {
		b = entityvillager;
		d = entityvillager.world;
		this.a(3);
	}

	@Override
	public boolean a() {
		if (b.getAge() != 0)
			return false;
		else if (b.aI().nextInt(500) != 0)
			return false;
		else {
			a = d.villages.getClosestVillage(MathHelper.floor(b.locX), MathHelper.floor(b.locY), MathHelper.floor(b.locZ), 0);
			if (a == null)
				return false;
			else if (!f())
				return false;
			else {
				Entity entity = d.a(EntityVillager.class, b.boundingBox.grow(8.0D, 3.0D, 8.0D), b);

				if (entity == null)
					return false;
				else {
					c = (EntityVillager) entity;
					return c.getAge() == 0;
				}
			}
		}
	}

	@Override
	public void c() {
		e = 300;
		b.i(true);
	}

	@Override
	public void d() {
		a = null;
		c = null;
		b.i(false);
	}

	@Override
	public boolean b() {
		return e >= 0 && f() && b.getAge() == 0;
	}

	@Override
	public void e() {
		--e;
		b.getControllerLook().a(c, 10.0F, 30.0F);
		if (b.f(c) > 2.25D) {
			b.getNavigation().a(c, 0.25D);
		} else if (e == 0 && c.ca()) {
			g();
		}

		if (b.aI().nextInt(35) == 0) {
			d.broadcastEntityEffect(b, (byte) 12);
		}
	}

	private boolean f() {
		if (!a.i())
			return false;
		else {
			int i = (int) (a.getDoorCount() * 0.35D);

			return a.getPopulationCount() < i;
		}
	}

	private void g() {
		EntityVillager entityvillager = b.b(c);

		c.setAge(6000);
		b.setAge(6000);
		entityvillager.setAge(-24000);
		entityvillager.setPositionRotation(b.locX, b.locY, b.locZ, 0.0F, 0.0F);
		d.addEntity(entityvillager, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.BREEDING); // CraftBukkit - added SpawnReason
		d.broadcastEntityEffect(entityvillager, (byte) 12);
	}
}
