package net.minecraft.server;

import java.util.Calendar;

import org.bukkit.event.entity.EntityCombustEvent; // CraftBukkit

public class EntitySkeleton extends EntityMonster implements IRangedEntity {

	private PathfinderGoalArrowAttack bp = new PathfinderGoalArrowAttack(this, 1.0D, 20, 60, 15.0F);
	private PathfinderGoalMeleeAttack bq = new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.2D, false);

	public EntitySkeleton(World world) {
		super(world);
		goalSelector.a(1, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalRestrictSun(this));
		goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
		goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
		if (world != null && !world.isStatic) {
			bZ();
		}
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.d).setValue(0.25D);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(13, new Byte((byte) 0));
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected String t() {
		return "mob.skeleton.say";
	}

	@Override
	protected String aT() {
		return "mob.skeleton.hurt";
	}

	@Override
	protected String aU() {
		return "mob.skeleton.death";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.skeleton.step", 0.15F, 1.0F);
	}

	@Override
	public boolean n(Entity entity) {
		if (super.n(entity)) {
			if (getSkeletonType() == 1 && entity instanceof EntityLiving) {
				((EntityLiving) entity).addEffect(new MobEffect(MobEffectList.WITHER.id, 200));
			}

			return true;
		} else
			return false;
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.UNDEAD;
	}

	@Override
	public void e() {
		if (world.w() && !world.isStatic) {
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

		if (world.isStatic && getSkeletonType() == 1) {
			this.a(0.72F, 2.34F);
		}

		super.e();
	}

	@Override
	public void ab() {
		super.ab();
		if (vehicle instanceof EntityCreature) {
			EntityCreature entitycreature = (EntityCreature) vehicle;

			aM = entitycreature.aM;
		}
	}

	@Override
	public void die(DamageSource damagesource) {
		super.die(damagesource);
		if (damagesource.i() instanceof EntityArrow && damagesource.getEntity() instanceof EntityHuman) {
			EntityHuman entityhuman = (EntityHuman) damagesource.getEntity();
			double d0 = entityhuman.locX - locX;
			double d1 = entityhuman.locZ - locZ;

			if (d0 * d0 + d1 * d1 >= 2500.0D) {
				entityhuman.a(AchievementList.v);
			}
		}
	}

	@Override
	protected Item getLoot() {
		return Items.ARROW;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j;
		int k;

		if (getSkeletonType() == 1) {
			j = random.nextInt(3 + i) - 1;

			for (k = 0; k < j; ++k) {
				this.a(Items.COAL, 1);
			}
		} else {
			j = random.nextInt(3 + i);

			for (k = 0; k < j; ++k) {
				this.a(Items.ARROW, 1);
			}
		}

		j = random.nextInt(3 + i);

		for (k = 0; k < j; ++k) {
			this.a(Items.BONE, 1);
		}
	}

	@Override
	protected void getRareDrop(int i) {
		if (getSkeletonType() == 1) {
			this.a(new ItemStack(Items.SKULL, 1, 1), 0.0F);
		}
	}

	@Override
	protected void bC() {
		super.bC();
		setEquipment(0, new ItemStack(Items.BOW));
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		groupdataentity = super.prepare(groupdataentity);
		if (world.worldProvider instanceof WorldProviderHell && aI().nextInt(5) > 0) {
			goalSelector.a(4, bq);
			setSkeletonType(1);
			setEquipment(0, new ItemStack(Items.STONE_SWORD));
			getAttributeInstance(GenericAttributes.e).setValue(4.0D);
		} else {
			goalSelector.a(4, bp);
			bC();
			bD();
		}

		this.h(random.nextFloat() < 0.55F * world.b(locX, locY, locZ));
		if (this.getEquipment(4) == null) {
			Calendar calendar = world.V();

			if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && random.nextFloat() < 0.25F) {
				setEquipment(4, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.PUMPKIN));
				dropChances[4] = 0.0F;
			}
		}

		return groupdataentity;
	}

	public void bZ() {
		goalSelector.a(bq);
		goalSelector.a(bp);
		ItemStack itemstack = be();

		if (itemstack != null && itemstack.getItem() == Items.BOW) {
			goalSelector.a(4, bp);
		} else {
			goalSelector.a(4, bq);
		}
	}

	@Override
	public void a(EntityLiving entityliving, float f) {
		EntityArrow entityarrow = new EntityArrow(world, this, entityliving, 1.6F, 14 - world.difficulty.a() * 4);
		int i = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, be());
		int j = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK.id, be());

		entityarrow.b(f * 2.0F + random.nextGaussian() * 0.25D + world.difficulty.a() * 0.11F);
		if (i > 0) {
			entityarrow.b(entityarrow.e() + i * 0.5D + 0.5D);
		}

		if (j > 0) {
			entityarrow.setKnockbackStrength(j);
		}

		if (EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_FIRE.id, be()) > 0 || getSkeletonType() == 1) {
			// CraftBukkit start - call EntityCombustEvent
			EntityCombustEvent event = new EntityCombustEvent(entityarrow.getBukkitEntity(), 100);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				entityarrow.setOnFire(event.getDuration());
			}
			// CraftBukkit end
		}

		// CraftBukkit start
		org.bukkit.event.entity.EntityShootBowEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityShootBowEvent(this, be(), entityarrow, 0.8F);
		if (event.isCancelled()) {
			event.getProjectile().remove();
			return;
		}

		if (event.getProjectile() == entityarrow.getBukkitEntity()) {
			world.addEntity(entityarrow);
		}
		// CraftBukkit end

		makeSound("random.bow", 1.0F, 1.0F / (aI().nextFloat() * 0.4F + 0.8F));
		// this.world.addEntity(entityarrow); // CraftBukkit - moved up
	}

	public int getSkeletonType() {
		return datawatcher.getByte(13);
	}

	public void setSkeletonType(int i) {
		datawatcher.watch(13, Byte.valueOf((byte) i));
		fireProof = i == 1;
		if (i == 1) {
			this.a(0.72F, 2.34F);
		} else {
			this.a(0.6F, 1.8F);
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		if (nbttagcompound.hasKeyOfType("SkeletonType", 99)) {
			byte b0 = nbttagcompound.getByte("SkeletonType");

			setSkeletonType(b0);
		}

		bZ();
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setByte("SkeletonType", (byte) getSkeletonType());
	}

	@Override
	public void setEquipment(int i, ItemStack itemstack) {
		super.setEquipment(i, itemstack);
		if (!world.isStatic && i == 0) {
			bZ();
		}
	}

	@Override
	public double ad() {
		return super.ad() - 0.5D;
	}
}
