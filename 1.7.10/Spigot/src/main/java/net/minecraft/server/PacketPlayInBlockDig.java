package net.minecraft.server;

public class PacketPlayInBlockDig extends Packet {

	private int a;
	private int b;
	private int c;
	private int face;
	private int e;

	public PacketPlayInBlockDig() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		e = packetdataserializer.readUnsignedByte();
		// Spigot start
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			b = packetdataserializer.readUnsignedByte();
			c = packetdataserializer.readInt();
		} else {
			long position = packetdataserializer.readLong();
			a = packetdataserializer.readPositionX(position);
			b = packetdataserializer.readPositionY(position);
			c = packetdataserializer.readPositionZ(position);
		}
		// Spigot end
		face = packetdataserializer.readUnsignedByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeByte(e);
		packetdataserializer.writeInt(a);
		packetdataserializer.writeByte(b);
		packetdataserializer.writeInt(c);
		packetdataserializer.writeByte(face);
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

	public int f() {
		return face;
	}

	public int g() {
		return e;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
