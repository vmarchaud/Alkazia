package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityPig extends EntityAnimal {

	private final PathfinderGoalPassengerCarrotStick bp;

	public EntityPig(World world) {
		super(world);
		this.a(0.9F, 0.9F);
		getNavigation().a(true);
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
		goalSelector.a(2, bp = new PathfinderGoalPassengerCarrotStick(this, 0.3F));
		goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
		goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, Items.CARROT_STICK, false));
		goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, Items.CARROT, false));
		goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
		goalSelector.a(6, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.25D);
	}

	@Override
	protected void bn() {
		super.bn();
	}

	@Override
	public boolean bE() {
		ItemStack itemstack = ((EntityHuman) passenger).be();

		return itemstack != null && itemstack.getItem() == Items.CARROT_STICK;
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, Byte.valueOf((byte) 0));
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("Saddle", hasSaddle());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setSaddle(nbttagcompound.getBoolean("Saddle"));
	}

	@Override
	protected String t() {
		return "mob.pig.say";
	}

	@Override
	protected String aT() {
		return "mob.pig.say";
	}

	@Override
	protected String aU() {
		return "mob.pig.death";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.pig.step", 0.15F, 1.0F);
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		if (super.a(entityhuman))
			return true;
		else if (hasSaddle() && !world.isStatic && (passenger == null || passenger == entityhuman)) {
			entityhuman.mount(this);
			return true;
		} else
			return false;
	}

	@Override
	protected Item getLoot() {
		return isBurning() ? Items.GRILLED_PORK : Items.PORK;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(3) + 1 + random.nextInt(1 + i);

		for (int k = 0; k < j; ++k) {
			if (isBurning()) {
				this.a(Items.GRILLED_PORK, 1);
			} else {
				this.a(Items.PORK, 1);
			}
		}

		if (hasSaddle()) {
			this.a(Items.SADDLE, 1);
		}
	}

	public boolean hasSaddle() {
		return (datawatcher.getByte(16) & 1) != 0;
	}

	public void setSaddle(boolean flag) {
		if (flag) {
			datawatcher.watch(16, Byte.valueOf((byte) 1));
		} else {
			datawatcher.watch(16, Byte.valueOf((byte) 0));
		}
	}

	@Override
	public void a(EntityLightning entitylightning) {
		if (!world.isStatic) {
			EntityPigZombie entitypigzombie = new EntityPigZombie(world);

			// CraftBukkit start
			if (CraftEventFactory.callPigZapEvent(this, entitylightning, entitypigzombie).isCancelled())
				return;

			entitypigzombie.setEquipment(0, new ItemStack(Items.GOLD_SWORD));
			entitypigzombie.setPositionRotation(locX, locY, locZ, yaw, pitch);
			// CraftBukkit - added a reason for spawning this creature
			world.addEntity(entitypigzombie, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.LIGHTNING);
			this.die();
		}
	}

	@Override
	protected void b(float f) {
		super.b(f);
		if (f > 5.0F && passenger instanceof EntityHuman) {
			((EntityHuman) passenger).a(AchievementList.u);
		}
	}

	public EntityPig b(EntityAgeable entityageable) {
		return new EntityPig(world);
	}

	@Override
	public boolean c(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.CARROT;
	}

	public PathfinderGoalPassengerCarrotStick ca() {
		return bp;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
