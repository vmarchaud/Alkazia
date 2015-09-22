package net.minecraft.server;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketPlayOutMapChunk extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private byte[] e;
	private byte[] f;
	private boolean g;
	private int h;
	private static byte[] i = new byte[196864];

	private Chunk chunk; // Spigot
	private int mask; // Spigot

	public PacketPlayOutMapChunk() {
	}

	// Spigot start - protocol patch
	public PacketPlayOutMapChunk(Chunk chunk, boolean flag, int i, int version) {
		this.chunk = chunk;
		mask = i;
		a = chunk.locX;
		b = chunk.locZ;
		g = flag;
		ChunkMap chunkmap = a(chunk, flag, i, version);

		d = chunkmap.c;
		c = chunkmap.b;

		f = chunkmap.a;
	}

	public static int c() {
		return 196864;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readInt();
		g = packetdataserializer.readBoolean();
		c = packetdataserializer.readShort();
		d = packetdataserializer.readShort();
		h = packetdataserializer.readInt();
		if (i.length < h) {
			i = new byte[h];
		}

		packetdataserializer.readBytes(i, 0, h);
		int i = 0;

		int j;

		for (j = 0; j < 16; ++j) {
			i += c >> j & 1;
		}

		j = 12288 * i;
		if (g) {
			j += 256;
		}

		f = new byte[j];
		Inflater inflater = new Inflater();

		inflater.setInput(PacketPlayOutMapChunk.i, 0, h);

		try {
			inflater.inflate(f);
		} catch (DataFormatException dataformatexception) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeInt(b);
		packetdataserializer.writeBoolean(g);
		packetdataserializer.writeShort((short) (c & '\uffff'));
		// Spigot start - protocol patch
		if (packetdataserializer.version < 27) {
			chunk.world.spigotConfig.antiXrayInstance.obfuscate(chunk.locX, chunk.locZ, mask, f, chunk.world, false); // Spigot
			Deflater deflater = new Deflater(4); // Spigot
			try {
				deflater.setInput(f, 0, f.length);
				deflater.finish();
				e = new byte[f.length];
				h = deflater.deflate(e);
			} finally {
				deflater.end();
			}
			packetdataserializer.writeShort((short) (d & '\uffff'));
			packetdataserializer.writeInt(h);
			packetdataserializer.writeBytes(e, 0, h);
		} else {
			chunk.world.spigotConfig.antiXrayInstance.obfuscate(chunk.locX, chunk.locZ, mask, f, chunk.world, true); // Spigot
			a(packetdataserializer, f);
		}
		// Spigot end - protocol patch
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d", new Object[] { Integer.valueOf(a), Integer.valueOf(b), Boolean.valueOf(g), Integer.valueOf(c), Integer.valueOf(d), Integer.valueOf(h) });
	}

	// Spigot start - protocol patch
	public static ChunkMap a(Chunk chunk, boolean flag, int i, int version) {
		int j = 0;
		ChunkSection[] achunksection = chunk.getSections();
		int k = 0;
		ChunkMap chunkmap = new ChunkMap();
		byte[] abyte = PacketPlayOutMapChunk.i;

		if (flag) {
			chunk.q = true;
		}

		int l;

		for (l = 0; l < achunksection.length; ++l) {
			if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
				chunkmap.b |= 1 << l;
				if (achunksection[l].getExtendedIdArray() != null) {
					chunkmap.c |= 1 << l;
					++k;
				}
			}
		}

		if (version < 24) {
			for (l = 0; l < achunksection.length; ++l) {
				if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
					byte[] abyte1 = achunksection[l].getIdArray();

					System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
					j += abyte1.length;
				}
			}
		} else {
			for (l = 0; l < achunksection.length; ++l) {
				if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
					byte[] abyte1 = achunksection[l].getIdArray();
					NibbleArray nibblearray = achunksection[l].getDataArray();
					for (int ind = 0; ind < abyte1.length; ind++) {
						int id = abyte1[ind] & 0xFF;
						int px = ind & 0xF;
						int py = ind >> 8 & 0xF;
						int pz = ind >> 4 & 0xF;
						int data = nibblearray.a(px, py, pz);
						if (id == 90 && data == 0) {
							Blocks.PORTAL.updateShape(chunk.world, (chunk.locX << 4) + px, (l << 4) + py, (chunk.locZ << 4) + pz);
						} else {
							data = org.spigotmc.SpigotDebreakifier.getCorrectedData(id, data);
						}
						char val = (char) (id << 4 | data);
						abyte[j++] = (byte) (val & 0xFF);
						abyte[j++] = (byte) (val >> 8 & 0xFF);
					}
				}
			}
		}

		NibbleArray nibblearray;

		if (version < 24) {
			for (l = 0; l < achunksection.length; ++l) {
				if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
					nibblearray = achunksection[l].getDataArray();
					System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
					j += nibblearray.a.length;
				}
			}
		}

		for (l = 0; l < achunksection.length; ++l) {
			if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
				nibblearray = achunksection[l].getEmittedLightArray();
				System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
				j += nibblearray.a.length;
			}
		}

		if (!chunk.world.worldProvider.g) {
			for (l = 0; l < achunksection.length; ++l) {
				if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
					nibblearray = achunksection[l].getSkyLightArray();
					System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
					j += nibblearray.a.length;
				}
			}
		}

		if (k > 0 && version < 24) {
			for (l = 0; l < achunksection.length; ++l) {
				if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && achunksection[l].getExtendedIdArray() != null && (i & 1 << l) != 0) {
					nibblearray = achunksection[l].getExtendedIdArray();
					System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
					j += nibblearray.a.length;
				}
			}
		}

		if (flag) {
			byte[] abyte2 = chunk.m();

			System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
			j += abyte2.length;
		}

		chunkmap.a = new byte[j];
		System.arraycopy(abyte, 0, chunkmap.a, 0, j);
		return chunkmap;
	}

	// Spigot end - protocol patch

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
