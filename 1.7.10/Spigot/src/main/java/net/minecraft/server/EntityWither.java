package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

// CraftBukkit end

public class EntityWither extends EntityMonster implements IRangedEntity {

	private float[] bp = new float[2];
	private float[] bq = new float[2];
	private float[] br = new float[2];
	private float[] bs = new float[2];
	private int[] bt = new int[2];
	private int[] bu = new int[2];
	private int bv;
	private static final IEntitySelector bw = new EntitySelectorNotUndead();

	public EntityWither(World world) {
		super(world);
		setHealth(getMaxHealth());
		this.a(0.9F, 4.0F);
		fireProof = true;
		getNavigation().e(true);
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
		goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, false, bw));
		b = 50;
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(17, new Integer(0));
		datawatcher.a(18, new Integer(0));
		datawatcher.a(19, new Integer(0));
		datawatcher.a(20, new Integer(0));
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("Invul", ca());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.s(nbttagcompound.getInt("Invul"));
	}

	@Override
	protected String t() {
		return "mob.wither.idle";
	}

	@Override
	protected String aT() {
		return "mob.wither.hurt";
	}

	@Override
	protected String aU() {
		return "mob.wither.death";
	}

	@Override
	public void e() {
		motY *= 0.6000000238418579D;
		double d0;
		double d1;
		double d2;

		if (!world.isStatic && this.t(0) > 0) {
			Entity entity = world.getEntity(this.t(0));

			if (entity != null) {
				if (locY < entity.locY || !cb() && locY < entity.locY + 5.0D) {
					if (motY < 0.0D) {
						motY = 0.0D;
					}

					motY += (0.5D - motY) * 0.6000000238418579D;
				}

				double d3 = entity.locX - locX;

				d0 = entity.locZ - locZ;
				d1 = d3 * d3 + d0 * d0;
				if (d1 > 9.0D) {
					d2 = MathHelper.sqrt(d1);
					motX += (d3 / d2 * 0.5D - motX) * 0.6000000238418579D;
					motZ += (d0 / d2 * 0.5D - motZ) * 0.6000000238418579D;
				}
			}
		}

		if (motX * motX + motZ * motZ > 0.05000000074505806D) {
			yaw = (float) Math.atan2(motZ, motX) * 57.295776F - 90.0F;
		}

		super.e();

		int i;

		for (i = 0; i < 2; ++i) {
			bs[i] = bq[i];
			br[i] = bp[i];
		}

		int j;

		for (i = 0; i < 2; ++i) {
			j = this.t(i + 1);
			Entity entity1 = null;

			if (j > 0) {
				entity1 = world.getEntity(j);
			}

			if (entity1 != null) {
				d0 = u(i + 1);
				d1 = v(i + 1);
				d2 = this.w(i + 1);
				double d4 = entity1.locX - d0;
				double d5 = entity1.locY + entity1.getHeadHeight() - d1;
				double d6 = entity1.locZ - d2;
				double d7 = MathHelper.sqrt(d4 * d4 + d6 * d6);
				float f = (float) (Math.atan2(d6, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
				float f1 = (float) -(Math.atan2(d5, d7) * 180.0D / 3.1415927410125732D);

				bp[i] = this.b(bp[i], f1, 40.0F);
				bq[i] = this.b(bq[i], f, 10.0F);
			} else {
				bq[i] = this.b(bq[i], aM, 10.0F);
			}
		}

		boolean flag = cb();

		for (j = 0; j < 3; ++j) {
			double d8 = u(j);
			double d9 = v(j);
			double d10 = this.w(j);

			world.addParticle("smoke", d8 + random.nextGaussian() * 0.30000001192092896D, d9 + random.nextGaussian() * 0.30000001192092896D, d10 + random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
			if (flag && world.random.nextInt(4) == 0) {
				world.addParticle("mobSpell", d8 + random.nextGaussian() * 0.30000001192092896D, d9 + random.nextGaussian() * 0.30000001192092896D, d10 + random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
			}
		}

		if (ca() > 0) {
			for (j = 0; j < 3; ++j) {
				world.addParticle("mobSpell", locX + random.nextGaussian() * 1.0D, locY + random.nextFloat() * 3.3F, locZ + random.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
			}
		}
	}

	@Override
	protected void bn() {
		int i;

		if (ca() > 0) {
			i = ca() - 1;
			if (i <= 0) {
				// CraftBukkit start
				ExplosionPrimeEvent event = new ExplosionPrimeEvent(getBukkitEntity(), 7.0F, false);
				world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					world.createExplosion(this, locX, locY + getHeadHeight(), locZ, event.getRadius(), event.getFire(), world.getGameRules().getBoolean("mobGriefing"));
				}
				// CraftBukkit end

				world.createExplosion(this, locX, locY + getHeadHeight(), locZ, 7.0F, false, world.getGameRules().getBoolean("mobGriefing"));
				// CraftBukkit start - Use relative location for far away sounds
				//this.world.b(1013, (int) this.locX, (int) this.locY, (int) this.locZ, 0);
				int viewDistance = ((WorldServer) world).getServer().getViewDistance() * 16;
				for (EntityPlayer player : (List<EntityPlayer>) world.players) {
					double deltaX = locX - player.locX;
					double deltaZ = locZ - player.locZ;
					double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
					if (world.spigotConfig.witherSpawnSoundRadius > 0 && distanceSquared > world.spigotConfig.witherSpawnSoundRadius * world.spigotConfig.witherSpawnSoundRadius) {
						continue; // Spigot
					}
					if (distanceSquared > viewDistance * viewDistance) {
						double deltaLength = Math.sqrt(distanceSquared);
						double relativeX = player.locX + deltaX / deltaLength * viewDistance;
						double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
						player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1013, (int) relativeX, (int) locY, (int) relativeZ, 0, true));
					} else {
						player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1013, (int) locX, (int) locY, (int) locZ, 0, true));
					}
				}
				// CraftBukkit end
			}

			this.s(i);
			if (ticksLived % 10 == 0) {
				this.heal(10.0F, EntityRegainHealthEvent.RegainReason.WITHER_SPAWN); // CraftBukkit
			}
		} else {
			super.bn();

			int j;

			for (i = 1; i < 3; ++i) {
				if (ticksLived >= bt[i - 1]) {
					bt[i - 1] = ticksLived + 10 + random.nextInt(10);
					if (world.difficulty == EnumDifficulty.NORMAL || world.difficulty == EnumDifficulty.HARD) {
						int i1001 = i - 1;
						int i1003 = bu[i - 1];

						bu[i1001] = bu[i - 1] + 1;
						if (i1003 > 15) {
							float f = 10.0F;
							float f1 = 5.0F;
							double d0 = MathHelper.a(random, locX - f, locX + f);
							double d1 = MathHelper.a(random, locY - f1, locY + f1);
							double d2 = MathHelper.a(random, locZ - f, locZ + f);

							this.a(i + 1, d0, d1, d2, true);
							bu[i - 1] = 0;
						}
					}

					j = this.t(i);
					if (j > 0) {
						Entity entity = world.getEntity(j);

						if (entity != null && entity.isAlive() && this.f(entity) <= 900.0D && hasLineOfSight(entity)) {
							this.a(i + 1, (EntityLiving) entity);
							bt[i - 1] = ticksLived + 40 + random.nextInt(20);
							bu[i - 1] = 0;
						} else {
							this.b(i, 0);
						}
					} else {
						List list = world.a(EntityLiving.class, boundingBox.grow(20.0D, 8.0D, 20.0D), bw);

						for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
							EntityLiving entityliving = (EntityLiving) list.get(random.nextInt(list.size()));

							if (entityliving != this && entityliving.isAlive() && hasLineOfSight(entityliving)) {
								if (entityliving instanceof EntityHuman) {
									if (!((EntityHuman) entityliving).abilities.isInvulnerable) {
										this.b(i, entityliving.getId());
									}
								} else {
									this.b(i, entityliving.getId());
								}
								break;
							}

							list.remove(entityliving);
						}
					}
				}
			}

			if (getGoalTarget() != null) {
				this.b(0, getGoalTarget().getId());
			} else {
				this.b(0, 0);
			}

			if (bv > 0) {
				--bv;
				if (bv == 0 && world.getGameRules().getBoolean("mobGriefing")) {
					i = MathHelper.floor(locY);
					j = MathHelper.floor(locX);
					int j1 = MathHelper.floor(locZ);
					boolean flag = false;

					for (int k1 = -1; k1 <= 1; ++k1) {
						for (int l1 = -1; l1 <= 1; ++l1) {
							for (int i2 = 0; i2 <= 3; ++i2) {
								int j2 = j + k1;
								int k2 = i + i2;
								int l2 = j1 + l1;
								Block block = world.getType(j2, k2, l2);

								if (block.getMaterial() != Material.AIR && block != Blocks.BEDROCK && block != Blocks.ENDER_PORTAL && block != Blocks.ENDER_PORTAL_FRAME && block != Blocks.COMMAND) {
									// CraftBukkit start
									if (CraftEventFactory.callEntityChangeBlockEvent(this, j2, k2, l2, Blocks.AIR, 0).isCancelled()) {
										continue;
									}
									// CraftBukkit end

									flag = world.setAir(j2, k2, l2, true) || flag;
								}
							}
						}
					}

					if (flag) {
						world.a((EntityHuman) null, 1012, (int) locX, (int) locY, (int) locZ, 0);
					}
				}
			}

			if (ticksLived % 20 == 0) {
				this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN); // CraftBukkit
			}
		}
	}

	public void bZ() {
		this.s(220);
		setHealth(getMaxHealth() / 3.0F);
	}

	@Override
	public void as() {
	}

	@Override
	public int aV() {
		return 4;
	}

	private double u(int i) {
		if (i <= 0)
			return locX;
		else {
			float f = (aM + 180 * (i - 1)) / 180.0F * 3.1415927F;
			float f1 = MathHelper.cos(f);

			return locX + f1 * 1.3D;
		}
	}

	private double v(int i) {
		return i <= 0 ? locY + 3.0D : locY + 2.2D;
	}

	private double w(int i) {
		if (i <= 0)
			return locZ;
		else {
			float f = (aM + 180 * (i - 1)) / 180.0F * 3.1415927F;
			float f1 = MathHelper.sin(f);

			return locZ + f1 * 1.3D;
		}
	}

	private float b(float f, float f1, float f2) {
		float f3 = MathHelper.g(f1 - f);

		if (f3 > f2) {
			f3 = f2;
		}

		if (f3 < -f2) {
			f3 = -f2;
		}

		return f + f3;
	}

	private void a(int i, EntityLiving entityliving) {
		this.a(i, entityliving.locX, entityliving.locY + entityliving.getHeadHeight() * 0.5D, entityliving.locZ, i == 0 && random.nextFloat() < 0.001F);
	}

	private void a(int i, double d0, double d1, double d2, boolean flag) {
		world.a((EntityHuman) null, 1014, (int) locX, (int) locY, (int) locZ, 0);
		double d3 = u(i);
		double d4 = v(i);
		double d5 = this.w(i);
		double d6 = d0 - d3;
		double d7 = d1 - d4;
		double d8 = d2 - d5;
		EntityWitherSkull entitywitherskull = new EntityWitherSkull(world, this, d6, d7, d8);

		if (flag) {
			entitywitherskull.setCharged(true);
		}

		entitywitherskull.locY = d4;
		entitywitherskull.locX = d3;
		entitywitherskull.locZ = d5;
		world.addEntity(entitywitherskull);
	}

	@Override
	public void a(EntityLiving entityliving, float f) {
		this.a(0, entityliving);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (damagesource == DamageSource.DROWN)
			return false;
		else if (ca() > 0)
			return false;
		else {
			Entity entity;

			if (cb()) {
				entity = damagesource.i();
				if (entity instanceof EntityArrow)
					return false;
			}

			entity = damagesource.getEntity();
			if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == getMonsterType())
				return false;
			else {
				if (bv <= 0) {
					bv = 20;
				}

				for (int i = 0; i < bu.length; ++i) {
					bu[i] += 3;
				}

				return super.damageEntity(damagesource, f);
			}
		}
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		this.a(Items.NETHER_STAR, 1);
		if (!world.isStatic) {
			Iterator iterator = world.a(EntityHuman.class, boundingBox.grow(50.0D, 100.0D, 50.0D)).iterator();

			while (iterator.hasNext()) {
				EntityHuman entityhuman = (EntityHuman) iterator.next();

				entityhuman.a(AchievementList.J);
			}
		}
	}

	@Override
	protected void w() {
		aU = 0;
	}

	@Override
	protected void b(float f) {
	}

	@Override
	public void addEffect(MobEffect mobeffect) {
	}

	@Override
	protected boolean bk() {
		return true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.6000000238418579D);
		getAttributeInstance(GenericAttributes.b).setValue(40.0D);
	}

	public int ca() {
		return datawatcher.getInt(20);
	}

	public void s(int i) {
		datawatcher.watch(20, Integer.valueOf(i));
	}

	public int t(int i) {
		return datawatcher.getInt(17 + i);
	}

	public void b(int i, int j) {
		datawatcher.watch(17 + i, Integer.valueOf(j));
	}

	public boolean cb() {
		return getHealth() <= getMaxHealth() / 2.0F;
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.UNDEAD;
	}

	@Override
	public void mount(Entity entity) {
		vehicle = null;
	}
}
