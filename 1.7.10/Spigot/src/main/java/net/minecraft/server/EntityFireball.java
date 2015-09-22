package net.minecraft.server;

import java.util.List;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public abstract class EntityFireball extends Entity {

	private int e = -1;
	private int f = -1;
	private int g = -1;
	private Block h;
	private boolean i;
	public EntityLiving shooter;
	private int at;
	private int au;
	public double dirX;
	public double dirY;
	public double dirZ;
	public float bukkitYield = 1; // CraftBukkit
	public boolean isIncendiary = true; // CraftBukkit

	public EntityFireball(World world) {
		super(world);
		this.a(1.0F, 1.0F);
	}

	@Override
	protected void c() {
	}

	public EntityFireball(World world, double d0, double d1, double d2, double d3, double d4, double d5) {
		super(world);
		this.a(1.0F, 1.0F);
		setPositionRotation(d0, d1, d2, yaw, pitch);
		setPosition(d0, d1, d2);
		double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

		dirX = d3 / d6 * 0.1D;
		dirY = d4 / d6 * 0.1D;
		dirZ = d5 / d6 * 0.1D;
	}

	public EntityFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
		super(world);
		shooter = entityliving;
		projectileSource = (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
		this.a(1.0F, 1.0F);
		setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
		setPosition(locX, locY, locZ);
		height = 0.0F;
		motX = motY = motZ = 0.0D;
		// CraftBukkit start - Added setDirection method
		setDirection(d0, d1, d2);
	}

	public void setDirection(double d0, double d1, double d2) {
		// CraftBukkit end
		d0 += random.nextGaussian() * 0.4D;
		d1 += random.nextGaussian() * 0.4D;
		d2 += random.nextGaussian() * 0.4D;
		double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

		dirX = d0 / d3 * 0.1D;
		dirY = d1 / d3 * 0.1D;
		dirZ = d2 / d3 * 0.1D;
	}

	@Override
	public void h() {
		if (!world.isStatic && (shooter != null && shooter.dead || !world.isLoaded((int) locX, (int) locY, (int) locZ))) {
			die();
		} else {
			super.h();
			setOnFire(1);
			if (i) {
				if (world.getType(e, f, g) == h) {
					++at;
					if (at == 600) {
						die();
					}

					return;
				}

				i = false;
				motX *= random.nextFloat() * 0.2F;
				motY *= random.nextFloat() * 0.2F;
				motZ *= random.nextFloat() * 0.2F;
				at = 0;
				au = 0;
			} else {
				++au;
			}

			Vec3D vec3d = Vec3D.a(locX, locY, locZ);
			Vec3D vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition movingobjectposition = world.a(vec3d, vec3d1);

			vec3d = Vec3D.a(locX, locY, locZ);
			vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			if (movingobjectposition != null) {
				vec3d1 = Vec3D.a(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
			}

			Entity entity = null;
			List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;

			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.R() && (!entity1.i(shooter) || au >= 25)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(f, f, f);
					MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);

					if (movingobjectposition1 != null) {
						double d1 = vec3d.distanceSquared(movingobjectposition1.pos); // CraftBukkit - distance efficiency

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if (movingobjectposition != null) {
				this.a(movingobjectposition);

				// CraftBukkit start - Fire ProjectileHitEvent
				if (dead) {
					CraftEventFactory.callProjectileHitEvent(this);
				}
				// CraftBukkit end
			}

			locX += motX;
			locY += motY;
			locZ += motZ;
			float f1 = MathHelper.sqrt(motX * motX + motZ * motZ);

			yaw = (float) (Math.atan2(motZ, motX) * 180.0D / 3.1415927410125732D) + 90.0F;

			for (pitch = (float) (Math.atan2(f1, motY) * 180.0D / 3.1415927410125732D) - 90.0F; pitch - lastPitch < -180.0F; lastPitch -= 360.0F) {
				;
			}

			while (pitch - lastPitch >= 180.0F) {
				lastPitch += 360.0F;
			}

			while (yaw - lastYaw < -180.0F) {
				lastYaw -= 360.0F;
			}

			while (yaw - lastYaw >= 180.0F) {
				lastYaw += 360.0F;
			}

			pitch = lastPitch + (pitch - lastPitch) * 0.2F;
			yaw = lastYaw + (yaw - lastYaw) * 0.2F;
			float f2 = this.e();

			if (M()) {
				for (int j = 0; j < 4; ++j) {
					float f3 = 0.25F;

					world.addParticle("bubble", locX - motX * f3, locY - motY * f3, locZ - motZ * f3, motX, motY, motZ);
				}

				f2 = 0.8F;
			}

			motX += dirX;
			motY += dirY;
			motZ += dirZ;
			motX *= f2;
			motY *= f2;
			motZ *= f2;
			world.addParticle("smoke", locX, locY + 0.5D, locZ, 0.0D, 0.0D, 0.0D);
			setPosition(locX, locY, locZ);
		}
	}

	protected float e() {
		return 0.95F;
	}

	protected abstract void a(MovingObjectPosition movingobjectposition);

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) e);
		nbttagcompound.setShort("yTile", (short) f);
		nbttagcompound.setShort("zTile", (short) g);
		nbttagcompound.setByte("inTile", (byte) Block.getId(h));
		nbttagcompound.setByte("inGround", (byte) (i ? 1 : 0));
		// CraftBukkit - Fix direction being mismapped to invalid variables
		nbttagcompound.set("power", this.a(new double[] { dirX, dirY, dirZ }));
		// Spigot - Support vanilla's direction tag
		nbttagcompound.set("direction", this.a(new double[] { motX, motY, motZ }));
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		e = nbttagcompound.getShort("xTile");
		f = nbttagcompound.getShort("yTile");
		g = nbttagcompound.getShort("zTile");
		h = Block.getById(nbttagcompound.getByte("inTile") & 255);
		i = nbttagcompound.getByte("inGround") == 1;
		// CraftBukkit start - direction -> power
		if (nbttagcompound.hasKeyOfType("power", 9)) {
			NBTTagList nbttaglist = nbttagcompound.getList("power", 6);

			dirX = nbttaglist.d(0);
			dirY = nbttaglist.d(1);
			dirZ = nbttaglist.d(2);
			// CraftBukkit end
		} else if (nbttagcompound.hasKeyOfType("direction", 9)) { // Spigot - Support vanilla's direction tag
			NBTTagList nbttaglist = nbttagcompound.getList("direction", 6);

			motX = nbttaglist.d(0);
			motY = nbttaglist.d(1);
			motZ = nbttaglist.d(2);

		} else {
			die();
		}
	}

	@Override
	public boolean R() {
		return true;
	}

	@Override
	public float af() {
		return 1.0F;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			Q();
			if (damagesource.getEntity() != null) {
				// CraftBukkit start
				if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f))
					return false;

				Vec3D vec3d = damagesource.getEntity().ag();

				if (vec3d != null) {
					motX = vec3d.a;
					motY = vec3d.b;
					motZ = vec3d.c;
					dirX = motX * 0.1D;
					dirY = motY * 0.1D;
					dirZ = motZ * 0.1D;
				}

				if (damagesource.getEntity() instanceof EntityLiving) {
					shooter = (EntityLiving) damagesource.getEntity();
					projectileSource = (org.bukkit.projectiles.ProjectileSource) shooter.getBukkitEntity();
				}

				return true;
			} else
				return false;
		}
	}

	@Override
	public float d(float f) {
		return 1.0F;
	}
}
