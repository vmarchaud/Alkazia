package net.minecraft.server;

import java.math.BigInteger;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.exceptions.AuthenticationUnavailableException;

// CraftBukkit start
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

// CraftBukkit end

class ThreadPlayerLookupUUID extends Thread {

	final LoginListener a;

	ThreadPlayerLookupUUID(LoginListener loginlistener, String s) {
		super(s);
		a = loginlistener;
	}

	@Override
	public void run() {
		GameProfile gameprofile = LoginListener.a(a);

		try {
			// Spigot Start
			if (!LoginListener.c(a).getOnlineMode()) {
				a.initUUID();
				fireLoginEvents();
				return;
			}
			// Spigot End
			String s = new BigInteger(MinecraftEncryption.a(LoginListener.b(a), LoginListener.c(a).K().getPublic(), LoginListener.d(a))).toString(16);

			LoginListener.a(a, LoginListener.c(a).av().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s));
			if (LoginListener.a(a) != null) {
				fireLoginEvents(); // Spigot
			} else if (LoginListener.c(a).N()) {
				LoginListener.e().warn("Failed to verify username but will let them in anyway!");
				LoginListener.a(a, a.a(gameprofile));
				LoginListener.a(a, EnumProtocolState.READY_TO_ACCEPT);
			} else {
				a.disconnect("Failed to verify username!");
				LoginListener.e().error("Username \'" + LoginListener.a(a).getName() + "\' tried to join with an invalid session");
			}
		} catch (AuthenticationUnavailableException authenticationunavailableexception) {
			if (LoginListener.c(a).N()) {
				LoginListener.e().warn("Authentication servers are down but will let them in anyway!");
				LoginListener.a(a, a.a(gameprofile));
				LoginListener.a(a, EnumProtocolState.READY_TO_ACCEPT);
			} else {
				a.disconnect("Authentication servers are down. Please try again later, sorry!");
				LoginListener.e().error("Couldn\'t verify username because servers are unavailable");
			}
			// CraftBukkit start - catch all exceptions
		} catch (Exception exception) {
			a.disconnect("Failed to verify username!");
			LoginListener.c(a).server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + LoginListener.a(a).getName(), exception);
			// CraftBukkit end
		}
	}

	private void fireLoginEvents() throws Exception {
		// CraftBukkit start - fire PlayerPreLoginEvent
		if (!a.networkManager.isConnected())
			return;

		String playerName = LoginListener.a(a).getName();
		java.net.InetAddress address = ((java.net.InetSocketAddress) a.networkManager.getSocketAddress()).getAddress();
		java.util.UUID uniqueId = LoginListener.a(a).getId();
		final org.bukkit.craftbukkit.CraftServer server = LoginListener.c(a).server;

		AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
		server.getPluginManager().callEvent(asyncEvent);

		if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
			final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
			if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
				event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
			}
			Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
				@Override
				protected PlayerPreLoginEvent.Result evaluate() {
					server.getPluginManager().callEvent(event);
					return event.getResult();
				}
			};

			LoginListener.c(a).processQueue.add(waitable);
			if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
				a.disconnect(event.getKickMessage());
				return;
			}
		} else {
			if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
				a.disconnect(asyncEvent.getKickMessage());
				return;
			}
		}
		// CraftBukkit end

		LoginListener.e().info("UUID of player " + LoginListener.a(a).getName() + " is " + LoginListener.a(a).getId());
		LoginListener.a(a, EnumProtocolState.READY_TO_ACCEPT);
	}
}
