package net.minecraft.server;

public class PacketPlayOutTileEntityData extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private NBTTagCompound e;

	public PacketPlayOutTileEntityData() {
	}

	public PacketPlayOutTileEntityData(int i, int j, int k, int l, NBTTagCompound nbttagcompound) {
		a = i;
		b = j;
		c = k;
		d = l;
		e = nbttagcompound;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readShort();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readUnsignedByte();
		e = packetdataserializer.b();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeShort(b);
			packetdataserializer.writeInt(c);
		} else {
			packetdataserializer.writePosition(a, b, c);
		}
		// Spigot end
		packetdataserializer.writeByte((byte) d);
		packetdataserializer.a(e);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
