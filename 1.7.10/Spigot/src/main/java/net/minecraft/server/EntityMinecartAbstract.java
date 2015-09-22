package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;

// CraftBukkit end

public abstract class EntityMinecartAbstract extends Entity {

	private boolean a;
	private String b;
	private static final int[][][] matrix = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };
	private int d;
	private double e;
	private double f;
	private double g;
	private double h;
	private double i;

	// CraftBukkit start
	public boolean slowWhenEmpty = true;
	private double derailedX = 0.5;
	private double derailedY = 0.5;
	private double derailedZ = 0.5;
	private double flyingX = 0.95;
	private double flyingY = 0.95;
	private double flyingZ = 0.95;
	public double maxSpeed = 0.4D;

	// CraftBukkit end

	public EntityMinecartAbstract(World world) {
		super(world);
		k = true;
		this.a(0.98F, 0.7F);
		height = length / 2.0F;
	}

	public static EntityMinecartAbstract a(World world, double d0, double d1, double d2, int i) {
		switch (i) {
		case 1:
			return new EntityMinecartChest(world, d0, d1, d2);

		case 2:
			return new EntityMinecartFurnace(world, d0, d1, d2);

		case 3:
			return new EntityMinecartTNT(world, d0, d1, d2);

		case 4:
			return new EntityMinecartMobSpawner(world, d0, d1, d2);

		case 5:
			return new EntityMinecartHopper(world, d0, d1, d2);

		case 6:
			return new EntityMinecartCommandBlock(world, d0, d1, d2);

		default:
			return new EntityMinecartRideable(world, d0, d1, d2);
		}
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
		datawatcher.a(20, new org.spigotmc.ProtocolData.DualInt(0, 0)); // Spigot - protocol patch
		datawatcher.a(21, new Integer(6));
		datawatcher.a(22, Byte.valueOf((byte) 0));
	}

	@Override
	public AxisAlignedBB h(Entity entity) {
		return entity.S() ? entity.boundingBox : null;
	}

	@Override
	public AxisAlignedBB J() {
		return null;
	}

	@Override
	public boolean S() {
		return true;
	}

	public EntityMinecartAbstract(World world, double d0, double d1, double d2) {
		this(world);
		setPosition(d0, d1, d2);
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
		if (!world.isStatic && !dead) {
			if (isInvulnerable())
				return false;
			else {
				// CraftBukkit start - fire VehicleDamageEvent
				Vehicle vehicle = (Vehicle) getBukkitEntity();
				org.bukkit.entity.Entity passenger = damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity();

				VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, f);
				world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled())
					return true;

				f = (float) event.getDamage();
				// CraftBukkit end

				this.j(-this.l());
				this.c(10);
				Q();
				setDamage(getDamage() + f * 10.0F);
				boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

				if (flag || getDamage() > 40.0F) {
					if (this.passenger != null) {
						this.passenger.mount(this);
					}

					// CraftBukkit start
					VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
					world.getServer().getPluginManager().callEvent(destroyEvent);

					if (destroyEvent.isCancelled()) {
						setDamage(40); // Maximize damage so this doesn't get triggered again right away
						return true;
					}
					// CraftBukkit end

					if (flag && !k_()) {
						die();
					} else {
						this.a(damagesource);
					}
				}

				return true;
			}
		} else
			return true;
	}

	public void a(DamageSource damagesource) {
		die();
		ItemStack itemstack = new ItemStack(Items.MINECART, 1);

		if (b != null) {
			itemstack.c(b);
		}

		this.a(itemstack, 0.0F);
	}

	@Override
	public boolean R() {
		return !dead;
	}

	@Override
	public void die() {
		super.die();
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

		if (getType() > 0) {
			this.c(getType() - 1);
		}

		if (getDamage() > 0.0F) {
			setDamage(getDamage() - 1.0F);
		}

		if (locY < -64.0D) {
			G();
		}

		int i;

		if (!world.isStatic && world instanceof WorldServer) {
			world.methodProfiler.a("portal");
			MinecraftServer minecraftserver = ((WorldServer) world).getMinecraftServer();

			i = D();
			if (an) {
				if (true || minecraftserver.getAllowNether()) { // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
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

		if (world.isStatic) {
			if (d > 0) {
				double d0 = locX + (e - locX) / d;
				double d1 = locY + (f - locY) / d;
				double d2 = locZ + (g - locZ) / d;
				double d3 = MathHelper.g(h - yaw);

				yaw = (float) (yaw + d3 / d);
				pitch = (float) (pitch + (this.i - pitch) / d);
				--d;
				setPosition(d0, d1, d2);
				this.b(yaw, pitch);
			} else {
				setPosition(locX, locY, locZ);
				this.b(yaw, pitch);
			}
		} else {
			lastX = locX;
			lastY = locY;
			lastZ = locZ;
			motY -= 0.03999999910593033D;
			int j = MathHelper.floor(locX);

			i = MathHelper.floor(locY);
			int k = MathHelper.floor(locZ);

			if (BlockMinecartTrackAbstract.b_(world, j, i - 1, k)) {
				--i;
			}

			double d4 = maxSpeed; // CraftBukkit
			double d5 = 0.0078125D;
			Block block = world.getType(j, i, k);

			if (BlockMinecartTrackAbstract.a(block)) {
				int l = world.getData(j, i, k);

				this.a(j, i, k, d4, d5, block, l);
				if (block == Blocks.ACTIVATOR_RAIL) {
					this.a(j, i, k, (l & 8) != 0);
				}
			} else {
				this.b(d4);
			}

			I();
			pitch = 0.0F;
			double d6 = lastX - locX;
			double d7 = lastZ - locZ;

			if (d6 * d6 + d7 * d7 > 0.001D) {
				yaw = (float) (Math.atan2(d7, d6) * 180.0D / 3.141592653589793D);
				if (a) {
					yaw += 180.0F;
				}
			}

			double d8 = MathHelper.g(yaw - lastYaw);

			if (d8 < -170.0D || d8 >= 170.0D) {
				yaw += 180.0F;
				a = !a;
			}

			this.b(yaw, pitch);

			// CraftBukkit start
			org.bukkit.World bworld = world.getWorld();
			Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
			Location to = new Location(bworld, locX, locY, locZ, yaw, pitch);
			Vehicle vehicle = (Vehicle) getBukkitEntity();

			world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

			if (!from.equals(to)) {
				world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
			}
			// CraftBukkit end

			List list = world.getEntities(this, boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

			if (list != null && !list.isEmpty()) {
				for (int i1 = 0; i1 < list.size(); ++i1) {
					Entity entity = (Entity) list.get(i1);

					if (entity != passenger && entity.S() && entity instanceof EntityMinecartAbstract) {
						entity.collide(this);
					}
				}
			}

			if (passenger != null && passenger.dead) {
				if (passenger.vehicle == this) {
					passenger.vehicle = null;
				}

				passenger = null;
			}
			// Spigot start - Make hoppers around this container minecart active.
			// Called each tick on each minecart.
			if (world.spigotConfig.altHopperTicking && this instanceof EntityMinecartContainer) {
				int xi = MathHelper.floor(boundingBox.a) - 1;
				int yi = MathHelper.floor(boundingBox.b) - 1;
				int zi = MathHelper.floor(boundingBox.c) - 1;
				int xf = MathHelper.floor(boundingBox.d) + 1;
				int yf = MathHelper.floor(boundingBox.e) + 1;
				int zf = MathHelper.floor(boundingBox.f) + 1;
				for (int a = xi; a <= xf; a++) {
					for (int b = yi; b <= yf; b++) {
						for (int c = zi; c <= zf; c++) {
							TileEntity tileEntity = world.getTileEntity(a, b, c);
							if (tileEntity instanceof TileEntityHopper) {
								((TileEntityHopper) tileEntity).makeTick();
							}
						}
					}
				}
			}
			// Spigot end
		}
	}

	public void a(int i, int j, int k, boolean flag) {
	}

	protected void b(double d0) {
		if (motX < -d0) {
			motX = -d0;
		}

		if (motX > d0) {
			motX = d0;
		}

		if (motZ < -d0) {
			motZ = -d0;
		}

		if (motZ > d0) {
			motZ = d0;
		}

		if (onGround) {
			// CraftBukkit start - replace magic numbers with our variables
			motX *= derailedX;
			motY *= derailedY;
			motZ *= derailedZ;
			// CraftBukkit end
		}

		move(motX, motY, motZ);
		if (!onGround) {
			// CraftBukkit start - replace magic numbers with our variables
			motX *= flyingX;
			motY *= flyingY;
			motZ *= flyingZ;
			// CraftBukkit end
		}
	}

	protected void a(int i, int j, int k, double d0, double d1, Block block, int l) {
		fallDistance = 0.0F;
		Vec3D vec3d = this.a(locX, locY, locZ);

		locY = j;
		boolean flag = false;
		boolean flag1 = false;

		if (block == Blocks.GOLDEN_RAIL) {
			flag = (l & 8) != 0;
			flag1 = !flag;
		}

		if (((BlockMinecartTrackAbstract) block).e()) {
			l &= 7;
		}

		if (l >= 2 && l <= 5) {
			locY = j + 1;
		}

		if (l == 2) {
			motX -= d1;
		}

		if (l == 3) {
			motX += d1;
		}

		if (l == 4) {
			motZ += d1;
		}

		if (l == 5) {
			motZ -= d1;
		}

		int[][] aint = matrix[l];
		double d2 = aint[1][0] - aint[0][0];
		double d3 = aint[1][2] - aint[0][2];
		double d4 = Math.sqrt(d2 * d2 + d3 * d3);
		double d5 = motX * d2 + motZ * d3;

		if (d5 < 0.0D) {
			d2 = -d2;
			d3 = -d3;
		}

		double d6 = Math.sqrt(motX * motX + motZ * motZ);

		if (d6 > 2.0D) {
			d6 = 2.0D;
		}

		motX = d6 * d2 / d4;
		motZ = d6 * d3 / d4;
		double d7;
		double d8;
		double d9;
		double d10;

		if (passenger != null && passenger instanceof EntityLiving) {
			d7 = ((EntityLiving) passenger).be;
			if (d7 > 0.0D) {
				d8 = -Math.sin(passenger.yaw * 3.1415927F / 180.0F);
				d9 = Math.cos(passenger.yaw * 3.1415927F / 180.0F);
				d10 = motX * motX + motZ * motZ;
				if (d10 < 0.01D) {
					motX += d8 * 0.1D;
					motZ += d9 * 0.1D;
					flag1 = false;
				}
			}
		}

		if (flag1) {
			d7 = Math.sqrt(motX * motX + motZ * motZ);
			if (d7 < 0.03D) {
				motX *= 0.0D;
				motY *= 0.0D;
				motZ *= 0.0D;
			} else {
				motX *= 0.5D;
				motY *= 0.0D;
				motZ *= 0.5D;
			}
		}

		d7 = 0.0D;
		d8 = i + 0.5D + aint[0][0] * 0.5D;
		d9 = k + 0.5D + aint[0][2] * 0.5D;
		d10 = i + 0.5D + aint[1][0] * 0.5D;
		double d11 = k + 0.5D + aint[1][2] * 0.5D;

		d2 = d10 - d8;
		d3 = d11 - d9;
		double d12;
		double d13;

		if (d2 == 0.0D) {
			locX = i + 0.5D;
			d7 = locZ - k;
		} else if (d3 == 0.0D) {
			locZ = k + 0.5D;
			d7 = locX - i;
		} else {
			d12 = locX - d8;
			d13 = locZ - d9;
			d7 = (d12 * d2 + d13 * d3) * 2.0D;
		}

		locX = d8 + d2 * d7;
		locZ = d9 + d3 * d7;
		setPosition(locX, locY + height, locZ);
		d12 = motX;
		d13 = motZ;
		if (passenger != null) {
			d12 *= 0.75D;
			d13 *= 0.75D;
		}

		if (d12 < -d0) {
			d12 = -d0;
		}

		if (d12 > d0) {
			d12 = d0;
		}

		if (d13 < -d0) {
			d13 = -d0;
		}

		if (d13 > d0) {
			d13 = d0;
		}

		move(d12, 0.0D, d13);
		if (aint[0][1] != 0 && MathHelper.floor(locX) - i == aint[0][0] && MathHelper.floor(locZ) - k == aint[0][2]) {
			setPosition(locX, locY + aint[0][1], locZ);
		} else if (aint[1][1] != 0 && MathHelper.floor(locX) - i == aint[1][0] && MathHelper.floor(locZ) - k == aint[1][2]) {
			setPosition(locX, locY + aint[1][1], locZ);
		}

		this.i();
		Vec3D vec3d1 = this.a(locX, locY, locZ);

		if (vec3d1 != null && vec3d != null) {
			double d14 = (vec3d.b - vec3d1.b) * 0.05D;

			d6 = Math.sqrt(motX * motX + motZ * motZ);
			if (d6 > 0.0D) {
				motX = motX / d6 * (d6 + d14);
				motZ = motZ / d6 * (d6 + d14);
			}

			setPosition(locX, vec3d1.b, locZ);
		}

		int i1 = MathHelper.floor(locX);
		int j1 = MathHelper.floor(locZ);

		if (i1 != i || j1 != k) {
			d6 = Math.sqrt(motX * motX + motZ * motZ);
			motX = d6 * (i1 - i);
			motZ = d6 * (j1 - k);
		}

		if (flag) {
			double d15 = Math.sqrt(motX * motX + motZ * motZ);

			if (d15 > 0.01D) {
				double d16 = 0.06D;

				motX += motX / d15 * d16;
				motZ += motZ / d15 * d16;
			} else if (l == 1) {
				if (world.getType(i - 1, j, k).r()) {
					motX = 0.02D;
				} else if (world.getType(i + 1, j, k).r()) {
					motX = -0.02D;
				}
			} else if (l == 0) {
				if (world.getType(i, j, k - 1).r()) {
					motZ = 0.02D;
				} else if (world.getType(i, j, k + 1).r()) {
					motZ = -0.02D;
				}
			}
		}
	}

	protected void i() {
		if (passenger != null || !slowWhenEmpty) { // CraftBukkit - add !this.slowWhenEmpty
			motX *= 0.996999979019165D;
			motY *= 0.0D;
			motZ *= 0.996999979019165D;
		} else {
			motX *= 0.9599999785423279D;
			motY *= 0.0D;
			motZ *= 0.9599999785423279D;
		}
	}

	public Vec3D a(double d0, double d1, double d2) {
		int i = MathHelper.floor(d0);
		int j = MathHelper.floor(d1);
		int k = MathHelper.floor(d2);

		if (BlockMinecartTrackAbstract.b_(world, i, j - 1, k)) {
			--j;
		}

		Block block = world.getType(i, j, k);

		if (BlockMinecartTrackAbstract.a(block)) {
			int l = world.getData(i, j, k);

			d1 = j;
			if (((BlockMinecartTrackAbstract) block).e()) {
				l &= 7;
			}

			if (l >= 2 && l <= 5) {
				d1 = j + 1;
			}

			int[][] aint = matrix[l];
			double d3 = 0.0D;
			double d4 = i + 0.5D + aint[0][0] * 0.5D;
			double d5 = j + 0.5D + aint[0][1] * 0.5D;
			double d6 = k + 0.5D + aint[0][2] * 0.5D;
			double d7 = i + 0.5D + aint[1][0] * 0.5D;
			double d8 = j + 0.5D + aint[1][1] * 0.5D;
			double d9 = k + 0.5D + aint[1][2] * 0.5D;
			double d10 = d7 - d4;
			double d11 = (d8 - d5) * 2.0D;
			double d12 = d9 - d6;

			if (d10 == 0.0D) {
				d0 = i + 0.5D;
				d3 = d2 - k;
			} else if (d12 == 0.0D) {
				d2 = k + 0.5D;
				d3 = d0 - i;
			} else {
				double d13 = d0 - d4;
				double d14 = d2 - d6;

				d3 = (d13 * d10 + d14 * d12) * 2.0D;
			}

			d0 = d4 + d10 * d3;
			d1 = d5 + d11 * d3;
			d2 = d6 + d12 * d3;
			if (d11 < 0.0D) {
				++d1;
			}

			if (d11 > 0.0D) {
				d1 += 0.5D;
			}

			return Vec3D.a(d0, d1, d2);
		} else
			return null;
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.getBoolean("CustomDisplayTile")) {
			this.k(nbttagcompound.getInt("DisplayTile"));
			this.l(nbttagcompound.getInt("DisplayData"));
			this.m(nbttagcompound.getInt("DisplayOffset"));
		}

		if (nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
			b = nbttagcompound.getString("CustomName");
		}
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
		if (t()) {
			nbttagcompound.setBoolean("CustomDisplayTile", true);
			nbttagcompound.setInt("DisplayTile", n().getMaterial() == Material.AIR ? 0 : Block.getId(n()));
			nbttagcompound.setInt("DisplayData", p());
			nbttagcompound.setInt("DisplayOffset", r());
		}

		if (b != null && b.length() > 0) {
			nbttagcompound.setString("CustomName", b);
		}
	}

	@Override
	public void collide(Entity entity) {
		if (!world.isStatic) {
			if (entity != passenger) {
				// CraftBukkit start
				Vehicle vehicle = (Vehicle) getBukkitEntity();
				org.bukkit.entity.Entity hitEntity = entity == null ? null : entity.getBukkitEntity();

				VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
				world.getServer().getPluginManager().callEvent(collisionEvent);

				if (collisionEvent.isCancelled())
					return;

				if (entity instanceof EntityLiving && !(entity instanceof EntityHuman) && !(entity instanceof EntityIronGolem) && this.m() == 0 && motX * motX + motZ * motZ > 0.01D && passenger == null && entity.vehicle == null) {
					entity.mount(this);
				}

				double d0 = entity.locX - locX;
				double d1 = entity.locZ - locZ;
				double d2 = d0 * d0 + d1 * d1;

				// CraftBukkit - collision
				if (d2 >= 9.999999747378752E-5D && !collisionEvent.isCollisionCancelled()) {
					d2 = MathHelper.sqrt(d2);
					d0 /= d2;
					d1 /= d2;
					double d3 = 1.0D / d2;

					if (d3 > 1.0D) {
						d3 = 1.0D;
					}

					d0 *= d3;
					d1 *= d3;
					d0 *= 0.10000000149011612D;
					d1 *= 0.10000000149011612D;
					d0 *= 1.0F - Y;
					d1 *= 1.0F - Y;
					d0 *= 0.5D;
					d1 *= 0.5D;
					if (entity instanceof EntityMinecartAbstract) {
						double d4 = entity.locX - locX;
						double d5 = entity.locZ - locZ;
						Vec3D vec3d = Vec3D.a(d4, 0.0D, d5).a();
						Vec3D vec3d1 = Vec3D.a(MathHelper.cos(yaw * 3.1415927F / 180.0F), 0.0D, MathHelper.sin(yaw * 3.1415927F / 180.0F)).a();
						double d6 = Math.abs(vec3d.b(vec3d1));

						if (d6 < 0.800000011920929D)
							return;

						double d7 = entity.motX + motX;
						double d8 = entity.motZ + motZ;

						if (((EntityMinecartAbstract) entity).m() == 2 && this.m() != 2) {
							motX *= 0.20000000298023224D;
							motZ *= 0.20000000298023224D;
							this.g(entity.motX - d0, 0.0D, entity.motZ - d1);
							entity.motX *= 0.949999988079071D;
							entity.motZ *= 0.949999988079071D;
						} else if (((EntityMinecartAbstract) entity).m() != 2 && this.m() == 2) {
							entity.motX *= 0.20000000298023224D;
							entity.motZ *= 0.20000000298023224D;
							entity.g(motX + d0, 0.0D, motZ + d1);
							motX *= 0.949999988079071D;
							motZ *= 0.949999988079071D;
						} else {
							d7 /= 2.0D;
							d8 /= 2.0D;
							motX *= 0.20000000298023224D;
							motZ *= 0.20000000298023224D;
							this.g(d7 - d0, 0.0D, d8 - d1);
							entity.motX *= 0.20000000298023224D;
							entity.motZ *= 0.20000000298023224D;
							entity.g(d7 + d0, 0.0D, d8 + d1);
						}
					} else {
						this.g(-d0, 0.0D, -d1);
						entity.g(d0 / 4.0D, 0.0D, d1 / 4.0D);
					}
				}
			}
		}
	}

	public void setDamage(float f) {
		datawatcher.watch(19, Float.valueOf(f));
	}

	public float getDamage() {
		return datawatcher.getFloat(19);
	}

	public void c(int i) {
		datawatcher.watch(17, Integer.valueOf(i));
	}

	public int getType() {
		return datawatcher.getInt(17);
	}

	public void j(int i) {
		datawatcher.watch(18, Integer.valueOf(i));
	}

	public int l() {
		return datawatcher.getInt(18);
	}

	public abstract int m();

	public Block n() {
		if (!t())
			return o();
		else {
			int i = getDataWatcher().getInt(20) & '\uffff';

			return Block.getById(i);
		}
	}

	public Block o() {
		return Blocks.AIR;
	}

	public int p() {
		return !t() ? q() : getDataWatcher().getInt(20) >> 16;
	}

	public int q() {
		return 0;
	}

	public int r() {
		return !t() ? s() : getDataWatcher().getInt(21);
	}

	public int s() {
		return 6;
	}

	public void k(int i) {
		// Spigot start - protocol patch
		org.spigotmc.ProtocolData.DualInt val = datawatcher.getDualInt(20);
		val.value = Integer.valueOf(i & '\uffff' | p() << 16);
		val.value2 = Integer.valueOf(i & '\uffff' | p() << 12);
		getDataWatcher().watch(20, val);
		// Spigot end
		this.a(true);
	}

	public void l(int i) {
		// Spigot start - protocol patch
		org.spigotmc.ProtocolData.DualInt val = datawatcher.getDualInt(20);
		val.value = Integer.valueOf(Block.getId(n()) & '\uffff' | i << 16);
		val.value2 = Integer.valueOf(Block.getId(n()) & '\uffff' | i << 12);
		getDataWatcher().watch(20, val);
		// Spigot end
		this.a(true);
	}

	public void m(int i) {
		getDataWatcher().watch(21, Integer.valueOf(i));
		this.a(true);
	}

	public boolean t() {
		return getDataWatcher().getByte(22) == 1;
	}

	public void a(boolean flag) {
		getDataWatcher().watch(22, Byte.valueOf((byte) (flag ? 1 : 0)));
	}

	public void a(String s) {
		b = s;
	}

	@Override
	public String getName() {
		return b != null ? b : super.getName();
	}

	public boolean k_() {
		return b != null;
	}

	public String u() {
		return b;
	}

	// CraftBukkit start - Methods for getting and setting flying and derailed velocity modifiers
	public Vector getFlyingVelocityMod() {
		return new Vector(flyingX, flyingY, flyingZ);
	}

	public void setFlyingVelocityMod(Vector flying) {
		flyingX = flying.getX();
		flyingY = flying.getY();
		flyingZ = flying.getZ();
	}

	public Vector getDerailedVelocityMod() {
		return new Vector(derailedX, derailedY, derailedZ);
	}

	public void setDerailedVelocityMod(Vector derailed) {
		derailedX = derailed.getX();
		derailedY = derailed.getY();
		derailedZ = derailed.getZ();
	}
	// CraftBukkit end
}
