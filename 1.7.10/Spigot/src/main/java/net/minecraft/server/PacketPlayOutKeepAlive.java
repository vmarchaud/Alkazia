package net.minecraft.server;

public class PacketPlayOutKeepAlive extends Packet {

	private int a;

	public PacketPlayOutKeepAlive() {
	}

	public PacketPlayOutKeepAlive(int i) {
		a = i;
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start
		if (packetdataserializer.version >= 32) {
			packetdataserializer.b(a);
		} else {
			packetdataserializer.writeInt(a);
		}
		// Spigot end
	}

	@Override
	public boolean a() {
		return true;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
