package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.painting.PaintingBreakEvent;

// CraftBukkit end

public abstract class EntityHanging extends Entity {

	private int e;
	public int direction;
	public int x;
	public int y;
	public int z;

	public EntityHanging(World world) {
		super(world);
		height = 0.0F;
		this.a(0.5F, 0.5F);
	}

	public EntityHanging(World world, int i, int j, int k, int l) {
		this(world);
		x = i;
		y = j;
		z = k;
	}

	@Override
	protected void c() {
	}

	public void setDirection(int i) {
		direction = i;
		lastYaw = yaw = i * 90;
		float f = this.f();
		float f1 = this.i();
		float f2 = this.f();

		if (i != 2 && i != 0) {
			f = 0.5F;
		} else {
			f2 = 0.5F;
			yaw = lastYaw = Direction.f[i] * 90;
		}

		f /= 32.0F;
		f1 /= 32.0F;
		f2 /= 32.0F;
		float f3 = x + 0.5F;
		float f4 = y + 0.5F;
		float f5 = z + 0.5F;
		float f6 = 0.5625F;

		if (i == 2) {
			f5 -= f6;
		}

		if (i == 1) {
			f3 -= f6;
		}

		if (i == 0) {
			f5 += f6;
		}

		if (i == 3) {
			f3 += f6;
		}

		if (i == 2) {
			f3 -= this.c(this.f());
		}

		if (i == 1) {
			f5 += this.c(this.f());
		}

		if (i == 0) {
			f3 += this.c(this.f());
		}

		if (i == 3) {
			f5 -= this.c(this.f());
		}

		f4 += this.c(this.i());
		setPosition(f3, f4, f5);
		float f7 = -0.03125F;

		boundingBox.b(f3 - f - f7, f4 - f1 - f7, f5 - f2 - f7, f3 + f + f7, f4 + f1 + f7, f5 + f2 + f7);
	}

	private float c(int i) {
		return i == 32 ? 0.5F : i == 64 ? 0.5F : 0.0F;
	}

	@Override
	public void h() {
		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		if (e++ == world.spigotConfig.hangingTickFrequency && !world.isStatic) { // Spigot - 100 -> this.world.spigotConfig.hangingTickFrequency
			e = 0;
			if (!dead && !survives()) {
				// CraftBukkit start - fire break events
				Material material = world.getType((int) locX, (int) locY, (int) locZ).getMaterial();
				HangingBreakEvent.RemoveCause cause;

				if (!material.equals(Material.AIR)) {
					// TODO: This feels insufficient to catch 100% of suffocation cases
					cause = HangingBreakEvent.RemoveCause.OBSTRUCTION;
				} else {
					cause = HangingBreakEvent.RemoveCause.PHYSICS;
				}

				HangingBreakEvent event = new HangingBreakEvent((Hanging) getBukkitEntity(), cause);
				world.getServer().getPluginManager().callEvent(event);

				PaintingBreakEvent paintingEvent = null;
				if (this instanceof EntityPainting) {
					// Fire old painting event until it can be removed
					paintingEvent = new PaintingBreakEvent((Painting) getBukkitEntity(), PaintingBreakEvent.RemoveCause.valueOf(cause.name()));
					paintingEvent.setCancelled(event.isCancelled());
					world.getServer().getPluginManager().callEvent(paintingEvent);
				}

				if (dead || event.isCancelled() || paintingEvent != null && paintingEvent.isCancelled())
					return;

				die();
				this.b((Entity) null);
			}
		}
	}

	public boolean survives() {
		if (!world.getCubes(this, boundingBox).isEmpty())
			return false;
		else {
			int i = Math.max(1, this.f() / 16);
			int j = Math.max(1, this.i() / 16);
			int k = x;
			int l = y;
			int i1 = z;

			if (direction == 2) {
				k = MathHelper.floor(locX - this.f() / 32.0F);
			}

			if (direction == 1) {
				i1 = MathHelper.floor(locZ - this.f() / 32.0F);
			}

			if (direction == 0) {
				k = MathHelper.floor(locX - this.f() / 32.0F);
			}

			if (direction == 3) {
				i1 = MathHelper.floor(locZ - this.f() / 32.0F);
			}

			l = MathHelper.floor(locY - this.i() / 32.0F);

			for (int j1 = 0; j1 < i; ++j1) {
				for (int k1 = 0; k1 < j; ++k1) {
					Material material;

					if (direction != 2 && direction != 0) {
						material = world.getType(x, l + k1, i1 + j1).getMaterial();
					} else {
						material = world.getType(k + j1, l + k1, z).getMaterial();
					}

					if (!material.isBuildable())
						return false;
				}
			}

			List list = world.getEntities(this, boundingBox);
			Iterator iterator = list.iterator();

			Entity entity;

			do {
				if (!iterator.hasNext())
					return true;

				entity = (Entity) iterator.next();
			} while (!(entity instanceof EntityHanging));

			return false;
		}
	}

	@Override
	public boolean R() {
		return true;
	}

	@Override
	public boolean j(Entity entity) {
		return entity instanceof EntityHuman ? damageEntity(DamageSource.playerAttack((EntityHuman) entity), 0.0F) : false;
	}

	@Override
	public void i(int i) {
		world.X();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			if (!dead && !world.isStatic) {
				// CraftBukkit start - fire break events
				HangingBreakEvent event = new HangingBreakEvent((Hanging) getBukkitEntity(), HangingBreakEvent.RemoveCause.DEFAULT);
				PaintingBreakEvent paintingEvent = null;
				if (damagesource.getEntity() != null) {
					event = new org.bukkit.event.hanging.HangingBreakByEntityEvent((Hanging) getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity());

					if (this instanceof EntityPainting) {
						// Fire old painting event until it can be removed
						paintingEvent = new org.bukkit.event.painting.PaintingBreakByEntityEvent((Painting) getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity());
					}
				} else if (damagesource.isExplosion()) {
					event = new HangingBreakEvent((Hanging) getBukkitEntity(), HangingBreakEvent.RemoveCause.EXPLOSION);
				}

				world.getServer().getPluginManager().callEvent(event);

				if (paintingEvent != null) {
					paintingEvent.setCancelled(event.isCancelled());
					world.getServer().getPluginManager().callEvent(paintingEvent);
				}

				if (dead || event.isCancelled() || paintingEvent != null && paintingEvent.isCancelled())
					return true;

				die();
				Q();
				this.b(damagesource.getEntity());
			}

			return true;
		}
	}

	@Override
	public void move(double d0, double d1, double d2) {
		if (!world.isStatic && !dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) {
			if (dead)
				return; // CraftBukkit

			// CraftBukkit start - fire break events
			// TODO - Does this need its own cause? Seems to only be triggered by pistons
			HangingBreakEvent event = new HangingBreakEvent((Hanging) getBukkitEntity(), HangingBreakEvent.RemoveCause.PHYSICS);
			world.getServer().getPluginManager().callEvent(event);

			if (dead || event.isCancelled())
				return;

			die();
			this.b((Entity) null);
		}
	}

	@Override
	public void g(double d0, double d1, double d2) {
		if (false && !world.isStatic && !dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) { // CraftBukkit - not needed
			die();
			this.b((Entity) null);
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Direction", (byte) direction);
		nbttagcompound.setInt("TileX", x);
		nbttagcompound.setInt("TileY", y);
		nbttagcompound.setInt("TileZ", z);
		switch (direction) {
		case 0:
			nbttagcompound.setByte("Dir", (byte) 2);
			break;

		case 1:
			nbttagcompound.setByte("Dir", (byte) 1);
			break;

		case 2:
			nbttagcompound.setByte("Dir", (byte) 0);
			break;

		case 3:
			nbttagcompound.setByte("Dir", (byte) 3);
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKeyOfType("Direction", 99)) {
			direction = nbttagcompound.getByte("Direction");
		} else {
			switch (nbttagcompound.getByte("Dir")) {
			case 0:
				direction = 2;
				break;

			case 1:
				direction = 1;
				break;

			case 2:
				direction = 0;
				break;

			case 3:
				direction = 3;
			}
		}

		x = nbttagcompound.getInt("TileX");
		y = nbttagcompound.getInt("TileY");
		z = nbttagcompound.getInt("TileZ");
		setDirection(direction);
	}

	public abstract int f();

	public abstract int i();

	public abstract void b(Entity entity);

	@Override
	protected boolean V() {
		return false;
	}
}
