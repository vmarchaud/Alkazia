package net.minecraft.server;

import java.util.List;
import java.util.Random;

abstract class WorldGenVillagePiece extends StructurePiece {

	protected int k = -1;
	private int a;
	private boolean b;

	public WorldGenVillagePiece() {
	}

	protected WorldGenVillagePiece(WorldGenVillageStartPiece worldgenvillagestartpiece, int i) {
		super(i);
		if (worldgenvillagestartpiece != null) {
			b = worldgenvillagestartpiece.b;
		}
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInt("HPos", k);
		nbttagcompound.setInt("VCount", a);
		nbttagcompound.setBoolean("Desert", b);
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
		k = nbttagcompound.getInt("HPos");
		a = nbttagcompound.getInt("VCount");
		b = nbttagcompound.getBoolean("Desert");
	}

	protected StructurePiece a(WorldGenVillageStartPiece worldgenvillagestartpiece, List list, Random random, int i, int j) {
		switch (g) {
		case 0:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a - 1, f.b + i, f.c + j, 1, d());

		case 1:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a + j, f.b + i, f.c - 1, 2, d());

		case 2:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a - 1, f.b + i, f.c + j, 1, d());

		case 3:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a + j, f.b + i, f.c - 1, 2, d());

		default:
			return null;
		}
	}

	protected StructurePiece b(WorldGenVillageStartPiece worldgenvillagestartpiece, List list, Random random, int i, int j) {
		switch (g) {
		case 0:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.d + 1, f.b + i, f.c + j, 3, d());

		case 1:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a + j, f.b + i, f.f + 1, 0, d());

		case 2:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.d + 1, f.b + i, f.c + j, 3, d());

		case 3:
			return WorldGenVillagePieces.a(worldgenvillagestartpiece, list, random, f.a + j, f.b + i, f.f + 1, 0, d());

		default:
			return null;
		}
	}

	protected int b(World world, StructureBoundingBox structureboundingbox) {
		int i = 0;
		int j = 0;

		for (int k = f.c; k <= f.f; ++k) {
			for (int l = f.a; l <= f.d; ++l) {
				if (structureboundingbox.b(l, 64, k)) {
					i += Math.max(world.i(l, k), world.worldProvider.getSeaLevel());
					++j;
				}
			}
		}

		if (j == 0)
			return -1;
		else
			return i / j;
	}

	protected static boolean a(StructureBoundingBox structureboundingbox) {
		return structureboundingbox != null && structureboundingbox.b > 10;
	}

	protected void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
		if (a < l) {
			for (int i1 = a; i1 < l; ++i1) {
				int j1 = this.a(i + i1, k);
				int k1 = this.a(j);
				int l1 = this.b(i + i1, k);

				if (!structureboundingbox.b(j1, k1, l1)) {
					break;
				}

				++a;
				EntityVillager entityvillager = new EntityVillager(world, this.b(i1));

				entityvillager.setPositionRotation(j1 + 0.5D, k1, l1 + 0.5D, 0.0F, 0.0F);
				world.addEntity(entityvillager, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CHUNK_GEN); // CraftBukkit - add SpawnReason
			}
		}
	}

	protected int b(int i) {
		return 0;
	}

	protected Block b(Block block, int i) {
		if (b) {
			if (block == Blocks.LOG || block == Blocks.LOG2)
				return Blocks.SANDSTONE;

			if (block == Blocks.COBBLESTONE)
				return Blocks.SANDSTONE;

			if (block == Blocks.WOOD)
				return Blocks.SANDSTONE;

			if (block == Blocks.WOOD_STAIRS)
				return Blocks.SANDSTONE_STAIRS;

			if (block == Blocks.COBBLESTONE_STAIRS)
				return Blocks.SANDSTONE_STAIRS;

			if (block == Blocks.GRAVEL)
				return Blocks.SANDSTONE;
		}

		return block;
	}

	protected int c(Block block, int i) {
		if (b) {
			if (block == Blocks.LOG || block == Blocks.LOG2)
				return 0;

			if (block == Blocks.COBBLESTONE)
				return 0;

			if (block == Blocks.WOOD)
				return 2;
		}

		return i;
	}

	@Override
	protected void a(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
		Block block1 = this.b(block, i);
		int i1 = this.c(block, i);

		super.a(world, block1, i1, j, k, l, structureboundingbox);
	}

	@Override
	protected void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, Block block, Block block1, boolean flag) {
		Block block2 = this.b(block, 0);
		int k1 = this.c(block, 0);
		Block block3 = this.b(block1, 0);
		int l1 = this.c(block1, 0);

		super.a(world, structureboundingbox, i, j, k, l, i1, j1, block2, k1, block3, l1, flag);
	}

	@Override
	protected void b(World world, Block block, int i, int j, int k, int l, StructureBoundingBox structureboundingbox) {
		Block block1 = this.b(block, i);
		int i1 = this.c(block, i);

		super.b(world, block1, i1, j, k, l, structureboundingbox);
	}
}
