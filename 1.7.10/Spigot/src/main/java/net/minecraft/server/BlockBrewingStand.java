package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockBrewingStand extends BlockContainer {

	private Random a = new Random();

	public BlockBrewingStand() {
		super(Material.ORE);
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public int b() {
		return 25;
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityBrewingStand();
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		this.setBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
		super.a(world, i, j, k, axisalignedbb, list, entity);
		this.g();
		super.a(world, i, j, k, axisalignedbb, list, entity);
	}

	@Override
	public void g() {
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		if (world.isStatic)
			return true;
		else {
			TileEntityBrewingStand tileentitybrewingstand = (TileEntityBrewingStand) world.getTileEntity(i, j, k);

			if (tileentitybrewingstand != null) {
				entityhuman.openBrewingStand(tileentitybrewingstand);
			}

			return true;
		}
	}

	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
		if (itemstack.hasName()) {
			((TileEntityBrewingStand) world.getTileEntity(i, j, k)).a(itemstack.getName());
		}
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		TileEntity tileentity = world.getTileEntity(i, j, k);

		if (tileentity instanceof TileEntityBrewingStand) {
			TileEntityBrewingStand tileentitybrewingstand = (TileEntityBrewingStand) tileentity;

			for (int i1 = 0; i1 < tileentitybrewingstand.getSize(); ++i1) {
				ItemStack itemstack = tileentitybrewingstand.getItem(i1);

				if (itemstack != null) {
					float f = a.nextFloat() * 0.8F + 0.1F;
					float f1 = a.nextFloat() * 0.8F + 0.1F;
					float f2 = a.nextFloat() * 0.8F + 0.1F;

					while (itemstack.count > 0) {
						int j1 = a.nextInt(21) + 10;

						if (j1 > itemstack.count) {
							j1 = itemstack.count;
						}

						itemstack.count -= j1;
						EntityItem entityitem = new EntityItem(world, i + f, j + f1, k + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getData()));
						float f3 = 0.05F;

						entityitem.motX = (float) a.nextGaussian() * f3;
						entityitem.motY = (float) a.nextGaussian() * f3 + 0.2F;
						entityitem.motZ = (float) a.nextGaussian() * f3;
						// Spigot Start
						if (itemstack.hasTag()) {
							entityitem.getItemStack().setTag((NBTTagCompound) itemstack.getTag().clone());
						}
						// Spigot End
						world.addEntity(entityitem);
					}
				}
			}
		}

		super.remove(world, i, j, k, block, l);
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.BREWING_STAND;
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
