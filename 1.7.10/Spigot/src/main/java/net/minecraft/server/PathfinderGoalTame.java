package net.minecraft.server;

public class PathfinderGoalTame extends PathfinderGoal {

	private EntityHorse entity;
	private double b;
	private double c;
	private double d;
	private double e;

	public PathfinderGoalTame(EntityHorse entityhorse, double d0) {
		entity = entityhorse;
		b = d0;
		this.a(1);
	}

	@Override
	public boolean a() {
		if (!entity.isTame() && entity.passenger != null) {
			Vec3D vec3d = RandomPositionGenerator.a(entity, 5, 4);

			if (vec3d == null)
				return false;
			else {
				c = vec3d.a;
				d = vec3d.b;
				e = vec3d.c;
				return true;
			}
		} else
			return false;
	}

	@Override
	public void c() {
		entity.getNavigation().a(c, d, e, b);
	}

	@Override
	public boolean b() {
		return !entity.getNavigation().g() && entity.passenger != null;
	}

	@Override
	public void e() {
		if (entity.aI().nextInt(50) == 0) {
			if (entity.passenger instanceof EntityHuman) {
				int i = entity.getTemper();
				int j = entity.getMaxDomestication();

				// CraftBukkit - fire EntityTameEvent
				if (j > 0 && entity.aI().nextInt(j) < i && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTameEvent(entity, (EntityHuman) entity.passenger).isCancelled() && entity.passenger instanceof EntityHuman) {
					entity.h((EntityHuman) entity.passenger);
					entity.world.broadcastEntityEffect(entity, (byte) 7);
					return;
				}

				entity.v(5);
			}

			// CraftBukkit start - Handle dismounting to account for VehicleExitEvent being fired.
			if (entity.passenger != null) {
				entity.passenger.mount((Entity) null);
				// If the entity still has a passenger, then a plugin cancelled the event.
				if (entity.passenger != null)
					return;
			}
			// this.entity.passenger = null;
			// CraftBukkit end
			entity.cJ();
			entity.world.broadcastEntityEffect(entity, (byte) 6);
		}
	}
}
