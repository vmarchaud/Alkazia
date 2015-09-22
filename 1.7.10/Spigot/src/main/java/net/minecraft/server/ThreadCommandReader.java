package net.minecraft.server;

import static org.bukkit.craftbukkit.Main.useConsole; // CraftBukkit
import static org.bukkit.craftbukkit.Main.useJline;

import java.io.IOException;

class ThreadCommandReader extends Thread {

	final DedicatedServer server;

	ThreadCommandReader(DedicatedServer dedicatedserver, String s) {
		super(s);
		server = dedicatedserver;
	}

	@Override
	public void run() {
		// CraftBukkit start
		if (!useConsole)
			return;

		jline.console.ConsoleReader bufferedreader = server.reader; // CraftBukkit
		String s;

		try {
			// CraftBukkit start - JLine disabling compatibility
			while (!server.isStopped() && server.isRunning()) {
				if (useJline) {
					s = bufferedreader.readLine(">", null);
				} else {
					s = bufferedreader.readLine();
				}
				if (s != null) {
					server.issueCommand(s, server);
				}
				// CraftBukkit end
			}
		} catch (IOException ioexception) {
			DedicatedServer.aF().error("Exception handling console input", ioexception);
		}
	}
}
