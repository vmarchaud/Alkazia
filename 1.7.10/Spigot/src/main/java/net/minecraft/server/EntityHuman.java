package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.mojang.authlib.GameProfile;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
// CraftBukkit end
import org.spigotmc.ProtocolData; // Spigot - protocol patch

public abstract class EntityHuman extends EntityLiving implements ICommandListener {

	public PlayerInventory inventory = new PlayerInventory(this);
	private InventoryEnderChest enderChest = new InventoryEnderChest();
	public Container defaultContainer;
	public Container activeContainer;
	protected FoodMetaData foodData = new FoodMetaData(this); // CraftBukkit - add "this" to constructor
	protected int bq;
	public float br;
	public float bs;
	public int bt;
	public double bu;
	public double bv;
	public double bw;
	public double bx;
	public double by;
	public double bz;
	// CraftBukkit start
	public boolean sleeping; // protected -> public
	public boolean fauxSleeping;
	public String spawnWorld = "";
	public boolean affectsSpawning = true; // PaperSpigot

	@Override
	public CraftHumanEntity getBukkitEntity() {
		return (CraftHumanEntity) super.getBukkitEntity();
	}

	// CraftBukkit end

	public ChunkCoordinates bB;
	public int sleepTicks; // CraftBukkit - private -> public
	public float bC;
	public float bD;
	private ChunkCoordinates c;
	private boolean d;
	private ChunkCoordinates e;
	public PlayerAbilities abilities = new PlayerAbilities();
	public int oldLevel = -1; // CraftBukkit - add field
	public int expLevel;
	public int expTotal;
	public float exp;
	private ItemStack f;
	private int g;
	protected float bI = 0.1F;
	protected float bJ = 0.02F;
	private int h;
	private final GameProfile i;
	public EntityFishingHook hookedFish;

	public EntityHuman(World world, GameProfile gameprofile) {
		super(world);
		uniqueID = a(gameprofile);
		i = gameprofile;
		defaultContainer = new ContainerPlayer(inventory, !world.isStatic, this);
		activeContainer = defaultContainer;
		height = 1.62F;
		ChunkCoordinates chunkcoordinates = world.getSpawn();

		setPositionRotation(chunkcoordinates.x + 0.5D, chunkcoordinates.y + 1, chunkcoordinates.z + 0.5D, 0.0F, 0.0F);
		aZ = 180.0F;
		maxFireTicks = 20;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeMap().b(GenericAttributes.e).setValue(1.0D);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, new ProtocolData.DualByte((byte) 0, (byte) 0)); // Spigot - protocol patch, handle metadata usage change (show cape -> collisions)
		datawatcher.a(17, Float.valueOf(0.0F));
		datawatcher.a(18, Integer.valueOf(0));
		datawatcher.a(10, new ProtocolData.HiddenByte((byte) 0)); // Spigot - protocol patch, handle new metadata value
	}

	public boolean by() {
		return f != null;
	}

	public void bA() {
		if (f != null) {
			f.b(world, this, g);
		}

		bB();
	}

	public void bB() {
		f = null;
		g = 0;
		if (!world.isStatic) {
			this.e(false);
		}
	}

	public boolean isBlocking() {
		return by() && f.getItem().d(f) == EnumAnimation.BLOCK;
	}

	@Override
	public void h() {
		if (f != null) {
			ItemStack itemstack = inventory.getItemInHand();

			if (itemstack == f) {
				if (g <= 25 && g % 4 == 0) {
					this.c(itemstack, 5);
				}

				if (--g == 0 && !world.isStatic) {
					this.p();
				}
			} else {
				bB();
			}
		}

		if (bt > 0) {
			--bt;
		}

		if (isSleeping()) {
			++sleepTicks;
			if (sleepTicks > 100) {
				sleepTicks = 100;
			}

			if (!world.isStatic) {
				if (!this.j()) {
					this.a(true, true, false);
				} else if (world.w()) {
					this.a(false, true, true);
				}
			}
		} else if (sleepTicks > 0) {
			++sleepTicks;
			if (sleepTicks >= 110) {
				sleepTicks = 0;
			}
		}

		super.h();
		if (!world.isStatic && activeContainer != null && !activeContainer.a(this)) {
			closeInventory();
			activeContainer = defaultContainer;
		}

		if (isBurning() && abilities.isInvulnerable) {
			extinguish();
		}

		bu = bx;
		bv = by;
		bw = bz;
		double d0 = locX - bx;
		double d1 = locY - by;
		double d2 = locZ - bz;
		double d3 = 10.0D;

		if (d0 > d3) {
			bu = bx = locX;
		}

		if (d2 > d3) {
			bw = bz = locZ;
		}

		if (d1 > d3) {
			bv = by = locY;
		}

		if (d0 < -d3) {
			bu = bx = locX;
		}

		if (d2 < -d3) {
			bw = bz = locZ;
		}

		if (d1 < -d3) {
			bv = by = locY;
		}

		bx += d0 * 0.25D;
		bz += d2 * 0.25D;
		by += d1 * 0.25D;
		if (vehicle == null) {
			e = null;
		}

		if (!world.isStatic) {
			foodData.a(this);
			this.a(StatisticList.g, 1);
		}
	}

	@Override
	public int D() {
		return abilities.isInvulnerable ? 0 : 80;
	}

	@Override
	protected String H() {
		return "game.player.swim";
	}

	@Override
	protected String O() {
		return "game.player.swim.splash";
	}

	@Override
	public int ai() {
		return 10;
	}

	@Override
	public void makeSound(String s, float f, float f1) {
		world.a(this, s, f, f1);
	}

	protected void c(ItemStack itemstack, int i) {
		if (itemstack.o() == EnumAnimation.DRINK) {
			makeSound("random.drink", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (itemstack.o() == EnumAnimation.EAT) {
			for (int j = 0; j < i; ++j) {
				Vec3D vec3d = Vec3D.a((random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

				vec3d.a(-pitch * 3.1415927F / 180.0F);
				vec3d.b(-yaw * 3.1415927F / 180.0F);
				Vec3D vec3d1 = Vec3D.a((random.nextFloat() - 0.5D) * 0.3D, -random.nextFloat() * 0.6D - 0.3D, 0.6D);

				vec3d1.a(-pitch * 3.1415927F / 180.0F);
				vec3d1.b(-yaw * 3.1415927F / 180.0F);
				vec3d1 = vec3d1.add(locX, locY + getHeadHeight(), locZ);
				String s = "iconcrack_" + Item.getId(itemstack.getItem());

				if (itemstack.usesData()) {
					s = s + "_" + itemstack.getData();
				}

				world.addParticle(s, vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05D, vec3d.c);
			}

			makeSound("random.eat", 0.5F + 0.5F * random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
		}
	}

	protected void p() {
		if (f != null) {
			this.c(f, 16);
			int i = f.count;

			// CraftBukkit start - fire PlayerItemConsumeEvent
			org.bukkit.inventory.ItemStack craftItem = CraftItemStack.asBukkitCopy(f);
			PlayerItemConsumeEvent event = new PlayerItemConsumeEvent((Player) getBukkitEntity(), craftItem);
			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				// Update client
				if (this instanceof EntityPlayer) {
					((EntityPlayer) this).playerConnection.sendPacket(new PacketPlayOutSetSlot((byte) 0, activeContainer.getSlot(inventory, inventory.itemInHandIndex).index, f));
					// Spigot Start
					((EntityPlayer) this).getBukkitEntity().updateInventory();
					((EntityPlayer) this).getBukkitEntity().updateScaledHealth();
					// Spigot End
				}
				return;
			}

			// Plugin modified the item, process it but don't remove it
			if (!craftItem.equals(event.getItem())) {
				CraftItemStack.asNMSCopy(event.getItem()).b(world, this);

				// Update client
				if (this instanceof EntityPlayer) {
					((EntityPlayer) this).playerConnection.sendPacket(new PacketPlayOutSetSlot((byte) 0, activeContainer.getSlot(inventory, inventory.itemInHandIndex).index, f));
				}
				return;
			}
			// CraftBukkit end

			ItemStack itemstack = f.b(world, this);

			if (itemstack != f || itemstack != null && itemstack.count != i) {
				inventory.items[inventory.itemInHandIndex] = itemstack;
				if (itemstack.count == 0) {
					inventory.items[inventory.itemInHandIndex] = null;
				}
			}

			bB();
		}
	}

	@Override
	protected boolean bh() {
		return getHealth() <= 0.0F || isSleeping();
	}

	// CraftBukkit - protected -> public
	public void closeInventory() {
		activeContainer = defaultContainer;
	}

	@Override
	public void mount(Entity entity) {
		// CraftBukkit start - mirror Entity mount changes
		setPassengerOf(entity);
	}

	@Override
	public void setPassengerOf(Entity entity) {
		// CraftBukkit end
		if (vehicle != null && entity == null) {
			world.getServer().getPluginManager().callEvent(new org.spigotmc.event.entity.EntityDismountEvent(getBukkitEntity(), vehicle.getBukkitEntity())); // Spigot
			// CraftBukkit start - use parent method instead to correctly fire VehicleExitEvent
			Entity originalVehicle = vehicle;
			// First statement moved down, second statement handled in parent method.
			/*
			if (!this.world.isStatic) {
			    this.m(this.vehicle);
			}

			if (this.vehicle != null) {
			    this.vehicle.passenger = null;
			}

			this.vehicle = null;
			*/
			super.setPassengerOf(entity);
			if (!world.isStatic && vehicle == null) {
				m(originalVehicle);
			}
			// CraftBukkit end
		} else {
			super.setPassengerOf(entity); // CraftBukkit - call new parent
		}
	}

	@Override
	public void ab() {
		if (!world.isStatic && isSneaking()) {
			mount((Entity) null);
			setSneaking(false);
		} else {
			double d0 = locX;
			double d1 = locY;
			double d2 = locZ;
			float f = yaw;
			float f1 = pitch;

			super.ab();
			br = bs;
			bs = 0.0F;
			this.l(locX - d0, locY - d1, locZ - d2);
			if (vehicle instanceof EntityPig) {
				pitch = f1;
				yaw = f;
				aM = ((EntityPig) vehicle).aM;
			}
		}
	}

	@Override
	protected void bq() {
		super.bq();
		bb();
	}

	@Override
	public void e() {
		if (bq > 0) {
			--bq;
		}

		if (world.difficulty == EnumDifficulty.PEACEFUL && getHealth() < getMaxHealth() && world.getGameRules().getBoolean("naturalRegeneration") && ticksLived % 20 * 12 == 0) {
			// CraftBukkit - added regain reason of "REGEN" for filtering purposes.
			this.heal(1.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.REGEN);
		}

		inventory.k();
		br = bs;
		super.e();
		AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

		if (!world.isStatic) {
			attributeinstance.setValue(abilities.b());
		}

		aQ = bJ;
		if (isSprinting()) {
			aQ = (float) (aQ + bJ * 0.3D);
		}

		this.i((float) attributeinstance.getValue());
		float f = MathHelper.sqrt(motX * motX + motZ * motZ);
		// CraftBukkit - Math -> TrigMath
		float f1 = (float) org.bukkit.craftbukkit.TrigMath.atan(-motY * 0.20000000298023224D) * 15.0F;

		if (f > 0.1F) {
			f = 0.1F;
		}

		if (!onGround || getHealth() <= 0.0F) {
			f = 0.0F;
		}

		if (onGround || getHealth() <= 0.0F) {
			f1 = 0.0F;
		}

		bs += (f - bs) * 0.4F;
		aJ += (f1 - aJ) * 0.8F;
		if (getHealth() > 0.0F) {
			AxisAlignedBB axisalignedbb = null;

			if (vehicle != null && !vehicle.dead) {
				axisalignedbb = boundingBox.a(vehicle.boundingBox).grow(1.0D, 0.0D, 1.0D);
			} else {
				axisalignedbb = boundingBox.grow(1.0D, 0.5D, 1.0D);
			}

			List list = world.getEntities(this, axisalignedbb);

			if (list != null && S()) { // Spigot: Add this.S() condition (second !this.isDead near bottom of EntityLiving)
				for (int i = 0; i < list.size(); ++i) {
					Entity entity = (Entity) list.get(i);

					if (!entity.dead) {
						this.d(entity);
					}
				}
			}
		}
	}

	private void d(Entity entity) {
		entity.b_(this);
	}

	public int getScore() {
		return datawatcher.getInt(18);
	}

	public void setScore(int i) {
		datawatcher.watch(18, Integer.valueOf(i));
	}

	public void addScore(int i) {
		int j = getScore();

		datawatcher.watch(18, Integer.valueOf(j + i));
	}

	@Override
	public void die(DamageSource damagesource) {
		super.die(damagesource);
		this.a(0.2F, 0.2F);
		setPosition(locX, locY, locZ);
		motY = 0.10000000149011612D;
		if (getName().equals("Notch")) {
			this.a(new ItemStack(Items.APPLE, 1), true, false);
		}

		if (!world.getGameRules().getBoolean("keepInventory")) {
			inventory.m();
		}

		if (damagesource != null) {
			motX = -MathHelper.cos((az + yaw) * 3.1415927F / 180.0F) * 0.1F;
			motZ = -MathHelper.sin((az + yaw) * 3.1415927F / 180.0F) * 0.1F;
		} else {
			motX = motZ = 0.0D;
		}

		height = 0.1F;
		this.a(StatisticList.v, 1);
	}

	@Override
	protected String aT() {
		return "game.player.hurt";
	}

	@Override
	protected String aU() {
		return "game.player.die";
	}

	@Override
	public void b(Entity entity, int i) {
		addScore(i);
		// CraftBukkit - Get our scores instead
		Collection<ScoreboardScore> collection = world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.e, getName(), new java.util.ArrayList<ScoreboardScore>());

		if (entity instanceof EntityHuman) {
			this.a(StatisticList.y, 1);
			// CraftBukkit - Get our scores instead
			world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.d, getName(), collection);
		} else {
			this.a(StatisticList.w, 1);
		}

		Iterator iterator = collection.iterator();

		while (iterator.hasNext()) {
			ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next(); // CraftBukkit - Use our scores instead

			scoreboardscore.incrementScore();
		}
	}

	public EntityItem a(boolean flag) {
		// Called only when dropped by Q or CTRL-Q
		return this.a(inventory.splitStack(inventory.itemInHandIndex, flag && inventory.getItemInHand() != null ? inventory.getItemInHand().count : 1), false, true);
	}

	public EntityItem drop(ItemStack itemstack, boolean flag) {
		return this.a(itemstack, false, false);
	}

	public EntityItem a(ItemStack itemstack, boolean flag, boolean flag1) {
		if (itemstack == null)
			return null;
		else if (itemstack.count == 0)
			return null;
		else {
			EntityItem entityitem = new EntityItem(world, locX, locY - 0.30000001192092896D + getHeadHeight(), locZ, itemstack);

			entityitem.pickupDelay = 40;
			if (flag1) {
				entityitem.b(getName());
			}

			float f = 0.1F;
			float f1;

			if (flag) {
				f1 = random.nextFloat() * 0.5F;
				float f2 = random.nextFloat() * 3.1415927F * 2.0F;

				entityitem.motX = -MathHelper.sin(f2) * f1;
				entityitem.motZ = MathHelper.cos(f2) * f1;
				entityitem.motY = 0.20000000298023224D;
			} else {
				f = 0.3F;
				entityitem.motX = -MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
				entityitem.motZ = MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
				entityitem.motY = -MathHelper.sin(pitch / 180.0F * 3.1415927F) * f + 0.1F;
				f = 0.02F;
				f1 = random.nextFloat() * 3.1415927F * 2.0F;
				f *= random.nextFloat();
				entityitem.motX += Math.cos(f1) * f;
				entityitem.motY += (random.nextFloat() - random.nextFloat()) * 0.1F;
				entityitem.motZ += Math.sin(f1) * f;
			}

			// CraftBukkit start - fire PlayerDropItemEvent
			Player player = (Player) getBukkitEntity();
			CraftItem drop = new CraftItem(world.getServer(), entityitem);

			PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
				if (flag1 && (cur == null || cur.getAmount() == 0)) {
					// The complete stack was dropped
					player.getInventory().setItemInHand(drop.getItemStack());
				} else if (flag1 && cur.isSimilar(drop.getItemStack()) && drop.getItemStack().getAmount() == 1) {
					// Only one item is dropped
					cur.setAmount(cur.getAmount() + 1);
					player.getInventory().setItemInHand(cur);
				} else {
					// Fallback
					player.getInventory().addItem(drop.getItemStack());
				}
				return null;
			}
			// CraftBukkit end

			this.a(entityitem);
			this.a(StatisticList.s, 1);
			return entityitem;
		}
	}

	protected void a(EntityItem entityitem) {
		world.addEntity(entityitem);
	}

	public float a(Block block, boolean flag) {
		float f = inventory.a(block);

		if (f > 1.0F) {
			int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this);
			ItemStack itemstack = inventory.getItemInHand();

			if (i > 0 && itemstack != null) {
				float f1 = i * i + 1;

				if (!itemstack.b(block) && f <= 1.0F) {
					f += f1 * 0.08F;
				} else {
					f += f1;
				}
			}
		}

		if (this.hasEffect(MobEffectList.FASTER_DIG)) {
			f *= 1.0F + (getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1) * 0.2F;
		}

		if (this.hasEffect(MobEffectList.SLOWER_DIG)) {
			f *= 1.0F - (getEffect(MobEffectList.SLOWER_DIG).getAmplifier() + 1) * 0.2F;
		}

		if (this.a(Material.WATER) && !EnchantmentManager.hasWaterWorkerEnchantment(this)) {
			f /= 5.0F;
		}

		if (!onGround) {
			f /= 5.0F;
		}

		return f;
	}

	public boolean a(Block block) {
		return inventory.b(block);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		uniqueID = a(i);
		NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

		inventory.b(nbttaglist);
		inventory.itemInHandIndex = nbttagcompound.getInt("SelectedItemSlot");
		sleeping = nbttagcompound.getBoolean("Sleeping");
		sleepTicks = nbttagcompound.getShort("SleepTimer");
		exp = nbttagcompound.getFloat("XpP");
		expLevel = nbttagcompound.getInt("XpLevel");
		expTotal = nbttagcompound.getInt("XpTotal");
		setScore(nbttagcompound.getInt("Score"));
		if (sleeping) {
			bB = new ChunkCoordinates(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ));
			this.a(true, true, false);
		}

		// CraftBukkit start
		spawnWorld = nbttagcompound.getString("SpawnWorld");
		if ("".equals(spawnWorld)) {
			spawnWorld = world.getServer().getWorlds().get(0).getName();
		}
		// CraftBukkit end

		if (nbttagcompound.hasKeyOfType("SpawnX", 99) && nbttagcompound.hasKeyOfType("SpawnY", 99) && nbttagcompound.hasKeyOfType("SpawnZ", 99)) {
			c = new ChunkCoordinates(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
			d = nbttagcompound.getBoolean("SpawnForced");
		}

		foodData.a(nbttagcompound);
		abilities.b(nbttagcompound);
		if (nbttagcompound.hasKeyOfType("EnderItems", 9)) {
			NBTTagList nbttaglist1 = nbttagcompound.getList("EnderItems", 10);

			enderChest.a(nbttaglist1);
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.set("Inventory", inventory.a(new NBTTagList()));
		nbttagcompound.setInt("SelectedItemSlot", inventory.itemInHandIndex);
		nbttagcompound.setBoolean("Sleeping", sleeping);
		nbttagcompound.setShort("SleepTimer", (short) sleepTicks);
		nbttagcompound.setFloat("XpP", exp);
		nbttagcompound.setInt("XpLevel", expLevel);
		nbttagcompound.setInt("XpTotal", expTotal);
		nbttagcompound.setInt("Score", getScore());
		if (c != null) {
			nbttagcompound.setInt("SpawnX", c.x);
			nbttagcompound.setInt("SpawnY", c.y);
			nbttagcompound.setInt("SpawnZ", c.z);
			nbttagcompound.setBoolean("SpawnForced", d);
			nbttagcompound.setString("SpawnWorld", spawnWorld); // CraftBukkit - fixes bed spawns for multiworld worlds
		}

		foodData.b(nbttagcompound);
		abilities.a(nbttagcompound);
		nbttagcompound.set("EnderItems", enderChest.h());
	}

	public void openContainer(IInventory iinventory) {
	}

	public void openHopper(TileEntityHopper tileentityhopper) {
	}

	public void openMinecartHopper(EntityMinecartHopper entityminecarthopper) {
	}

	public void openHorseInventory(EntityHorse entityhorse, IInventory iinventory) {
	}

	public void startEnchanting(int i, int j, int k, String s) {
	}

	public void openAnvil(int i, int j, int k) {
	}

	public void startCrafting(int i, int j, int k) {
	}

	@Override
	public float getHeadHeight() {
		return 0.12F;
	}

	protected void e_() {
		height = 1.62F;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else if (abilities.isInvulnerable && !damagesource.ignoresInvulnerability())
			return false;
		else {
			aU = 0;
			if (getHealth() <= 0.0F)
				return false;
			else {
				if (isSleeping() && !world.isStatic) {
					this.a(true, true, false);
				}

				if (damagesource.r()) {
					if (world.difficulty == EnumDifficulty.PEACEFUL)
						return false; // CraftBukkit - f = 0.0f -> return false

					if (world.difficulty == EnumDifficulty.EASY) {
						f = f / 2.0F + 1.0F;
					}

					if (world.difficulty == EnumDifficulty.HARD) {
						f = f * 3.0F / 2.0F;
					}
				}

				if (false && f == 0.0F)
					return false;
				else {
					Entity entity = damagesource.getEntity();

					if (entity instanceof EntityArrow && ((EntityArrow) entity).shooter != null) {
						entity = ((EntityArrow) entity).shooter;
					}

					this.a(StatisticList.u, Math.round(f * 10.0F));
					return super.damageEntity(damagesource, f);
				}
			}
		}
	}

	public boolean a(EntityHuman entityhuman) {
		// CraftBukkit start - Change to check OTHER player's scoreboard team according to API
		// To summarize this method's logic, it's "Can parameter hurt this"
		org.bukkit.scoreboard.Team team;
		if (entityhuman instanceof EntityPlayer) {
			EntityPlayer thatPlayer = (EntityPlayer) entityhuman;
			team = thatPlayer.getBukkitEntity().getScoreboard().getPlayerTeam(thatPlayer.getBukkitEntity());
			if (team == null || team.allowFriendlyFire())
				return true;
		} else {
			// This should never be called, but is implemented anyway
			org.bukkit.OfflinePlayer thisPlayer = entityhuman.world.getServer().getOfflinePlayer(entityhuman.getName());
			team = entityhuman.world.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(thisPlayer);
			if (team == null || team.allowFriendlyFire())
				return true;
		}

		if (this instanceof EntityPlayer)
			return !team.hasPlayer(((EntityPlayer) this).getBukkitEntity());
		return !team.hasPlayer(world.getServer().getOfflinePlayer(getName()));
		// CraftBukkit end
	}

	@Override
	protected void damageArmor(float f) {
		inventory.a(f);
	}

	@Override
	public int aV() {
		return inventory.l();
	}

	public float bE() {
		int i = 0;
		ItemStack[] aitemstack = inventory.armor;
		int j = aitemstack.length;

		for (int k = 0; k < j; ++k) {
			ItemStack itemstack = aitemstack[k];

			if (itemstack != null) {
				++i;
			}
		}

		return (float) i / (float) inventory.armor.length;
	}

	// CraftBukkit start
	@Override
	protected boolean d(DamageSource damagesource, float f) { // void -> boolean
		if (true)
			return super.d(damagesource, f);
		// CraftBukkit end
		if (!isInvulnerable()) {
			if (!damagesource.ignoresArmor() && isBlocking() && f > 0.0F) {
				f = (1.0F + f) * world.paperSpigotConfig.playerBlockingDamageMultiplier; // PaperSpigot - Configurable damage multiplier for blocking
			}

			f = applyArmorModifier(damagesource, f);
			f = applyMagicModifier(damagesource, f);
			float f1 = f;

			f = Math.max(f - getAbsorptionHearts(), 0.0F);
			setAbsorptionHearts(getAbsorptionHearts() - (f1 - f));
			if (f != 0.0F) {
				applyExhaustion(damagesource.getExhaustionCost());
				float f2 = getHealth();

				setHealth(getHealth() - f);
				aW().a(damagesource, f2, f);
			}
		}
		return false; // CraftBukkit
	}

	public void openFurnace(TileEntityFurnace tileentityfurnace) {
	}

	public void openDispenser(TileEntityDispenser tileentitydispenser) {
	}

	public void a(TileEntity tileentity) {
	}

	public void a(CommandBlockListenerAbstract commandblocklistenerabstract) {
	}

	public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {
	}

	public void openBeacon(TileEntityBeacon tileentitybeacon) {
	}

	public void openTrade(IMerchant imerchant, String s) {
	}

	public void b(ItemStack itemstack) {
	}

	public boolean q(Entity entity) {
		ItemStack itemstack = bF();
		ItemStack itemstack1 = itemstack != null ? itemstack.cloneItemStack() : null;

		if (!entity.c(this)) {
			if (itemstack != null && entity instanceof EntityLiving) {
				if (abilities.canInstantlyBuild) {
					itemstack = itemstack1;
				}

				if (itemstack.a(this, (EntityLiving) entity)) {
					// CraftBukkit - bypass infinite items; <= 0 -> == 0
					if (itemstack.count == 0 && !abilities.canInstantlyBuild) {
						bG();
					}

					return true;
				}
			}

			return false;
		} else {
			if (itemstack != null && itemstack == bF()) {
				if (itemstack.count <= 0 && !abilities.canInstantlyBuild) {
					bG();
				} else if (itemstack.count < itemstack1.count && abilities.canInstantlyBuild) {
					itemstack.count = itemstack1.count;
				}
			}

			return true;
		}
	}

	public ItemStack bF() {
		return inventory.getItemInHand();
	}

	public void bG() {
		inventory.setItem(inventory.itemInHandIndex, (ItemStack) null);
	}

	@Override
	public double ad() {
		return height - 0.5F;
	}

	public void attack(Entity entity) {
		if (entity.av()) {
			if (!entity.j(this)) {
				float f = (float) getAttributeInstance(GenericAttributes.e).getValue();
				int i = 0;
				float f1 = 0.0F;

				if (entity instanceof EntityLiving) {
					f1 = EnchantmentManager.a((EntityLiving) this, (EntityLiving) entity);
					i += EnchantmentManager.getKnockbackEnchantmentLevel(this, (EntityLiving) entity);
				}

				if (isSprinting()) {
					++i;
				}

				if (f > 0.0F || f1 > 0.0F) {
					boolean flag = fallDistance > 0.0F && !onGround && !h_() && !M() && !this.hasEffect(MobEffectList.BLINDNESS) && vehicle == null && entity instanceof EntityLiving;

					if (flag && f > 0.0F) {
						f *= 1.5F;
					}

					f += f1;
					boolean flag1 = false;
					int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

					if (entity instanceof EntityLiving && j > 0 && !entity.isBurning()) {
						// CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
						EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), 1);
						org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

						if (!combustEvent.isCancelled()) {
							flag1 = true;
							entity.setOnFire(combustEvent.getDuration());
						}
						// CraftBukkit end
					}

					boolean flag2 = entity.damageEntity(DamageSource.playerAttack(this), f);

					if (flag2) {
						if (i > 0) {
							entity.g(-MathHelper.sin(yaw * 3.1415927F / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(yaw * 3.1415927F / 180.0F) * i * 0.5F);
							motX *= 0.6D;
							motZ *= 0.6D;
							setSprinting(false);
						}

						if (flag) {
							this.b(entity);
						}

						if (f1 > 0.0F) {
							this.c(entity);
						}

						if (f >= 18.0F) {
							this.a(AchievementList.F);
						}

						this.l(entity);
						if (entity instanceof EntityLiving) {
							EnchantmentManager.a((EntityLiving) entity, (Entity) this);
						}

						EnchantmentManager.b(this, entity);
						ItemStack itemstack = bF();
						Object object = entity;

						if (entity instanceof EntityComplexPart) {
							IComplex icomplex = ((EntityComplexPart) entity).owner;

							if (icomplex != null && icomplex instanceof EntityLiving) {
								object = icomplex;
							}
						}

						if (itemstack != null && object instanceof EntityLiving) {
							itemstack.a((EntityLiving) object, this);
							// CraftBukkit - bypass infinite items; <= 0 -> == 0
							if (itemstack.count == 0) {
								bG();
							}
						}

						if (entity instanceof EntityLiving) {
							this.a(StatisticList.t, Math.round(f * 10.0F));
							if (j > 0) {
								// CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
								EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(getBukkitEntity(), entity.getBukkitEntity(), j * 4);
								org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

								if (!combustEvent.isCancelled()) {
									entity.setOnFire(combustEvent.getDuration());
								}
								// CraftBukkit end
							}
						}

						applyExhaustion(world.spigotConfig.combatExhaustion); // Spigot - Change to use configurable value
					} else if (flag1) {
						entity.extinguish();
					}
				}
			}
		}
	}

	public void b(Entity entity) {
	}

	public void c(Entity entity) {
	}

	@Override
	public void die() {
		super.die();
		defaultContainer.b(this);
		if (activeContainer != null) {
			activeContainer.b(this);
		}
	}

	@Override
	public boolean inBlock() {
		return !sleeping && super.inBlock();
	}

	public GameProfile getProfile() {
		return i;
	}

	public EnumBedResult a(int i, int j, int k) {
		if (!world.isStatic) {
			if (isSleeping() || !isAlive())
				return EnumBedResult.OTHER_PROBLEM;

			if (!world.worldProvider.d())
				return EnumBedResult.NOT_POSSIBLE_HERE;

			if (world.w())
				return EnumBedResult.NOT_POSSIBLE_NOW;

			if (Math.abs(locX - i) > 3.0D || Math.abs(locY - j) > 2.0D || Math.abs(locZ - k) > 3.0D)
				return EnumBedResult.TOO_FAR_AWAY;

			double d0 = 8.0D;
			double d1 = 5.0D;
			List list = world.a(EntityMonster.class, AxisAlignedBB.a(i - d0, j - d1, k - d0, i + d0, j + d1, k + d0));

			if (!list.isEmpty())
				return EnumBedResult.NOT_SAFE;
		}

		if (am()) {
			mount((Entity) null);
		}

		// CraftBukkit start - fire PlayerBedEnterEvent
		if (getBukkitEntity() instanceof Player) {
			Player player = (Player) getBukkitEntity();
			org.bukkit.block.Block bed = world.getWorld().getBlockAt(i, j, k);

			PlayerBedEnterEvent event = new PlayerBedEnterEvent(player, bed);
			world.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled())
				return EnumBedResult.OTHER_PROBLEM;
		}
		// CraftBukkit end

		this.a(0.2F, 0.2F);
		height = 0.2F;
		if (world.isLoaded(i, j, k)) {
			int l = world.getData(i, j, k);
			int i1 = BlockDirectional.l(l);
			float f = 0.5F;
			float f1 = 0.5F;

			switch (i1) {
			case 0:
				f1 = 0.9F;
				break;

			case 1:
				f = 0.1F;
				break;

			case 2:
				f1 = 0.1F;
				break;

			case 3:
				f = 0.9F;
			}

			w(i1);
			setPosition(i + f, j + 0.9375F, k + f1);
		} else {
			setPosition(i + 0.5F, j + 0.9375F, k + 0.5F);
		}

		sleeping = true;
		sleepTicks = 0;
		bB = new ChunkCoordinates(i, j, k);
		motX = motZ = motY = 0.0D;
		if (!world.isStatic) {
			world.everyoneSleeping();
		}

		return EnumBedResult.OK;
	}

	private void w(int i) {
		bC = 0.0F;
		bD = 0.0F;
		switch (i) {
		case 0:
			bD = -1.8F;
			break;

		case 1:
			bC = 1.8F;
			break;

		case 2:
			bD = 1.8F;
			break;

		case 3:
			bC = -1.8F;
		}
	}

	public void a(boolean flag, boolean flag1, boolean flag2) {
		this.a(0.6F, 1.8F);
		e_();
		ChunkCoordinates chunkcoordinates = bB;
		ChunkCoordinates chunkcoordinates1 = bB;

		if (chunkcoordinates != null && world.getType(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z) == Blocks.BED) {
			BlockBed.a(world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, false);
			chunkcoordinates1 = BlockBed.a(world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, 0);
			if (chunkcoordinates1 == null) {
				chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
			}

			setPosition(chunkcoordinates1.x + 0.5F, chunkcoordinates1.y + height + 0.1F, chunkcoordinates1.z + 0.5F);
		}

		sleeping = false;
		if (!world.isStatic && flag1) {
			world.everyoneSleeping();
		}

		// CraftBukkit start - fire PlayerBedLeaveEvent
		if (getBukkitEntity() instanceof Player) {
			Player player = (Player) getBukkitEntity();

			org.bukkit.block.Block bed;
			if (chunkcoordinates != null) {
				bed = world.getWorld().getBlockAt(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
			} else {
				bed = world.getWorld().getBlockAt(player.getLocation());
			}

			PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed);
			world.getServer().getPluginManager().callEvent(event);
		}
		// CraftBukkit end

		if (flag) {
			sleepTicks = 0;
		} else {
			sleepTicks = 100;
		}

		if (flag2) {
			setRespawnPosition(bB, false);
		}
	}

	private boolean j() {
		return world.getType(bB.x, bB.y, bB.z) == Blocks.BED;
	}

	public static ChunkCoordinates getBed(World world, ChunkCoordinates chunkcoordinates, boolean flag) {
		IChunkProvider ichunkprovider = world.L();

		ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z - 3 >> 4);
		ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z - 3 >> 4);
		ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z + 3 >> 4);
		ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z + 3 >> 4);
		if (world.getType(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z) == Blocks.BED) {
			ChunkCoordinates chunkcoordinates1 = BlockBed.a(world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, 0);

			return chunkcoordinates1;
		} else {
			Material material = world.getType(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z).getMaterial();
			Material material1 = world.getType(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z).getMaterial();
			boolean flag1 = !material.isBuildable() && !material.isLiquid();
			boolean flag2 = !material1.isBuildable() && !material1.isLiquid();

			return flag && flag1 && flag2 ? chunkcoordinates : null;
		}
	}

	@Override
	public boolean isSleeping() {
		return sleeping;
	}

	public boolean isDeeplySleeping() {
		return sleeping && sleepTicks >= 100;
	}

	// Spigot start - protocol patch, handle metadata usage change (show cape -> collisions)
	protected void b(int i, boolean flag, int version) {
		ProtocolData.DualByte db = datawatcher.getDualByte(16);
		byte b0 = version >= 16 ? db.value2 : db.value;
		if (flag) {
			b0 = (byte) (b0 | 1 << i);
		} else {
			b0 = (byte) (b0 & ~(1 << i));
		}
		if (version >= 16) {
			db.value2 = b0;
		} else {
			db.value = b0;
		}
		datawatcher.watch(16, db);
	}

	// Spigot end

	public void b(IChatBaseComponent ichatbasecomponent) {
	}

	public ChunkCoordinates getBed() {
		return c;
	}

	public boolean isRespawnForced() {
		return d;
	}

	public void setRespawnPosition(ChunkCoordinates chunkcoordinates, boolean flag) {
		if (chunkcoordinates != null) {
			c = new ChunkCoordinates(chunkcoordinates);
			d = flag;
			spawnWorld = world.worldData.getName(); // CraftBukkit
		} else {
			c = null;
			d = false;
			spawnWorld = ""; // CraftBukkit
		}
	}

	public void a(Statistic statistic) {
		this.a(statistic, 1);
	}

	public void a(Statistic statistic, int i) {
	}

	@Override
	public void bj() {
		super.bj();
		this.a(StatisticList.r, 1);
		if (isSprinting()) {
			applyExhaustion(world.spigotConfig.sprintExhaustion); // Spigot - Change to use configurable value
		} else {
			applyExhaustion(world.spigotConfig.walkExhaustion); // Spigot - Change to use configurable value
		}
	}

	@Override
	public void e(float f, float f1) {
		double d0 = locX;
		double d1 = locY;
		double d2 = locZ;

		if (abilities.isFlying && vehicle == null) {
			double d3 = motY;
			float f2 = aQ;

			aQ = abilities.a();
			super.e(f, f1);
			motY = d3 * 0.6D;
			aQ = f2;
		} else {
			super.e(f, f1);
		}

		checkMovement(locX - d0, locY - d1, locZ - d2);
	}

	@Override
	public float bl() {
		return (float) getAttributeInstance(GenericAttributes.d).getValue();
	}

	public void checkMovement(double d0, double d1, double d2) {
		if (vehicle == null) {
			int i;

			if (this.a(Material.WATER)) {
				i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
				if (i > 0) {
					this.a(StatisticList.m, i);
					applyExhaustion(world.paperSpigotConfig.playerSwimmingExhaustion * i * 0.01F); // PaperSpigot - Configurable swimming exhaustion
				}
			} else if (M()) {
				i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
				if (i > 0) {
					this.a(StatisticList.i, i);
					applyExhaustion(world.paperSpigotConfig.playerSwimmingExhaustion * i * 0.01F); // PaperSpigot - Configurable swimming (diving) exhaustion
				}
			} else if (h_()) {
				if (d1 > 0.0D) {
					this.a(StatisticList.k, (int) Math.round(d1 * 100.0D));
				}
			} else if (onGround) {
				i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
				if (i > 0) {
					this.a(StatisticList.h, i);
					if (isSprinting()) {
						applyExhaustion(0.099999994F * i * 0.01F);
					} else {
						applyExhaustion(0.01F * i * 0.01F);
					}
				}
			} else {
				i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
				if (i > 25) {
					this.a(StatisticList.l, i);
				}
			}
		}
	}

	private void l(double d0, double d1, double d2) {
		if (vehicle != null) {
			int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

			if (i > 0) {
				if (vehicle instanceof EntityMinecartAbstract) {
					this.a(StatisticList.n, i);
					if (e == null) {
						e = new ChunkCoordinates(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ));
					} else if (e.e(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)) >= 1000000.0D) {
						this.a(AchievementList.q, 1);
					}
				} else if (vehicle instanceof EntityBoat) {
					this.a(StatisticList.o, i);
				} else if (vehicle instanceof EntityPig) {
					this.a(StatisticList.p, i);
				} else if (vehicle instanceof EntityHorse) {
					this.a(StatisticList.q, i);
				}
			}
		}
	}

	@Override
	protected void b(float f) {
		if (!abilities.canFly) {
			if (f >= 2.0F) {
				this.a(StatisticList.j, (int) Math.round(f * 100.0D));
			}

			super.b(f);
		}
	}

	@Override
	protected String o(int i) {
		return i > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
	}

	@Override
	public void a(EntityLiving entityliving) {
		if (entityliving instanceof IMonster) {
			this.a(AchievementList.s);
		}

		int i = EntityTypes.a(entityliving);
		MonsterEggInfo monsteregginfo = (MonsterEggInfo) EntityTypes.eggInfo.get(Integer.valueOf(i));

		if (monsteregginfo != null) {
			this.a(monsteregginfo.killEntityStatistic, 1);
		}
	}

	@Override
	public void as() {
		if (!abilities.isFlying) {
			super.as();
		}
	}

	public ItemStack r(int i) {
		return inventory.d(i);
	}

	public void giveExp(int i) {
		addScore(i);
		int j = Integer.MAX_VALUE - expTotal;

		if (i > j) {
			i = j;
		}

		exp += (float) i / (float) getExpToLevel();

		for (expTotal += i; exp >= 1.0F; exp /= getExpToLevel()) {
			exp = (exp - 1.0F) * getExpToLevel();
			levelDown(1);
		}
	}

	public void levelDown(int i) {
		expLevel += i;
		if (expLevel < 0) {
			expLevel = 0;
			exp = 0.0F;
			expTotal = 0;
		}

		if (i > 0 && expLevel % 5 == 0 && h < ticksLived - 100.0F) {
			float f = expLevel > 30 ? 1.0F : expLevel / 30.0F;

			world.makeSound(this, "random.levelup", f * 0.75F, 1.0F);
			h = ticksLived;
		}
	}

	public int getExpToLevel() {
		return expLevel >= 30 ? 62 + (expLevel - 30) * 7 : expLevel >= 15 ? 17 + (expLevel - 15) * 3 : 17;
	}

	public void applyExhaustion(float f) {
		if (!abilities.isInvulnerable) {
			if (!world.isStatic) {
				foodData.a(f);
			}
		}
	}

	public FoodMetaData getFoodData() {
		return foodData;
	}

	public boolean g(boolean flag) {
		return (flag || foodData.c()) && !abilities.isInvulnerable;
	}

	public boolean bR() {
		return getHealth() > 0.0F && getHealth() < getMaxHealth();
	}

	public void a(ItemStack itemstack, int i) {
		if (itemstack != f) {
			f = itemstack;
			g = i;
			if (!world.isStatic) {
				this.e(true);
			}
		}
	}

	public boolean d(int i, int j, int k) {
		if (abilities.mayBuild)
			return true;
		else {
			Block block = world.getType(i, j, k);

			if (block.getMaterial() != Material.AIR) {
				if (block.getMaterial().q())
					return true;

				if (bF() != null) {
					ItemStack itemstack = bF();

					if (itemstack.b(block) || itemstack.a(block) > 1.0F)
						return true;
				}
			}

			return false;
		}
	}

	public boolean a(int i, int j, int k, int l, ItemStack itemstack) {
		return abilities.mayBuild ? true : itemstack != null ? itemstack.z() : false;
	}

	@Override
	protected int getExpValue(EntityHuman entityhuman) {
		if (world.getGameRules().getBoolean("keepInventory"))
			return 0;
		else {
			int i = expLevel * 7;

			return i > 100 ? 100 : i;
		}
	}

	@Override
	protected boolean alwaysGivesExp() {
		return true;
	}

	public void copyTo(EntityHuman entityhuman, boolean flag) {
		if (flag) {
			inventory.b(entityhuman.inventory);
			setHealth(entityhuman.getHealth());
			foodData = entityhuman.foodData;
			expLevel = entityhuman.expLevel;
			expTotal = entityhuman.expTotal;
			exp = entityhuman.exp;
			setScore(entityhuman.getScore());
			aq = entityhuman.aq;
		} else if (world.getGameRules().getBoolean("keepInventory")) {
			inventory.b(entityhuman.inventory);
			expLevel = entityhuman.expLevel;
			expTotal = entityhuman.expTotal;
			exp = entityhuman.exp;
			setScore(entityhuman.getScore());
		}

		enderChest = entityhuman.enderChest;
	}

	@Override
	protected boolean g_() {
		return !abilities.isFlying;
	}

	public void updateAbilities() {
	}

	public void a(EnumGamemode enumgamemode) {
	}

	@Override
	public String getName() {
		return i.getName();
	}

	@Override
	public World getWorld() {
		return world;
	}

	public InventoryEnderChest getEnderChest() {
		return enderChest;
	}

	@Override
	public ItemStack getEquipment(int i) {
		return i == 0 ? inventory.getItemInHand() : inventory.armor[i - 1];
	}

	@Override
	public ItemStack be() {
		return inventory.getItemInHand();
	}

	@Override
	public void setEquipment(int i, ItemStack itemstack) {
		inventory.armor[i] = itemstack;
	}

	@Override
	public ItemStack[] getEquipment() {
		return inventory.armor;
	}

	@Override
	public boolean aC() {
		return !abilities.isFlying;
	}

	public Scoreboard getScoreboard() {
		return world.getScoreboard();
	}

	@Override
	public ScoreboardTeamBase getScoreboardTeam() {
		return getScoreboard().getPlayerTeam(getName());
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		// CraftBukkit - todo: fun
		ChatComponentText chatcomponenttext = new ChatComponentText(ScoreboardTeam.getPlayerDisplayName(getScoreboardTeam(), getName()));

		chatcomponenttext.getChatModifier().setChatClickable(new ChatClickable(EnumClickAction.SUGGEST_COMMAND, "/msg " + getName() + " "));
		return chatcomponenttext;
	}

	@Override
	public void setAbsorptionHearts(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		getDataWatcher().watch(17, Float.valueOf(f));
	}

	@Override
	public float getAbsorptionHearts() {
		return getDataWatcher().getFloat(17);
	}

	public static UUID a(GameProfile gameprofile) {
		UUID uuid = gameprofile.getId();

		if (uuid == null) {
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));
		}

		return uuid;
	}
}
