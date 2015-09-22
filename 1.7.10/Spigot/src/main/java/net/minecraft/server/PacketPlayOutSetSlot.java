package net.minecraft.server;

public class PacketPlayOutSetSlot extends Packet {

	public int a; // Spigot
	public int b; // Spigot
	private ItemStack c;

	public PacketPlayOutSetSlot() {
	}

	public PacketPlayOutSetSlot(int i, int j, ItemStack itemstack) {
		a = i;
		b = j;
		c = itemstack == null ? null : itemstack.cloneItemStack();
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readByte();
		b = packetdataserializer.readShort();
		c = packetdataserializer.c();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeByte(a);
		packetdataserializer.writeShort(b);
		packetdataserializer.a(c);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
