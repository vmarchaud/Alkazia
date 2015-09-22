package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutChat extends Packet {

	private IChatBaseComponent a;
	private boolean b;

	public PacketPlayOutChat() {
		b = true;
	}

	public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
		this(ichatbasecomponent, true);
	}

	public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, boolean flag) {
		b = true;
		a = ichatbasecomponent;
		b = flag;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = ChatSerializer.a(packetdataserializer.c(32767));
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.a(ChatSerializer.a(a));
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 16) {
			packetdataserializer.writeByte(0);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("message=\'%s\'", new Object[] { a });
	}

	public boolean d() {
		return b;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
