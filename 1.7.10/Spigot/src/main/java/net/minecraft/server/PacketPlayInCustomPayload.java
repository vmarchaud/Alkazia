package net.minecraft.server;

import java.io.IOException; // CraftBukkit

public class PacketPlayInCustomPayload extends Packet {

	private String tag;
	public int length; // CraftBukkit - private -> public
	private byte[] data;

	public PacketPlayInCustomPayload() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		tag = packetdataserializer.c(20);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 29) {
			length = packetdataserializer.readShort();
		} else {
			length = packetdataserializer.readableBytes();
		}
		// Spigot end
		if (length > 0 && length < 32767) {
			data = new byte[length];
			packetdataserializer.readBytes(data);
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		packetdataserializer.a(tag);
		packetdataserializer.writeShort((short) length);
		if (data != null) {
			packetdataserializer.writeBytes(data);
		}
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public String c() {
		return tag;
	}

	public byte[] e() {
		return data;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
