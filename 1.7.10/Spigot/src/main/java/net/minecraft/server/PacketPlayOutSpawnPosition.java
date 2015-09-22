package net.minecraft.server;

public class PacketPlayOutSpawnPosition extends Packet {

	public int x; // CraftBukkit - private -> public
	public int y; // CraftBukkit - private -> public
	public int z; // CraftBukkit - private -> public

	public PacketPlayOutSpawnPosition() {
	}

	public PacketPlayOutSpawnPosition(int i, int j, int k) {
		x = i;
		y = j;
		z = k;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		x = packetdataserializer.readInt();
		y = packetdataserializer.readInt();
		z = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(x);
			packetdataserializer.writeInt(y);
			packetdataserializer.writeInt(z);

		} else {
			packetdataserializer.writePosition(x, y, z);
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public boolean a() {
		return false;
	}

	@Override
	public String b() {
		return String.format("x=%d, y=%d, z=%d", new Object[] { Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
