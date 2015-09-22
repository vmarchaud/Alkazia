package net.minecraft.server;

public class PacketPlayOutBed extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;

	public PacketPlayOutBed() {
	}

	public PacketPlayOutBed(EntityHuman entityhuman, int i, int j, int k) {
		b = i;
		c = j;
		d = k;
		a = entityhuman.getId();
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readInt();
		c = packetdataserializer.readByte();
		d = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeInt(b);
			packetdataserializer.writeByte(c);
			packetdataserializer.writeInt(d);
		} else {
			packetdataserializer.b(a);
			packetdataserializer.writePosition(b, c, d);
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
