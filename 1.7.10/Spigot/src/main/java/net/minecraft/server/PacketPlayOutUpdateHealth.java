package net.minecraft.server;

public class PacketPlayOutUpdateHealth extends Packet {

	private float a;
	private int b;
	private float c;

	public PacketPlayOutUpdateHealth() {
	}

	public PacketPlayOutUpdateHealth(float f, int i, float f1) {
		a = f;
		b = i;
		c = f1;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readFloat();
		b = packetdataserializer.readShort();
		c = packetdataserializer.readFloat();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeFloat(a);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeShort(b);
		} else {
			packetdataserializer.b(b);
		}
		// Spigot end
		packetdataserializer.writeFloat(c);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
