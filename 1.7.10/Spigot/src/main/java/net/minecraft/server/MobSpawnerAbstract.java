package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

// CraftBukkit end

public abstract class MobSpawnerAbstract {

	public int spawnDelay = 20;
	private String mobName = "Pig";
	private List mobs;
	private TileEntityMobSpawnerData spawnData;
	public double c;
	public double d;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	private Entity j;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;

	public MobSpawnerAbstract() {
	}

	public String getMobName() {
		if (i() == null) {
			if (mobName.equals("Minecart")) {
				mobName = "MinecartRideable";
			}

			return mobName;
		} else
			return i().c;
	}

	public void setMobName(String s) {
		mobName = s;
	}

	public boolean f() {
		return this.a().findNearbyPlayerWhoAffectsSpawning(this.b() + 0.5D, c() + 0.5D, d() + 0.5D, requiredPlayerRange) != null; // PaperSpigot
	}

	public void g() {
		if (f()) {
			double d0;

			if (this.a().isStatic) {
				double d1 = this.b() + this.a().random.nextFloat();
				double d2 = c() + this.a().random.nextFloat();

				d0 = d() + this.a().random.nextFloat();
				this.a().addParticle("smoke", d1, d2, d0, 0.0D, 0.0D, 0.0D);
				this.a().addParticle("flame", d1, d2, d0, 0.0D, 0.0D, 0.0D);
				if (spawnDelay > 0) {
					--spawnDelay;
				}

				d = c;
				c = (c + 1000.0F / (spawnDelay + 200.0F)) % 360.0D;
			} else {
				if (spawnDelay == -1) {
					j();
				}

				if (spawnDelay > 0) {
					--spawnDelay;
					return;
				}

				boolean flag = false;

				for (int i = 0; i < spawnCount; ++i) {
					Entity entity = EntityTypes.createEntityByName(getMobName(), this.a());

					if (entity == null)
						return;

					int j = this.a().a(entity.getClass(), AxisAlignedBB.a(this.b(), c(), d(), this.b() + 1, c() + 1, d() + 1).grow(spawnRange * 2, 4.0D, spawnRange * 2)).size();

					if (j >= maxNearbyEntities) {
						j();
						return;
					}

					d0 = this.b() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * spawnRange;
					double d3 = c() + this.a().random.nextInt(3) - 1;
					double d4 = d() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * spawnRange;
					EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;

					entity.setPositionRotation(d0, d3, d4, this.a().random.nextFloat() * 360.0F, 0.0F);
					if (entityinsentient == null || entityinsentient.canSpawn()) {
						this.a(entity);
						this.a().triggerEffect(2004, this.b(), c(), d(), 0);
						if (entityinsentient != null) {
							entityinsentient.s();
						}

						flag = true;
					}
				}

				if (flag) {
					j();
				}
			}
		}
	}

	public Entity a(Entity entity) {
		if (i() != null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			entity.d(nbttagcompound);
			Iterator iterator = i().b.c().iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				NBTBase nbtbase = i().b.get(s);

				nbttagcompound.set(s, nbtbase.clone());
			}

			entity.f(nbttagcompound);
			if (entity.world != null) {
				// CraftBukkit start - call SpawnerSpawnEvent, abort if cancelled
				SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b(), c(), d());
				if (!event.isCancelled()) {
					entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
					// Spigot Start
					if (entity.world.spigotConfig.nerfSpawnerMobs) {
						entity.fromMobSpawner = true;
					}
					// Spigot End
				}
				// CraftBukkit end
			}

			NBTTagCompound nbttagcompound1;

			for (Entity entity1 = entity; nbttagcompound.hasKeyOfType("Riding", 10); nbttagcompound = nbttagcompound1) {
				nbttagcompound1 = nbttagcompound.getCompound("Riding");
				Entity entity2 = EntityTypes.createEntityByName(nbttagcompound1.getString("id"), entity.world);

				if (entity2 != null) {
					NBTTagCompound nbttagcompound2 = new NBTTagCompound();

					entity2.d(nbttagcompound2);
					Iterator iterator1 = nbttagcompound1.c().iterator();

					while (iterator1.hasNext()) {
						String s1 = (String) iterator1.next();
						NBTBase nbtbase1 = nbttagcompound1.get(s1);

						nbttagcompound2.set(s1, nbtbase1.clone());
					}

					entity2.f(nbttagcompound2);
					entity2.setPositionRotation(entity1.locX, entity1.locY, entity1.locZ, entity1.yaw, entity1.pitch);
					// CraftBukkit start - call SpawnerSpawnEvent, skip if cancelled
					SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity2, this.b(), c(), d());
					if (event.isCancelled()) {
						continue;
					}
					if (entity.world != null) {
						entity.world.addEntity(entity2, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
						if (entity2.world.spigotConfig.nerfSpawnerMobs) {
							entity2.fromMobSpawner = true;
						}
					}

					entity1.mount(entity2);
				}

				entity1 = entity2;
			}
		} else if (entity instanceof EntityLiving && entity.world != null) {
			((EntityInsentient) entity).prepare((GroupDataEntity) null);
			// Spigot start - call SpawnerSpawnEvent, abort if cancelled
			SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b(), c(), d());
			if (!event.isCancelled()) {
				this.a().addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
				// Spigot Start
				if (entity.world.spigotConfig.nerfSpawnerMobs) {
					entity.fromMobSpawner = true;
				}
				// Spigot End
			}
			// Spigot end
		}

		return entity;
	}

	private void j() {
		if (maxSpawnDelay <= minSpawnDelay) {
			spawnDelay = minSpawnDelay;
		} else {
			int i = maxSpawnDelay - minSpawnDelay;

			spawnDelay = minSpawnDelay + this.a().random.nextInt(i);
		}

		if (mobs != null && mobs.size() > 0) {
			this.a((TileEntityMobSpawnerData) WeightedRandom.a(this.a().random, mobs));
		}

		this.a(1);
	}

	public void a(NBTTagCompound nbttagcompound) {
		mobName = nbttagcompound.getString("EntityId");
		spawnDelay = nbttagcompound.getShort("Delay");
		if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
			mobs = new ArrayList();
			NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

			for (int i = 0; i < nbttaglist.size(); ++i) {
				mobs.add(new TileEntityMobSpawnerData(this, nbttaglist.get(i)));
			}
		} else {
			mobs = null;
		}

		if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
			this.a(new TileEntityMobSpawnerData(this, nbttagcompound.getCompound("SpawnData"), mobName));
		} else {
			this.a((TileEntityMobSpawnerData) null);
		}

		if (nbttagcompound.hasKeyOfType("MinSpawnDelay", 99)) {
			minSpawnDelay = nbttagcompound.getShort("MinSpawnDelay");
			maxSpawnDelay = nbttagcompound.getShort("MaxSpawnDelay");
			spawnCount = nbttagcompound.getShort("SpawnCount");
		}

		if (nbttagcompound.hasKeyOfType("MaxNearbyEntities", 99)) {
			maxNearbyEntities = nbttagcompound.getShort("MaxNearbyEntities");
			requiredPlayerRange = nbttagcompound.getShort("RequiredPlayerRange");
		}

		if (nbttagcompound.hasKeyOfType("SpawnRange", 99)) {
			spawnRange = nbttagcompound.getShort("SpawnRange");
		}

		if (this.a() != null && this.a().isStatic) {
			j = null;
		}
	}

	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("EntityId", getMobName());
		nbttagcompound.setShort("Delay", (short) spawnDelay);
		nbttagcompound.setShort("MinSpawnDelay", (short) minSpawnDelay);
		nbttagcompound.setShort("MaxSpawnDelay", (short) maxSpawnDelay);
		nbttagcompound.setShort("SpawnCount", (short) spawnCount);
		nbttagcompound.setShort("MaxNearbyEntities", (short) maxNearbyEntities);
		nbttagcompound.setShort("RequiredPlayerRange", (short) requiredPlayerRange);
		nbttagcompound.setShort("SpawnRange", (short) spawnRange);
		if (i() != null) {
			nbttagcompound.set("SpawnData", i().b.clone());
		}

		if (i() != null || mobs != null && mobs.size() > 0) {
			NBTTagList nbttaglist = new NBTTagList();

			if (mobs != null && mobs.size() > 0) {
				Iterator iterator = mobs.iterator();

				while (iterator.hasNext()) {
					TileEntityMobSpawnerData tileentitymobspawnerdata = (TileEntityMobSpawnerData) iterator.next();

					nbttaglist.add(tileentitymobspawnerdata.a());
				}
			} else {
				nbttaglist.add(i().a());
			}

			nbttagcompound.set("SpawnPotentials", nbttaglist);
		}
	}

	public boolean b(int i) {
		if (i == 1 && this.a().isStatic) {
			spawnDelay = minSpawnDelay;
			return true;
		} else
			return false;
	}

	public TileEntityMobSpawnerData i() {
		return spawnData;
	}

	public void a(TileEntityMobSpawnerData tileentitymobspawnerdata) {
		spawnData = tileentitymobspawnerdata;
	}

	public abstract void a(int i);

	public abstract World a();

	public abstract int b();

	public abstract int c();

	public abstract int d();
}
