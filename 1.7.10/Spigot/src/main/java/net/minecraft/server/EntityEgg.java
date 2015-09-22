package net.minecraft.server;

// CraftBukkit start
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEggThrowEvent;

// CraftBukkit end

public class EntityEgg extends EntityProjectile {

	public EntityEgg(World world) {
		super(world);
	}

	public EntityEgg(World world, EntityLiving entityliving) {
		super(world, entityliving);
	}

	public EntityEgg(World world, double d0, double d1, double d2) {
		super(world, d0, d1, d2);
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.entity != null) {
			movingobjectposition.entity.damageEntity(DamageSource.projectile(this, getShooter()), 0.0F);
		}

		// CraftBukkit start - Fire PlayerEggThrowEvent
		boolean hatching = !world.isStatic && random.nextInt(8) == 0;
		int numHatching = random.nextInt(32) == 0 ? 4 : 1;
		if (!hatching) {
			numHatching = 0;
		}

		EntityType hatchingType = EntityType.CHICKEN;

		Entity shooter = getShooter();
		if (shooter instanceof EntityPlayer) {
			Player player = shooter == null ? null : (Player) shooter.getBukkitEntity();

			PlayerEggThrowEvent event = new PlayerEggThrowEvent(player, (org.bukkit.entity.Egg) getBukkitEntity(), hatching, (byte) numHatching, hatchingType);
			world.getServer().getPluginManager().callEvent(event);

			hatching = event.isHatching();
			numHatching = event.getNumHatches();
			hatchingType = event.getHatchingType();
		}

		if (hatching) {
			for (int k = 0; k < numHatching; k++) {
				org.bukkit.entity.Entity entity = world.getWorld().spawn(new org.bukkit.Location(world.getWorld(), locX, locY, locZ, yaw, 0.0F), hatchingType.getEntityClass(), org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.EGG);
				if (entity instanceof Ageable) {
					((Ageable) entity).setBaby();
				}
			}
		}
		// CraftBukkit end

		for (int j = 0; j < 8; ++j) {
			world.addParticle("snowballpoof", locX, locY, locZ, 0.0D, 0.0D, 0.0D);
		}

		if (!world.isStatic) {
			die();
		}
	}
}
