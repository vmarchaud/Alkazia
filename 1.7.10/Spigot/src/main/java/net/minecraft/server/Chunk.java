package net.minecraft.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit; // CraftBukkit

public class Chunk {

	private static final Logger t = LogManager.getLogger();
	public static boolean a;
	private ChunkSection[] sections;
	private byte[] v;
	public int[] b;
	public boolean[] c;
	public boolean d;
	public World world;
	public int[] heightMap;
	public final int locX;
	public final int locZ;
	private boolean w;
	public Map tileEntities;
	public List[] entitySlices;
	public boolean done;
	public boolean lit;
	public boolean m;
	public boolean n;
	public boolean o;
	public long lastSaved;
	public boolean q;
	public int r;
	public long s;
	private int x;
	protected net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap<Class> entityCount = new net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap<Class>(); // Spigot

	// CraftBukkit start - Neighbor loaded cache for chunk lighting and entity ticking
	private int neighbors = 0x1 << 12;

	public boolean areNeighborsLoaded(final int radius) {
		switch (radius) {
		case 2:
			return neighbors == Integer.MAX_VALUE >> 6;
		case 1:
			final int mask =
			//        x        z   offset            x        z   offset            x         z   offset
			0x1 << 1 * 5 + 1 + 12 | 0x1 << 0 * 5 + 1 + 12 | 0x1 << -1 * 5 + 1 + 12 | 0x1 << 1 * 5 + 0 + 12 | 0x1 << 0 * 5 + 0 + 12 | 0x1 << -1 * 5 + 0 + 12 | 0x1 << 1 * 5 + -1 + 12 | 0x1 << 0 * 5 + -1 + 12 | 0x1 << -1 * 5 + -1 + 12;
			return (neighbors & mask) == mask;
		default:
			throw new UnsupportedOperationException(String.valueOf(radius));
		}
	}

	public void setNeighborLoaded(final int x, final int z) {
		neighbors |= 0x1 << x * 5 + 12 + z;
	}

	public void setNeighborUnloaded(final int x, final int z) {
		neighbors &= ~(0x1 << x * 5 + 12 + z);
	}

	// CraftBukkit end

	public Chunk(World world, int i, int j) {
		sections = new ChunkSection[16];
		v = new byte[256];
		b = new int[256];
		c = new boolean[256];
		tileEntities = new HashMap();
		x = 4096;
		entitySlices = new List[16];
		this.world = world;
		locX = i;
		locZ = j;
		heightMap = new int[256];

		for (int k = 0; k < entitySlices.length; ++k) {
			entitySlices[k] = new org.bukkit.craftbukkit.util.UnsafeList(); // CraftBukkit - ArrayList -> UnsafeList
		}

		Arrays.fill(b, -999);
		Arrays.fill(v, (byte) -1);

		// CraftBukkit start
		if (!(this instanceof EmptyChunk)) {
			bukkitChunk = new org.bukkit.craftbukkit.CraftChunk(this);
		}
	}

	public org.bukkit.Chunk bukkitChunk;
	public boolean mustSave;

	// CraftBukkit end

	public Chunk(World world, Block[] ablock, int i, int j) {
		this(world, i, j);
		int k = ablock.length / 256;
		boolean flag = !world.worldProvider.g;

		for (int l = 0; l < 16; ++l) {
			for (int i1 = 0; i1 < 16; ++i1) {
				for (int j1 = 0; j1 < k; ++j1) {
					Block block = ablock[l << 11 | i1 << 7 | j1];

					if (block != null && block.getMaterial() != Material.AIR) {
						int k1 = j1 >> 4;

						if (sections[k1] == null) {
							sections[k1] = new ChunkSection(k1 << 4, flag);
						}

						sections[k1].setTypeId(l, j1 & 15, i1, block);
					}
				}
			}
		}
	}

	public Chunk(World world, Block[] ablock, byte[] abyte, int i, int j) {
		this(world, i, j);
		int k = ablock.length / 256;
		boolean flag = !world.worldProvider.g;

		for (int l = 0; l < 16; ++l) {
			for (int i1 = 0; i1 < 16; ++i1) {
				for (int j1 = 0; j1 < k; ++j1) {
					int k1 = l * k * 16 | i1 * k | j1;
					Block block = ablock[k1];

					if (block != null && block != Blocks.AIR) {
						int l1 = j1 >> 4;

						if (sections[l1] == null) {
							sections[l1] = new ChunkSection(l1 << 4, flag);
						}

						sections[l1].setTypeId(l, j1 & 15, i1, block);
						sections[l1].setData(l, j1 & 15, i1, checkData(block, abyte[k1]));
					}
				}
			}
		}
	}

	public boolean a(int i, int j) {
		return i == locX && j == locZ;
	}

	public int b(int i, int j) {
		return heightMap[j << 4 | i];
	}

	public int h() {
		for (int i = sections.length - 1; i >= 0; --i) {
			if (sections[i] != null)
				return sections[i].getYPosition();
		}

		return 0;
	}

	public ChunkSection[] getSections() {
		return sections;
	}

	public void initLighting() {
		int i = this.h();

		r = Integer.MAX_VALUE;

		for (int j = 0; j < 16; ++j) {
			int k = 0;

			while (k < 16) {
				b[j + (k << 4)] = -999;
				int l = i + 16 - 1;

				while (true) {
					if (l > 0) {
						if (this.b(j, l - 1, k) == 0) {
							--l;
							continue;
						}

						heightMap[k << 4 | j] = l;
						if (l < r) {
							r = l;
						}
					}

					if (!world.worldProvider.g) {
						l = 15;
						int i1 = i + 16 - 1;

						do {
							int j1 = this.b(j, i1, k);

							if (j1 == 0 && l != 15) {
								j1 = 1;
							}

							l -= j1;
							if (l > 0) {
								ChunkSection chunksection = sections[i1 >> 4];

								if (chunksection != null) {
									chunksection.setSkyLight(j, i1 & 15, k, l);
									world.m((locX << 4) + j, i1, (locZ << 4) + k);
								}
							}

							--i1;
						} while (i1 > 0 && l > 0);
					}

					++k;
					break;
				}
			}
		}

		n = true;
	}

	private void e(int i, int j) {
		c[i + j * 16] = true;
		w = true;
	}

	private void c(boolean flag) {
		world.methodProfiler.a("recheckGaps");
		if (world.areChunksLoaded(locX * 16 + 8, 0, locZ * 16 + 8, 16)) {
			for (int i = 0; i < 16; ++i) {
				for (int j = 0; j < 16; ++j) {
					if (c[i + j * 16]) {
						c[i + j * 16] = false;
						int k = this.b(i, j);
						int l = locX * 16 + i;
						int i1 = locZ * 16 + j;
						int j1 = world.g(l - 1, i1);
						int k1 = world.g(l + 1, i1);
						int l1 = world.g(l, i1 - 1);
						int i2 = world.g(l, i1 + 1);

						if (k1 < j1) {
							j1 = k1;
						}

						if (l1 < j1) {
							j1 = l1;
						}

						if (i2 < j1) {
							j1 = i2;
						}

						g(l, i1, j1);
						g(l - 1, i1, k);
						g(l + 1, i1, k);
						g(l, i1 - 1, k);
						g(l, i1 + 1, k);
						if (flag) {
							world.methodProfiler.b();
							return;
						}
					}
				}
			}

			w = false;
		}

		world.methodProfiler.b();
	}

	private void g(int i, int j, int k) {
		int l = world.getHighestBlockYAt(i, j);

		if (l > k) {
			this.c(i, j, k, l + 1);
		} else if (l < k) {
			this.c(i, j, l, k + 1);
		}
	}

	private void c(int i, int j, int k, int l) {
		if (l > k && world.areChunksLoaded(i, 0, j, 16)) {
			for (int i1 = k; i1 < l; ++i1) {
				world.c(EnumSkyBlock.SKY, i, i1, j);
			}

			n = true;
		}
	}

	private void h(int i, int j, int k) {
		int l = heightMap[k << 4 | i] & 255;
		int i1 = l;

		if (j > l) {
			i1 = j;
		}

		while (i1 > 0 && this.b(i, i1 - 1, k) == 0) {
			--i1;
		}

		if (i1 != l) {
			world.b(i + locX * 16, k + locZ * 16, i1, l);
			heightMap[k << 4 | i] = i1;
			int j1 = locX * 16 + i;
			int k1 = locZ * 16 + k;
			int l1;
			int i2;

			if (!world.worldProvider.g) {
				ChunkSection chunksection;

				if (i1 < l) {
					for (l1 = i1; l1 < l; ++l1) {
						chunksection = sections[l1 >> 4];
						if (chunksection != null) {
							chunksection.setSkyLight(i, l1 & 15, k, 15);
							world.m((locX << 4) + i, l1, (locZ << 4) + k);
						}
					}
				} else {
					for (l1 = l; l1 < i1; ++l1) {
						chunksection = sections[l1 >> 4];
						if (chunksection != null) {
							chunksection.setSkyLight(i, l1 & 15, k, 0);
							world.m((locX << 4) + i, l1, (locZ << 4) + k);
						}
					}
				}

				l1 = 15;

				while (i1 > 0 && l1 > 0) {
					--i1;
					i2 = this.b(i, i1, k);
					if (i2 == 0) {
						i2 = 1;
					}

					l1 -= i2;
					if (l1 < 0) {
						l1 = 0;
					}

					ChunkSection chunksection1 = sections[i1 >> 4];

					if (chunksection1 != null) {
						chunksection1.setSkyLight(i, i1 & 15, k, l1);
					}
				}
			}

			l1 = heightMap[k << 4 | i];
			i2 = l;
			int j2 = l1;

			if (l1 < l) {
				i2 = l1;
				j2 = l;
			}

			if (l1 < r) {
				r = l1;
			}

			if (!world.worldProvider.g) {
				this.c(j1 - 1, k1, i2, j2);
				this.c(j1 + 1, k1, i2, j2);
				this.c(j1, k1 - 1, i2, j2);
				this.c(j1, k1 + 1, i2, j2);
				this.c(j1, k1, i2, j2);
			}

			n = true;
		}
	}

	public int b(int i, int j, int k) {
		return getType(i, j, k).k();
	}

	public Block getType(int i, int j, int k) {
		Block block = Blocks.AIR;

		if (j >> 4 < sections.length) {
			ChunkSection chunksection = sections[j >> 4];

			if (chunksection != null) {
				try {
					block = chunksection.getTypeId(i, j & 15, k);
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.a(throwable, "Getting block");
					CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

					crashreportsystemdetails.a("Location", new CrashReportLocation(this, i, j, k));
					throw new ReportedException(crashreport);
				}
			}
		}

		return block;
	}

	public int getData(int i, int j, int k) {
		if (j >> 4 >= sections.length)
			return 0;
		else {
			ChunkSection chunksection = sections[j >> 4];

			return chunksection != null ? chunksection.getData(i, j & 15, k) : 0;
		}
	}

	// Spigot start - prevent invalid data values
	public static int checkData(Block block, int data) {
		if (block == Blocks.DOUBLE_PLANT)
			return data < 6 || data >= 8 ? data : 0;
		return data;
	}

	// Spigot end

	public boolean a(int i, int j, int k, Block block, int l) {
		int i1 = k << 4 | i;

		if (j >= b[i1] - 1) {
			b[i1] = -999;
		}

		int j1 = heightMap[i1];
		Block block1 = getType(i, j, k);
		int k1 = getData(i, j, k);

		if (block1 == block && k1 == l)
			return false;
		else {
			ChunkSection chunksection = sections[j >> 4];
			boolean flag = false;

			if (chunksection == null) {
				if (block == Blocks.AIR)
					return false;

				chunksection = sections[j >> 4] = new ChunkSection(j >> 4 << 4, !world.worldProvider.g);
				flag = j >= j1;
			}

			int l1 = locX * 16 + i;
			int i2 = locZ * 16 + k;

			if (!world.isStatic) {
				block1.f(world, l1, j, i2, k1);
			}

			// CraftBukkit start - Delay removing containers until after they're cleaned up
			if (!(block1 instanceof IContainer)) {
				chunksection.setTypeId(i, j & 15, k, block);
			}
			// CraftBukkit end

			if (!world.isStatic) {
				block1.remove(world, l1, j, i2, block1, k1);
			} else if (block1 instanceof IContainer && block1 != block) {
				world.p(l1, j, i2);
			}

			// CraftBukkit start - Remove containers now after cleanup
			if (block1 instanceof IContainer) {
				chunksection.setTypeId(i, j & 15, k, block);
			}
			// CraftBukkit end

			if (chunksection.getTypeId(i, j & 15, k) != block)
				return false;
			else {
				chunksection.setData(i, j & 15, k, checkData(block, l));
				if (flag) {
					initLighting();
				} else {
					int j2 = block.k();
					int k2 = block1.k();

					if (j2 > 0) {
						if (j >= j1) {
							this.h(i, j + 1, k);
						}
					} else if (j == j1 - 1) {
						this.h(i, j, k);
					}

					if (j2 != k2 && (j2 < k2 || getBrightness(EnumSkyBlock.SKY, i, j, k) > 0 || getBrightness(EnumSkyBlock.BLOCK, i, j, k) > 0)) {
						this.e(i, k);
					}
				}

				TileEntity tileentity;

				if (block1 instanceof IContainer) {
					tileentity = this.e(i, j, k);
					if (tileentity != null) {
						tileentity.u();
					}
				}

				// CraftBukkit - Don't place while processing the BlockPlaceEvent, unless it's a BlockContainer. Prevents blocks such as TNT from activating when cancelled.
				if (!world.isStatic && (!world.captureBlockStates || block instanceof BlockContainer)) {
					block.onPlace(world, l1, j, i2);
				}

				if (block instanceof IContainer) {

					tileentity = this.e(i, j, k);
					if (tileentity == null) {
						tileentity = ((IContainer) block).a(world, l);
						world.setTileEntity(l1, j, i2, tileentity);
					}

					if (tileentity != null) {
						tileentity.u();
					}
				}

				n = true;
				return true;
			}
		}
	}

	public boolean a(int i, int j, int k, int l) {
		ChunkSection chunksection = sections[j >> 4];

		if (chunksection == null)
			return false;
		else {
			int i1 = chunksection.getData(i, j & 15, k);

			if (i1 == l)
				return false;
			else {
				n = true;
				Block block = chunksection.getTypeId(i, j & 15, k);
				chunksection.setData(i, j & 15, k, checkData(block, l));
				if (block instanceof IContainer) {
					TileEntity tileentity = this.e(i, j, k);

					if (tileentity != null) {
						tileentity.u();
						tileentity.g = l;
					}
				}

				return true;
			}
		}
	}

	public int getBrightness(EnumSkyBlock enumskyblock, int i, int j, int k) {
		ChunkSection chunksection = sections[j >> 4];

		return chunksection == null ? this.d(i, j, k) ? enumskyblock.c : 0 : enumskyblock == EnumSkyBlock.SKY ? world.worldProvider.g ? 0 : chunksection.getSkyLight(i, j & 15, k) : enumskyblock == EnumSkyBlock.BLOCK ? chunksection.getEmittedLight(i, j & 15, k) : enumskyblock.c;
	}

	public void a(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
		ChunkSection chunksection = sections[j >> 4];

		if (chunksection == null) {
			chunksection = sections[j >> 4] = new ChunkSection(j >> 4 << 4, !world.worldProvider.g);
			initLighting();
		}

		n = true;
		if (enumskyblock == EnumSkyBlock.SKY) {
			if (!world.worldProvider.g) {
				chunksection.setSkyLight(i, j & 15, k, l);
			}
		} else if (enumskyblock == EnumSkyBlock.BLOCK) {
			chunksection.setEmittedLight(i, j & 15, k, l);
		}
	}

	public int b(int i, int j, int k, int l) {
		ChunkSection chunksection = sections[j >> 4];

		if (chunksection == null)
			return !world.worldProvider.g && l < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - l : 0;
		else {
			int i1 = world.worldProvider.g ? 0 : chunksection.getSkyLight(i, j & 15, k);

			if (i1 > 0) {
				a = true;
			}

			i1 -= l;
			int j1 = chunksection.getEmittedLight(i, j & 15, k);

			if (j1 > i1) {
				i1 = j1;
			}

			return i1;
		}
	}

	public void a(Entity entity) {
		o = true;
		int i = MathHelper.floor(entity.locX / 16.0D);
		int j = MathHelper.floor(entity.locZ / 16.0D);

		if (i != locX || j != locZ) {
			// CraftBukkit start
			Bukkit.getLogger().warning("Wrong location for " + entity + " in world '" + world.getWorld().getName() + "'!");
			// t.warn("Wrong location! " + entity + " (at " + i + ", " + j + " instead of " + this.locX + ", " + this.locZ + ")");
			// Thread.dumpStack();
			Bukkit.getLogger().warning("Entity is at " + entity.locX + "," + entity.locZ + " (chunk " + i + "," + j + ") but was stored in chunk " + locX + "," + locZ);
			// CraftBukkit end
		}

		int k = MathHelper.floor(entity.locY / 16.0D);

		if (k < 0) {
			k = 0;
		}

		if (k >= entitySlices.length) {
			k = entitySlices.length - 1;
		}

		entity.ag = true;
		entity.ah = locX;
		entity.ai = k;
		entity.aj = locZ;
		entitySlices[k].add(entity);
		// Spigot start - increment creature type count
		// Keep this synced up with World.a(Class)
		if (entity instanceof EntityInsentient) {
			EntityInsentient entityinsentient = (EntityInsentient) entity;
			if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent())
				return;
		}
		for (EnumCreatureType creatureType : EnumCreatureType.values()) {
			if (creatureType.a().isAssignableFrom(entity.getClass())) {
				entityCount.adjustOrPutValue(creatureType.a(), 1, 1);
			}
		}
		// Spigot end
	}

	public void b(Entity entity) {
		this.a(entity, entity.ai);
	}

	public void a(Entity entity, int i) {
		if (i < 0) {
			i = 0;
		}

		if (i >= entitySlices.length) {
			i = entitySlices.length - 1;
		}

		entitySlices[i].remove(entity);
		// Spigot start - decrement creature type count
		// Keep this synced up with World.a(Class)
		if (entity instanceof EntityInsentient) {
			EntityInsentient entityinsentient = (EntityInsentient) entity;
			if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent())
				return;
		}
		for (EnumCreatureType creatureType : EnumCreatureType.values()) {
			if (creatureType.a().isAssignableFrom(entity.getClass())) {
				entityCount.adjustValue(creatureType.a(), -1);
			}
		}
		// Spigot end
	}

	public boolean d(int i, int j, int k) {
		return j >= heightMap[k << 4 | i];
	}

	public TileEntity e(int i, int j, int k) {
		ChunkPosition chunkposition = new ChunkPosition(i, j, k);
		TileEntity tileentity = (TileEntity) tileEntities.get(chunkposition);

		if (tileentity == null) {
			Block block = getType(i, j, k);

			if (!block.isTileEntity())
				return null;

			tileentity = ((IContainer) block).a(world, getData(i, j, k));
			world.setTileEntity(locX * 16 + i, j, locZ * 16 + k, tileentity);
		}

		if (tileentity != null && tileentity.r()) {
			tileEntities.remove(chunkposition);
			return null;
		} else
			return tileentity;
	}

	public void a(TileEntity tileentity) {
		int i = tileentity.x - locX * 16;
		int j = tileentity.y;
		int k = tileentity.z - locZ * 16;

		this.a(i, j, k, tileentity);
		if (d) {
			world.tileEntityList.add(tileentity);
		}
	}

	public void a(int i, int j, int k, TileEntity tileentity) {
		ChunkPosition chunkposition = new ChunkPosition(i, j, k);

		tileentity.a(world);
		tileentity.x = locX * 16 + i;
		tileentity.y = j;
		tileentity.z = locZ * 16 + k;
		if (getType(i, j, k) instanceof IContainer) {
			if (tileEntities.containsKey(chunkposition)) {
				((TileEntity) tileEntities.get(chunkposition)).s();
			}

			tileentity.t();
			tileEntities.put(chunkposition, tileentity);
			// Spigot start - The tile entity has a world, now hoppers can be born ticking.
			if (world.spigotConfig.altHopperTicking) {
				world.triggerHoppersList.add(tileentity);
			}
			// Spigot end
			// PaperSpigot start - Remove invalid mob spawner Tile Entities
		} else if (world.paperSpigotConfig.removeInvalidMobSpawnerTEs && tileentity instanceof TileEntityMobSpawner && org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(getType(i, j, k)) != org.bukkit.Material.MOB_SPAWNER) {
			tileEntities.remove(chunkposition);
			// PaperSpigot end
			// CraftBukkit start
		} else {
			System.out.println("Attempted to place a tile entity (" + tileentity + ") at " + tileentity.x + "," + tileentity.y + "," + tileentity.z + " (" + org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(getType(i, j, k)) + ") where there was no entity tile!");
			System.out.println("Chunk coordinates: " + locX * 16 + "," + locZ * 16);
			new Exception().printStackTrace();
			// CraftBukkit end
		}
	}

	public void f(int i, int j, int k) {
		ChunkPosition chunkposition = new ChunkPosition(i, j, k);

		if (d) {
			TileEntity tileentity = (TileEntity) tileEntities.remove(chunkposition);

			if (tileentity != null) {
				tileentity.s();
			}
		}
	}

	public void addEntities() {
		d = true;
		world.a(tileEntities.values());

		for (int i = 0; i < entitySlices.length; ++i) {
			Iterator iterator = entitySlices[i].iterator();

			while (iterator.hasNext()) {
				Entity entity = (Entity) iterator.next();

				entity.X();
			}

			world.a(entitySlices[i]);
		}
	}

	public void removeEntities() {
		d = false;
		Iterator iterator = tileEntities.values().iterator();

		while (iterator.hasNext()) {
			TileEntity tileentity = (TileEntity) iterator.next();
			// Spigot Start
			if (tileentity instanceof IInventory) {
				for (org.bukkit.entity.HumanEntity h : new ArrayList<org.bukkit.entity.HumanEntity>(((IInventory) tileentity).getViewers())) {
					if (h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity) {
						((org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory();
					}
				}
			}
			// Spigot End

			world.a(tileentity);
		}

		for (int i = 0; i < entitySlices.length; ++i) {
			// CraftBukkit start
			java.util.Iterator<Object> iter = entitySlices[i].iterator();
			while (iter.hasNext()) {
				Entity entity = (Entity) iter.next();
				// Spigot Start
				if (entity instanceof IInventory) {
					for (org.bukkit.entity.HumanEntity h : new ArrayList<org.bukkit.entity.HumanEntity>(((IInventory) entity).getViewers())) {
						if (h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity) {
							((org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory();
						}
					}
				}
				// Spigot End

				// Do not pass along players, as doing so can get them stuck outside of time.
				// (which for example disables inventory icon updates and prevents block breaking)
				if (entity instanceof EntityPlayer) {
					iter.remove();
				}
			}
			// CraftBukkit end

			world.b(entitySlices[i]);
		}
	}

	public void e() {
		n = true;
	}

	public void a(Entity entity, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {
		int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
		int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

		i = MathHelper.a(i, 0, entitySlices.length - 1);
		j = MathHelper.a(j, 0, entitySlices.length - 1);

		for (int k = i; k <= j; ++k) {
			List list1 = entitySlices[k];

			for (int l = 0; l < list1.size(); ++l) {
				Entity entity1 = (Entity) list1.get(l);

				if (entity1 != entity && entity1.boundingBox.b(axisalignedbb) && (ientityselector == null || ientityselector.a(entity1))) {
					list.add(entity1);
					Entity[] aentity = entity1.at();

					if (aentity != null) {
						for (int i1 = 0; i1 < aentity.length; ++i1) {
							entity1 = aentity[i1];
							if (entity1 != entity && entity1.boundingBox.b(axisalignedbb) && (ientityselector == null || ientityselector.a(entity1))) {
								list.add(entity1);
							}
						}
					}
				}
			}
		}
	}

	public void a(Class oclass, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {
		int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
		int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

		i = MathHelper.a(i, 0, entitySlices.length - 1);
		j = MathHelper.a(j, 0, entitySlices.length - 1);

		for (int k = i; k <= j; ++k) {
			List list1 = entitySlices[k];

			for (int l = 0; l < list1.size(); ++l) {
				Entity entity = (Entity) list1.get(l);

				if (oclass.isAssignableFrom(entity.getClass()) && entity.boundingBox.b(axisalignedbb) && (ientityselector == null || ientityselector.a(entity))) {
					list.add(entity);
				}
			}
		}
	}

	public boolean a(boolean flag) {
		if (flag) {
			if (o && world.getTime() != lastSaved || n)
				return true;
		} else if (o && world.getTime() >= lastSaved + 600L)
			return true;

		return n;
	}

	public Random a(long i) {
		return new Random(world.getSeed() + locX * locX * 4987142 + locX * 5947611 + locZ * locZ * 4392871L + locZ * 389711 ^ i);
	}

	public boolean isEmpty() {
		return false;
	}

	public void loadNearby(IChunkProvider ichunkprovider, IChunkProvider ichunkprovider1, int i, int j) {
		world.timings.syncChunkLoadPostTimer.startTiming(); // Spigot
		if (!done && ichunkprovider.isChunkLoaded(i + 1, j + 1) && ichunkprovider.isChunkLoaded(i, j + 1) && ichunkprovider.isChunkLoaded(i + 1, j)) {
			ichunkprovider.getChunkAt(ichunkprovider1, i, j);
		}

		if (ichunkprovider.isChunkLoaded(i - 1, j) && !ichunkprovider.getOrCreateChunk(i - 1, j).done && ichunkprovider.isChunkLoaded(i - 1, j + 1) && ichunkprovider.isChunkLoaded(i, j + 1) && ichunkprovider.isChunkLoaded(i - 1, j + 1)) {
			ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j);
		}

		if (ichunkprovider.isChunkLoaded(i, j - 1) && !ichunkprovider.getOrCreateChunk(i, j - 1).done && ichunkprovider.isChunkLoaded(i + 1, j - 1) && ichunkprovider.isChunkLoaded(i + 1, j - 1) && ichunkprovider.isChunkLoaded(i + 1, j)) {
			ichunkprovider.getChunkAt(ichunkprovider1, i, j - 1);
		}

		if (ichunkprovider.isChunkLoaded(i - 1, j - 1) && !ichunkprovider.getOrCreateChunk(i - 1, j - 1).done && ichunkprovider.isChunkLoaded(i, j - 1) && ichunkprovider.isChunkLoaded(i - 1, j)) {
			ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j - 1);
		}
		world.timings.syncChunkLoadPostTimer.stopTiming(); // Spigot
	}

	public int d(int i, int j) {
		int k = i | j << 4;
		int l = b[k];

		if (l == -999) {
			int i1 = this.h() + 15;

			l = -1;

			while (i1 > 0 && l == -1) {
				Block block = getType(i, i1, j);
				Material material = block.getMaterial();

				if (!material.isSolid() && !material.isLiquid()) {
					--i1;
				} else {
					l = i1 + 1;
				}
			}

			b[k] = l;
		}

		return l;
	}

	public void b(boolean flag) {
		if (w && !world.worldProvider.g && !flag) {
			this.c(world.isStatic);
		}

		m = true;
		if (!lit && done && world.spigotConfig.randomLightUpdates) { // Spigot - also use random light updates setting to determine if we should relight
			p();
		}
	}

	public boolean isReady() {
		// Spigot Start
		/*
		 * As of 1.7, Mojang added a check to make sure that only chunks which have been lit are sent to the client.
		 * Unfortunately this interferes with our modified chunk ticking algorithm, which will only tick chunks distant from the player on a very infrequent basis.
		 * We cannot unfortunately do this lighting stage during chunk gen as it appears to put a lot more noticeable load on the server, than when it is done at play time.
		 * For now at least we will simply send all chunks, in accordance with pre 1.7 behaviour.
		 */
		return true;
		// Spigot End
	}

	public ChunkCoordIntPair l() {
		return new ChunkCoordIntPair(locX, locZ);
	}

	public boolean c(int i, int j) {
		if (i < 0) {
			i = 0;
		}

		if (j >= 256) {
			j = 255;
		}

		for (int k = i; k <= j; k += 16) {
			ChunkSection chunksection = sections[k >> 4];

			if (chunksection != null && !chunksection.isEmpty())
				return false;
		}

		return true;
	}

	public void a(ChunkSection[] achunksection) {
		sections = achunksection;
	}

	public BiomeBase getBiome(int i, int j, WorldChunkManager worldchunkmanager) {
		int k = v[j << 4 | i] & 255;

		if (k == 255) {
			BiomeBase biomebase = worldchunkmanager.getBiome((locX << 4) + i, (locZ << 4) + j);

			k = biomebase.id;
			v[j << 4 | i] = (byte) (k & 255);
		}

		return BiomeBase.getBiome(k) == null ? BiomeBase.PLAINS : BiomeBase.getBiome(k);
	}

	public byte[] m() {
		return v;
	}

	public void a(byte[] abyte) {
		v = abyte;
	}

	public void n() {
		x = 0;
	}

	public void o() {
		for (int i = 0; i < 8; ++i) {
			if (x >= 4096)
				return;

			int j = x % 16;
			int k = x / 16 % 16;
			int l = x / 256;

			++x;
			int i1 = (locX << 4) + k;
			int j1 = (locZ << 4) + l;

			for (int k1 = 0; k1 < 16; ++k1) {
				int l1 = (j << 4) + k1;

				if (sections[j] == null && (k1 == 0 || k1 == 15 || k == 0 || k == 15 || l == 0 || l == 15) || sections[j] != null && sections[j].getTypeId(k, k1, l).getMaterial() == Material.AIR) {
					if (world.getType(i1, l1 - 1, j1).m() > 0) {
						world.t(i1, l1 - 1, j1);
					}

					if (world.getType(i1, l1 + 1, j1).m() > 0) {
						world.t(i1, l1 + 1, j1);
					}

					if (world.getType(i1 - 1, l1, j1).m() > 0) {
						world.t(i1 - 1, l1, j1);
					}

					if (world.getType(i1 + 1, l1, j1).m() > 0) {
						world.t(i1 + 1, l1, j1);
					}

					if (world.getType(i1, l1, j1 - 1).m() > 0) {
						world.t(i1, l1, j1 - 1);
					}

					if (world.getType(i1, l1, j1 + 1).m() > 0) {
						world.t(i1, l1, j1 + 1);
					}

					world.t(i1, l1, j1);
				}
			}
		}
	}

	public void p() {
		done = true;
		lit = true;
		if (!world.worldProvider.g) {
			if (world.b(locX * 16 - 1, 0, locZ * 16 - 1, locX * 16 + 1, 63, locZ * 16 + 1)) {
				for (int i = 0; i < 16; ++i) {
					for (int j = 0; j < 16; ++j) {
						if (!this.f(i, j)) {
							lit = false;
							break;
						}
					}
				}

				if (lit) {
					Chunk chunk = world.getChunkAtWorldCoords(locX * 16 - 1, locZ * 16);

					chunk.a(3);
					chunk = world.getChunkAtWorldCoords(locX * 16 + 16, locZ * 16);
					chunk.a(1);
					chunk = world.getChunkAtWorldCoords(locX * 16, locZ * 16 - 1);
					chunk.a(0);
					chunk = world.getChunkAtWorldCoords(locX * 16, locZ * 16 + 16);
					chunk.a(2);
				}
			} else {
				lit = false;
			}
		}
	}

	private void a(int i) {
		if (done) {
			int j;

			if (i == 3) {
				for (j = 0; j < 16; ++j) {
					this.f(15, j);
				}
			} else if (i == 1) {
				for (j = 0; j < 16; ++j) {
					this.f(0, j);
				}
			} else if (i == 0) {
				for (j = 0; j < 16; ++j) {
					this.f(j, 15);
				}
			} else if (i == 2) {
				for (j = 0; j < 16; ++j) {
					this.f(j, 0);
				}
			}
		}
	}

	private boolean f(int i, int j) {
		int k = this.h();
		boolean flag = false;
		boolean flag1 = false;

		int l;

		for (l = k + 16 - 1; l > 63 || l > 0 && !flag1; --l) {
			int i1 = this.b(i, l, j);

			if (i1 == 255 && l < 63) {
				flag1 = true;
			}

			if (!flag && i1 > 0) {
				flag = true;
			} else if (flag && i1 == 0 && !world.t(locX * 16 + i, l, locZ * 16 + j))
				return false;
		}

		for (; l > 0; --l) {
			if (getType(i, l, j).m() > 0) {
				world.t(locX * 16 + i, l, locZ * 16 + j);
			}
		}

		return true;
	}
}
