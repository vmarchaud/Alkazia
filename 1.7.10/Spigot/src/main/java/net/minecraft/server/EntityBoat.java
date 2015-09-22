package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

// CraftBukkit end

public class EntityBoat extends Entity {

	private boolean a;
	private double b;
	private int c;
	private double d;
	private double e;
	private double f;
	private double g;
	private double h;

	// CraftBukkit start
	public double maxSpeed = 0.4D;
	public double occupiedDeceleration = 0.2D;
	public double unoccupiedDeceleration = -1;
	public boolean landBoats = false;

	@Override
	public void collide(Entity entity) {
		org.bukkit.entity.Entity hitEntity = entity == null ? null : entity.getBukkitEntity();

		VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle) getBukkitEntity(), hitEntity);
		world.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		super.collide(entity);
	}

	// CraftBukkit end

	public EntityBoat(World world) {
		super(world);
		a = true;
		b = 0.07D;
		k = true;
		this.a(1.5F, 0.6F);
		height = length / 2.0F;
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	protected void c() {
		datawatcher.a(17, new Integer(0));
		datawatcher.a(18, new Integer(1));
		datawatcher.a(19, new Float(0.0F));
	}

	@Override
	public AxisAlignedBB h(Entity entity) {
		return entity.boundingBox;
	}

	@Override
	public AxisAlignedBB J() {
		return boundingBox;
	}

	@Override
	public boolean S() {
		return true;
	}

	public EntityBoat(World world, double d0, double d1, double d2) {
		this(world);
		setPosition(d0, d1 + height, d2);
		motX = 0.0D;
		motY = 0.0D;
		motZ = 0.0D;
		lastX = d0;
		lastY = d1;
		lastZ = d2;

		this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleCreateEvent((Vehicle) getBukkitEntity())); // CraftBukkit
	}

	@Override
	public double ae() {
		return length * 0.0D - 0.30000001192092896D;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (!world.isStatic && !dead) {
			// CraftBukkit start
			Vehicle vehicle = (Vehicle) getBukkitEntity();
			org.bukkit.entity.Entity attacker = damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity();

			VehicleDamageEvent event = new VehicleDamageEvent(vehicle, attacker, f);
			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled())
				return true;

			this.c(-this.i());
			this.a(10);
			setDamage(getDamage() + f * 10.0F);
			Q();
			boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

			if (flag || getDamage() > 40.0F) {
				// CraftBukkit start
				VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, attacker);
				world.getServer().getPluginManager().callEvent(destroyEvent);

				if (destroyEvent.isCancelled()) {
					setDamage(40F); // Maximize damage so this doesn't get triggered again right away
					return true;
				}
				// CraftBukkit end

				if (passenger != null) {
					passenger.mount(this);
				}

				if (!flag) {
					this.a(Items.BOAT, 1, 0.0F);
				}

				die();
			}

			return true;
		} else
			return true;
	}

	@Override
	public boolean R() {
		return !dead;
	}

	@Override
	public void h() {
		// CraftBukkit start
		double prevX = locX;
		double prevY = locY;
		double prevZ = locZ;
		float prevYaw = yaw;
		float prevPitch = pitch;
		// CraftBukkit end

		super.h();
		if (this.f() > 0) {
			this.a(this.f() - 1);
		}

		if (getDamage() > 0.0F) {
			setDamage(getDamage() - 1.0F);
		}

		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		byte b0 = 5;
		double d0 = 0.0D;

		for (int i = 0; i < b0; ++i) {
			double d1 = boundingBox.b + (boundingBox.e - boundingBox.b) * (i + 0) / b0 - 0.125D;
			double d2 = boundingBox.b + (boundingBox.e - boundingBox.b) * (i + 1) / b0 - 0.125D;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.a(boundingBox.a, d1, boundingBox.c, boundingBox.d, d2, boundingBox.f);

			if (world.b(axisalignedbb, Material.WATER)) {
				d0 += 1.0D / b0;
			}
		}

		double d3 = Math.sqrt(motX * motX + motZ * motZ);
		double d4;
		double d5;
		int j;

		if (d3 > 0.26249999999999996D) {
			d4 = Math.cos(yaw * 3.141592653589793D / 180.0D);
			d5 = Math.sin(yaw * 3.141592653589793D / 180.0D);

			for (j = 0; j < 1.0D + d3 * 60.0D; ++j) {
				double d6 = random.nextFloat() * 2.0F - 1.0F;
				double d7 = (random.nextInt(2) * 2 - 1) * 0.7D;
				double d8;
				double d9;

				if (random.nextBoolean()) {
					d8 = locX - d4 * d6 * 0.8D + d5 * d7;
					d9 = locZ - d5 * d6 * 0.8D - d4 * d7;
					world.addParticle("splash", d8, locY - 0.125D, d9, motX, motY, motZ);
				} else {
					d8 = locX + d4 + d5 * d6 * 0.7D;
					d9 = locZ + d5 - d4 * d6 * 0.7D;
					world.addParticle("splash", d8, locY - 0.125D, d9, motX, motY, motZ);
				}
			}
		}

		double d10;
		double d11;

		if (world.isStatic && a) {
			if (c > 0) {
				d4 = locX + (d - locX) / c;
				d5 = locY + (e - locY) / c;
				d10 = locZ + (f - locZ) / c;
				d11 = MathHelper.g(g - yaw);
				yaw = (float) (yaw + d11 / c);
				pitch = (float) (pitch + (h - pitch) / c);
				--c;
				setPosition(d4, d5, d10);
				this.b(yaw, pitch);
			} else {
				d4 = locX + motX;
				d5 = locY + motY;
				d10 = locZ + motZ;
				setPosition(d4, d5, d10);
				if (onGround) {
					motX *= 0.5D;
					motY *= 0.5D;
					motZ *= 0.5D;
				}

				motX *= 0.9900000095367432D;
				motY *= 0.949999988079071D;
				motZ *= 0.9900000095367432D;
			}
		} else {
			if (d0 < 1.0D) {
				d4 = d0 * 2.0D - 1.0D;
				motY += 0.03999999910593033D * d4;
			} else {
				if (motY < 0.0D) {
					motY /= 2.0D;
				}

				motY += 0.007000000216066837D;
			}

			if (passenger != null && passenger instanceof EntityLiving) {
				EntityLiving entityliving = (EntityLiving) passenger;
				float f = passenger.yaw + -entityliving.bd * 90.0F;

				motX += -Math.sin(f * 3.1415927F / 180.0F) * b * entityliving.be * 0.05000000074505806D;
				motZ += Math.cos(f * 3.1415927F / 180.0F) * b * entityliving.be * 0.05000000074505806D;
			}
			// CraftBukkit start - Support unoccupied deceleration
			else if (unoccupiedDeceleration >= 0) {
				motX *= unoccupiedDeceleration;
				motZ *= unoccupiedDeceleration;
				// Kill lingering speed
				if (motX <= 0.00001) {
					motX = 0;
				}
				if (motZ <= 0.00001) {
					motZ = 0;
				}
			}
			// CraftBukkit end

			d4 = Math.sqrt(motX * motX + motZ * motZ);
			if (d4 > 0.35D) {
				d5 = 0.35D / d4;
				motX *= d5;
				motZ *= d5;
				d4 = 0.35D;
			}

			if (d4 > d3 && b < 0.35D) {
				b += (0.35D - b) / 35.0D;
				if (b > 0.35D) {
					b = 0.35D;
				}
			} else {
				b -= (b - 0.07D) / 35.0D;
				if (b < 0.07D) {
					b = 0.07D;
				}
			}

			int k;

			for (k = 0; k < 4; ++k) {
				int l = MathHelper.floor(locX + (k % 2 - 0.5D) * 0.8D);

				j = MathHelper.floor(locZ + (k / 2 - 0.5D) * 0.8D);

				for (int i1 = 0; i1 < 2; ++i1) {
					int j1 = MathHelper.floor(locY) + i1;
					Block block = world.getType(l, j1, j);

					if (block == Blocks.SNOW) {
						// CraftBukkit start
						if (CraftEventFactory.callEntityChangeBlockEvent(this, l, j1, j, Blocks.AIR, 0).isCancelled()) {
							continue;
						}
						// CraftBukkit end
						world.setAir(l, j1, j);
						positionChanged = false;
					} else if (block == Blocks.WATER_LILY) {
						// CraftBukkit start
						if (CraftEventFactory.callEntityChangeBlockEvent(this, l, j1, j, Blocks.AIR, 0).isCancelled()) {
							continue;
						}
						// CraftBukkit end
						world.setAir(l, j1, j, true);
						positionChanged = false;
					}
				}
			}

			if (onGround && !landBoats) { // CraftBukkit
				motX *= 0.5D;
				motY *= 0.5D;
				motZ *= 0.5D;
			}

			move(motX, motY, motZ);
			if (positionChanged && d3 > 0.2D) {
				if (!world.isStatic && !dead) {
					// CraftBukkit start
					Vehicle vehicle = (Vehicle) getBukkitEntity();
					VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
					world.getServer().getPluginManager().callEvent(destroyEvent);
					if (!destroyEvent.isCancelled()) {
						die();

						for (k = 0; k < 3; ++k) {
							this.a(Item.getItemOf(Blocks.WOOD), 1, 0.0F);
						}

						for (k = 0; k < 2; ++k) {
							this.a(Items.STICK, 1, 0.0F);
						}
					}
					// CraftBukkit end
				}
			} else {
				motX *= 0.9900000095367432D;
				motY *= 0.949999988079071D;
				motZ *= 0.9900000095367432D;
			}

			pitch = 0.0F;
			d5 = yaw;
			d10 = lastX - locX;
			d11 = lastZ - locZ;
			if (d10 * d10 + d11 * d11 > 0.001D) {
				d5 = (float) (Math.atan2(d11, d10) * 180.0D / 3.141592653589793D);
			}

			double d12 = MathHelper.g(d5 - yaw);

			if (d12 > 20.0D) {
				d12 = 20.0D;
			}

			if (d12 < -20.0D) {
				d12 = -20.0D;
			}

			yaw = (float) (yaw + d12);
			this.b(yaw, pitch);

			// CraftBukkit start
			org.bukkit.Server server = world.getServer();
			org.bukkit.World bworld = world.getWorld();

			Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
			Location to = new Location(bworld, locX, locY, locZ, yaw, pitch);
			Vehicle vehicle = (Vehicle) getBukkitEntity();

			server.getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

			if (!from.equals(to)) {
				VehicleMoveEvent event = new VehicleMoveEvent(vehicle, from, to);
				server.getPluginManager().callEvent(event);
			}
			// CraftBukkit end

			if (!world.isStatic) {
				List list = world.getEntities(this, boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

				if (list != null && !list.isEmpty()) {
					for (int k1 = 0; k1 < list.size(); ++k1) {
						Entity entity = (Entity) list.get(k1);

						if (entity != passenger && entity.S() && entity instanceof EntityBoat) {
							entity.collide(this);
						}
					}
				}

				if (passenger != null && passenger.dead) {
					passenger.vehicle = null; // CraftBukkit
					passenger = null;
				}
			}
		}
	}

	@Override
	public void ac() {
		if (passenger != null) {
			double d0 = Math.cos(yaw * 3.141592653589793D / 180.0D) * 0.4D;
			double d1 = Math.sin(yaw * 3.141592653589793D / 180.0D) * 0.4D;

			passenger.setPosition(locX + d0, locY + ae() + passenger.ad(), locZ + d1);
		}
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
	}

	@Override
	public boolean c(EntityHuman entityhuman) {
		if (passenger != null && passenger instanceof EntityHuman && passenger != entityhuman)
			return true;
		else {
			if (!world.isStatic) {
				entityhuman.mount(this);
			}

			return true;
		}
	}

	@Override
	protected void a(double d0, boolean flag) {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locY);
		int k = MathHelper.floor(locZ);

		if (flag) {
			if (fallDistance > 3.0F) {
				this.b(fallDistance);
				if (!world.isStatic && !dead) {
					// CraftBukkit start
					Vehicle vehicle = (Vehicle) getBukkitEntity();
					VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
					world.getServer().getPluginManager().callEvent(destroyEvent);
					if (!destroyEvent.isCancelled()) {
						die();

						int l;

						for (l = 0; l < 3; ++l) {
							this.a(Item.getItemOf(Blocks.WOOD), 1, 0.0F);
						}

						for (l = 0; l < 2; ++l) {
							this.a(Items.STICK, 1, 0.0F);
						}
					}
					// CraftBukkit end
				}

				fallDistance = 0.0F;
			}
		} else if (world.getType(i, j - 1, k).getMaterial() != Material.WATER && d0 < 0.0D) {
			fallDistance = (float) (fallDistance - d0);
		}
	}

	public void setDamage(float f) {
		datawatcher.watch(19, Float.valueOf(f));
	}

	public float getDamage() {
		return datawatcher.getFloat(19);
	}

	public void a(int i) {
		datawatcher.watch(17, Integer.valueOf(i));
	}

	public int f() {
		return datawatcher.getInt(17);
	}

	public void c(int i) {
		datawatcher.watch(18, Integer.valueOf(i));
	}

	public int i() {
		return datawatcher.getInt(18);
	}
}
