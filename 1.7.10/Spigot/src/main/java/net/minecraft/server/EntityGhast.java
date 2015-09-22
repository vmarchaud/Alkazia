package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;

// CraftBukkit end

public class EntityGhast extends EntityFlying implements IMonster {

	public int h;
	public double i;
	public double bm;
	public double bn;
	private Entity target;
	private int br;
	public int bo;
	public int bp;
	private int explosionPower = 1;

	public EntityGhast(World world) {
		super(world);
		this.a(4.0F, 4.0F);
		fireProof = true;
		b = 5;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if ("fireball".equals(damagesource.p()) && damagesource.getEntity() instanceof EntityHuman) {
			super.damageEntity(damagesource, 1000.0F);
			((EntityHuman) damagesource.getEntity()).a(AchievementList.z);
			return true;
		} else
			return super.damageEntity(damagesource, f);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, Byte.valueOf((byte) 0));
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
	}

	@Override
	protected void bq() {
		if (!world.isStatic && world.difficulty == EnumDifficulty.PEACEFUL) {
			this.die();
		}

		w();
		bo = bp;
		double d0 = i - locX;
		double d1 = bm - locY;
		double d2 = bn - locZ;
		double d3 = d0 * d0 + d1 * d1 + d2 * d2;

		if (d3 < 1.0D || d3 > 3600.0D) {
			i = locX + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			bm = locY + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
			bn = locZ + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
		}

		if (h-- <= 0) {
			h += random.nextInt(5) + 2;
			d3 = MathHelper.sqrt(d3);
			if (this.a(i, bm, bn, d3)) {
				motX += d0 / d3 * 0.1D;
				motY += d1 / d3 * 0.1D;
				motZ += d2 / d3 * 0.1D;
			} else {
				i = locX;
				bm = locY;
				bn = locZ;
			}
		}

		if (target != null && target.dead) {
			// CraftBukkit start - fire EntityTargetEvent
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

		if (target == null || br-- <= 0) {
			// CraftBukkit start - fire EntityTargetEvent
			Entity target = world.findNearbyVulnerablePlayer(this, 100.0D);
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
				br = 20;
			}
		}

		double d4 = 64.0D;

		if (target != null && target.f(this) < d4 * d4) {
			double d5 = target.locX - locX;
			double d6 = target.boundingBox.b + target.length / 2.0F - (locY + length / 2.0F);
			double d7 = target.locZ - locZ;

			aM = yaw = -((float) Math.atan2(d5, d7)) * 180.0F / 3.1415927F;
			if (hasLineOfSight(target)) {
				if (bp == 10) {
					world.a((EntityHuman) null, 1007, (int) locX, (int) locY, (int) locZ, 0);
				}

				++bp;
				if (bp == 20) {
					world.a((EntityHuman) null, 1008, (int) locX, (int) locY, (int) locZ, 0);
					EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this, d5, d6, d7);

					// CraftBukkit - set bukkitYield when setting explosionpower
					entitylargefireball.bukkitYield = entitylargefireball.yield = explosionPower;
					double d8 = 4.0D;
					Vec3D vec3d = this.j(1.0F);

					entitylargefireball.locX = locX + vec3d.a * d8;
					entitylargefireball.locY = locY + length / 2.0F + 0.5D;
					entitylargefireball.locZ = locZ + vec3d.c * d8;
					world.addEntity(entitylargefireball);
					bp = -40;
				}
			} else if (bp > 0) {
				--bp;
			}
		} else {
			aM = yaw = -((float) Math.atan2(motX, motZ)) * 180.0F / 3.1415927F;
			if (bp > 0) {
				--bp;
			}
		}

		if (!world.isStatic) {
			byte b0 = datawatcher.getByte(16);
			byte b1 = (byte) (bp > 10 ? 1 : 0);

			if (b0 != b1) {
				datawatcher.watch(16, Byte.valueOf(b1));
			}
		}
	}

	private boolean a(double d0, double d1, double d2, double d3) {
		double d4 = (i - locX) / d3;
		double d5 = (bm - locY) / d3;
		double d6 = (bn - locZ) / d3;
		AxisAlignedBB axisalignedbb = boundingBox.clone();

		for (int i = 1; i < d3; ++i) {
			axisalignedbb.d(d4, d5, d6);
			if (!world.getCubes(this, axisalignedbb).isEmpty())
				return false;
		}

		return true;
	}

	@Override
	protected String t() {
		return "mob.ghast.moan";
	}

	@Override
	protected String aT() {
		return "mob.ghast.scream";
	}

	@Override
	protected String aU() {
		return "mob.ghast.death";
	}

	@Override
	protected Item getLoot() {
		return Items.SULPHUR;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(2) + random.nextInt(1 + i);

		int k;

		for (k = 0; k < j; ++k) {
			this.a(Items.GHAST_TEAR, 1);
		}

		j = random.nextInt(3) + random.nextInt(1 + i);

		for (k = 0; k < j; ++k) {
			this.a(Items.SULPHUR, 1);
		}
	}

	@Override
	protected float bf() {
		return 10.0F;
	}

	@Override
	public boolean canSpawn() {
		return random.nextInt(20) == 0 && super.canSpawn() && world.difficulty != EnumDifficulty.PEACEFUL;
	}

	@Override
	public int bB() {
		return 1;
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("ExplosionPower", explosionPower);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
			explosionPower = nbttagcompound.getInt("ExplosionPower");
		}
	}
}
