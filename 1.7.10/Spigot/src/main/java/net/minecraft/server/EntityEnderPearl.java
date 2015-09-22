package net.minecraft.server;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerTeleportEvent;

// CraftBukkit end

public class EntityEnderPearl extends EntityProjectile {

	public EntityEnderPearl(World world) {
		super(world);
	}

	public EntityEnderPearl(World world, EntityLiving entityliving) {
		super(world, entityliving);
	}

	@Override
	protected void a(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.entity != null) {
			movingobjectposition.entity.damageEntity(DamageSource.projectile(this, getShooter()), 0.0F);
		}

		// PaperSpigot start - Remove entities in unloaded chunks
		if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
			die();
		}
		// PaperSpigot end

		for (int i = 0; i < 32; ++i) {
			world.addParticle("portal", locX, locY + random.nextDouble() * 2.0D, locZ, random.nextGaussian(), 0.0D, random.nextGaussian());
		}

		if (!world.isStatic) {
			if (getShooter() != null && getShooter() instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) getShooter();

				if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == world) {
					// CraftBukkit start - Fire PlayerTeleportEvent
					org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();
					org.bukkit.Location location = getBukkitEntity().getLocation();
					location.setPitch(player.getLocation().getPitch());
					location.setYaw(player.getLocation().getYaw());

					PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
					Bukkit.getPluginManager().callEvent(teleEvent);

					if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
						if (getShooter().am()) {
							getShooter().mount((Entity) null);
						}

						entityplayer.playerConnection.teleport(teleEvent.getTo());
						getShooter().fallDistance = 0.0F;
						CraftEventFactory.entityDamage = this;
						getShooter().damageEntity(DamageSource.FALL, 5.0F);
						CraftEventFactory.entityDamage = null;
					}
					// CraftBukkit end
				}
			}

			die();
		}
	}
}
