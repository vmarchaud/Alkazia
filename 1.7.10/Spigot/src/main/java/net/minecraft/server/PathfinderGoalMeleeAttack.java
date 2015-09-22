package net.minecraft.server;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class PathfinderGoalMeleeAttack extends PathfinderGoal {

	World a;
	EntityCreature b;
	int c;
	double d;
	boolean e;
	PathEntity f;
	Class g;
	private int h;
	private double i;
	private double j;
	private double k;

	public PathfinderGoalMeleeAttack(EntityCreature entitycreature, Class oclass, double d0, boolean flag) {
		this(entitycreature, d0, flag);
		g = oclass;
	}

	public PathfinderGoalMeleeAttack(EntityCreature entitycreature, double d0, boolean flag) {
		b = entitycreature;
		a = entitycreature.world;
		d = d0;
		e = flag;
		this.a(3);
	}

	@Override
	public boolean a() {
		EntityLiving entityliving = b.getGoalTarget();

		if (entityliving == null)
			return false;
		else if (!entityliving.isAlive())
			return false;
		else if (g != null && !g.isAssignableFrom(entityliving.getClass()))
			return false;
		else {
			f = b.getNavigation().a(entityliving);
			return f != null;
		}
	}

	@Override
	public boolean b() {
		EntityLiving entityliving = b.getGoalTarget();

		// CraftBukkit start
		EntityTargetEvent.TargetReason reason = b.getGoalTarget() == null ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
		if (b.getGoalTarget() == null || b.getGoalTarget() != null && !b.getGoalTarget().isAlive()) {
			org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent(b, null, reason);
		}
		// CraftBukkit end

		return entityliving == null ? false : !entityliving.isAlive() ? false : !e ? !b.getNavigation().g() : b.b(MathHelper.floor(entityliving.locX), MathHelper.floor(entityliving.locY), MathHelper.floor(entityliving.locZ));
	}

	@Override
	public void c() {
		b.getNavigation().a(f, d);
		h = 0;
	}

	@Override
	public void d() {
		b.getNavigation().h();
	}

	@Override
	public void e() {
		EntityLiving entityliving = b.getGoalTarget();

		b.getControllerLook().a(entityliving, 30.0F, 30.0F);
		double d0 = b.e(entityliving.locX, entityliving.boundingBox.b, entityliving.locZ);
		double d1 = b.width * 2.0F * b.width * 2.0F + entityliving.width;

		--h;
		if ((e || b.getEntitySenses().canSee(entityliving)) && h <= 0 && (i == 0.0D && j == 0.0D && k == 0.0D || entityliving.e(i, j, k) >= 1.0D || b.aI().nextFloat() < 0.05F)) {
			i = entityliving.locX;
			j = entityliving.boundingBox.b;
			k = entityliving.locZ;
			h = 4 + b.aI().nextInt(7);
			if (d0 > 1024.0D) {
				h += 10;
			} else if (d0 > 256.0D) {
				h += 5;
			}

			if (!b.getNavigation().a(entityliving, d)) {
				h += 15;
			}
		}

		c = Math.max(c - 1, 0);
		if (d0 <= d1 && c <= 20) {
			c = 20;
			if (b.be() != null) {
				b.ba();
			}

			b.n(entityliving);
		}
	}
}
