package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInSettings extends Packet {

	private String a;
	private int b;
	private EnumChatVisibility c;
	private boolean d;
	private EnumDifficulty e;
	private boolean f;

	// Spigot start - protocol patch
	public int version;
	public int flags;

	// Spigot end

	public PacketPlayInSettings() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(7);
		b = packetdataserializer.readByte();
		c = EnumChatVisibility.a(packetdataserializer.readByte());
		d = packetdataserializer.readBoolean();
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			e = EnumDifficulty.getById(packetdataserializer.readByte());
			f = packetdataserializer.readBoolean();
		} else {
			flags = packetdataserializer.readUnsignedByte();
		}
		version = packetdataserializer.version;
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.a(a);
		packetdataserializer.writeByte(b);
		packetdataserializer.writeByte(c.a());
		packetdataserializer.writeBoolean(d);
		packetdataserializer.writeByte(e.a());
		packetdataserializer.writeBoolean(f);
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public String c() {
		return a;
	}

	public int d() {
		return b;
	}

	public EnumChatVisibility e() {
		return c;
	}

	public boolean f() {
		return d;
	}

	public EnumDifficulty g() {
		return e;
	}

	public boolean h() {
		return f;
	}

	@Override
	public String b() {
		return String.format("lang=\'%s\', view=%d, chat=%s, col=%b, difficulty=%s, cape=%b", new Object[] { a, Integer.valueOf(b), c, Boolean.valueOf(d), e, Boolean.valueOf(f) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
