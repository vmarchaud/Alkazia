package net.minecraft.server;

// CraftBukkit start
import java.awt.print.Paper;
import java.net.InetAddress;
import java.util.HashMap;

import org.clipspigot.ClipSpigotConfig;

import net.minecraft.util.com.mojang.authlib.properties.Property; // Spigot
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
// CraftBukkit end
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

public class HandshakeListener implements PacketHandshakingInListener {

	private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot
	// CraftBukkit start - add fields
	private static final HashMap<InetAddress, Long> throttleTracker = new HashMap<InetAddress, Long>();
	private static int throttleCounter = 0;
	// CraftBukkit end

	private final MinecraftServer a;
	private final NetworkManager b;

	public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
		a = minecraftserver;
		b = networkmanager;
	}

	@Override
	public void a(PacketHandshakingInSetProtocol handshakingInPacket) {
		// Spigot start
		if (NetworkManager.SUPPORTED_VERSIONS.contains(handshakingInPacket.d())) {
			NetworkManager.a(b).attr(NetworkManager.protocolVersion).set(handshakingInPacket.d());
		}
		// Spigot end
		switch (ProtocolOrdinalWrapper.a[handshakingInPacket.c().ordinal()]) {
		case 1:
			b.a(EnumProtocol.LOGIN);
			ChatComponentText chatcomponenttext;

			// CraftBukkit start - Connection throttle
			try {
				long currentTime = System.currentTimeMillis();
				long connectionThrottle = MinecraftServer.getServer().server.getConnectionThrottle();
				InetAddress address = ((java.net.InetSocketAddress) b.getSocketAddress()).getAddress();

				synchronized (throttleTracker) {
					if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
						throttleTracker.put(address, currentTime);
						chatcomponenttext = new ChatComponentText("Connection throttled! Please wait before reconnecting.");
						b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
						b.close(chatcomponenttext);
						return;
					}

					throttleTracker.put(address, currentTime);
					throttleCounter++;
					if (throttleCounter > 200) {
						throttleCounter = 0;

						// Cleanup stale entries
						java.util.Iterator iter = throttleTracker.entrySet().iterator();
						while (iter.hasNext()) {
							java.util.Map.Entry<InetAddress, Long> entry = (java.util.Map.Entry) iter.next();
							if (entry.getValue() > connectionThrottle) {
								iter.remove();
							}
						}
					}
				}
			} catch (Throwable t) {
				org.apache.logging.log4j.LogManager.getLogger().debug("Failed to check connection throttle", t);
			}
			// CraftBukkit end

			// ClipSpigot start
			if(ClipSpigotConfig.allow18clientsOnly && handshakingInPacket.d() != 47) {
				chatcomponenttext = new ChatComponentText(ClipSpigotConfig.only18ClientAllowedMessage);
				b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
				b.close(chatcomponenttext);
				break;
			}
			// ClipSpigot end
			
			if (handshakingInPacket.d() > 66 && handshakingInPacket.d() != 66) { // Spigot
				chatcomponenttext = new ChatComponentText(org.spigotmc.SpigotConfig.outdatedServerMessage); // Spigot
				b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
				b.close(chatcomponenttext);
			} else if (handshakingInPacket.d() < 66) {
				chatcomponenttext = new ChatComponentText(org.spigotmc.SpigotConfig.outdatedClientMessage); // Spigot
				b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
				b.close(chatcomponenttext);
			} else {
				b.a(new LoginListener(a, b));
				// Spigot Start
				if (org.spigotmc.SpigotConfig.bungee) {
					String[] split = handshakingInPacket.b.split("\00");
					if (split.length == 3 || split.length == 4) {
						handshakingInPacket.b = split[0];
						b.n = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) b.getSocketAddress()).getPort());
						b.spoofedUUID = UUIDTypeAdapter.fromString(split[2]);
					} else {
						chatcomponenttext = new ChatComponentText("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
						b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
						b.close(chatcomponenttext);
						return;
					}
					if (split.length == 4) {
						b.spoofedProfile = gson.fromJson(split[3], Property[].class);
					}
				}
				// Spigot End
				((LoginListener) b.getPacketListener()).hostname = handshakingInPacket.b + ":" + handshakingInPacket.c; // CraftBukkit - set hostname
			}
			break;

		case 2:
			b.a(EnumProtocol.STATUS);
			b.a(new PacketStatusListener(a, b));
			break;

		default:
			throw new UnsupportedOperationException("Invalid intention " + handshakingInPacket.c());
		}
	}

	@Override
	public void a(IChatBaseComponent ichatbasecomponent) {
	}

	@Override
	public void a(EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {
		if (enumprotocol1 != EnumProtocol.LOGIN && enumprotocol1 != EnumProtocol.STATUS)
			throw new UnsupportedOperationException("Invalid state " + enumprotocol1);
	}

	@Override
	public void a() {
	}
}
