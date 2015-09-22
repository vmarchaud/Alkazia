package net.minecraft.server;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
//CraftBukkit end
//CraftBukkit start

public class EntityZombie extends EntityMonster {

	protected static final IAttribute bp = new AttributeRanged("zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D).a("Spawn Reinforcements Chance");
	private static final UUID bq = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	// PaperSpigot - Configurable baby zombie movement speed
	private static final AttributeModifier br = new AttributeModifier(bq, "Baby speed boost", org.clipspigot.ClipSpigotConfig.babyZombieMovementSpeed, 1);
	private final PathfinderGoalBreakDoor bs = new PathfinderGoalBreakDoor(this);
	private int bt;
	private boolean bu = false;
	private float bv = -1.0F;
	private float bw;
	private int lastTick = MinecraftServer.currentTick; // CraftBukkit - add field

	public EntityZombie(World world) {
		super(world);
		getNavigation().b(true);
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
		if (world.spigotConfig.zombieAggressiveTowardsVillager) {
			goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityVillager.class, 1.0D, true));
		} // Spigot
		goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
		goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
		goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
		if (world.spigotConfig.zombieAggressiveTowardsVillager) {
			targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, 0, false));
		} // Spigot
		this.a(0.6F, 1.8F);
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.b).setValue(40.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.23000000417232513D);
		getAttributeInstance(GenericAttributes.e).setValue(3.0D);
		getAttributeMap().b(bp).setValue(random.nextDouble() * 0.10000000149011612D);
	}

	@Override
	protected void c() {
		super.c();
		getDataWatcher().a(12, Byte.valueOf((byte) 0));
		getDataWatcher().a(13, Byte.valueOf((byte) 0));
		getDataWatcher().a(14, Byte.valueOf((byte) 0));
	}

	@Override
	public int aV() {
		int i = super.aV() + 2;

		if (i > 20) {
			i = 20;
		}

		return i;
	}

	@Override
	protected boolean bk() {
		return true;
	}

	public boolean bZ() {
		return bu;
	}

	public void a(boolean flag) {
		if (bu != flag) {
			bu = flag;
			if (flag) {
				goalSelector.a(1, bs);
			} else {
				goalSelector.a(bs);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return getDataWatcher().getByte(12) == 1;
	}

	@Override
	protected int getExpValue(EntityHuman entityhuman) {
		if (isBaby()) {
			b = (int) (b * 2.5F);
		}

		return super.getExpValue(entityhuman);
	}

	public void setBaby(boolean flag) {
		getDataWatcher().watch(12, Byte.valueOf((byte) (flag ? 1 : 0)));
		if (world != null && !world.isStatic) {
			AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

			attributeinstance.b(br);
			if (flag) {
				attributeinstance.a(br);
			}
		}

		this.k(flag);
	}

	public boolean isVillager() {
		return getDataWatcher().getByte(13) == 1;
	}

	public void setVillager(boolean flag) {
		getDataWatcher().watch(13, Byte.valueOf((byte) (flag ? 1 : 0)));
	}

	@Override
	public void e() {
		if (world.w() && !world.isStatic && !isBaby()) {
			float f = this.d(1.0F);

			if (f > 0.5F && random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && world.i(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ))) {
				boolean flag = true;
				ItemStack itemstack = this.getEquipment(4);

				if (itemstack != null) {
					if (itemstack.g()) {
						itemstack.setData(itemstack.j() + random.nextInt(2));
						if (itemstack.j() >= itemstack.l()) {
							this.a(itemstack);
							setEquipment(4, (ItemStack) null);
						}
					}

					flag = false;
				}

				if (flag) {
					// CraftBukkit start
					EntityCombustEvent event = new EntityCombustEvent(getBukkitEntity(), 8);
					world.getServer().getPluginManager().callEvent(event);

					if (!event.isCancelled()) {
						setOnFire(event.getDuration());
					}
					// CraftBukkit end
				}
			}
		}

		if (am() && getGoalTarget() != null && vehicle instanceof EntityChicken) {
			((EntityInsentient) vehicle).getNavigation().a(getNavigation().e(), 1.5D);
		}

		super.e();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (!super.damageEntity(damagesource, f))
			return false;
		else {
			EntityLiving entityliving = getGoalTarget();

			if (entityliving == null && bT() instanceof EntityLiving) {
				entityliving = (EntityLiving) bT();
			}

			if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
				entityliving = (EntityLiving) damagesource.getEntity();
			}

			if (entityliving != null && world.difficulty == EnumDifficulty.HARD && random.nextFloat() < getAttributeInstance(bp).getValue()) {
				int i = MathHelper.floor(locX);
				int j = MathHelper.floor(locY);
				int k = MathHelper.floor(locZ);
				EntityZombie entityzombie = new EntityZombie(world);

				for (int l = 0; l < 50; ++l) {
					int i1 = i + MathHelper.nextInt(random, 7, 40) * MathHelper.nextInt(random, -1, 1);
					int j1 = j + MathHelper.nextInt(random, 7, 40) * MathHelper.nextInt(random, -1, 1);
					int k1 = k + MathHelper.nextInt(random, 7, 40) * MathHelper.nextInt(random, -1, 1);

					if (World.a(world, i1, j1 - 1, k1) && world.getLightLevel(i1, j1, k1) < 10) {
						entityzombie.setPosition(i1, j1, k1);
						if (world.b(entityzombie.boundingBox) && world.getCubes(entityzombie, entityzombie.boundingBox).isEmpty() && !world.containsLiquid(entityzombie.boundingBox)) {
							world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.REINFORCEMENTS); // CraftBukkit
							// CraftBukkit start - call EntityTargetEvent
							org.bukkit.event.entity.EntityTargetLivingEntityEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetLivingEvent(entityzombie, entityliving, EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET);
							if (!event.isCancelled()) {
								if (event.getTarget() == null) {
									entityzombie.setGoalTarget(null);
								} else {
									entityzombie.setGoalTarget(((org.bukkit.craftbukkit.entity.CraftLivingEntity) event.getTarget()).getHandle());
								}
							}
							// CraftBukkit end
							entityzombie.prepare((GroupDataEntity) null);
							getAttributeInstance(bp).a(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
							entityzombie.getAttributeInstance(bp).a(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
							break;
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public void h() {
		if (!world.isStatic && cc()) {
			int i = ce();

			// CraftBukkit start - Use wall time instead of ticks for villager conversion
			int elapsedTicks = MinecraftServer.currentTick - lastTick;
			lastTick = MinecraftServer.currentTick;
			i *= elapsedTicks;
			// CraftBukkit end

			bt -= i;
			if (bt <= 0) {
				cd();
			}
		}

		super.h();
	}

	@Override
	public boolean n(Entity entity) {
		boolean flag = super.n(entity);

		if (flag) {
			int i = world.difficulty.a();

			if (be() == null && isBurning() && random.nextFloat() < i * 0.3F) {
				// CraftBukkit start
				EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), 2 * i);
				world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					entity.setOnFire(event.getDuration());
				}
				// CraftBukkit end
			}
		}

		return flag;
	}

	@Override
	protected String t() {
		return "mob.zombie.say";
	}

	@Override
	protected String aT() {
		return "mob.zombie.hurt";
	}

	@Override
	protected String aU() {
		return "mob.zombie.death";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.zombie.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getLoot() {
		return Items.ROTTEN_FLESH;
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.UNDEAD;
	}

	@Override
	protected void getRareDrop(int i) {
		switch (random.nextInt(3)) {
		case 0:
			this.a(Items.IRON_INGOT, 1);
			break;

		case 1:
			this.a(Items.CARROT, 1);
			break;

		case 2:
			this.a(Items.POTATO, 1);
		}
	}

	@Override
	protected void bC() {
		super.bC();
		if (random.nextFloat() < (world.difficulty == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
			int i = random.nextInt(3);

			if (i == 0) {
				setEquipment(0, new ItemStack(Items.IRON_SWORD));
			} else {
				setEquipment(0, new ItemStack(Items.IRON_SPADE));
			}
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		if (isBaby()) {
			nbttagcompound.setBoolean("IsBaby", true);
		}

		if (isVillager()) {
			nbttagcompound.setBoolean("IsVillager", true);
		}

		nbttagcompound.setInt("ConversionTime", cc() ? bt : -1);
		nbttagcompound.setBoolean("CanBreakDoors", bZ());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		if (nbttagcompound.getBoolean("IsBaby")) {
			setBaby(true);
		}

		if (nbttagcompound.getBoolean("IsVillager")) {
			setVillager(true);
		}

		if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
			this.a(nbttagcompound.getInt("ConversionTime"));
		}

		this.a(nbttagcompound.getBoolean("CanBreakDoors"));
	}

	@Override
	public void a(EntityLiving entityliving) {
		super.a(entityliving);
		if ((world.difficulty == EnumDifficulty.NORMAL || world.difficulty == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
			if (world.difficulty != EnumDifficulty.HARD && random.nextBoolean())
				return;

			EntityZombie entityzombie = new EntityZombie(world);

			entityzombie.k(entityliving);
			world.kill(entityliving);
			entityzombie.prepare((GroupDataEntity) null);
			entityzombie.setVillager(true);
			if (entityliving.isBaby()) {
				entityzombie.setBaby(true);
			}

			world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.INFECTION); // CraftBukkit - add SpawnReason
			world.a((EntityHuman) null, 1016, (int) locX, (int) locY, (int) locZ, 0);
		}
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		Object object = super.prepare(groupdataentity);
		float f = world.b(locX, locY, locZ);

		this.h(random.nextFloat() < 0.55F * f);
		if (object == null) {
			object = new GroupDataZombie(this, world.random.nextFloat() < 0.05F, world.random.nextFloat() < 0.05F, (EmptyClassZombie) null);
		}

		if (object instanceof GroupDataZombie) {
			GroupDataZombie groupdatazombie = (GroupDataZombie) object;

			if (groupdatazombie.b) {
				setVillager(true);
			}

			if (groupdatazombie.a) {
				setBaby(true);
				if (world.random.nextFloat() < 0.05D) {
					List list = world.a(EntityChicken.class, boundingBox.grow(5.0D, 3.0D, 5.0D), IEntitySelector.b);

					if (!list.isEmpty()) {
						EntityChicken entitychicken = (EntityChicken) list.get(0);

						entitychicken.i(true);
						mount(entitychicken);
					}
				} else if (world.random.nextFloat() < 0.05D) {
					EntityChicken entitychicken1 = new EntityChicken(world);

					entitychicken1.setPositionRotation(locX, locY, locZ, yaw, 0.0F);
					entitychicken1.prepare((GroupDataEntity) null);
					entitychicken1.i(true);
					world.addEntity(entitychicken1, CreatureSpawnEvent.SpawnReason.MOUNT);
					mount(entitychicken1);
				}
			}
		}

		this.a(random.nextFloat() < f * 0.1F);
		bC();
		bD();
		if (this.getEquipment(4) == null) {
			Calendar calendar = world.V();

			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && random.nextFloat() < 0.25F) {
				setEquipment(4, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				dropChances[4] = 0.0F;
			}
		}

		getAttributeInstance(GenericAttributes.c).a(new AttributeModifier("Random spawn bonus", random.nextDouble() * 0.05000000074505806D, 0));
		double d0 = random.nextDouble() * 1.5D * world.b(locX, locY, locZ);

		if (d0 > 1.0D) {
			getAttributeInstance(GenericAttributes.b).a(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
		}

		if (random.nextFloat() < f * 0.05F) {
			getAttributeInstance(bp).a(new AttributeModifier("Leader zombie bonus", random.nextDouble() * 0.25D + 0.5D, 0));
			getAttributeInstance(GenericAttributes.maxHealth).a(new AttributeModifier("Leader zombie bonus", random.nextDouble() * 3.0D + 1.0D, 2));
			this.a(true);
		}

		return (GroupDataEntity) object;
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.bF();

		if (itemstack != null && itemstack.getItem() == Items.GOLDEN_APPLE && itemstack.getData() == 0 && isVillager() && this.hasEffect(MobEffectList.WEAKNESS)) {
			if (!entityhuman.abilities.canInstantlyBuild) {
				--itemstack.count;
			}

			if (itemstack.count <= 0) {
				entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
			}

			if (!world.isStatic) {
				this.a(random.nextInt(2401) + 3600);
			}

			return true;
		} else
			return false;
	}

	protected void a(int i) {
		bt = i;
		getDataWatcher().watch(14, Byte.valueOf((byte) 1));
		removeEffect(MobEffectList.WEAKNESS.id);
		addEffect(new MobEffect(MobEffectList.INCREASE_DAMAGE.id, i, Math.min(world.difficulty.a() - 1, 0)));
		world.broadcastEntityEffect(this, (byte) 16);
	}

	@Override
	protected boolean isTypeNotPersistent() {
		return !cc();
	}

	public boolean cc() {
		return getDataWatcher().getByte(14) == 1;
	}

	protected void cd() {
		EntityVillager entityvillager = new EntityVillager(world);

		entityvillager.k(this);
		entityvillager.prepare((GroupDataEntity) null);
		entityvillager.cd();
		if (isBaby()) {
			entityvillager.setAge(-24000);
		}

		world.kill(this);
		world.addEntity(entityvillager, CreatureSpawnEvent.SpawnReason.CURED); // CraftBukkit - add SpawnReason
		entityvillager.addEffect(new MobEffect(MobEffectList.CONFUSION.id, 200, 0));
		world.a((EntityHuman) null, 1017, (int) locX, (int) locY, (int) locZ, 0);
	}

	protected int ce() {
		int i = 1;

		if (random.nextFloat() < 0.01F) {
			int j = 0;

			for (int k = (int) locX - 4; k < (int) locX + 4 && j < 14; ++k) {
				for (int l = (int) locY - 4; l < (int) locY + 4 && j < 14; ++l) {
					for (int i1 = (int) locZ - 4; i1 < (int) locZ + 4 && j < 14; ++i1) {
						Block block = world.getType(k, l, i1);

						if (block == Blocks.IRON_FENCE || block == Blocks.BED) {
							if (random.nextFloat() < 0.3F) {
								++i;
							}

							++j;
						}
					}
				}
			}
		}

		return i;
	}

	public void k(boolean flag) {
		this.a(flag ? 0.5F : 1.0F);
	}

	@Override
	protected final void a(float f, float f1) {
		boolean flag = bv > 0.0F && bw > 0.0F;

		bv = f;
		bw = f1;
		if (!flag) {
			this.a(1.0F);
		}
	}

	protected final void a(float f) {
		super.a(bv * f, bw * f);
	}
}
