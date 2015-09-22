package net.minecraft.server;

public class PacketPlayOutBlockAction extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private Block f;

	public PacketPlayOutBlockAction() {
	}

	public PacketPlayOutBlockAction(int i, int j, int k, Block block, int l, int i1) {
		a = i;
		b = j;
		c = k;
		d = l;
		e = i1;
		f = block;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readShort();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readUnsignedByte();
		e = packetdataserializer.readUnsignedByte();
		f = Block.getById(packetdataserializer.a() & 4095);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeShort(b);
			packetdataserializer.writeInt(c);
		} else {
			packetdataserializer.writePosition(a, b, c);
		}
		// Spigot end
		packetdataserializer.writeByte(d);
		packetdataserializer.writeByte(e);
		packetdataserializer.b(Block.getId(f) & 4095);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
