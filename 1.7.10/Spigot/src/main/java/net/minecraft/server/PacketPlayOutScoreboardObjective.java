package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutScoreboardObjective extends Packet {

	private String a;
	private String b;
	private int c;

	public PacketPlayOutScoreboardObjective() {
	}

	public PacketPlayOutScoreboardObjective(ScoreboardObjective scoreboardobjective, int i) {
		a = scoreboardobjective.getName();
		b = scoreboardobjective.getDisplayName();
		c = i;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(16);
		b = packetdataserializer.c(32);
		c = packetdataserializer.readByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.a(a);
			packetdataserializer.a(b);
			packetdataserializer.writeByte(c);
		} else {
			packetdataserializer.a(a);
			packetdataserializer.writeByte(c);
			if (c == 0 || c == 2) {
				packetdataserializer.a(b);
				packetdataserializer.a("integer");
			}
		}
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
