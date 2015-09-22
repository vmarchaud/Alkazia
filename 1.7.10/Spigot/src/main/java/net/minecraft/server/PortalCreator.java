package net.minecraft.server;

import org.bukkit.event.world.PortalCreateEvent; // CraftBukkit

public class PortalCreator {

	private final World a;
	private final int b;
	private final int c;
	private final int d;
	private int e = 0;
	private ChunkCoordinates f;
	private int g;
	private int h;
	java.util.Collection<org.bukkit.block.Block> blocks = new java.util.HashSet<org.bukkit.block.Block>(); // CraftBukkit - add field

	public PortalCreator(World world, int i, int j, int k, int l) {
		a = world;
		b = l;
		d = BlockPortal.a[l][0];
		c = BlockPortal.a[l][1];

		for (int i1 = j; j > i1 - 21 && j > 0 && this.a(world.getType(i, j - 1, k)); --j) {
			;
		}

		int j1 = this.a(i, j, k, d) - 1;

		if (j1 >= 0) {
			f = new ChunkCoordinates(i + j1 * Direction.a[d], j, k + j1 * Direction.b[d]);
			h = this.a(f.x, f.y, f.z, c);
			if (h < 2 || h > 21) {
				f = null;
				h = 0;
			}
		}

		if (f != null) {
			g = this.a();
		}
	}

	protected int a(int i, int j, int k, int l) {
		int i1 = Direction.a[l];
		int j1 = Direction.b[l];

		int k1;
		Block block;

		for (k1 = 0; k1 < 22; ++k1) {
			block = a.getType(i + i1 * k1, j, k + j1 * k1);
			if (!this.a(block)) {
				break;
			}

			Block block1 = a.getType(i + i1 * k1, j - 1, k + j1 * k1);

			if (block1 != Blocks.OBSIDIAN) {
				break;
			}
		}

		block = a.getType(i + i1 * k1, j, k + j1 * k1);
		return block == Blocks.OBSIDIAN ? k1 : 0;
	}

	protected int a() {
		// CraftBukkit start
		blocks.clear();
		org.bukkit.World bworld = a.getWorld();
		// CraftBukkit end
		int i;
		int j;
		int k;
		int l;

		label56: for (g = 0; g < 21; ++g) {
			i = f.y + g;

			for (j = 0; j < h; ++j) {
				k = f.x + j * Direction.a[BlockPortal.a[b][1]];
				l = f.z + j * Direction.b[BlockPortal.a[b][1]];
				Block block = a.getType(k, i, l);

				if (!this.a(block)) {
					break label56;
				}

				if (block == Blocks.PORTAL) {
					++e;
				}

				if (j == 0) {
					block = a.getType(k + Direction.a[BlockPortal.a[b][0]], i, l + Direction.b[BlockPortal.a[b][0]]);
					if (block != Blocks.OBSIDIAN) {
						break label56;
						// CraftBukkit start - add the block to our list
					} else {
						blocks.add(bworld.getBlockAt(k + Direction.a[BlockPortal.a[b][0]], i, l + Direction.b[BlockPortal.a[b][0]]));
						// CraftBukkit end
					}
				} else if (j == h - 1) {
					block = a.getType(k + Direction.a[BlockPortal.a[b][1]], i, l + Direction.b[BlockPortal.a[b][1]]);
					if (block != Blocks.OBSIDIAN) {
						break label56;
						// CraftBukkit start - add the block to our list
					} else {
						blocks.add(bworld.getBlockAt(k + Direction.a[BlockPortal.a[b][1]], i, l + Direction.b[BlockPortal.a[b][1]]));
						// CraftBukkit end
					}
				}
			}
		}

		for (i = 0; i < h; ++i) {
			j = f.x + i * Direction.a[BlockPortal.a[b][1]];
			k = f.y + g;
			l = f.z + i * Direction.b[BlockPortal.a[b][1]];
			if (a.getType(j, k, l) != Blocks.OBSIDIAN) {
				g = 0;
				break;
				// CraftBukkit start - add the block to our list
			} else {
				blocks.add(bworld.getBlockAt(j, k, l));
				// CraftBukkit end
			}
		}

		if (g <= 21 && g >= 3)
			return g;
		else {
			f = null;
			h = 0;
			g = 0;
			return 0;
		}
	}

	protected boolean a(Block block) {
		return block.material == Material.AIR || block == Blocks.FIRE || block == Blocks.PORTAL;
	}

	public boolean b() {
		return f != null && h >= 2 && h <= 21 && g >= 3 && g <= 21;
	}

	// CraftBukkit start - return boolean
	public boolean c() {
		org.bukkit.World bworld = a.getWorld();

		// Copy below for loop
		for (int i = 0; i < h; ++i) {
			int j = f.x + Direction.a[c] * i;
			int k = f.z + Direction.b[c] * i;

			for (int l = 0; l < g; ++l) {
				int i1 = f.y + l;

				blocks.add(bworld.getBlockAt(j, i1, k));
			}
		}

		PortalCreateEvent event = new PortalCreateEvent(blocks, bworld, PortalCreateEvent.CreateReason.FIRE);
		a.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return false;

		for (int i = 0; i < h; ++i) {
			int j = f.x + Direction.a[c] * i;
			int k = f.z + Direction.b[c] * i;

			for (int l = 0; l < g; ++l) {
				int i1 = f.y + l;

				a.setTypeAndData(j, i1, k, Blocks.PORTAL, b, 2);
			}
		}

		return true; // CraftBukkit
	}

	static int a(PortalCreator portalcreator) {
		return portalcreator.e;
	}

	static int b(PortalCreator portalcreator) {
		return portalcreator.h;
	}

	static int c(PortalCreator portalcreator) {
		return portalcreator.g;
	}
}
