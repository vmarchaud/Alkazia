package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PersistentCollection {

	private IDataManager a;
	private Map b = new HashMap();
	public List c = new ArrayList(); // Spigot
	private Map d = new HashMap();

	public PersistentCollection(IDataManager idatamanager) {
		a = idatamanager;
		b();
	}

	public PersistentBase get(Class oclass, String s) {
		PersistentBase persistentbase = (PersistentBase) b.get(s);

		if (persistentbase != null)
			return persistentbase;
		else {
			if (a != null) {
				try {
					File file1 = a.getDataFile(s);

					if (file1 != null && file1.exists()) {
						try {
							persistentbase = (PersistentBase) oclass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { s });
						} catch (Exception exception) {
							throw new RuntimeException("Failed to instantiate " + oclass.toString(), exception);
						}

						FileInputStream fileinputstream = new FileInputStream(file1);
						NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(fileinputstream);

						fileinputstream.close();
						persistentbase.a(nbttagcompound.getCompound("data"));
					}
				} catch (Exception exception1) {
					exception1.printStackTrace();
				}
			}

			if (persistentbase != null) {
				b.put(s, persistentbase);
				c.add(persistentbase);
			}

			return persistentbase;
		}
	}

	public void a(String s, PersistentBase persistentbase) {
		if (persistentbase == null)
			throw new RuntimeException("Can\'t set null data");
		else {
			if (b.containsKey(s)) {
				c.remove(b.remove(s));
			}

			b.put(s, persistentbase);
			c.add(persistentbase);
		}
	}

	public void a() {
		for (int i = 0; i < c.size(); ++i) {
			PersistentBase persistentbase = (PersistentBase) c.get(i);

			if (persistentbase.d()) {
				this.a(persistentbase);
				persistentbase.a(false);
			}
		}
	}

	private void a(PersistentBase persistentbase) {
		if (a != null) {
			try {
				File file1 = a.getDataFile(persistentbase.id);

				if (file1 != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();

					persistentbase.b(nbttagcompound);
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();

					nbttagcompound1.set("data", nbttagcompound);
					FileOutputStream fileoutputstream = new FileOutputStream(file1);

					NBTCompressedStreamTools.a(nbttagcompound1, fileoutputstream);
					fileoutputstream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	private void b() {
		try {
			d.clear();
			if (a == null)
				return;

			File file1 = a.getDataFile("idcounts");

			if (file1 != null && file1.exists()) {
				DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
				NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);

				datainputstream.close();
				Iterator iterator = nbttagcompound.c().iterator();

				while (iterator.hasNext()) {
					String s = (String) iterator.next();
					NBTBase nbtbase = nbttagcompound.get(s);

					if (nbtbase instanceof NBTTagShort) {
						NBTTagShort nbttagshort = (NBTTagShort) nbtbase;
						short short1 = nbttagshort.e();

						d.put(s, Short.valueOf(short1));
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public int a(String s) {
		Short oshort = (Short) d.get(s);

		if (oshort == null) {
			oshort = Short.valueOf((short) 0);
		} else {
			oshort = Short.valueOf((short) (oshort.shortValue() + 1));
		}

		d.put(s, oshort);
		if (a == null)
			return oshort.shortValue();
		else {
			try {
				File file1 = a.getDataFile("idcounts");

				if (file1 != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					Iterator iterator = d.keySet().iterator();

					while (iterator.hasNext()) {
						String s1 = (String) iterator.next();
						short short1 = ((Short) d.get(s1)).shortValue();

						nbttagcompound.setShort(s1, short1);
					}

					DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

					NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
					dataoutputstream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return oshort.shortValue();
		}
	}
}
