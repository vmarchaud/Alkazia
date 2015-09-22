package net.minecraft.server;

public class PacketPlayInKeepAlive extends Packet {

	private int a;

	public PacketPlayInKeepAlive() {
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
		} else {
			a = packetdataserializer.a();
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
	}

	@Override
	public boolean a() {
		return true;
	}

	public int c() {
		return a;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
