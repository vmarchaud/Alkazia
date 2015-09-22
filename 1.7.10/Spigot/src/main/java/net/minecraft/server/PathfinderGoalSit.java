package net.minecraft.server;

public class PathfinderGoalSit extends PathfinderGoal {

	private EntityTameableAnimal entity;
	private boolean willSit;

	public PathfinderGoalSit(EntityTameableAnimal entitytameableanimal) {
		entity = entitytameableanimal;
		this.a(5);
	}

	@Override
	public boolean a() {
		if (!entity.isTamed())
			return willSit && entity.getGoalTarget() == null; // CraftBukkit - Allow sitting for wild animals
		else if (entity.M())
			return false;
		else if (!entity.onGround)
			return false;
		else {
			EntityLiving entityliving = entity.getOwner();

			return entityliving == null ? true : entity.f(entityliving) < 144.0D && entityliving.getLastDamager() != null ? false : willSit;
		}
	}

	@Override
	public void c() {
		entity.getNavigation().h();
		entity.setSitting(true);
	}

	@Override
	public void d() {
		entity.setSitting(false);
	}

	public void setSitting(boolean flag) {
		willSit = flag;
	}
}
