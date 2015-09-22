package net.minecraft.server;

public class PacketPlayOutBlockBreakAnimation extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;

	public PacketPlayOutBlockBreakAnimation() {
	}

	public PacketPlayOutBlockBreakAnimation(int i, int j, int k, int l, int i1) {
		a = i;
		b = j;
		c = k;
		d = l;
		e = i1;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.a();
		b = packetdataserializer.readInt();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readInt();
		e = packetdataserializer.readUnsignedByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.b(a);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(b);
			packetdataserializer.writeInt(c);
			packetdataserializer.writeInt(d);
		} else {
			packetdataserializer.writePosition(b, c, d);
		}
		// Spigot end
		packetdataserializer.writeByte(e);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
