package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.ExplosionPrimeEvent;

// CraftBukkit end

public class EntityCreeper extends EntityMonster {

	private int bp;
	private int fuseTicks;
	private int maxFuseTicks = 30;
	private int explosionRadius = 3;
	private int record = -1; // CraftBukkit

	public EntityCreeper(World world) {
		super(world);
		goalSelector.a(1, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalSwell(this));
		goalSelector.a(3, new PathfinderGoalAvoidPlayer(this, EntityOcelot.class, 6.0F, 1.0D, 1.2D));
		goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
		goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8D));
		goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
		targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.d).setValue(0.25D);
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	public int ax() {
		return getGoalTarget() == null ? 3 : 3 + (int) (getHealth() - 1.0F);
	}

	@Override
	protected void b(float f) {
		super.b(f);
		fuseTicks = (int) (fuseTicks + f * 1.5F);
		if (fuseTicks > maxFuseTicks - 5) {
			fuseTicks = maxFuseTicks - 5;
		}
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, Byte.valueOf((byte) -1));
		datawatcher.a(17, Byte.valueOf((byte) 0));
		datawatcher.a(18, Byte.valueOf((byte) 0));
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		if (datawatcher.getByte(17) == 1) {
			nbttagcompound.setBoolean("powered", true);
		}

		nbttagcompound.setShort("Fuse", (short) maxFuseTicks);
		nbttagcompound.setByte("ExplosionRadius", (byte) explosionRadius);
		nbttagcompound.setBoolean("ignited", cc());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		datawatcher.watch(17, Byte.valueOf((byte) (nbttagcompound.getBoolean("powered") ? 1 : 0)));
		if (nbttagcompound.hasKeyOfType("Fuse", 99)) {
			maxFuseTicks = nbttagcompound.getShort("Fuse");
		}

		if (nbttagcompound.hasKeyOfType("ExplosionRadius", 99)) {
			explosionRadius = nbttagcompound.getByte("ExplosionRadius");
		}

		if (nbttagcompound.getBoolean("ignited")) {
			cd();
		}
	}

	@Override
	public void h() {
		if (isAlive()) {
			bp = fuseTicks;
			if (cc()) {
				this.a(1);
			}

			int i = cb();

			if (i > 0 && fuseTicks == 0) {
				makeSound("creeper.primed", 1.0F, 0.5F);
			}

			fuseTicks += i;
			if (fuseTicks < 0) {
				fuseTicks = 0;
			}

			if (fuseTicks >= maxFuseTicks) {
				fuseTicks = maxFuseTicks;
				ce();
			}
		}

		super.h();
	}

	@Override
	protected String aT() {
		return "mob.creeper.say";
	}

	@Override
	protected String aU() {
		return "mob.creeper.death";
	}

	@Override
	public void die(DamageSource damagesource) {
		// super.die(damagesource); // CraftBukkit - Moved to end
		if (damagesource.getEntity() instanceof EntitySkeleton) {
			int i = Item.getId(Items.RECORD_1);
			int j = Item.getId(Items.RECORD_12);
			int k = i + random.nextInt(j - i + 1);

			// CraftBukkit start - Store record for now, drop in dropDeathLoot
			// this.a(Item.getById(k), 1);
			record = k;
			// CraftBukkit end
		}

		super.die(damagesource); // CraftBukkit - Moved from above
	}

	// CraftBukkit start - Whole method
	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		super.dropDeathLoot(flag, i);

		// Drop a music disc?
		if (record != -1) {
			this.a(Item.getById(record), 1);
			record = -1;
		}
	}

	// CraftBukkit end

	@Override
	public boolean n(Entity entity) {
		return true;
	}

	public boolean isPowered() {
		return datawatcher.getByte(17) == 1;
	}

	@Override
	protected Item getLoot() {
		return Items.SULPHUR;
	}

	public int cb() {
		return datawatcher.getByte(16);
	}

	public void a(int i) {
		datawatcher.watch(16, Byte.valueOf((byte) i));
	}

	@Override
	public void a(EntityLightning entitylightning) {
		super.a(entitylightning);
		// CraftBukkit start
		if (CraftEventFactory.callCreeperPowerEvent(this, entitylightning, org.bukkit.event.entity.CreeperPowerEvent.PowerCause.LIGHTNING).isCancelled())
			return;

		setPowered(true);
	}

	public void setPowered(boolean powered) {
		if (!powered) {
			datawatcher.watch(17, Byte.valueOf((byte) 0));
		} else {
			datawatcher.watch(17, Byte.valueOf((byte) 1));
		}
		// CraftBukkit end
	}

	@Override
	protected boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() == Items.FLINT_AND_STEEL) {
			world.makeSound(locX + 0.5D, locY + 0.5D, locZ + 0.5D, "fire.ignite", 1.0F, random.nextFloat() * 0.4F + 0.8F);
			entityhuman.ba();
			if (!world.isStatic) {
				cd();
				itemstack.damage(1, entityhuman);
				return true;
			}
		}

		return super.a(entityhuman);
	}

	private void ce() {
		if (!world.isStatic) {
			boolean flag = world.getGameRules().getBoolean("mobGriefing");

			// CraftBukkit start
			float radius = isPowered() ? 6.0F : 3.0F;

			ExplosionPrimeEvent event = new ExplosionPrimeEvent(getBukkitEntity(), radius, false);
			world.getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				world.createExplosion(this, locX, locY, locZ, event.getRadius(), event.getFire(), flag);
				this.die();
			} else {
				fuseTicks = 0;
			}
			// CraftBukkit end
		}
	}

	public boolean cc() {
		return datawatcher.getByte(18) != 0;
	}

	public void cd() {
		datawatcher.watch(18, Byte.valueOf((byte) 1));
	}
}
