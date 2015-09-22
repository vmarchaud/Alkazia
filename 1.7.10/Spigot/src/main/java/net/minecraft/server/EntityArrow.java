package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

// CraftBukkit end

public class EntityArrow extends Entity implements IProjectile {

	private int d = -1;
	private int e = -1;
	private int f = -1;
	private Block g;
	private int h;
	public boolean inGround = false; // Spigot - private -> public
	public int fromPlayer;
	public int shake;
	public Entity shooter;
	private int at;
	private int au;
	double damage = 2.0D;
	public int knockbackStrength; // CraftBukkit - private -> public

	// Spigot Start
	@Override
	public void inactiveTick() {
		if (inGround) {
			at += 19; // Despawn counter. First int after shooter
		}
		super.inactiveTick();
	}

	// Spigot End

	public EntityArrow(World world) {
		super(world);
		j = 10.0D;
		this.a(0.5F, 0.5F);
	}

	public EntityArrow(World world, double d0, double d1, double d2) {
		super(world);
		j = 10.0D;
		this.a(0.5F, 0.5F);
		setPosition(d0, d1, d2);
		height = 0.0F;
	}

	public EntityArrow(World world, EntityLiving entityliving, EntityLiving entityliving1, float f, float f1) {
		super(world);
		j = 10.0D;
		shooter = entityliving;
		projectileSource = (LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
		if (entityliving instanceof EntityHuman) {
			fromPlayer = 1;
		}

		locY = entityliving.locY + entityliving.getHeadHeight() - 0.10000000149011612D;
		double d0 = entityliving1.locX - entityliving.locX;
		double d1 = entityliving1.boundingBox.b + entityliving1.length / 3.0F - locY;
		double d2 = entityliving1.locZ - entityliving.locZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

		if (d3 >= 1.0E-7D) {
			float f2 = (float) (Math.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
			float f3 = (float) -(Math.atan2(d1, d3) * 180.0D / 3.1415927410125732D);
			double d4 = d0 / d3;
			double d5 = d2 / d3;

			setPositionRotation(entityliving.locX + d4, locY, entityliving.locZ + d5, f2, f3);
			height = 0.0F;
			float f4 = (float) d3 * 0.2F;

			shoot(d0, d1 + f4, d2, f, f1);
		}
	}

	public EntityArrow(World world, EntityLiving entityliving, float f) {
		super(world);
		j = 10.0D;
		shooter = entityliving;
		projectileSource = (LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
		if (entityliving instanceof EntityHuman) {
			fromPlayer = 1;
		}

		this.a(0.5F, 0.5F);
		setPositionRotation(entityliving.locX, entityliving.locY + entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
		locX -= MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F;
		locY -= 0.10000000149011612D;
		locZ -= MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F;
		setPosition(locX, locY, locZ);
		height = 0.0F;
		motX = -MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F);
		motZ = MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F);
		motY = -MathHelper.sin(pitch / 180.0F * 3.1415927F);
		shoot(motX, motY, motZ, f * 1.5F, 1.0F);
	}

	@Override
	protected void c() {
		datawatcher.a(16, Byte.valueOf((byte) 0));
	}

	@Override
	public void shoot(double d0, double d1, double d2, float f, float f1) {
		float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

		d0 /= f2;
		d1 /= f2;
		d2 /= f2;
		d0 += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * f1;
		d1 += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * f1;
		d2 += random.nextGaussian() * (random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * f1;
		d0 *= f;
		d1 *= f;
		d2 *= f;
		motX = d0;
		motY = d1;
		motZ = d2;
		float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

		lastYaw = yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
		lastPitch = pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
		at = 0;
	}

	@Override
	public void h() {
		super.h();
		if (lastPitch == 0.0F && lastYaw == 0.0F) {
			float f = MathHelper.sqrt(motX * motX + motZ * motZ);

			lastYaw = yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.1415927410125732D);
			lastPitch = pitch = (float) (Math.atan2(motY, f) * 180.0D / 3.1415927410125732D);
		}

		Block block = world.getType(d, e, f);

		if (block.getMaterial() != Material.AIR) {
			block.updateShape(world, d, e, f);
			AxisAlignedBB axisalignedbb = block.a(world, d, e, f);

			if (axisalignedbb != null && axisalignedbb.a(Vec3D.a(locX, locY, locZ))) {
				inGround = true;
			}
		}

		if (shake > 0) {
			--shake;
		}

		if (inGround) {
			int i = world.getData(d, e, f);

			if (block == g && i == h) {
				++at;
				if (at >= world.spigotConfig.arrowDespawnRate) { // First int after shooter
					die();
				}
			} else {
				inGround = false;
				motX *= random.nextFloat() * 0.2F;
				motY *= random.nextFloat() * 0.2F;
				motZ *= random.nextFloat() * 0.2F;
				at = 0;
				au = 0;
			}
		} else {
			++au;
			Vec3D vec3d = Vec3D.a(locX, locY, locZ);
			Vec3D vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition movingobjectposition = world.rayTrace(vec3d, vec3d1, false, true, false);

			vec3d = Vec3D.a(locX, locY, locZ);
			vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			if (movingobjectposition != null) {
				vec3d1 = Vec3D.a(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
			}

			Entity entity = null;
			List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;

			int j;
			float f1;

			for (j = 0; j < list.size(); ++j) {
				Entity entity1 = (Entity) list.get(j);

				if (entity1.R() && (entity1 != shooter || au >= 5)) {
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.grow(f1, f1, f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);

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

			if (movingobjectposition != null && movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityHuman) {
				EntityHuman entityhuman = (EntityHuman) movingobjectposition.entity;

				if (entityhuman.abilities.isInvulnerable || shooter instanceof EntityHuman && !((EntityHuman) shooter).a(entityhuman)) {
					movingobjectposition = null;
				}
			}

			float f2;
			float f3;

			// PaperSpigot start - Allow arrows to fly through players
			if (movingobjectposition != null && movingobjectposition.entity instanceof EntityPlayer && shooter != null && shooter instanceof EntityPlayer) {
				if (!((EntityPlayer) shooter).getBukkitEntity().canSee(((EntityPlayer) movingobjectposition.entity).getBukkitEntity())) {
					movingobjectposition = null;
				}
			}
			// PaperSpigot end

			if (movingobjectposition != null) {
				org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this); // CraftBukkit - Call event

				if (movingobjectposition.entity != null) {
					f2 = MathHelper.sqrt(motX * motX + motY * motY + motZ * motZ);
					int k = MathHelper.f(f2 * damage);

					if (isCritical()) {
						k += random.nextInt(k / 2 + 2);
					}

					DamageSource damagesource = null;

					if (shooter == null) {
						damagesource = DamageSource.arrow(this, this);
					} else {
						damagesource = DamageSource.arrow(this, shooter);
					}

					// CraftBukkit start - Moved damage call
					if (movingobjectposition.entity.damageEntity(damagesource, k)) {
						if (isBurning() && !(movingobjectposition.entity instanceof EntityEnderman) && (!(movingobjectposition.entity instanceof EntityPlayer) || !(shooter instanceof EntityPlayer) || world.pvpMode)) { // CraftBukkit - abide by pvp setting if destination is a player
							EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), 5);
							org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

							if (!combustEvent.isCancelled()) {
								movingobjectposition.entity.setOnFire(combustEvent.getDuration());
							}
							// CraftBukkit end
						}

						// if (movingobjectposition.entity.damageEntity(damagesource, (float) k)) { // CraftBukkit - moved up
						if (movingobjectposition.entity instanceof EntityLiving) {
							EntityLiving entityliving = (EntityLiving) movingobjectposition.entity;

							if (!world.isStatic) {
								entityliving.p(entityliving.aZ() + 1);
							}

							if (knockbackStrength > 0) {
								f3 = MathHelper.sqrt(motX * motX + motZ * motZ);
								if (f3 > 0.0F) {
									movingobjectposition.entity.g(motX * knockbackStrength * 0.6000000238418579D / f3, 0.1D, motZ * knockbackStrength * 0.6000000238418579D / f3);
								}
							}

							if (shooter != null && shooter instanceof EntityLiving) {
								EnchantmentManager.a(entityliving, shooter);
								EnchantmentManager.b((EntityLiving) shooter, entityliving);
							}

							if (shooter != null && movingobjectposition.entity != shooter && movingobjectposition.entity instanceof EntityHuman && shooter instanceof EntityPlayer) {
								((EntityPlayer) shooter).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
							}
						}

						makeSound("random.bowhit", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
						if (!(movingobjectposition.entity instanceof EntityEnderman)) {
							die();
						}
					} else {
						motX *= -0.10000000149011612D;
						motY *= -0.10000000149011612D;
						motZ *= -0.10000000149011612D;
						yaw += 180.0F;
						lastYaw += 180.0F;
						au = 0;
					}
				} else {
					d = movingobjectposition.b;
					e = movingobjectposition.c;
					f = movingobjectposition.d;
					g = world.getType(d, e, f);
					h = world.getData(d, e, f);
					motX = (float) (movingobjectposition.pos.a - locX);
					motY = (float) (movingobjectposition.pos.b - locY);
					motZ = (float) (movingobjectposition.pos.c - locZ);
					f2 = MathHelper.sqrt(motX * motX + motY * motY + motZ * motZ);
					locX -= motX / f2 * 0.05000000074505806D;
					locY -= motY / f2 * 0.05000000074505806D;
					locZ -= motZ / f2 * 0.05000000074505806D;
					makeSound("random.bowhit", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
					inGround = true;
					shake = 7;
					setCritical(false);
					if (g.getMaterial() != Material.AIR) {
						g.a(world, d, e, f, this);
					}
				}
			}

			if (isCritical()) {
				for (j = 0; j < 4; ++j) {
					world.addParticle("crit", locX + motX * j / 4.0D, locY + motY * j / 4.0D, locZ + motZ * j / 4.0D, -motX, -motY + 0.2D, -motZ);
				}
			}

			locX += motX;
			locY += motY;
			locZ += motZ;
			f2 = MathHelper.sqrt(motX * motX + motZ * motZ);
			yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.1415927410125732D);

			for (pitch = (float) (Math.atan2(motY, f2) * 180.0D / 3.1415927410125732D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F) {
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
			float f4 = 0.99F;

			f1 = 0.05F;
			if (M()) {
				for (int l = 0; l < 4; ++l) {
					f3 = 0.25F;
					world.addParticle("bubble", locX - motX * f3, locY - motY * f3, locZ - motZ * f3, motX, motY, motZ);
				}

				f4 = 0.8F;
			}

			if (L()) {
				extinguish();
			}

			motX *= f4;
			motY *= f4;
			motZ *= f4;
			motY -= f1;
			setPosition(locX, locY, locZ);
			I();
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) d);
		nbttagcompound.setShort("yTile", (short) e);
		nbttagcompound.setShort("zTile", (short) f);
		nbttagcompound.setShort("life", (short) at);
		nbttagcompound.setByte("inTile", (byte) Block.getId(g));
		nbttagcompound.setByte("inData", (byte) h);
		nbttagcompound.setByte("shake", (byte) shake);
		nbttagcompound.setByte("inGround", (byte) (inGround ? 1 : 0));
		nbttagcompound.setByte("pickup", (byte) fromPlayer);
		nbttagcompound.setDouble("damage", damage);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		d = nbttagcompound.getShort("xTile");
		e = nbttagcompound.getShort("yTile");
		f = nbttagcompound.getShort("zTile");
		at = nbttagcompound.getShort("life");
		g = Block.getById(nbttagcompound.getByte("inTile") & 255);
		h = nbttagcompound.getByte("inData") & 255;
		shake = nbttagcompound.getByte("shake") & 255;
		inGround = nbttagcompound.getByte("inGround") == 1;
		if (nbttagcompound.hasKeyOfType("damage", 99)) {
			damage = nbttagcompound.getDouble("damage");
		}

		if (nbttagcompound.hasKeyOfType("pickup", 99)) {
			fromPlayer = nbttagcompound.getByte("pickup");
		} else if (nbttagcompound.hasKeyOfType("player", 99)) {
			fromPlayer = nbttagcompound.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void b_(EntityHuman entityhuman) {
		if (!world.isStatic && inGround && shake <= 0) {
			// CraftBukkit start
			ItemStack itemstack = new ItemStack(Items.ARROW);
			if (fromPlayer == 1 && entityhuman.inventory.canHold(itemstack) > 0) {
				EntityItem item = new EntityItem(world, locX, locY, locZ, itemstack);

				PlayerPickupItemEvent event = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(world.getServer(), this, item), 0);
				// event.setCancelled(!entityhuman.canPickUpLoot); TODO
				world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled())
					return;
			}
			// CraftBukkit end

			boolean flag = fromPlayer == 1 || fromPlayer == 2 && entityhuman.abilities.canInstantlyBuild;

			if (fromPlayer == 1 && !entityhuman.inventory.pickup(new ItemStack(Items.ARROW, 1))) {
				flag = false;
			}

			if (flag) {
				makeSound("random.pop", 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityhuman.receive(this, 1);
				die();
			}
		}
	}

	@Override
	protected boolean g_() {
		return false;
	}

	public void b(double d0) {
		damage = d0;
	}

	public double e() {
		return damage;
	}

	public void setKnockbackStrength(int i) {
		knockbackStrength = i;
	}

	@Override
	public boolean av() {
		return false;
	}

	public void setCritical(boolean flag) {
		byte b0 = datawatcher.getByte(16);

		if (flag) {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 | 1)));
		} else {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	public boolean isCritical() {
		byte b0 = datawatcher.getByte(16);

		return (b0 & 1) != 0;
	}

	// CraftBukkit start
	public boolean isInGround() {
		return inGround;
	}
	// CraftBukkit end
}
