package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutLogin extends Packet {

	private int a;
	private boolean b;
	private EnumGamemode c;
	private int d;
	private EnumDifficulty e;
	private int f;
	private WorldType g;

	public PacketPlayOutLogin() {
	}

	public PacketPlayOutLogin(int i, EnumGamemode enumgamemode, boolean flag, int j, EnumDifficulty enumdifficulty, int k, WorldType worldtype) {
		a = i;
		d = j;
		e = enumdifficulty;
		c = enumgamemode;
		f = k;
		b = flag;
		g = worldtype;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.readInt();
		short short1 = packetdataserializer.readUnsignedByte();

		b = (short1 & 8) == 8;
		int i = short1 & -9;

		c = EnumGamemode.getById(i);
		d = packetdataserializer.readByte();
		e = EnumDifficulty.getById(packetdataserializer.readUnsignedByte());
		f = packetdataserializer.readUnsignedByte();
		g = WorldType.getType(packetdataserializer.c(16));
		if (g == null) {
			g = WorldType.NORMAL;
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.writeInt(a);
		int i = c.getId();

		if (b) {
			i |= 8;
		}

		packetdataserializer.writeByte(i);
		packetdataserializer.writeByte(d);
		packetdataserializer.writeByte(e.a());
		packetdataserializer.writeByte(f);
		packetdataserializer.a(g.name());

		// Spigot start - protocol patch
		if (packetdataserializer.version >= 29) {
			packetdataserializer.writeBoolean(false);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("eid=%d, gameType=%d, hardcore=%b, dimension=%d, difficulty=%s, maxplayers=%d", new Object[] { Integer.valueOf(a), Integer.valueOf(c.getId()), Boolean.valueOf(b), Integer.valueOf(d), e, Integer.valueOf(f) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
