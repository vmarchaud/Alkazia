package net.minecraft.server;

// CraftBukkit start
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.craftbukkit.SpigotTimings; // Spigot
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
// CraftBukkit end

import com.google.common.base.Function;

public abstract class EntityLiving extends Entity {

	private static final UUID b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final AttributeModifier c = new AttributeModifier(b, "Sprinting speed boost", 0.30000001192092896D, 2).a(false);
	private AttributeMapBase d;
	public CombatTracker combatTracker = new CombatTracker(this); // CraftBukkit - private -> public, remove final
	public final HashMap effects = new HashMap(); // CraftBukkit - protected -> public
	private final ItemStack[] g = new ItemStack[5];
	public boolean at;
	public int au;
	public int av;
	public float aw;
	public int hurtTicks;
	public int ay;
	public float az;
	public int deathTicks;
	public int attackTicks;
	public float aC;
	public float aD;
	public float aE;
	public float aF;
	public float aG;
	public int maxNoDamageTicks = 20;
	public float aI;
	public float aJ;
	public float aK;
	public float aL;
	public float aM;
	public float aN;
	public float aO;
	public float aP;
	public float aQ = 0.02F;
	public EntityHuman killer; // CraftBukkit - protected -> public
	protected int lastDamageByPlayerTime;
	protected boolean aT;
	protected int aU;
	protected float aV;
	protected float aW;
	protected float aX;
	protected float aY;
	protected float aZ;
	protected int ba;
	public float lastDamage; // CraftBukkit - protected -> public
	protected boolean bc;
	public float bd;
	public float be;
	protected float bf;
	protected int bg;
	protected double bh;
	protected double bi;
	protected double bj;
	protected double bk;
	protected double bl;
	public boolean updateEffects = true; // CraftBukkit - private -> public
	public EntityLiving lastDamager; // CraftBukkit - private -> public
	private int bm;
	private EntityLiving bn;
	private int bo;
	private float bp;
	private int bq;
	private float br;
	// CraftBukkit start
	public int expToDrop;
	public int maxAirTicks = 300;
	ArrayList<org.bukkit.inventory.ItemStack> drops = null;

	// CraftBukkit end
	// Spigot start
	@Override
	public void inactiveTick() {
		super.inactiveTick();
		++aU; // Above all the floats
	}

	// Spigot end

	public EntityLiving(World world) {
		super(world);
		aD();
		// CraftBukkit - setHealth(getMaxHealth()) inlined and simplified to skip the instanceof check for EntityPlayer, as getBukkitEntity() is not initialized in constructor
		datawatcher.watch(6, (float) getAttributeInstance(GenericAttributes.maxHealth).getValue());
		k = true;
		aL = (float) (Math.random() + 1.0D) * 0.01F;
		setPosition(locX, locY, locZ);
		aK = (float) Math.random() * 12398.0F;
		yaw = (float) (Math.random() * 3.1415927410125732D * 2.0D);
		aO = yaw;
		W = 0.5F;
	}

	@Override
	protected void c() {
		datawatcher.a(7, Integer.valueOf(0));
		datawatcher.a(8, Byte.valueOf((byte) 0));
		datawatcher.a(9, Byte.valueOf((byte) 0));
		datawatcher.a(6, Float.valueOf(1.0F));
	}

	protected void aD() {
		getAttributeMap().b(GenericAttributes.maxHealth);
		getAttributeMap().b(GenericAttributes.c);
		getAttributeMap().b(GenericAttributes.d);
		if (!bk()) {
			getAttributeInstance(GenericAttributes.d).setValue(0.10000000149011612D);
		}
	}

	@Override
	protected void a(double d0, boolean flag) {
		if (!M()) {
			N();
		}

		if (flag && fallDistance > 0.0F) {
			int i = MathHelper.floor(locX);
			int j = MathHelper.floor(locY - 0.20000000298023224D - height);
			int k = MathHelper.floor(locZ);
			Block block = world.getType(i, j, k);

			if (block.getMaterial() == Material.AIR) {
				int l = world.getType(i, j - 1, k).b();

				if (l == 11 || l == 32 || l == 21) {
					block = world.getType(i, j - 1, k);
				}
			} else if (!world.isStatic && fallDistance > 3.0F) {
				// CraftBukkit start - supply player as argument in particles for visibility API to work
				if (this instanceof EntityPlayer) {
					world.a((EntityHuman) this, 2006, i, j, k, MathHelper.f(fallDistance - 3.0F));
					((EntityPlayer) this).playerConnection.sendPacket(new PacketPlayOutWorldEvent(2006, i, j, k, MathHelper.f(fallDistance - 3.0F), false));
				} else {
					world.triggerEffect(2006, i, j, k, MathHelper.f(fallDistance - 3.0F));
				}
				// CraftBukkit end
			}

			block.a(world, i, j, k, this, fallDistance);
		}

		super.a(d0, flag);
	}

	public boolean aE() {
		return false;
	}

	@Override
	public void C() {
		aC = aD;
		super.C();
		world.methodProfiler.a("livingEntityBaseTick");
		if (isAlive() && inBlock()) {
			damageEntity(DamageSource.STUCK, 1.0F);
		}

		if (isFireproof() || world.isStatic) {
			extinguish();
		}

		boolean flag = this instanceof EntityHuman && ((EntityHuman) this).abilities.isInvulnerable;

		if (isAlive() && this.a(Material.WATER)) {
			if (!aE() && !this.hasEffect(MobEffectList.WATER_BREATHING.id) && !flag) {
				setAirTicks(this.j(getAirTicks()));
				if (getAirTicks() == -20) {
					setAirTicks(0);

					for (int i = 0; i < 8; ++i) {
						float f = random.nextFloat() - random.nextFloat();
						float f1 = random.nextFloat() - random.nextFloat();
						float f2 = random.nextFloat() - random.nextFloat();

						world.addParticle("bubble", locX + f, locY + f1, locZ + f2, motX, motY, motZ);
					}

					damageEntity(DamageSource.DROWN, 2.0F);
				}
			}

			if (!world.isStatic && am() && vehicle instanceof EntityLiving) {
				mount((Entity) null);
			}
		} else {
			// CraftBukkit start - Only set if needed to work around a DataWatcher inefficiency
			if (getAirTicks() != 300) {
				setAirTicks(maxAirTicks);
			}
			// CraftBukkit end
		}

		if (isAlive() && L()) {
			extinguish();
		}

		aI = aJ;
		if (attackTicks > 0) {
			--attackTicks;
		}

		if (hurtTicks > 0) {
			--hurtTicks;
		}

		if (noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
			--noDamageTicks;
		}

		if (getHealth() <= 0.0F) {
			aF();
		}

		if (lastDamageByPlayerTime > 0) {
			--lastDamageByPlayerTime;
		} else {
			killer = null;
		}

		if (bn != null && !bn.isAlive()) {
			bn = null;
		}

		if (lastDamager != null) {
			if (!lastDamager.isAlive()) {
				this.b((EntityLiving) null);
			} else if (ticksLived - bm > 100) {
				this.b((EntityLiving) null);
			}
		}

		aO();
		aY = aX;
		aN = aM;
		aP = aO;
		lastYaw = yaw;
		lastPitch = pitch;
		world.methodProfiler.b();
	}

	// CraftBukkit start
	public int getExpReward() {
		int exp = getExpValue(killer);

		if (!world.isStatic && (lastDamageByPlayerTime > 0 || alwaysGivesExp()) && aG() && world.getGameRules().getBoolean("doMobLoot"))
			return exp;
		else
			return 0;
	}

	// CraftBukkit end

	public boolean isBaby() {
		return false;
	}

	protected void aF() {
		++deathTicks;
		if (deathTicks >= 20 && !dead) { // CraftBukkit - (this.deathTicks == 20) -> (this.deathTicks >= 20 && !this.dead)
			int i;

			// CraftBukkit start - Update getExpReward() above if the removed if() changes!
			i = expToDrop;
			while (i > 0) {
				int j = EntityExperienceOrb.getOrbValue(i);

				i -= j;
				world.addEntity(new EntityExperienceOrb(world, locX, locY, locZ, j));
			}
			expToDrop = 0;
			// CraftBukkit end

			this.die();

			for (i = 0; i < 20; ++i) {
				double d0 = random.nextGaussian() * 0.02D;
				double d1 = random.nextGaussian() * 0.02D;
				double d2 = random.nextGaussian() * 0.02D;

				world.addParticle("explode", locX + random.nextFloat() * width * 2.0F - width, locY + random.nextFloat() * length, locZ + random.nextFloat() * width * 2.0F - width, d0, d1, d2);
			}
		}
	}

	protected boolean aG() {
		return !isBaby();
	}

	protected int j(int i) {
		int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

		return j > 0 && random.nextInt(j + 1) > 0 ? i : i - 1;
	}

	protected int getExpValue(EntityHuman entityhuman) {
		return 0;
	}

	protected boolean alwaysGivesExp() {
		return false;
	}

	public Random aI() {
		return random;
	}

	public EntityLiving getLastDamager() {
		return lastDamager;
	}

	public int aK() {
		return bm;
	}

	public void b(EntityLiving entityliving) {
		lastDamager = entityliving;
		bm = ticksLived;
	}

	public EntityLiving aL() {
		return bn;
	}

	public int aM() {
		return bo;
	}

	public void l(Entity entity) {
		if (entity instanceof EntityLiving) {
			bn = (EntityLiving) entity;
		} else {
			bn = null;
		}

		bo = ticksLived;
	}

	public int aN() {
		return aU;
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setFloat("HealF", getHealth());
		nbttagcompound.setShort("Health", (short) (int) Math.ceil(getHealth()));
		nbttagcompound.setShort("HurtTime", (short) hurtTicks);
		nbttagcompound.setShort("DeathTime", (short) deathTicks);
		nbttagcompound.setShort("AttackTime", (short) attackTicks);
		nbttagcompound.setFloat("AbsorptionAmount", getAbsorptionHearts());
		ItemStack[] aitemstack = this.getEquipment();
		int i = aitemstack.length;

		int j;
		ItemStack itemstack;

		for (j = 0; j < i; ++j) {
			itemstack = aitemstack[j];
			if (itemstack != null) {
				d.a(itemstack.D());
			}
		}

		nbttagcompound.set("Attributes", GenericAttributes.a(getAttributeMap()));
		aitemstack = this.getEquipment();
		i = aitemstack.length;

		for (j = 0; j < i; ++j) {
			itemstack = aitemstack[j];
			if (itemstack != null) {
				d.b(itemstack.D());
			}
		}

		if (!effects.isEmpty()) {
			NBTTagList nbttaglist = new NBTTagList();
			Iterator iterator = effects.values().iterator();

			while (iterator.hasNext()) {
				MobEffect mobeffect = (MobEffect) iterator.next();

				nbttaglist.add(mobeffect.a(new NBTTagCompound()));
			}

			nbttagcompound.set("ActiveEffects", nbttaglist);
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
		if (nbttagcompound.hasKeyOfType("Attributes", 9) && world != null && !world.isStatic) {
			GenericAttributes.a(getAttributeMap(), nbttagcompound.getList("Attributes", 10));
		}

		if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
			NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

			for (int i = 0; i < nbttaglist.size(); ++i) {
				NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
				MobEffect mobeffect = MobEffect.b(nbttagcompound1);

				if (mobeffect != null) {
					effects.put(Integer.valueOf(mobeffect.getEffectId()), mobeffect);
				}
			}
		}

		// CraftBukkit start
		if (nbttagcompound.hasKey("Bukkit.MaxHealth")) {
			NBTBase nbtbase = nbttagcompound.get("Bukkit.MaxHealth");
			if (nbtbase.getTypeId() == 5) {
				getAttributeInstance(GenericAttributes.maxHealth).setValue(((NBTTagFloat) nbtbase).c());
			} else if (nbtbase.getTypeId() == 3) {
				getAttributeInstance(GenericAttributes.maxHealth).setValue(((NBTTagInt) nbtbase).d());
			}
		}
		// CraftBukkit end

		if (nbttagcompound.hasKeyOfType("HealF", 99)) {
			setHealth(nbttagcompound.getFloat("HealF"));
		} else {
			NBTBase nbtbase = nbttagcompound.get("Health");

			if (nbtbase == null) {
				setHealth(getMaxHealth());
			} else if (nbtbase.getTypeId() == 5) {
				setHealth(((NBTTagFloat) nbtbase).h());
			} else if (nbtbase.getTypeId() == 2) {
				setHealth(((NBTTagShort) nbtbase).e());
			}
		}

		hurtTicks = nbttagcompound.getShort("HurtTime");
		deathTicks = nbttagcompound.getShort("DeathTime");
		attackTicks = nbttagcompound.getShort("AttackTime");
	}

	protected void aO() {
		Iterator iterator = effects.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			MobEffect mobeffect = (MobEffect) effects.get(integer);

			if (!mobeffect.tick(this)) {
				if (!world.isStatic) {
					iterator.remove();
					this.b(mobeffect);
				}
			} else if (mobeffect.getDuration() % 600 == 0) {
				this.a(mobeffect, false);
			}
		}

		int i;

		if (updateEffects) {
			if (!world.isStatic) {
				if (effects.isEmpty()) {
					datawatcher.watch(8, Byte.valueOf((byte) 0));
					datawatcher.watch(7, Integer.valueOf(0));
					setInvisible(false);
				} else {
					i = PotionBrewer.a(effects.values());
					datawatcher.watch(8, Byte.valueOf((byte) (PotionBrewer.b(effects.values()) ? 1 : 0)));
					datawatcher.watch(7, Integer.valueOf(i));
					setInvisible(this.hasEffect(MobEffectList.INVISIBILITY.id));
				}
			}

			updateEffects = false;
		}

		i = datawatcher.getInt(7);
		boolean flag = datawatcher.getByte(8) > 0;

		if (i > 0) {
			boolean flag1 = false;

			if (!isInvisible()) {
				flag1 = random.nextBoolean();
			} else {
				flag1 = random.nextInt(15) == 0;
			}

			if (flag) {
				flag1 &= random.nextInt(5) == 0;
			}

			if (flag1 && i > 0) {
				double d0 = (i >> 16 & 255) / 255.0D;
				double d1 = (i >> 8 & 255) / 255.0D;
				double d2 = (i >> 0 & 255) / 255.0D;

				world.addParticle(flag ? "mobSpellAmbient" : "mobSpell", locX + (random.nextDouble() - 0.5D) * width, locY + random.nextDouble() * length - height, locZ + (random.nextDouble() - 0.5D) * width, d0, d1, d2);
			}
		}
	}

	public void removeAllEffects() {
		Iterator iterator = effects.keySet().iterator();

		while (iterator.hasNext()) {
			Integer integer = (Integer) iterator.next();
			MobEffect mobeffect = (MobEffect) effects.get(integer);

			if (!world.isStatic) {
				iterator.remove();
				this.b(mobeffect);
			}
		}
	}

	public Collection getEffects() {
		return effects.values();
	}

	public boolean hasEffect(int i) {
		// CraftBukkit - Add size check for efficiency
		return effects.size() != 0 && effects.containsKey(Integer.valueOf(i));
	}

	public boolean hasEffect(MobEffectList mobeffectlist) {
		// CraftBukkit - Add size check for efficiency
		return effects.size() != 0 && effects.containsKey(Integer.valueOf(mobeffectlist.id));
	}

	public MobEffect getEffect(MobEffectList mobeffectlist) {
		return (MobEffect) effects.get(Integer.valueOf(mobeffectlist.id));
	}

	public void addEffect(MobEffect mobeffect) {
		if (this.d(mobeffect)) {
			if (effects.containsKey(Integer.valueOf(mobeffect.getEffectId()))) {
				((MobEffect) effects.get(Integer.valueOf(mobeffect.getEffectId()))).a(mobeffect);
				this.a((MobEffect) effects.get(Integer.valueOf(mobeffect.getEffectId())), true);
			} else {
				effects.put(Integer.valueOf(mobeffect.getEffectId()), mobeffect);
				this.a(mobeffect);
			}
		}
	}

	public boolean d(MobEffect mobeffect) {
		if (getMonsterType() == EnumMonsterType.UNDEAD) {
			int i = mobeffect.getEffectId();

			if (i == MobEffectList.REGENERATION.id || i == MobEffectList.POISON.id)
				return false;
		}

		return true;
	}

	public boolean aR() {
		return getMonsterType() == EnumMonsterType.UNDEAD;
	}

	public void removeEffect(int i) {
		MobEffect mobeffect = (MobEffect) effects.remove(Integer.valueOf(i));

		if (mobeffect != null) {
			this.b(mobeffect);
		}
	}

	protected void a(MobEffect mobeffect) {
		updateEffects = true;
		if (!world.isStatic) {
			MobEffectList.byId[mobeffect.getEffectId()].b(this, getAttributeMap(), mobeffect.getAmplifier());
		}
	}

	protected void a(MobEffect mobeffect, boolean flag) {
		updateEffects = true;
		if (flag && !world.isStatic) {
			MobEffectList.byId[mobeffect.getEffectId()].a(this, getAttributeMap(), mobeffect.getAmplifier());
			MobEffectList.byId[mobeffect.getEffectId()].b(this, getAttributeMap(), mobeffect.getAmplifier());
		}
	}

	protected void b(MobEffect mobeffect) {
		updateEffects = true;
		if (!world.isStatic) {
			MobEffectList.byId[mobeffect.getEffectId()].a(this, getAttributeMap(), mobeffect.getAmplifier());
		}
	}

	// CraftBukkit start - Delegate so we can handle providing a reason for health being regained
	public void heal(float f) {
		heal(f, EntityRegainHealthEvent.RegainReason.CUSTOM);
	}

	public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
		float f1 = getHealth();

		if (f1 > 0.0F) {
			EntityRegainHealthEvent event = new EntityRegainHealthEvent(getBukkitEntity(), f, regainReason);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				setHealth((float) (getHealth() + event.getAmount()));
			}
		}
	}

	public final float getHealth() {
		// CraftBukkit start - Use unscaled health
		if (this instanceof EntityPlayer)
			return (float) ((EntityPlayer) this).getBukkitEntity().getHealth();
		// CraftBukkit end
		return datawatcher.getFloat(6);
	}

	public void setHealth(float f) {
		// CraftBukkit start - Handle scaled health
		if (this instanceof EntityPlayer) {
			org.bukkit.craftbukkit.entity.CraftPlayer player = ((EntityPlayer) this).getBukkitEntity();
			// Squeeze
			if (f < 0.0F) {
				player.setRealHealth(0.0D);
			} else if (f > player.getMaxHealth()) {
				player.setRealHealth(player.getMaxHealth());
			} else {
				player.setRealHealth(f);
			}

			datawatcher.watch(6, Float.valueOf(player.getScaledHealth()));
			return;
		}
		// CraftBukkit end
		datawatcher.watch(6, Float.valueOf(MathHelper.a(f, 0.0F, getMaxHealth())));
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (world.isStatic)
			return false;
		else {
			aU = 0;
			if (getHealth() <= 0.0F)
				return false;
			else if (damagesource.o() && this.hasEffect(MobEffectList.FIRE_RESISTANCE))
				return false;
			else {
				// CraftBukkit - Moved into d(DamageSource, float)
				if (false && (damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && this.getEquipment(4) != null) {
					this.getEquipment(4).damage((int) (f * 4.0F + random.nextFloat() * f * 2.0F), this);
					f *= 0.75F;
				}

				aF = 1.5F;
				boolean flag = true;

				if (noDamageTicks > maxNoDamageTicks / 2.0F) {
					if (f <= lastDamage)
						return false;

					// CraftBukkit start
					if (!this.d(damagesource, f - lastDamage))
						return false;
					// CraftBukkit end
					lastDamage = f;
					flag = false;
				} else {
					// CraftBukkit start
					float previousHealth = getHealth();
					if (!this.d(damagesource, f))
						return false;
					lastDamage = f;
					aw = previousHealth;
					noDamageTicks = maxNoDamageTicks;
					// CraftBukkit end
					hurtTicks = ay = 10;
				}

				az = 0.0F;
				Entity entity = damagesource.getEntity();

				if (entity != null) {
					if (entity instanceof EntityLiving) {
						this.b((EntityLiving) entity);
					}

					if (entity instanceof EntityHuman) {
						lastDamageByPlayerTime = 100;
						killer = (EntityHuman) entity;
					} else if (entity instanceof EntityWolf) {
						EntityWolf entitywolf = (EntityWolf) entity;

						if (entitywolf.isTamed()) {
							lastDamageByPlayerTime = 100;
							killer = null;
						}
					}
				}

				if (flag) {
					world.broadcastEntityEffect(this, (byte) 2);
					if (damagesource != DamageSource.DROWN) {
						Q();
					}

					if (entity != null) {
						double d0 = entity.locX - locX;

						double d1;

						for (d1 = entity.locZ - locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
							d0 = (Math.random() - Math.random()) * 0.01D;
						}

						az = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - yaw;
						this.a(entity, f, d0, d1);
					} else {
						az = (int) (Math.random() * 2.0D) * 180;
					}
				}

				String s;

				if (getHealth() <= 0.0F) {
					s = aU();
					if (flag && s != null) {
						makeSound(s, bf(), bg());
					}

					this.die(damagesource);
				} else {
					s = aT();
					if (flag && s != null) {
						makeSound(s, bf(), bg());
					}
				}

				return true;
			}
		}
	}

	public void a(ItemStack itemstack) {
		makeSound("random.break", 0.8F, 0.8F + world.random.nextFloat() * 0.4F);

		for (int i = 0; i < 5; ++i) {
			Vec3D vec3d = Vec3D.a((random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

			vec3d.a(-pitch * 3.1415927F / 180.0F);
			vec3d.b(-yaw * 3.1415927F / 180.0F);
			Vec3D vec3d1 = Vec3D.a((random.nextFloat() - 0.5D) * 0.3D, -random.nextFloat() * 0.6D - 0.3D, 0.6D);

			vec3d1.a(-pitch * 3.1415927F / 180.0F);
			vec3d1.b(-yaw * 3.1415927F / 180.0F);
			vec3d1 = vec3d1.add(locX, locY + getHeadHeight(), locZ);
			world.addParticle("iconcrack_" + Item.getId(itemstack.getItem()), vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05D, vec3d.c);
		}
	}

	public void die(DamageSource damagesource) {
		Entity entity = damagesource.getEntity();
		EntityLiving entityliving = aX();

		if (ba >= 0 && entityliving != null) {
			entityliving.b(this, ba);
		}

		if (entity != null) {
			entity.a(this);
		}

		aT = true;
		aW().g();
		if (!world.isStatic) {
			int i = 0;

			if (entity instanceof EntityHuman) {
				i = EnchantmentManager.getBonusMonsterLootEnchantmentLevel((EntityLiving) entity);
			}

			if (aG() && world.getGameRules().getBoolean("doMobLoot")) {
				drops = new ArrayList<org.bukkit.inventory.ItemStack>(); // CraftBukkit - Setup drop capture

				dropDeathLoot(lastDamageByPlayerTime > 0, i);
				dropEquipment(lastDamageByPlayerTime > 0, i);
				if (lastDamageByPlayerTime > 0) {
					int j = random.nextInt(200) - i;

					if (j < 5) {
						getRareDrop(j <= 0 ? 1 : 0);
					}
				}

				// CraftBukkit start - Call death event
				CraftEventFactory.callEntityDeathEvent(this, drops);
				drops = null;
			} else {
				CraftEventFactory.callEntityDeathEvent(this);
				// CraftBukkit end
			}
		}

		world.broadcastEntityEffect(this, (byte) 3);
	}

	protected void dropEquipment(boolean flag, int i) {
	}

	public void a(Entity entity, float f, double d0, double d1) {
		if (random.nextDouble() >= getAttributeInstance(GenericAttributes.c).getValue()) {
			al = true;
			float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
			float f2 = 0.4F;

			motX /= 2.0D;
			motY /= 2.0D;
			motZ /= 2.0D;
			motX -= d0 / f1 * f2;
			motY += f2;
			motZ -= d1 / f1 * f2;
			if (motY > 0.4000000059604645D) {
				motY = 0.4000000059604645D;
			}
		}
	}

	protected String aT() {
		return "game.neutral.hurt";
	}

	protected String aU() {
		return "game.neutral.die";
	}

	protected void getRareDrop(int i) {
	}

	protected void dropDeathLoot(boolean flag, int i) {
	}

	public boolean h_() {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(boundingBox.b);
		int k = MathHelper.floor(locZ);
		Block block = world.getType(i, j, k);

		return block == Blocks.LADDER || block == Blocks.VINE;
	}

	@Override
	public boolean isAlive() {
		return !dead && getHealth() > 0.0F;
	}

	@Override
	protected void b(float f) {
		super.b(f);
		MobEffect mobeffect = getEffect(MobEffectList.JUMP);
		float f1 = mobeffect != null ? (float) (mobeffect.getAmplifier() + 1) : 0.0F;
		int i = MathHelper.f(f - 3.0F - f1);

		if (i > 0) {
			// CraftBukkit start
			// Alkazia - fall effect is here
            if(this.hasEffect(MobEffectList.fall)) {
            	if (!damageEntity(DamageSource.FALL, i / 3))
    				return;
            } 
            else {
            	if (!damageEntity(DamageSource.FALL, i))
    				return;
            }
			// CraftBukkit end
			makeSound(this.o(i), 1.0F, 1.0F);
			// this.damageEntity(DamageSource.FALL, (float) i); // CraftBukkit - moved up
			int j = MathHelper.floor(locX);
			int k = MathHelper.floor(locY - 0.20000000298023224D - height);
			int l = MathHelper.floor(locZ);
			Block block = world.getType(j, k, l);

			if (block.getMaterial() != Material.AIR) {
				StepSound stepsound = block.stepSound;

				makeSound(stepsound.getStepSound(), stepsound.getVolume1() * 0.5F, stepsound.getVolume2() * 0.75F);
			}
		}
	}
	
	public boolean isOnIronLadder() {
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(boundingBox.b);
		int k = MathHelper.floor(locZ);
		Block block = world.getType(i, j, k);

		return block == Blocks.ironLadder;
	}
	
	 public void clearNegativeEffect() {
	        Iterator iterator = this.effects.keySet().iterator();

	        while (iterator.hasNext()) {
	            Integer integer = (Integer) iterator.next();
	            MobEffect mobeffect = (MobEffect) this.effects.get(integer);
	            if (!this.world.isStatic && (mobeffect.f() == "potion.poison" || mobeffect.f() == "potion.weakness" || mobeffect.f() == "potion.moveSlowdown" )) {
	                iterator.remove();
	                this.b(mobeffect);
	            }
	        }
	    }

	protected String o(int i) {
		return i > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
	}

	public int aV() {
		int i = 0;
		ItemStack[] aitemstack = this.getEquipment();
		int j = aitemstack.length;

		for (int k = 0; k < j; ++k) {
			ItemStack itemstack = aitemstack[k];

			if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
				int l = ((ItemArmor) itemstack.getItem()).c;

				i += l;
			}
		}

		return i;
	}

	protected void damageArmor(float f) {
	}

	protected float applyArmorModifier(DamageSource damagesource, float f) {
		if (!damagesource.ignoresArmor()) {
			int i = 25 - aV();
			float f1 = f * i;

			// this.damageArmor(f); // CraftBukkit - Moved into d(DamageSource, float)
			f = f1 / 25.0F;
		}

		return f;
	}

	protected float applyMagicModifier(DamageSource damagesource, float f) {
		if (damagesource.isStarvation())
			return f;
		else {
			if (this instanceof EntityZombie) {
				f = f;
			}

			int i;
			int j;
			float f1;

			// CraftBukkit - Moved to d(DamageSource, float)
			if (false && this.hasEffect(MobEffectList.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
				i = (getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
				j = 25 - i;
				f1 = f * j;
				f = f1 / 25.0F;
			}

			if (f <= 0.0F)
				return 0.0F;
			else {
				i = EnchantmentManager.a(this.getEquipment(), damagesource);
				if (i > 20) {
					i = 20;
				}

				if (i > 0 && i <= 20) {
					j = 25 - i;
					f1 = f * j;
					f = f1 / 25.0F;
				}

				return f;
			}
		}
	}

	// CraftBukkit start
	protected boolean d(final DamageSource damagesource, float f) { // void -> boolean, add final
		if (!isInvulnerable()) {
			final boolean human = this instanceof EntityHuman;
			float originalDamage = f;
			Function<Double, Double> hardHat = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && EntityLiving.this.getEquipment(4) != null)
						return -(f - f * 0.75F);
					return -0.0;
				}
			};
			float hardHatModifier = hardHat.apply((double) f).floatValue();
			f += hardHatModifier;

			Function<Double, Double> blocking = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					if (human) {
						if (!damagesource.ignoresArmor() && ((EntityHuman) EntityLiving.this).isBlocking() && f > 0.0F)
							return -(f - (1.0F + f) * 0.5F);
					}
					return -0.0;
				}
			};
			float blockingModifier = blocking.apply((double) f).floatValue();
			f += blockingModifier;

			Function<Double, Double> armor = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					return -(f - EntityLiving.this.applyArmorModifier(damagesource, f.floatValue()));
				}
			};
			float armorModifier = armor.apply((double) f).floatValue();
			f += armorModifier;

			Function<Double, Double> resistance = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					if (!damagesource.isStarvation() && EntityLiving.this.hasEffect(MobEffectList.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
						int i = (EntityLiving.this.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
						int j = 25 - i;
						float f1 = f.floatValue() * j;
						return -(f - f1 / 25.0F);
					}
					return -0.0;
				}
			};
			float resistanceModifier = resistance.apply((double) f).floatValue();
			f += resistanceModifier;

			Function<Double, Double> magic = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					return -(f - EntityLiving.this.applyMagicModifier(damagesource, f.floatValue()));
				}
			};
			float magicModifier = magic.apply((double) f).floatValue();
			f += magicModifier;

			Function<Double, Double> absorption = new Function<Double, Double>() {
				@Override
				public Double apply(Double f) {
					return -Math.max(f - Math.max(f - EntityLiving.this.getAbsorptionHearts(), 0.0F), 0.0F);
				}
			};
			float absorptionModifier = absorption.apply((double) f).floatValue();

			EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(this, damagesource, originalDamage, hardHatModifier, blockingModifier, armorModifier, resistanceModifier, magicModifier, absorptionModifier, hardHat, blocking, armor, resistance, magic, absorption);
			if (event.isCancelled())
				return false;

			f = (float) event.getFinalDamage();

			// Apply damage to helmet
			if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && this.getEquipment(4) != null) {
				this.getEquipment(4).damage((int) (event.getDamage() * 4.0F + random.nextFloat() * event.getDamage() * 2.0F), this);
			}

			// Apply damage to armor
			if (!damagesource.ignoresArmor()) {
				float armorDamage = (float) (event.getDamage() + event.getDamage(DamageModifier.BLOCKING) + event.getDamage(DamageModifier.HARD_HAT));
				damageArmor(armorDamage);
			}

			absorptionModifier = (float) -event.getDamage(DamageModifier.ABSORPTION);
			setAbsorptionHearts(Math.max(getAbsorptionHearts() - absorptionModifier, 0.0F));
			if (f != 0.0F) {
				if (human) {
					((EntityHuman) this).applyExhaustion(damagesource.getExhaustionCost());
				}
				// CraftBukkit end
				float f2 = getHealth();

				setHealth(f2 - f);
				aW().a(damagesource, f2, f);
				// CraftBukkit start
				if (human)
					return true;
				// CraftBukkit end
				setAbsorptionHearts(getAbsorptionHearts() - f);
			}
			return true; // CraftBukkit
		}
		return false; // CraftBukkit
	}

	public CombatTracker aW() {
		return combatTracker;
	}

	public EntityLiving aX() {
		return combatTracker.c() != null ? combatTracker.c() : killer != null ? killer : lastDamager != null ? lastDamager : null;
	}

	public final float getMaxHealth() {
		return (float) getAttributeInstance(GenericAttributes.maxHealth).getValue();
	}

	public final int aZ() {
		return datawatcher.getByte(9);
	}

	public final void p(int i) {
		datawatcher.watch(9, Byte.valueOf((byte) i));
	}

	private int j() {
		return this.hasEffect(MobEffectList.FASTER_DIG) ? 6 - (1 + getEffect(MobEffectList.FASTER_DIG).getAmplifier()) * 1 : this.hasEffect(MobEffectList.SLOWER_DIG) ? 6 + (1 + getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) * 2 : 6;
	}

	public void ba() {
		if (!at || au >= this.j() / 2 || au < 0) {
			au = -1;
			at = true;
			if (world instanceof WorldServer) {
				((WorldServer) world).getTracker().a(this, new PacketPlayOutAnimation(this, 0));
			}
		}
	}

	@Override
	protected void G() {
		damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void bb() {
		int i = this.j();

		if (at) {
			++au;
			if (au >= i) {
				au = 0;
				at = false;
			}
		} else {
			au = 0;
		}

		aD = (float) au / (float) i;
	}

	public AttributeInstance getAttributeInstance(IAttribute iattribute) {
		return getAttributeMap().a(iattribute);
	}

	public AttributeMapBase getAttributeMap() {
		if (d == null) {
			d = new AttributeMapServer();
		}

		return d;
	}

	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.UNDEFINED;
	}

	public abstract ItemStack be();

	public abstract ItemStack getEquipment(int i);

	@Override
	public abstract void setEquipment(int i, ItemStack itemstack);

	@Override
	public void setSprinting(boolean flag) {
		super.setSprinting(flag);
		AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

		if (attributeinstance.a(b) != null) {
			attributeinstance.b(c);
		}

		if (flag) {
			attributeinstance.a(c);
		}
	}

	@Override
	public abstract ItemStack[] getEquipment();

	protected float bf() {
		return 1.0F;
	}

	protected float bg() {
		return isBaby() ? (random.nextFloat() - random.nextFloat()) * 0.2F + 1.5F : (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean bh() {
		return getHealth() <= 0.0F;
	}

	public void enderTeleportTo(double d0, double d1, double d2) {
		setPositionRotation(d0, d1, d2, yaw, pitch);
	}

	public void m(Entity entity) {
		double d0 = entity.locX;
		double d1 = entity.boundingBox.b + entity.length;
		double d2 = entity.locZ;
		byte b0 = 1;

		for (int i = -b0; i <= b0; ++i) {
			for (int j = -b0; j < b0; ++j) {
				if (i != 0 || j != 0) {
					int k = (int) (locX + i);
					int l = (int) (locZ + j);
					AxisAlignedBB axisalignedbb = boundingBox.c(i, 1.0D, j);

					if (world.a(axisalignedbb).isEmpty()) {
						if (World.a(world, k, (int) locY, l)) {
							enderTeleportTo(locX + i, locY + 1.0D, locZ + j);
							return;
						}

						if (World.a(world, k, (int) locY - 1, l) || world.getType(k, (int) locY - 1, l).getMaterial() == Material.WATER) {
							d0 = locX + i;
							d1 = locY + 1.0D;
							d2 = locZ + j;
						}
					}
				}
			}
		}

		enderTeleportTo(d0, d1, d2);
	}

	protected void bj() {
		motY = 0.41999998688697815D;
		if (this.hasEffect(MobEffectList.JUMP)) {
			motY += (getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F;
		}

		if (isSprinting()) {
			float f = yaw * 0.017453292F;

			motX -= MathHelper.sin(f) * 0.2F;
			motZ += MathHelper.cos(f) * 0.2F;
		}

		al = true;
	}

	public void e(float f, float f1) {
		double d0;

		if (M() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
			d0 = locY;
			this.a(f, f1, bk() ? 0.04F : 0.02F);
			move(motX, motY, motZ);
			motX *= 0.800000011920929D;
			motY *= 0.800000011920929D;
			motZ *= 0.800000011920929D;
			motY -= 0.02D;
			if (positionChanged && this.c(motX, motY + 0.6000000238418579D - locY + d0, motZ)) {
				motY = 0.30000001192092896D;
			}
		} else if (P() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
			d0 = locY;
			this.a(f, f1, 0.02F);
			move(motX, motY, motZ);
			motX *= 0.5D;
			motY *= 0.5D;
			motZ *= 0.5D;
			motY -= 0.02D;
			if (positionChanged && this.c(motX, motY + 0.6000000238418579D - locY + d0, motZ)) {
				motY = 0.30000001192092896D;
			}
		} else {
			float f2 = 0.91F;

			if (onGround) {
				f2 = world.getType(MathHelper.floor(locX), MathHelper.floor(boundingBox.b) - 1, MathHelper.floor(locZ)).frictionFactor * 0.91F;
			}

			float f3 = 0.16277136F / (f2 * f2 * f2);
			float f4;

			if (onGround) {
				f4 = bl() * f3;
			} else {
				f4 = aQ;
			}

			this.a(f, f1, f4);
			f2 = 0.91F;
			if (onGround) {
				f2 = world.getType(MathHelper.floor(locX), MathHelper.floor(boundingBox.b) - 1, MathHelper.floor(locZ)).frictionFactor * 0.91F;
			}

			if (h_()) {
				float f5 = 0.15F;

				if (motX < -f5) {
					motX = -f5;
				}

				if (motX > f5) {
					motX = f5;
				}

				if (motZ < -f5) {
					motZ = -f5;
				}

				if (motZ > f5) {
					motZ = f5;
				}

				fallDistance = 0.0F;
				if (motY < -0.15D) {
					motY = -0.15D;
				}

				boolean flag = isSneaking() && this instanceof EntityHuman;

				if (flag && motY < 0.0D) {
					motY = 0.0D;
				}
			}
			
			// Alkazia - iron ladder
            if (this.isOnIronLadder()) {
                float f5 = 0.15F;

                if (this.motX < (double) (-f5)) {
                    this.motX = (double) (-f5);
                }

                if (this.motX > (double) f5) {
                    this.motX = (double) f5;
                }

                if (this.motZ < (double) (-f5)) {
                    this.motZ = (double) (-f5);
                }

                if (this.motZ > (double) f5) {
                    this.motZ = (double) f5;
                }

                this.fallDistance = 0.0F;
                if (this.motY < -0.15D) {
                    this.motY = -0.15D;
                }

                boolean flag = this.isSneaking() && this instanceof EntityHuman;

                if (flag && this.motY < 0.0D) {
                    this.motY = 0.0D;
                }
            }
         // Alkazia - iron ladder
            if (this.positionChanged && this.isOnIronLadder()) {
                this.motY = 0.5D;
            }

			move(motX, motY, motZ);
			if (positionChanged && h_()) {
				motY = 0.2D;
			}

			if (world.isStatic && (!world.isLoaded((int) locX, 0, (int) locZ) || !world.getChunkAtWorldCoords((int) locX, (int) locZ).d)) {
				if (locY > 0.0D) {
					motY = -0.1D;
				} else {
					motY = 0.0D;
				}
			} else {
				motY -= 0.08D;
			}

			motY *= 0.9800000190734863D;
			motX *= f2;
			motZ *= f2;
		}

		aE = aF;
		d0 = locX - lastX;
		double d1 = locZ - lastZ;
		float f6 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

		if (f6 > 1.0F) {
			f6 = 1.0F;
		}

		aF += (f6 - aF) * 0.4F;
		aG += aF;
	}

	protected boolean bk() {
		return false;
	}

	public float bl() {
		return bk() ? bp : 0.1F;
	}

	public void i(float f) {
		bp = f;
	}

	public boolean n(Entity entity) {
		l(entity);
		return false;
	}

	public boolean isSleeping() {
		return false;
	}

	@Override
	public void h() {
		SpigotTimings.timerEntityBaseTick.startTiming(); // Spigot
		super.h();
		if (!world.isStatic) {
			int i = aZ();

			if (i > 0) {
				if (av <= 0) {
					av = 20 * (30 - i);
				}

				--av;
				if (av <= 0) {
					p(i - 1);
				}
			}

			for (int j = 0; j < 5; ++j) {
				ItemStack itemstack = g[j];
				ItemStack itemstack1 = this.getEquipment(j);

				if (!ItemStack.matches(itemstack1, itemstack)) {
					((WorldServer) world).getTracker().a(this, new PacketPlayOutEntityEquipment(getId(), j, itemstack1));
					if (itemstack != null) {
						d.a(itemstack.D());
					}

					if (itemstack1 != null) {
						d.b(itemstack1.D());
					}

					g[j] = itemstack1 == null ? null : itemstack1.cloneItemStack();
				}
			}

			if (ticksLived % 20 == 0) {
				aW().g();
			}
		}

		SpigotTimings.timerEntityBaseTick.stopTiming(); // Spigot
		this.e();
		SpigotTimings.timerEntityTickRest.startTiming(); // Spigot
		double d0 = locX - lastX;
		double d1 = locZ - lastZ;
		float f = (float) (d0 * d0 + d1 * d1);
		float f1 = aM;
		float f2 = 0.0F;

		aV = aW;
		float f3 = 0.0F;

		if (f > 0.0025000002F) {
			f3 = 1.0F;
			f2 = (float) Math.sqrt(f) * 3.0F;
			// CraftBukkit - Math -> TrigMath
			f1 = (float) org.bukkit.craftbukkit.TrigMath.atan2(d1, d0) * 180.0F / 3.1415927F - 90.0F;
		}

		if (aD > 0.0F) {
			f1 = yaw;
		}

		if (!onGround) {
			f3 = 0.0F;
		}

		aW += (f3 - aW) * 0.3F;
		world.methodProfiler.a("headTurn");
		f2 = this.f(f1, f2);
		world.methodProfiler.b();
		world.methodProfiler.a("rangeChecks");

		while (yaw - lastYaw < -180.0F) {
			lastYaw -= 360.0F;
		}

		while (yaw - lastYaw >= 180.0F) {
			lastYaw += 360.0F;
		}

		while (aM - aN < -180.0F) {
			aN -= 360.0F;
		}

		while (aM - aN >= 180.0F) {
			aN += 360.0F;
		}

		while (pitch - lastPitch < -180.0F) {
			lastPitch -= 360.0F;
		}

		while (pitch - lastPitch >= 180.0F) {
			lastPitch += 360.0F;
		}

		while (aO - aP < -180.0F) {
			aP -= 360.0F;
		}

		while (aO - aP >= 180.0F) {
			aP += 360.0F;
		}

		world.methodProfiler.b();
		aX += f2;
		SpigotTimings.timerEntityTickRest.stopTiming(); // Spigot
	}

	protected float f(float f, float f1) {
		float f2 = MathHelper.g(f - aM);

		aM += f2 * 0.3F;
		float f3 = MathHelper.g(yaw - aM);
		boolean flag = f3 < -90.0F || f3 >= 90.0F;

		if (f3 < -75.0F) {
			f3 = -75.0F;
		}

		if (f3 >= 75.0F) {
			f3 = 75.0F;
		}

		aM = yaw - f3;
		if (f3 * f3 > 2500.0F) {
			aM += f3 * 0.2F;
		}

		if (flag) {
			f1 *= -1.0F;
		}

		return f1;
	}

	public void e() {
		if (bq > 0) {
			--bq;
		}

		if (bg > 0) {
			double d0 = locX + (bh - locX) / bg;
			double d1 = locY + (bi - locY) / bg;
			double d2 = locZ + (bj - locZ) / bg;
			double d3 = MathHelper.g(bk - yaw);

			yaw = (float) (yaw + d3 / bg);
			pitch = (float) (pitch + (bl - pitch) / bg);
			--bg;
			setPosition(d0, d1, d2);
			this.b(yaw, pitch);
		} else if (!br()) {
			motX *= 0.98D;
			motY *= 0.98D;
			motZ *= 0.98D;
		}

		if (Math.abs(motX) < 0.005D) {
			motX = 0.0D;
		}

		if (Math.abs(motY) < 0.005D) {
			motY = 0.0D;
		}

		if (Math.abs(motZ) < 0.005D) {
			motZ = 0.0D;
		}

		world.methodProfiler.a("ai");
		SpigotTimings.timerEntityAI.startTiming(); // Spigot
		if (bh()) {
			bc = false;
			bd = 0.0F;
			be = 0.0F;
			bf = 0.0F;
		} else if (br()) {
			if (bk()) {
				world.methodProfiler.a("newAi");
				bn();
				world.methodProfiler.b();
			} else {
				world.methodProfiler.a("oldAi");
				bq();
				world.methodProfiler.b();
				aO = yaw;
			}
		}
		SpigotTimings.timerEntityAI.stopTiming(); // Spigot

		world.methodProfiler.b();
		world.methodProfiler.a("jump");
		if (bc) {
			if (!M() && !P()) {
				if (onGround && bq == 0) {
					bj();
					bq = 10;
				}
			} else {
				motY += 0.03999999910593033D;
			}
		} else {
			bq = 0;
		}

		world.methodProfiler.b();
		world.methodProfiler.a("travel");
		bd *= 0.98F;
		be *= 0.98F;
		bf *= 0.9F;
		SpigotTimings.timerEntityAIMove.startTiming(); // Spigot
		this.e(bd, be);
		SpigotTimings.timerEntityAIMove.stopTiming(); // Spigot
		world.methodProfiler.b();
		world.methodProfiler.a("push");
		if (!world.isStatic) {
			SpigotTimings.timerEntityAICollision.startTiming(); // Spigot
			bo();
			SpigotTimings.timerEntityAICollision.stopTiming(); // Spigot
		}

		world.methodProfiler.b();
	}

	protected void bn() {
	}

	protected void bo() {
		List list = world.getEntities(this, boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

		if (R() && list != null && !list.isEmpty()) { // Spigot: Add this.R() condition
			numCollisions -= world.spigotConfig.maxCollisionsPerEntity; // Spigot
			for (int i = 0; i < list.size(); ++i) {
				if (numCollisions > world.spigotConfig.maxCollisionsPerEntity) {
					break;
				} // Spigot
				Entity entity = (Entity) list.get(i);

				// TODO better check now?
				// CraftBukkit start - Only handle mob (non-player) collisions every other tick
				if (entity instanceof EntityLiving && !(this instanceof EntityPlayer) && ticksLived % 2 == 0) {
					continue;
				}
				// CraftBukkit end

				if (entity.S()) {
					entity.numCollisions++; // Spigot
					numCollisions++; // Spigot
					this.o(entity);
				}
			}
			numCollisions = 0; // Spigot
		}
	}

	protected void o(Entity entity) {
		entity.collide(this);
	}

	@Override
	public void ab() {
		super.ab();
		aV = aW;
		aW = 0.0F;
		fallDistance = 0.0F;
	}

	protected void bp() {
	}

	protected void bq() {
		++aU;
	}

	public void f(boolean flag) {
		bc = flag;
	}

	public void receive(Entity entity, int i) {
		if (!entity.dead && !world.isStatic) {
			EntityTracker entitytracker = ((WorldServer) world).getTracker();

			if (entity instanceof EntityItem) {
				entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), getId()));
			}

			if (entity instanceof EntityArrow) {
				entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), getId()));
			}

			if (entity instanceof EntityExperienceOrb) {
				entitytracker.a(entity, new PacketPlayOutCollect(entity.getId(), getId()));
			}
		}
	}

	public boolean hasLineOfSight(Entity entity) {
		return world.a(Vec3D.a(locX, locY + getHeadHeight(), locZ), Vec3D.a(entity.locX, entity.locY + entity.getHeadHeight(), entity.locZ)) == null;
	}

	@Override
	public Vec3D ag() {
		return this.j(1.0F);
	}

	public Vec3D j(float f) {
		float f1;
		float f2;
		float f3;
		float f4;

		if (f == 1.0F) {
			f1 = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
			f2 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
			f3 = -MathHelper.cos(-pitch * 0.017453292F);
			f4 = MathHelper.sin(-pitch * 0.017453292F);
			return Vec3D.a(f2 * f3, f4, f1 * f3);
		} else {
			f1 = lastPitch + (pitch - lastPitch) * f;
			f2 = lastYaw + (yaw - lastYaw) * f;
			f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
			f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
			float f5 = -MathHelper.cos(-f1 * 0.017453292F);
			float f6 = MathHelper.sin(-f1 * 0.017453292F);

			return Vec3D.a(f4 * f5, f6, f3 * f5);
		}
	}

	public boolean br() {
		return !world.isStatic;
	}

	@Override
	public boolean R() {
		return !dead;
	}

	@Override
	public boolean S() {
		return !dead;
	}

	@Override
	public float getHeadHeight() {
		return length * 0.85F;
	}

	@Override
	protected void Q() {
		velocityChanged = random.nextDouble() >= getAttributeInstance(GenericAttributes.c).getValue();
	}

	@Override
	public float getHeadRotation() {
		return aO;
	}

	public float getAbsorptionHearts() {
		return br;
	}

	public void setAbsorptionHearts(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		br = f;
	}

	public ScoreboardTeamBase getScoreboardTeam() {
		return null;
	}

	public boolean c(EntityLiving entityliving) {
		return this.a(entityliving.getScoreboardTeam());
	}

	public boolean a(ScoreboardTeamBase scoreboardteambase) {
		return getScoreboardTeam() != null ? getScoreboardTeam().isAlly(scoreboardteambase) : false;
	}

	public void bu() {
	}

	public void bv() {
	}
}
