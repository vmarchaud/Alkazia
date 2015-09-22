package net.minecraft.server;

public class PacketPlayInCloseWindow extends Packet {

	private int a;

	public PacketPlayInCloseWindow() {
	}

	// CraftBukkit start - Add constructor
	public PacketPlayInCloseWindow(int id) {
		a = id;
	}

	// CraftBukkit end
	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeByte(a);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
