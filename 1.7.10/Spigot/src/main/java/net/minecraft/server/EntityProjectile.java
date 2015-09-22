package net.minecraft.server;

import java.util.List;

public abstract class EntityProjectile extends Entity implements IProjectile {

	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	private Block inBlockId;
	protected boolean inGround;
	public int shake;
	public EntityLiving shooter; // CraftBukkit - private -> public
	public String shooterName; // CraftBukkit - private -> public
	private int i;
	private int at;

	public EntityProjectile(World world) {
		super(world);
		this.a(0.25F, 0.25F);
	}

	@Override
	protected void c() {
	}

	public EntityProjectile(World world, EntityLiving entityliving) {
		super(world);
		shooter = entityliving;
		projectileSource = (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
		this.a(0.25F, 0.25F);
		setPositionRotation(entityliving.locX, entityliving.locY + entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
		locX -= MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F;
		locY -= 0.10000000149011612D;
		locZ -= MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F;
		setPosition(locX, locY, locZ);
		height = 0.0F;
		float f = 0.4F;

		motX = -MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
		motZ = MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
		motY = -MathHelper.sin((pitch + this.f()) / 180.0F * 3.1415927F) * f;
		shoot(motX, motY, motZ, this.e(), 1.0F);
	}

	public EntityProjectile(World world, double d0, double d1, double d2) {
		super(world);
		i = 0;
		this.a(0.25F, 0.25F);
		setPosition(d0, d1, d2);
		height = 0.0F;
	}

	protected float e() {
		return 1.5F;
	}

	protected float f() {
		return 0.0F;
	}

	@Override
	public void shoot(double d0, double d1, double d2, float f, float f1) {
		float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

		d0 /= f2;
		d1 /= f2;
		d2 /= f2;
		d0 += random.nextGaussian() * 0.007499999832361937D * f1;
		d1 += random.nextGaussian() * 0.007499999832361937D * f1;
		d2 += random.nextGaussian() * 0.007499999832361937D * f1;
		d0 *= f;
		d1 *= f;
		d2 *= f;
		motX = d0;
		motY = d1;
		motZ = d2;
		float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

		lastYaw = yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
		lastPitch = pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
		i = 0;
	}

	@Override
	public void h() {
		S = locX;
		T = locY;
		U = locZ;
		super.h();
		if (shake > 0) {
			--shake;
		}

		if (inGround) {
			if (world.getType(blockX, blockY, blockZ) == inBlockId) {
				++i;
				if (i == 1200) {
					die();
				}

				return;
			}

			inGround = false;
			motX *= random.nextFloat() * 0.2F;
			motY *= random.nextFloat() * 0.2F;
			motZ *= random.nextFloat() * 0.2F;
			i = 0;
			at = 0;
		} else {
			++at;
		}

		Vec3D vec3d = Vec3D.a(locX, locY, locZ);
		Vec3D vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
		MovingObjectPosition movingobjectposition = world.a(vec3d, vec3d1);

		vec3d = Vec3D.a(locX, locY, locZ);
		vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
		if (movingobjectposition != null) {
			vec3d1 = Vec3D.a(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
		}

		if (!world.isStatic) {
			Entity entity = null;
			List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			EntityLiving entityliving = getShooter();

			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.R() && (entity1 != entityliving || at >= 5)) {
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
		}

		// PaperSpigot start - Allow projectiles to fly through players the shooter can't see
		if (movingobjectposition != null && movingobjectposition.entity instanceof EntityPlayer && shooter != null && shooter instanceof EntityPlayer) {
			if (!((EntityPlayer) shooter).getBukkitEntity().canSee(((EntityPlayer) movingobjectposition.entity).getBukkitEntity())) {
				movingobjectposition = null;
			}
		}
		// PaperSpigot end

		if (movingobjectposition != null) {
			if (movingobjectposition.type == EnumMovingObjectType.BLOCK && world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d) == Blocks.PORTAL) {
				ah();
			} else {
				this.a(movingobjectposition);
				// CraftBukkit start
				if (dead) {
					org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this);
				}
				// CraftBukkit end
			}
		}

		locX += motX;
		locY += motY;
		locZ += motZ;
		float f1 = MathHelper.sqrt(motX * motX + motZ * motZ);

		yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.1415927410125732D);

		for (pitch = (float) (Math.atan2(motY, f1) * 180.0D / 3.1415927410125732D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F) {
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
		float f2 = 0.99F;
		float f3 = this.i();

		if (M()) {
			for (int j = 0; j < 4; ++j) {
				float f4 = 0.25F;

				world.addParticle("bubble", locX - motX * f4, locY - motY * f4, locZ - motZ * f4, motX, motY, motZ);
			}

			f2 = 0.8F;
		}

		motX *= f2;
		motY *= f2;
		motZ *= f2;
		motY -= f3;
		setPosition(locX, locY, locZ);
	}

	protected float i() {
		return 0.03F;
	}

	protected abstract void a(MovingObjectPosition movingobjectposition);

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) blockX);
		nbttagcompound.setShort("yTile", (short) blockY);
		nbttagcompound.setShort("zTile", (short) blockZ);
		nbttagcompound.setByte("inTile", (byte) Block.getId(inBlockId));
		nbttagcompound.setByte("shake", (byte) shake);
		nbttagcompound.setByte("inGround", (byte) (inGround ? 1 : 0));
		if ((shooterName == null || shooterName.length() == 0) && shooter != null && shooter instanceof EntityHuman) {
			shooterName = shooter.getName();
		}

		nbttagcompound.setString("ownerName", shooterName == null ? "" : shooterName);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		blockX = nbttagcompound.getShort("xTile");
		blockY = nbttagcompound.getShort("yTile");
		blockZ = nbttagcompound.getShort("zTile");
		inBlockId = Block.getById(nbttagcompound.getByte("inTile") & 255);
		shake = nbttagcompound.getByte("shake") & 255;
		inGround = nbttagcompound.getByte("inGround") == 1;
		shooterName = nbttagcompound.getString("ownerName");
		if (shooterName != null && shooterName.length() == 0) {
			shooterName = null;
		}
	}

	public EntityLiving getShooter() {
		if (shooter == null && shooterName != null && shooterName.length() > 0) {
			shooter = world.a(shooterName);
		}

		return shooter;
	}
}
