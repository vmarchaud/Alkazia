package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class Village {

	private World world;
	private final List doors = new ArrayList();
	private final ChunkCoordinates c = new ChunkCoordinates(0, 0, 0);
	private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
	private int size;
	private int f;
	private int time;
	private int population;
	private int noBreedTicks;
	private TreeMap playerStandings = new TreeMap();
	private List aggressors = new ArrayList();
	private int ironGolemCount;

	public Village() {
	}

	public Village(World world) {
		this.world = world;
	}

	public void a(World world) {
		this.world = world;
	}

	public void tick(int i) {
		time = i;
		m();
		l();
		if (i % 20 == 0) {
			k();
		}

		if (i % 30 == 0) {
			countPopulation();
		}

		int j = population / 10;

		if (ironGolemCount < j && doors.size() > 20 && world.random.nextInt(7000) == 0) {
			Vec3D vec3d = this.a(MathHelper.d(center.x), MathHelper.d(center.y), MathHelper.d(center.z), 2, 4, 2);

			if (vec3d != null) {
				EntityIronGolem entityirongolem = new EntityIronGolem(world);

				entityirongolem.setPosition(vec3d.a, vec3d.b, vec3d.c);
				world.addEntity(entityirongolem, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE); // CraftBukkit
				++ironGolemCount;
			}
		}
	}

	private Vec3D a(int i, int j, int k, int l, int i1, int j1) {
		for (int k1 = 0; k1 < 10; ++k1) {
			int l1 = i + world.random.nextInt(16) - 8;
			int i2 = j + world.random.nextInt(6) - 3;
			int j2 = k + world.random.nextInt(16) - 8;

			if (this.a(l1, i2, j2) && this.b(l1, i2, j2, l, i1, j1))
				return Vec3D.a(l1, i2, j2);
		}

		return null;
	}

	private boolean b(int i, int j, int k, int l, int i1, int j1) {
		if (!World.a(world, i, j - 1, k))
			return false;
		else {
			int k1 = i - l / 2;
			int l1 = k - j1 / 2;

			for (int i2 = k1; i2 < k1 + l; ++i2) {
				for (int j2 = j; j2 < j + i1; ++j2) {
					for (int k2 = l1; k2 < l1 + j1; ++k2) {
						if (world.getType(i2, j2, k2).r())
							return false;
					}
				}
			}

			return true;
		}
	}

	private void countPopulation() {
		List list = world.a(EntityIronGolem.class, AxisAlignedBB.a(center.x - size, center.y - 4, center.z - size, center.x + size, center.y + 4, center.z + size));

		ironGolemCount = list.size();
	}

	private void k() {
		List list = world.a(EntityVillager.class, AxisAlignedBB.a(center.x - size, center.y - 4, center.z - size, center.x + size, center.y + 4, center.z + size));

		population = list.size();
		if (population == 0) {
			playerStandings.clear();
		}
	}

	public ChunkCoordinates getCenter() {
		return center;
	}

	public int getSize() {
		return size;
	}

	public int getDoorCount() {
		return doors.size();
	}

	public int d() {
		return time - f;
	}

	public int getPopulationCount() {
		return population;
	}

	public boolean a(int i, int j, int k) {
		return center.e(i, j, k) < size * size;
	}

	public List getDoors() {
		return doors;
	}

	public VillageDoor b(int i, int j, int k) {
		VillageDoor villagedoor = null;
		int l = Integer.MAX_VALUE;
		Iterator iterator = doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villagedoor1 = (VillageDoor) iterator.next();
			int i1 = villagedoor1.b(i, j, k);

			if (i1 < l) {
				villagedoor = villagedoor1;
				l = i1;
			}
		}

		return villagedoor;
	}

	public VillageDoor c(int i, int j, int k) {
		VillageDoor villagedoor = null;
		int l = Integer.MAX_VALUE;
		Iterator iterator = doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villagedoor1 = (VillageDoor) iterator.next();
			int i1 = villagedoor1.b(i, j, k);

			if (i1 > 256) {
				i1 *= 1000;
			} else {
				i1 = villagedoor1.f();
			}

			if (i1 < l) {
				villagedoor = villagedoor1;
				l = i1;
			}
		}

		return villagedoor;
	}

	public VillageDoor e(int i, int j, int k) {
		if (center.e(i, j, k) > size * size)
			return null;
		else {
			Iterator iterator = doors.iterator();

			VillageDoor villagedoor;

			do {
				if (!iterator.hasNext())
					return null;

				villagedoor = (VillageDoor) iterator.next();
			} while (villagedoor.locX != i || villagedoor.locZ != k || Math.abs(villagedoor.locY - j) > 1);

			return villagedoor;
		}
	}

	public void addDoor(VillageDoor villagedoor) {
		doors.add(villagedoor);
		c.x += villagedoor.locX;
		c.y += villagedoor.locY;
		c.z += villagedoor.locZ;
		n();
		f = villagedoor.addedTime;
	}

	public boolean isAbandoned() {
		return doors.isEmpty();
	}

	public void a(EntityLiving entityliving) {
		Iterator iterator = aggressors.iterator();

		VillageAggressor villageaggressor;

		do {
			if (!iterator.hasNext()) {
				aggressors.add(new VillageAggressor(this, entityliving, time));
				return;
			}

			villageaggressor = (VillageAggressor) iterator.next();
		} while (villageaggressor.a != entityliving);

		villageaggressor.b = time;
	}

	public EntityLiving b(EntityLiving entityliving) {
		double d0 = Double.MAX_VALUE;
		VillageAggressor villageaggressor = null;

		for (int i = 0; i < aggressors.size(); ++i) {
			VillageAggressor villageaggressor1 = (VillageAggressor) aggressors.get(i);
			double d1 = villageaggressor1.a.f(entityliving);

			if (d1 <= d0) {
				villageaggressor = villageaggressor1;
				d0 = d1;
			}
		}

		return villageaggressor != null ? villageaggressor.a : null;
	}

	public EntityHuman c(EntityLiving entityliving) {
		double d0 = Double.MAX_VALUE;
		EntityHuman entityhuman = null;
		Iterator iterator = playerStandings.keySet().iterator();

		while (iterator.hasNext()) {
			String s = (String) iterator.next();

			if (this.d(s)) {
				EntityHuman entityhuman1 = world.a(s);

				if (entityhuman1 != null) {
					double d1 = entityhuman1.f(entityliving);

					if (d1 <= d0) {
						entityhuman = entityhuman1;
						d0 = d1;
					}
				}
			}
		}

		return entityhuman;
	}

	private void l() {
		Iterator iterator = aggressors.iterator();

		while (iterator.hasNext()) {
			VillageAggressor villageaggressor = (VillageAggressor) iterator.next();

			if (!villageaggressor.a.isAlive() || Math.abs(time - villageaggressor.b) > 300) {
				iterator.remove();
			}
		}
	}

	private void m() {
		boolean flag = false;
		boolean flag1 = world.random.nextInt(50) == 0;
		Iterator iterator = doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villagedoor = (VillageDoor) iterator.next();

			if (flag1) {
				villagedoor.d();
			}

			if (!isDoor(villagedoor.locX, villagedoor.locY, villagedoor.locZ) || Math.abs(time - villagedoor.addedTime) > 1200) {
				c.x -= villagedoor.locX;
				c.y -= villagedoor.locY;
				c.z -= villagedoor.locZ;
				flag = true;
				villagedoor.removed = true;
				iterator.remove();
			}
		}

		if (flag) {
			n();
		}
	}

	private boolean isDoor(int i, int j, int k) {
		return world.getType(i, j, k) == Blocks.WOODEN_DOOR;
	}

	private void n() {
		int i = doors.size();

		if (i == 0) {
			center.b(0, 0, 0);
			size = 0;
		} else {
			center.b(c.x / i, c.y / i, c.z / i);
			int j = 0;

			VillageDoor villagedoor;

			for (Iterator iterator = doors.iterator(); iterator.hasNext(); j = Math.max(villagedoor.b(center.x, center.y, center.z), j)) {
				villagedoor = (VillageDoor) iterator.next();
			}

			size = Math.max(32, (int) Math.sqrt(j) + 1);
		}
	}

	public int a(String s) {
		Integer integer = (Integer) playerStandings.get(s);

		return integer != null ? integer.intValue() : 0;
	}

	public int a(String s, int i) {
		int j = this.a(s);
		int k = MathHelper.a(j + i, -30, 10);

		playerStandings.put(s, Integer.valueOf(k));
		return k;
	}

	public boolean d(String s) {
		return this.a(s) <= -15;
	}

	public void a(NBTTagCompound nbttagcompound) {
		population = nbttagcompound.getInt("PopSize");
		size = nbttagcompound.getInt("Radius");
		ironGolemCount = nbttagcompound.getInt("Golems");
		f = nbttagcompound.getInt("Stable");
		time = nbttagcompound.getInt("Tick");
		noBreedTicks = nbttagcompound.getInt("MTick");
		center.x = nbttagcompound.getInt("CX");
		center.y = nbttagcompound.getInt("CY");
		center.z = nbttagcompound.getInt("CZ");
		c.x = nbttagcompound.getInt("ACX");
		c.y = nbttagcompound.getInt("ACY");
		c.z = nbttagcompound.getInt("ACZ");
		NBTTagList nbttaglist = nbttagcompound.getList("Doors", 10);

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
			VillageDoor villagedoor = new VillageDoor(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z"), nbttagcompound1.getInt("IDX"), nbttagcompound1.getInt("IDZ"), nbttagcompound1.getInt("TS"));

			doors.add(villagedoor);
		}

		NBTTagList nbttaglist1 = nbttagcompound.getList("Players", 10);

		for (int j = 0; j < nbttaglist1.size(); ++j) {
			NBTTagCompound nbttagcompound2 = nbttaglist1.get(j);

			playerStandings.put(nbttagcompound2.getString("Name"), Integer.valueOf(nbttagcompound2.getInt("S")));
		}
	}

	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInt("PopSize", population);
		nbttagcompound.setInt("Radius", size);
		nbttagcompound.setInt("Golems", ironGolemCount);
		nbttagcompound.setInt("Stable", f);
		nbttagcompound.setInt("Tick", time);
		nbttagcompound.setInt("MTick", noBreedTicks);
		nbttagcompound.setInt("CX", center.x);
		nbttagcompound.setInt("CY", center.y);
		nbttagcompound.setInt("CZ", center.z);
		nbttagcompound.setInt("ACX", c.x);
		nbttagcompound.setInt("ACY", c.y);
		nbttagcompound.setInt("ACZ", c.z);
		NBTTagList nbttaglist = new NBTTagList();
		Iterator iterator = doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villagedoor = (VillageDoor) iterator.next();
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();

			nbttagcompound1.setInt("X", villagedoor.locX);
			nbttagcompound1.setInt("Y", villagedoor.locY);
			nbttagcompound1.setInt("Z", villagedoor.locZ);
			nbttagcompound1.setInt("IDX", villagedoor.d);
			nbttagcompound1.setInt("IDZ", villagedoor.e);
			nbttagcompound1.setInt("TS", villagedoor.addedTime);
			nbttaglist.add(nbttagcompound1);
		}

		nbttagcompound.set("Doors", nbttaglist);
		NBTTagList nbttaglist1 = new NBTTagList();
		Iterator iterator1 = playerStandings.keySet().iterator();

		while (iterator1.hasNext()) {
			String s = (String) iterator1.next();
			NBTTagCompound nbttagcompound2 = new NBTTagCompound();

			nbttagcompound2.setString("Name", s);
			nbttagcompound2.setInt("S", ((Integer) playerStandings.get(s)).intValue());
			nbttaglist1.add(nbttagcompound2);
		}

		nbttagcompound.set("Players", nbttaglist1);
	}

	public void h() {
		noBreedTicks = time;
	}

	public boolean i() {
		return noBreedTicks == 0 || time - noBreedTicks >= 3600;
	}

	public void b(int i) {
		Iterator iterator = playerStandings.keySet().iterator();

		while (iterator.hasNext()) {
			String s = (String) iterator.next();

			this.a(s, i);
		}
	}
}
