package net.minecraft.server;

public class PacketPlayOutWorldEvent extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private boolean f;

	public PacketPlayOutWorldEvent() {
	}

	public PacketPlayOutWorldEvent(int i, int j, int k, int l, int i1, boolean flag) {
		a = i;
		c = j;
		d = k;
		e = l;
		b = i1;
		f = flag;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readByte() & 255;
		e = packetdataserializer.readInt();
		b = packetdataserializer.readInt();
		f = packetdataserializer.readBoolean();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(c);
			packetdataserializer.writeByte(d & 255);
			packetdataserializer.writeInt(e);
		} else {
			packetdataserializer.writePosition(c, d, e);
		}
		// Spigot end
		packetdataserializer.writeInt(b);
		packetdataserializer.writeBoolean(f);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
