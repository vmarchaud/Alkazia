package net.minecraft.server;

import java.util.Random;

public class BlockFlowerPot extends BlockContainer {

	public BlockFlowerPot() {
		super(Material.ORIENTABLE);
		this.g();
	}

	@Override
	public void g() {
		float f = 0.375F;
		float f1 = f / 2.0F;

		this.setBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, f, 0.5F + f1);
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public int b() {
		return 33;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() instanceof ItemBlock) {
			TileEntityFlowerPot tileentityflowerpot = e(world, i, j, k);

			if (tileentityflowerpot != null) {
				if (tileentityflowerpot.a() != null)
					return false;
				else {
					Block block = Block.setSound(itemstack.getItem());

					if (!this.a(block, itemstack.getData()))
						return false;
					else {
						tileentityflowerpot.a(itemstack.getItem(), itemstack.getData());
						tileentityflowerpot.update();
						if (!world.setData(i, j, k, itemstack.getData(), 2)) {
							world.notify(i, j, k);
						}

						if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count <= 0) {
							entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
						}

						return true;
					}
				}
			} else
				return false;
		} else
			return false;
	}

	private boolean a(Block block, int i) {
		return block != Blocks.YELLOW_FLOWER && block != Blocks.RED_ROSE && block != Blocks.CACTUS && block != Blocks.BROWN_MUSHROOM && block != Blocks.RED_MUSHROOM && block != Blocks.SAPLING && block != Blocks.DEAD_BUSH ? block == Blocks.LONG_GRASS && i == 2 : true;
	}

	@Override
	public int getDropData(World world, int i, int j, int k) {
		TileEntityFlowerPot tileentityflowerpot = e(world, i, j, k);

		return tileentityflowerpot != null && tileentityflowerpot.a() != null ? tileentityflowerpot.b() : 0;
	}

	@Override
	public boolean canPlace(World world, int i, int j, int k) {
		return super.canPlace(world, i, j, k) && World.a(world, i, j - 1, k);
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
		if (!World.a(world, i, j - 1, k)) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setAir(i, j, k);
		}
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		TileEntityFlowerPot tileentityflowerpot = e(world, i, j, k);

		if (tileentityflowerpot != null && tileentityflowerpot.a() != null) {
			this.a(world, i, j, k, new ItemStack(tileentityflowerpot.a(), 1, tileentityflowerpot.b()));
			tileentityflowerpot.a(null, 0); // Spigot
		}

		super.remove(world, i, j, k, block, l);
	}

	@Override
	public void a(World world, int i, int j, int k, int l, EntityHuman entityhuman) {
		super.a(world, i, j, k, l, entityhuman);
		if (entityhuman.abilities.canInstantlyBuild) {
			TileEntityFlowerPot tileentityflowerpot = e(world, i, j, k);

			if (tileentityflowerpot != null) {
				tileentityflowerpot.a(Item.getById(0), 0);
			}
		}
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.FLOWER_POT;
	}

	private TileEntityFlowerPot e(World world, int i, int j, int k) {
		TileEntity tileentity = world.getTileEntity(i, j, k);

		return tileentity != null && tileentity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot) tileentity : null;
	}

	@Override
	public TileEntity a(World world, int i) {
		Object object = null;
		byte b0 = 0;

		switch (i) {
		case 1:
			object = Blocks.RED_ROSE;
			b0 = 0;
			break;

		case 2:
			object = Blocks.YELLOW_FLOWER;
			break;

		case 3:
			object = Blocks.SAPLING;
			b0 = 0;
			break;

		case 4:
			object = Blocks.SAPLING;
			b0 = 1;
			break;

		case 5:
			object = Blocks.SAPLING;
			b0 = 2;
			break;

		case 6:
			object = Blocks.SAPLING;
			b0 = 3;
			break;

		case 7:
			object = Blocks.RED_MUSHROOM;
			break;

		case 8:
			object = Blocks.BROWN_MUSHROOM;
			break;

		case 9:
			object = Blocks.CACTUS;
			break;

		case 10:
			object = Blocks.DEAD_BUSH;
			break;

		case 11:
			object = Blocks.LONG_GRASS;
			b0 = 2;
			break;

		case 12:
			object = Blocks.SAPLING;
			b0 = 4;
			break;

		case 13:
			object = Blocks.SAPLING;
			b0 = 5;
		}

		return new TileEntityFlowerPot(Item.getItemOf((Block) object), b0);
	}
}
