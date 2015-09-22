package net.minecraft.server;

import java.io.IOException;

import org.bukkit.craftbukkit.util.CraftChatMessage; // Spigot - protocol patch

public class PacketPlayOutUpdateSign extends Packet {

	private int x;
	private int y;
	private int z;
	private String[] lines;

	public PacketPlayOutUpdateSign() {
	}

	public PacketPlayOutUpdateSign(int i, int j, int k, String[] astring) {
		x = i;
		y = j;
		z = k;
		lines = new String[] { astring[0], astring[1], astring[2], astring[3] };
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		x = packetdataserializer.readInt();
		y = packetdataserializer.readShort();
		z = packetdataserializer.readInt();
		lines = new String[4];

		for (int i = 0; i < 4; ++i) {
			lines[i] = packetdataserializer.c(15);
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(x);
			packetdataserializer.writeShort(y);
			packetdataserializer.writeInt(z);
		} else {
			packetdataserializer.writePosition(x, y, z);
		}

		for (int i = 0; i < 4; ++i) {
			if (packetdataserializer.version < 21) {
				packetdataserializer.a(lines[i]);
			} else {
				String line = ChatSerializer.a(CraftChatMessage.fromString(lines[i])[0]);
				packetdataserializer.a(line);
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
