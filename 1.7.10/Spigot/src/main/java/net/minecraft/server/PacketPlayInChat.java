package net.minecraft.server;

import java.io.IOException; // CraftBukkit

public class PacketPlayInChat extends Packet {

	private String message;

	public PacketPlayInChat() {
	}

	public PacketPlayInChat(String s) {
		if (s.length() > 100) {
			s = s.substring(0, 100);
		}

		message = s;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		message = packetdataserializer.c(100);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		packetdataserializer.a(message);
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("message=\'%s\'", new Object[] { message });
	}

	public String c() {
		return message;
	}

	// CraftBukkit start - make chat async
	@Override
	public boolean a() {
		return !message.startsWith("/");
	}

	// CraftBukkit end

	// Spigot Start
	private static final java.util.concurrent.ExecutorService executors = java.util.concurrent.Executors.newCachedThreadPool(new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon(true).setNameFormat("Async Chat Thread - #%d").build());

	@Override
	public void handle(final PacketListener packetlistener) {
		if (a()) {
			executors.submit(new Runnable() {

				@Override
				public void run() {
					PacketPlayInChat.this.a((PacketPlayInListener) packetlistener);
				}
			});
			return;
		}
		// Spigot End
		this.a((PacketPlayInListener) packetlistener);
	}
}
