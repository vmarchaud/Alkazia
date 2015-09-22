package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

// CraftBukkit end

public class EntityWolf extends EntityTameableAnimal {

	private float bq;
	private float br;
	private boolean bs;
	private boolean bt;
	private float bu;
	private float bv;

	public EntityWolf(World world) {
		super(world);
		this.a(0.6F, 0.8F);
		getNavigation().a(true);
		goalSelector.a(1, new PathfinderGoalFloat(this));
		goalSelector.a(2, bp);
		goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
		goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
		goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
		goalSelector.a(6, new PathfinderGoalBreed(this, 1.0D));
		goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(8, new PathfinderGoalBeg(this, 8.0F));
		goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
		targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
		targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true));
		targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed(this, EntitySheep.class, 200, false));
		setTamed(false);
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.d).setValue(0.30000001192092896D);
		if (isTamed()) {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
		} else {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		}
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	public void setGoalTarget(EntityLiving entityliving) {
		super.setGoalTarget(entityliving);
		if (entityliving == null) {
			setAngry(false);
		} else if (!isTamed()) {
			setAngry(true);
		}
	}

	@Override
	protected void bp() {
		datawatcher.watch(18, Float.valueOf(getHealth()));
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(18, new Float(getHealth()));
		datawatcher.a(19, new Byte((byte) 0));
		datawatcher.a(20, new Byte((byte) BlockCloth.b(1)));
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.wolf.step", 0.15F, 1.0F);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("Angry", isAngry());
		nbttagcompound.setByte("CollarColor", (byte) getCollarColor());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setAngry(nbttagcompound.getBoolean("Angry"));
		if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
			setCollarColor(nbttagcompound.getByte("CollarColor"));
		}
	}

	@Override
	protected String t() {
		// CraftBukkit - (getFloat(18) < 10) -> (getFloat(18) < this.getMaxHealth() / 2)
		return isAngry() ? "mob.wolf.growl" : random.nextInt(3) == 0 ? isTamed() && datawatcher.getFloat(18) < getMaxHealth() / 2 ? "mob.wolf.whine" : "mob.wolf.panting" : "mob.wolf.bark";
	}

	@Override
	protected String aT() {
		return "mob.wolf.hurt";
	}

	@Override
	protected String aU() {
		return "mob.wolf.death";
	}

	@Override
	protected float bf() {
		return 0.4F;
	}

	@Override
	protected Item getLoot() {
		return Item.getById(-1);
	}

	@Override
	public void e() {
		super.e();
		if (!world.isStatic && bs && !bt && !bS() && onGround) {
			bt = true;
			bu = 0.0F;
			bv = 0.0F;
			world.broadcastEntityEffect(this, (byte) 8);
		}
	}

	@Override
	public void h() {
		super.h();
		br = bq;
		if (ck()) {
			bq += (1.0F - bq) * 0.4F;
		} else {
			bq += (0.0F - bq) * 0.4F;
		}

		if (ck()) {
			g = 10;
		}

		if (L()) {
			bs = true;
			bt = false;
			bu = 0.0F;
			bv = 0.0F;
		} else if ((bs || bt) && bt) {
			if (bu == 0.0F) {
				makeSound("mob.wolf.shake", bf(), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
			}

			bv = bu;
			bu += 0.05F;
			if (bv >= 2.0F) {
				bs = false;
				bt = false;
				bv = 0.0F;
				bu = 0.0F;
			}

			if (bu > 0.4F) {
				float f = (float) boundingBox.b;
				int i = (int) (MathHelper.sin((bu - 0.4F) * 3.1415927F) * 7.0F);

				for (int j = 0; j < i; ++j) {
					float f1 = (random.nextFloat() * 2.0F - 1.0F) * width * 0.5F;
					float f2 = (random.nextFloat() * 2.0F - 1.0F) * width * 0.5F;

					world.addParticle("splash", locX + f1, f + 0.8F, locZ + f2, motX, motY, motZ);
				}
			}
		}
	}

	@Override
	public float getHeadHeight() {
		return length * 0.8F;
	}

	@Override
	public int x() {
		return isSitting() ? 20 : super.x();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			Entity entity = damagesource.getEntity();

			bp.setSitting(false);
			if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
				f = (f + 1.0F) / 2.0F;
			}

			return super.damageEntity(damagesource, f);
		}
	}

	@Override
	public boolean n(Entity entity) {
		int i = isTamed() ? 4 : 2;

		return entity.damageEntity(DamageSource.mobAttack(this), i);
	}

	@Override
	public void setTamed(boolean flag) {
		super.setTamed(flag);
		if (flag) {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
		} else {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (isTamed()) {
			if (itemstack != null) {
				if (itemstack.getItem() instanceof ItemFood) {
					ItemFood itemfood = (ItemFood) itemstack.getItem();

					if (itemfood.i() && datawatcher.getFloat(18) < 20.0F) {
						if (!entityhuman.abilities.canInstantlyBuild) {
							--itemstack.count;
						}

						this.heal(itemfood.getNutrition(itemstack), org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.EATING); // CraftBukkit
						if (itemstack.count <= 0) {
							entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
						}

						return true;
					}
				} else if (itemstack.getItem() == Items.INK_SACK) {
					int i = BlockCloth.b(itemstack.getData());

					if (i != getCollarColor()) {
						setCollarColor(i);
						if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count <= 0) {
							entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
						}

						return true;
					}
				}
			}

			if (this.e(entityhuman) && !world.isStatic && !this.c(itemstack)) {
				bp.setSitting(!isSitting());
				bc = false;
				setPathEntity((PathEntity) null);
				setTarget((Entity) null);
				// CraftBukkit start
				if (getGoalTarget() != null) {
					CraftEventFactory.callEntityTargetEvent(this, null, TargetReason.FORGOT_TARGET);
				}
				// CraftBukkit end
				setGoalTarget((EntityLiving) null);
			}
		} else if (itemstack != null && itemstack.getItem() == Items.BONE && !isAngry()) {
			if (!entityhuman.abilities.canInstantlyBuild) {
				--itemstack.count;
			}

			if (itemstack.count <= 0) {
				entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
			}

			if (!world.isStatic) {
				// CraftBukkit - added event call and isCancelled check.
				if (random.nextInt(3) == 0 && !CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
					setTamed(true);
					setPathEntity((PathEntity) null);
					// CraftBukkit start
					if (getGoalTarget() != null) {
						CraftEventFactory.callEntityTargetEvent(this, null, TargetReason.FORGOT_TARGET);
					}
					// CraftBukkit end
					setGoalTarget((EntityLiving) null);
					bp.setSitting(true);
					setHealth(getMaxHealth()); // CraftBukkit - 20.0 -> getMaxHealth()
					setOwnerUUID(entityhuman.getUniqueID().toString());
					this.i(true);
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

	@Override
	public boolean c(ItemStack itemstack) {
		return itemstack == null ? false : !(itemstack.getItem() instanceof ItemFood) ? false : ((ItemFood) itemstack.getItem()).i();
	}

	@Override
	public int bB() {
		return 8;
	}

	public boolean isAngry() {
		return (datawatcher.getByte(16) & 2) != 0;
	}

	public void setAngry(boolean flag) {
		byte b0 = datawatcher.getByte(16);

		if (flag) {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 | 2)));
		} else {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 & -3)));
		}
	}

	public int getCollarColor() {
		return datawatcher.getByte(20) & 15;
	}

	public void setCollarColor(int i) {
		datawatcher.watch(20, Byte.valueOf((byte) (i & 15)));
	}

	public EntityWolf b(EntityAgeable entityageable) {
		EntityWolf entitywolf = new EntityWolf(world);
		String s = getOwnerUUID();

		if (s != null && s.trim().length() > 0) {
			entitywolf.setOwnerUUID(s);
			entitywolf.setTamed(true);
		}

		return entitywolf;
	}

	public void m(boolean flag) {
		if (flag) {
			datawatcher.watch(19, Byte.valueOf((byte) 1));
		} else {
			datawatcher.watch(19, Byte.valueOf((byte) 0));
		}
	}

	@Override
	public boolean mate(EntityAnimal entityanimal) {
		if (entityanimal == this)
			return false;
		else if (!isTamed())
			return false;
		else if (!(entityanimal instanceof EntityWolf))
			return false;
		else {
			EntityWolf entitywolf = (EntityWolf) entityanimal;

			return !entitywolf.isTamed() ? false : entitywolf.isSitting() ? false : ce() && entitywolf.ce();
		}
	}

	public boolean ck() {
		return datawatcher.getByte(19) == 1;
	}

	@Override
	protected boolean isTypeNotPersistent() {
		return !isTamed() /*&& this.ticksLived > 2400*/; // CraftBukkit
	}

	@Override
	public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
		if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
			if (entityliving instanceof EntityWolf) {
				EntityWolf entitywolf = (EntityWolf) entityliving;

				if (entitywolf.isTamed() && entitywolf.getOwner() == entityliving1)
					return false;
			}

			return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false : !(entityliving instanceof EntityHorse) || !((EntityHorse) entityliving).isTame();
		} else
			return false;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
