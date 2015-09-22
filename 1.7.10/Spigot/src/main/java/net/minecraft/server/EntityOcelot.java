package net.minecraft.server;

public class EntityOcelot extends EntityTameableAnimal {

	public boolean spawnBonus = true; // Spigot
	private PathfinderGoalTempt bq;

	public EntityOcelot(World world) {
		super(world);
		this.a(0.6F, 0.8F);
		getNavigation().a(true);
		goalSelector.a(1, new PathfinderGoalFloat(this));
		goalSelector.a(2, bp);
		goalSelector.a(3, bq = new PathfinderGoalTempt(this, 0.6D, Items.RAW_FISH, true));
		goalSelector.a(4, new PathfinderGoalAvoidPlayer(this, EntityHuman.class, 16.0F, 0.8D, 1.33D));
		goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F));
		goalSelector.a(6, new PathfinderGoalJumpOnBlock(this, 1.33D));
		goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
		goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
		goalSelector.a(9, new PathfinderGoalBreed(this, 0.8D));
		goalSelector.a(10, new PathfinderGoalRandomStroll(this, 0.8D));
		goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
		targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed(this, EntityChicken.class, 750, false));
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(18, Byte.valueOf((byte) 0));
	}

	// Spigot start - When this ocelot begins standing, chests below this ocelot must be
	// updated as if its contents have changed. We update chests if this ocelot is sitting
	// knowing that it may be dead, gone, or standing after this method returns.
	// Called each tick on each ocelot.
	@Override
	public void h() {
		if (world.spigotConfig.altHopperTicking && isSitting()) {
			int xi = MathHelper.floor(boundingBox.a);
			int yi = MathHelper.floor(boundingBox.b) - 1;
			int zi = MathHelper.floor(boundingBox.c);
			int xf = MathHelper.floor(boundingBox.d);
			int yf = MathHelper.floor(boundingBox.e) - 1;
			int zf = MathHelper.floor(boundingBox.f);
			for (int a = xi; a <= xf; a++) {
				for (int c = zi; c <= zf; c++) {
					for (int b = yi; b <= yf; b++) {
						world.updateChestAndHoppers(a, b, c);
					}
				}
			}
		}
		super.h();
	}

	// Spigot end

	@Override
	public void bp() {
		if (getControllerMove().a()) {
			double d0 = getControllerMove().b();

			if (d0 == 0.6D) {
				setSneaking(true);
				setSprinting(false);
			} else if (d0 == 1.33D) {
				setSneaking(false);
				setSprinting(true);
			} else {
				setSneaking(false);
				setSprinting(false);
			}
		} else {
			setSneaking(false);
			setSprinting(false);
		}
	}

	@Override
	protected boolean isTypeNotPersistent() {
		return !isTamed() /*&& this.ticksLived > 2400*/; // CraftBukkit
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.30000001192092896D);
	}

	@Override
	protected void b(float f) {
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("CatType", getCatType());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setCatType(nbttagcompound.getInt("CatType"));
	}

	@Override
	protected String t() {
		return isTamed() ? ce() ? "mob.cat.purr" : random.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow" : "";
	}

	@Override
	protected String aT() {
		return "mob.cat.hitt";
	}

	@Override
	protected String aU() {
		return "mob.cat.hitt";
	}

	@Override
	protected float bf() {
		return 0.4F;
	}

	@Override
	protected Item getLoot() {
		return Items.LEATHER;
	}

	@Override
	public boolean n(Entity entity) {
		return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			bp.setSitting(false);
			return super.damageEntity(damagesource, f);
		}
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (isTamed()) {
			if (this.e(entityhuman) && !world.isStatic && !this.c(itemstack)) {
				bp.setSitting(!isSitting());
			}
		} else if (bq.f() && itemstack != null && itemstack.getItem() == Items.RAW_FISH && entityhuman.f(this) < 9.0D) {
			if (!entityhuman.abilities.canInstantlyBuild) {
				--itemstack.count;
			}

			if (itemstack.count <= 0) {
				entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
			}

			if (!world.isStatic) {
				// CraftBukkit - added event call and isCancelled check
				if (random.nextInt(3) == 0 && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
					setTamed(true);
					setCatType(1 + world.random.nextInt(3));
					setOwnerUUID(entityhuman.getUniqueID().toString());
					this.i(true);
					bp.setSitting(true);
					world.broadcastEntityEffect(this, (byte) 7);
				} else {
					this.i(false);
					world.broadcastEntityEffect(this, (byte) 6);
				}
			}

			return true;
		}

		return super.a(entityhuman);
	}

	public EntityOcelot b(EntityAgeable entityageable) {
		EntityOcelot entityocelot = new EntityOcelot(world);

		if (isTamed()) {
			entityocelot.setOwnerUUID(getOwnerUUID());
			entityocelot.setTamed(true);
			entityocelot.setCatType(getCatType());
		}

		return entityocelot;
	}

	@Override
	public boolean c(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.RAW_FISH;
	}

	@Override
	public boolean mate(EntityAnimal entityanimal) {
		if (entityanimal == this)
			return false;
		else if (!isTamed())
			return false;
		else if (!(entityanimal instanceof EntityOcelot))
			return false;
		else {
			EntityOcelot entityocelot = (EntityOcelot) entityanimal;

			return !entityocelot.isTamed() ? false : ce() && entityocelot.ce();
		}
	}

	public int getCatType() {
		return datawatcher.getByte(18);
	}

	public void setCatType(int i) {
		datawatcher.watch(18, Byte.valueOf((byte) i));
	}

	@Override
	public boolean canSpawn() {
		if (world.random.nextInt(3) == 0)
			return false;
		else {
			if (world.b(boundingBox) && world.getCubes(this, boundingBox).isEmpty() && !world.containsLiquid(boundingBox)) {
				int i = MathHelper.floor(locX);
				int j = MathHelper.floor(boundingBox.b);
				int k = MathHelper.floor(locZ);

				if (j < 63)
					return false;

				Block block = world.getType(i, j - 1, k);

				if (block == Blocks.GRASS || block.getMaterial() == Material.LEAVES)
					return true;
			}

			return false;
		}
	}

	@Override
	public String getName() {
		return hasCustomName() ? getCustomName() : isTamed() ? LocaleI18n.get("entity.Cat.name") : super.getName();
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		groupdataentity = super.prepare(groupdataentity);
		if (spawnBonus && world.random.nextInt(7) == 0) { // Spigot
			for (int i = 0; i < 2; ++i) {
				EntityOcelot entityocelot = new EntityOcelot(world);

				entityocelot.setPositionRotation(locX, locY, locZ, yaw, 0.0F);
				entityocelot.setAge(-24000);
				world.addEntity(entityocelot, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.OCELOT_BABY); // CraftBukkit - add SpawnReason
			}
		}

		return groupdataentity;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
