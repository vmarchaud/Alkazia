package net.minecraft.server;

import java.util.Arrays;

public class PacketPlayOutMap extends Packet {

	private int a;
	private byte[] b;
	private byte scale; // Spigot - protocol patch

	public PacketPlayOutMap() {
	}

	// Spigot start - protocol patch
	public PacketPlayOutMap(int i, byte[] abyte, byte scale) {
		this.scale = scale;
		// Spigot end
		a = i;
		b = abyte;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.a();
		b = new byte[packetdataserializer.readUnsignedShort()];
		packetdataserializer.readBytes(b);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.b(a);
		if (packetdataserializer.version < 27) {
			packetdataserializer.writeShort(b.length);
			packetdataserializer.writeBytes(b);
		} else {
			packetdataserializer.writeByte(scale);
			if (b[0] == 1) {
				int count = (b.length - 1) / 3;
				packetdataserializer.b(count);
				for (int i = 0; i < count; i++) {
					packetdataserializer.writeByte(b[1 + i * 3]);
					packetdataserializer.writeByte(b[2 + i * 3]);
					packetdataserializer.writeByte(b[3 + i * 3]);
				}
			} else {
				packetdataserializer.b(0);
			}

			if (b[0] == 0) {
				packetdataserializer.writeByte(1);
				int rows = b.length - 3;
				packetdataserializer.writeByte(rows);
				packetdataserializer.writeByte(b[1]);
				packetdataserializer.writeByte(b[2]);
				a(packetdataserializer, Arrays.copyOfRange(b, 3, b.length));
			} else {
				packetdataserializer.writeByte(0);
			}
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, length=%d", new Object[] { Integer.valueOf(a), Integer.valueOf(b.length) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
