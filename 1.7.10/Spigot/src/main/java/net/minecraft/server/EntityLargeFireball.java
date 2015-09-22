package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityLargeFireball extends EntityFireball {

	public int yield = 1;

	public EntityLargeFireball(World world) {
		super(world);
	}

	public EntityLargeFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
		super(world, entityliving, d0, d1, d2);
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (!world.isStatic) {
			if (movingobjectposition.entity != null) {
				movingobjectposition.entity.damageEntity(DamageSource.fireball(this, shooter), 6.0F);
			}

			// CraftBukkit start - fire ExplosionPrimeEvent
			ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(world.getServer(), this));
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				// give 'this' instead of (Entity) null so we know what causes the damage
				world.createExplosion(this, locX, locY, locZ, event.getRadius(), event.getFire(), world.getGameRules().getBoolean("mobGriefing"));
			}
			// CraftBukkit end

			die();
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("ExplosionPower", yield);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
			// CraftBukkit - set bukkitYield when setting explosionpower
			bukkitYield = yield = nbttagcompound.getInt("ExplosionPower");
		}
	}
}
