package net.minecraft.server;

public class PacketPlayOutEntityEquipment extends Packet {

	private int a;
	private int b;
	private ItemStack c;

	public PacketPlayOutEntityEquipment() {
	}

	public PacketPlayOutEntityEquipment(int i, int j, ItemStack itemstack) {
		a = i;
		b = j;
		c = itemstack == null ? null : itemstack.cloneItemStack();
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readShort();
		c = packetdataserializer.c();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
		} else {
			packetdataserializer.b(a);
		}
		// Spigot end
		packetdataserializer.writeShort(b);
		packetdataserializer.a(c);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("entity=%d, slot=%d, item=%s", new Object[] { Integer.valueOf(a), Integer.valueOf(b), c });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
