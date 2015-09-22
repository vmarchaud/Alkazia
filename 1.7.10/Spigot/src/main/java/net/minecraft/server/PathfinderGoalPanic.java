package net.minecraft.server;

public class PathfinderGoalPanic extends PathfinderGoal {

	private EntityCreature a;
	private double b;
	private double c;
	private double d;
	private double e;

	public PathfinderGoalPanic(EntityCreature entitycreature, double d0) {
		a = entitycreature;
		b = d0;
		this.a(1);
	}

	@Override
	public boolean a() {
		if (a.getLastDamager() == null && !a.isBurning())
			return false;
		else {
			Vec3D vec3d = RandomPositionGenerator.a(a, 5, 4);

			if (vec3d == null)
				return false;
			else {
				c = vec3d.a;
				d = vec3d.b;
				e = vec3d.c;
				return true;
			}
		}
	}

	@Override
	public void c() {
		a.getNavigation().a(c, d, e, b);
	}

	@Override
	public boolean b() {
		// CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
		if (a.ticksLived - a.aK() > 100) {
			a.b((EntityLiving) null);
			return false;
		}
		// CraftBukkit end
		return !a.getNavigation().g();
	}
}
