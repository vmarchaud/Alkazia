package net.minecraft.server;

import java.util.Random;

public class BlockDispenser extends BlockContainer {

	public static final IRegistry a = new RegistryDefault(new DispenseBehaviorItem());
	protected Random b = new Random();
	public static boolean eventFired = false; // CraftBukkit

	protected BlockDispenser() {
		super(Material.STONE);
		this.a(CreativeModeTab.d);
	}

	@Override
	public int a(World world) {
		return 4;
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
		super.onPlace(world, i, j, k);
		this.m(world, i, j, k);
	}

	private void m(World world, int i, int j, int k) {
		if (!world.isStatic) {
			Block block = world.getType(i, j, k - 1);
			Block block1 = world.getType(i, j, k + 1);
			Block block2 = world.getType(i - 1, j, k);
			Block block3 = world.getType(i + 1, j, k);
			byte b0 = 3;

			if (block.j() && !block1.j()) {
				b0 = 3;
			}

			if (block1.j() && !block.j()) {
				b0 = 2;
			}

			if (block2.j() && !block3.j()) {
				b0 = 5;
			}

			if (block3.j() && !block2.j()) {
				b0 = 4;
			}

			world.setData(i, j, k, b0, 2);
		}
	}

	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		if (world.isStatic)
			return true;
		else {
			TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getTileEntity(i, j, k);

			if (tileentitydispenser != null) {
				entityhuman.openDispenser(tileentitydispenser);
			}

			return true;
		}
	}

	// CraftBukkit - protected -> public
	public void dispense(World world, int i, int j, int k) {
		SourceBlock sourceblock = new SourceBlock(world, i, j, k);
		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getTileEntity();

		if (tileentitydispenser != null) {
			int l = tileentitydispenser.i();

			if (l < 0) {
				world.triggerEffect(1001, i, j, k, 0);
			} else {
				ItemStack itemstack = tileentitydispenser.getItem(l);
				IDispenseBehavior idispensebehavior = this.a(itemstack);

				if (idispensebehavior != IDispenseBehavior.a) {
					ItemStack itemstack1 = idispensebehavior.a(sourceblock, itemstack);
					eventFired = false; // CraftBukkit - reset event status

					tileentitydispenser.setItem(l, itemstack1.count == 0 ? null : itemstack1);
				}
			}
		}
	}

	protected IDispenseBehavior a(ItemStack itemstack) {
		return (IDispenseBehavior) a.get(itemstack.getItem());
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		boolean flag = world.isBlockIndirectlyPowered(i, j, k) || world.isBlockIndirectlyPowered(i, j + 1, k);
		int l = world.getData(i, j, k);
		boolean flag1 = (l & 8) != 0;

		if (flag && !flag1) {
			world.a(i, j, k, this, this.a(world));
			world.setData(i, j, k, l | 8, 4);
		} else if (!flag && flag1) {
			world.setData(i, j, k, l & -9, 4);
		}
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		if (!world.isStatic) {
			dispense(world, i, j, k);
		}
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityDispenser();
	}

	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
		int l = BlockPiston.a(world, i, j, k, entityliving);

		world.setData(i, j, k, l, 2);
		if (itemstack.hasName()) {
			((TileEntityDispenser) world.getTileEntity(i, j, k)).a(itemstack.getName());
		}
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getTileEntity(i, j, k);

		if (tileentitydispenser != null) {
			for (int i1 = 0; i1 < tileentitydispenser.getSize(); ++i1) {
				ItemStack itemstack = tileentitydispenser.getItem(i1);

				if (itemstack != null) {
					float f = b.nextFloat() * 0.8F + 0.1F;
					float f1 = b.nextFloat() * 0.8F + 0.1F;
					float f2 = b.nextFloat() * 0.8F + 0.1F;

					while (itemstack.count > 0) {
						int j1 = b.nextInt(21) + 10;

						if (j1 > itemstack.count) {
							j1 = itemstack.count;
						}

						itemstack.count -= j1;
						EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getData()));

						if (itemstack.hasTag()) {
							entityitem.getItemStack().setTag((NBTTagCompound) itemstack.getTag().clone());
						}

						float f3 = 0.05F;

						entityitem.motX = (float) b.nextGaussian() * f3;
						entityitem.motY = (float) b.nextGaussian() * f3 + 0.2F;
						entityitem.motZ = (float) b.nextGaussian() * f3;
						world.addEntity(entityitem);
					}
				}
			}

			world.updateAdjacentComparators(i, j, k, block);
		}

		super.remove(world, i, j, k, block, l);
	}

	public static IPosition a(ISourceBlock isourceblock) {
		EnumFacing enumfacing = b(isourceblock.h());
		double d0 = isourceblock.getX() + 0.7D * enumfacing.getAdjacentX();
		double d1 = isourceblock.getY() + 0.7D * enumfacing.getAdjacentY();
		double d2 = isourceblock.getZ() + 0.7D * enumfacing.getAdjacentZ();

		return new Position(d0, d1, d2);
	}

	public static EnumFacing b(int i) {
		return EnumFacing.a(i & 7);
	}

	@Override
	public boolean isComplexRedstone() {
		return true;
	}

	@Override
	public int g(World world, int i, int j, int k, int l) {
		return Container.b((IInventory) world.getTileEntity(i, j, k));
	}
}
