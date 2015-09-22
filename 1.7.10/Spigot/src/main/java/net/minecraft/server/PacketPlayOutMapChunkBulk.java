package net.minecraft.server;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PacketPlayOutMapChunkBulk extends Packet {

	private int[] a;
	private int[] b;
	private int[] c;
	private int[] d;
	private byte[] buffer;
	private byte[][] inflatedBuffers;
	private int size;
	private boolean h;
	private byte[] buildBuffer = new byte[0]; // CraftBukkit - remove static
	// CraftBukkit start
	static final ThreadLocal<Deflater> localDeflater = new ThreadLocal<Deflater>() {
		@Override
		protected Deflater initialValue() {
			// Don't use higher compression level, slows things down too much
			return new Deflater(4); // Spigot 6 -> 4
		}
	};
	// CraftBukkit end
	private World world; // Spigot

	public PacketPlayOutMapChunkBulk() {
	}

	public PacketPlayOutMapChunkBulk(List list, int version) {
		int i = list.size();

		a = new int[i];
		b = new int[i];
		c = new int[i];
		d = new int[i];
		inflatedBuffers = new byte[i][];
		h = !list.isEmpty() && !((Chunk) list.get(0)).world.worldProvider.g;
		int j = 0;

		for (int k = 0; k < i; ++k) {
			Chunk chunk = (Chunk) list.get(k);
			ChunkMap chunkmap = PacketPlayOutMapChunk.a(chunk, true, '\uffff', version);

			// Spigot start
			world = chunk.world;
			/*
			if (buildBuffer.length < j + chunkmap.a.length) {
			    byte[] abyte = new byte[j + chunkmap.a.length];

			    System.arraycopy(buildBuffer, 0, abyte, 0, buildBuffer.length);
			    buildBuffer = abyte;
			}

			System.arraycopy(chunkmap.a, 0, buildBuffer, j, chunkmap.a.length);
			*/
			// Spigot end
			j += chunkmap.a.length;
			a[k] = chunk.locX;
			b[k] = chunk.locZ;
			c[k] = chunkmap.b;
			d[k] = chunkmap.c;
			inflatedBuffers[k] = chunkmap.a;
		}

		/* CraftBukkit start - Moved to compress()
		Deflater deflater = new Deflater(-1);

		try {
		    deflater.setInput(buildBuffer, 0, j);
		    deflater.finish();
		    this.buffer = new byte[j];
		    this.size = deflater.deflate(this.buffer);
		} finally {
		    deflater.end();
		}
		*/
	}

	// Add compression method
	public void compress() {
		if (buffer != null)
			return;
		// Spigot start
		int finalBufferSize = 0;
		// Obfuscate all sections
		for (int i = 0; i < a.length; i++) {
			world.spigotConfig.antiXrayInstance.obfuscate(a[i], b[i], c[i], inflatedBuffers[i], world, false);
			finalBufferSize += inflatedBuffers[i].length;
		}

		// Now it's time to efficiently copy the chunk to the build buffer
		buildBuffer = new byte[finalBufferSize];
		int bufferLocation = 0;
		for (int i = 0; i < a.length; i++) {
			System.arraycopy(inflatedBuffers[i], 0, buildBuffer, bufferLocation, inflatedBuffers[i].length);
			bufferLocation += inflatedBuffers[i].length;
		}
		// Spigot end

		Deflater deflater = localDeflater.get();
		deflater.reset();
		deflater.setInput(buildBuffer);
		deflater.finish();

		buffer = new byte[buildBuffer.length + 100];
		size = deflater.deflate(buffer);
	}

	// CraftBukkit end

	public static int c() {
		return 5;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - throws IOException
		short short1 = packetdataserializer.readShort();

		size = packetdataserializer.readInt();
		h = packetdataserializer.readBoolean();
		a = new int[short1];
		b = new int[short1];
		c = new int[short1];
		d = new int[short1];
		inflatedBuffers = new byte[short1][];
		if (buildBuffer.length < size) {
			buildBuffer = new byte[size];
		}

		packetdataserializer.readBytes(buildBuffer, 0, size);
		byte[] abyte = new byte[PacketPlayOutMapChunk.c() * short1];
		Inflater inflater = new Inflater();

		inflater.setInput(buildBuffer, 0, size);

		try {
			inflater.inflate(abyte);
		} catch (DataFormatException dataformatexception) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}

		int i = 0;

		for (int j = 0; j < short1; ++j) {
			a[j] = packetdataserializer.readInt();
			b[j] = packetdataserializer.readInt();
			c[j] = packetdataserializer.readShort();
			d[j] = packetdataserializer.readShort();
			int k = 0;
			int l = 0;

			int i1;

			for (i1 = 0; i1 < 16; ++i1) {
				k += c[j] >> i1 & 1;
				l += d[j] >> i1 & 1;
			}

			i1 = 2048 * 4 * k + 256;
			i1 += 2048 * l;
			if (h) {
				i1 += 2048 * k;
			}

			inflatedBuffers[j] = new byte[i1];
			System.arraycopy(abyte, i, inflatedBuffers[j], 0, i1);
			i += i1;
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - throws IOException
		if (packetdataserializer.version < 27) {
			compress(); // CraftBukkit
			packetdataserializer.writeShort(a.length);
			packetdataserializer.writeInt(size);
			packetdataserializer.writeBoolean(h);
			packetdataserializer.writeBytes(buffer, 0, size);

			for (int i = 0; i < a.length; ++i) {
				packetdataserializer.writeInt(a[i]);
				packetdataserializer.writeInt(b[i]);
				packetdataserializer.writeShort((short) (c[i] & '\uffff'));
				packetdataserializer.writeShort((short) (d[i] & '\uffff'));
			}
		} else {
			packetdataserializer.writeBoolean(h);
			packetdataserializer.b(a.length);

			for (int i = 0; i < a.length; ++i) {
				packetdataserializer.writeInt(a[i]);
				packetdataserializer.writeInt(b[i]);
				packetdataserializer.writeShort((short) (c[i] & '\uffff'));
			}
			for (int i = 0; i < a.length; ++i) {
				world.spigotConfig.antiXrayInstance.obfuscate(a[i], b[i], c[i], inflatedBuffers[i], world, true);
				packetdataserializer.writeBytes(inflatedBuffers[i]);
			}
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		StringBuilder stringbuilder = new StringBuilder();

		for (int i = 0; i < a.length; ++i) {
			if (i > 0) {
				stringbuilder.append(", ");
			}

			stringbuilder.append(String.format("{x=%d, z=%d, sections=%d, adds=%d, data=%d}", new Object[] { Integer.valueOf(a[i]), Integer.valueOf(b[i]), Integer.valueOf(c[i]), Integer.valueOf(d[i]), Integer.valueOf(inflatedBuffers[i].length) }));
		}

		return String.format("size=%d, chunks=%d[%s]", new Object[] { Integer.valueOf(size), Integer.valueOf(a.length), stringbuilder });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
