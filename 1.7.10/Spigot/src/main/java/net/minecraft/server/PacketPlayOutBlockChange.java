package net.minecraft.server;

public class PacketPlayOutBlockChange extends Packet {

	private int a;
	private int b;
	private int c;
	public Block block; // CraftBukkit - public
	public int data; // CraftBukkit - public

	public PacketPlayOutBlockChange() {
	}

	public PacketPlayOutBlockChange(int i, int j, int k, World world) {
		a = i;
		b = j;
		c = k;
		block = world.getType(i, j, k);
		data = world.getData(i, j, k);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readUnsignedByte();
		c = packetdataserializer.readInt();
		block = Block.getById(packetdataserializer.a());
		data = packetdataserializer.readUnsignedByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 25) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeByte(b);
			packetdataserializer.writeInt(c);
			packetdataserializer.b(Block.getId(block));
			packetdataserializer.writeByte(data);
		} else {
			packetdataserializer.writePosition(a, b, c);
			int id = Block.getId(block);
			data = org.spigotmc.SpigotDebreakifier.getCorrectedData(id, data);
			packetdataserializer.b(id << 4 | data);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("type=%d, data=%d, x=%d, y=%d, z=%d", new Object[] { Integer.valueOf(Block.getId(block)), Integer.valueOf(data), Integer.valueOf(a), Integer.valueOf(b), Integer.valueOf(c) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
