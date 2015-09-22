package net.minecraft.server;

import java.util.List;

public class PacketPlayOutEntityMetadata extends Packet {

	private int a;
	private List b;

	public PacketPlayOutEntityMetadata() {
	}

	public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
		a = i;
		if (flag) {
			b = datawatcher.c();
		} else {
			b = datawatcher.b();
		}
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = DataWatcher.b(packetdataserializer);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
		} else {
			packetdataserializer.b(a);
		}
		DataWatcher.a(b, packetdataserializer, packetdataserializer.version);
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
