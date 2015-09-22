package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.ExplosionPrimeEvent;

// CraftBukkit end

public class EntityEnderCrystal extends Entity {

	public int a;
	public int b;

	public EntityEnderCrystal(World world) {
		super(world);
		k = true;
		this.a(2.0F, 2.0F);
		height = length / 2.0F;
		b = 5;
		a = random.nextInt(100000);
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	protected void c() {
		datawatcher.a(8, Integer.valueOf(b));
	}

	@Override
	public void h() {
		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		++a;
		datawatcher.watch(8, Integer.valueOf(b));
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locY);
		int k = MathHelper.floor(locZ);

		if (world.worldProvider instanceof WorldProviderTheEnd && world.getType(i, j, k) != Blocks.FIRE) {
			// CraftBukkit start
			if (!CraftEventFactory.callBlockIgniteEvent(world, i, j, k, this).isCancelled()) {
				world.setTypeUpdate(i, j, k, Blocks.FIRE);
			}
			// CraftBukkit end
		}
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
	}

	@Override
	public boolean R() {
		return true;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			if (!dead && !world.isStatic) {
				// CraftBukkit start - All non-living entities need this
				if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f))
					return false;

				b = 0;
				if (b <= 0) {
					die();
					if (!world.isStatic) {
						// CraftBukkit start
						ExplosionPrimeEvent event = new ExplosionPrimeEvent(getBukkitEntity(), 6.0F, false);
						world.getServer().getPluginManager().callEvent(event);
						if (event.isCancelled()) {
							dead = false;
							return false;
						}
						world.createExplosion(this, locX, locY, locZ, event.getRadius(), event.getFire(), true);
						// CraftBukkit end
					}
				}
			}

			return true;
		}
	}
}
