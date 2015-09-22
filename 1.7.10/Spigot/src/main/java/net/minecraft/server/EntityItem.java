package net.minecraft.server;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.player.PlayerPickupItemEvent; // CraftBukkit

public class EntityItem extends Entity {

	private static final Logger d = LogManager.getLogger();
	public int age;
	public int pickupDelay;
	private int e;
	private String f;
	private String g;
	public float c;
	private int lastTick = MinecraftServer.currentTick; // CraftBukkit

	public EntityItem(World world, double d0, double d1, double d2) {
		super(world);
		e = 5;
		c = (float) (Math.random() * 3.141592653589793D * 2.0D);
		this.a(0.25F, 0.25F);
		height = length / 2.0F;
		setPosition(d0, d1, d2);
		yaw = (float) (Math.random() * 360.0D);
		motX = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D);
		motY = 0.20000000298023224D;
		motZ = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D);
	}

	public EntityItem(World world, double d0, double d1, double d2, ItemStack itemstack) {
		this(world, d0, d1, d2);
		// CraftBukkit start - Can't set null items in the datawatcher
		if (itemstack == null || itemstack.getItem() == null)
			return;
		// CraftBukkit end
		setItemStack(itemstack);
	}

	@Override
	protected boolean g_() {
		return false;
	}

	public EntityItem(World world) {
		super(world);
		e = 5;
		c = (float) (Math.random() * 3.141592653589793D * 2.0D);
		this.a(0.25F, 0.25F);
		height = length / 2.0F;
	}

	@Override
	protected void c() {
		getDataWatcher().add(10, 5);
	}

	@Override
	public void h() {
		if (getItemStack() == null) {
			die();
		} else {
			super.h();
			// CraftBukkit start - Use wall time for pickup and despawn timers
			int elapsedTicks = MinecraftServer.currentTick - lastTick;
			pickupDelay -= elapsedTicks;
			age += elapsedTicks;
			lastTick = MinecraftServer.currentTick;
			// CraftBukkit end

			lastX = locX;
			lastY = locY;
			lastZ = locZ;
			motY -= 0.03999999910593033D;
			X = this.j(locX, (boundingBox.b + boundingBox.e) / 2.0D, locZ);
			move(motX, motY, motZ);
			boolean flag = (int) lastX != (int) locX || (int) lastY != (int) locY || (int) lastZ != (int) locZ;

			if (flag || ticksLived % 25 == 0) {
				if (world.getType(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)).getMaterial() == Material.LAVA) {
					motY = 0.20000000298023224D;
					motX = (random.nextFloat() - random.nextFloat()) * 0.2F;
					motZ = (random.nextFloat() - random.nextFloat()) * 0.2F;
					makeSound("random.fizz", 0.4F, 2.0F + random.nextFloat() * 0.4F);
				}

				if (!world.isStatic) {
					this.k();
				}
			}

			float f = 0.98F;

			if (onGround) {
				f = world.getType(MathHelper.floor(locX), MathHelper.floor(boundingBox.b) - 1, MathHelper.floor(locZ)).frictionFactor * 0.98F;
			}

			motX *= f;
			motY *= 0.9800000190734863D;
			motZ *= f;
			if (onGround) {
				motY *= -0.5D;
			}
			// Spigot start - Make the hopper(s) below this item active.
			// Called each tick on each item entity.
			if (world.spigotConfig.altHopperTicking) {
				int xi = MathHelper.floor(boundingBox.a);
				int yi = MathHelper.floor(boundingBox.b) - 1;
				int zi = MathHelper.floor(boundingBox.c);
				int xf = MathHelper.floor(boundingBox.d);
				int yf = MathHelper.floor(boundingBox.e) - 1;
				int zf = MathHelper.floor(boundingBox.f);
				for (int a = xi; a <= xf; a++) {
					for (int c = zi; c <= zf; c++) {
						for (int b = yi; b <= yf; b++) {
							TileEntity tileEntity = world.getTileEntity(a, b, c);
							if (tileEntity instanceof TileEntityHopper) {
								((TileEntityHopper) tileEntity).makeTick();
							}
						}
					}
				}
			}
			// Spigot end

			// ++this.age; // CraftBukkit - Moved up
			if (!world.isStatic && age >= world.spigotConfig.itemDespawnRate) { // Spigot
				// CraftBukkit start - fire ItemDespawnEvent
				if (org.bukkit.craftbukkit.event.CraftEventFactory.callItemDespawnEvent(this).isCancelled()) {
					age = 0;
					return;
				}
				// CraftBukkit end
				die();
			}
		}
	}

	private void k() {
		// Spigot start
		double radius = world.spigotConfig.itemMerge;
		Iterator iterator = world.a(EntityItem.class, boundingBox.grow(radius, radius, radius)).iterator();
		// Spigot end

		while (iterator.hasNext()) {
			EntityItem entityitem = (EntityItem) iterator.next();

			this.a(entityitem);
		}
	}

	public boolean a(EntityItem entityitem) {
		if (entityitem == this)
			return false;
		else if (entityitem.isAlive() && isAlive()) {
			ItemStack itemstack = getItemStack();
			ItemStack itemstack1 = entityitem.getItemStack();

			if (itemstack1.getItem() != itemstack.getItem())
				return false;
			else if (itemstack1.hasTag() ^ itemstack.hasTag())
				return false;
			else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag()))
				return false;
			else if (itemstack1.getItem() == null)
				return false;
			else if (itemstack1.getItem().n() && itemstack1.getData() != itemstack.getData())
				return false;
			else if (itemstack1.count < itemstack.count)
				return entityitem.a(this);
			else if (itemstack1.count + itemstack.count > itemstack1.getMaxStackSize())
				return false;
			else {
				// Spigot start
				itemstack.count += itemstack1.count;
				pickupDelay = Math.max(entityitem.pickupDelay, pickupDelay);
				age = Math.min(entityitem.age, age);
				setItemStack(itemstack);
				entityitem.die();
				// Spigot end
				return true;
			}
		} else
			return false;
	}

	public void e() {
		age = 4800;
	}

	@Override
	public boolean N() {
		return world.a(boundingBox, Material.WATER, this);
	}

	protected void burn(int i) {
		damageEntity(DamageSource.FIRE, i);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (getItemStack() != null && getItemStack().getItem() == Items.NETHER_STAR && damagesource.isExplosion())
			return false;
		else {
			Q();
			e = (int) (e - f);
			if (e <= 0) {
				die();
			}

			return false;
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("Health", (byte) e);
		nbttagcompound.setShort("Age", (short) age);
		if (this.j() != null) {
			nbttagcompound.setString("Thrower", f);
		}

		if (this.i() != null) {
			nbttagcompound.setString("Owner", g);
		}

		if (getItemStack() != null) {
			nbttagcompound.set("Item", getItemStack().save(new NBTTagCompound()));
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		e = nbttagcompound.getShort("Health") & 255;
		age = nbttagcompound.getShort("Age");
		if (nbttagcompound.hasKey("Owner")) {
			g = nbttagcompound.getString("Owner");
		}

		if (nbttagcompound.hasKey("Thrower")) {
			f = nbttagcompound.getString("Thrower");
		}

		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

		// CraftBukkit start - Handle missing "Item" compounds
		if (nbttagcompound1 != null) {
			ItemStack itemstack = ItemStack.createStack(nbttagcompound1);
			if (itemstack != null) {
				setItemStack(itemstack);
			} else {
				die();
			}
		} else {
			die();
		}
		// CraftBukkit end
		if (getItemStack() == null) {
			die();
		}
	}

	@Override
	public void b_(EntityHuman entityhuman) {
		if (!world.isStatic) {
			ItemStack itemstack = getItemStack();
			int i = itemstack.count;

			// CraftBukkit start - fire PlayerPickupItemEvent
			int canHold = entityhuman.inventory.canHold(itemstack);
			int remaining = itemstack.count - canHold;

			if (pickupDelay <= 0 && canHold > 0) {
				itemstack.count = canHold;
				PlayerPickupItemEvent event = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) getBukkitEntity(), remaining);
				// event.setCancelled(!entityhuman.canPickUpLoot); TODO
				world.getServer().getPluginManager().callEvent(event);
				itemstack.count = canHold + remaining;

				if (event.isCancelled())
					return;

				// Possibly < 0; fix here so we do not have to modify code below
				pickupDelay = 0;
			}
			// CraftBukkit end

			if (pickupDelay == 0 && (g == null || 6000 - age <= 200 || g.equals(entityhuman.getName())) && entityhuman.inventory.pickup(itemstack)) {
				if (itemstack.getItem() == Item.getItemOf(Blocks.LOG)) {
					entityhuman.a(AchievementList.g);
				}

				if (itemstack.getItem() == Item.getItemOf(Blocks.LOG2)) {
					entityhuman.a(AchievementList.g);
				}

				if (itemstack.getItem() == Items.LEATHER) {
					entityhuman.a(AchievementList.t);
				}

				if (itemstack.getItem() == Items.DIAMOND) {
					entityhuman.a(AchievementList.w);
				}

				if (itemstack.getItem() == Items.BLAZE_ROD) {
					entityhuman.a(AchievementList.A);
				}

				if (itemstack.getItem() == Items.DIAMOND && this.j() != null) {
					EntityHuman entityhuman1 = world.a(this.j());

					if (entityhuman1 != null && entityhuman1 != entityhuman) {
						entityhuman1.a(AchievementList.x);
					}
				}

				world.makeSound(entityhuman, "random.pop", 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityhuman.receive(this, i);
				if (itemstack.count <= 0) {
					die();
				}
			}
		}
	}

	@Override
	public String getName() {
		return LocaleI18n.get("item." + getItemStack().a());
	}

	@Override
	public boolean av() {
		return false;
	}

	@Override
	public void b(int i) {
		super.b(i);
		if (!world.isStatic) {
			this.k();
		}
	}

	public ItemStack getItemStack() {
		ItemStack itemstack = getDataWatcher().getItemStack(10);

		return itemstack == null ? new ItemStack(Blocks.STONE) : itemstack;
	}

	public void setItemStack(ItemStack itemstack) {
		getDataWatcher().watch(10, itemstack);
		getDataWatcher().update(10);
	}

	public String i() {
		return g;
	}

	public void a(String s) {
		g = s;
	}

	public String j() {
		return f;
	}

	public void b(String s) {
		f = s;
	}
}
