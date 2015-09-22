package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;

// CraftBukkit end

public abstract class EntityInsentient extends EntityLiving {

	public int a_;
	protected int b;
	private ControllerLook lookController;
	private ControllerMove moveController;
	private ControllerJump bm;
	private EntityAIBodyControl bn;
	private Navigation navigation;
	protected final PathfinderGoalSelector goalSelector;
	protected final PathfinderGoalSelector targetSelector;
	private EntityLiving goalTarget;
	private EntitySenses bq;
	private ItemStack[] equipment = new ItemStack[5];
	public float[] dropChances = new float[5]; // CraftBukkit - protected -> public
	public boolean canPickUpLoot; // CraftBukkit - private -> public
	public boolean persistent = !isTypeNotPersistent(); // CraftBukkit - private -> public
	protected float f;
	private Entity bu;
	protected int g;
	private boolean bv;
	private Entity bw;
	private NBTTagCompound bx;

	public EntityInsentient(World world) {
		super(world);
		goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
		targetSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
		lookController = new ControllerLook(this);
		moveController = new ControllerMove(this);
		bm = new ControllerJump(this);
		bn = new EntityAIBodyControl(this);
		navigation = new Navigation(this, world);
		bq = new EntitySenses(this);

		for (int i = 0; i < dropChances.length; ++i) {
			dropChances[i] = 0.085F;
		}
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeMap().b(GenericAttributes.b).setValue(16.0D);
	}

	public ControllerLook getControllerLook() {
		return lookController;
	}

	public ControllerMove getControllerMove() {
		return moveController;
	}

	public ControllerJump getControllerJump() {
		return bm;
	}

	public Navigation getNavigation() {
		return navigation;
	}

	public EntitySenses getEntitySenses() {
		return bq;
	}

	public EntityLiving getGoalTarget() {
		return goalTarget;
	}

	public void setGoalTarget(EntityLiving entityliving) {
		goalTarget = entityliving;
	}

	public boolean a(Class oclass) {
		return EntityCreeper.class != oclass && EntityGhast.class != oclass;
	}

	public void p() {
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(11, Byte.valueOf((byte) 0));
		datawatcher.a(10, "");
		// Spigot start - protocol patch
		datawatcher.a(3, Byte.valueOf((byte) 0));
		datawatcher.a(2, "");
		// Spigot end
	}

	public int q() {
		return 80;
	}

	public void r() {
		String s = t();

		if (s != null) {
			makeSound(s, bf(), bg());
		}
	}

	@Override
	public void C() {
		super.C();
		world.methodProfiler.a("mobBaseTick");
		if (isAlive() && random.nextInt(1000) < a_++) {
			a_ = -q();
			this.r();
		}

		world.methodProfiler.b();
	}

	@Override
	protected int getExpValue(EntityHuman entityhuman) {
		if (b > 0) {
			int i = b;
			ItemStack[] aitemstack = this.getEquipment();

			for (int j = 0; j < aitemstack.length; ++j) {
				if (aitemstack[j] != null && dropChances[j] <= 1.0F) {
					i += 1 + random.nextInt(3);
				}
			}

			return i;
		} else
			return b;
	}

	public void s() {
		for (int i = 0; i < 20; ++i) {
			double d0 = random.nextGaussian() * 0.02D;
			double d1 = random.nextGaussian() * 0.02D;
			double d2 = random.nextGaussian() * 0.02D;
			double d3 = 10.0D;

			world.addParticle("explode", locX + random.nextFloat() * width * 2.0F - width - d0 * d3, locY + random.nextFloat() * length - d1 * d3, locZ + random.nextFloat() * width * 2.0F - width - d2 * d3, d0, d1, d2);
		}
	}

	@Override
	public void h() {
		super.h();
		if (!world.isStatic) {
			bL();
		}
	}

	@Override
	protected float f(float f, float f1) {
		if (bk()) {
			bn.a();
			return f1;
		} else
			return super.f(f, f1);
	}

	protected String t() {
		return null;
	}

	protected Item getLoot() {
		return Item.getById(0);
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		Item item = getLoot();

		if (item != null) {
			int j = random.nextInt(3);

			if (i > 0) {
				j += random.nextInt(i + 1);
			}

			for (int k = 0; k < j; ++k) {
				this.a(item, 1);
			}
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("CanPickUpLoot", bJ());
		nbttagcompound.setBoolean("PersistenceRequired", persistent);
		NBTTagList nbttaglist = new NBTTagList();

		NBTTagCompound nbttagcompound1;

		for (int i = 0; i < equipment.length; ++i) {
			nbttagcompound1 = new NBTTagCompound();
			if (equipment[i] != null) {
				equipment[i].save(nbttagcompound1);
			}

			nbttaglist.add(nbttagcompound1);
		}

		nbttagcompound.set("Equipment", nbttaglist);
		NBTTagList nbttaglist1 = new NBTTagList();

		for (int j = 0; j < dropChances.length; ++j) {
			nbttaglist1.add(new NBTTagFloat(dropChances[j]));
		}

		nbttagcompound.set("DropChances", nbttaglist1);
		nbttagcompound.setString("CustomName", getCustomName());
		nbttagcompound.setBoolean("CustomNameVisible", getCustomNameVisible());
		nbttagcompound.setBoolean("Leashed", bv);
		if (bw != null) {
			nbttagcompound1 = new NBTTagCompound();
			if (bw instanceof EntityLiving) {
				nbttagcompound1.setLong("UUIDMost", bw.getUniqueID().getMostSignificantBits());
				nbttagcompound1.setLong("UUIDLeast", bw.getUniqueID().getLeastSignificantBits());
			} else if (bw instanceof EntityHanging) {
				EntityHanging entityhanging = (EntityHanging) bw;

				nbttagcompound1.setInt("X", entityhanging.x);
				nbttagcompound1.setInt("Y", entityhanging.y);
				nbttagcompound1.setInt("Z", entityhanging.z);
			}

			nbttagcompound.set("Leash", nbttagcompound1);
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);

		// CraftBukkit start - If looting or persistence is false only use it if it was set after we started using it
		boolean data = nbttagcompound.getBoolean("CanPickUpLoot");
		if (isLevelAtLeast(nbttagcompound, 1) || data) {
			canPickUpLoot = data;
		}

		data = nbttagcompound.getBoolean("PersistenceRequired");
		if (isLevelAtLeast(nbttagcompound, 1) || data) {
			persistent = data;
		}
		// CraftBukkit end

		if (nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
			setCustomName(nbttagcompound.getString("CustomName"));
		}

		setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
		NBTTagList nbttaglist;
		int i;

		if (nbttagcompound.hasKeyOfType("Equipment", 9)) {
			nbttaglist = nbttagcompound.getList("Equipment", 10);

			for (i = 0; i < equipment.length; ++i) {
				equipment[i] = ItemStack.createStack(nbttaglist.get(i));
			}
		}

		if (nbttagcompound.hasKeyOfType("DropChances", 9)) {
			nbttaglist = nbttagcompound.getList("DropChances", 5);

			for (i = 0; i < nbttaglist.size(); ++i) {
				dropChances[i] = nbttaglist.e(i);
			}
		}

		bv = nbttagcompound.getBoolean("Leashed");
		if (bv && nbttagcompound.hasKeyOfType("Leash", 10)) {
			bx = nbttagcompound.getCompound("Leash");
		}
	}

	public void n(float f) {
		be = f;
	}

	@Override
	public void i(float f) {
		super.i(f);
		this.n(f);
	}

	@Override
	public void e() {
		super.e();
		world.methodProfiler.a("looting");
		if (!world.isStatic && bJ() && !aT && world.getGameRules().getBoolean("mobGriefing")) {
			List list = world.a(EntityItem.class, boundingBox.grow(1.0D, 0.0D, 1.0D));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityItem entityitem = (EntityItem) iterator.next();

				if (!entityitem.dead && entityitem.getItemStack() != null) {
					ItemStack itemstack = entityitem.getItemStack();
					int i = b(itemstack);

					if (i > -1) {
						boolean flag = true;
						ItemStack itemstack1 = this.getEquipment(i);

						if (itemstack1 != null) {
							if (i == 0) {
								if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
									flag = true;
								} else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
									ItemSword itemsword = (ItemSword) itemstack.getItem();
									ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

									if (itemsword.i() == itemsword1.i()) {
										flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
									} else {
										flag = itemsword.i() > itemsword1.i();
									}
								} else {
									flag = false;
								}
							} else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
								flag = true;
							} else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor) {
								ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
								ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

								if (itemarmor.c == itemarmor1.c) {
									flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
								} else {
									flag = itemarmor.c > itemarmor1.c;
								}
							} else {
								flag = false;
							}
						}

						if (flag) {
							if (itemstack1 != null && random.nextFloat() - 0.1F < dropChances[i]) {
								this.a(itemstack1, 0.0F);
							}

							if (itemstack.getItem() == Items.DIAMOND && entityitem.j() != null) {
								EntityHuman entityhuman = world.a(entityitem.j());

								if (entityhuman != null) {
									entityhuman.a(AchievementList.x);
								}
							}

							setEquipment(i, itemstack);
							dropChances[i] = 2.0F;
							persistent = true;
							receive(entityitem, 1);
							entityitem.die();
						}
					}
				}
			}
		}

		world.methodProfiler.b();
	}

	@Override
	protected boolean bk() {
		return false;
	}

	protected boolean isTypeNotPersistent() {
		return true;
	}

	protected void w() {
		if (persistent) {
			aU = 0;
		} else {
			EntityHuman entityhuman = world.findNearbyPlayerWhoAffectsSpawning(this, -1.0D); // PaperSpigot

			if (entityhuman != null) {
				double d0 = entityhuman.locX - locX;
				double d1 = entityhuman.locY - locY;
				double d2 = entityhuman.locZ - locZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				if (d3 > world.paperSpigotConfig.hardDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // PaperSpigot - custom despawn distances
					this.die();
				}

				if (aU > 600 && random.nextInt(800) == 0 && d3 > world.paperSpigotConfig.softDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // PaperSpigot - custom despawn distances
					this.die();
				} else if (d3 < world.paperSpigotConfig.softDespawnDistance) { // PaperSpigot - custom despawn distances
					aU = 0;
				}
			}
		}
	}

	@Override
	protected void bn() {
		++aU;
		world.methodProfiler.a("checkDespawn");
		w();
		world.methodProfiler.b();
		// Spigot Start
		if (fromMobSpawner || getName().equals("CaveSpider") || getName().equals("Spider")) {
			// PaperSpigot start - Allow nerfed mobs to jump
			world.methodProfiler.a("goalSelector");
			goalSelector.a();
			world.methodProfiler.c("jump");
			bm.b();
			// PaperSpigot end
			return;
		}
		// Spigot End
		world.methodProfiler.a("sensing");
		bq.a();
		world.methodProfiler.b();
		world.methodProfiler.a("targetSelector");
		targetSelector.a();
		world.methodProfiler.b();
		world.methodProfiler.a("goalSelector");
		goalSelector.a();
		world.methodProfiler.b();
		world.methodProfiler.a("navigation");
		navigation.f();
		world.methodProfiler.b();
		world.methodProfiler.a("mob tick");
		bp();
		world.methodProfiler.b();
		world.methodProfiler.a("controls");
		world.methodProfiler.a("move");
		moveController.c();
		world.methodProfiler.c("look");
		lookController.a();
		world.methodProfiler.c("jump");
		bm.b();
		world.methodProfiler.b();
		world.methodProfiler.b();
	}

	@Override
	protected void bq() {
		super.bq();
		bd = 0.0F;
		be = 0.0F;
		w();
		float f = 8.0F;

		if (random.nextFloat() < 0.02F) {
			EntityHuman entityhuman = world.findNearbyPlayer(this, f);

			if (entityhuman != null) {
				bu = entityhuman;
				g = 10 + random.nextInt(20);
			} else {
				bf = (random.nextFloat() - 0.5F) * 20.0F;
			}
		}

		if (bu != null) {
			this.a(bu, 10.0F, x());
			if (g-- <= 0 || bu.dead || bu.f(this) > f * f) {
				bu = null;
			}
		} else {
			if (random.nextFloat() < 0.05F) {
				bf = (random.nextFloat() - 0.5F) * 20.0F;
			}

			yaw += bf;
			pitch = this.f;
		}

		boolean flag = M();
		boolean flag1 = P();

		if (flag || flag1) {
			bc = random.nextFloat() < 0.8F;
		}
	}

	public int x() {
		return 40;
	}

	public void a(Entity entity, float f, float f1) {
		double d0 = entity.locX - locX;
		double d1 = entity.locZ - locZ;
		double d2;

		if (entity instanceof EntityLiving) {
			EntityLiving entityliving = (EntityLiving) entity;

			d2 = entityliving.locY + entityliving.getHeadHeight() - (locY + getHeadHeight());
		} else {
			d2 = (entity.boundingBox.b + entity.boundingBox.e) / 2.0D - (locY + getHeadHeight());
		}

		double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
		float f2 = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
		float f3 = (float) -(Math.atan2(d2, d3) * 180.0D / 3.1415927410125732D);

		pitch = this.b(pitch, f3, f1);
		yaw = this.b(yaw, f2, f);
	}

	private float b(float f, float f1, float f2) {
		float f3 = MathHelper.g(f1 - f);

		if (f3 > f2) {
			f3 = f2;
		}

		if (f3 < -f2) {
			f3 = -f2;
		}

		return f + f3;
	}

	public boolean canSpawn() {
		return world.b(boundingBox) && world.getCubes(this, boundingBox).isEmpty() && !world.containsLiquid(boundingBox);
	}

	public int bB() {
		return 4;
	}

	@Override
	public int ax() {
		if (getGoalTarget() == null)
			return 3;
		else {
			int i = (int) (getHealth() - getMaxHealth() * 0.33F);

			i -= (3 - world.difficulty.a()) * 4;
			if (i < 0) {
				i = 0;
			}

			return i + 3;
		}
	}

	@Override
	public ItemStack be() {
		return equipment[0];
	}

	@Override
	public ItemStack getEquipment(int i) {
		return equipment[i];
	}

	public ItemStack r(int i) {
		return equipment[i + 1];
	}

	@Override
	public void setEquipment(int i, ItemStack itemstack) {
		equipment[i] = itemstack;
	}

	@Override
	public ItemStack[] getEquipment() {
		return equipment;
	}

	@Override
	protected void dropEquipment(boolean flag, int i) {
		for (int j = 0; j < this.getEquipment().length; ++j) {
			ItemStack itemstack = this.getEquipment(j);
			boolean flag1 = dropChances[j] > 1.0F;

			if (itemstack != null && (flag || flag1) && random.nextFloat() - i * 0.01F < dropChances[j]) {
				if (!flag1 && itemstack.g()) {
					int k = Math.max(itemstack.l() - 25, 1);
					int l = itemstack.l() - random.nextInt(random.nextInt(k) + 1);

					if (l > k) {
						l = k;
					}

					if (l < 1) {
						l = 1;
					}

					itemstack.setData(l);
				}

				this.a(itemstack, 0.0F);
			}
		}
	}

	protected void bC() {
		if (random.nextFloat() < 0.15F * world.b(locX, locY, locZ)) {
			int i = random.nextInt(2);
			float f = world.difficulty == EnumDifficulty.HARD ? 0.1F : 0.25F;

			if (random.nextFloat() < 0.095F) {
				++i;
			}

			if (random.nextFloat() < 0.095F) {
				++i;
			}

			if (random.nextFloat() < 0.095F) {
				++i;
			}

			for (int j = 3; j >= 0; --j) {
				ItemStack itemstack = this.r(j);

				if (j < 3 && random.nextFloat() < f) {
					break;
				}

				if (itemstack == null) {
					Item item = a(j + 1, i);

					if (item != null) {
						setEquipment(j + 1, new ItemStack(item));
					}
				}
			}
		}
	}

	public static int b(ItemStack itemstack) {
		if (itemstack.getItem() != Item.getItemOf(Blocks.PUMPKIN) && itemstack.getItem() != Items.SKULL) {
			if (itemstack.getItem() instanceof ItemArmor) {
				switch (((ItemArmor) itemstack.getItem()).b) {
				case 0:
					return 4;

				case 1:
					return 3;

				case 2:
					return 2;

				case 3:
					return 1;
				}
			}

			return 0;
		} else
			return 4;
	}

	public static Item a(int i, int j) {
		switch (i) {
		case 4:
			if (j == 0)
				return Items.LEATHER_HELMET;
			else if (j == 1)
				return Items.GOLD_HELMET;
			else if (j == 2)
				return Items.CHAINMAIL_HELMET;
			else if (j == 3)
				return Items.IRON_HELMET;
			else if (j == 4)
				return Items.DIAMOND_HELMET;

		case 3:
			if (j == 0)
				return Items.LEATHER_CHESTPLATE;
			else if (j == 1)
				return Items.GOLD_CHESTPLATE;
			else if (j == 2)
				return Items.CHAINMAIL_CHESTPLATE;
			else if (j == 3)
				return Items.IRON_CHESTPLATE;
			else if (j == 4)
				return Items.DIAMOND_CHESTPLATE;

		case 2:
			if (j == 0)
				return Items.LEATHER_LEGGINGS;
			else if (j == 1)
				return Items.GOLD_LEGGINGS;
			else if (j == 2)
				return Items.CHAINMAIL_LEGGINGS;
			else if (j == 3)
				return Items.IRON_LEGGINGS;
			else if (j == 4)
				return Items.DIAMOND_LEGGINGS;

		case 1:
			if (j == 0)
				return Items.LEATHER_BOOTS;
			else if (j == 1)
				return Items.GOLD_BOOTS;
			else if (j == 2)
				return Items.CHAINMAIL_BOOTS;
			else if (j == 3)
				return Items.IRON_BOOTS;
			else if (j == 4)
				return Items.DIAMOND_BOOTS;

		default:
			return null;
		}
	}

	protected void bD() {
		float f = world.b(locX, locY, locZ);

		if (be() != null && random.nextFloat() < 0.25F * f) {
			EnchantmentManager.a(random, be(), (int) (5.0F + f * random.nextInt(18)));
		}

		for (int i = 0; i < 4; ++i) {
			ItemStack itemstack = this.r(i);

			if (itemstack != null && random.nextFloat() < 0.5F * f) {
				EnchantmentManager.a(random, itemstack, (int) (5.0F + f * random.nextInt(18)));
			}
		}
	}

	public GroupDataEntity prepare(GroupDataEntity groupdataentity) {
		getAttributeInstance(GenericAttributes.b).a(new AttributeModifier("Random spawn bonus", random.nextGaussian() * 0.05D, 1));
		return groupdataentity;
	}

	public boolean bE() {
		return false;
	}

	@Override
	public String getName() {
		return hasCustomName() ? getCustomName() : super.getName();
	}

	public void bF() {
		persistent = true;
	}

	public void setCustomName(String s) {
		datawatcher.watch(10, s);
		datawatcher.watch(2, s); // Spigot - protocol patch
	}

	public String getCustomName() {
		return datawatcher.getString(10);
	}

	public boolean hasCustomName() {
		return datawatcher.getString(10).length() > 0;
	}

	public void setCustomNameVisible(boolean flag) {
		datawatcher.watch(11, Byte.valueOf((byte) (flag ? 1 : 0)));
		datawatcher.watch(3, Byte.valueOf((byte) (flag ? 1 : 0))); // Spigot - protocol patch
	}

	public boolean getCustomNameVisible() {
		return datawatcher.getByte(11) == 1;
	}

	public void a(int i, float f) {
		dropChances[i] = f;
	}

	public boolean bJ() {
		return canPickUpLoot;
	}

	public void h(boolean flag) {
		canPickUpLoot = flag;
	}

	public boolean isPersistent() {
		return persistent;
	}

	@Override
	public final boolean c(EntityHuman entityhuman) {
		if (bN() && getLeashHolder() == entityhuman) {
			// CraftBukkit start - fire PlayerUnleashEntityEvent
			if (CraftEventFactory.callPlayerUnleashEntityEvent(this, entityhuman).isCancelled()) {
				((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, getLeashHolder()));
				return false;
			}
			// CraftBukkit end
			unleash(true, !entityhuman.abilities.canInstantlyBuild);
			return true;
		} else {
			ItemStack itemstack = entityhuman.inventory.getItemInHand();

			if (itemstack != null && itemstack.getItem() == Items.LEASH && bM()) {
				if (!(this instanceof EntityTameableAnimal) || !((EntityTameableAnimal) this).isTamed()) {
					// CraftBukkit start - fire PlayerLeashEntityEvent
					if (CraftEventFactory.callPlayerLeashEntityEvent(this, entityhuman, entityhuman).isCancelled()) {
						((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, getLeashHolder()));
						return false;
					}
					// CraftBukkit end
					setLeashHolder(entityhuman, true);
					--itemstack.count;
					return true;
				}

				if (((EntityTameableAnimal) this).e(entityhuman)) {
					// CraftBukkit start - fire PlayerLeashEntityEvent
					if (CraftEventFactory.callPlayerLeashEntityEvent(this, entityhuman, entityhuman).isCancelled()) {
						((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, getLeashHolder()));
						return false;
					}
					// CraftBukkit end
					setLeashHolder(entityhuman, true);
					--itemstack.count;
					return true;
				}
			}

			return this.a(entityhuman) ? true : super.c(entityhuman);
		}
	}

	protected boolean a(EntityHuman entityhuman) {
		return false;
	}

	protected void bL() {
		if (bx != null) {
			bP();
		}

		if (bv) {
			if (bw == null || bw.dead) {
				world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(getBukkitEntity(), UnleashReason.HOLDER_GONE)); // CraftBukkit
				unleash(true, true);
			}
		}
	}

	public void unleash(boolean flag, boolean flag1) {
		if (bv) {
			bv = false;
			bw = null;
			if (!world.isStatic && flag1) {
				this.a(Items.LEASH, 1);
			}

			if (!world.isStatic && flag && world instanceof WorldServer) {
				((WorldServer) world).getTracker().a(this, new PacketPlayOutAttachEntity(1, this, (Entity) null));
			}
		}
	}

	public boolean bM() {
		return !bN() && !(this instanceof IMonster);
	}

	public boolean bN() {
		return bv;
	}

	public Entity getLeashHolder() {
		return bw;
	}

	public void setLeashHolder(Entity entity, boolean flag) {
		bv = true;
		bw = entity;
		if (!world.isStatic && flag && world instanceof WorldServer) {
			((WorldServer) world).getTracker().a(this, new PacketPlayOutAttachEntity(1, this, bw));
		}
	}

	private void bP() {
		if (bv && bx != null) {
			if (bx.hasKeyOfType("UUIDMost", 4) && bx.hasKeyOfType("UUIDLeast", 4)) {
				UUID uuid = new UUID(bx.getLong("UUIDMost"), bx.getLong("UUIDLeast"));
				List list = world.a(EntityLiving.class, boundingBox.grow(10.0D, 10.0D, 10.0D));
				Iterator iterator = list.iterator();

				while (iterator.hasNext()) {
					EntityLiving entityliving = (EntityLiving) iterator.next();

					if (entityliving.getUniqueID().equals(uuid)) {
						bw = entityliving;
						break;
					}
				}
			} else if (bx.hasKeyOfType("X", 99) && bx.hasKeyOfType("Y", 99) && bx.hasKeyOfType("Z", 99)) {
				int i = bx.getInt("X");
				int j = bx.getInt("Y");
				int k = bx.getInt("Z");
				EntityLeash entityleash = EntityLeash.b(world, i, j, k);

				if (entityleash == null) {
					entityleash = EntityLeash.a(world, i, j, k);
				}

				bw = entityleash;
			} else {
				world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(getBukkitEntity(), UnleashReason.UNKNOWN)); // CraftBukkit
				unleash(false, true);
			}
		}

		bx = null;
	}
}
