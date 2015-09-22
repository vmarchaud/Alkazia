package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason; // CraftBukkit

public class EntityHorse extends EntityAnimal implements IInventoryListener {

	private static final IEntitySelector bu = new EntitySelectorHorse();
	public static final IAttribute attributeJumpStrength = new AttributeRanged("horse.jumpStrength", 0.7D, 0.0D, 2.0D).a("Jump Strength").a(true); // CraftBukkit - private -> public
	private static final String[] bw = new String[] { null, "textures/entity/horse/armor/horse_armor_iron.png", "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png" };
	private static final String[] bx = new String[] { "", "meo", "goo", "dio" };
	private static final int[] by = new int[] { 0, 5, 7, 11 };
	private static final String[] bz = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png" };
	private static final String[] bA = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb" };
	private static final String[] bB = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png" };
	private static final String[] bC = new String[] { "", "wo_", "wmo", "wdo", "bdo" };
	private int bD;
	private int bE;
	private int bF;
	public int bp;
	public int bq;
	protected boolean br;
	public InventoryHorseChest inventoryChest; // CraftBukkit - private -> public
	private boolean bH;
	protected int bs;
	protected float bt;
	private boolean bI;
	private float bJ;
	private float bK;
	private float bL;
	private float bM;
	private float bN;
	private float bO;
	private int bP;
	private String bQ;
	private String[] bR = new String[3];
	public int maxDomestication = 100; // CraftBukkit - store max domestication value

	public EntityHorse(World world) {
		super(world);
		this.a(1.4F, 1.6F);
		fireProof = false;
		setHasChest(false);
		getNavigation().a(true);
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
		goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
		goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
		goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
		goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.7D));
		goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		loadChest();
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, Integer.valueOf(0));
		datawatcher.a(19, Byte.valueOf((byte) 0));
		datawatcher.a(20, Integer.valueOf(0));
		datawatcher.a(21, String.valueOf(""));
		datawatcher.a(22, Integer.valueOf(0));
	}

	public void setType(int i) {
		datawatcher.watch(19, Byte.valueOf((byte) i));
		cP();
	}

	public int getType() {
		return datawatcher.getByte(19);
	}

	public void setVariant(int i) {
		datawatcher.watch(20, Integer.valueOf(i));
		cP();
	}

	public int getVariant() {
		return datawatcher.getInt(20);
	}

	@Override
	public String getName() {
		if (hasCustomName())
			return getCustomName();
		else {
			int i = getType();

			switch (i) {
			case 0:
			default:
				return LocaleI18n.get("entity.horse.name");

			case 1:
				return LocaleI18n.get("entity.donkey.name");

			case 2:
				return LocaleI18n.get("entity.mule.name");

			case 3:
				return LocaleI18n.get("entity.zombiehorse.name");

			case 4:
				return LocaleI18n.get("entity.skeletonhorse.name");
			}
		}
	}

	private boolean x(int i) {
		return (datawatcher.getInt(16) & i) != 0;
	}

	private void b(int i, boolean flag) {
		int j = datawatcher.getInt(16);

		if (flag) {
			datawatcher.watch(16, Integer.valueOf(j | i));
		} else {
			datawatcher.watch(16, Integer.valueOf(j & ~i));
		}
	}

	public boolean cb() {
		return !isBaby();
	}

	public boolean isTame() {
		return this.x(2);
	}

	public boolean cg() {
		return cb();
	}

	public String getOwnerUUID() {
		return datawatcher.getString(21);
	}

	public void setOwnerUUID(String s) {
		datawatcher.watch(21, s);
	}

	public float ci() {
		int i = getAge();

		return i >= 0 ? 1.0F : 0.5F + (-24000 - i) / -24000.0F * 0.5F;
	}

	@Override
	public void a(boolean flag) {
		if (flag) {
			this.a(ci());
		} else {
			this.a(1.0F);
		}
	}

	public boolean cj() {
		return br;
	}

	public void setTame(boolean flag) {
		this.b(2, flag);
	}

	public void j(boolean flag) {
		br = flag;
	}

	@Override
	public boolean bM() {
		// PaperSpigot start - Configurable undead horse leashing
		if (world.paperSpigotConfig.allowUndeadHorseLeashing)
			return super.bM();
		else
			return !cE() && super.bM();
	}

	@Override
	protected void o(float f) {
		if (f > 6.0F && cm()) {
			this.o(false);
		}
	}

	public boolean hasChest() {
		return this.x(8);
	}

	public int cl() {
		return datawatcher.getInt(22);
	}

	private int e(ItemStack itemstack) {
		if (itemstack == null)
			return 0;
		else {
			Item item = itemstack.getItem();

			return item == Items.HORSE_ARMOR_IRON ? 1 : item == Items.HORSE_ARMOR_GOLD ? 2 : item == Items.HORSE_ARMOR_DIAMOND ? 3 : 0;
		}
	}

	public boolean cm() {
		return this.x(32);
	}

	public boolean cn() {
		return this.x(64);
	}

	public boolean co() {
		return this.x(16);
	}

	public boolean cp() {
		return bH;
	}

	public void d(ItemStack itemstack) {
		datawatcher.watch(22, Integer.valueOf(this.e(itemstack)));
		cP();
	}

	public void k(boolean flag) {
		this.b(16, flag);
	}

	public void setHasChest(boolean flag) {
		this.b(8, flag);
	}

	public void m(boolean flag) {
		bH = flag;
	}

	public void n(boolean flag) {
		this.b(4, flag);
	}

	public int getTemper() {
		return bs;
	}

	public void setTemper(int i) {
		bs = i;
	}

	public int v(int i) {
		int j = MathHelper.a(getTemper() + i, 0, getMaxDomestication());

		setTemper(j);
		return j;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		Entity entity = damagesource.getEntity();

		return passenger != null && passenger.equals(entity) ? false : super.damageEntity(damagesource, f);
	}

	@Override
	public int aV() {
		return by[cl()];
	}

	@Override
	public boolean S() {
		return passenger == null;
	}

	public boolean cr() {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locZ);

		world.getBiome(i, j);
		return true;
	}

	public void cs() {
		if (!world.isStatic && hasChest()) {
			this.a(Item.getItemOf(Blocks.CHEST), 1);
			setHasChest(false);
		}
	}

	private void cL() {
		cS();
		world.makeSound(this, "eating", 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
	}

	@Override
	protected void b(float f) {
		if (f > 1.0F) {
			makeSound("mob.horse.land", 0.4F, 1.0F);
		}

		int i = MathHelper.f(f * 0.5F - 3.0F);

		if (i > 0) {
			damageEntity(DamageSource.FALL, i);
			if (passenger != null) {
				passenger.damageEntity(DamageSource.FALL, i);
			}

			Block block = world.getType(MathHelper.floor(locX), MathHelper.floor(locY - 0.2D - lastYaw), MathHelper.floor(locZ));

			if (block.getMaterial() != Material.AIR) {
				StepSound stepsound = block.stepSound;

				world.makeSound(this, stepsound.getStepSound(), stepsound.getVolume1() * 0.5F, stepsound.getVolume2() * 0.75F);
			}
		}
	}

	private int cM() {
		int i = getType();

		return hasChest() /* && (i == 1 || i == 2) */? 17 : 2; // CraftBukkit - Remove type check
	}

	public void loadChest() { // CraftBukkit - private -> public
		InventoryHorseChest inventoryhorsechest = inventoryChest;

		inventoryChest = new InventoryHorseChest("HorseChest", cM(), this); // CraftBukkit - add this horse
		inventoryChest.a(getName());
		if (inventoryhorsechest != null) {
			inventoryhorsechest.b(this);
			int i = Math.min(inventoryhorsechest.getSize(), inventoryChest.getSize());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inventoryhorsechest.getItem(j);

				if (itemstack != null) {
					inventoryChest.setItem(j, itemstack.cloneItemStack());
				}
			}

			inventoryhorsechest = null;
		}

		inventoryChest.a(this);
		cO();
	}

	private void cO() {
		if (!world.isStatic) {
			this.n(inventoryChest.getItem(0) != null);
			if (cB()) {
				this.d(inventoryChest.getItem(1));
			}
		}
	}

	@Override
	public void a(InventorySubcontainer inventorysubcontainer) {
		int i = cl();
		boolean flag = cu();

		cO();
		if (ticksLived > 20) {
			if (i == 0 && i != cl()) {
				makeSound("mob.horse.armor", 0.5F, 1.0F);
			} else if (i != cl()) {
				makeSound("mob.horse.armor", 0.5F, 1.0F);
			}

			if (!flag && cu()) {
				makeSound("mob.horse.leather", 0.5F, 1.0F);
			}
		}
	}

	@Override
	public boolean canSpawn() {
		cr();
		return super.canSpawn();
	}

	protected EntityHorse a(Entity entity, double d0) {
		double d1 = Double.MAX_VALUE;
		Entity entity1 = null;
		List list = world.getEntities(entity, entity.boundingBox.a(d0, d0, d0), bu);
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			Entity entity2 = (Entity) iterator.next();
			double d2 = entity2.e(entity.locX, entity.locY, entity.locZ);

			if (d2 < d1) {
				entity1 = entity2;
				d1 = d2;
			}
		}

		return (EntityHorse) entity1;
	}

	public double getJumpStrength() {
		return getAttributeInstance(attributeJumpStrength).getValue();
	}

	@Override
	protected String aU() {
		cS();
		int i = getType();

		return i == 3 ? "mob.horse.zombie.death" : i == 4 ? "mob.horse.skeleton.death" : i != 1 && i != 2 ? "mob.horse.death" : "mob.horse.donkey.death";
	}

	@Override
	protected Item getLoot() {
		boolean flag = random.nextInt(4) == 0;
		int i = getType();

		return i == 4 ? Items.BONE : i == 3 ? flag ? Item.getById(0) : Items.ROTTEN_FLESH : Items.LEATHER;
	}

	@Override
	protected String aT() {
		cS();
		if (random.nextInt(3) == 0) {
			cU();
		}

		int i = getType();

		return i == 3 ? "mob.horse.zombie.hit" : i == 4 ? "mob.horse.skeleton.hit" : i != 1 && i != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit";
	}

	public boolean cu() {
		return this.x(4);
	}

	@Override
	protected String t() {
		cS();
		if (random.nextInt(10) == 0 && !bh()) {
			cU();
		}

		int i = getType();

		return i == 3 ? "mob.horse.zombie.idle" : i == 4 ? "mob.horse.skeleton.idle" : i != 1 && i != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle";
	}

	protected String cv() {
		cS();
		cU();
		int i = getType();

		return i != 3 && i != 4 ? i != 1 && i != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry" : null;
	}

	@Override
	protected void a(int i, int j, int k, Block block) {
		StepSound stepsound = block.stepSound;

		if (world.getType(i, j + 1, k) == Blocks.SNOW) {
			stepsound = Blocks.SNOW.stepSound;
		}

		if (!block.getMaterial().isLiquid()) {
			int l = getType();

			if (passenger != null && l != 1 && l != 2) {
				++bP;
				if (bP > 5 && bP % 3 == 0) {
					makeSound("mob.horse.gallop", stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
					if (l == 0 && random.nextInt(10) == 0) {
						makeSound("mob.horse.breathe", stepsound.getVolume1() * 0.6F, stepsound.getVolume2());
					}
				} else if (bP <= 5) {
					makeSound("mob.horse.wood", stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
				}
			} else if (stepsound == Block.f) {
				makeSound("mob.horse.wood", stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
			} else {
				makeSound("mob.horse.soft", stepsound.getVolume1() * 0.15F, stepsound.getVolume2());
			}
		}
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeMap().b(attributeJumpStrength);
		getAttributeInstance(GenericAttributes.maxHealth).setValue(53.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.22499999403953552D);
	}

	@Override
	public int bB() {
		return 6;
	}

	public int getMaxDomestication() {
		return maxDomestication; // CraftBukkit - return stored max domestication instead of 100
	}

	@Override
	protected float bf() {
		return 0.8F;
	}

	@Override
	public int q() {
		return 400;
	}

	private void cP() {
		bQ = null;
	}

	public void g(EntityHuman entityhuman) {
		if (!world.isStatic && (passenger == null || passenger == entityhuman) && isTame()) {
			inventoryChest.a(getName());
			entityhuman.openHorseInventory(this, inventoryChest);
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() == Items.MONSTER_EGG)
			return super.a(entityhuman);
		else if (!isTame() && cE())
			return false;
		else if (isTame() && cb() && entityhuman.isSneaking()) {
			this.g(entityhuman);
			return true;
		} else if (cg() && passenger != null)
			return super.a(entityhuman);
		else {
			if (itemstack != null) {
				boolean flag = false;

				if (cB()) {
					byte b0 = -1;

					if (itemstack.getItem() == Items.HORSE_ARMOR_IRON) {
						b0 = 1;
					} else if (itemstack.getItem() == Items.HORSE_ARMOR_GOLD) {
						b0 = 2;
					} else if (itemstack.getItem() == Items.HORSE_ARMOR_DIAMOND) {
						b0 = 3;
					}

					if (b0 >= 0) {
						if (!isTame()) {
							cJ();
							return true;
						}

						this.g(entityhuman);
						return true;
					}
				}

				if (!flag && !cE()) {
					float f = 0.0F;
					short short1 = 0;
					byte b1 = 0;

					if (itemstack.getItem() == Items.WHEAT) {
						f = 2.0F;
						short1 = 60;
						b1 = 3;
					} else if (itemstack.getItem() == Items.SUGAR) {
						f = 1.0F;
						short1 = 30;
						b1 = 3;
					} else if (itemstack.getItem() == Items.BREAD) {
						f = 7.0F;
						short1 = 180;
						b1 = 3;
					} else if (Block.setSound(itemstack.getItem()) == Blocks.HAY_BLOCK) {
						f = 20.0F;
						short1 = 180;
					} else if (itemstack.getItem() == Items.APPLE) {
						f = 3.0F;
						short1 = 60;
						b1 = 3;
					} else if (itemstack.getItem() == Items.CARROT_GOLDEN) {
						f = 4.0F;
						short1 = 60;
						b1 = 5;
						if (isTame() && getAge() == 0) {
							flag = true;
							this.f(entityhuman);
						}
					} else if (itemstack.getItem() == Items.GOLDEN_APPLE) {
						f = 10.0F;
						short1 = 240;
						b1 = 10;
						if (isTame() && getAge() == 0) {
							flag = true;
							this.f(entityhuman);
						}
					}

					if (getHealth() < getMaxHealth() && f > 0.0F) {
						this.heal(f, RegainReason.EATING); // CraftBukkit
						flag = true;
					}

					if (!cb() && short1 > 0) {
						this.a(short1);
						flag = true;
					}

					if (b1 > 0 && (flag || !isTame()) && b1 < getMaxDomestication()) {
						flag = true;
						v(b1);
					}

					if (flag) {
						cL();
					}
				}

				if (!isTame() && !flag) {
					if (itemstack != null && itemstack.a(entityhuman, this))
						return true;

					cJ();
					return true;
				}

				if (!flag && cC() && !hasChest() && itemstack.getItem() == Item.getItemOf(Blocks.CHEST)) {
					setHasChest(true);
					makeSound("mob.chickenplop", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
					flag = true;
					loadChest();
				}

				if (!flag && cg() && !cu() && itemstack.getItem() == Items.SADDLE) {
					this.g(entityhuman);
					return true;
				}

				if (flag) {
					if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count == 0) {
						entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
					}

					return true;
				}
			}

			if (cg() && passenger == null) {
				if (itemstack != null && itemstack.a(entityhuman, this))
					return true;
				else {
					this.i(entityhuman);
					return true;
				}
			} else
				return super.a(entityhuman);
		}
	}

	private void i(EntityHuman entityhuman) {
		entityhuman.yaw = yaw;
		entityhuman.pitch = pitch;
		this.o(false);
		this.p(false);
		if (!world.isStatic) {
			entityhuman.mount(this);
		}
	}

	public boolean cB() {
		return getType() == 0;
	}

	public boolean cC() {
		int i = getType();

		return i == 2 || i == 1;
	}

	@Override
	protected boolean bh() {
		return passenger != null && cu() ? true : cm() || cn();
	}

	public boolean cE() {
		int i = getType();

		return i == 3 || i == 4;
	}

	public boolean cF() {
		return cE() || getType() == 2;
	}

	@Override
	public boolean c(ItemStack itemstack) {
		return false;
	}

	private void cR() {
		bp = 1;
	}

	@Override
	public void die(DamageSource damagesource) {
		super.die(damagesource);
		/* CraftBukkit start - Handle chest dropping in dropDeathLoot below
		if (!this.world.isStatic) {
		    this.dropChest();
		}
		// CraftBukkit end */
	}

	// CraftBukkit start - Add method
	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		super.dropDeathLoot(flag, i);

		// Moved from die method above
		if (!world.isStatic) {
			dropChest();
		}
	}

	// CraftBukkit end

	@Override
	public void e() {
		if (random.nextInt(200) == 0) {
			cR();
		}

		super.e();
		if (!world.isStatic) {
			if (random.nextInt(900) == 0 && deathTicks == 0) {
				this.heal(1.0F, RegainReason.REGEN); // CraftBukkit
			}

			if (!cm() && passenger == null && random.nextInt(300) == 0 && world.getType(MathHelper.floor(locX), MathHelper.floor(locY) - 1, MathHelper.floor(locZ)) == Blocks.GRASS) {
				this.o(true);
			}

			if (cm() && ++bD > 50) {
				bD = 0;
				this.o(false);
			}

			if (co() && !cb() && !cm()) {
				EntityHorse entityhorse = this.a(this, 16.0D);

				if (entityhorse != null && this.f(entityhorse) > 4.0D) {
					PathEntity pathentity = world.findPath(this, entityhorse, 16.0F, true, false, false, true);

					setPathEntity(pathentity);
				}
			}
		}
	}

	@Override
	public void h() {
		super.h();
		if (world.isStatic && datawatcher.a()) {
			datawatcher.e();
			cP();
		}

		if (bE > 0 && ++bE > 30) {
			bE = 0;
			this.b(128, false);
		}

		if (!world.isStatic && bF > 0 && ++bF > 20) {
			bF = 0;
			this.p(false);
		}

		if (bp > 0 && ++bp > 8) {
			bp = 0;
		}

		if (bq > 0) {
			++bq;
			if (bq > 300) {
				bq = 0;
			}
		}

		bK = bJ;
		if (cm()) {
			bJ += (1.0F - bJ) * 0.4F + 0.05F;
			if (bJ > 1.0F) {
				bJ = 1.0F;
			}
		} else {
			bJ += (0.0F - bJ) * 0.4F - 0.05F;
			if (bJ < 0.0F) {
				bJ = 0.0F;
			}
		}

		bM = bL;
		if (cn()) {
			bK = bJ = 0.0F;
			bL += (1.0F - bL) * 0.4F + 0.05F;
			if (bL > 1.0F) {
				bL = 1.0F;
			}
		} else {
			bI = false;
			bL += (0.8F * bL * bL * bL - bL) * 0.6F - 0.05F;
			if (bL < 0.0F) {
				bL = 0.0F;
			}
		}

		bO = bN;
		if (this.x(128)) {
			bN += (1.0F - bN) * 0.7F + 0.05F;
			if (bN > 1.0F) {
				bN = 1.0F;
			}
		} else {
			bN += (0.0F - bN) * 0.7F - 0.05F;
			if (bN < 0.0F) {
				bN = 0.0F;
			}
		}
	}

	private void cS() {
		if (!world.isStatic) {
			bE = 1;
			this.b(128, true);
		}
	}

	private boolean cT() {
		return passenger == null && vehicle == null && isTame() && cb() && !cF() && getHealth() >= getMaxHealth();
	}

	@Override
	public void e(boolean flag) {
		this.b(32, flag);
	}

	public void o(boolean flag) {
		this.e(flag);
	}

	public void p(boolean flag) {
		if (flag) {
			this.o(false);
		}

		this.b(64, flag);
	}

	private void cU() {
		if (!world.isStatic) {
			bF = 1;
			this.p(true);
		}
	}

	public void cJ() {
		cU();
		String s = cv();

		if (s != null) {
			makeSound(s, bf(), bg());
		}
	}

	public void dropChest() {
		this.a(this, inventoryChest);
		cs();
	}

	private void a(Entity entity, InventoryHorseChest inventoryhorsechest) {
		if (inventoryhorsechest != null && !world.isStatic) {
			for (int i = 0; i < inventoryhorsechest.getSize(); ++i) {
				ItemStack itemstack = inventoryhorsechest.getItem(i);

				if (itemstack != null) {
					this.a(itemstack, 0.0F);
				}
			}
		}
	}

	public boolean h(EntityHuman entityhuman) {
		setOwnerUUID(entityhuman.getUniqueID().toString());
		setTame(true);
		return true;
	}

	@Override
	public void e(float f, float f1) {
		if (passenger != null && passenger instanceof EntityLiving && cu()) {
			lastYaw = yaw = passenger.yaw;
			pitch = passenger.pitch * 0.5F;
			this.b(yaw, pitch);
			aO = aM = yaw;
			f = ((EntityLiving) passenger).bd * 0.5F;
			f1 = ((EntityLiving) passenger).be;
			if (f1 <= 0.0F) {
				f1 *= 0.25F;
				bP = 0;
			}

			if (onGround && bt == 0.0F && cn() && !bI) {
				f = 0.0F;
				f1 = 0.0F;
			}

			if (bt > 0.0F && !cj() && onGround) {
				motY = getJumpStrength() * bt;
				if (this.hasEffect(MobEffectList.JUMP)) {
					motY += (getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F;
				}

				this.j(true);
				al = true;
				if (f1 > 0.0F) {
					float f2 = MathHelper.sin(yaw * 3.1415927F / 180.0F);
					float f3 = MathHelper.cos(yaw * 3.1415927F / 180.0F);

					motX += -0.4F * f2 * bt;
					motZ += 0.4F * f3 * bt;
					makeSound("mob.horse.jump", 0.4F, 1.0F);
				}

				bt = 0.0F;
			}

			W = 1.0F;
			aQ = bl() * 0.1F;
			if (!world.isStatic) {
				this.i((float) getAttributeInstance(GenericAttributes.d).getValue());
				super.e(f, f1);
			}

			if (onGround) {
				bt = 0.0F;
				this.j(false);
			}

			aE = aF;
			double d0 = locX - lastX;
			double d1 = locZ - lastZ;
			float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

			if (f4 > 1.0F) {
				f4 = 1.0F;
			}

			aF += (f4 - aF) * 0.4F;
			aG += aF;
		} else {
			W = 0.5F;
			aQ = 0.02F;
			super.e(f, f1);
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("EatingHaystack", cm());
		nbttagcompound.setBoolean("ChestedHorse", hasChest());
		nbttagcompound.setBoolean("HasReproduced", cp());
		nbttagcompound.setBoolean("Bred", co());
		nbttagcompound.setInt("Type", getType());
		nbttagcompound.setInt("Variant", getVariant());
		nbttagcompound.setInt("Temper", getTemper());
		nbttagcompound.setBoolean("Tame", isTame());
		nbttagcompound.setString("OwnerUUID", getOwnerUUID());
		nbttagcompound.setInt("Bukkit.MaxDomestication", maxDomestication); // CraftBukkit
		if (hasChest()) {
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 2; i < inventoryChest.getSize(); ++i) {
				ItemStack itemstack = inventoryChest.getItem(i);

				if (itemstack != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();

					nbttagcompound1.setByte("Slot", (byte) i);
					itemstack.save(nbttagcompound1);
					nbttaglist.add(nbttagcompound1);
				}
			}

			nbttagcompound.set("Items", nbttaglist);
		}

		if (inventoryChest.getItem(1) != null) {
			nbttagcompound.set("ArmorItem", inventoryChest.getItem(1).save(new NBTTagCompound()));
		}

		if (inventoryChest.getItem(0) != null) {
			nbttagcompound.set("SaddleItem", inventoryChest.getItem(0).save(new NBTTagCompound()));
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.o(nbttagcompound.getBoolean("EatingHaystack"));
		this.k(nbttagcompound.getBoolean("Bred"));
		setHasChest(nbttagcompound.getBoolean("ChestedHorse"));
		this.m(nbttagcompound.getBoolean("HasReproduced"));
		setType(nbttagcompound.getInt("Type"));
		setVariant(nbttagcompound.getInt("Variant"));
		setTemper(nbttagcompound.getInt("Temper"));
		setTame(nbttagcompound.getBoolean("Tame"));
		if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
			setOwnerUUID(nbttagcompound.getString("OwnerUUID"));
		}
		// Spigot start
		else if (nbttagcompound.hasKey("OwnerName")) {
			String owner = nbttagcompound.getString("OwnerName");
			if (owner != null && !owner.isEmpty()) {
				setOwnerUUID(NameReferencingFileConverter.a(owner));
			}
		}
		// Spigot end
		// CraftBukkit start
		if (nbttagcompound.hasKey("Bukkit.MaxDomestication")) {
			maxDomestication = nbttagcompound.getInt("Bukkit.MaxDomestication");
		}
		// CraftBukkit end
		AttributeInstance attributeinstance = getAttributeMap().a("Speed");

		if (attributeinstance != null) {
			getAttributeInstance(GenericAttributes.d).setValue(attributeinstance.b() * 0.25D);
		}

		if (hasChest()) {
			NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

			loadChest();

			for (int i = 0; i < nbttaglist.size(); ++i) {
				NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
				int j = nbttagcompound1.getByte("Slot") & 255;

				if (j >= 2 && j < inventoryChest.getSize()) {
					inventoryChest.setItem(j, ItemStack.createStack(nbttagcompound1));
				}
			}
		}

		ItemStack itemstack;

		if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
			itemstack = ItemStack.createStack(nbttagcompound.getCompound("ArmorItem"));
			if (itemstack != null && a(itemstack.getItem())) {
				inventoryChest.setItem(1, itemstack);
			}
		}

		if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
			itemstack = ItemStack.createStack(nbttagcompound.getCompound("SaddleItem"));
			if (itemstack != null && itemstack.getItem() == Items.SADDLE) {
				inventoryChest.setItem(0, itemstack);
			}
		} else if (nbttagcompound.getBoolean("Saddle")) {
			inventoryChest.setItem(0, new ItemStack(Items.SADDLE));
		}

		cO();
	}

	@Override
	public boolean mate(EntityAnimal entityanimal) {
		if (entityanimal == this)
			return false;
		else if (entityanimal.getClass() != this.getClass())
			return false;
		else {
			EntityHorse entityhorse = (EntityHorse) entityanimal;

			if (cT() && entityhorse.cT()) {
				int i = getType();
				int j = entityhorse.getType();

				return i == j || i == 0 && j == 1 || i == 1 && j == 0;
			} else
				return false;
		}
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		EntityHorse entityhorse = (EntityHorse) entityageable;
		EntityHorse entityhorse1 = new EntityHorse(world);
		int i = getType();
		int j = entityhorse.getType();
		int k = 0;

		if (i == j) {
			k = i;
		} else if (i == 0 && j == 1 || i == 1 && j == 0) {
			k = 2;
		}

		if (k == 0) {
			int l = random.nextInt(9);
			int i1;

			if (l < 4) {
				i1 = getVariant() & 255;
			} else if (l < 8) {
				i1 = entityhorse.getVariant() & 255;
			} else {
				i1 = random.nextInt(7);
			}

			int j1 = random.nextInt(5);

			if (j1 < 2) {
				i1 |= getVariant() & '\uff00';
			} else if (j1 < 4) {
				i1 |= entityhorse.getVariant() & '\uff00';
			} else {
				i1 |= random.nextInt(5) << 8 & '\uff00';
			}

			entityhorse1.setVariant(i1);
		}

		entityhorse1.setType(k);
		double d0 = getAttributeInstance(GenericAttributes.maxHealth).b() + entityageable.getAttributeInstance(GenericAttributes.maxHealth).b() + cV();

		entityhorse1.getAttributeInstance(GenericAttributes.maxHealth).setValue(d0 / 3.0D);
		double d1 = getAttributeInstance(attributeJumpStrength).b() + entityageable.getAttributeInstance(attributeJumpStrength).b() + cW();

		entityhorse1.getAttributeInstance(attributeJumpStrength).setValue(d1 / 3.0D);
		double d2 = getAttributeInstance(GenericAttributes.d).b() + entityageable.getAttributeInstance(GenericAttributes.d).b() + cX();

		entityhorse1.getAttributeInstance(GenericAttributes.d).setValue(d2 / 3.0D);
		return entityhorse1;
	}

	@Override
	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		Object object = super.prepare(groupdataentity);
		boolean flag = false;
		int i = 0;
		int j;

		if (object instanceof GroupDataHorse) {
			j = ((GroupDataHorse) object).a;
			i = ((GroupDataHorse) object).b & 255 | random.nextInt(5) << 8;
		} else {
			if (random.nextInt(10) == 0) {
				j = 1;
			} else {
				int k = random.nextInt(7);
				int l = random.nextInt(5);

				j = 0;
				i = k | l << 8;
			}

			object = new GroupDataHorse(j, i);
		}

		setType(j);
		setVariant(i);
		if (random.nextInt(5) == 0) {
			setAge(-24000);
		}

		if (j != 4 && j != 3) {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(cV());
			if (j == 0) {
				getAttributeInstance(GenericAttributes.d).setValue(cX());
			} else {
				getAttributeInstance(GenericAttributes.d).setValue(0.17499999701976776D);
			}
		} else {
			getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
			getAttributeInstance(GenericAttributes.d).setValue(0.20000000298023224D);
		}

		if (j != 2 && j != 1) {
			getAttributeInstance(attributeJumpStrength).setValue(cW());
		} else {
			getAttributeInstance(attributeJumpStrength).setValue(0.5D);
		}

		setHealth(getMaxHealth());
		return (GroupDataEntity) object;
	}

	@Override
	protected boolean bk() {
		return true;
	}

	public void w(int i) {
		if (cu()) {
			// CraftBukkit start - fire HorseJumpEvent, use event power
			if (i < 0) {
				i = 0;
			}

			float power;
			if (i >= 90) {
				power = 1.0F;
			} else {
				power = 0.4F + 0.4F * i / 90.0F;
			}

			org.bukkit.event.entity.HorseJumpEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callHorseJumpEvent(this, power);
			if (!event.isCancelled()) {
				bI = true;
				cU();
				bt = event.getPower();
			}
			// CraftBukkit end
		}
	}

	@Override
	public void ac() {
		super.ac();
		if (bM > 0.0F) {
			float f = MathHelper.sin(aM * 3.1415927F / 180.0F);
			float f1 = MathHelper.cos(aM * 3.1415927F / 180.0F);
			float f2 = 0.7F * bM;
			float f3 = 0.15F * bM;

			passenger.setPosition(locX + f2 * f, locY + ad() + passenger.ad() + f3, locZ - f2 * f1);
			if (passenger instanceof EntityLiving) {
				((EntityLiving) passenger).aM = aM;
			}
		}
	}

	private float cV() {
		return 15.0F + random.nextInt(8) + random.nextInt(9);
	}

	private double cW() {
		return 0.4000000059604645D + random.nextDouble() * 0.2D + random.nextDouble() * 0.2D + random.nextDouble() * 0.2D;
	}

	private double cX() {
		return (0.44999998807907104D + random.nextDouble() * 0.3D + random.nextDouble() * 0.3D + random.nextDouble() * 0.3D) * 0.25D;
	}

	public static boolean a(Item item) {
		return item == Items.HORSE_ARMOR_IRON || item == Items.HORSE_ARMOR_GOLD || item == Items.HORSE_ARMOR_DIAMOND;
	}

	@Override
	public boolean h_() {
		return false;
	}
}
