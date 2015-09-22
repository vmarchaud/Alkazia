package net.minecraft.server;

public class PacketPlayOutExperience extends Packet {

	private float a;
	private int b;
	private int c;

	public PacketPlayOutExperience() {
	}

	public PacketPlayOutExperience(float f, int i, int j) {
		a = f;
		b = i;
		c = j;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readFloat();
		c = packetdataserializer.readShort();
		b = packetdataserializer.readShort();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeFloat(a);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeShort(c);
			packetdataserializer.writeShort(b);
		} else {
			packetdataserializer.b(c);
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
