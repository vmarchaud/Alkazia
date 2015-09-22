package net.minecraft.server;

import java.io.IOException;

import org.bukkit.craftbukkit.util.CraftChatMessage;

public class PacketPlayOutOpenWindow extends Packet {

	private int a;
	private int b;
	private String c;
	private int d;
	private boolean e;
	private int f;

	public PacketPlayOutOpenWindow() {
	}

	public PacketPlayOutOpenWindow(int i, int j, String s, int k, boolean flag) {
		if (s.length() > 32) {
			s = s.substring(0, 32); // Spigot - Cap window name to prevent client disconnects
		}
		a = i;
		b = j;
		c = s;
		d = k;
		e = flag;
	}

	public PacketPlayOutOpenWindow(int i, int j, String s, int k, boolean flag, int l) {
		this(i, j, s, k, flag);
		f = l;
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.readUnsignedByte();
		b = packetdataserializer.readUnsignedByte();
		c = packetdataserializer.c(32);
		d = packetdataserializer.readUnsignedByte();
		e = packetdataserializer.readBoolean();
		if (b == 11) {
			f = packetdataserializer.readInt();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeByte(a);
			packetdataserializer.writeByte(b);
			packetdataserializer.a(c);
			packetdataserializer.writeByte(d);
			packetdataserializer.writeBoolean(e);
			if (b == 11) {
				packetdataserializer.writeInt(f);
			}
		} else {
			packetdataserializer.writeByte(a);
			packetdataserializer.a(getInventoryString(b));
			if (e) {
				packetdataserializer.a(ChatSerializer.a(CraftChatMessage.fromString(c)[0]));
			} else {
				packetdataserializer.a(ChatSerializer.a(new ChatMessage(c)));
			}
			packetdataserializer.writeByte(d);
			if (b == 11) {
				packetdataserializer.writeInt(f);
			}
		}
	}

	// Spigot start - protocol patch
	private String getInventoryString(int b) {
		switch (b) {
		case 0:
			return "minecraft:chest";
		case 1:
			return "minecraft:crafting_table";
		case 2:
			return "minecraft:furnace";
		case 3:
			return "minecraft:dispenser";
		case 4:
			return "minecraft:enchanting_table";
		case 5:
			return "minecraft:brewing_stand";
		case 6:
			return "minecraft:villager";
		case 7:
			return "minecraft:beacon";
		case 8:
			return "minecraft:anvil";
		case 9:
			return "minecraft:hopper";
		case 10:
			return "minecraft:dropper";
		case 11:
			return "EntityHorse";
		}
		throw new IllegalArgumentException("Unknown type " + b);
	}

	// Spigot end

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
