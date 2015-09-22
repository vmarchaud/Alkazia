package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityWitherSkull extends EntityFireball {

	public EntityWitherSkull(World world) {
		super(world);
		this.a(0.3125F, 0.3125F);
	}

	public EntityWitherSkull(World world, EntityLiving entityliving, double d0, double d1, double d2) {
		super(world, entityliving, d0, d1, d2);
		this.a(0.3125F, 0.3125F);
	}

	@Override
	protected float e() {
		return isCharged() ? 0.73F : super.e();
	}

	@Override
	public boolean isBurning() {
		return false;
	}

	@Override
	public float a(Explosion explosion, World world, int i, int j, int k, Block block) {
		float f = super.a(explosion, world, i, j, k, block);

		if (isCharged() && block != Blocks.BEDROCK && block != Blocks.ENDER_PORTAL && block != Blocks.ENDER_PORTAL_FRAME && block != Blocks.COMMAND) {
			f = Math.min(0.8F, f);
		}

		return f;
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (!world.isStatic) {
			if (movingobjectposition.entity != null) {
				// Spigot start
				boolean didDamage = false;
				if (shooter != null) {
					didDamage = movingobjectposition.entity.damageEntity(DamageSource.mobAttack(shooter), 8.0F);
					if (didDamage && !movingobjectposition.entity.isAlive()) {
						shooter.heal(5.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.WITHER); // CraftBukkit
					}
				} else {
					didDamage = movingobjectposition.entity.damageEntity(DamageSource.MAGIC, 5.0F);
				}

				if (didDamage && movingobjectposition.entity instanceof EntityLiving) {
					// Spigot end
					byte b0 = 0;

					if (world.difficulty == EnumDifficulty.NORMAL) {
						b0 = 10;
					} else if (world.difficulty == EnumDifficulty.HARD) {
						b0 = 40;
					}

					if (b0 > 0) {
						((EntityLiving) movingobjectposition.entity).addEffect(new MobEffect(MobEffectList.WITHER.id, 20 * b0, 1));
					}
				}
			}

			// CraftBukkit start
			ExplosionPrimeEvent event = new ExplosionPrimeEvent(getBukkitEntity(), 1.0F, false);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				world.createExplosion(this, locX, locY, locZ, event.getRadius(), event.getFire(), world.getGameRules().getBoolean("mobGriefing"));
			}
			// CraftBukkit end

			die();
		}
	}

	@Override
	public boolean R() {
		return false;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}

	@Override
	protected void c() {
		datawatcher.a(10, Byte.valueOf((byte) 0));
	}

	public boolean isCharged() {
		return datawatcher.getByte(10) == 1;
	}

	public void setCharged(boolean flag) {
		datawatcher.watch(10, Byte.valueOf((byte) (flag ? 1 : 0)));
	}
}
