package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockPistonExtendEvent;
// CraftBukkit end
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockPiston extends Block {

	private final boolean a;

	public BlockPiston(boolean flag) {
		super(Material.PISTON);
		a = flag;
		this.setSound(i);
		this.setHardness(0.5F);
		this.a(CreativeModeTab.d);
	}

	@Override
	public int b() {
		return 16;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		return false;
	}

	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
		int l = a(world, i, j, k, entityliving);

		world.setData(i, j, k, l, 2);
		if (!world.isStatic) {
			e(world, i, j, k);
		}
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		if (!world.isStatic) {
			e(world, i, j, k);
		}
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
		if (!world.isStatic && world.getTileEntity(i, j, k) == null) {
			e(world, i, j, k);
		}
	}

	private void e(World world, int i, int j, int k) {
		int l = world.getData(i, j, k);
		int i1 = b(l);

		if (i1 != 7) {
			boolean flag = this.a(world, i, j, k, i1);

			if (flag && !c(l)) {
				// CraftBukkit start
				int length = h(world, i, j, k, i1);
				if (length >= 0) {
					org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
					BlockPistonExtendEvent event = new BlockPistonExtendEvent(block, length, CraftBlock.notchToBlockFace(i1));
					world.getServer().getPluginManager().callEvent(event);

					if (event.isCancelled())
						return;

					world.playBlockAction(i, j, k, this, 0, i1);
				}
			} else if (!flag && c(l)) {
				// CraftBukkit start
				org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
				BlockPistonRetractEvent event = new BlockPistonRetractEvent(block, CraftBlock.notchToBlockFace(i1));
				world.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled())
					return;

				world.setData(i, j, k, i1, 2);
				world.playBlockAction(i, j, k, this, 1, i1);
			}
		}
	}

	private boolean a(World world, int i, int j, int k, int l) {
		return l != 0 && world.isBlockFacePowered(i, j - 1, k, 0) ? true : l != 1 && world.isBlockFacePowered(i, j + 1, k, 1) ? true : l != 2 && world.isBlockFacePowered(i, j, k - 1, 2) ? true : l != 3 && world.isBlockFacePowered(i, j, k + 1, 3) ? true : l != 5 && world.isBlockFacePowered(i + 1, j, k, 5) ? true : l != 4 && world.isBlockFacePowered(i - 1, j, k, 4) ? true : world
				.isBlockFacePowered(i, j, k, 0) ? true : world.isBlockFacePowered(i, j + 2, k, 1) ? true : world.isBlockFacePowered(i, j + 1, k - 1, 2) ? true : world.isBlockFacePowered(i, j + 1, k + 1, 3) ? true : world.isBlockFacePowered(i - 1, j + 1, k, 4) ? true : world.isBlockFacePowered(i + 1, j + 1, k, 5);
	}

	@Override
	public boolean a(World world, int i, int j, int k, int l, int i1) {
		if (!world.isStatic) {
			boolean flag = this.a(world, i, j, k, i1);

			if (flag && l == 1) {
				world.setData(i, j, k, i1 | 8, 2);
				return false;
			}

			if (!flag && l == 0)
				return false;
		}

		if (l == 0) {
			if (!i(world, i, j, k, i1))
				return false;

			world.setData(i, j, k, i1 | 8, 2);
			world.makeSound(i + 0.5D, j + 0.5D, k + 0.5D, "tile.piston.out", 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (l == 1) {
			TileEntity tileentity = world.getTileEntity(i + Facing.b[i1], j + Facing.c[i1], k + Facing.d[i1]);

			if (tileentity instanceof TileEntityPiston) {
				((TileEntityPiston) tileentity).f();
			}

			world.setTypeAndData(i, j, k, Blocks.PISTON_MOVING, i1, 3);
			world.setTileEntity(i, j, k, BlockPistonMoving.a(this, i1, i1, false, true));
			if (a) {
				int j1 = i + Facing.b[i1] * 2;
				int k1 = j + Facing.c[i1] * 2;
				int l1 = k + Facing.d[i1] * 2;
				Block block = world.getType(j1, k1, l1);
				int i2 = world.getData(j1, k1, l1);
				boolean flag1 = false;

				if (block == Blocks.PISTON_MOVING) {
					TileEntity tileentity1 = world.getTileEntity(j1, k1, l1);

					if (tileentity1 instanceof TileEntityPiston) {
						TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

						if (tileentitypiston.c() == i1 && tileentitypiston.b()) {
							tileentitypiston.f();
							block = tileentitypiston.a();
							i2 = tileentitypiston.p();
							flag1 = true;
						}
					}
				}

				if (!flag1 && block.getMaterial() != Material.AIR && a(block, world, j1, k1, l1, false) && (block.h() == 0 || block == Blocks.PISTON || block == Blocks.PISTON_STICKY)) {
					i += Facing.b[i1];
					j += Facing.c[i1];
					k += Facing.d[i1];
					world.setTypeAndData(i, j, k, Blocks.PISTON_MOVING, i2, 3);
					world.setTileEntity(i, j, k, BlockPistonMoving.a(block, i2, i1, false, false));
					world.setAir(j1, k1, l1);
				} else if (!flag1) {
					world.setAir(i + Facing.b[i1], j + Facing.c[i1], k + Facing.d[i1]);
				}
			} else {
				world.setAir(i + Facing.b[i1], j + Facing.c[i1], k + Facing.d[i1]);
			}

			world.makeSound(i + 0.5D, j + 0.5D, k + 0.5D, "tile.piston.in", 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k);

		if (c(l)) {
			float f = 0.25F;

			switch (b(l)) {
			case 0:
				this.setBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;

			case 1:
				this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
				break;

			case 2:
				this.setBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
				break;

			case 3:
				this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
				break;

			case 4:
				this.setBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				break;

			case 5:
				this.setBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
			}
		} else {
			this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public void g() {
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.a(world, i, j, k, axisalignedbb, list, entity);
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		updateShape(world, i, j, k);
		return super.a(world, i, j, k);
	}

	@Override
	public boolean d() {
		return false;
	}

	public static int b(int i) {
		if ((i & 7) >= Facing.OPPOSITE_FACING.length)
			return 7; // CraftBukkit - check for AIOOB on piston data
		return i & 7;
	}

	public static boolean c(int i) {
		return (i & 8) != 0;
	}

	public static int a(World world, int i, int j, int k, EntityLiving entityliving) {
		if (MathHelper.abs((float) entityliving.locX - i) < 2.0F && MathHelper.abs((float) entityliving.locZ - k) < 2.0F) {
			double d0 = entityliving.locY + 1.82D - entityliving.height;

			if (d0 - j > 2.0D)
				return 1;

			if (j - d0 > 0.0D)
				return 0;
		}

		int l = MathHelper.floor(entityliving.yaw * 4.0F / 360.0F + 0.5D) & 3;

		return l == 0 ? 2 : l == 1 ? 5 : l == 2 ? 3 : l == 3 ? 4 : 0;
	}

	private static boolean a(Block block, World world, int i, int j, int k, boolean flag) {
		if (block == Blocks.OBSIDIAN)
			return false;
		else {
			if (block != Blocks.PISTON && block != Blocks.PISTON_STICKY) {
				if (block.f(world, i, j, k) == -1.0F)
					return false;

				if (block.h() == 2)
					return false;

				if (block.h() == 1) {
					if (!flag)
						return false;

					return true;
				}
			} else if (c(world.getData(i, j, k)))
				return false;

			return !(block instanceof IContainer);
		}
	}

	// CraftBukkit - boolean -> int return
	private static int h(World world, int i, int j, int k, int l) {
		int i1 = i + Facing.b[l];
		int j1 = j + Facing.c[l];
		int k1 = k + Facing.d[l];
		int l1 = 0;

		while (true) {
			if (l1 < 13) {
				if (j1 <= 0 || j1 >= 255)
					return -1; // CraftBukkit

				Block block = world.getType(i1, j1, k1);

				if (block.getMaterial() != Material.AIR) {
					if (!a(block, world, i1, j1, k1, true))
						return -1; // CraftBukkit

					if (block.h() != 1) {
						if (l1 == 12)
							return -1; // CraftBukkit

						i1 += Facing.b[l];
						j1 += Facing.c[l];
						k1 += Facing.d[l];
						++l1;
						continue;
					}
				}
			}

			return l1; // CraftBukkit
		}
	}

	private boolean i(World world, int i, int j, int k, int l) {
		int i1 = i + Facing.b[l];
		int j1 = j + Facing.c[l];
		int k1 = k + Facing.d[l];
		int l1 = 0;

		while (true) {
			if (l1 < 13) {
				if (j1 <= 0 || j1 >= 255)
							return false;

				Block block = world.getType(i1, j1, k1);

				if (block.getMaterial() != Material.AIR) {
					if (!a(block, world, i1, j1, k1, true))
								return false;

					if (block.h() != 1) {
						if (l1 == 12)
									return false;

						i1 += Facing.b[l];
						j1 += Facing.c[l];
						k1 += Facing.d[l];
						++l1;
						continue;
					}

					block.b(world, i1, j1, k1, world.getData(i1, j1, k1), 0);
					world.setAir(i1, j1, k1);
				}
			}

			l1 = i1;
			int i2 = j1;
			int j2 = k1;
			int k2 = 0;

			Block[] ablock;
			int l2;
			int i3;
			int j3;

			for (ablock = new Block[13]; i1 != i || j1 != j || k1 != k; k1 = j3) {
				l2 = i1 - Facing.b[l];
				i3 = j1 - Facing.c[l];
				j3 = k1 - Facing.d[l];
				Block block1 = world.getType(l2, i3, j3);
				int k3 = world.getData(l2, i3, j3);

				if (block1 == this && l2 == i && i3 == j && j3 == k) {
					world.setTypeAndData(i1, j1, k1, Blocks.PISTON_MOVING, l | (a ? 8 : 0), 4);
					world.setTileEntity(i1, j1, k1, BlockPistonMoving.a(Blocks.PISTON_EXTENSION, l | (a ? 8 : 0), l, true, false));
				} else {
					world.setTypeAndData(i1, j1, k1, Blocks.PISTON_MOVING, k3, 4);
					world.setTileEntity(i1, j1, k1, BlockPistonMoving.a(block1, k3, l, true, false));
				}

				ablock[k2++] = block1;
				i1 = l2;
				j1 = i3;
			}

			i1 = l1;
			j1 = i2;
			k1 = j2;

			for (k2 = 0; i1 != i || j1 != j || k1 != k; k1 = j3) {
				l2 = i1 - Facing.b[l];
				i3 = j1 - Facing.c[l];
				j3 = k1 - Facing.d[l];
				world.applyPhysics(l2, i3, j3, ablock[k2++]);
				i1 = l2;
				j1 = i3;
			}

			return true;
		}
	}
}
