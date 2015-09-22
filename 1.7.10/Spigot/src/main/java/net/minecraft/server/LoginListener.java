package net.minecraft.server;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKey;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.util.concurrent.Future;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.org.apache.commons.lang3.Validate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginListener implements PacketLoginInListener {

	private static final AtomicInteger b = new AtomicInteger(0);
	private static final Logger c = LogManager.getLogger();
	private static final Random random = new Random();
	private final byte[] e = new byte[4];
	private final MinecraftServer server;
	public final NetworkManager networkManager;
	private EnumProtocolState g;
	private int h;
	private GameProfile i;
	private String j;
	private SecretKey loginKey;
	public String hostname = ""; // CraftBukkit - add field

	public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
		g = EnumProtocolState.HELLO;
		j = "";
		server = minecraftserver;
		networkManager = networkmanager;
		random.nextBytes(e);
	}

	@Override
	public void a() {
		if (g == EnumProtocolState.READY_TO_ACCEPT) {
			this.c();
		}

		if (h++ == 600) {
			disconnect("Took too long to log in");
		}
	}

	public void disconnect(String s) {
		try {
			c.info("Disconnecting " + getName() + ": " + s);
			ChatComponentText chatcomponenttext = new ChatComponentText(s);

			networkManager.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
			networkManager.close(chatcomponenttext);
		} catch (Exception exception) {
			c.error("Error whilst disconnecting player", exception);
		}
	}

	// Spigot start
	public void initUUID() {
		UUID uuid;
		if (networkManager.spoofedUUID != null) {
			uuid = networkManager.spoofedUUID;
		} else {
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + i.getName()).getBytes(Charsets.UTF_8));
		}

		i = new GameProfile(uuid, i.getName());

		if (networkManager.spoofedProfile != null) {
			for (Property property : networkManager.spoofedProfile) {
				i.getProperties().put(property.getName(), property);
			}
		}
	}

	// Spigot end

	public void c() {
		// Spigot start - Moved to initUUID
		/*
		if (!this.i.isComplete()) {
		    this.i = this.a(this.i);
		}
		*/
		// Spigot end

		// CraftBukkit start - fire PlayerLoginEvent
		EntityPlayer s = server.getPlayerList().attemptLogin(this, i, hostname);

		if (s == null) {
			// this.disconnect(s);
			// CraftBukkit end
		} else {
			g = EnumProtocolState.e;
			// Spigot start
			if (networkManager.getVersion() >= 27) {
				networkManager.handle(new org.spigotmc.ProtocolInjector.PacketLoginCompression(256), new GenericFutureListener() {
					@Override
					public void operationComplete(Future future) throws Exception {
						networkManager.enableCompression();
					}
				});
			}
			// Spigot end
			networkManager.handle(new PacketLoginOutSuccess(i), new GenericFutureListener[0]);
			server.getPlayerList().a(networkManager, server.getPlayerList().processLogin(i, s)); // CraftBukkit - add player reference
		}
	}

	@Override
	public void a(IChatBaseComponent ichatbasecomponent) {
		c.info(getName() + " lost connection: " + ichatbasecomponent.c());
	}

	public String getName() {
		return i != null ? i.toString() + " (" + networkManager.getSocketAddress().toString() + ")" : String.valueOf(networkManager.getSocketAddress());
	}

	@Override
	public void a(EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {
		Validate.validState(g == EnumProtocolState.e || g == EnumProtocolState.HELLO, "Unexpected change in protocol", new Object[0]);
		Validate.validState(enumprotocol1 == EnumProtocol.PLAY || enumprotocol1 == EnumProtocol.LOGIN, "Unexpected protocol " + enumprotocol1, new Object[0]);
	}

	@Override
	public void a(PacketLoginInStart packetlogininstart) {
		Validate.validState(g == EnumProtocolState.HELLO, "Unexpected hello packet", new Object[0]);
		i = packetlogininstart.c();
		if (server.getOnlineMode() && !networkManager.c()) {
			g = EnumProtocolState.KEY;
			networkManager.handle(new PacketLoginOutEncryptionBegin(j, server.K().getPublic(), e), new GenericFutureListener[0]);
		} else {
			new ThreadPlayerLookupUUID(this, "User Authenticator #" + b.incrementAndGet()).start(); // Spigot
		}
	}

	@Override
	public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
		Validate.validState(g == EnumProtocolState.KEY, "Unexpected key packet", new Object[0]);
		PrivateKey privatekey = server.K().getPrivate();

		if (!Arrays.equals(e, packetlogininencryptionbegin.b(privatekey)))
			throw new IllegalStateException("Invalid nonce!");
		else {
			loginKey = packetlogininencryptionbegin.a(privatekey);
			g = EnumProtocolState.AUTHENTICATING;
			networkManager.a(loginKey);
			new ThreadPlayerLookupUUID(this, "User Authenticator #" + b.incrementAndGet()).start();
		}
	}

	protected GameProfile a(GameProfile gameprofile) {
		UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));

		return new GameProfile(uuid, gameprofile.getName());
	}

	static GameProfile a(LoginListener loginlistener) {
		return loginlistener.i;
	}

	static String b(LoginListener loginlistener) {
		return loginlistener.j;
	}

	static MinecraftServer c(LoginListener loginlistener) {
		return loginlistener.server;
	}

	static SecretKey d(LoginListener loginlistener) {
		return loginlistener.loginKey;
	}

	static GameProfile a(LoginListener loginlistener, GameProfile gameprofile) {
		return loginlistener.i = gameprofile;
	}

	static Logger e() {
		return c;
	}

	static EnumProtocolState a(LoginListener loginlistener, EnumProtocolState enumprotocolstate) {
		return loginlistener.g = enumprotocolstate;
	}
}
