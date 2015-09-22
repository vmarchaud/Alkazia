package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
// CraftBukkit end
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

class PlayerChunk {

	private final List b;
	private final ChunkCoordIntPair location;
	private short[] dirtyBlocks;
	private int dirtyCount;
	private int f;
	private long g;
	final PlayerChunkMap playerChunkMap;
	// CraftBukkit start - add fields
	private final HashMap<EntityPlayer, Runnable> players = new HashMap<EntityPlayer, Runnable>();
	private boolean loaded = false;
	private Runnable loadedRunnable = new Runnable() {
		@Override
		public void run() {
			loaded = true;
		}
	};

	// CraftBukkit end

	public PlayerChunk(PlayerChunkMap playerchunkmap, int i, int j) {
		playerChunkMap = playerchunkmap;
		b = new ArrayList();
		dirtyBlocks = new short[64];
		location = new ChunkCoordIntPair(i, j);
		playerchunkmap.a().chunkProviderServer.getChunkAt(i, j, loadedRunnable); // CraftBukkit
	}

	public void a(final EntityPlayer entityplayer) { // CraftBukkit - added final to argument
		if (b.contains(entityplayer)) {
			PlayerChunkMap.c().debug("Failed to add player. {} already is in chunk {}, {}", new Object[] { entityplayer, Integer.valueOf(location.x), Integer.valueOf(location.z) });
		} else {
			if (b.isEmpty()) {
				g = PlayerChunkMap.a(playerChunkMap).getTime();
			}

			b.add(entityplayer);
			// CraftBukkit start - use async chunk io
			Runnable playerRunnable;
			if (loaded) {
				playerRunnable = null;
				entityplayer.chunkCoordIntPairQueue.add(location);
			} else {
				playerRunnable = new Runnable() {
					@Override
					public void run() {
						entityplayer.chunkCoordIntPairQueue.add(location);
					}
				};
				playerChunkMap.a().chunkProviderServer.getChunkAt(location.x, location.z, playerRunnable);
			}

			players.put(entityplayer, playerRunnable);
			// CraftBukkit end
		}
	}

	public void b(EntityPlayer entityplayer) {
		if (b.contains(entityplayer)) {
			// CraftBukkit start - If we haven't loaded yet don't load the chunk just so we can clean it up
			if (!loaded) {
				ChunkIOExecutor.dropQueuedChunkLoad(playerChunkMap.a(), location.x, location.z, players.get(entityplayer));
				b.remove(entityplayer);
				players.remove(entityplayer);

				if (b.isEmpty()) {
					ChunkIOExecutor.dropQueuedChunkLoad(playerChunkMap.a(), location.x, location.z, loadedRunnable);
					long i = location.x + 2147483647L | location.z + 2147483647L << 32;
					PlayerChunkMap.b(playerChunkMap).remove(i);
					PlayerChunkMap.c(playerChunkMap).remove(this);
				}

				return;
			}
			// CraftBukkit end

			Chunk chunk = PlayerChunkMap.a(playerChunkMap).getChunkAt(location.x, location.z);

			if (chunk.isReady()) {
				entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 0, entityplayer.playerConnection.networkManager.getVersion())); // Spigot - protocol patch
			}

			players.remove(entityplayer); // CraftBukkit
			b.remove(entityplayer);
			entityplayer.chunkCoordIntPairQueue.remove(location);
			if (b.isEmpty()) {
				long i = location.x + 2147483647L | location.z + 2147483647L << 32;

				this.a(chunk);
				PlayerChunkMap.b(playerChunkMap).remove(i);
				PlayerChunkMap.c(playerChunkMap).remove(this);
				if (dirtyCount > 0) {
					PlayerChunkMap.d(playerChunkMap).remove(this);
				}

				playerChunkMap.a().chunkProviderServer.queueUnload(location.x, location.z);
			}
		}
	}

	public void a() {
		this.a(PlayerChunkMap.a(playerChunkMap).getChunkAt(location.x, location.z));
	}

	private void a(Chunk chunk) {
		chunk.s += PlayerChunkMap.a(playerChunkMap).getTime() - g;
		g = PlayerChunkMap.a(playerChunkMap).getTime();
	}

	public void a(int i, int j, int k) {
		if (dirtyCount == 0) {
			PlayerChunkMap.d(playerChunkMap).add(this);
		}

		f |= 1 << (j >> 4);
		if (dirtyCount < 64) {
			short short1 = (short) (i << 12 | k << 8 | j);

			for (int l = 0; l < dirtyCount; ++l) {
				if (dirtyBlocks[l] == short1)
					return;
			}

			dirtyBlocks[dirtyCount++] = short1;
		}
	}

	public void sendAll(Packet packet) {
		for (int i = 0; i < b.size(); ++i) {
			EntityPlayer entityplayer = (EntityPlayer) b.get(i);

			if (!entityplayer.chunkCoordIntPairQueue.contains(location)) {
				entityplayer.playerConnection.sendPacket(packet);
			}
		}
	}

	public void b() {
		if (dirtyCount != 0) {
			int i;
			int j;
			int k;

			if (dirtyCount == 1) {
				i = location.x * 16 + (dirtyBlocks[0] >> 12 & 15);
				j = dirtyBlocks[0] & 255;
				k = location.z * 16 + (dirtyBlocks[0] >> 8 & 15);
				sendAll(new PacketPlayOutBlockChange(i, j, k, PlayerChunkMap.a(playerChunkMap)));
				if (PlayerChunkMap.a(playerChunkMap).getType(i, j, k).isTileEntity()) {
					sendTileEntity(PlayerChunkMap.a(playerChunkMap).getTileEntity(i, j, k));
				}
			} else {
				int l;

				if (dirtyCount == 64) {
					i = location.x * 16;
					j = location.z * 16;
					// Spigot start - protocol patch
					//this.sendAll(new PacketPlayOutMapChunk(PlayerChunkMap.a(this.playerChunkMap).getChunkAt(this.location.x, this.location.z), (this.f == 0xFFFF), this.f)); // CraftBukkit - send everything (including biome) if all sections flagged

					Chunk chunk = PlayerChunkMap.a(playerChunkMap).getChunkAt(location.x, location.z);
					for (int idx = 0; idx < b.size(); ++idx) {
						EntityPlayer entityplayer = (EntityPlayer) b.get(idx);

						if (!entityplayer.chunkCoordIntPairQueue.contains(location)) {
							entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, f == 0xFFFF, f, entityplayer.playerConnection.networkManager.getVersion()));
						}
					}

					// Spigot end - protocol patch
					for (k = 0; k < 16; ++k) {
						if ((f & 1 << k) != 0) {
							l = k << 4;
							List list = PlayerChunkMap.a(playerChunkMap).getTileEntities(i, l, j, i + 16, l + 16, j + 16);

							for (int i1 = 0; i1 < list.size(); ++i1) {
								sendTileEntity((TileEntity) list.get(i1));
							}
						}
					}
				} else {
					sendAll(new PacketPlayOutMultiBlockChange(dirtyCount, dirtyBlocks, PlayerChunkMap.a(playerChunkMap).getChunkAt(location.x, location.z)));

					for (i = 0; i < dirtyCount; ++i) {
						j = location.x * 16 + (dirtyBlocks[i] >> 12 & 15);
						k = dirtyBlocks[i] & 255;
						l = location.z * 16 + (dirtyBlocks[i] >> 8 & 15);
						if (PlayerChunkMap.a(playerChunkMap).getType(j, k, l).isTileEntity()) {
							sendTileEntity(PlayerChunkMap.a(playerChunkMap).getTileEntity(j, k, l));
						}
					}
				}
			}

			dirtyCount = 0;
			f = 0;
		}
	}

	private void sendTileEntity(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.getUpdatePacket();

			if (packet != null) {
				sendAll(packet);
			}
		}
	}

	static ChunkCoordIntPair a(PlayerChunk playerchunk) {
		return playerchunk.location;
	}

	static List b(PlayerChunk playerchunk) {
		return playerchunk.b;
	}
}
