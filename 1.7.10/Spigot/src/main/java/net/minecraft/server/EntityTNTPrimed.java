package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityTNTPrimed extends Entity {

	public int fuseTicks;
	private EntityLiving source;
	public float yield = 4; // CraftBukkit - add field
	public boolean isIncendiary = false; // CraftBukkit - add field

	public EntityTNTPrimed(World world) {
		super(world);
		k = true;
		this.a(0.98F, 0.98F);
		height = length / 2.0F;
	}

	public EntityTNTPrimed(World world, double d0, double d1, double d2, EntityLiving entityliving) {
		this(world);
		setPosition(d0, d1, d2);
		//float f = (float) (Math.random() * 3.1415927410125732D * 2.0D); // PaperSpigot - Fix directional TNT bias

		motX = 0; // PaperSpigot - Fix directional TNT bias //(double) (-((float) Math.sin((double) f)) * 0.02F);
		motY = 0.20000000298023224D;
		motZ = 0; // PaperSpigot - Fix directional TNT bias //(double) (-((float) Math.cos((double) f)) * 0.02F);
		fuseTicks = 80;
		lastX = d0;
		lastY = d1;
		lastZ = d2;
		source = entityliving;
	}

	@Override
	protected void c() {
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	public boolean R() {
		return !dead;
	}

	@Override
	public void h() {
		if (world.spigotConfig.currentPrimedTnt++ > world.spigotConfig.maxTntTicksPerTick)
			return;
		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		motY -= 0.03999999910593033D;
		move(motX, motY, motZ);
		// PaperSpigot start - Remove entities in unloaded chunks
		if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedTNTEntities) {
			die();
			fuseTicks = 2;
		}
		// PaperSpigot end
		motX *= 0.9800000190734863D;
		motY *= 0.9800000190734863D;
		motZ *= 0.9800000190734863D;
		if (onGround) {
			motX *= 0.699999988079071D;
			motZ *= 0.699999988079071D;
			motY *= -0.5D;
		}

		if (fuseTicks-- <= 0) {
			// CraftBukkit start - Need to reverse the order of the explosion and the entity death so we have a location for the event
			if (!world.isStatic) {
				explode();
			}
			die();
			// CraftBukkit end
		} else {
			world.addParticle("smoke", locX, locY + 0.5D, locZ, 0.0D, 0.0D, 0.0D);
		}
	}

	private void explode() {
		// CraftBukkit start
		// float f = 4.0F;

		org.bukkit.craftbukkit.CraftServer server = world.getServer();

		ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(server, this));
		server.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			// give 'this' instead of (Entity) null so we know what causes the damage
			world.createExplosion(this, locX, locY, locZ, event.getRadius(), event.getFire(), true);
		}
		// CraftBukkit end
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Fuse", (byte) fuseTicks);
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		fuseTicks = nbttagcompound.getByte("Fuse");
	}

	public EntityLiving getSource() {
		return source;
	}
}
