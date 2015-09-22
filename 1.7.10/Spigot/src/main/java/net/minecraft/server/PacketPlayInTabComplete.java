package net.minecraft.server;

import java.io.IOException;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class PacketPlayInTabComplete extends Packet {

	private String a;

	public PacketPlayInTabComplete() {
	}

	public PacketPlayInTabComplete(String s) {
		a = s;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(32767);
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 37) {
			if (packetdataserializer.readBoolean()) {
				long position = packetdataserializer.readLong();
			}
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.a(StringUtils.substring(a, 0, 32767));
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public String c() {
		return a;
	}

	@Override
	public String b() {
		return String.format("message=\'%s\'", new Object[] { a });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
