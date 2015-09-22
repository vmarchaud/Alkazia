package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;

// CraftBukkit end

public class BlockFlowing extends BlockFluids {

	int a;
	boolean[] b = new boolean[4];
	int[] M = new int[4];

	protected BlockFlowing(Material material) {
		super(material);
	}

	private void n(World world, int i, int j, int k) {
		int l = world.getData(i, j, k);

		world.setTypeAndData(i, j, k, Block.getById(Block.getId(this) + 1), l, 2);
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		// CraftBukkit start
		org.bukkit.World bworld = world.getWorld();
		org.bukkit.Server server = world.getServer();
		org.bukkit.block.Block source = bworld == null ? null : bworld.getBlockAt(i, j, k);
		// CraftBukkit end

		int l = this.e(world, i, j, k);
		byte b0 = 1;

		if (material == Material.LAVA && !world.worldProvider.f) {
			b0 = 2;
		}

		boolean flag = true;
		int i1 = getFlowSpeed(world, i, j, k); // PaperSpigot
		int j1;

		if (l > 0) {
			byte b1 = -100;

			a = 0;
			int k1 = this.a(world, i - 1, j, k, b1);

			k1 = this.a(world, i + 1, j, k, k1);
			k1 = this.a(world, i, j, k - 1, k1);
			k1 = this.a(world, i, j, k + 1, k1);
			j1 = k1 + b0;
			if (j1 >= 8 || k1 < 0) {
				j1 = -1;
			}

			if (this.e(world, i, j + 1, k) >= 0) {
				int l1 = this.e(world, i, j + 1, k);

				if (l1 >= 8) {
					j1 = l1;
				} else {
					j1 = l1 + 8;
				}
			}

			if (a >= 2 && material == Material.WATER) {
				if (world.getType(i, j - 1, k).getMaterial().isBuildable()) {
					j1 = 0;
				} else if (world.getType(i, j - 1, k).getMaterial() == material && world.getData(i, j - 1, k) == 0) {
					j1 = 0;
				}
			}

			if (material == Material.LAVA && l < 8 && j1 < 8 && j1 > l && random.nextInt(4) != 0) {
				i1 *= 4;
			}

			if (j1 == l) {
				if (flag) {
					this.n(world, i, j, k);
				}
			} else {
				l = j1;
				if (j1 < 0) {
					world.setAir(i, j, k);
				} else {
					world.setData(i, j, k, j1, 2);
					world.a(i, j, k, this, i1);
					world.applyPhysics(i, j, k, this);
				}
			}
		} else {
			this.n(world, i, j, k);
		}

		if (q(world, i, j - 1, k)) {
			// CraftBukkit start - Send "down" to the server
			BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
			if (server != null) {
				server.getPluginManager().callEvent(event);
			}

			if (!event.isCancelled()) {
				if (material == Material.LAVA && world.getType(i, j - 1, k).getMaterial() == Material.WATER) {
					world.setTypeUpdate(i, j - 1, k, Blocks.STONE);
					fizz(world, i, j - 1, k);
					return;
				}

				if (l >= 8) {
					flow(world, i, j - 1, k, l);
				} else {
					flow(world, i, j - 1, k, l + 8);
				}
			}
			// CraftBukkit end
		} else if (l >= 0 && (l == 0 || this.p(world, i, j - 1, k))) {
			boolean[] aboolean = o(world, i, j, k);

			j1 = l + b0;
			if (l >= 8) {
				j1 = 1;
			}

			if (j1 >= 8)
				return;

			// CraftBukkit start - All four cardinal directions. Do not change the order!
			BlockFace[] faces = new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH };
			int index = 0;

			for (BlockFace currentFace : faces) {
				if (aboolean[index]) {
					BlockFromToEvent event = new BlockFromToEvent(source, currentFace);

					if (server != null) {
						server.getPluginManager().callEvent(event);
					}

					if (!event.isCancelled()) {
						flow(world, i + currentFace.getModX(), j, k + currentFace.getModZ(), j1);
					}
				}
				index++;
			}
			// CraftBukkit end
		}
	}

	private void flow(World world, int i, int j, int k, int l) {
		if (q(world, i, j, k)) {
			Block block = world.getType(i, j, k);

			if (material == Material.LAVA) {
				fizz(world, i, j, k);
			} else {
				block.b(world, i, j, k, world.getData(i, j, k), 0);
			}

			world.setTypeAndData(i, j, k, this, l, 3);
		}
	}

	private int c(World world, int i, int j, int k, int l, int i1) {
		int j1 = 1000;

		for (int k1 = 0; k1 < 4; ++k1) {
			if ((k1 != 0 || i1 != 1) && (k1 != 1 || i1 != 0) && (k1 != 2 || i1 != 3) && (k1 != 3 || i1 != 2)) {
				int l1 = i;
				int i2 = k;

				if (k1 == 0) {
					l1 = i - 1;
				}

				if (k1 == 1) {
					++l1;
				}

				if (k1 == 2) {
					i2 = k - 1;
				}

				if (k1 == 3) {
					++i2;
				}

				if (!this.p(world, l1, j, i2) && (world.getType(l1, j, i2).getMaterial() != material || world.getData(l1, j, i2) != 0)) {
					if (!this.p(world, l1, j - 1, i2))
						return l;

					if (l < 4) {
						int j2 = this.c(world, l1, j, i2, l + 1, k1);

						if (j2 < j1) {
							j1 = j2;
						}
					}
				}
			}
		}

		return j1;
	}

	private boolean[] o(World world, int i, int j, int k) {
		int l;
		int i1;

		for (l = 0; l < 4; ++l) {
			M[l] = 1000;
			i1 = i;
			int j1 = k;

			if (l == 0) {
				i1 = i - 1;
			}

			if (l == 1) {
				++i1;
			}

			if (l == 2) {
				j1 = k - 1;
			}

			if (l == 3) {
				++j1;
			}

			if (!this.p(world, i1, j, j1) && (world.getType(i1, j, j1).getMaterial() != material || world.getData(i1, j, j1) != 0)) {
				if (this.p(world, i1, j - 1, j1)) {
					M[l] = this.c(world, i1, j, j1, 1, l);
				} else {
					M[l] = 0;
				}
			}
		}

		l = M[0];

		for (i1 = 1; i1 < 4; ++i1) {
			if (M[i1] < l) {
				l = M[i1];
			}
		}

		for (i1 = 0; i1 < 4; ++i1) {
			b[i1] = M[i1] == l;
		}

		return b;
	}

	private boolean p(World world, int i, int j, int k) {
		Block block = world.getType(i, j, k);

		return block != Blocks.WOODEN_DOOR && block != Blocks.IRON_DOOR_BLOCK && block != Blocks.SIGN_POST && block != Blocks.LADDER && block != Blocks.SUGAR_CANE_BLOCK ? block.material == Material.PORTAL ? true : block.material.isSolid() : true;
	}

	protected int a(World world, int i, int j, int k, int l) {
		int i1 = this.e(world, i, j, k);

		if (i1 < 0)
			return l;
		else {
			if (i1 == 0) {
				++a;
			}

			if (i1 >= 8) {
				i1 = 0;
			}

			return l >= 0 && i1 >= l ? l : i1;
		}
	}

	private boolean q(World world, int i, int j, int k) {
		Material material = world.getType(i, j, k).getMaterial();

		return material == this.material ? false : material == Material.LAVA ? false : !this.p(world, i, j, k);
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
		super.onPlace(world, i, j, k);
		if (world.getType(i, j, k) == this) {
			world.a(i, j, k, this, getFlowSpeed(world, i, j, k)); // PaperSpigot
		}
	}

	@Override
	public boolean L() {
		return true;
	}

	/**
	 * PaperSpigot - Get flow speed. Throttle if its water and flowing adjacent to lava
	 */
	public int getFlowSpeed(World world, int x, int y, int z) {
		if (getMaterial() == Material.WATER && (world.getType(x, y, z - 1).getMaterial() == Material.LAVA || world.getType(x, y, z + 1).getMaterial() == Material.LAVA || world.getType(x - 1, y, z).getMaterial() == Material.LAVA || world.getType(x + 1, y, z).getMaterial() == Material.LAVA))
			 return world.paperSpigotConfig.waterOverLavaFlowSpeed;
		return super.a(world);
	}
}
