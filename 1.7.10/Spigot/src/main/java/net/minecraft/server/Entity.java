package net.minecraft.server;

import java.util.List;
import java.util.Random;
import java.util.UUID;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TravelAgent;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
// CraftBukkit end
import org.spigotmc.CustomTimingsHandler; // Spigot

public abstract class Entity {

	// CraftBukkit start
	private static final int CURRENT_LEVEL = 2;

	static boolean isLevelAtLeast(NBTTagCompound tag, int level) {
		return tag.hasKey("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
	}

	// PaperSpigot start
	public void retrack() {
		final EntityTracker entityTracker = ((WorldServer) world).getTracker();
		entityTracker.untrackEntity(this);
		entityTracker.track(this);
	}

	// PaperSpigot end
	// CraftBukkit end

	private static int entityCount;
	private int id;
	public double j;
	public boolean k;
	public Entity passenger;
	public Entity vehicle;
	public boolean attachedToPlayer;
	public World world;
	public double lastX;
	public double lastY;
	public double lastZ;
	public double locX;
	public double locY;
	public double locZ;
	public double motX;
	public double motY;
	public double motZ;
	public float yaw;
	public float pitch;
	public float lastYaw;
	public float lastPitch;
	public final AxisAlignedBB boundingBox;
	public boolean onGround;
	public boolean positionChanged;
	public boolean F;
	public boolean G;
	public boolean velocityChanged;
	protected boolean I;
	public boolean J;
	public boolean dead;
	public float height;
	public float width;
	public float length;
	public float O;
	public float P;
	public float Q;
	public float fallDistance;
	private int d;
	public double S;
	public double T;
	public double U;
	public float V;
	public float W;
	public boolean X;
	public float Y;
	public float Z;
	protected Random random;
	public int ticksLived;
	public int maxFireTicks;
	public int fireTicks; // CraftBukkit - private -> public
	public boolean inWater; // Spigot - protected -> public
	public int noDamageTicks;
	private boolean justCreated;
	protected boolean fireProof;
	protected DataWatcher datawatcher;
	private double g;
	private double h;
	public boolean ag;
	public int ah;
	public int ai;
	public int aj;
	public boolean ak;
	public boolean al;
	public int portalCooldown;
	protected boolean an;
	protected int ao;
	public int dimension;
	protected int aq;
	private boolean invulnerable;
	public UUID uniqueID; // CraftBukkit - protected -> public
	public EnumEntitySize as;
	public boolean valid; // CraftBukkit
	public org.bukkit.projectiles.ProjectileSource projectileSource; // CraftBukkit - For projectiles only
	public boolean inUnloadedChunk = false; // PaperSpigot - Remove entities in unloaded chunks

	// Spigot start
	public CustomTimingsHandler tickTimer = org.bukkit.craftbukkit.SpigotTimings.getEntityTimings(this); // Spigot
	public final byte activationType = org.spigotmc.ActivationRange.initializeEntityActivationType(this);
	public final boolean defaultActivationState;
	public long activatedTick = 0;
	public boolean fromMobSpawner;

	public void inactiveTick() {
	}

	// Spigot end

	public int getId() {
		return id;
	}

	public void d(int i) {
		id = i;
	}

	public Entity(World world) {
		id = entityCount++;
		j = 1.0D;
		boundingBox = AxisAlignedBB.a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		J = true;
		width = 0.6F;
		length = 1.8F;
		d = 1;
		random = new Random();
		maxFireTicks = 1;
		justCreated = true;
		uniqueID = new UUID(random.nextLong(), random.nextLong()); // Spigot
		as = EnumEntitySize.SIZE_2;
		this.world = world;
		setPosition(0.0D, 0.0D, 0.0D);
		if (world != null) {
			dimension = world.worldProvider.dimension;
			// Spigot start
			defaultActivationState = org.spigotmc.ActivationRange.initializeEntityActivationState(this, world.spigotConfig);
		} else {
			defaultActivationState = false;
		}
		// Spigot end

		datawatcher = new DataWatcher(this);
		datawatcher.a(0, Byte.valueOf((byte) 0));
		datawatcher.a(1, Short.valueOf((short) 300));
		this.c();
	}

	protected abstract void c();

	public DataWatcher getDataWatcher() {
		return datawatcher;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity) object).id == id : false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public void die() {
		dead = true;
	}

	protected void a(float f, float f1) {
		float f2;

		if (f != width || f1 != length) {
			f2 = width;
			width = f;
			length = f1;
			boundingBox.d = boundingBox.a + width;
			boundingBox.f = boundingBox.c + width;
			boundingBox.e = boundingBox.b + length;
			if (width > f2 && !justCreated && !world.isStatic) {
				move(f2 - width, 0.0D, f2 - width);
			}
		}

		f2 = f % 2.0F;
		if (f2 < 0.375D) {
			as = EnumEntitySize.SIZE_1;
		} else if (f2 < 0.75D) {
			as = EnumEntitySize.SIZE_2;
		} else if (f2 < 1.0D) {
			as = EnumEntitySize.SIZE_3;
		} else if (f2 < 1.375D) {
			as = EnumEntitySize.SIZE_4;
		} else if (f2 < 1.75D) {
			as = EnumEntitySize.SIZE_5;
		} else {
			as = EnumEntitySize.SIZE_6;
		}
	}

	protected void b(float f, float f1) {
		// CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
		if (Float.isNaN(f)) {
			f = 0;
		}

		if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
			if (this instanceof EntityPlayer) {
				world.getServer().getLogger().warning(((CraftPlayer) getBukkitEntity()).getName() + " was caught trying to crash the server with an invalid yaw");
				((CraftPlayer) getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)"); //Spigot "Nope" -> Descriptive reason
			}
			f = 0;
		}

		// pitch was sometimes set to NaN, so we need to set it back to 0.
		if (Float.isNaN(f1)) {
			f1 = 0;
		}

		if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
			if (this instanceof EntityPlayer) {
				world.getServer().getLogger().warning(((CraftPlayer) getBukkitEntity()).getName() + " was caught trying to crash the server with an invalid pitch");
				((CraftPlayer) getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)"); //Spigot "Nope" -> Descriptive reason
			}
			f1 = 0;
		}
		// CraftBukkit end

		yaw = f % 360.0F;
		pitch = f1 % 360.0F;
	}

	public void setPosition(double d0, double d1, double d2) {
		locX = d0;
		locY = d1;
		locZ = d2;
		float f = width / 2.0F;
		float f1 = length;

		boundingBox.b(d0 - f, d1 - height + V, d2 - f, d0 + f, d1 - height + V + f1, d2 + f);
	}

	public void h() {
		C();
	}

	public void C() {
		world.methodProfiler.a("entityBaseTick");
		if (vehicle != null && vehicle.dead) {
			vehicle = null;
		}

		O = P;
		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		lastPitch = pitch;
		lastYaw = yaw;
		int i;

		if (!world.isStatic && world instanceof WorldServer) {
			world.methodProfiler.a("portal");
			MinecraftServer minecraftserver = ((WorldServer) world).getMinecraftServer();

			i = D();
			if (an) {
				if (true || minecraftserver.getAllowNether()) { // CraftBukkit
					if (vehicle == null && ao++ >= i) {
						ao = i;
						portalCooldown = ai();
						byte b0;

						if (world.worldProvider.dimension == -1) {
							b0 = 0;
						} else {
							b0 = -1;
						}

						this.b(b0);
					}

					an = false;
				}
			} else {
				if (ao > 0) {
					ao -= 4;
				}

				if (ao < 0) {
					ao = 0;
				}
			}

			if (portalCooldown > 0) {
				--portalCooldown;
			}

			world.methodProfiler.b();
		}

		if (isSprinting() && !M()) {
			int j = MathHelper.floor(locX);

			i = MathHelper.floor(locY - 0.20000000298023224D - height);
			int k = MathHelper.floor(locZ);
			Block block = world.getType(j, i, k);

			if (block.getMaterial() != Material.AIR) {
				world.addParticle("blockcrack_" + Block.getId(block) + "_" + world.getData(j, i, k), locX + (random.nextFloat() - 0.5D) * width, boundingBox.b + 0.1D, locZ + (random.nextFloat() - 0.5D) * width, -motX * 4.0D, 1.5D, -motZ * 4.0D);
			}
		}

		N();
		if (world.isStatic) {
			fireTicks = 0;
		} else if (fireTicks > 0) {
			if (fireProof) {
				fireTicks -= 4;
				if (fireTicks < 0) {
					fireTicks = 0;
				}
			} else {
				if (fireTicks % 20 == 0) {
					damageEntity(DamageSource.BURN, 1.0F);
				}

				--fireTicks;
			}
		}

		if (P()) {
			E();
			fallDistance *= 0.5F;
		}

		if (locY < -64.0D) {
			G();
		}

		if (!world.isStatic) {
			this.a(0, fireTicks > 0);
		}

		justCreated = false;
		world.methodProfiler.b();
	}

	public int D() {
		return 0;
	}

	protected void E() {
		if (!fireProof) {
			damageEntity(DamageSource.LAVA, 4);

			// CraftBukkit start - Fallen in lava TODO: this event spams!
			if (this instanceof EntityLiving) {
				if (fireTicks <= 0) {
					// not on fire yet
					// TODO: shouldn't be sending null for the block.
					org.bukkit.block.Block damager = null; // ((WorldServer) this.l).getWorld().getBlockAt(i, j, k);
					org.bukkit.entity.Entity damagee = getBukkitEntity();
					EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
					world.getServer().getPluginManager().callEvent(combustEvent);

					if (!combustEvent.isCancelled()) {
						setOnFire(combustEvent.getDuration());
					}
				} else {
					// This will be called every single tick the entity is in lava, so don't throw an event
					setOnFire(15);
				}
				return;
			}
			// CraftBukkit end - we also don't throw an event unless the object in lava is living, to save on some event calls

			setOnFire(15);
		}
	}

	public void setOnFire(int i) {
		int j = i * 20;

		j = EnchantmentProtection.a(this, j);
		if (fireTicks < j) {
			fireTicks = j;
		}
	}

	public void extinguish() {
		fireTicks = 0;
	}

	protected void G() {
		die();
	}

	public boolean c(double d0, double d1, double d2) {
		AxisAlignedBB axisalignedbb = boundingBox.c(d0, d1, d2);
		List list = world.getCubes(this, axisalignedbb);

		return !list.isEmpty() ? false : !world.containsLiquid(axisalignedbb);
	}

	public void move(double d0, double d1, double d2) {
		// CraftBukkit start - Don't do anything if we aren't moving
		// We need to do this regardless of whether or not we are moving thanks to portals
		try {
			I();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
			CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

			this.a(crashreportsystemdetails);
			throw new ReportedException(crashreport);
		}
		// Check if we're moving
		if (d0 == 0 && d1 == 0 && d2 == 0 && vehicle == null && passenger == null)
			return;
		// CraftBukkit end
		org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.startTiming(); // Spigot
		if (X) {
			boundingBox.d(d0, d1, d2);
			locX = (boundingBox.a + boundingBox.d) / 2.0D;
			locY = boundingBox.b + height - V;
			locZ = (boundingBox.c + boundingBox.f) / 2.0D;
		} else {
			world.methodProfiler.a("move");
			V *= 0.4F;
			double d3 = locX;
			double d4 = locY;
			double d5 = locZ;

			if (I) {
				I = false;
				d0 *= 0.25D;
				d1 *= 0.05000000074505806D;
				d2 *= 0.25D;
				motX = 0.0D;
				motY = 0.0D;
				motZ = 0.0D;
			}

			double d6 = d0;
			double d7 = d1;
			double d8 = d2;
			AxisAlignedBB axisalignedbb = boundingBox.clone();
			boolean flag = onGround && isSneaking() && this instanceof EntityHuman;

			if (flag) {
				double d9;

				for (d9 = 0.05D; d0 != 0.0D && world.getCubes(this, boundingBox.c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
					if (d0 < d9 && d0 >= -d9) {
						d0 = 0.0D;
					} else if (d0 > 0.0D) {
						d0 -= d9;
					} else {
						d0 += d9;
					}
				}

				for (; d2 != 0.0D && world.getCubes(this, boundingBox.c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
					if (d2 < d9 && d2 >= -d9) {
						d2 = 0.0D;
					} else if (d2 > 0.0D) {
						d2 -= d9;
					} else {
						d2 += d9;
					}
				}

				while (d0 != 0.0D && d2 != 0.0D && world.getCubes(this, boundingBox.c(d0, -1.0D, d2)).isEmpty()) {
					if (d0 < d9 && d0 >= -d9) {
						d0 = 0.0D;
					} else if (d0 > 0.0D) {
						d0 -= d9;
					} else {
						d0 += d9;
					}

					if (d2 < d9 && d2 >= -d9) {
						d2 = 0.0D;
					} else if (d2 > 0.0D) {
						d2 -= d9;
					} else {
						d2 += d9;
					}

					d6 = d0;
					d8 = d2;
				}
			}

			List list = world.getCubes(this, boundingBox.a(d0, d1, d2));

			for (int i = 0; i < list.size(); ++i) {
				d1 = ((AxisAlignedBB) list.get(i)).b(boundingBox, d1);
			}

			boundingBox.d(0.0D, d1, 0.0D);
			if (!J && d7 != d1) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			boolean flag1 = onGround || d7 != d1 && d7 < 0.0D;

			int j;

			for (j = 0; j < list.size(); ++j) {
				d0 = ((AxisAlignedBB) list.get(j)).a(boundingBox, d0);
			}

			boundingBox.d(d0, 0.0D, 0.0D);
			if (!J && d6 != d0) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			for (j = 0; j < list.size(); ++j) {
				d2 = ((AxisAlignedBB) list.get(j)).c(boundingBox, d2);
			}

			boundingBox.d(0.0D, 0.0D, d2);
			if (!J && d8 != d2) {
				d2 = 0.0D;
				d1 = 0.0D;
				d0 = 0.0D;
			}

			double d10;
			double d11;
			double d12;
			int k;

			if (W > 0.0F && flag1 && (flag || V < 0.05F) && (d6 != d0 || d8 != d2)) {
				d10 = d0;
				d11 = d1;
				d12 = d2;
				d0 = d6;
				d1 = W;
				d2 = d8;
				AxisAlignedBB axisalignedbb1 = boundingBox.clone();

				boundingBox.d(axisalignedbb);
				list = world.getCubes(this, boundingBox.a(d6, d1, d8));

				for (k = 0; k < list.size(); ++k) {
					d1 = ((AxisAlignedBB) list.get(k)).b(boundingBox, d1);
				}

				boundingBox.d(0.0D, d1, 0.0D);
				if (!J && d7 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				for (k = 0; k < list.size(); ++k) {
					d0 = ((AxisAlignedBB) list.get(k)).a(boundingBox, d0);
				}

				boundingBox.d(d0, 0.0D, 0.0D);
				if (!J && d6 != d0) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				for (k = 0; k < list.size(); ++k) {
					d2 = ((AxisAlignedBB) list.get(k)).c(boundingBox, d2);
				}

				boundingBox.d(0.0D, 0.0D, d2);
				if (!J && d8 != d2) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				}

				if (!J && d7 != d1) {
					d2 = 0.0D;
					d1 = 0.0D;
					d0 = 0.0D;
				} else {
					d1 = -W;

					for (k = 0; k < list.size(); ++k) {
						d1 = ((AxisAlignedBB) list.get(k)).b(boundingBox, d1);
					}

					boundingBox.d(0.0D, d1, 0.0D);
				}

				if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
					d0 = d10;
					d1 = d11;
					d2 = d12;
					boundingBox.d(axisalignedbb1);
				}
			}

			world.methodProfiler.b();
			world.methodProfiler.a("rest");
			locX = (boundingBox.a + boundingBox.d) / 2.0D;
			locY = boundingBox.b + height - V;
			locZ = (boundingBox.c + boundingBox.f) / 2.0D;
			positionChanged = d6 != d0 || d8 != d2;
			F = d7 != d1;
			onGround = d7 != d1 && d7 < 0.0D;
			G = positionChanged || F;
			this.a(d1, onGround);
			if (d6 != d0) {
				motX = 0.0D;
			}

			if (d7 != d1) {
				motY = 0.0D;
			}

			if (d8 != d2) {
				motZ = 0.0D;
			}

			d10 = locX - d3;
			d11 = locY - d4;
			d12 = locZ - d5;

			// CraftBukkit start
			if (positionChanged && getBukkitEntity() instanceof Vehicle) {
				Vehicle vehicle = (Vehicle) getBukkitEntity();
				org.bukkit.block.Block block = world.getWorld().getBlockAt(MathHelper.floor(locX), MathHelper.floor(locY - height), MathHelper.floor(locZ));

				if (d6 > d0) {
					block = block.getRelative(BlockFace.EAST);
				} else if (d6 < d0) {
					block = block.getRelative(BlockFace.WEST);
				} else if (d8 > d2) {
					block = block.getRelative(BlockFace.SOUTH);
				} else if (d8 < d2) {
					block = block.getRelative(BlockFace.NORTH);
				}

				VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, block);
				world.getServer().getPluginManager().callEvent(event);
			}
			// CraftBukkit end

			if (g_() && !flag && vehicle == null) {
				int l = MathHelper.floor(locX);

				k = MathHelper.floor(locY - 0.20000000298023224D - height);
				int i1 = MathHelper.floor(locZ);
				Block block = world.getType(l, k, i1);
				int j1 = world.getType(l, k - 1, i1).b();

				if (j1 == 11 || j1 == 32 || j1 == 21) {
					block = world.getType(l, k - 1, i1);
				}

				if (block != Blocks.LADDER) {
					d11 = 0.0D;
				}

				P = (float) (P + MathHelper.sqrt(d10 * d10 + d12 * d12) * 0.6D);
				Q = (float) (Q + MathHelper.sqrt(d10 * d10 + d11 * d11 + d12 * d12) * 0.6D);
				if (Q > d && block.getMaterial() != Material.AIR) {
					d = (int) Q + 1;
					if (M()) {
						float f = MathHelper.sqrt(motX * motX * 0.20000000298023224D + motY * motY + motZ * motZ * 0.20000000298023224D) * 0.35F;

						if (f > 1.0F) {
							f = 1.0F;
						}

						makeSound(H(), f, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
					}

					this.a(l, k, i1, block);
					block.b(world, l, k, i1, this);
				}
			}

			// CraftBukkit start - Move to the top of the method
			/*
			try {
			    this.I();
			} catch (Throwable throwable) {
			    CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
			    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

			    this.a(crashreportsystemdetails);
			    throw new ReportedException(crashreport);
			}
			*/
			// CraftBukkit end
			boolean flag2 = L();

			if (world.e(boundingBox.shrink(0.001D, 0.001D, 0.001D))) {
				burn(1);
				if (!flag2) {
					++fireTicks;
					// CraftBukkit start - Not on fire yet
					if (fireTicks <= 0) { // Only throw events on the first combust, otherwise it spams
						EntityCombustEvent event = new EntityCombustEvent(getBukkitEntity(), 8);
						world.getServer().getPluginManager().callEvent(event);

						if (!event.isCancelled()) {
							setOnFire(event.getDuration());
						}
					} else {
						// CraftBukkit end
						setOnFire(8);
					}
				}
			} else if (fireTicks <= 0) {
				fireTicks = -maxFireTicks;
			}

			if (flag2 && fireTicks > 0) {
				makeSound("random.fizz", 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F);
				fireTicks = -maxFireTicks;
			}

			world.methodProfiler.b();
		}
		org.bukkit.craftbukkit.SpigotTimings.entityMoveTimer.stopTiming(); // Spigot
	}

	protected String H() {
		return "game.neutral.swim";
	}

	protected void I() {
		int i = MathHelper.floor(boundingBox.a + 0.001D);
		int j = MathHelper.floor(boundingBox.b + 0.001D);
		int k = MathHelper.floor(boundingBox.c + 0.001D);
		int l = MathHelper.floor(boundingBox.d - 0.001D);
		int i1 = MathHelper.floor(boundingBox.e - 0.001D);
		int j1 = MathHelper.floor(boundingBox.f - 0.001D);

		if (world.b(i, j, k, l, i1, j1)) {
			for (int k1 = i; k1 <= l; ++k1) {
				for (int l1 = j; l1 <= i1; ++l1) {
					for (int i2 = k; i2 <= j1; ++i2) {
						Block block = world.getType(k1, l1, i2);

						try {
							block.a(world, k1, l1, i2, this);
						} catch (Throwable throwable) {
							CrashReport crashreport = CrashReport.a(throwable, "Colliding entity with block");
							CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");

							CrashReportSystemDetails.a(crashreportsystemdetails, k1, l1, i2, block, world.getData(k1, l1, i2));
							throw new ReportedException(crashreport);
						}
					}
				}
			}
		}
	}

	protected void a(int i, int j, int k, Block block) {
		StepSound stepsound = block.stepSound;

		if (world.getType(i, j + 1, k) == Blocks.SNOW) {
			stepsound = Blocks.SNOW.stepSound;
			makeSound(stepsound.getStepSound(), stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
		} else if (!block.getMaterial().isLiquid()) {
			makeSound(stepsound.getStepSound(), stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
		}
	}

	public void makeSound(String s, float f, float f1) {
		world.makeSound(this, s, f, f1);
	}

	protected boolean g_() {
		return true;
	}

	protected void a(double d0, boolean flag) {
		if (flag) {
			if (fallDistance > 0.0F) {
				this.b(fallDistance);
				fallDistance = 0.0F;
			}
		} else if (d0 < 0.0D) {
			fallDistance = (float) (fallDistance - d0);
		}
	}

	public AxisAlignedBB J() {
		return null;
	}

	protected void burn(float i) { // CraftBukkit - int -> float
		if (!fireProof) {
			damageEntity(DamageSource.FIRE, i);
		}
	}

	public final boolean isFireproof() {
		return fireProof;
	}

	protected void b(float f) {
		if (passenger != null) {
			passenger.b(f);
		}
	}

	public boolean L() {
		return inWater || world.isRainingAt(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)) || world.isRainingAt(MathHelper.floor(locX), MathHelper.floor(locY + length), MathHelper.floor(locZ));
	}

	public boolean M() {
		return inWater;
	}

	public boolean N() {
		if (world.a(boundingBox.grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D, 0.001D, 0.001D), Material.WATER, this)) {
			if (!inWater && !justCreated) {
				float f = MathHelper.sqrt(motX * motX * 0.20000000298023224D + motY * motY + motZ * motZ * 0.20000000298023224D) * 0.2F;

				if (f > 1.0F) {
					f = 1.0F;
				}

				makeSound(O(), f, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
				float f1 = MathHelper.floor(boundingBox.b);

				int i;
				float f2;
				float f3;

				for (i = 0; i < 1.0F + width * 20.0F; ++i) {
					f2 = (random.nextFloat() * 2.0F - 1.0F) * width;
					f3 = (random.nextFloat() * 2.0F - 1.0F) * width;
					world.addParticle("bubble", locX + f2, f1 + 1.0F, locZ + f3, motX, motY - random.nextFloat() * 0.2F, motZ);
				}

				for (i = 0; i < 1.0F + width * 20.0F; ++i) {
					f2 = (random.nextFloat() * 2.0F - 1.0F) * width;
					f3 = (random.nextFloat() * 2.0F - 1.0F) * width;
					world.addParticle("splash", locX + f2, f1 + 1.0F, locZ + f3, motX, motY, motZ);
				}
			}

			fallDistance = 0.0F;
			inWater = true;
			fireTicks = 0;
		} else {
			inWater = false;
		}

		return inWater;
	}

	protected String O() {
		return "game.neutral.swim.splash";
	}

	public boolean a(Material material) {
		double d0 = locY + getHeadHeight();
		int i = MathHelper.floor(locX);
		int j = MathHelper.d(MathHelper.floor(d0));
		int k = MathHelper.floor(locZ);
		Block block = world.getType(i, j, k);

		if (block.getMaterial() == material) {
			float f = BlockFluids.b(world.getData(i, j, k)) - 0.11111111F;
			float f1 = j + 1 - f;

			return d0 < f1;
		} else
			return false;
	}

	public float getHeadHeight() {
		return 0.0F;
	}

	public boolean P() {
		return world.a(boundingBox.grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
	}

	public void a(float f, float f1, float f2) {
		float f3 = f * f + f1 * f1;

		if (f3 >= 1.0E-4F) {
			f3 = MathHelper.c(f3);
			if (f3 < 1.0F) {
				f3 = 1.0F;
			}

			f3 = f2 / f3;
			f *= f3;
			f1 *= f3;
			float f4 = MathHelper.sin(yaw * 3.1415927F / 180.0F);
			float f5 = MathHelper.cos(yaw * 3.1415927F / 180.0F);

			motX += f * f5 - f1 * f4;
			motZ += f1 * f5 + f * f4;
		}
	}

	public float d(float f) {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locZ);

		if (world.isLoaded(i, 0, j)) {
			double d0 = (boundingBox.e - boundingBox.b) * 0.66D;
			int k = MathHelper.floor(locY - height + d0);

			return world.n(i, k, j);
		} else
			return 0.0F;
	}

	public void spawnIn(World world) {
		// CraftBukkit start
		if (world == null) {
			die();
			this.world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
			return;
		}
		// CraftBukkit end

		this.world = world;
	}

	public void setLocation(double d0, double d1, double d2, float f, float f1) {
		lastX = locX = d0;
		lastY = locY = d1;
		lastZ = locZ = d2;
		lastYaw = yaw = f;
		lastPitch = pitch = f1;
		V = 0.0F;
		double d3 = lastYaw - f;

		if (d3 < -180.0D) {
			lastYaw += 360.0F;
		}

		if (d3 >= 180.0D) {
			lastYaw -= 360.0F;
		}

		setPosition(locX, locY, locZ);
		this.b(f, f1);
	}

	public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
		S = lastX = locX = d0;
		T = lastY = locY = d1 + height;
		U = lastZ = locZ = d2;
		yaw = f;
		pitch = f1;
		setPosition(locX, locY, locZ);
	}

	public float e(Entity entity) {
		float f = (float) (locX - entity.locX);
		float f1 = (float) (locY - entity.locY);
		float f2 = (float) (locZ - entity.locZ);

		return MathHelper.c(f * f + f1 * f1 + f2 * f2);
	}

	public double e(double d0, double d1, double d2) {
		double d3 = locX - d0;
		double d4 = locY - d1;
		double d5 = locZ - d2;

		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	public double f(double d0, double d1, double d2) {
		double d3 = locX - d0;
		double d4 = locY - d1;
		double d5 = locZ - d2;

		return MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
	}

	public double f(Entity entity) {
		double d0 = locX - entity.locX;
		double d1 = locY - entity.locY;
		double d2 = locZ - entity.locZ;

		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public void b_(EntityHuman entityhuman) {
	}

	int numCollisions = 0; // Spigot

	public void collide(Entity entity) {
		if (entity.passenger != this && entity.vehicle != this) {
			double d0 = entity.locX - locX;
			double d1 = entity.locZ - locZ;
			double d2 = MathHelper.a(d0, d1);

			if (d2 >= 0.009999999776482582D) {
				d2 = MathHelper.sqrt(d2);
				d0 /= d2;
				d1 /= d2;
				double d3 = 1.0D / d2;

				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				d0 *= d3;
				d1 *= d3;
				d0 *= 0.05000000074505806D;
				d1 *= 0.05000000074505806D;
				d0 *= 1.0F - Y;
				d1 *= 1.0F - Y;
				this.g(-d0, 0.0D, -d1);
				entity.g(d0, 0.0D, d1);
			}
		}
	}

	public void g(double d0, double d1, double d2) {
		motX += d0;
		motY += d1;
		motZ += d2;
		al = true;
	}

	protected void Q() {
		velocityChanged = true;
	}

	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			Q();
			return false;
		}
	}

	public boolean R() {
		return false;
	}

	public boolean S() {
		return false;
	}

	public void b(Entity entity, int i) {
	}

	public boolean c(NBTTagCompound nbttagcompound) {
		String s = W();

		if (!dead && s != null) {
			nbttagcompound.setString("id", s);
			this.e(nbttagcompound);
			return true;
		} else
			return false;
	}

	public boolean d(NBTTagCompound nbttagcompound) {
		String s = W();

		if (!dead && s != null && passenger == null) {
			nbttagcompound.setString("id", s);
			this.e(nbttagcompound);
			return true;
		} else
			return false;
	}

	public void e(NBTTagCompound nbttagcompound) {
		try {
			nbttagcompound.set("Pos", this.a(new double[] { locX, locY + V, locZ }));
			nbttagcompound.set("Motion", this.a(new double[] { motX, motY, motZ }));

			// CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
			// TODO: make sure this is the best way to address this.
			if (Float.isNaN(yaw)) {
				yaw = 0;
			}

			if (Float.isNaN(pitch)) {
				pitch = 0;
			}
			// CraftBukkit end

			nbttagcompound.set("Rotation", this.a(new float[] { yaw, pitch }));
			nbttagcompound.setFloat("FallDistance", fallDistance);
			nbttagcompound.setShort("Fire", (short) fireTicks);
			nbttagcompound.setShort("Air", (short) getAirTicks());
			nbttagcompound.setBoolean("OnGround", onGround);
			nbttagcompound.setInt("Dimension", dimension);
			nbttagcompound.setBoolean("Invulnerable", invulnerable);
			nbttagcompound.setInt("PortalCooldown", portalCooldown);
			nbttagcompound.setLong("UUIDMost", getUniqueID().getMostSignificantBits());
			nbttagcompound.setLong("UUIDLeast", getUniqueID().getLeastSignificantBits());
			// CraftBukkit start
			nbttagcompound.setLong("WorldUUIDLeast", world.getDataManager().getUUID().getLeastSignificantBits());
			nbttagcompound.setLong("WorldUUIDMost", world.getDataManager().getUUID().getMostSignificantBits());
			nbttagcompound.setInt("Bukkit.updateLevel", CURRENT_LEVEL);
			nbttagcompound.setInt("Spigot.ticksLived", ticksLived);
			// CraftBukkit end
			this.b(nbttagcompound);
			if (vehicle != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();

				if (vehicle.c(nbttagcompound1)) {
					nbttagcompound.set("Riding", nbttagcompound1);
				}
			}
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.a(throwable, "Saving entity NBT");
			CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");

			this.a(crashreportsystemdetails);
			throw new ReportedException(crashreport);
		}
	}

	public void f(NBTTagCompound nbttagcompound) {
		try {
			NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
			NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
			NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);

			motX = nbttaglist1.d(0);
			motY = nbttaglist1.d(1);
			motZ = nbttaglist1.d(2);
			/* CraftBukkit start - Moved section down
			if (Math.abs(this.motX) > 10.0D) {
			    this.motX = 0.0D;
			}

			if (Math.abs(this.motY) > 10.0D) {
			    this.motY = 0.0D;
			}

			if (Math.abs(this.motZ) > 10.0D) {
			    this.motZ = 0.0D;
			}
			// CraftBukkit end */

			lastX = S = locX = nbttaglist.d(0);
			lastY = T = locY = nbttaglist.d(1);
			lastZ = U = locZ = nbttaglist.d(2);
			lastYaw = yaw = nbttaglist2.e(0);
			lastPitch = pitch = nbttaglist2.e(1);
			fallDistance = nbttagcompound.getFloat("FallDistance");
			fireTicks = nbttagcompound.getShort("Fire");
			setAirTicks(nbttagcompound.getShort("Air"));
			onGround = nbttagcompound.getBoolean("OnGround");
			dimension = nbttagcompound.getInt("Dimension");
			invulnerable = nbttagcompound.getBoolean("Invulnerable");
			portalCooldown = nbttagcompound.getInt("PortalCooldown");
			if (nbttagcompound.hasKeyOfType("UUIDMost", 4) && nbttagcompound.hasKeyOfType("UUIDLeast", 4)) {
				uniqueID = new UUID(nbttagcompound.getLong("UUIDMost"), nbttagcompound.getLong("UUIDLeast"));
			}

			setPosition(locX, locY, locZ);
			this.b(yaw, pitch);
			this.a(nbttagcompound);
			if (V()) {
				setPosition(locX, locY, locZ);
			}

			// CraftBukkit start
			if (this instanceof EntityLiving) {
				EntityLiving entity = (EntityLiving) this;

				ticksLived = nbttagcompound.getInt("Spigot.ticksLived");

				// Reset the persistence for tamed animals
				if (entity instanceof EntityTameableAnimal && !isLevelAtLeast(nbttagcompound, 2) && !nbttagcompound.getBoolean("PersistenceRequired")) {
					EntityInsentient entityinsentient = (EntityInsentient) entity;
					entityinsentient.persistent = !entityinsentient.isTypeNotPersistent();
				}
			}
			// CraftBukkit end

			// CraftBukkit start - Exempt Vehicles from notch's sanity check
			if (!(getBukkitEntity() instanceof Vehicle)) {
				if (Math.abs(motX) > 10.0D) {
					motX = 0.0D;
				}

				if (Math.abs(motY) > 10.0D) {
					motY = 0.0D;
				}

				if (Math.abs(motZ) > 10.0D) {
					motZ = 0.0D;
				}
			}
			// CraftBukkit end

			// CraftBukkit start - Reset world
			if (this instanceof EntityPlayer) {
				Server server = Bukkit.getServer();
				org.bukkit.World bworld = null;

				// TODO: Remove World related checks, replaced with WorldUID.
				String worldName = nbttagcompound.getString("World");

				if (nbttagcompound.hasKey("WorldUUIDMost") && nbttagcompound.hasKey("WorldUUIDLeast")) {
					UUID uid = new UUID(nbttagcompound.getLong("WorldUUIDMost"), nbttagcompound.getLong("WorldUUIDLeast"));
					bworld = server.getWorld(uid);
				} else {
					bworld = server.getWorld(worldName);
				}

				if (bworld == null) {
					EntityPlayer entityPlayer = (EntityPlayer) this;
					bworld = ((org.bukkit.craftbukkit.CraftServer) server).getServer().getWorldServer(entityPlayer.dimension).getWorld();
				}

				spawnIn(bworld == null ? null : ((CraftWorld) bworld).getHandle());
			}
			// CraftBukkit end
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.a(throwable, "Loading entity NBT");
			CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");

			this.a(crashreportsystemdetails);
			throw new ReportedException(crashreport);
		}
	}

	protected boolean V() {
		return true;
	}

	protected final String W() {
		return EntityTypes.b(this);
	}

	protected abstract void a(NBTTagCompound nbttagcompound);

	protected abstract void b(NBTTagCompound nbttagcompound);

	public void X() {
	}

	protected NBTTagList a(double... adouble) {
		NBTTagList nbttaglist = new NBTTagList();
		double[] adouble1 = adouble;
		int i = adouble.length;

		for (int j = 0; j < i; ++j) {
			double d0 = adouble1[j];

			nbttaglist.add(new NBTTagDouble(d0));
		}

		return nbttaglist;
	}

	protected NBTTagList a(float... afloat) {
		NBTTagList nbttaglist = new NBTTagList();
		float[] afloat1 = afloat;
		int i = afloat.length;

		for (int j = 0; j < i; ++j) {
			float f = afloat1[j];

			nbttaglist.add(new NBTTagFloat(f));
		}

		return nbttaglist;
	}

	public EntityItem a(Item item, int i) {
		return this.a(item, i, 0.0F);
	}

	public EntityItem a(Item item, int i, float f) {
		return this.a(new ItemStack(item, i, 0), f);
	}

	public EntityItem a(ItemStack itemstack, float f) {
		if (itemstack.count != 0 && itemstack.getItem() != null) {
			// CraftBukkit start - Capture drops for death event
			if (this instanceof EntityLiving && ((EntityLiving) this).drops != null) {
				((EntityLiving) this).drops.add(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemstack));
				return null;
			}
			// CraftBukkit end

			EntityItem entityitem = new EntityItem(world, locX, locY + f, locZ, itemstack);

			entityitem.pickupDelay = 10;
			world.addEntity(entityitem);
			return entityitem;
		} else
			return null;
	}

	public boolean isAlive() {
		return !dead;
	}

	public boolean inBlock() {
		for (int i = 0; i < 8; ++i) {
			float f = ((i >> 0) % 2 - 0.5F) * width * 0.8F;
			float f1 = ((i >> 1) % 2 - 0.5F) * 0.1F;
			float f2 = ((i >> 2) % 2 - 0.5F) * width * 0.8F;
			int j = MathHelper.floor(locX + f);
			int k = MathHelper.floor(locY + getHeadHeight() + f1);
			int l = MathHelper.floor(locZ + f2);

			if (world.getType(j, k, l).r())
				return true;
		}

		return false;
	}

	public boolean c(EntityHuman entityhuman) {
		return false;
	}

	public AxisAlignedBB h(Entity entity) {
		return null;
	}

	public void ab() {
		if (vehicle.dead) {
			vehicle = null;
		} else {
			motX = 0.0D;
			motY = 0.0D;
			motZ = 0.0D;
			this.h();
			if (vehicle != null) {
				vehicle.ac();
				h += vehicle.yaw - vehicle.lastYaw;

				for (g += vehicle.pitch - vehicle.lastPitch; h >= 180.0D; h -= 360.0D) {
					;
				}

				while (h < -180.0D) {
					h += 360.0D;
				}

				while (g >= 180.0D) {
					g -= 360.0D;
				}

				while (g < -180.0D) {
					g += 360.0D;
				}

				double d0 = h * 0.5D;
				double d1 = g * 0.5D;
				float f = 10.0F;

				if (d0 > f) {
					d0 = f;
				}

				if (d0 < -f) {
					d0 = -f;
				}

				if (d1 > f) {
					d1 = f;
				}

				if (d1 < -f) {
					d1 = -f;
				}

				h -= d0;
				g -= d1;
			}
		}
	}

	public void ac() {
		if (passenger != null) {
			passenger.setPosition(locX, locY + ae() + passenger.ad(), locZ); // Spigot
		}
	}

	public double ad() {
		return height;
	}

	public double ae() {
		return length * 0.75D;
	}

	public void mount(Entity entity) {
		// CraftBukkit start
		setPassengerOf(entity);
	}

	protected CraftEntity bukkitEntity;

	public CraftEntity getBukkitEntity() {
		if (bukkitEntity == null) {
			bukkitEntity = CraftEntity.getEntity(world.getServer(), this);
		}
		return bukkitEntity;
	}

	public void setPassengerOf(Entity entity) {
		// b(null) doesn't really fly for overloaded methods,
		// so this method is needed

		Entity originalVehicle = vehicle;
		Entity originalPassenger = vehicle == null ? null : vehicle.passenger;
		PluginManager pluginManager = Bukkit.getPluginManager();
		getBukkitEntity(); // make sure bukkitEntity is initialised
		// CraftBukkit end
		g = 0.0D;
		h = 0.0D;
		if (entity == null) {
			if (vehicle != null) {
				// CraftBukkit start
				if (bukkitEntity instanceof LivingEntity && vehicle.getBukkitEntity() instanceof Vehicle) {
					VehicleExitEvent event = new VehicleExitEvent((Vehicle) vehicle.getBukkitEntity(), (LivingEntity) bukkitEntity);
					pluginManager.callEvent(event);

					if (event.isCancelled() || vehicle != originalVehicle)
								return;
				}
				// CraftBukkit end
				pluginManager.callEvent(new org.spigotmc.event.entity.EntityDismountEvent(getBukkitEntity(), vehicle.getBukkitEntity())); // Spigot

				setPositionRotation(vehicle.locX, vehicle.boundingBox.b + vehicle.length, vehicle.locZ, yaw, pitch);
				vehicle.passenger = null;
			}

			vehicle = null;
		} else {
			// CraftBukkit start
			if (bukkitEntity instanceof LivingEntity && entity.getBukkitEntity() instanceof Vehicle && entity.world.isChunkLoaded((int) entity.locX >> 4, (int) entity.locZ >> 4)) {
				// It's possible to move from one vehicle to another.  We need to check if they're already in a vehicle, and fire an exit event if they are.
				VehicleExitEvent exitEvent = null;
				if (vehicle != null && vehicle.getBukkitEntity() instanceof Vehicle) {
					exitEvent = new VehicleExitEvent((Vehicle) vehicle.getBukkitEntity(), (LivingEntity) bukkitEntity);
					pluginManager.callEvent(exitEvent);

					if (exitEvent.isCancelled() || vehicle != originalVehicle || vehicle != null && vehicle.passenger != originalPassenger)
								return;
				}

				VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) entity.getBukkitEntity(), bukkitEntity);
				pluginManager.callEvent(event);

				// If a plugin messes with the vehicle or the vehicle's passenger
				if (event.isCancelled() || vehicle != originalVehicle || vehicle != null && vehicle.passenger != originalPassenger) {
					// If we only cancelled the enterevent then we need to put the player in a decent position.
					if (exitEvent != null && vehicle == originalVehicle && vehicle != null && vehicle.passenger == originalPassenger) {
						setPositionRotation(vehicle.locX, vehicle.boundingBox.b + vehicle.length, vehicle.locZ, yaw, pitch);
						vehicle.passenger = null;
						vehicle = null;
					}
					return;
				}
			}
			// CraftBukkit end
			// Spigot Start
			if (entity.world.isChunkLoaded((int) entity.locX >> 4, (int) entity.locZ >> 4)) {
				org.spigotmc.event.entity.EntityMountEvent event = new org.spigotmc.event.entity.EntityMountEvent(getBukkitEntity(), entity.getBukkitEntity());
				pluginManager.callEvent(event);
				if (event.isCancelled())
							return;
			}
			// Spigot End

			if (vehicle != null) {
				vehicle.passenger = null;
			}

			if (entity != null) {
				for (Entity entity1 = entity.vehicle; entity1 != null; entity1 = entity1.vehicle) {
					if (entity1 == this)
								return;
				}
			}

			vehicle = entity;
			entity.passenger = this;
		}
	}

	public float af() {
		return 0.1F;
	}

	public Vec3D ag() {
		return null;
	}

	public void ah() {
		if (portalCooldown > 0) {
			portalCooldown = ai();
		} else {
			double d0 = lastX - locX;
			double d1 = lastZ - locZ;

			if (!world.isStatic && !an) {
				aq = Direction.a(d0, d1);
			}

			an = true;
		}
	}

	public int ai() {
		return 300;
	}

	public ItemStack[] getEquipment() {
		return null;
	}

	public void setEquipment(int i, ItemStack itemstack) {
	}

	public boolean isBurning() {
		boolean flag = world != null && world.isStatic;

		return !fireProof && (fireTicks > 0 || flag && this.g(0));
	}

	public boolean am() {
		return vehicle != null;
	}

	public boolean isSneaking() {
		return this.g(1);
	}

	public void setSneaking(boolean flag) {
		this.a(1, flag);
	}

	public boolean isSprinting() {
		return this.g(3);
	}

	public void setSprinting(boolean flag) {
		this.a(3, flag);
	}

	public boolean isInvisible() {
		return this.g(5);
	}

	public void setInvisible(boolean flag) {
		this.a(5, flag);
	}

	public void e(boolean flag) {
		this.a(4, flag);
	}

	protected boolean g(int i) {
		return (datawatcher.getByte(0) & 1 << i) != 0;
	}

	protected void a(int i, boolean flag) {
		byte b0 = datawatcher.getByte(0);

		if (flag) {
			datawatcher.watch(0, Byte.valueOf((byte) (b0 | 1 << i)));
		} else {
			datawatcher.watch(0, Byte.valueOf((byte) (b0 & ~(1 << i))));
		}
	}

	public int getAirTicks() {
		return datawatcher.getShort(1);
	}

	public void setAirTicks(int i) {
		datawatcher.watch(1, Short.valueOf((short) i));
	}

	public void a(EntityLightning entitylightning) {
		// CraftBukkit start
		final org.bukkit.entity.Entity thisBukkitEntity = getBukkitEntity();
		final org.bukkit.entity.Entity stormBukkitEntity = entitylightning.getBukkitEntity();
		final PluginManager pluginManager = Bukkit.getPluginManager();

		if (thisBukkitEntity instanceof Hanging) {
			HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) thisBukkitEntity, stormBukkitEntity);
			PaintingBreakByEntityEvent paintingEvent = null;

			if (thisBukkitEntity instanceof Painting) {
				paintingEvent = new PaintingBreakByEntityEvent((Painting) thisBukkitEntity, stormBukkitEntity);
			}

			pluginManager.callEvent(hangingEvent);

			if (paintingEvent != null) {
				paintingEvent.setCancelled(hangingEvent.isCancelled());
				pluginManager.callEvent(paintingEvent);
			}

			if (hangingEvent.isCancelled() || paintingEvent != null && paintingEvent.isCancelled())
				return;
		}

		if (fireProof)
			return;
		CraftEventFactory.entityDamage = entitylightning;
		if (!damageEntity(DamageSource.FIRE, 5.0F)) {
			CraftEventFactory.entityDamage = null;
			return;
		}
		// CraftBukkit end

		++fireTicks;
		if (fireTicks == 0) {
			// CraftBukkit start - Call a combust event when lightning strikes
			EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
			pluginManager.callEvent(entityCombustEvent);
			if (!entityCombustEvent.isCancelled()) {
				setOnFire(entityCombustEvent.getDuration());
			}
			// CraftBukkit end
		}
	}

	public void a(EntityLiving entityliving) {
	}

	protected boolean j(double d0, double d1, double d2) {
		int i = MathHelper.floor(d0);
		int j = MathHelper.floor(d1);
		int k = MathHelper.floor(d2);
		double d3 = d0 - i;
		double d4 = d1 - j;
		double d5 = d2 - k;
		List list = world.a(boundingBox);

		if (list.isEmpty() && !world.q(i, j, k))
			return false;
		else {
			boolean flag = !world.q(i - 1, j, k);
			boolean flag1 = !world.q(i + 1, j, k);
			boolean flag2 = !world.q(i, j - 1, k);
			boolean flag3 = !world.q(i, j + 1, k);
			boolean flag4 = !world.q(i, j, k - 1);
			boolean flag5 = !world.q(i, j, k + 1);
			byte b0 = 3;
			double d6 = 9999.0D;

			if (flag && d3 < d6) {
				d6 = d3;
				b0 = 0;
			}

			if (flag1 && 1.0D - d3 < d6) {
				d6 = 1.0D - d3;
				b0 = 1;
			}

			if (flag3 && 1.0D - d4 < d6) {
				d6 = 1.0D - d4;
				b0 = 3;
			}

			if (flag4 && d5 < d6) {
				d6 = d5;
				b0 = 4;
			}

			if (flag5 && 1.0D - d5 < d6) {
				d6 = 1.0D - d5;
				b0 = 5;
			}

			float f = random.nextFloat() * 0.2F + 0.1F;

			if (b0 == 0) {
				motX = -f;
			}

			if (b0 == 1) {
				motX = f;
			}

			if (b0 == 2) {
				motY = -f;
			}

			if (b0 == 3) {
				motY = f;
			}

			if (b0 == 4) {
				motZ = -f;
			}

			if (b0 == 5) {
				motZ = f;
			}

			return true;
		}
	}

	public void as() {
		I = true;
		fallDistance = 0.0F;
	}

	public String getName() {
		String s = EntityTypes.b(this);

		if (s == null) {
			s = "generic";
		}

		return LocaleI18n.get("entity." + s + ".name");
	}

	public Entity[] at() {
		return null;
	}

	public boolean i(Entity entity) {
		return this == entity;
	}

	public float getHeadRotation() {
		return 0.0F;
	}

	public boolean av() {
		return true;
	}

	public boolean j(Entity entity) {
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] { this.getClass().getSimpleName(), getName(), Integer.valueOf(id), world == null ? "~NULL~" : world.getWorldData().getName(), Double.valueOf(locX), Double.valueOf(locY), Double.valueOf(locZ) });
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void k(Entity entity) {
		setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
	}

	public void a(Entity entity, boolean flag) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		entity.e(nbttagcompound);
		this.f(nbttagcompound);
		portalCooldown = entity.portalCooldown;
		aq = entity.aq;
	}

	public void b(int i) {
		if (!world.isStatic && !dead) {
			world.methodProfiler.a("changeDimension");
			MinecraftServer minecraftserver = MinecraftServer.getServer();
			// CraftBukkit start - Move logic into new function "teleportToLocation"
			// int j = this.dimension;
			WorldServer exitWorld = null;
			if (dimension < CraftWorld.CUSTOM_DIMENSION_OFFSET) { // Plugins must specify exit from custom Bukkit worlds
				// Only target existing worlds (compensate for allow-nether/allow-end as false)
				for (WorldServer world : minecraftserver.worlds) {
					if (world.dimension == i) {
						exitWorld = world;
					}
				}
			}

			Location enter = getBukkitEntity().getLocation();
			Location exit = exitWorld != null ? minecraftserver.getPlayerList().calculateTarget(enter, minecraftserver.getWorldServer(i)) : null;
			boolean useTravelAgent = exitWorld != null && !(dimension == 1 && exitWorld.dimension == 1); // don't use agent for custom worlds or return from THE_END

			TravelAgent agent = exit != null ? (TravelAgent) ((CraftWorld) exit.getWorld()).getHandle().getTravelAgent() : org.bukkit.craftbukkit.CraftTravelAgent.DEFAULT; // return arbitrary TA to compensate for implementation dependent plugins
			EntityPortalEvent event = new EntityPortalEvent(getBukkitEntity(), enter, exit, agent);
			event.useTravelAgent(useTravelAgent);
			event.getEntity().getServer().getPluginManager().callEvent(event);
			if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !isAlive())
				return;
			exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
			teleportTo(exit, true);
		}
	}

	public void teleportTo(Location exit, boolean portal) {
		if (true) {
			WorldServer worldserver = ((CraftWorld) getBukkitEntity().getLocation().getWorld()).getHandle();
			WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
			int i = worldserver1.dimension;
			// CraftBukkit end

			dimension = i;
			/* CraftBukkit start - TODO: Check if we need this
			if (j == 1 && i == 1) {
			    worldserver1 = minecraftserver.getWorldServer(0);
			    this.dimension = 0;
			}
			// CraftBukkit end */

			world.kill(this);
			dead = false;
			world.methodProfiler.a("reposition");
			// CraftBukkit start - Ensure chunks are loaded in case TravelAgent is not used which would initially cause chunks to load during find/create
			// minecraftserver.getPlayerList().a(this, j, worldserver, worldserver1);
			boolean before = worldserver1.chunkProviderServer.forceChunkLoad;
			worldserver1.chunkProviderServer.forceChunkLoad = true;
			//worldserver1.getMinecraftServer().getPlayerList().repositionEntity(this, exit, portal); // PaperSpigot - no... this entity is dead
			worldserver1.chunkProviderServer.forceChunkLoad = before;
			// CraftBukkit end
			world.methodProfiler.c("reloading");
			Entity entity = EntityTypes.createEntityByName(EntityTypes.b(this), worldserver1);

			if (entity != null) {
				entity.a(this, true);
				// PaperSpigot start - move entity to new location
				exit.getBlock(); // force load
				entity.setLocation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
				// PaperSpigot end
				/* CraftBukkit start - We need to do this...
				if (j == 1 && i == 1) {
				    ChunkCoordinates chunkcoordinates = worldserver1.getSpawn();

				    chunkcoordinates.y = this.world.i(chunkcoordinates.x, chunkcoordinates.z);
				    entity.setPositionRotation((double) chunkcoordinates.x, (double) chunkcoordinates.y, (double) chunkcoordinates.z, entity.yaw, entity.pitch);
				}
				// CraftBukkit end */
				worldserver1.addEntity(entity);
				// CraftBukkit start - Forward the CraftEntity to the new entity
				getBukkitEntity().setHandle(entity);
				entity.bukkitEntity = getBukkitEntity();
				// CraftBukkit end
			}

			dead = true;
			world.methodProfiler.b();
			worldserver.i();
			worldserver1.i();
			world.methodProfiler.b();
		}
	}

	public float a(Explosion explosion, World world, int i, int j, int k, Block block) {
		return block.a(this);
	}

	public boolean a(Explosion explosion, World world, int i, int j, int k, Block block, float f) {
		return true;
	}

	public int ax() {
		return 3;
	}

	public int ay() {
		return aq;
	}

	public boolean az() {
		return false;
	}

	public void a(CrashReportSystemDetails crashreportsystemdetails) {
		crashreportsystemdetails.a("Entity Type", new CrashReportEntityType(this));
		crashreportsystemdetails.a("Entity ID", Integer.valueOf(id));
		crashreportsystemdetails.a("Entity Name", new CrashReportEntityName(this));
		crashreportsystemdetails.a("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(locX), Double.valueOf(locY), Double.valueOf(locZ) }));
		crashreportsystemdetails.a("Entity\'s Block location", CrashReportSystemDetails.a(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)));
		crashreportsystemdetails.a("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(motX), Double.valueOf(motY), Double.valueOf(motZ) }));
	}

	public UUID getUniqueID() {
		return uniqueID;
	}

	public boolean aC() {
		return true;
	}

	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatComponentText(getName());
	}

	public void i(int i) {
	}
}
