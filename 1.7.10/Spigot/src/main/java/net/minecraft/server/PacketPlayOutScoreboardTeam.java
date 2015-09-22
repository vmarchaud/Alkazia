package net.minecraft.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PacketPlayOutScoreboardTeam extends Packet {

	private String a = "";
	private String b = "";
	private String c = "";
	private String d = "";
	private Collection e = new ArrayList();
	private int f;
	private int g;

	public PacketPlayOutScoreboardTeam() {
	}

	public PacketPlayOutScoreboardTeam(ScoreboardTeam scoreboardteam, int i) {
		a = scoreboardteam.getName();
		f = i;
		if (i == 0 || i == 2) {
			b = scoreboardteam.getDisplayName();
			c = scoreboardteam.getPrefix();
			d = scoreboardteam.getSuffix();
			g = scoreboardteam.packOptionData();
		}

		if (i == 0) {
			e.addAll(scoreboardteam.getPlayerNameSet());
		}
	}

	public PacketPlayOutScoreboardTeam(ScoreboardTeam scoreboardteam, Collection collection, int i) {
		if (i != 3 && i != 4)
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
					else if (collection != null && !collection.isEmpty()) {
			f = i;
			a = scoreboardteam.getName();
			e.addAll(collection);
		} else
						throw new IllegalArgumentException("Players cannot be null/empty");
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(16);
		f = packetdataserializer.readByte();
		if (f == 0 || f == 2) {
			b = packetdataserializer.c(32);
			c = packetdataserializer.c(16);
			d = packetdataserializer.c(16);
			g = packetdataserializer.readByte();
		}

		if (f == 0 || f == 3 || f == 4) {
			short short1 = packetdataserializer.readShort();

			for (int i = 0; i < short1; ++i) {
				e.add(packetdataserializer.c(40));
			}
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.a(a);
		packetdataserializer.writeByte(f);
		if (f == 0 || f == 2) {
			packetdataserializer.a(b);
			packetdataserializer.a(c);
			packetdataserializer.a(d);
			packetdataserializer.writeByte(g);
			// Spigot start - protocol patch
			if (packetdataserializer.version >= 16) {
				packetdataserializer.a("always");
				packetdataserializer.writeByte(EnumChatFormat.WHITE.ordinal());
			}
			// Spigot end
		}

		if (f == 0 || f == 3 || f == 4) {
			// Spigot start - protocol patch
			if (packetdataserializer.version < 16) {
				packetdataserializer.writeShort(e.size());
			} else {
				packetdataserializer.b(e.size());
			}
			// Spigot end
			Iterator iterator = e.iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();

				packetdataserializer.a(s);
			}
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
