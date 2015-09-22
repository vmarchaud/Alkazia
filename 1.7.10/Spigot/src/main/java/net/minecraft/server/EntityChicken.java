package net.minecraft.server;

public class EntityChicken extends EntityAnimal {

	public float bp;
	public float bq;
	public float br;
	public float bs;
	public float bt = 1.0F;
	public int bu;
	public boolean bv;

	public EntityChicken(World world) {
		super(world);
		this.a(0.3F, 0.7F);
		bu = random.nextInt(6000) + 6000;
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
		goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
		goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Items.SEEDS, false));
		goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
		goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.25D);
	}

	@Override
	public void e() {
		// CraftBukkit start
		if (isChickenJockey()) {
			persistent = !isTypeNotPersistent();
		}
		// CraftBukkit end
		super.e();
		bs = bp;
		br = bq;
		bq = (float) (bq + (onGround ? -1 : 4) * 0.3D);
		if (bq < 0.0F) {
			bq = 0.0F;
		}

		if (bq > 1.0F) {
			bq = 1.0F;
		}

		if (!onGround && bt < 1.0F) {
			bt = 1.0F;
		}

		bt = (float) (bt * 0.9D);
		if (!onGround && motY < 0.0D) {
			motY *= 0.6D;
		}

		bp += bt * 2.0F;
		if (!world.isStatic && !isBaby() && !isChickenJockey() && --bu <= 0) {
			makeSound("mob.chicken.plop", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
			this.a(Items.EGG, 1);
			bu = random.nextInt(6000) + 6000;
		}
	}

	@Override
	protected void b(float f) {
	}

	@Override
	protected String t() {
		return "mob.chicken.say";
	}

	@Override
	protected String aT() {
		return "mob.chicken.hurt";
	}

	@Override
	protected String aU() {
		return "mob.chicken.hurt";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.chicken.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getLoot() {
		return Items.FEATHER;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(3) + random.nextInt(1 + i);

		for (int k = 0; k < j; ++k) {
			this.a(Items.FEATHER, 1);
		}

		if (isBurning()) {
			this.a(Items.COOKED_CHICKEN, 1);
		} else {
			this.a(Items.RAW_CHICKEN, 1);
		}
	}

	public EntityChicken b(EntityAgeable entityageable) {
		return new EntityChicken(world);
	}

	@Override
	public boolean c(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() instanceof ItemSeeds;
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		bv = nbttagcompound.getBoolean("IsChickenJockey");
	}

	@Override
	protected int getExpValue(EntityHuman entityhuman) {
		return isChickenJockey() ? 10 : super.getExpValue(entityhuman);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("IsChickenJockey", bv);
	}

	@Override
	protected boolean isTypeNotPersistent() {
		return isChickenJockey() && passenger == null;
	}

	@Override
	public void ac() {
		super.ac();
		float f = MathHelper.sin(aM * 3.1415927F / 180.0F);
		float f1 = MathHelper.cos(aM * 3.1415927F / 180.0F);
		float f2 = 0.1F;
		float f3 = 0.0F;

		passenger.setPosition(locX + f2 * f, locY + length * 0.5F + passenger.ad() + f3, locZ - f2 * f1);
		if (passenger instanceof EntityLiving) {
			((EntityLiving) passenger).aM = aM;
		}
	}

	public boolean isChickenJockey() {
		return bv;
	}

	public void i(boolean flag) {
		bv = flag;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
