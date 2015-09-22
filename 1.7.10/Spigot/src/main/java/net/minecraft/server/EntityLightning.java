package net.minecraft.server;

import java.util.List;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityLightning extends EntityWeather {

	private int lifeTicks;
	public long a;
	private int c;

	// CraftBukkit start
	public boolean isEffect = false;

	public boolean isSilent = false; // Spigot

	public EntityLightning(World world, double d0, double d1, double d2) {
		this(world, d0, d1, d2, false);
	}

	public EntityLightning(World world, double d0, double d1, double d2, boolean isEffect) {
		// CraftBukkit end

		super(world);

		// CraftBukkit - Set isEffect
		this.isEffect = isEffect;

		setPositionRotation(d0, d1, d2, 0.0F, 0.0F);
		lifeTicks = 2;
		a = random.nextLong();
		c = random.nextInt(3) + 1;

		// CraftBukkit - add "!isEffect"
		if (!isEffect && !world.isStatic && world.getGameRules().getBoolean("doFireTick") && (world.difficulty == EnumDifficulty.NORMAL || world.difficulty == EnumDifficulty.HARD) && world.areChunksLoaded(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2), 10)) {
			int i = MathHelper.floor(d0);
			int j = MathHelper.floor(d1);
			int k = MathHelper.floor(d2);

			if (world.getType(i, j, k).getMaterial() == Material.AIR && Blocks.FIRE.canPlace(world, i, j, k)) {
				// CraftBukkit start
				if (!CraftEventFactory.callBlockIgniteEvent(world, i, j, k, this).isCancelled()) {
					world.setTypeUpdate(i, j, k, Blocks.FIRE);
				}
				// CraftBukkit end
			}

			for (i = 0; i < 4; ++i) {
				j = MathHelper.floor(d0) + random.nextInt(3) - 1;
				k = MathHelper.floor(d1) + random.nextInt(3) - 1;
				int l = MathHelper.floor(d2) + random.nextInt(3) - 1;

				if (world.getType(j, k, l).getMaterial() == Material.AIR && Blocks.FIRE.canPlace(world, j, k, l)) {
					// CraftBukkit start
					if (!CraftEventFactory.callBlockIgniteEvent(world, j, k, l, this).isCancelled()) {
						world.setTypeUpdate(j, k, l, Blocks.FIRE);
					}
					// CraftBukkit end
				}
			}
		}
	}

	// Spigot start
	public EntityLightning(World world, double d0, double d1, double d2, boolean isEffect, boolean isSilent) {
		this(world, d0, d1, d2, isEffect);
		this.isSilent = isSilent;
	}

	// Spigot end

	@Override
	public void h() {
		super.h();
		if (!isSilent && lifeTicks == 2) { // Spigot
			// CraftBukkit start - Use relative location for far away sounds
			//this.world.makeSound(this.locX, this.locY, this.locZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
			float pitch = 0.8F + random.nextFloat() * 0.2F;
			int viewDistance = ((WorldServer) world).getServer().getViewDistance() * 16;
			for (EntityPlayer player : (List<EntityPlayer>) world.players) {
				double deltaX = locX - player.locX;
				double deltaZ = locZ - player.locZ;
				double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
				if (distanceSquared > viewDistance * viewDistance) {
					double deltaLength = Math.sqrt(distanceSquared);
					double relativeX = player.locX + deltaX / deltaLength * viewDistance;
					double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
					player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", relativeX, locY, relativeZ, 10000.0F, pitch));
				} else {
					player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", locX, locY, locZ, 10000.0F, pitch));
				}
			}
			// CraftBukkit end
			world.makeSound(locX, locY, locZ, "random.explode", 2.0F, 0.5F + random.nextFloat() * 0.2F);
		}

		--lifeTicks;
		if (lifeTicks < 0) {
			if (c == 0) {
				die();
			} else if (lifeTicks < -random.nextInt(10)) {
				--c;
				lifeTicks = 1;
				a = random.nextLong();
				// CraftBukkit - add "!isEffect"
				if (!isEffect && !world.isStatic && world.getGameRules().getBoolean("doFireTick") && world.areChunksLoaded(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ), 10)) {
					int i = MathHelper.floor(locX);
					int j = MathHelper.floor(locY);
					int k = MathHelper.floor(locZ);

					if (world.getType(i, j, k).getMaterial() == Material.AIR && Blocks.FIRE.canPlace(world, i, j, k)) {
						// CraftBukkit start
						if (!CraftEventFactory.callBlockIgniteEvent(world, i, j, k, this).isCancelled()) {
							world.setTypeUpdate(i, j, k, Blocks.FIRE);
						}
						// CraftBukkit end
					}
				}
			}
		}

		if (lifeTicks >= 0 && !isEffect) { // CraftBukkit - add !this.isEffect
			if (world.isStatic) {
				world.q = 2;
			} else {
				double d0 = 3.0D;
				List list = world.getEntities(this, AxisAlignedBB.a(locX - d0, locY - d0, locZ - d0, locX + d0, locY + 6.0D + d0, locZ + d0));

				for (int l = 0; l < list.size(); ++l) {
					Entity entity = (Entity) list.get(l);

					entity.a(this);
				}
			}
		}
	}

	@Override
	protected void c() {
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
	}
}
