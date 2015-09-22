package net.minecraft.server;

import java.io.IOException; // CraftBukkit

public class PacketHandshakingInSetProtocol extends Packet {

	private int a;
	public String b; // CraftBukkit private -> public
	public int c; // CraftBukkit private -> public
	private EnumProtocol d;

	public PacketHandshakingInSetProtocol() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		a = packetdataserializer.a();
		b = packetdataserializer.c(Short.MAX_VALUE); // Spigot
		c = packetdataserializer.readUnsignedShort();
		d = EnumProtocol.a(packetdataserializer.a());
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		packetdataserializer.b(a);
		packetdataserializer.a(b);
		packetdataserializer.writeShort(c);
		packetdataserializer.b(d.c());
	}

	public void a(PacketHandshakingInListener packethandshakinginlistener) {
		packethandshakinginlistener.a(this);
	}

	@Override
	public boolean a() {
		return true;
	}

	public EnumProtocol c() {
		return d;
	}

	public int d() {
		return a;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketHandshakingInListener) packetlistener);
	}
}
