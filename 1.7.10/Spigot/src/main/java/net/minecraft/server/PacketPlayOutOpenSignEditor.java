package net.minecraft.server;

public class PacketPlayOutOpenSignEditor extends Packet {

	private int a;
	private int b;
	private int c;

	public PacketPlayOutOpenSignEditor() {
	}

	public PacketPlayOutOpenSignEditor(int i, int j, int k) {
		a = i;
		b = j;
		c = k;
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readInt();
		c = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeInt(b);
			packetdataserializer.writeInt(c);
		} else {
			packetdataserializer.writePosition(a, b, c);
		}
		// Spigot end
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
