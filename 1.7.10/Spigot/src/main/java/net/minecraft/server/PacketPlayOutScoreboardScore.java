package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutScoreboardScore extends Packet {

	private String a = "";
	private String b = "";
	private int c;
	private int d;

	public PacketPlayOutScoreboardScore() {
	}

	public PacketPlayOutScoreboardScore(ScoreboardScore scoreboardscore, int i) {
		a = scoreboardscore.getPlayerName();
		b = scoreboardscore.getObjective().getName();
		c = scoreboardscore.getScore();
		d = i;
	}

	public PacketPlayOutScoreboardScore(String s) {
		a = s;
		b = "";
		c = 0;
		d = 1;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(16);
		d = packetdataserializer.readByte();
		if (d != 1) {
			b = packetdataserializer.c(16);
			c = packetdataserializer.readInt();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.a(a);
		packetdataserializer.writeByte(d);
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			if (d != 1) {
				packetdataserializer.a(b);
				packetdataserializer.writeInt(c);
			}
		} else {
			packetdataserializer.a(b);
			if (d != 1) {
				packetdataserializer.b(c);
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
