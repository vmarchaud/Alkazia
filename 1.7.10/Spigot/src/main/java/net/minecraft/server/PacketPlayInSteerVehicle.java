package net.minecraft.server;

public class PacketPlayInSteerVehicle extends Packet {

	private float a;
	private float b;
	private boolean c;
	private boolean d;

	public PacketPlayInSteerVehicle() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readFloat();
		b = packetdataserializer.readFloat();
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			c = packetdataserializer.readBoolean();
			d = packetdataserializer.readBoolean();
		} else {
			int flags = packetdataserializer.readUnsignedByte();
			c = (flags & 0x1) != 0;
			d = (flags & 0x2) != 0;
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeFloat(a);
		packetdataserializer.writeFloat(b);
		packetdataserializer.writeBoolean(c);
		packetdataserializer.writeBoolean(d);
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public float c() {
		return a;
	}

	public float d() {
		return b;
	}

	public boolean e() {
		return c;
	}

	public boolean f() {
		return d;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
