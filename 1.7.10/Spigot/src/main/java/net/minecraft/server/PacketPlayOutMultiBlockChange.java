package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketPlayOutMultiBlockChange extends Packet {

	private static final Logger a = LogManager.getLogger();
	private ChunkCoordIntPair b;
	private byte[] c;
	private int d;
	// Spigot start - protocol patch
	private short[] ashort;
	private int[] blocks;
	private Chunk chunk;

	// Spigot end

	public PacketPlayOutMultiBlockChange() {
	}

	public PacketPlayOutMultiBlockChange(int i, short[] ashort, Chunk chunk) {
		// Spigot start
		this.ashort = ashort;
		this.chunk = chunk;
		// Spigot end
		b = new ChunkCoordIntPair(chunk.locX, chunk.locZ);
		d = i;
		int j = 4 * i;

		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(j);
			DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

			// Spigot start
			blocks = new int[i];
			for (int k = 0; k < i; ++k) {
				int l = ashort[k] >> 12 & 15;
				int i1 = ashort[k] >> 8 & 15;
				int j1 = ashort[k] & 255;

				dataoutputstream.writeShort(ashort[k]);
				int blockId = Block.getId(chunk.getType(l, j1, i1));
				int data = chunk.getData(l, j1, i1);
				data = org.spigotmc.SpigotDebreakifier.getCorrectedData(blockId, data);
				int id = (blockId & 4095) << 4 | data & 15;
				dataoutputstream.writeShort((short) id);
				blocks[k] = id;
			}
			// Spigot end

			c = bytearrayoutputstream.toByteArray();
			if (c.length != j)
				throw new RuntimeException("Expected length " + j + " doesn\'t match received length " + c.length);
		} catch (IOException ioexception) {
			a.error("Couldn\'t create bulk block update packet", ioexception);
			c = null;
		}
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		b = new ChunkCoordIntPair(packetdataserializer.readInt(), packetdataserializer.readInt());
		d = packetdataserializer.readShort() & '\uffff';
		int i = packetdataserializer.readInt();

		if (i > 0) {
			c = new byte[i];
			packetdataserializer.readBytes(c);
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 25) {
			packetdataserializer.writeInt(b.x);
			packetdataserializer.writeInt(b.z);
			packetdataserializer.writeShort((short) d);
			if (c != null) {
				packetdataserializer.writeInt(c.length);
				packetdataserializer.writeBytes(c);
			} else {
				packetdataserializer.writeInt(0);
			}
		} else {
			packetdataserializer.writeInt(b.x);
			packetdataserializer.writeInt(b.z);
			packetdataserializer.b(d);
			for (int i = 0; i < d; i++) {
				packetdataserializer.writeShort(ashort[i]);
				packetdataserializer.b(blocks[i]);
			}
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("xc=%d, zc=%d, count=%d", new Object[] { Integer.valueOf(b.x), Integer.valueOf(b.z), Integer.valueOf(d) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
