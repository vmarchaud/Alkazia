package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {

    private final Entity a;
    private boolean b = true;
    // Spigot Start
    private static final gnu.trove.map.TObjectIntMap classToId = new gnu.trove.map.hash.TObjectIntHashMap( 10, 0.5f, -1 );
    private final gnu.trove.map.TIntObjectMap dataValues = new gnu.trove.map.hash.TIntObjectHashMap( 10, 0.5f, -1 );
    // These exist as an attempt at backwards compatability for (broken) NMS plugins
    private static final Map c = gnu.trove.TDecorators.wrap( classToId );
    private final Map d = gnu.trove.TDecorators.wrap( dataValues );
    // Spigot End
    private boolean e;
    private ReadWriteLock f = new ReentrantReadWriteLock();

    public DataWatcher(Entity entity) {
        this.a = entity;
    }

    public void a(int i, Object object) {
        int integer = classToId.get(object.getClass()); // Spigot

        if (integer == -1) { // Spigot
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        } else if (i > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
        } else if (this.dataValues.containsKey(i)) { // Spigot
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else {
            WatchableObject watchableobject = new WatchableObject(integer, i, object); // Spigot

            this.f.writeLock().lock();
            this.dataValues.put(i, watchableobject); // Spigot
            this.f.writeLock().unlock();
            this.b = false;
        }
    }

    public void a(int i, int j) {
        WatchableObject watchableobject = new WatchableObject(j, i, null);

        this.f.writeLock().lock();
        this.dataValues.put(i, watchableobject); // Spigot
        this.f.writeLock().unlock();
        this.b = false;
    }

    public byte getByte(int i) {
        return ((Byte) this.i(i).b()).byteValue();
    }

    public short getShort(int i) {
        return ((Short) this.i(i).b()).shortValue();
    }

    public int getInt(int i) {
        return ((Integer) this.i(i).b()).intValue();
    }

    public float getFloat(int i) {
        return ((Float) this.i(i).b()).floatValue();
    }

    public String getString(int i) {
        return (String) this.i(i).b();
    }

    public ItemStack getItemStack(int i) {
        return (ItemStack) this.i(i).b();
    }

    private WatchableObject i(int i) {
        this.f.readLock().lock();

        WatchableObject watchableobject;

        try {
            watchableobject = (WatchableObject) this.dataValues.get(i); // Spigot
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting synched entity data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Synched entity data");

            crashreportsystemdetails.a("Data ID", Integer.valueOf(i));
            throw new ReportedException(crashreport);
        }

        this.f.readLock().unlock();
        return watchableobject;
    }

    public void watch(int i, Object object) {
        WatchableObject watchableobject = this.i(i);

        if (ObjectUtils.notEqual(object, watchableobject.b())) {
            watchableobject.a(object);
            this.a.i(i);
            watchableobject.a(true);
            this.e = true;
        }
    }

    public void h(int i) {
        WatchableObject.a(this.i(i), true);
        this.e = true;
    }

    public boolean a() {
        return this.e;
    }

    public static void a(List list, PacketDataSerializer packetdataserializer) {
        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WatchableObject watchableobject = (WatchableObject) iterator.next();

                a(packetdataserializer, watchableobject);
            }
        }

        packetdataserializer.writeByte(127);
    }

    public List b() {
        ArrayList arraylist = null;

        if (this.e) {
            this.f.readLock().lock();
            Iterator iterator = this.dataValues.valueCollection().iterator(); // Spigot

            while (iterator.hasNext()) {
                WatchableObject watchableobject = (WatchableObject) iterator.next();

                if (watchableobject.d()) {
                    watchableobject.a(false);
                    if (arraylist == null) {
                        arraylist = new ArrayList();
                    }

                    arraylist.add(watchableobject);
                }
            }

            this.f.readLock().unlock();
        }

        this.e = false;
        return arraylist;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.f.readLock().lock();
        Iterator iterator = this.dataValues.valueCollection().iterator(); // Spigot

        while (iterator.hasNext()) {
            WatchableObject watchableobject = (WatchableObject) iterator.next();

            a(packetdataserializer, watchableobject);
        }

        this.f.readLock().unlock();
        packetdataserializer.writeByte(127);
    }

    public List c() {
        ArrayList arraylist = new ArrayList(); // Spigot

        this.f.readLock().lock();

        arraylist.addAll(this.dataValues.valueCollection()); // Spigot

        this.f.readLock().unlock();
        return arraylist;
    }

    private static void a(PacketDataSerializer packetdataserializer, WatchableObject watchableobject) {
        int i = (watchableobject.c() << 5 | watchableobject.a() & 31) & 255;

        packetdataserializer.writeByte(i);
        switch (watchableobject.c()) {
        case 0:
            packetdataserializer.writeByte(((Byte) watchableobject.b()).byteValue());
            break;

        case 1:
            packetdataserializer.writeShort(((Short) watchableobject.b()).shortValue());
            break;

        case 2:
            packetdataserializer.writeInt(((Integer) watchableobject.b()).intValue());
            break;

        case 3:
            packetdataserializer.writeFloat(((Float) watchableobject.b()).floatValue());
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
        return this.b;
    }

    public void e() {
        this.e = false;
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
