package net.minecraft.server;

public class BiomeTheEndDecorator extends BiomeDecorator {

	protected WorldGenerator J;

	public BiomeTheEndDecorator() {
		J = new WorldGenEnder(Blocks.WHITESTONE);
	}

	@Override
	protected void a(BiomeBase biomebase) {
		this.a();
		if (b.nextInt(5) == 0) {
			int i = c + b.nextInt(16) + 8;
			int j = d + b.nextInt(16) + 8;
			int k = a.i(i, j);

			J.generate(a, b, i, k, j);
		}

		if (c == 0 && d == 0) {
			EntityEnderDragon entityenderdragon = new EntityEnderDragon(a);

			entityenderdragon.setPositionRotation(0.0D, 128.0D, 0.0D, b.nextFloat() * 360.0F, 0.0F);
			a.addEntity(entityenderdragon, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CHUNK_GEN); // CraftBukkit - add SpawnReason
		}
	}
}
