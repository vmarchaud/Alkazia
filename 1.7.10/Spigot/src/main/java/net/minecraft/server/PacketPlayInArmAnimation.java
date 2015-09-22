package net.minecraft.server;

public class PacketPlayInArmAnimation extends Packet {

	private int a;
	private int b;

	public PacketPlayInArmAnimation() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			b = packetdataserializer.readByte();
		} else {
			b = 1;
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeByte(b);
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, type=%d", new Object[] { Integer.valueOf(a), Integer.valueOf(b) });
	}

	public int d() {
		return b;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
