package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class VillageSiege {

	private World world;
	private boolean b;
	private int c = -1;
	private int d;
	private int e;
	private Village f;
	private int g;
	private int h;
	private int i;

	public VillageSiege(World world) {
		this.world = world;
	}

	public void a() {
		boolean flag = false;

		if (flag) {
			if (c == 2) {
				d = 100;
				return;
			}
		} else {
			if (world.w()) {
				c = 0;
				return;
			}

			if (c == 2)
				return;

			if (c == 0) {
				float f = world.c(0.0F);

				if (f < 0.5D || f > 0.501D)
					return;

				c = world.random.nextInt(10) == 0 ? 1 : 2;
				b = false;
				if (c == 2)
					return;
			}

			// PaperSpigot start - Siege manager initial state is -1
			if (c == -1)
				return;
		}

		if (!b) {
			if (!b())
				return;

			b = true;
		}

		if (e > 0) {
			--e;
		} else {
			e = 2;
			if (d > 0) {
				c();
				--d;
			} else {
				c = 2;
			}
		}
	}

	private boolean b() {
		List list = world.players;
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			EntityHuman entityhuman = (EntityHuman) iterator.next();

			f = world.villages.getClosestVillage((int) entityhuman.locX, (int) entityhuman.locY, (int) entityhuman.locZ, 1);
			if (f != null && f.getDoorCount() >= 10 && f.d() >= 20 && f.getPopulationCount() >= 20) {
				ChunkCoordinates chunkcoordinates = f.getCenter();
				float f = this.f.getSize();
				boolean flag = false;
				int i = 0;

				while (true) {
					if (i < 10) {
						// PaperSpigot start - Zombies should spawn near the perimeter of the village not in the center of it
						float angle = world.random.nextFloat() * (float) Math.PI * 2.0F;
						g = chunkcoordinates.x + (int) (MathHelper.cos(angle) * f * 0.9D);
						h = chunkcoordinates.y;
						this.i = chunkcoordinates.z + (int) (MathHelper.sin(angle) * f * 0.9D);
						// PaperSpigot end
						flag = false;
						Iterator iterator1 = world.villages.getVillages().iterator();

						while (iterator1.hasNext()) {
							Village village = (Village) iterator1.next();

							if (village != this.f && village.a(g, h, this.i)) {
								flag = true;
								break;
							}
						}

						if (flag) {
							++i;
							continue;
						}
					}

					if (flag)
						return false;

					Vec3D vec3d = this.a(g, h, this.i);

					if (vec3d != null) {
						e = 0;
						d = 20;
						return true;
					}
					break;
				}
			}
		}

		return false;
	}

	private boolean c() {
		Vec3D vec3d = this.a(g, h, i);

		if (vec3d == null)
			return false;
		else {
			EntityZombie entityzombie;

			try {
				entityzombie = new EntityZombie(world);
				entityzombie.prepare((GroupDataEntity) null);
				entityzombie.setVillager(false);
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}

			entityzombie.setPositionRotation(vec3d.a, vec3d.b, vec3d.c, world.random.nextFloat() * 360.0F, 0.0F);
			world.addEntity(entityzombie, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION); // CraftBukkit
			ChunkCoordinates chunkcoordinates = f.getCenter();

			entityzombie.a(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, f.getSize());
			return true;
		}
	}

	private Vec3D a(int i, int j, int k) {
		for (int l = 0; l < 10; ++l) {
			int i1 = i + world.random.nextInt(16) - 8;
			int j1 = j + world.random.nextInt(6) - 3;
			int k1 = k + world.random.nextInt(16) - 8;

			if (f.a(i1, j1, k1) && SpawnerCreature.a(EnumCreatureType.MONSTER, world, i1, j1, k1))
				// CraftBukkit - add Return
				return Vec3D.a(i1, j1, k1);

		}

		return null;
	}
}
