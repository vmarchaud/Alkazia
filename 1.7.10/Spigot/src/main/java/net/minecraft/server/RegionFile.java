package net.minecraft.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {

	private static final byte[] a = new byte[4096];
	private final File b;
	private RandomAccessFile c;
	private final int[] d = new int[1024];
	private final int[] e = new int[1024];
	private ArrayList f;
	private int g;
	private long h;

	public RegionFile(File file1) {
		b = file1;
		g = 0;

		try {
			if (file1.exists()) {
				h = file1.lastModified();
			}

			c = new RandomAccessFile(file1, "rw");
			int i;

			if (c.length() < 4096L) {
				for (i = 0; i < 1024; ++i) {
					c.writeInt(0);
				}

				for (i = 0; i < 1024; ++i) {
					c.writeInt(0);
				}

				g += 8192;
			}

			if ((c.length() & 4095L) != 0L) {
				for (i = 0; i < (c.length() & 4095L); ++i) {
					c.write(0);
				}
			}

			i = (int) c.length() / 4096;
			f = new ArrayList(i);

			int j;

			for (j = 0; j < i; ++j) {
				f.add(Boolean.valueOf(true));
			}

			f.set(0, Boolean.valueOf(false));
			f.set(1, Boolean.valueOf(false));
			c.seek(0L);

			int k;

			for (j = 0; j < 1024; ++j) {
				k = c.readInt();
				d[j] = k;
				if (k != 0 && (k >> 8) + (k & 255) <= f.size()) {
					for (int l = 0; l < (k & 255); ++l) {
						f.set((k >> 8) + l, Boolean.valueOf(false));
					}
				}
			}

			for (j = 0; j < 1024; ++j) {
				k = c.readInt();
				e[j] = k;
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	// CraftBukkit start - This is a copy (sort of) of the method below it, make sure they stay in sync
	public synchronized boolean chunkExists(int i, int j) {
		if (d(i, j))
			return false;
		else {
			try {
				int k = e(i, j);

				if (k == 0)
					return false;
				else {
					int l = k >> 8;
					int i1 = k & 255;

					if (l + i1 > f.size())
				return false;

					c.seek(l * 4096);
					int j1 = c.readInt();

					if (j1 > 4096 * i1 || j1 <= 0)
				return false;

					byte b0 = c.readByte();
					if (b0 == 1 || b0 == 2)
				return true;
				}
			} catch (IOException ioexception) {
				return false;
			}
		}

		return false;
	}

	// CraftBukkit end

	public synchronized DataInputStream a(int i, int j) {
		if (d(i, j))
			return null;
		else {
			try {
				int k = e(i, j);

				if (k == 0)
					return null;
				else {
					int l = k >> 8;
					int i1 = k & 255;

					if (l + i1 > f.size())
				return null;
			else {
						c.seek(l * 4096);
						int j1 = c.readInt();

						if (j1 > 4096 * i1)
					return null;
				else if (j1 <= 0)
					return null;
				else {
							byte b0 = c.readByte();
							byte[] abyte;

							if (b0 == 1) {
								abyte = new byte[j1 - 1];
								c.read(abyte);
								return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
							} else if (b0 == 2) {
								abyte = new byte[j1 - 1];
								c.read(abyte);
								return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
							} else
						return null;
						}
					}
				}
			} catch (IOException ioexception) {
				return null;
			}
		}
	}

	public DataOutputStream b(int i, int j) {
		return d(i, j) ? null : new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(this, i, j)));
	}

	protected synchronized void a(int i, int j, byte[] abyte, int k) {
		try {
			int l = e(i, j);
			int i1 = l >> 8;
			int j1 = l & 255;
			int k1 = (k + 5) / 4096 + 1;

			if (k1 >= 256)
					return;

			if (i1 != 0 && j1 == k1) {
				this.a(i1, abyte, k);
			} else {
				int l1;

				for (l1 = 0; l1 < j1; ++l1) {
					f.set(i1 + l1, Boolean.valueOf(true));
				}

				l1 = f.indexOf(Boolean.valueOf(true));
				int i2 = 0;
				int j2;

				if (l1 != -1) {
					for (j2 = l1; j2 < f.size(); ++j2) {
						if (i2 != 0) {
							if (((Boolean) f.get(j2)).booleanValue()) {
								++i2;
							} else {
								i2 = 0;
							}
						} else if (((Boolean) f.get(j2)).booleanValue()) {
							l1 = j2;
							i2 = 1;
						}

						if (i2 >= k1) {
							break;
						}
					}
				}

				if (i2 >= k1) {
					i1 = l1;
					this.a(i, j, l1 << 8 | k1);

					for (j2 = 0; j2 < k1; ++j2) {
						f.set(i1 + j2, Boolean.valueOf(false));
					}

					this.a(i1, abyte, k);
				} else {
					c.seek(c.length());
					i1 = f.size();

					for (j2 = 0; j2 < k1; ++j2) {
						c.write(a);
						f.add(Boolean.valueOf(false));
					}

					g += 4096 * k1;
					this.a(i1, abyte, k);
					this.a(i, j, i1 << 8 | k1);
				}
			}

			this.b(i, j, (int) (MinecraftServer.ar() / 1000L));
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	private void a(int i, byte[] abyte, int j) throws IOException { // CraftBukkit - added throws
		c.seek(i * 4096);
		c.writeInt(j + 1);
		c.writeByte(2);
		c.write(abyte, 0, j);
	}

	private boolean d(int i, int j) {
		return i < 0 || i >= 32 || j < 0 || j >= 32;
	}

	private int e(int i, int j) {
		return d[i + j * 32];
	}

	public boolean c(int i, int j) {
		return e(i, j) != 0;
	}

	private void a(int i, int j, int k) throws IOException { // CraftBukkit - added throws
		d[i + j * 32] = k;
		c.seek((i + j * 32) * 4);
		c.writeInt(k);
	}

	private void b(int i, int j, int k) throws IOException { // CraftBukkit - added throws
		e[i + j * 32] = k;
		c.seek(4096 + (i + j * 32) * 4);
		c.writeInt(k);
	}

	public void c() throws IOException { // CraftBukkit - added throws
		if (c != null) {
			c.close();
		}
	}
}
