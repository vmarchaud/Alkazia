package net.minecraft.server;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class PathfinderGoalOcelotAttack extends PathfinderGoal {

	World a;
	EntityInsentient b;
	EntityLiving c;
	int d;

	public PathfinderGoalOcelotAttack(EntityInsentient entityinsentient) {
		b = entityinsentient;
		a = entityinsentient.world;
		this.a(3);
	}

	@Override
	public boolean a() {
		EntityLiving entityliving = b.getGoalTarget();

		if (entityliving == null)
			return false;
		else {
			c = entityliving;
			return true;
		}
	}

	@Override
	public boolean b() {
		return !c.isAlive() ? false : b.f(c) > 225.0D ? false : !b.getNavigation().g() || this.a();
	}

	@Override
	public void d() {
		// CraftBukkit start
		EntityTargetEvent.TargetReason reason = c.isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
		org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent(c, null, reason);
		// CraftBukkit end
		c = null;
		b.getNavigation().h();
	}

	@Override
	public void e() {
		b.getControllerLook().a(c, 30.0F, 30.0F);
		double d0 = b.width * 2.0F * b.width * 2.0F;
		double d1 = b.e(c.locX, c.boundingBox.b, c.locZ);
		double d2 = 0.8D;

		if (d1 > d0 && d1 < 16.0D) {
			d2 = 1.33D;
		} else if (d1 < 225.0D) {
			d2 = 0.6D;
		}

		b.getNavigation().a(c, d2);
		d = Math.max(d - 1, 0);
		if (d1 <= d0) {
			if (d <= 0) {
				d = 20;
				b.n(c);
			}
		}
	}
}
