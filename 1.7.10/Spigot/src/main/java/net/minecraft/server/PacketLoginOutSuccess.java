package net.minecraft.server;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.GameProfile;

public class PacketLoginOutSuccess extends Packet {

	private GameProfile a;

	public PacketLoginOutSuccess() {
	}

	public PacketLoginOutSuccess(GameProfile gameprofile) {
		a = gameprofile;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		String s = packetdataserializer.c(36);
		String s1 = packetdataserializer.c(16);
		UUID uuid = UUID.fromString(s);

		a = new GameProfile(uuid, s1);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		UUID uuid = a.getId();

		packetdataserializer.a(uuid == null ? "" : packetdataserializer.version >= 5 ? uuid.toString() : uuid.toString().replaceAll("-", ""));
		packetdataserializer.a(a.getName());
	}

	public void a(PacketLoginOutListener packetloginoutlistener) {
		packetloginoutlistener.a(this);
	}

	@Override
	public boolean a() {
		return true;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketLoginOutListener) packetlistener);
	}
}
