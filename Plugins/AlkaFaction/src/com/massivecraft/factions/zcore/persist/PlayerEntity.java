package com.massivecraft.factions.zcore.persist;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

public class PlayerEntity extends Entity {
    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.getId());
    }

    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    // make sure target player should be able to detect that this player is online
    public boolean isOnlineAndVisibleTo(final Player player) {
        final Player target = this.getPlayer();
        return target != null && player.canSee(target);
    }

    public boolean isOffline() {
        return !this.isOnline();
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public void sendMessage(final String msg) {
        final Player player = this.getPlayer();
        if (player == null) return;
        sendMessageToPlayer(player, msg);
    }
    
    public void sendMap(final String msg) {
        final Player player = this.getPlayer();
        if (player == null) return;
       	player.sendMessage(msg);
    }

    public void sendMessage(final List<String> msgs) {
        for (final String msg : msgs) {
            this.sendMap(msg);
        }
    }
    
    public static void sendMessageToPlayer(Player player, String msg) {
		IChatBaseComponent chatComponent = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(chatComponent, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
