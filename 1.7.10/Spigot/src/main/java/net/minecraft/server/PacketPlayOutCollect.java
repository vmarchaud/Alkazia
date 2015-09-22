package net.minecraft.server;

public class PacketPlayOutCollect extends Packet {

	private int a;
	private int b;

	public PacketPlayOutCollect() {
	}

	public PacketPlayOutCollect(int i, int j) {
		a = i;
		b = j;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeInt(b);
		} else {
			packetdataserializer.b(a);
			packetdataserializer.b(b);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
