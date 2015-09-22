package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

// CraftBukkit start
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

// CraftBukkit end

public class BlockSkull extends BlockContainer {

	protected BlockSkull() {
		super(Material.ORIENTABLE);
		this.setBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
	}

	@Override
	public int b() {
		return -1;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k) & 7;

		switch (l) {
		case 1:
		default:
			this.setBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
			break;

		case 2:
			this.setBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
			break;

		case 3:
			this.setBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
			break;

		case 4:
			this.setBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
			break;

		case 5:
			this.setBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
		}
	}

	@Override
	public AxisAlignedBB a(World world, int i, int j, int k) {
		updateShape(world, i, j, k);
		return super.a(world, i, j, k);
	}

	@Override
	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
		int l = MathHelper.floor(entityliving.yaw * 4.0F / 360.0F + 2.5D) & 3;

		world.setData(i, j, k, l, 2);
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntitySkull();
	}

	@Override
	public int getDropData(World world, int i, int j, int k) {
		TileEntity tileentity = world.getTileEntity(i, j, k);

		return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).getSkullType() : super.getDropData(world, i, j, k);
	}

	@Override
	public int getDropData(int i) {
		return i;
	}

	// CraftBukkit start - Special case dropping so we can get info from the tile entity
	@Override
	public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
		if (world.random.nextFloat() < f) {
			ItemStack itemstack = new ItemStack(Items.SKULL, 1, this.getDropData(world, i, j, k));
			TileEntitySkull tileentityskull = (TileEntitySkull) world.getTileEntity(i, j, k);

			if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
				itemstack.setTag(new NBTTagCompound());
				NBTTagCompound nbttagcompound = new NBTTagCompound();

				GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
				itemstack.getTag().set("SkullOwner", nbttagcompound);
			}

			this.a(world, i, j, k, itemstack);
		}
	}

	// CraftBukkit end

	@Override
	public void a(World world, int i, int j, int k, int l, EntityHuman entityhuman) {
		if (entityhuman.abilities.canInstantlyBuild) {
			l |= 8;
			world.setData(i, j, k, l, 4);
		}

		super.a(world, i, j, k, l, entityhuman);
	}

	@Override
	public void remove(World world, int i, int j, int k, Block block, int l) {
		if (!world.isStatic) {
			// CraftBukkit start - Drop item in code above, not here
			// if ((l & 8) == 0) {
			if (false) {
				// CraftBukkit end
				ItemStack itemstack = new ItemStack(Items.SKULL, 1, this.getDropData(world, i, j, k));
				TileEntitySkull tileentityskull = (TileEntitySkull) world.getTileEntity(i, j, k);

				if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
					itemstack.setTag(new NBTTagCompound());
					NBTTagCompound nbttagcompound = new NBTTagCompound();

					GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
					itemstack.getTag().set("SkullOwner", nbttagcompound);
				}

				this.a(world, i, j, k, itemstack);
			}

			super.remove(world, i, j, k, block, l);
		}
	}

	@Override
	public Item getDropType(int i, Random random, int j) {
		return Items.SKULL;
	}

	public void a(World world, int i, int j, int k, TileEntitySkull tileentityskull) {
		if (tileentityskull.getSkullType() == 1 && j >= 2 && world.difficulty != EnumDifficulty.PEACEFUL && !world.isStatic) {
			int l;
			EntityWither entitywither;
			Iterator iterator;
			EntityHuman entityhuman;
			int i1;

			for (l = -2; l <= 0; ++l) {
				if (world.getType(i, j - 1, k + l) == Blocks.SOUL_SAND && world.getType(i, j - 1, k + l + 1) == Blocks.SOUL_SAND && world.getType(i, j - 2, k + l + 1) == Blocks.SOUL_SAND && world.getType(i, j - 1, k + l + 2) == Blocks.SOUL_SAND && this.a(world, i, j, k + l, 1) && this.a(world, i, j, k + l + 1, 1) && this.a(world, i, j, k + l + 2, 1)) {
					// CraftBukkit start - Use BlockStateListPopulator
					BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());

					world.setData(i, j, k + l, 8, 2);
					world.setData(i, j, k + l + 1, 8, 2);
					world.setData(i, j, k + l + 2, 8, 2);

					blockList.setTypeAndData(i, j, k + l, getById(0), 0, 2);
					blockList.setTypeAndData(i, j, k + l + 1, getById(0), 0, 2);
					blockList.setTypeAndData(i, j, k + l + 2, getById(0), 0, 2);
					blockList.setTypeAndData(i, j - 1, k + l, getById(0), 0, 2);
					blockList.setTypeAndData(i, j - 1, k + l + 1, getById(0), 0, 2);
					blockList.setTypeAndData(i, j - 1, k + l + 2, getById(0), 0, 2);
					blockList.setTypeAndData(i, j - 2, k + l + 1, getById(0), 0, 2);

					if (!world.isStatic) {
						entitywither = new EntityWither(world);
						entitywither.setPositionRotation(i + 0.5D, j - 1.45D, k + l + 1.5D, 90.0F, 0.0F);
						entitywither.aM = 90.0F;
						entitywither.bZ();

						if (world.addEntity(entitywither, SpawnReason.BUILD_WITHER)) {
							if (!world.isStatic) {
								iterator = world.a(EntityHuman.class, entitywither.boundingBox.grow(50.0D, 50.0D, 50.0D)).iterator();

								while (iterator.hasNext()) {
									entityhuman = (EntityHuman) iterator.next();
									entityhuman.a(AchievementList.I);
								}
							}

							blockList.updateList();
						}
					}

					for (i1 = 0; i1 < 120; ++i1) {
						world.addParticle("snowballpoof", i + world.random.nextDouble(), j - 2 + world.random.nextDouble() * 3.9D, k + l + 1 + world.random.nextDouble(), 0.0D, 0.0D, 0.0D);
					}
					// CraftBukkit end
					return;
				}
			}

			for (l = -2; l <= 0; ++l) {
				if (world.getType(i + l, j - 1, k) == Blocks.SOUL_SAND && world.getType(i + l + 1, j - 1, k) == Blocks.SOUL_SAND && world.getType(i + l + 1, j - 2, k) == Blocks.SOUL_SAND && world.getType(i + l + 2, j - 1, k) == Blocks.SOUL_SAND && this.a(world, i + l, j, k, 1) && this.a(world, i + l + 1, j, k, 1) && this.a(world, i + l + 2, j, k, 1)) {
					// CraftBukkit start - Use BlockStateListPopulator
					BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());

					world.setData(i + l, j, k, 8, 2);
					world.setData(i + l + 1, j, k, 8, 2);
					world.setData(i + l + 2, j, k, 8, 2);

					blockList.setTypeAndData(i + l, j, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l + 1, j, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l + 2, j, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l, j - 1, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l + 1, j - 1, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l + 2, j - 1, k, getById(0), 0, 2);
					blockList.setTypeAndData(i + l + 1, j - 2, k, getById(0), 0, 2);
					if (!world.isStatic) {
						entitywither = new EntityWither(world);
						entitywither.setPositionRotation(i + l + 1.5D, j - 1.45D, k + 0.5D, 0.0F, 0.0F);
						entitywither.bZ();

						if (world.addEntity(entitywither, SpawnReason.BUILD_WITHER)) {
							if (!world.isStatic) {
								iterator = world.a(EntityHuman.class, entitywither.boundingBox.grow(50.0D, 50.0D, 50.0D)).iterator();

								while (iterator.hasNext()) {
									entityhuman = (EntityHuman) iterator.next();
									entityhuman.a(AchievementList.I);
								}
							}
							blockList.updateList();
						}
					}

					for (i1 = 0; i1 < 120; ++i1) {
						world.addParticle("snowballpoof", i + l + 1 + world.random.nextDouble(), j - 2 + world.random.nextDouble() * 3.9D, k + world.random.nextDouble(), 0.0D, 0.0D, 0.0D);
					}
					// CraftBukkit end

					return;
				}
			}
		}
	}

	private boolean a(World world, int i, int j, int k, int l) {
		if (world.getType(i, j, k) != this)
			return false;
		else {
			TileEntity tileentity = world.getTileEntity(i, j, k);

			return tileentity != null && tileentity instanceof TileEntitySkull ? ((TileEntitySkull) tileentity).getSkullType() == l : false;
		}
	}
}
