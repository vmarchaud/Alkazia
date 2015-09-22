package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
// CraftBukkit start
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongHashSet;
import org.bukkit.craftbukkit.util.LongObjectHashMap;
import org.bukkit.event.world.ChunkUnloadEvent;

// CraftBukkit end

public class ChunkProviderServer implements IChunkProvider {

	private static final Logger b = LogManager.getLogger();
	// CraftBukkit start - private -> public
	public LongHashSet unloadQueue = new LongHashSet(); // LongHashSet
	public Chunk emptyChunk;
	public IChunkProvider chunkProvider;
	private IChunkLoader f;
	public boolean forceChunkLoad = false; // true -> false
	public LongObjectHashMap<Chunk> chunks = new LongObjectHashMap<Chunk>();
	public WorldServer world;

	// CraftBukkit end

	public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
		emptyChunk = new EmptyChunk(worldserver, 0, 0);
		world = worldserver;
		f = ichunkloader;
		chunkProvider = ichunkprovider;
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit
	}

	// CraftBukkit start - Change return type to Collection and return the values of our chunk map
	public java.util.Collection a() {
		// return this.chunkList;
		return chunks.values();
		// CraftBukkit end
	}

	public void queueUnload(int i, int j) {
		if (world.worldProvider.e()) {
			ChunkCoordinates chunkcoordinates = world.getSpawn();
			int k = i * 16 + 8 - chunkcoordinates.x;
			int l = j * 16 + 8 - chunkcoordinates.z;
			short short1 = 128;

			// CraftBukkit start
			if (k < -short1 || k > short1 || l < -short1 || l > short1 || !world.keepSpawnInMemory) { // Added 'this.world.keepSpawnInMemory'
				unloadQueue.add(i, j);

				Chunk c = chunks.get(LongHash.toLong(i, j));
				if (c != null) {
					c.mustSave = true;
				}
			}
			// CraftBukkit end
		} else {
			// CraftBukkit start
			unloadQueue.add(i, j);

			Chunk c = chunks.get(LongHash.toLong(i, j));
			if (c != null) {
				c.mustSave = true;
			}
			// CraftBukkit end
		}
	}

	public void b() {
		Iterator iterator = chunks.values().iterator(); // CraftBukkit

		while (iterator.hasNext()) {
			Chunk chunk = (Chunk) iterator.next();

			queueUnload(chunk.locX, chunk.locZ);
		}
	}

	// CraftBukkit start - Add async variant, provide compatibility
	public Chunk getChunkIfLoaded(int x, int z) {
		return chunks.get(LongHash.toLong(x, z));
	}

	@Override
	public Chunk getChunkAt(int i, int j) {
		return getChunkAt(i, j, null);
	}

	public Chunk getChunkAt(int i, int j, Runnable runnable) {
		unloadQueue.remove(i, j);
		Chunk chunk = chunks.get(LongHash.toLong(i, j));
		ChunkRegionLoader loader = null;

		if (f instanceof ChunkRegionLoader) {
			loader = (ChunkRegionLoader) f;
		}

		// We can only use the queue for already generated chunks
		if (chunk == null && loader != null && loader.chunkExists(world, i, j)) {
			if (runnable != null) {
				ChunkIOExecutor.queueChunkLoad(world, loader, this, i, j, runnable);
				return null;
			} else {
				chunk = ChunkIOExecutor.syncChunkLoad(world, loader, this, i, j);
			}
		} else if (chunk == null) {
			chunk = originalGetChunkAt(i, j);
		}

		// If we didn't load the chunk async and have a callback run it now
		if (runnable != null) {
			runnable.run();
		}

		return chunk;
	}

	public Chunk originalGetChunkAt(int i, int j) {
		unloadQueue.remove(i, j);
		Chunk chunk = chunks.get(LongHash.toLong(i, j));
		boolean newChunk = false;

		if (chunk == null) {
			world.timings.syncChunkLoadTimer.startTiming(); // Spigot
			chunk = loadChunk(i, j);
			if (chunk == null) {
				if (chunkProvider == null) {
					chunk = emptyChunk;
				} else {
					try {
						chunk = chunkProvider.getOrCreateChunk(i, j);
					} catch (Throwable throwable) {
						CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
						CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

						crashreportsystemdetails.a("Location", String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
						crashreportsystemdetails.a("Position hash", Long.valueOf(LongHash.toLong(i, j))); // CraftBukkit - Use LongHash
						crashreportsystemdetails.a("Generator", chunkProvider.getName());
						throw new ReportedException(crashreport);
					}
				}
				newChunk = true; // CraftBukkit
			}

			chunks.put(LongHash.toLong(i, j), chunk); // CraftBukkit
			chunk.addEntities();

			// CraftBukkit start
			Server server = world.getServer();
			if (server != null) {
				/*
				 * If it's a new world, the first few chunks are generated inside
				 * the World constructor. We can't reliably alter that, so we have
				 * no way of creating a CraftWorld/CraftServer at that point.
				 */
				server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
			}

			// Update neighbor counts
			for (int x = -2; x < 3; x++) {
				for (int z = -2; z < 3; z++) {
					if (x == 0 && z == 0) {
						continue;
					}

					Chunk neighbor = getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
					if (neighbor != null) {
						neighbor.setNeighborLoaded(-x, -z);
						chunk.setNeighborLoaded(x, z);
					}
				}
			}
			// CraftBukkit end
			chunk.loadNearby(this, this, i, j);
			world.timings.syncChunkLoadTimer.stopTiming(); // Spigot
		}

		return chunk;
	}

	@Override
	public Chunk getOrCreateChunk(int i, int j) {
		// CraftBukkit start
		Chunk chunk = chunks.get(LongHash.toLong(i, j));

		chunk = chunk == null ? !world.isLoading && !forceChunkLoad ? emptyChunk : this.getChunkAt(i, j) : chunk;
		if (chunk == emptyChunk)
			return chunk;
		if (i != chunk.locX || j != chunk.locZ) {
			b.error("Chunk (" + chunk.locX + ", " + chunk.locZ + ") stored at  (" + i + ", " + j + ") in world '" + world.getWorld().getName() + "'");
			b.error(chunk.getClass().getName());
			Throwable ex = new Throwable();
			ex.fillInStackTrace();
			ex.printStackTrace();
		}
		return chunk;
		// CraftBukkit end
	}

	public Chunk loadChunk(int i, int j) { // CraftBukkit - private -> public
		if (f == null)
			return null;
		else {
			try {
				Chunk chunk = f.a(world, i, j);

				if (chunk != null) {
					chunk.lastSaved = world.getTime();
					if (chunkProvider != null) {
						world.timings.syncChunkLoadStructuresTimer.startTiming(); // Spigot
						chunkProvider.recreateStructures(i, j);
						world.timings.syncChunkLoadStructuresTimer.stopTiming(); // Spigot
					}
				}

				return chunk;
			} catch (Exception exception) {
				b.error("Couldn\'t load chunk", exception);
				return null;
			}
		}
	}

	public void saveChunkNOP(Chunk chunk) { // CraftBukkit - private -> public
		if (f != null) {
			try {
				f.b(world, chunk);
			} catch (Exception exception) {
				b.error("Couldn\'t save entities", exception);
			}
		}
	}

	public void saveChunk(Chunk chunk) { // CraftBukkit - private -> public
		if (f != null) {
			try {
				chunk.lastSaved = world.getTime();
				f.a(world, chunk);
				// CraftBukkit start - IOException to Exception
			} catch (Exception ioexception) {
				b.error("Couldn\'t save chunk", ioexception);
				/* Remove extra exception
				} catch (ExceptionWorldConflict exceptionworldconflict) {
				b.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
				// CraftBukkit end */
			}
		}
	}

	@Override
	public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
		Chunk chunk = getOrCreateChunk(i, j);

		if (!chunk.done) {
			chunk.p();
			if (chunkProvider != null) {
				chunkProvider.getChunkAt(ichunkprovider, i, j);

				// CraftBukkit start
				BlockFalling.instaFall = true;
				Random random = new Random();
				random.setSeed(world.getSeed());
				long xRand = random.nextLong() / 2L * 2L + 1L;
				long zRand = random.nextLong() / 2L * 2L + 1L;
				random.setSeed(i * xRand + j * zRand ^ world.getSeed());

				org.bukkit.World world = this.world.getWorld();
				if (world != null) {
					this.world.populating = true;
					try {
						for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
							populator.populate(world, random, chunk.bukkitChunk);
						}
					} finally {
						this.world.populating = false;
					}
				}
				BlockFalling.instaFall = false;
				this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(chunk.bukkitChunk));
				// CraftBukkit end

				chunk.e();
			}
		}
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		int i = 0;
		// CraftBukkit start
		Iterator iterator = chunks.values().iterator();

		while (iterator.hasNext()) {
			Chunk chunk = (Chunk) iterator.next();
			// CraftBukkit end

			if (flag) {
				saveChunkNOP(chunk);
			}

			if (chunk.a(flag)) {
				saveChunk(chunk);
				chunk.n = false;
				++i;
				if (i == 24 && !flag)
					return false;
			}
		}

		return true;
	}

	@Override
	public void c() {
		if (f != null) {
			f.b();
		}
	}

	@Override
	public boolean unloadChunks() {
		if (!world.savingDisabled) {
			// CraftBukkit start
			Server server = world.getServer();
			for (int i = 0; i < 100 && !unloadQueue.isEmpty(); i++) {
				long chunkcoordinates = unloadQueue.popFirst();
				Chunk chunk = chunks.get(chunkcoordinates);
				if (chunk == null) {
					continue;
				}

				ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
				server.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					if (chunk != null) {
						chunk.removeEntities();
						saveChunk(chunk);
						saveChunkNOP(chunk);
						chunks.remove(chunkcoordinates); // CraftBukkit
					}

					// this.unloadQueue.remove(olong);
					// this.chunks.remove(olong.longValue());

					// Update neighbor counts
					for (int x = -2; x < 3; x++) {
						for (int z = -2; z < 3; z++) {
							if (x == 0 && z == 0) {
								continue;
							}

							Chunk neighbor = getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
							if (neighbor != null) {
								neighbor.setNeighborUnloaded(-x, -z);
								chunk.setNeighborUnloaded(x, z);
							}
						}
					}
				}
			}
			// CraftBukkit end

			if (f != null) {
				f.a();
			}
		}

		return chunkProvider.unloadChunks();
	}

	@Override
	public boolean canSave() {
		return !world.savingDisabled;
	}

	@Override
	public String getName() {
		// CraftBukkit - this.chunks.count() -> .values().size()
		return "ServerChunkCache: " + chunks.values().size() + " Drop: " + unloadQueue.size();
	}

	@Override
	public List getMobsFor(EnumCreatureType enumcreaturetype, int i, int j, int k) {
		return chunkProvider.getMobsFor(enumcreaturetype, i, j, k);
	}

	@Override
	public ChunkPosition findNearestMapFeature(World world, String s, int i, int j, int k) {
		return chunkProvider.findNearestMapFeature(world, s, i, j, k);
	}

	@Override
	public int getLoadedChunks() {
		// CraftBukkit - this.chunks.count() -> this.chunks.size()
		return chunks.size();
	}

	@Override
	public void recreateStructures(int i, int j) {
	}
}
