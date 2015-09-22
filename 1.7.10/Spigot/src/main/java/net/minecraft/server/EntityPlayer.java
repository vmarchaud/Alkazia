package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.com.google.common.collect.Sets;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.org.apache.commons.io.Charsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
// CraftBukkit end
import org.spigotmc.ProtocolData; // Spigot - protocol patch

public class EntityPlayer extends EntityHuman implements ICrafting {

	private static final Logger bL = LogManager.getLogger();
	public String locale = "en_US"; // Spigot
	public PlayerConnection playerConnection;
	public final MinecraftServer server;
	public final PlayerInteractManager playerInteractManager;
	public double d;
	public double e;
	public final List chunkCoordIntPairQueue = new LinkedList();
	public final List removeQueue = new LinkedList(); // CraftBukkit - private -> public
	private final ServerStatisticManager bO;
	private float bP = Float.MIN_VALUE;
	private float bQ = -1.0E8F;
	private int bR = -99999999;
	private boolean bS = true;
	public int lastSentExp = -99999999; // CraftBukkit - private -> public
	public int invulnerableTicks = 60; // CraftBukkit - private -> public
	private EnumChatVisibility bV;
	private boolean bW = true;
	private long bX = System.currentTimeMillis();
	private int containerCounter;
	public boolean g;
	public int ping;
	public boolean viewingCredits;
	// CraftBukkit start
	public String displayName;
	public String listName;
	public org.bukkit.Location compassTarget;
	public int newExp = 0;
	public int newLevel = 0;
	public int newTotalExp = 0;
	public boolean keepLevel = false;
	public double maxHealthCache;
	public boolean joining = true;
	public int lastPing = -1; // Spigot
	// CraftBukkit end
	// Spigot start
	public boolean collidesWithEntities = true;

	@Override
	public boolean R() {
		return collidesWithEntities && super.R(); // (first !this.isDead near bottom of EntityLiving)
	}

	@Override
	public boolean S() {
		return collidesWithEntities && super.S(); // (second !this.isDead near bottom of EntityLiving)
	}

	// Spigot end

	public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
		super(worldserver, gameprofile);
		playerinteractmanager.player = this;
		playerInteractManager = playerinteractmanager;
		ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
		int i = chunkcoordinates.x;
		int j = chunkcoordinates.z;
		int k = chunkcoordinates.y;

		if (!worldserver.worldProvider.g && worldserver.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
			int l = Math.max(5, minecraftserver.getSpawnProtection() - 6);

			i += random.nextInt(l * 2) - l;
			j += random.nextInt(l * 2) - l;
			k = worldserver.i(i, j);
		}

		server = minecraftserver;
		bO = minecraftserver.getPlayerList().a((EntityHuman) this);
		W = 0.0F;
		height = 0.0F;
		setPositionRotation(i + 0.5D, k, j + 0.5D, 0.0F, 0.0F);

		while (!worldserver.getCubes(this, boundingBox).isEmpty()) {
			setPosition(locX, locY + 1.0D, locZ);
		}

		// CraftBukkit start
		displayName = getName();
		listName = getName();
		// this.canPickUpLoot = true; TODO
		maxHealthCache = getMaxHealth();
		// CraftBukkit end
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		if (nbttagcompound.hasKeyOfType("playerGameType", 99)) {
			if (MinecraftServer.getServer().getForceGamemode()) {
				playerInteractManager.setGameMode(MinecraftServer.getServer().getGamemode());
			} else {
				playerInteractManager.setGameMode(EnumGamemode.getById(nbttagcompound.getInt("playerGameType")));
			}
		}
		getBukkitEntity().readExtraData(nbttagcompound); // CraftBukkit
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("playerGameType", playerInteractManager.getGameMode().getId());
		getBukkitEntity().setExtraData(nbttagcompound); // CraftBukkit
	}

	// CraftBukkit start - World fallback code, either respawn location or global spawn
	@Override
	public void spawnIn(World world) {
		super.spawnIn(world);
		if (world == null) {
			dead = false;
			ChunkCoordinates position = null;
			if (spawnWorld != null && !spawnWorld.equals("")) {
				CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(spawnWorld);
				if (cworld != null && this.getBed() != null) {
					world = cworld.getHandle();
					position = EntityHuman.getBed(cworld.getHandle(), this.getBed(), false);
				}
			}
			if (world == null || position == null) {
				world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
				position = world.getSpawn();
			}
			this.world = world;
			setPosition(position.x + 0.5, position.y, position.z + 0.5);
		}
		dimension = ((WorldServer) this.world).dimension;
		playerInteractManager.a((WorldServer) world);
	}

	// CraftBukkit end

	@Override
	public void levelDown(int i) {
		super.levelDown(i);
		lastSentExp = -1;
	}

	public void syncInventory() {
		activeContainer.addSlotListener(this);
	}

	@Override
	protected void e_() {
		height = 0.0F;
	}

	@Override
	public float getHeadHeight() {
		return 1.62F;
	}

	@Override
	public void h() {
		// CraftBukkit start
		if (joining) {
			joining = false;
		}
		// CraftBukkit end

		playerInteractManager.a();
		--invulnerableTicks;
		if (noDamageTicks > 0) {
			--noDamageTicks;
		}

		activeContainer.b();
		if (!world.isStatic && !activeContainer.a(this)) {
			closeInventory();
			activeContainer = defaultContainer;
		}

		while (!removeQueue.isEmpty()) {
			int i = Math.min(removeQueue.size(), 127);
			int[] aint = new int[i];
			Iterator iterator = removeQueue.iterator();
			int j = 0;

			while (iterator.hasNext() && j < i) {
				aint[j++] = ((Integer) iterator.next()).intValue();
				iterator.remove();
			}

			playerConnection.sendPacket(new PacketPlayOutEntityDestroy(aint));
		}

		if (!chunkCoordIntPairQueue.isEmpty()) {
			ArrayList arraylist = new ArrayList();
			Iterator iterator1 = chunkCoordIntPairQueue.iterator();
			ArrayList arraylist1 = new ArrayList();

			Chunk chunk;

			while (iterator1.hasNext() && arraylist.size() < world.spigotConfig.maxBulkChunk) { // Spigot
				ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator1.next();

				if (chunkcoordintpair != null) {
					if (world.isLoaded(chunkcoordintpair.x << 4, 0, chunkcoordintpair.z << 4)) {
						chunk = world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);
						if (chunk.isReady()) {
							arraylist.add(chunk);
							arraylist1.addAll(chunk.tileEntities.values()); // CraftBukkit - Get tile entities directly from the chunk instead of the world
							iterator1.remove();
						}
					}
				} else {
					iterator1.remove();
				}
			}

			if (!arraylist.isEmpty()) {
				playerConnection.sendPacket(new PacketPlayOutMapChunkBulk(arraylist, playerConnection.networkManager.getVersion())); // Spigot - protocol patch
				Iterator iterator2 = arraylist1.iterator();

				while (iterator2.hasNext()) {
					TileEntity tileentity = (TileEntity) iterator2.next();

					this.b(tileentity);
				}

				iterator2 = arraylist.iterator();

				while (iterator2.hasNext()) {
					chunk = (Chunk) iterator2.next();
					this.r().getTracker().a(this, chunk);
				}
			}
		}
	}

	public void i() {
		try {
			super.h();

			for (int i = 0; i < inventory.getSize(); ++i) {
				ItemStack itemstack = inventory.getItem(i);

				if (itemstack != null && itemstack.getItem().h()) {
					Packet packet = ((ItemWorldMapBase) itemstack.getItem()).c(itemstack, world, this);

					if (packet != null) {
						playerConnection.sendPacket(packet);
					}
				}
			}

			// CraftBukkit - Optionally scale health
			if (getHealth() != bQ || bR != foodData.getFoodLevel() || foodData.getSaturationLevel() == 0.0F != bS) {
				playerConnection.sendPacket(new PacketPlayOutUpdateHealth(getBukkitEntity().getScaledHealth(), foodData.getFoodLevel(), foodData.getSaturationLevel()));
				bQ = getHealth();
				bR = foodData.getFoodLevel();
				bS = foodData.getSaturationLevel() == 0.0F;
			}

			if (getHealth() + getAbsorptionHearts() != bP) {
				bP = getHealth() + getAbsorptionHearts();
				// CraftBukkit - Update ALL the scores!
				world.getServer().getScoreboardManager().updateAllScoresForList(IScoreboardCriteria.f, getName(), com.google.common.collect.ImmutableList.of(this));
			}

			// CraftBukkit start - Force max health updates
			if (maxHealthCache != getMaxHealth()) {
				getBukkitEntity().updateScaledHealth();
			}
			// CraftBukkit end

			if (expTotal != lastSentExp) {
				lastSentExp = expTotal;
				playerConnection.sendPacket(new PacketPlayOutExperience(exp, expTotal, expLevel));
			}

			if (ticksLived % 20 * 5 == 0 && !getStatisticManager().hasAchievement(AchievementList.L)) {
				this.j();
			}

			// CraftBukkit start - initialize oldLevel and fire PlayerLevelChangeEvent
			if (oldLevel == -1) {
				oldLevel = expLevel;
			}

			if (oldLevel != expLevel) {
				CraftEventFactory.callPlayerLevelChangeEvent(world.getServer().getPlayer(this), oldLevel, expLevel);
				oldLevel = expLevel;
			}
			// CraftBukkit end
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
			CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");

			this.a(crashreportsystemdetails);
			throw new ReportedException(crashreport);
		}
	}

	protected void j() {
		BiomeBase biomebase = world.getBiome(MathHelper.floor(locX), MathHelper.floor(locZ));

		if (biomebase != null) {
			String s = biomebase.af;
			AchievementSet achievementset = (AchievementSet) getStatisticManager().b((Statistic) AchievementList.L); // CraftBukkit - fix decompile error

			if (achievementset == null) {
				achievementset = (AchievementSet) getStatisticManager().a(AchievementList.L, new AchievementSet());
			}

			achievementset.add(s);
			if (getStatisticManager().b(AchievementList.L) && achievementset.size() == BiomeBase.n.size()) {
				HashSet hashset = Sets.newHashSet(BiomeBase.n);
				Iterator iterator = achievementset.iterator();

				while (iterator.hasNext()) {
					String s1 = (String) iterator.next();
					Iterator iterator1 = hashset.iterator();

					while (iterator1.hasNext()) {
						BiomeBase biomebase1 = (BiomeBase) iterator1.next();

						if (biomebase1.af.equals(s1)) {
							iterator1.remove();
						}
					}

					if (hashset.isEmpty()) {
						break;
					}
				}

				if (hashset.isEmpty()) {
					this.a(AchievementList.L);
				}
			}
		}
	}

	@Override
	public void die(DamageSource damagesource) {
		// CraftBukkit start - fire PlayerDeathEvent
		if (dead)
			return;

		java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
		boolean keepInventory = world.getGameRules().getBoolean("keepInventory");

		if (!keepInventory) {
			for (int i = 0; i < inventory.items.length; ++i) {
				if (inventory.items[i] != null) {
					loot.add(CraftItemStack.asCraftMirror(inventory.items[i]));
				}
			}

			for (int i = 0; i < inventory.armor.length; ++i) {
				if (inventory.armor[i] != null) {
					loot.add(CraftItemStack.asCraftMirror(inventory.armor[i]));
				}
			}
		}

		IChatBaseComponent chatmessage = aW().b();

		String deathmessage = chatmessage.c();
		org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);

		String deathMessage = event.getDeathMessage();

		if (deathMessage != null && deathMessage.length() > 0) {
			if (deathMessage.equals(deathmessage)) {
				server.getPlayerList().sendMessage(chatmessage);
			} else {
				server.getPlayerList().sendMessage(org.bukkit.craftbukkit.util.CraftChatMessage.fromString(deathMessage));
			}
		}

		// we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
		if (!event.getKeepInventory()) {
			for (int i = 0; i < inventory.items.length; ++i) {
				inventory.items[i] = null;
			}

			for (int i = 0; i < inventory.armor.length; ++i) {
				inventory.armor[i] = null;
			}
		}

		closeInventory();
		// CraftBukkit end

		// CraftBukkit - Get our scores instead
		Collection<ScoreboardScore> collection = world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.c, getName(), new java.util.ArrayList<ScoreboardScore>());
		Iterator iterator = collection.iterator();

		while (iterator.hasNext()) {
			ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next(); // CraftBukkit - Use our scores instead

			scoreboardscore.incrementScore();
		}

		EntityLiving entityliving = aX();

		if (entityliving != null) {
			int i = EntityTypes.a(entityliving);
			MonsterEggInfo monsteregginfo = (MonsterEggInfo) EntityTypes.eggInfo.get(Integer.valueOf(i));

			if (monsteregginfo != null) {
				this.a(monsteregginfo.e, 1);
			}

			entityliving.b(this, ba);
		}

		this.a(StatisticList.v, 1);
		aW().g();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			// CraftBukkit - this.server.getPvP() -> this.world.pvpMode
			boolean flag = server.X() && world.pvpMode && "fall".equals(damagesource.translationIndex);

			if (!flag && invulnerableTicks > 0 && damagesource != DamageSource.OUT_OF_WORLD)
				return false;
			else {
				if (damagesource instanceof EntityDamageSource) {
					Entity entity = damagesource.getEntity();

					if (entity instanceof EntityHuman && !this.a((EntityHuman) entity))
						return false;

					if (entity instanceof EntityArrow) {
						EntityArrow entityarrow = (EntityArrow) entity;

						if (entityarrow.shooter instanceof EntityHuman && !this.a((EntityHuman) entityarrow.shooter))
							return false;
					}
				}

				return super.damageEntity(damagesource, f);
			}
		}
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		// CraftBukkit - this.server.getPvP() -> this.world.pvpMode
		return !world.pvpMode ? false : super.a(entityhuman);
	}

	@Override
	public void b(int i) {
		if (dimension == 1 && i == 1) {
			this.a(AchievementList.D);
			world.kill(this);
			viewingCredits = true;
			playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, 0.0F));
		} else {
			if (dimension == 0 && i == 1) {
				this.a(AchievementList.C);
				// CraftBukkit start - Rely on custom portal management
				/*
				ChunkCoordinates chunkcoordinates = this.server.getWorldServer(i).getDimensionSpawn();

				if (chunkcoordinates != null) {
				    this.playerConnection.a((double) chunkcoordinates.x, (double) chunkcoordinates.y, (double) chunkcoordinates.z, 0.0F, 0.0F);
				}

				i = 1;
				*/
				// CraftBukkit end
			} else {
				this.a(AchievementList.y);
			}

			// CraftBukkit start
			TeleportCause cause = dimension == 1 || i == 1 ? TeleportCause.END_PORTAL : TeleportCause.NETHER_PORTAL;
			server.getPlayerList().changeDimension(this, i, cause);
			// CraftBukkit end
			lastSentExp = -1;
			bQ = -1.0F;
			bR = -1;
		}
	}

	private void b(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.getUpdatePacket();

			if (packet != null) {
				playerConnection.sendPacket(packet);
			}
		}
	}

	@Override
	public void receive(Entity entity, int i) {
		super.receive(entity, i);
		activeContainer.b();
	}

	@Override
	public EnumBedResult a(int i, int j, int k) {
		EnumBedResult enumbedresult = super.a(i, j, k);

		if (enumbedresult == EnumBedResult.OK) {
			PacketPlayOutBed packetplayoutbed = new PacketPlayOutBed(this, i, j, k);

			this.r().getTracker().a(this, packetplayoutbed);
			playerConnection.a(locX, locY, locZ, yaw, pitch);
			playerConnection.sendPacket(packetplayoutbed);
		}

		return enumbedresult;
	}

	@Override
	public void a(boolean flag, boolean flag1, boolean flag2) {
		if (!sleeping)
			return; // CraftBukkit - Can't leave bed if not in one!

		if (isSleeping()) {
			this.r().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
		}

		super.a(flag, flag1, flag2);
		if (playerConnection != null) {
			playerConnection.a(locX, locY, locZ, yaw, pitch);
		}
	}

	@Override
	public void mount(Entity entity) {
		// CraftBukkit start
		setPassengerOf(entity);
	}

	@Override
	public void setPassengerOf(Entity entity) {
		// mount(null) doesn't really fly for overloaded methods,
		// so this method is needed
		Entity currentVehicle = vehicle;

		super.setPassengerOf(entity);

		// Check if the vehicle actually changed.
		if (currentVehicle != vehicle) {
			playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this, vehicle));
			playerConnection.a(locX, locY, locZ, yaw, pitch);
		}
		// CraftBukkit end
	}

	@Override
	protected void a(double d0, boolean flag) {
	}

	public void b(double d0, boolean flag) {
		super.a(d0, flag);
	}

	@Override
	public void a(TileEntity tileentity) {
		if (tileentity instanceof TileEntitySign) {
			((TileEntitySign) tileentity).a(this);
			playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(tileentity.x, tileentity.y, tileentity.z));
		}
	}

	public int nextContainerCounter() { // CraftBukkit - private void -> public int
		containerCounter = containerCounter % 100 + 1;
		return containerCounter; // CraftBukkit
	}

	@Override
	public void startCrafting(int i, int j, int k) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerWorkbench(inventory, world, i, j, k));
		if (container == null)
			return;

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 1, "Crafting", 0, true)); // Spigot - protocol patch
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void startEnchanting(int i, int j, int k, String s) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerEnchantTable(inventory, world, i, j, k));
		if (container == null)
			return;

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 4, s == null ? "" : s, 0, s != null)); // Spigot - protocol patch
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openAnvil(int i, int j, int k) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerAnvil(inventory, world, i, j, k, this));
		if (container == null)
			return;

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 8, "Repairing", 0, true)); // Spigot - protocol patch
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openContainer(IInventory iinventory) {
		if (activeContainer != defaultContainer) {
			closeInventory();
		}

		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerChest(inventory, iinventory));
		if (container == null) {
			iinventory.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 0, iinventory.getInventoryName(), iinventory.getSize(), iinventory.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openHopper(TileEntityHopper tileentityhopper) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHopper(inventory, tileentityhopper));
		if (container == null) {
			tileentityhopper.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 9, tileentityhopper.getInventoryName(), tileentityhopper.getSize(), tileentityhopper.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openMinecartHopper(EntityMinecartHopper entityminecarthopper) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHopper(inventory, entityminecarthopper));
		if (container == null) {
			entityminecarthopper.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 9, entityminecarthopper.getInventoryName(), entityminecarthopper.getSize(), entityminecarthopper.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openFurnace(TileEntityFurnace tileentityfurnace) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerFurnace(inventory, tileentityfurnace));
		if (container == null) {
			tileentityfurnace.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 2, tileentityfurnace.getInventoryName(), tileentityfurnace.getSize(), tileentityfurnace.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openDispenser(TileEntityDispenser tileentitydispenser) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerDispenser(inventory, tileentitydispenser));
		if (container == null) {
			tileentitydispenser.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, tileentitydispenser instanceof TileEntityDropper ? 10 : 3, tileentitydispenser.getInventoryName(), tileentitydispenser.getSize(), tileentitydispenser.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerBrewingStand(inventory, tileentitybrewingstand));
		if (container == null) {
			tileentitybrewingstand.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 5, tileentitybrewingstand.getInventoryName(), tileentitybrewingstand.getSize(), tileentitybrewingstand.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openBeacon(TileEntityBeacon tileentitybeacon) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerBeacon(inventory, tileentitybeacon));
		if (container == null) {
			tileentitybeacon.closeContainer();
			return;
		}
		// CraftBukkit end

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 7, tileentitybeacon.getInventoryName(), tileentitybeacon.getSize(), tileentitybeacon.k_()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void openTrade(IMerchant imerchant, String s) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerMerchant(inventory, imerchant, world));
		if (container == null)
			return;

		nextContainerCounter();
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
		InventoryMerchant inventorymerchant = ((ContainerMerchant) activeContainer).getMerchantInventory();

		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 6, s == null ? "" : s, inventorymerchant.getSize(), s != null));
		MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

		if (merchantrecipelist != null) {
			PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(), playerConnection.networkManager.getVersion()); // Spigot

			try {
				packetdataserializer.writeInt(containerCounter);
				merchantrecipelist.a(packetdataserializer);
				playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
			} catch (Exception ioexception) { // CraftBukkit - IOException -> Exception
				bL.error("Couldn\'t send trade list", ioexception);
			} finally {
				packetdataserializer.release();
			}
		}
	}

	@Override
	public void openHorseInventory(EntityHorse entityhorse, IInventory iinventory) {
		// CraftBukkit start - Inventory open hook
		Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHorse(inventory, iinventory, entityhorse));
		if (container == null) {
			iinventory.closeContainer();
			return;
		}
		// CraftBukkit end

		if (activeContainer != defaultContainer) {
			closeInventory();
		}

		nextContainerCounter();
		playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 11, iinventory.getInventoryName(), iinventory.getSize(), iinventory.k_(), entityhorse.getId()));
		activeContainer = container; // CraftBukkit - Use container we passed to event
		activeContainer.windowId = containerCounter;
		activeContainer.addSlotListener(this);
	}

	@Override
	public void a(Container container, int i, ItemStack itemstack) {
		if (!(container.getSlot(i) instanceof SlotResult)) {
			if (!g) {
				playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, i, itemstack));
			}
		}
	}

	public void updateInventory(Container container) {
		this.a(container, container.a());
	}

	@Override
	public void a(Container container, List list) {
		playerConnection.sendPacket(new PacketPlayOutWindowItems(container.windowId, list));
		playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, inventory.getCarried()));
		// CraftBukkit start - Send a Set Slot to update the crafting result slot
		if (java.util.EnumSet.of(InventoryType.CRAFTING, InventoryType.WORKBENCH).contains(container.getBukkitView().getType())) {
			playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
		}
		// CraftBukkit end
	}

	@Override
	public void setContainerData(Container container, int i, int j) {
		// Spigot start - protocol patch
		if (container instanceof ContainerFurnace && playerConnection.networkManager.getVersion() >= 47) {
			switch (i) {
			case 0:
				i = 2;
				playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, 3, 200));
				break;
			case 1:
				i = 0;
				break;
			case 2:
				i = 1;
			}
		}
		// Spigot end
		playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, j));
	}

	@Override
	public void closeInventory() {
		CraftEventFactory.handleInventoryCloseEvent(this); // CraftBukkit
		playerConnection.sendPacket(new PacketPlayOutCloseWindow(activeContainer.windowId));
		this.m();
	}

	public void broadcastCarriedItem() {
		if (!g) {
			playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, inventory.getCarried()));
		}
	}

	public void m() {
		activeContainer.b(this);
		activeContainer = defaultContainer;
	}

	public void a(float f, float f1, boolean flag, boolean flag1) {
		if (vehicle != null) {
			if (f >= -1.0F && f <= 1.0F) {
				bd = f;
			}

			if (f1 >= -1.0F && f1 <= 1.0F) {
				be = f1;
			}

			bc = flag;
			setSneaking(flag1);
		}
	}

	@Override
	public void a(Statistic statistic, int i) {
		if (statistic != null) {
			bO.b(this, statistic, i);
			Iterator iterator = getScoreboard().getObjectivesForCriteria(statistic.k()).iterator();

			while (iterator.hasNext()) {
				ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

				getScoreboard().getPlayerScoreForObjective(getName(), scoreboardobjective).incrementScore();
			}

			if (bO.e()) {
				bO.a(this);
			}
		}
	}

	public void n() {
		if (passenger != null) {
			passenger.mount(this);
		}

		if (sleeping) {
			this.a(true, false, false);
		}
	}

	public void triggerHealthUpdate() {
		bQ = -1.0E8F;
		lastSentExp = -1; // CraftBukkit - Added to reset
	}

	@Override
	public void b(IChatBaseComponent ichatbasecomponent) {
		playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
	}

	@Override
	protected void p() {
		playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 9));
		super.p();
	}

	@Override
	public void a(ItemStack itemstack, int i) {
		super.a(itemstack, i);
		if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().d(itemstack) == EnumAnimation.EAT) {
			this.r().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 3));
		}
	}

	@Override
	public void copyTo(EntityHuman entityhuman, boolean flag) {
		super.copyTo(entityhuman, flag);
		lastSentExp = -1;
		bQ = -1.0F;
		bR = -1;
		removeQueue.addAll(((EntityPlayer) entityhuman).removeQueue);
	}

	@Override
	protected void a(MobEffect mobeffect) {
		super.a(mobeffect);
		playerConnection.sendPacket(new PacketPlayOutEntityEffect(getId(), mobeffect));
	}

	@Override
	protected void a(MobEffect mobeffect, boolean flag) {
		super.a(mobeffect, flag);
		playerConnection.sendPacket(new PacketPlayOutEntityEffect(getId(), mobeffect));
	}

	@Override
	protected void b(MobEffect mobeffect) {
		super.b(mobeffect);
		playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(getId(), mobeffect));
	}

	@Override
	public void enderTeleportTo(double d0, double d1, double d2) {
		playerConnection.a(d0, d1, d2, yaw, pitch);
	}

	@Override
	public void b(Entity entity) {
		this.r().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
	}

	@Override
	public void c(Entity entity) {
		this.r().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
	}

	@Override
	public void updateAbilities() {
		if (playerConnection != null) {
			playerConnection.sendPacket(new PacketPlayOutAbilities(abilities));
		}
	}

	public WorldServer r() {
		return (WorldServer) world;
	}

	@Override
	public void a(EnumGamemode enumgamemode) {
		playerInteractManager.setGameMode(enumgamemode);
		playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, enumgamemode.getId()));
	}

	// CraftBukkit start - Support multi-line messages
	public void sendMessage(IChatBaseComponent[] ichatbasecomponent) {
		for (IChatBaseComponent component : ichatbasecomponent) {
			this.sendMessage(component);
		}
	}

	// CraftBukkit end

	@Override
	public void sendMessage(IChatBaseComponent ichatbasecomponent) {
		playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
	}

	@Override
	public boolean a(int i, String s) {
		if ("seed".equals(s) && !server.X())
			return true;
		else if (!"tell".equals(s) && !"help".equals(s) && !"me".equals(s)) {
			if (server.getPlayerList().isOp(getProfile())) {
				OpListEntry oplistentry = (OpListEntry) server.getPlayerList().getOPs().get(getProfile());

				return oplistentry != null ? oplistentry.a() >= i : server.l() >= i;
			} else
				return false;
		} else
			return true;
	}

	public String s() {
		String s = playerConnection.networkManager.getSocketAddress().toString();

		s = s.substring(s.indexOf("/") + 1);
		s = s.substring(0, s.indexOf(":"));
		return s;
	}

	public void a(PacketPlayInSettings packetplayinsettings) {
		locale = packetplayinsettings.c();
		int i = 256 >> packetplayinsettings.d();

		if (i > 3 && i < 20) {
			;
		}

		bV = packetplayinsettings.e();
		bW = packetplayinsettings.f();
		if (server.N() && server.M().equals(getName())) {
			server.a(packetplayinsettings.g());
		}

		// Spigot start - protocol patch, handle metadata usage change (show cape -> collisions)
		if (packetplayinsettings.version < 16) {
			this.b(1, !packetplayinsettings.h(), packetplayinsettings.version);
		} else {
			this.b(1, false, packetplayinsettings.version);
			datawatcher.watch(10, new ProtocolData.HiddenByte((byte) packetplayinsettings.flags));
		}
		// Spigot end
	}

	public EnumChatVisibility getChatFlags() {
		return bV;
	}

	public void setResourcePack(String s) {
		playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|RPack", s.getBytes(Charsets.UTF_8)));
		// Spigot start - protocol patch
		if (playerConnection.networkManager.getVersion() >= 36) {
			playerConnection.sendPacket(new org.spigotmc.ProtocolInjector.PacketPlayResourcePackSend(s, "thinkislazy"));
		}
		// Spigot end
	}

	@Override
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(MathHelper.floor(locX), MathHelper.floor(locY + 0.5D), MathHelper.floor(locZ));
	}

	public void v() {
		bX = MinecraftServer.ar();
	}

	public ServerStatisticManager getStatisticManager() {
		return bO;
	}

	public void d(Entity entity) {
		if (entity instanceof EntityHuman) {
			playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { entity.getId() }));
		} else {
			removeQueue.add(Integer.valueOf(entity.getId()));
		}
	}

	public long x() {
		return bX;
	}

	// CraftBukkit start - Add per-player time and weather.
	public long timeOffset = 0;
	public boolean relativeTime = true;

	public long getPlayerTime() {
		if (relativeTime)
			// Adds timeOffset to the current server time.
			return world.getDayTime() + timeOffset;
		else
			// Adds timeOffset to the beginning of this day.
			return world.getDayTime() - world.getDayTime() % 24000 + timeOffset;
	}

	public WeatherType weather = null;

	public WeatherType getPlayerWeather() {
		return weather;
	}

	public void setPlayerWeather(WeatherType type, boolean plugin) {
		if (!plugin && weather != null)
			return;

		if (plugin) {
			weather = type;
		}

		if (type == WeatherType.DOWNFALL) {
			playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
			// this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, this.world.j(1.0F)));
			// this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, this.world.h(1.0F)));
		} else {
			playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0));
		}
	}

	public void resetPlayerWeather() {
		weather = null;
		setPlayerWeather(world.getWorldData().hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + getName() + " at " + locX + "," + locY + "," + locZ + ")";
	}

	public void reset() {
		float exp = 0;
		boolean keepInventory = world.getGameRules().getBoolean("keepInventory");

		if (keepLevel || keepInventory) {
			exp = this.exp;
			newTotalExp = expTotal;
			newLevel = expLevel;
		}

		setHealth(getMaxHealth());
		fireTicks = 0;
		fallDistance = 0;
		foodData = new FoodMetaData(this);
		expLevel = newLevel;
		expTotal = newTotalExp;
		this.exp = 0;
		deathTicks = 0;
		removeAllEffects();
		updateEffects = true;
		activeContainer = defaultContainer;
		killer = null;
		lastDamager = null;
		combatTracker = new CombatTracker(this);
		lastSentExp = -1;
		if (keepLevel || keepInventory) {
			this.exp = exp;
		} else {
			giveExp(newExp);
		}
		keepLevel = false;
	}

	@Override
	public CraftPlayer getBukkitEntity() {
		return (CraftPlayer) super.getBukkitEntity();
	}
	// CraftBukkit end
}
