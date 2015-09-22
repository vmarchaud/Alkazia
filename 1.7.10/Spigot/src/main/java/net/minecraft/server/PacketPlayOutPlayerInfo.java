package net.minecraft.server;

import java.io.IOException;

// Spigot start - protocol patch
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;

import org.bukkit.craftbukkit.util.CraftChatMessage;

public class PacketPlayOutPlayerInfo extends Packet {

	private static final int ADD_PLAYER = 0;
	private static final int UPDATE_GAMEMODE = 1;
	private static final int UPDATE_LATENCY = 2;
	private static final int UPDATE_DISPLAY_NAME = 3;
	private static final int REMOVE_PLAYER = 4;

	private int action;
	// private int length; We don't batch (yet)
	private GameProfile player;

	private int gamemode;
	private int ping;
	private String username;

	public PacketPlayOutPlayerInfo() {
	}

	/* removed to force breaking
	public PacketPlayOutPlayerInfo(String s, boolean flag, int i) {
	    this.a = s;
	    this.b = flag;
	    this.c = i;
	}
	*/

	public static PacketPlayOutPlayerInfo addPlayer(EntityPlayer player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		packet.action = ADD_PLAYER;
		packet.username = player.listName;
		packet.player = player.getProfile();
		packet.ping = player.ping;
		packet.gamemode = player.playerInteractManager.getGameMode().getId();
		return packet;
	}

	public static PacketPlayOutPlayerInfo updatePing(EntityPlayer player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		packet.action = UPDATE_LATENCY;
		packet.username = player.listName;
		packet.player = player.getProfile();
		packet.ping = player.ping;
		return packet;
	}

	public static PacketPlayOutPlayerInfo updateGamemode(EntityPlayer player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		packet.action = UPDATE_GAMEMODE;
		packet.username = player.listName;
		packet.player = player.getProfile();
		packet.gamemode = player.playerInteractManager.getGameMode().getId();
		return packet;
	}

	public static PacketPlayOutPlayerInfo updateDisplayName(EntityPlayer player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		packet.action = UPDATE_DISPLAY_NAME;
		packet.username = player.listName;
		packet.player = player.getProfile();
		return packet;
	}

	public static PacketPlayOutPlayerInfo removePlayer(EntityPlayer player) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		packet.action = REMOVE_PLAYER;
		packet.username = player.listName;
		packet.player = player.getProfile();
		return packet;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		// Not needed
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		if (packetdataserializer.version >= 20) {
			packetdataserializer.b(action);
			packetdataserializer.b(1);
			packetdataserializer.writeUUID(player.getId());
			switch (action) {
			case ADD_PLAYER:
				packetdataserializer.a(player.getName());
				PropertyMap properties = player.getProperties();
				packetdataserializer.b(properties.size());
				for (Property property : properties.values()) {
					packetdataserializer.a(property.getName());
					packetdataserializer.a(property.getValue());
					packetdataserializer.writeBoolean(property.hasSignature());
					if (property.hasSignature()) {
						packetdataserializer.a(property.getSignature());
					}
				}
				packetdataserializer.b(gamemode);
				packetdataserializer.b(ping);
				packetdataserializer.writeBoolean(username != null);
				if (username != null) {
					packetdataserializer.a(ChatSerializer.a(CraftChatMessage.fromString(username)[0]));
				}
				break;
			case UPDATE_GAMEMODE:
				packetdataserializer.b(gamemode);
				break;
			case UPDATE_LATENCY:
				packetdataserializer.b(ping);
				break;
			case UPDATE_DISPLAY_NAME:
				packetdataserializer.writeBoolean(username != null);
				if (username != null) {
					packetdataserializer.a(ChatSerializer.a(CraftChatMessage.fromString(username)[0]));
				}
				break;
			case REMOVE_PLAYER:
				break;

			}
		} else {
			packetdataserializer.a(username);
			packetdataserializer.writeBoolean(action != REMOVE_PLAYER);
			packetdataserializer.writeShort(ping);
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
// Spigot end