package net.minecraft.server;

public class EntityIronGolem extends EntityGolem {

	private int bq;
	Village bp;
	private int br;
	private int bs;

	public EntityIronGolem(World world) {
		super(world);
		this.a(1.4F, 2.9F);
		getNavigation().a(true);
		goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
		goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
		goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
		goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
		goalSelector.a(5, new PathfinderGoalOfferFlower(this));
		goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
		goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalDefendVillage(this));
		targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
		targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, true, IMonster.a));
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, Byte.valueOf((byte) 0));
	}

	@Override
	public boolean bk() {
		return true;
	}

	@Override
	protected void bp() {
		if (--bq <= 0) {
			bq = 70 + random.nextInt(50);
			bp = world.villages.getClosestVillage(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ), 32);
			if (bp == null) {
				bX();
			} else {
				ChunkCoordinates chunkcoordinates = bp.getCenter();

				this.a(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, (int) (bp.getSize() * 0.6F));
			}
		}

		super.bp();
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.25D);
	}

	@Override
	protected int j(int i) {
		return i;
	}

	@Override
	protected void o(Entity entity) {
		if (entity instanceof IMonster && aI().nextInt(20) == 0) {
			// CraftBukkit start
			org.bukkit.event.entity.EntityTargetLivingEntityEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetLivingEvent(this, (EntityLiving) entity, org.bukkit.event.entity.EntityTargetEvent.TargetReason.COLLISION);
			if (!event.isCancelled()) {
				if (event.getTarget() == null) {
					setGoalTarget(null);
				} else {
					setGoalTarget(((org.bukkit.craftbukkit.entity.CraftLivingEntity) event.getTarget()).getHandle());
				}
			}
			// CraftBukkit end
		}

		super.o(entity);
	}

	@Override
	public void e() {
		super.e();
		if (br > 0) {
			--br;
		}

		if (bs > 0) {
			--bs;
		}

		if (motX * motX + motZ * motZ > 2.500000277905201E-7D && random.nextInt(5) == 0) {
			int i = MathHelper.floor(locX);
			int j = MathHelper.floor(locY - 0.20000000298023224D - height);
			int k = MathHelper.floor(locZ);
			Block block = world.getType(i, j, k);

			if (block.getMaterial() != Material.AIR) {
				world.addParticle("blockcrack_" + Block.getId(block) + "_" + world.getData(i, j, k), locX + (random.nextFloat() - 0.5D) * width, boundingBox.b + 0.1D, locZ + (random.nextFloat() - 0.5D) * width, 4.0D * (random.nextFloat() - 0.5D), 0.5D, (random.nextFloat() - 0.5D) * 4.0D);
			}
		}
	}

	@Override
	public boolean a(Class oclass) {
		return isPlayerCreated() && EntityHuman.class.isAssignableFrom(oclass) ? false : super.a(oclass);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("PlayerCreated", isPlayerCreated());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
	}

	@Override
	public boolean n(Entity entity) {
		br = 10;
		world.broadcastEntityEffect(this, (byte) 4);
		boolean flag = entity.damageEntity(DamageSource.mobAttack(this), 7 + random.nextInt(15));

		if (flag) {
			entity.motY += 0.4000000059604645D;
		}

		makeSound("mob.irongolem.throw", 1.0F, 1.0F);
		return flag;
	}

	public Village bZ() {
		return bp;
	}

	public void a(boolean flag) {
		bs = flag ? 400 : 0;
		world.broadcastEntityEffect(this, (byte) 11);
	}

	@Override
	protected String aT() {
		return "mob.irongolem.hit";
	}

	@Override
	protected String aU() {
		return "mob.irongolem.death";
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		makeSound("mob.irongolem.walk", 1.0F, 1.0F);
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(3);

		int k;

		for (k = 0; k < j; ++k) {
			this.a(Item.getItemOf(Blocks.RED_ROSE), 1, 0.0F);
		}

		k = 3 + random.nextInt(3);

		for (int l = 0; l < k; ++l) {
			this.a(Items.IRON_INGOT, 1);
		}
	}

	public int cb() {
		return bs;
	}

	public boolean isPlayerCreated() {
		return (datawatcher.getByte(16) & 1) != 0;
	}

	public void setPlayerCreated(boolean flag) {
		byte b0 = datawatcher.getByte(16);

		if (flag) {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 | 1)));
		} else {
			datawatcher.watch(16, Byte.valueOf((byte) (b0 & -2)));
		}
	}

	@Override
	public void die(DamageSource damagesource) {
		if (!isPlayerCreated() && killer != null && bp != null) {
			bp.a(killer.getName(), -5);
		}

		super.die(damagesource);
	}
}
