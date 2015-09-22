package net.minecraft.server;

import java.util.List;

public class PacketPlayOutWindowItems extends Packet {

	public int a; // Spigot
	public ItemStack[] b; // Spigot

	public PacketPlayOutWindowItems() {
	}

	public PacketPlayOutWindowItems(int i, List list) {
		a = i;
		b = new ItemStack[list.size()];

		for (int j = 0; j < b.length; ++j) {
			ItemStack itemstack = (ItemStack) list.get(j);

			b[j] = itemstack == null ? null : itemstack.cloneItemStack();
		}
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readUnsignedByte();
		short short1 = packetdataserializer.readShort();

		b = new ItemStack[short1];

		for (int i = 0; i < short1; ++i) {
			b[i] = packetdataserializer.c();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeByte(a);
		packetdataserializer.writeShort(b.length);
		ItemStack[] aitemstack = b;
		int i = aitemstack.length;

		for (int j = 0; j < i; ++j) {
			ItemStack itemstack = aitemstack[j];

			packetdataserializer.a(itemstack);
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
