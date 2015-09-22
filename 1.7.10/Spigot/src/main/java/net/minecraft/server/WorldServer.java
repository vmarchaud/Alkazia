package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.WeatherType;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

// CraftBukkit end

public class WorldServer extends World {

	private static final Logger a = LogManager.getLogger();
	private final MinecraftServer server;
	public EntityTracker tracker; // CraftBukkit - private final -> public
	private final PlayerChunkMap manager;
	private Set M;
	private TreeSet N;
	public ChunkProviderServer chunkProviderServer;
	public boolean savingDisabled;
	private boolean O;
	private int emptyTime;
	private final PortalTravelAgent Q;
	private final SpawnerCreature R = new SpawnerCreature();
	private BlockActionDataList[] S = new BlockActionDataList[] { new BlockActionDataList((BananaAPI) null), new BlockActionDataList((BananaAPI) null) };
	private int T;
	private static final StructurePieceTreasure[] U = new StructurePieceTreasure[] { new StructurePieceTreasure(Items.STICK, 0, 1, 3, 10), new StructurePieceTreasure(Item.getItemOf(Blocks.WOOD), 0, 1, 3, 10), new StructurePieceTreasure(Item.getItemOf(Blocks.LOG), 0, 1, 3, 10), new StructurePieceTreasure(Items.STONE_AXE, 0, 1, 1, 3), new StructurePieceTreasure(Items.WOOD_AXE, 0, 1, 1, 5),
			new StructurePieceTreasure(Items.STONE_PICKAXE, 0, 1, 1, 3), new StructurePieceTreasure(Items.WOOD_PICKAXE, 0, 1, 1, 5), new StructurePieceTreasure(Items.APPLE, 0, 2, 3, 5), new StructurePieceTreasure(Items.BREAD, 0, 2, 3, 3), new StructurePieceTreasure(Item.getItemOf(Blocks.LOG2), 0, 1, 3, 10) };
	private List V = new ArrayList();
	private IntHashMap entitiesById;

	// CraftBukkit start
	public final int dimension;

	// Add env and gen to constructor
	public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, WorldSettings worldsettings, MethodProfiler methodprofiler, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen) {
		super(idatamanager, s, worldsettings, WorldProvider.byDimension(env.getId()), methodprofiler, gen, env);
		dimension = i;
		pvpMode = minecraftserver.getPvP();
		// CraftBukkit end
		server = minecraftserver;
		tracker = new EntityTracker(this);
		manager = new PlayerChunkMap(this, spigotConfig.viewDistance); // Spigot
		if (entitiesById == null) {
			entitiesById = new IntHashMap();
		}

		if (M == null) {
			M = new HashSet();
		}

		if (N == null) {
			N = new TreeSet();
		}

		Q = new org.bukkit.craftbukkit.CraftTravelAgent(this); // CraftBukkit
		scoreboard = new ScoreboardServer(minecraftserver);
		PersistentScoreboard persistentscoreboard = (PersistentScoreboard) worldMaps.get(PersistentScoreboard.class, "scoreboard");

		if (persistentscoreboard == null) {
			persistentscoreboard = new PersistentScoreboard();
			worldMaps.a("scoreboard", persistentscoreboard);
		}

		persistentscoreboard.a(scoreboard);
		((ScoreboardServer) scoreboard).a(persistentscoreboard);
	}

	// CraftBukkit start
	@Override
	public TileEntity getTileEntity(int i, int j, int k) {
		TileEntity result = super.getTileEntity(i, j, k);
		Block type = getType(i, j, k);

		if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) { // Spigot
			if (!(result instanceof TileEntityChest)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.FURNACE) {
			if (!(result instanceof TileEntityFurnace)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.DROPPER) {
			if (!(result instanceof TileEntityDropper)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.DISPENSER) {
			if (!(result instanceof TileEntityDispenser)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.JUKEBOX) {
			if (!(result instanceof TileEntityRecordPlayer)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.NOTE_BLOCK) {
			if (!(result instanceof TileEntityNote)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.MOB_SPAWNER) {
			if (!(result instanceof TileEntityMobSpawner)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.SIGN_POST || type == Blocks.WALL_SIGN) {
			if (!(result instanceof TileEntitySign)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.ENDER_CHEST) {
			if (!(result instanceof TileEntityEnderChest)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.BREWING_STAND) {
			if (!(result instanceof TileEntityBrewingStand)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.BEACON) {
			if (!(result instanceof TileEntityBeacon)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		} else if (type == Blocks.HOPPER) {
			if (!(result instanceof TileEntityHopper)) {
				result = fixTileEntity(i, j, k, type, result);
			}
		}

		return result;
	}

	private TileEntity fixTileEntity(int x, int y, int z, Block type, TileEntity found) {
		getServer().getLogger().severe("Block at " + x + "," + y + "," + z + " is " + org.bukkit.Material.getMaterial(Block.getId(type)).toString() + " but has " + found + ". " + "Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.");

		if (type instanceof IContainer) {
			TileEntity replacement = ((IContainer) type).a(this, getData(x, y, z));
			replacement.world = this;
			setTileEntity(x, y, z, replacement);
			return replacement;
		} else {
			getServer().getLogger().severe("Don't know how to fix for this type... Can't do anything! :(");
			return found;
		}
	}

	private boolean canSpawn(int x, int z) {
		if (generator != null)
			return generator.canSpawn(getWorld(), x, z);
		else
			return worldProvider.canSpawn(x, z);
	}

	// CraftBukkit end

	@Override
	public void doTick() {
		super.doTick();
		if (getWorldData().isHardcore() && difficulty != EnumDifficulty.HARD) {
			difficulty = EnumDifficulty.HARD;
		}

		worldProvider.e.b();
		if (everyoneDeeplySleeping()) {
			if (getGameRules().getBoolean("doDaylightCycle")) {
				long i = worldData.getDayTime() + 24000L;

				worldData.setDayTime(i - i % 24000L);
			}

			this.d();
		}

		methodProfiler.a("mobSpawner");
		// CraftBukkit start - Only call spawner if we have players online and the world allows for mobs or animals
		long time = worldData.getTime();
		if (getGameRules().getBoolean("doMobSpawning") && (allowMonsters || allowAnimals) && this instanceof WorldServer && players.size() > 0) {
			timings.mobSpawn.startTiming(); // Spigot
			R.spawnEntities(this, allowMonsters && ticksPerMonsterSpawns != 0 && time % ticksPerMonsterSpawns == 0L, allowAnimals && ticksPerAnimalSpawns != 0 && time % ticksPerAnimalSpawns == 0L, worldData.getTime() % 400L == 0L);
			timings.mobSpawn.stopTiming(); // Spigot
			// CraftBukkit end
		}
		// CraftBukkit end
		timings.doChunkUnload.startTiming(); // Spigot
		methodProfiler.c("chunkSource");
		chunkProvider.unloadChunks();
		int j = this.a(1.0F);

		if (j != this.j) {
			this.j = j;
		}

		worldData.setTime(worldData.getTime() + 1L);
		if (getGameRules().getBoolean("doDaylightCycle")) {
			worldData.setDayTime(worldData.getDayTime() + 1L);
		}

		timings.doChunkUnload.stopTiming(); // Spigot
		methodProfiler.c("tickPending");
		timings.doTickPending.startTiming(); // Spigot
		this.a(false);
		timings.doTickPending.stopTiming(); // Spigot
		methodProfiler.c("tickBlocks");
		timings.doTickTiles.startTiming(); // Spigot
		this.g();
		timings.doTickTiles.stopTiming(); // Spigot
		methodProfiler.c("chunkMap");
		timings.doChunkMap.startTiming(); // Spigot
		manager.flush();
		timings.doChunkMap.stopTiming(); // Spigot
		methodProfiler.c("village");
		timings.doVillages.startTiming(); // Spigot
		villages.tick();
		siegeManager.a();
		timings.doVillages.stopTiming(); // Spigot
		methodProfiler.c("portalForcer");
		timings.doPortalForcer.startTiming(); // Spigot
		Q.a(getTime());
		timings.doPortalForcer.stopTiming(); // Spigot
		methodProfiler.b();
		timings.doSounds.startTiming(); // Spigot
		Z();
		timings.doSounds.stopTiming(); // Spigot

		timings.doChunkGC.startTiming(); // Spigot
		getWorld().processChunkGC(); // CraftBukkit
		timings.doChunkGC.stopTiming(); // Spigot
	}

	public BiomeMeta a(EnumCreatureType enumcreaturetype, int i, int j, int k) {
		List list = L().getMobsFor(enumcreaturetype, i, j, k);

		return list != null && !list.isEmpty() ? (BiomeMeta) WeightedRandom.a(random, list) : null;
	}

	@Override
	public void everyoneSleeping() {
		O = !players.isEmpty();
		Iterator iterator = players.iterator();

		while (iterator.hasNext()) {
			EntityHuman entityhuman = (EntityHuman) iterator.next();

			if (!entityhuman.isSleeping() && !entityhuman.fauxSleeping) { // CraftBukkit
				O = false;
				break;
			}
		}
	}

	protected void d() {
		O = false;
		Iterator iterator = players.iterator();

		while (iterator.hasNext()) {
			EntityHuman entityhuman = (EntityHuman) iterator.next();

			if (entityhuman.isSleeping()) {
				entityhuman.a(false, false, true);
			}
		}

		Y();
	}

	private void Y() {
		// CraftBukkit start
		WeatherChangeEvent weather = new WeatherChangeEvent(getWorld(), false);
		getServer().getPluginManager().callEvent(weather);

		ThunderChangeEvent thunder = new ThunderChangeEvent(getWorld(), false);
		getServer().getPluginManager().callEvent(thunder);
		if (!weather.isCancelled()) {
			worldData.setWeatherDuration(0);
			worldData.setStorm(false);
		}
		if (!thunder.isCancelled()) {
			worldData.setThunderDuration(0);
			worldData.setThundering(false);
		}
		// CraftBukkit end
	}

	public boolean everyoneDeeplySleeping() {
		if (O && !isStatic) {
			Iterator iterator = players.iterator();

			// CraftBukkit - This allows us to assume that some people are in bed but not really, allowing time to pass in spite of AFKers
			boolean foundActualSleepers = false;

			EntityHuman entityhuman;

			do {
				if (!iterator.hasNext())
					return foundActualSleepers; // CraftBukkit

				entityhuman = (EntityHuman) iterator.next();
				// CraftBukkit start
				if (entityhuman.isDeeplySleeping()) {
					foundActualSleepers = true;
				}
			} while (entityhuman.isDeeplySleeping() || entityhuman.fauxSleeping);
			// CraftBukkit end

			return false;
		} else
			return false;
	}

	@Override
	protected void g() {
		super.g();
		int i = 0;
		int j = 0;
		// CraftBukkit start
		// Iterator iterator = this.chunkTickList.iterator();

		// Spigot start
		for (net.minecraft.util.gnu.trove.iterator.TLongShortIterator iter = chunkTickList.iterator(); iter.hasNext();) {
			iter.advance();
			long chunkCoord = iter.key();
			int chunkX = World.keyToX(chunkCoord);
			int chunkZ = World.keyToZ(chunkCoord);
			// If unloaded, or in procedd of being unloaded, drop it
			if (!isChunkLoaded(chunkX, chunkZ) || chunkProviderServer.unloadQueue.contains(chunkX, chunkZ)) {
				iter.remove();
				continue;
			}
			// Spigot end
			// ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
			int k = chunkX * 16;
			int l = chunkZ * 16;

			methodProfiler.a("getChunk");
			Chunk chunk = getChunkAt(chunkX, chunkZ);
			// CraftBukkit end

			this.a(k, l, chunk);
			methodProfiler.c("tickChunk");
			chunk.b(false);
			methodProfiler.c("thunder");
			int i1;
			int j1;
			int k1;
			int l1;

			if (random.nextInt(100000) == 0 && Q() && P()) {
				this.k = this.k * 3 + 1013904223;
				i1 = this.k >> 2;
				j1 = k + (i1 & 15);
				k1 = l + (i1 >> 8 & 15);
				l1 = this.h(j1, k1);
				if (isRainingAt(j1, l1, k1)) {
					strikeLightning(new EntityLightning(this, j1, l1, k1));
				}
			}

			methodProfiler.c("iceandsnow");
			if (random.nextInt(16) == 0) {
				this.k = this.k * 3 + 1013904223;
				i1 = this.k >> 2;
				j1 = i1 & 15;
				k1 = i1 >> 8 & 15;
				l1 = this.h(j1 + k, k1 + l);
				if (s(j1 + k, l1 - 1, k1 + l)) {
					// CraftBukkit start
					BlockState blockState = getWorld().getBlockAt(j1 + k, l1 - 1, k1 + l).getState();
					blockState.setTypeId(Block.getId(Blocks.ICE));

					BlockFormEvent iceBlockForm = new BlockFormEvent(blockState.getBlock(), blockState);
					getServer().getPluginManager().callEvent(iceBlockForm);
					if (!iceBlockForm.isCancelled()) {
						blockState.update(true);
					}
					// CraftBukkit end
				}

				if (Q() && this.e(j1 + k, l1, k1 + l, true)) {
					// CraftBukkit start
					BlockState blockState = getWorld().getBlockAt(j1 + k, l1, k1 + l).getState();
					blockState.setTypeId(Block.getId(Blocks.SNOW));

					BlockFormEvent snow = new BlockFormEvent(blockState.getBlock(), blockState);
					getServer().getPluginManager().callEvent(snow);
					if (!snow.isCancelled()) {
						blockState.update(true);
					}
					// CraftBukkit end
				}

				if (Q()) {
					BiomeBase biomebase = getBiome(j1 + k, k1 + l);

					if (biomebase.e()) {
						this.getType(j1 + k, l1 - 1, k1 + l).l(this, j1 + k, l1 - 1, k1 + l);
					}
				}
			}

			methodProfiler.c("tickBlocks");
			ChunkSection[] achunksection = chunk.getSections();

			j1 = achunksection.length;

			for (k1 = 0; k1 < j1; ++k1) {
				ChunkSection chunksection = achunksection[k1];

				if (chunksection != null && chunksection.shouldTick()) {
					for (int i2 = 0; i2 < 3; ++i2) {
						this.k = this.k * 3 + 1013904223;
						int j2 = this.k >> 2;
						int k2 = j2 & 15;
						int l2 = j2 >> 8 & 15;
						int i3 = j2 >> 16 & 15;

						++j;
						Block block = chunksection.getTypeId(k2, i3, l2);

						if (block.isTicking()) {
							++i;
							growthOdds = iter.value() < 1 ? modifiedOdds : 100; // Spigot - grow fast if no players are in this chunk (value = player count)
							block.a(this, k2 + k, i3 + chunksection.getYPosition(), l2 + l, random);
						}
					}
				}
			}

			methodProfiler.b();
		}
		// Spigot Start
		if (spigotConfig.clearChunksOnTick) {
			chunkTickList.clear();
		}
		// Spigot End
	}

	@Override
	public boolean a(int i, int j, int k, Block block) {
		NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, block);

		return V.contains(nextticklistentry);
	}

	@Override
	public void a(int i, int j, int k, Block block, int l) {
		this.a(i, j, k, block, l, 0);
	}

	@Override
	public void a(int i, int j, int k, Block block, int l, int i1) {
		NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, block);
		byte b0 = 0;

		if (d && block.getMaterial() != Material.AIR) {
			if (block.L()) {
				b0 = 8;
				if (this.b(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
					Block block1 = this.getType(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

					if (block1.getMaterial() != Material.AIR && block1 == nextticklistentry.a()) {
						block1.a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, random);
					}
				}

				return;
			}

			l = 1;
		}

		if (this.b(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
			if (block.getMaterial() != Material.AIR) {
				nextticklistentry.a(l + worldData.getTime());
				nextticklistentry.a(i1);
			}

			if (!M.contains(nextticklistentry)) {
				M.add(nextticklistentry);
				N.add(nextticklistentry);
			}
		}
	}

	@Override
	public void b(int i, int j, int k, Block block, int l, int i1) {
		NextTickListEntry nextticklistentry = new NextTickListEntry(i, j, k, block);

		nextticklistentry.a(i1);
		if (block.getMaterial() != Material.AIR) {
			nextticklistentry.a(l + worldData.getTime());
		}

		if (!M.contains(nextticklistentry)) {
			M.add(nextticklistentry);
			N.add(nextticklistentry);
		}
	}

	@Override
	public void tickEntities() {
		if (false && players.isEmpty()) { // CraftBukkit - this prevents entity cleanup, other issues on servers with no players
			if (emptyTime++ >= 1200)
				return;
		} else {
			this.i();
		}

		super.tickEntities();
		spigotConfig.currentPrimedTnt = 0; // Spigot
	}

	public void i() {
		emptyTime = 0;
	}

	@Override
	public boolean a(boolean flag) {
		int i = N.size();

		if (i != M.size())
			throw new IllegalStateException("TickNextTick list out of synch");
		else {
			/* PaperSpigot start - Fix redstone lag issues
			if (i > 1000) {
			    // CraftBukkit start - If the server has too much to process over time, try to alleviate that
			    if (i > 20 * 1000) {
			        i = i / 20;
			    } else {
			        i = 1000;
			    }
			    // CraftBukkit end
			} */

			if (i > 10000) {
				i = 10000;
			}
			// PaperSpigot end

			methodProfiler.a("cleaning");

			NextTickListEntry nextticklistentry;

			for (int j = 0; j < i; ++j) {
				nextticklistentry = (NextTickListEntry) N.first();
				if (!flag && nextticklistentry.d > worldData.getTime()) {
					break;
				}

				N.remove(nextticklistentry);
				M.remove(nextticklistentry);
				V.add(nextticklistentry);
			}

			methodProfiler.b();
			methodProfiler.a("ticking");
			Iterator iterator = V.iterator();

			while (iterator.hasNext()) {
				nextticklistentry = (NextTickListEntry) iterator.next();
				iterator.remove();
				byte b0 = 0;

				if (this.b(nextticklistentry.a - b0, nextticklistentry.b - b0, nextticklistentry.c - b0, nextticklistentry.a + b0, nextticklistentry.b + b0, nextticklistentry.c + b0)) {
					Block block = this.getType(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);

					if (block.getMaterial() != Material.AIR && Block.a(block, nextticklistentry.a())) {
						try {
							block.a(this, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, random);
						} catch (Throwable throwable) {
							CrashReport crashreport = CrashReport.a(throwable, "Exception while ticking a block");
							CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");

							int k;

							try {
								k = getData(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c);
							} catch (Throwable throwable1) {
								k = -1;
							}

							CrashReportSystemDetails.a(crashreportsystemdetails, nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, block, k);
							throw new ReportedException(crashreport);
						}
					}
				} else {
					this.a(nextticklistentry.a, nextticklistentry.b, nextticklistentry.c, nextticklistentry.a(), 0);
				}
			}

			methodProfiler.b();
			V.clear();
			return !N.isEmpty();
		}
	}

	@Override
	public List a(Chunk chunk, boolean flag) {
		ArrayList arraylist = null;
		ChunkCoordIntPair chunkcoordintpair = chunk.l();
		int i = (chunkcoordintpair.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkcoordintpair.z << 4) - 2;
		int l = k + 16 + 2;

		for (int i1 = 0; i1 < 2; ++i1) {
			Iterator iterator;

			if (i1 == 0) {
				iterator = N.iterator();
			} else {
				iterator = V.iterator();
				if (!V.isEmpty()) {
					a.debug("toBeTicked = " + V.size());
				}
			}

			while (iterator.hasNext()) {
				NextTickListEntry nextticklistentry = (NextTickListEntry) iterator.next();

				if (nextticklistentry.a >= i && nextticklistentry.a < j && nextticklistentry.c >= k && nextticklistentry.c < l) {
					if (flag) {
						M.remove(nextticklistentry);
						iterator.remove();
					}

					if (arraylist == null) {
						arraylist = new ArrayList();
					}

					arraylist.add(nextticklistentry);
				}
			}
		}

		return arraylist;
	}

	/* CraftBukkit start - We prevent spawning in general, so this butchering is not needed
	public void entityJoinedWorld(Entity entity, boolean flag) {
	    if (!this.server.getSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
	        entity.die();
	    }

	    if (!this.server.getSpawnNPCs() && entity instanceof NPC) {
	        entity.die();
	    }

	    super.entityJoinedWorld(entity, flag);
	}
	// CraftBukkit end */

	@Override
	protected IChunkProvider j() {
		IChunkLoader ichunkloader = dataManager.createChunkLoader(worldProvider);

		// CraftBukkit start
		org.bukkit.craftbukkit.generator.InternalChunkGenerator gen;

		if (generator != null) {
			gen = new org.bukkit.craftbukkit.generator.CustomChunkGenerator(this, getSeed(), generator);
		} else if (worldProvider instanceof WorldProviderHell) {
			gen = new org.bukkit.craftbukkit.generator.NetherChunkGenerator(this, getSeed());
		} else if (worldProvider instanceof WorldProviderTheEnd) {
			gen = new org.bukkit.craftbukkit.generator.SkyLandsChunkGenerator(this, getSeed());
		} else {
			gen = new org.bukkit.craftbukkit.generator.NormalChunkGenerator(this, getSeed());
		}

		chunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
		// CraftBukkit end

		return chunkProviderServer;
	}

	public List getTileEntities(int i, int j, int k, int l, int i1, int j1) {
		ArrayList arraylist = new ArrayList();

		// CraftBukkit start - Get tile entities from chunks instead of world
		for (int chunkX = i >> 4; chunkX <= l - 1 >> 4; chunkX++) {
			for (int chunkZ = k >> 4; chunkZ <= j1 - 1 >> 4; chunkZ++) {
				Chunk chunk = getChunkAt(chunkX, chunkZ);
				if (chunk == null) {
					continue;
				}

				for (Object te : chunk.tileEntities.values()) {
					TileEntity tileentity = (TileEntity) te;
					if (tileentity.x >= i && tileentity.y >= j && tileentity.z >= k && tileentity.x < l && tileentity.y < i1 && tileentity.z < j1) {
						arraylist.add(tileentity);
					}
				}
			}
		}
		// CraftBukkit end

		return arraylist;
	}

	@Override
	public boolean a(EntityHuman entityhuman, int i, int j, int k) {
		return !server.a(this, i, j, k, entityhuman);
	}

	@Override
	protected void a(WorldSettings worldsettings) {
		if (entitiesById == null) {
			entitiesById = new IntHashMap();
		}

		if (M == null) {
			M = new HashSet();
		}

		if (N == null) {
			N = new TreeSet();
		}

		this.b(worldsettings);
		super.a(worldsettings);
	}

	protected void b(WorldSettings worldsettings) {
		if (!worldProvider.e()) {
			worldData.setSpawn(0, worldProvider.getSeaLevel(), 0);
		} else {
			isLoading = true;
			WorldChunkManager worldchunkmanager = worldProvider.e;
			List list = worldchunkmanager.a();
			Random random = new Random(getSeed());
			ChunkPosition chunkposition = worldchunkmanager.a(0, 0, 256, list, random);
			int i = 0;
			int j = worldProvider.getSeaLevel();
			int k = 0;

			// CraftBukkit start
			if (generator != null) {
				Random rand = new Random(getSeed());
				org.bukkit.Location spawn = generator.getFixedSpawnLocation(this.getWorld(), rand);

				if (spawn != null) {
					if (spawn.getWorld() != this.getWorld())
						throw new IllegalStateException("Cannot set spawn point for " + worldData.getName() + " to be in another world (" + spawn.getWorld().getName() + ")");
					else {
						worldData.setSpawn(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
						isLoading = false;
						return;
					}
				}
			}
			// CraftBukkit end

			if (chunkposition != null) {
				i = chunkposition.x;
				k = chunkposition.z;
			} else {
				a.warn("Unable to find spawn biome");
			}

			int l = 0;

			while (!canSpawn(i, k)) { // CraftBukkit - use our own canSpawn
				i += random.nextInt(64) - random.nextInt(64);
				k += random.nextInt(64) - random.nextInt(64);
				++l;
				if (l == 1000) {
					break;
				}
			}

			worldData.setSpawn(i, j, k);
			isLoading = false;
			if (worldsettings.c()) {
				k();
			}
		}
	}

	protected void k() {
		WorldGenBonusChest worldgenbonuschest = new WorldGenBonusChest(U, 10);

		for (int i = 0; i < 10; ++i) {
			int j = worldData.c() + random.nextInt(6) - random.nextInt(6);
			int k = worldData.e() + random.nextInt(6) - random.nextInt(6);
			int l = this.i(j, k) + 1;

			if (worldgenbonuschest.generate(this, random, j, l, k)) {
				break;
			}
		}
	}

	public ChunkCoordinates getDimensionSpawn() {
		return worldProvider.h();
	}

	public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict { // CraftBukkit - added throws
		if (chunkProvider.canSave()) {
			if (iprogressupdate != null) {
				iprogressupdate.a("Saving level");
			}

			this.a();
			if (iprogressupdate != null) {
				iprogressupdate.c("Saving chunks");
			}

			chunkProvider.saveChunks(flag, iprogressupdate);
			// CraftBukkit - ArrayList -> Collection
			Collection arraylist = chunkProviderServer.a();
			Iterator iterator = arraylist.iterator();

			while (iterator.hasNext()) {
				Chunk chunk = (Chunk) iterator.next();

				if (chunk != null && !manager.a(chunk.locX, chunk.locZ)) {
					chunkProviderServer.queueUnload(chunk.locX, chunk.locZ);
				}
			}
		}
	}

	public void flushSave() {
		if (chunkProvider.canSave()) {
			chunkProvider.c();
		}
	}

	protected void a() throws ExceptionWorldConflict { // CraftBukkit - added throws
		G();
		dataManager.saveWorldData(worldData, server.getPlayerList().t());
		// CraftBukkit start - save worldMaps once, rather than once per shared world
		if (!(this instanceof SecondaryWorldServer)) {
			worldMaps.a();
		}
		// CraftBukkit end
	}

	@Override
	protected void a(Entity entity) {
		super.a(entity);
		entitiesById.a(entity.getId(), entity);
		Entity[] aentity = entity.at();

		if (aentity != null) {
			for (int i = 0; i < aentity.length; ++i) {
				entitiesById.a(aentity[i].getId(), aentity[i]);
			}
		}
	}

	@Override
	protected void b(Entity entity) {
		super.b(entity);
		entitiesById.d(entity.getId());
		Entity[] aentity = entity.at();

		if (aentity != null) {
			for (int i = 0; i < aentity.length; ++i) {
				entitiesById.d(aentity[i].getId());
			}
		}
	}

	@Override
	public Entity getEntity(int i) {
		return (Entity) entitiesById.get(i);
	}

	@Override
	public boolean strikeLightning(Entity entity) {
		// CraftBukkit start
		LightningStrikeEvent lightning = new LightningStrikeEvent(getWorld(), (org.bukkit.entity.LightningStrike) entity.getBukkitEntity());
		getServer().getPluginManager().callEvent(lightning);

		if (lightning.isCancelled())
			return false;

		if (super.strikeLightning(entity)) {
			server.getPlayerList().sendPacketNearby(entity.locX, entity.locY, entity.locZ, 512.0D, dimension, new PacketPlayOutSpawnEntityWeather(entity));
			// CraftBukkit end
			return true;
		} else
			return false;
	}

	@Override
	public void broadcastEntityEffect(Entity entity, byte b0) {
		getTracker().sendPacketToEntity(entity, new PacketPlayOutEntityStatus(entity, b0));
	}

	@Override
	public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
		// CraftBukkit start
		Explosion explosion = super.createExplosion(entity, d0, d1, d2, f, flag, flag1);

		if (explosion.wasCanceled)
			return explosion;

		/* Remove
		explosion.a = flag;
		explosion.b = flag1;
		explosion.a();
		explosion.a(false);
		*/
		// CraftBukkit end - TODO: Check if explosions are still properly implemented

		if (!flag1) {
			explosion.blocks.clear();
		}

		Iterator iterator = players.iterator();

		while (iterator.hasNext()) {
			EntityHuman entityhuman = (EntityHuman) iterator.next();

			if (entityhuman.e(d0, d1, d2) < 4096.0D) {
				((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.blocks, (Vec3D) explosion.b().get(entityhuman)));
			}
		}

		return explosion;
	}

	@Override
	public void playBlockAction(int i, int j, int k, Block block, int l, int i1) {
		BlockActionData blockactiondata = new BlockActionData(i, j, k, block, l, i1);
		Iterator iterator = S[T].iterator();

		BlockActionData blockactiondata1;

		do {
			if (!iterator.hasNext()) {
				S[T].add(blockactiondata);
				return;
			}

			blockactiondata1 = (BlockActionData) iterator.next();
		} while (!blockactiondata1.equals(blockactiondata));

	}

	private void Z() {
		while (!S[T].isEmpty()) {
			int i = T;

			T ^= 1;
			Iterator iterator = S[i].iterator();

			while (iterator.hasNext()) {
				BlockActionData blockactiondata = (BlockActionData) iterator.next();

				if (this.a(blockactiondata)) {
					// CraftBukkit - this.worldProvider.dimension -> this.dimension
					server.getPlayerList().sendPacketNearby(blockactiondata.a(), blockactiondata.b(), blockactiondata.c(), 64.0D, dimension, new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.b(), blockactiondata.c(), blockactiondata.f(), blockactiondata.d(), blockactiondata.e()));
				}
			}

			S[i].clear();
		}
	}

	private boolean a(BlockActionData blockactiondata) {
		Block block = this.getType(blockactiondata.a(), blockactiondata.b(), blockactiondata.c());

		return block == blockactiondata.f() ? block.a(this, blockactiondata.a(), blockactiondata.b(), blockactiondata.c(), blockactiondata.d(), blockactiondata.e()) : false;
	}

	public void saveLevel() {
		dataManager.a();
	}

	@Override
	protected void o() {
		boolean flag = Q();

		super.o();
		/* CraftBukkit start
		if (this.m != this.n) {
		    this.server.getPlayerList().a(new PacketPlayOutGameStateChange(7, this.n), this.worldProvider.dimension);
		}

		if (this.o != this.p) {
		    this.server.getPlayerList().a(new PacketPlayOutGameStateChange(8, this.p), this.worldProvider.dimension);
		}

		if (flag != this.Q()) {
		    if (flag) {
		        this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(2, 0.0F));
		    } else {
		        this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(1, 0.0F));
		    }

		    this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(7, this.n));
		    this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(8, this.p));
		}
		// */
		if (flag != Q()) {
			// Only send weather packets to those affected
			for (int i = 0; i < players.size(); ++i) {
				if (((EntityPlayer) players.get(i)).world == this) {
					((EntityPlayer) players.get(i)).setPlayerWeather(!flag ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
				}
			}
			// CraftBukkit end
		}
	}

	@Override
	protected int p() {
		return server.getPlayerList().s();
	}

	public MinecraftServer getMinecraftServer() {
		return server;
	}

	public EntityTracker getTracker() {
		return tracker;
	}

	public PlayerChunkMap getPlayerChunkMap() {
		return manager;
	}

	public PortalTravelAgent getTravelAgent() {
		return Q;
	}

	public void a(String s, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
		PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(s, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i);

		for (int j = 0; j < players.size(); ++j) {
			EntityPlayer entityplayer = (EntityPlayer) players.get(j);
			ChunkCoordinates chunkcoordinates = entityplayer.getChunkCoordinates();
			double d7 = d0 - chunkcoordinates.x;
			double d8 = d1 - chunkcoordinates.y;
			double d9 = d2 - chunkcoordinates.z;
			double d10 = d7 * d7 + d8 * d8 + d9 * d9;

			if (d10 <= 256.0D) {
				entityplayer.playerConnection.sendPacket(packetplayoutworldparticles);
			}
		}
	}

	// CraftBukkit start - Helper method
	public int getTypeId(int x, int y, int z) {
		return Block.getId(getType(x, y, z));
	}
	// CraftBukkit end
}
