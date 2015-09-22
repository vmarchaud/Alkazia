package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInUpdateSign extends Packet {

	private int a;
	private int b;
	private int c;
	private String[] d;

	public PacketPlayInUpdateSign() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			b = packetdataserializer.readShort();
			c = packetdataserializer.readInt();
		} else {
			long position = packetdataserializer.readLong();
			a = packetdataserializer.readPositionX(position);
			b = packetdataserializer.readPositionY(position);
			c = packetdataserializer.readPositionZ(position);
		}
		// Spigot end
		d = new String[4];

		for (int i = 0; i < 4; ++i) {
			// Spigot start - protocol patch
			if (packetdataserializer.version < 21) {
				d[i] = packetdataserializer.c(15);
			} else {
				d[i] = ChatSerializer.a(packetdataserializer.c(Short.MAX_VALUE)).c();
			}
			if (d[i].length() > 15) {
				d[i] = d[i].substring(0, 15);
			}
			// Spigot end
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeShort(b);
		packetdataserializer.writeInt(c);

		for (int i = 0; i < 4; ++i) {
			packetdataserializer.a(d[i]);
		}
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public int c() {
		return a;
	}

	public int d() {
		return b;
	}

	public int e() {
		return c;
	}

	public String[] f() {
		return d;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
