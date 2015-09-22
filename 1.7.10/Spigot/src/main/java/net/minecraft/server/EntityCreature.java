package net.minecraft.server;

import java.util.UUID;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityUnleashEvent;

// CraftBukkit end

public abstract class EntityCreature extends EntityInsentient {

	public static final UUID h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
	public static final AttributeModifier i = new AttributeModifier(h, "Fleeing speed bonus", 2.0D, 2).a(false);
	public PathEntity pathEntity; // CraftBukkit - private -> public
	public Entity target; // CraftBukkit - protected -> public
	protected boolean bn;
	protected int bo;
	private ChunkCoordinates bq = new ChunkCoordinates(0, 0, 0);
	private float br = -1.0F;
	private PathfinderGoal bs = new PathfinderGoalMoveTowardsRestriction(this, 1.0D);
	private boolean bt;

	public EntityCreature(World world) {
		super(world);
	}

	protected boolean bP() {
		return false;
	}

	@Override
	protected void bq() {
		world.methodProfiler.a("ai");
		if (bo > 0 && --bo == 0) {
			AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

			attributeinstance.b(i);
		}

		bn = bP();
		float f11 = 16.0F;

		if (target == null) {
			// CraftBukkit start
			Entity target = findTarget();
			if (target != null) {
				EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(), target.getBukkitEntity(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
				world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					if (event.getTarget() == null) {
						this.target = null;
					} else {
						this.target = ((CraftEntity) event.getTarget()).getHandle();
					}
				}
			}
			// CraftBukkit end

			if (this.target != null) {
				pathEntity = world.findPath(this, this.target, f11, true, false, false, true);
			}
		} else if (target.isAlive()) {
			float f1 = target.e(this);

			if (hasLineOfSight(target)) {
				this.a(target, f1);
			}
		} else {
			// CraftBukkit start
			EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(), null, EntityTargetEvent.TargetReason.TARGET_DIED);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				if (event.getTarget() == null) {
					target = null;
				} else {
					target = ((CraftEntity) event.getTarget()).getHandle();
				}
			}
			// CraftBukkit end
		}

		if (target instanceof EntityPlayer && ((EntityPlayer) target).playerInteractManager.isCreative()) {
			target = null;
		}

		world.methodProfiler.b();
		if (!bn && target != null && (pathEntity == null || random.nextInt(20) == 0)) {
			pathEntity = world.findPath(this, target, f11, true, false, false, true);
		} else if (!bn && (pathEntity == null && random.nextInt(180) == 0 || random.nextInt(120) == 0 || bo > 0) && aU < 100) {
			bQ();
		}

		int i = MathHelper.floor(boundingBox.b + 0.5D);
		boolean flag = M();
		boolean flag1 = P();

		pitch = 0.0F;
		if (pathEntity != null && random.nextInt(100) != 0) {
			world.methodProfiler.a("followpath");
			Vec3D vec3d = pathEntity.a(this);
			double d0 = width * 2.0F;

			while (vec3d != null && vec3d.d(locX, vec3d.b, locZ) < d0 * d0) {
				pathEntity.a();
				if (pathEntity.b()) {
					vec3d = null;
					pathEntity = null;
				} else {
					vec3d = pathEntity.a(this);
				}
			}

			bc = false;
			if (vec3d != null) {
				double d1 = vec3d.a - locX;
				double d2 = vec3d.c - locZ;
				double d3 = vec3d.b - i;
				// CraftBukkit - Math -> TrigMath
				float f2 = (float) (org.bukkit.craftbukkit.TrigMath.atan2(d2, d1) * 180.0D / 3.1415927410125732D) - 90.0F;
				float f3 = MathHelper.g(f2 - yaw);

				be = (float) getAttributeInstance(GenericAttributes.d).getValue();
				if (f3 > 30.0F) {
					f3 = 30.0F;
				}

				if (f3 < -30.0F) {
					f3 = -30.0F;
				}

				yaw += f3;
				if (bn && target != null) {
					double d4 = target.locX - locX;
					double d5 = target.locZ - locZ;
					float f4 = yaw;

					yaw = (float) (Math.atan2(d5, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
					f3 = (f4 - yaw + 90.0F) * 3.1415927F / 180.0F;
					bd = -MathHelper.sin(f3) * be * 1.0F;
					be = MathHelper.cos(f3) * be * 1.0F;
				}

				if (d3 > 0.0D) {
					bc = true;
				}
			}

			if (target != null) {
				this.a(target, 30.0F, 30.0F);
			}

			if (positionChanged && !bS()) {
				bc = true;
			}

			if (random.nextFloat() < 0.8F && (flag || flag1)) {
				bc = true;
			}

			world.methodProfiler.b();
		} else {
			super.bq();
			pathEntity = null;
		}
	}

	protected void bQ() {
		world.methodProfiler.a("stroll");
		boolean flag = false;
		int i = -1;
		int j = -1;
		int k = -1;
		float f = -99999.0F;

		for (int l = 0; l < 10; ++l) {
			int i1 = MathHelper.floor(locX + random.nextInt(13) - 6.0D);
			int j1 = MathHelper.floor(locY + random.nextInt(7) - 3.0D);
			int k1 = MathHelper.floor(locZ + random.nextInt(13) - 6.0D);
			float f1 = this.a(i1, j1, k1);

			if (f1 > f) {
				f = f1;
				i = i1;
				j = j1;
				k = k1;
				flag = true;
			}
		}

		if (flag) {
			pathEntity = world.a(this, i, j, k, 10.0F, true, false, false, true);
		}

		world.methodProfiler.b();
	}

	protected void a(Entity entity, float f) {
	}

	public float a(int i, int j, int k) {
		return 0.0F;
	}

	protected Entity findTarget() {
		return null;
	}

	@Override
	public boolean canSpawn() {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(boundingBox.b);
		int k = MathHelper.floor(locZ);

		return super.canSpawn() && this.a(i, j, k) >= 0.0F;
	}

	public boolean bS() {
		return pathEntity != null;
	}

	public void setPathEntity(PathEntity pathentity) {
		pathEntity = pathentity;
	}

	public Entity bT() {
		return target;
	}

	public void setTarget(Entity entity) {
		target = entity;
	}

	public boolean bU() {
		return this.b(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ));
	}

	public boolean b(int i, int j, int k) {
		return br == -1.0F ? true : bq.e(i, j, k) < br * br;
	}

	public void a(int i, int j, int k, int l) {
		bq.b(i, j, k);
		br = l;
	}

	public ChunkCoordinates bV() {
		return bq;
	}

	public float bW() {
		return br;
	}

	public void bX() {
		br = -1.0F;
	}

	public boolean bY() {
		return br != -1.0F;
	}

	@Override
	protected void bL() {
		super.bL();
		if (bN() && getLeashHolder() != null && getLeashHolder().world == world) {
			Entity entity = getLeashHolder();

			this.a((int) entity.locX, (int) entity.locY, (int) entity.locZ, 5);
			float f = this.e(entity);

			if (this instanceof EntityTameableAnimal && ((EntityTameableAnimal) this).isSitting()) {
				if (f > 10.0F) {
					world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
					unleash(true, true);
				}

				return;
			}

			if (!bt) {
				goalSelector.a(2, bs);
				getNavigation().a(false);
				bt = true;
			}

			this.o(f);
			if (f > 4.0F) {
				getNavigation().a(entity, 1.0D);
			}

			if (f > 6.0F) {
				double d0 = (entity.locX - locX) / f;
				double d1 = (entity.locY - locY) / f;
				double d2 = (entity.locZ - locZ) / f;

				motX += d0 * Math.abs(d0) * 0.4D;
				motY += d1 * Math.abs(d1) * 0.4D;
				motZ += d2 * Math.abs(d2) * 0.4D;
			}

			if (f > 10.0F) {
				world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
				unleash(true, true);
			}
		} else if (!bN() && bt) {
			bt = false;
			goalSelector.a(bs);
			getNavigation().a(true);
			bX();
		}
	}

	protected void o(float f) {
	}
}
