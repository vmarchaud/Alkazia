package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;

import org.spigotmc.ProtocolData; // Spigot - protocol patch

public class DataWatcher {

	private final Entity a;
	private boolean b = true;
	// Spigot Start
	private static final net.minecraft.util.gnu.trove.map.TObjectIntMap classToId = new net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap(10, 0.5f, -1);
	private final net.minecraft.util.gnu.trove.map.TIntObjectMap dataValues = new net.minecraft.util.gnu.trove.map.hash.TIntObjectHashMap(10, 0.5f, -1);
	// These exist as an attempt at backwards compatability for (broken) NMS plugins
	private static final Map c = net.minecraft.util.gnu.trove.TDecorators.wrap(classToId);
	private final Map d = net.minecraft.util.gnu.trove.TDecorators.wrap(dataValues);
	// Spigot End
	private boolean e;
	private ReadWriteLock f = new ReentrantReadWriteLock();

	public DataWatcher(Entity entity) {
		a = entity;
	}

	public void a(int i, Object object) {
		int integer = classToId.get(object.getClass()); // Spigot

		// Spigot start - protocol patch
		if (object instanceof ProtocolData.ByteShort || object instanceof ProtocolData.DualByte || object instanceof ProtocolData.HiddenByte) {
			integer = classToId.get(Byte.class);
		}
		if (object instanceof ProtocolData.IntByte || object instanceof ProtocolData.DualInt) {
			integer = classToId.get(Integer.class);
		}
		// Spigot end

		if (integer == -1)
			throw new IllegalArgumentException("Unknown data type: " + object.getClass());
		else if (i > 31)
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
		else if (dataValues.containsKey(i))
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		else {
			WatchableObject watchableobject = new WatchableObject(integer, i, object); // Spigot

			f.writeLock().lock();
			dataValues.put(i, watchableobject); // Spigot
			f.writeLock().unlock();
			b = false;
		}
	}

	public void add(int i, int j) {
		WatchableObject watchableobject = new WatchableObject(j, i, null);

		f.writeLock().lock();
		dataValues.put(i, watchableobject); // Spigot
		f.writeLock().unlock();
		b = false;
	}

	public byte getByte(int i) {
		return ((Number) i(i).b()).byteValue(); // Spigot - protocol patch
	}

	public short getShort(int i) {
		return ((Number) i(i).b()).shortValue(); // Spigot - protocol patch
	}

	public int getInt(int i) {
		return ((Number) i(i).b()).intValue(); // Spigot - protocol patch
	}

	public float getFloat(int i) {
		return ((Number) i(i).b()).floatValue(); // Spigot - protocol patch
	}

	public String getString(int i) {
		return (String) i(i).b();
	}

	public ItemStack getItemStack(int i) {
		return (ItemStack) i(i).b();
	}

	// Spigot start - protocol patch
	public ProtocolData.DualByte getDualByte(int i) {
		return (ProtocolData.DualByte) i(i).b();
	}

	public ProtocolData.IntByte getIntByte(int i) {
		return (ProtocolData.IntByte) i(i).b();
	}

	public ProtocolData.DualInt getDualInt(int i) {
		return (ProtocolData.DualInt) i(i).b();
	}

	// Spigot end

	private WatchableObject i(int i) {
		f.readLock().lock();

		WatchableObject watchableobject;

		try {
			watchableobject = (WatchableObject) dataValues.get(i); // Spigot
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.a(throwable, "Getting synched entity data");
			CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Synched entity data");

			crashreportsystemdetails.a("Data ID", Integer.valueOf(i));
			throw new ReportedException(crashreport);
		}

		f.readLock().unlock();
		return watchableobject;
	}

	public void watch(int i, Object object) {
		WatchableObject watchableobject = i(i);

		if (ObjectUtils.notEqual(object, watchableobject.b())) {
			watchableobject.a(object);
			a.i(i);
			watchableobject.a(true);
			e = true;
		}
	}

	public void update(int i) {
		WatchableObject.a(i(i), true);
		e = true;
	}

	public boolean a() {
		return e;
	}

	// Spigot start - protocol patch
	public static void a(List list, PacketDataSerializer packetdataserializer) {
		a(list, packetdataserializer, 5);
	}

	public static void a(List list, PacketDataSerializer packetdataserializer, int version) {
		// Spigot end - protocol patch
		if (list != null) {
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				WatchableObject watchableobject = (WatchableObject) iterator.next();

				a(packetdataserializer, watchableobject, version); // Spigot - protocol patch
			}
		}

		packetdataserializer.writeByte(127);
	}

	public List b() {
		ArrayList arraylist = null;

		if (e) {
			f.readLock().lock();
			Iterator iterator = dataValues.valueCollection().iterator(); // Spigot

			while (iterator.hasNext()) {
				WatchableObject watchableobject = (WatchableObject) iterator.next();

				if (watchableobject.d()) {
					watchableobject.a(false);
					if (arraylist == null) {
						arraylist = new ArrayList();
					}

					// Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
					if (watchableobject.b() instanceof ItemStack) {
						watchableobject = new WatchableObject(watchableobject.c(), watchableobject.a(), ((ItemStack) watchableobject.b()).cloneItemStack());
					}
					// Spigot end

					arraylist.add(watchableobject);
				}
			}

			f.readLock().unlock();
		}

		e = false;
		return arraylist;
	}

	// Spigot start - protocol patch
	public void a(PacketDataSerializer packetdataserializer) {
		a(packetdataserializer, 5);
	}

	public void a(PacketDataSerializer packetdataserializer, int version) {
		// Spigot end
		f.readLock().lock();
		Iterator iterator = dataValues.valueCollection().iterator(); // Spigot

		while (iterator.hasNext()) {
			WatchableObject watchableobject = (WatchableObject) iterator.next();

			a(packetdataserializer, watchableobject, version); // Spigot - protocol patch
		}

		f.readLock().unlock();
		packetdataserializer.writeByte(127);
	}

	public List c() {
		ArrayList arraylist = new ArrayList(); // Spigot

		f.readLock().lock();

		arraylist.addAll(dataValues.valueCollection()); // Spigot
		// Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
		for (int i = 0; i < arraylist.size(); i++) {
			WatchableObject watchableobject = (WatchableObject) arraylist.get(i);
			if (watchableobject.b() instanceof ItemStack) {
				watchableobject = new WatchableObject(watchableobject.c(), watchableobject.a(), ((ItemStack) watchableobject.b()).cloneItemStack());
				arraylist.set(i, watchableobject);
			}
		}
		// Spigot end

		f.readLock().unlock();
		return arraylist;
	}

	// Spigot start - protocol patch
	private static void a(PacketDataSerializer packetdataserializer, WatchableObject watchableobject, int version) {
		int type = watchableobject.c();
		if (watchableobject.b() instanceof ProtocolData.ByteShort && version >= 16) {
			type = 1;
		}
		if (watchableobject.b() instanceof ProtocolData.IntByte && version >= 28) {
			type = 0;
		}
		if (version < 16 && watchableobject.b() instanceof ProtocolData.HiddenByte)
			return;

		int i = (type << 5 | watchableobject.a() & 31) & 255;

		packetdataserializer.writeByte(i);
		switch (type) {
		case 0:
			if (watchableobject.b() instanceof ProtocolData.DualByte) {
				ProtocolData.DualByte dualByte = (ProtocolData.DualByte) watchableobject.b();
				packetdataserializer.writeByte(version >= 16 ? dualByte.value2 : dualByte.value);
			} else {
				packetdataserializer.writeByte(((Number) watchableobject.b()).byteValue());
			}
			break;

		case 1:
			packetdataserializer.writeShort(((Number) watchableobject.b()).shortValue());
			break;

		case 2:
			int val = ((Number) watchableobject.b()).intValue();
			if (watchableobject.b() instanceof ProtocolData.DualInt && version >= 46) {
				val = ((ProtocolData.DualInt) watchableobject.b()).value2;
			}
			packetdataserializer.writeInt(val);
			break;

		case 3:
			packetdataserializer.writeFloat(((Number) watchableobject.b()).floatValue());
			break;

		case 4:
			try {
				packetdataserializer.a((String) watchableobject.b());
			} catch (java.io.IOException ex) {
				throw new RuntimeException(ex);
			}
			break;

		case 5:
			ItemStack itemstack = (ItemStack) watchableobject.b();

			packetdataserializer.a(itemstack);
			break;

		case 6:
			ChunkCoordinates chunkcoordinates = (ChunkCoordinates) watchableobject.b();

			packetdataserializer.writeInt(chunkcoordinates.x);
			packetdataserializer.writeInt(chunkcoordinates.y);
			packetdataserializer.writeInt(chunkcoordinates.z);
		}
	}

	// Spigot end

	public static List b(PacketDataSerializer packetdataserializer) {
		ArrayList arraylist = null;

		for (byte b0 = packetdataserializer.readByte(); b0 != 127; b0 = packetdataserializer.readByte()) {
			if (arraylist == null) {
				arraylist = new ArrayList();
			}

			int i = (b0 & 224) >> 5;
			int j = b0 & 31;
			WatchableObject watchableobject = null;

			switch (i) {
			case 0:
				watchableobject = new WatchableObject(i, j, Byte.valueOf(packetdataserializer.readByte()));
				break;

			case 1:
				watchableobject = new WatchableObject(i, j, Short.valueOf(packetdataserializer.readShort()));
				break;

			case 2:
				watchableobject = new WatchableObject(i, j, Integer.valueOf(packetdataserializer.readInt()));
				break;

			case 3:
				watchableobject = new WatchableObject(i, j, Float.valueOf(packetdataserializer.readFloat()));
				break;

			case 4:
				try {
					watchableobject = new WatchableObject(i, j, packetdataserializer.c(32767));
				} catch (java.io.IOException ex) {
					throw new RuntimeException(ex);
				}
				break;

			case 5:
				watchableobject = new WatchableObject(i, j, packetdataserializer.c());
				break;

			case 6:
				int k = packetdataserializer.readInt();
				int l = packetdataserializer.readInt();
				int i1 = packetdataserializer.readInt();

				watchableobject = new WatchableObject(i, j, new ChunkCoordinates(k, l, i1));
			}

			arraylist.add(watchableobject);
		}

		return arraylist;
	}

	public boolean d() {
		return b;
	}

	public void e() {
		e = false;
	}

	static {
		// Spigot Start - remove valueOf
		classToId.put(Byte.class, 0);
		classToId.put(Short.class, 1);
		classToId.put(Integer.class, 2);
		classToId.put(Float.class, 3);
		classToId.put(String.class, 4);
		classToId.put(ItemStack.class, 5);
		classToId.put(ChunkCoordinates.class, 6);
		// Spigot End
	}
}
