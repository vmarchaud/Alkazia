package net.minecraft.server;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class PathfinderGoalArrowAttack extends PathfinderGoal {

	private final EntityInsentient a;
	private final IRangedEntity b;
	private EntityLiving c;
	private int d;
	private double e;
	private int f;
	private int g;
	private int h;
	private float i;
	private float j;

	public PathfinderGoalArrowAttack(IRangedEntity irangedentity, double d0, int i, float f) {
		this(irangedentity, d0, i, i, f);
	}

	public PathfinderGoalArrowAttack(IRangedEntity irangedentity, double d0, int i, int j, float f) {
		d = -1;
		if (!(irangedentity instanceof EntityLiving))
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		else {
			b = irangedentity;
			a = (EntityInsentient) irangedentity;
			e = d0;
			g = i;
			h = j;
			this.i = f;
			this.j = f * f;
			this.a(3);
		}
	}

	@Override
	public boolean a() {
		EntityLiving entityliving = a.getGoalTarget();

		if (entityliving == null)
			return false;
		else {
			c = entityliving;
			return true;
		}
	}

	@Override
	public boolean b() {
		return this.a() || !a.getNavigation().g();
	}

	@Override
	public void d() {
		// CraftBukkit start
		EntityTargetEvent.TargetReason reason = c.isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
		org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent((Entity) b, null, reason);
		// CraftBukkit end
		c = null;
		f = 0;
		d = -1;
	}

	@Override
	public void e() {
		double d0 = a.e(c.locX, c.boundingBox.b, c.locZ);
		boolean flag = a.getEntitySenses().canSee(c);

		if (flag) {
			++f;
		} else {
			f = 0;
		}

		if (d0 <= j && f >= 20) {
			a.getNavigation().h();
		} else {
			a.getNavigation().a(c, e);
		}

		a.getControllerLook().a(c, 30.0F, 30.0F);
		float f;

		if (--d == 0) {
			if (d0 > j || !flag)
				return;

			f = MathHelper.sqrt(d0) / i;
			float f1 = f;

			if (f < 0.1F) {
				f1 = 0.1F;
			}

			if (f1 > 1.0F) {
				f1 = 1.0F;
			}

			b.a(c, f1);
			d = MathHelper.d(f * (h - g) + g);
		} else if (d < 0) {
			f = MathHelper.sqrt(d0) / i;
			d = MathHelper.d(f * (h - g) + g);
		}
	}
}
