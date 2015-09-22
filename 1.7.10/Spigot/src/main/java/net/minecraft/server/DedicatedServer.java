package net.minecraft.server;

import java.io.File;
// CraftBukkit start
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.LoggerOutputStream;
import org.bukkit.craftbukkit.SpigotTimings; // Spigot
import org.bukkit.event.server.ServerCommandEvent;
// CraftBukkit end

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {

	private static final Logger i = LogManager.getLogger();
	private final List j = Collections.synchronizedList(new ArrayList());
	private RemoteStatusListener k;
	private RemoteControlListener l;
	public PropertyManager propertyManager; // CraftBukkit - private -> public
	private EULA n;
	private boolean generateStructures;
	private EnumGamemode p;
	private boolean q;

	// CraftBukkit start - Signature changed
	public DedicatedServer(joptsimple.OptionSet options) {
		super(options, Proxy.NO_PROXY);
		// super(file1, Proxy.NO_PROXY);
		// CraftBukkit end
		new ThreadSleepForever(this, "Server Infinisleeper");
	}

	@Override
	protected boolean init(boolean fullConsole) throws java.net.UnknownHostException { // ClipSpigot - full console // CraftBukkit - throws UnknownHostException
		ThreadCommandReader threadcommandreader = new ThreadCommandReader(this, "Server console handler");

		threadcommandreader.setDaemon(true);
		threadcommandreader.start();

		// CraftBukkit start - TODO: handle command-line logging arguments
		java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
		global.setUseParentHandlers(false);
		for (java.util.logging.Handler handler : global.getHandlers()) {
			global.removeHandler(handler);
		}
		global.addHandler(new org.bukkit.craftbukkit.util.ForwardLogHandler());

		final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
		
		// ClipSpigot - full console
		if(fullConsole) {
			for (org.apache.logging.log4j.core.Appender appender : logger.getAppenders().values()) {
				if (appender instanceof org.apache.logging.log4j.core.appender.ConsoleAppender) {
					logger.removeAppender(appender);
				}
			}
		}

		new Thread(new org.bukkit.craftbukkit.util.TerminalConsoleWriterThread(System.out, reader)).start();

		// ClipSpigot - full console
		if(fullConsole) {
			System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));
			System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.WARN), true));
		}
		// CraftBukkit end

		i.info("Starting minecraft server version 1.7.10");
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			i.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		i.info("Loading properties");
		propertyManager = new PropertyManager(options); // CraftBukkit - CLI argument support

		if (N()) {
			this.c("127.0.0.1");
		} else {
			setOnlineMode(propertyManager.getBoolean("online-mode", true));
			this.c(propertyManager.getString("server-ip", ""));
		}

		setSpawnAnimals(propertyManager.getBoolean("spawn-animals", true));
		setSpawnNPCs(propertyManager.getBoolean("spawn-npcs", true));
		setPvP(propertyManager.getBoolean("pvp", true));
		setAllowFlight(propertyManager.getBoolean("allow-flight", false));
		setTexturePack(propertyManager.getString("resource-pack", ""));
		setMotd(propertyManager.getString("motd", "A Minecraft Server"));
		setForceGamemode(propertyManager.getBoolean("force-gamemode", false));
		setIdleTimeout(propertyManager.getInt("player-idle-timeout", 0));
		if (propertyManager.getInt("difficulty", 1) < 0) {
			propertyManager.setProperty("difficulty", Integer.valueOf(0));
		} else if (propertyManager.getInt("difficulty", 1) > 3) {
			propertyManager.setProperty("difficulty", Integer.valueOf(3));
		}

		generateStructures = propertyManager.getBoolean("generate-structures", true);
		int gamemode = propertyManager.getInt("gamemode", EnumGamemode.SURVIVAL.getId()); // CraftBukkit - Unique name to avoid stomping on logger

		p = WorldSettings.a(gamemode); // CraftBukkit - Use new name
		i.info("Default game type: " + p);
		InetAddress inetaddress = null;

		if (getServerIp().length() > 0) {
			inetaddress = InetAddress.getByName(getServerIp());
		}

		if (L() < 0) {
			setPort(propertyManager.getInt("server-port", 25565));
		}
		// Spigot start
		this.a(new DedicatedPlayerList(this));
		org.spigotmc.SpigotConfig.init();
		org.spigotmc.SpigotConfig.registerCommands();
		// Spigot end
		// PaperSpigot start
		org.clipspigot.ClipSpigotConfig.init();
		org.clipspigot.ClipSpigotConfig.registerCommands();
		// PaperSpigot stop

		i.info("Generating keypair");
		this.a(MinecraftEncryption.b());
		i.info("Starting Minecraft server on " + (getServerIp().length() == 0 ? "*" : getServerIp()) + ":" + L());

		if (!org.spigotmc.SpigotConfig.lateBind) {
			try {
				ai().a(inetaddress, L());
			} catch (Throwable ioexception) { // CraftBukkit - IOException -> Throwable
				i.warn("**** FAILED TO BIND TO PORT!");
				i.warn("The exception was: {}", new Object[] { ioexception.toString() });
				i.warn("Perhaps a server is already running on that port?");
				return false;
			}
		}

		// Spigot Start - Move DedicatedPlayerList up and bring plugin loading from CraftServer to here
		// this.a((PlayerList) (new DedicatedPlayerList(this))); // CraftBukkit
		server.loadPlugins();
		server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.STARTUP);
		// Spigot End

		if (!getOnlineMode()) {
			i.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			i.warn("The server will make no attempt to authenticate usernames. Beware.");
			i.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
			i.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
		}

		if (aE()) {
			getUserCache().c();
		}

		if (!NameReferencingFileConverter.a(propertyManager))
			return false;
		else {
			// this.a((PlayerList) (new DedicatedPlayerList(this))); // CraftBukkit - moved up
			convertable = new WorldLoaderServer(server.getWorldContainer()); // CraftBukkit - moved from MinecraftServer constructor
			long j = System.nanoTime();

			if (O() == null) {
				k(propertyManager.getString("level-name", "world"));
			}

			String s = propertyManager.getString("level-seed", "");
			String s1 = propertyManager.getString("level-type", "DEFAULT");
			String s2 = propertyManager.getString("generator-settings", "");
			long k = new Random().nextLong();

			if (s.length() > 0) {
				try {
					long l = Long.parseLong(s);

					if (l != 0L) {
						k = l;
					}
				} catch (NumberFormatException numberformatexception) {
					k = s.hashCode();
				}
			}

			WorldType worldtype = WorldType.getType(s1);

			if (worldtype == null) {
				worldtype = WorldType.NORMAL;
			}

			at();
			getEnableCommandBlock();
			l();
			this.c(propertyManager.getInt("max-build-height", 256));
			this.c((getMaxBuildHeight() + 8) / 16 * 16);
			this.c(MathHelper.a(getMaxBuildHeight(), 64, 256));
			propertyManager.setProperty("max-build-height", Integer.valueOf(getMaxBuildHeight()));
			i.info("Preparing level \"" + O() + "\"");
			this.a(O(), O(), k, worldtype, s2);
			long i1 = System.nanoTime() - j;
			String s3 = String.format("%.3fs", new Object[] { Double.valueOf(i1 / 1.0E9D) });

			i.info("Done (" + s3 + ")! For help, type \"help\" or \"?\"");
			if (propertyManager.getBoolean("enable-query", false)) {
				i.info("Starting GS4 status listener");
				this.k = new RemoteStatusListener(this);
				this.k.a();
			}

			if (propertyManager.getBoolean("enable-rcon", false)) {
				i.info("Starting remote control listener");
				l = new RemoteControlListener(this);
				l.a();
				remoteConsole = new org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender(); // CraftBukkit
			}

			// CraftBukkit start
			if (server.getBukkitSpawnRadius() > -1) {
				i.info("'settings.spawn-radius' in bukkit.yml has been moved to 'spawn-protection' in server.properties. I will move your config for you.");
				propertyManager.properties.remove("spawn-protection");
				propertyManager.getInt("spawn-protection", server.getBukkitSpawnRadius());
				server.removeBukkitSpawnRadius();
				propertyManager.savePropertiesFile();
			}
			// CraftBukkit end

			if (org.spigotmc.SpigotConfig.lateBind) {
				try {
					ai().a(inetaddress, L());
				} catch (Throwable ioexception) { // CraftBukkit - IOException -> Throwable
					i.warn("**** FAILED TO BIND TO PORT!");
					i.warn("The exception was: {}", new Object[] { ioexception.toString() });
					i.warn("Perhaps a server is already running on that port?");
					return false;
				}
			}
			return true;
		}
	}

	// CraftBukkit start
	@Override
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	// CraftBukkit end

	@Override
	public boolean getGenerateStructures() {
		return generateStructures;
	}

	@Override
	public EnumGamemode getGamemode() {
		return p;
	}

	@Override
	public EnumDifficulty getDifficulty() {
		return EnumDifficulty.getById(propertyManager.getInt("difficulty", 1));
	}

	@Override
	public boolean isHardcore() {
		return propertyManager.getBoolean("hardcore", false);
	}

	@Override
	protected void a(CrashReport crashreport) {
	}

	@Override
	public CrashReport b(CrashReport crashreport) {
		crashreport = super.b(crashreport);
		crashreport.g().a("Is Modded", new CrashReportModded(this));
		crashreport.g().a("Type", new CrashReportType(this));
		return crashreport;
	}

	@Override
	protected void t() {
		System.exit(0);
	}

	@Override
	public void v() { // CraftBukkit - protected -> public (decompile error?)
		super.v();
		aB();
	}

	@Override
	public boolean getAllowNether() {
		return propertyManager.getBoolean("allow-nether", true);
	}

	@Override
	public boolean getSpawnMonsters() {
		return propertyManager.getBoolean("spawn-monsters", true);
	}

	public void issueCommand(String s, ICommandListener icommandlistener) {
		j.add(new ServerCommand(s, icommandlistener));
	}

	public void aB() {
		SpigotTimings.serverCommandTimer.startTiming(); // Spigot
		while (!j.isEmpty()) {
			ServerCommand servercommand = (ServerCommand) j.remove(0);

			// CraftBukkit start - ServerCommand for preprocessing
			ServerCommandEvent event = new ServerCommandEvent(console, servercommand.command);
			server.getPluginManager().callEvent(event);
			servercommand = new ServerCommand(event.getCommand(), servercommand.source);

			// this.getCommandHandler().a(servercommand.source, servercommand.command); // Called in dispatchServerCommand
			server.dispatchServerCommand(console, servercommand);
			// CraftBukkit end
		}
		SpigotTimings.serverCommandTimer.stopTiming(); // Spigot
	}

	@Override
	public boolean X() {
		return true;
	}

	public DedicatedPlayerList aC() {
		return (DedicatedPlayerList) super.getPlayerList();
	}

	@Override
	public int a(String s, int i) {
		return propertyManager.getInt(s, i);
	}

	@Override
	public String a(String s, String s1) {
		return propertyManager.getString(s, s1);
	}

	public boolean a(String s, boolean flag) {
		return propertyManager.getBoolean(s, flag);
	}

	@Override
	public void a(String s, Object object) {
		propertyManager.setProperty(s, object);
	}

	@Override
	public void a() {
		propertyManager.savePropertiesFile();
	}

	@Override
	public String b() {
		File file1 = propertyManager.c();

		return file1 != null ? file1.getAbsolutePath() : "No settings file";
	}

	public void aD() {
		ServerGUI.a(this);
		q = true;
	}

	@Override
	public boolean ak() {
		return q;
	}

	@Override
	public String a(EnumGamemode enumgamemode, boolean flag) {
		return "";
	}

	@Override
	public boolean getEnableCommandBlock() {
		return propertyManager.getBoolean("enable-command-block", false);
	}

	@Override
	public int getSpawnProtection() {
		return propertyManager.getInt("spawn-protection", super.getSpawnProtection());
	}

	@Override
	public boolean a(World world, int i, int j, int k, EntityHuman entityhuman) {
		if (world.worldProvider.dimension != 0)
			return false;
		else if (aC().getOPs().isEmpty())
			return false;
		else if (aC().isOp(entityhuman.getProfile()))
			return false;
		else if (getSpawnProtection() <= 0)
			return false;
		else {
			ChunkCoordinates chunkcoordinates = world.getSpawn();
			int l = MathHelper.a(i - chunkcoordinates.x);
			int i1 = MathHelper.a(k - chunkcoordinates.z);
			int j1 = Math.max(l, i1);

			return j1 <= getSpawnProtection();
		}
	}

	@Override
	public int l() {
		return propertyManager.getInt("op-permission-level", 4);
	}

	@Override
	public void setIdleTimeout(int i) {
		super.setIdleTimeout(i);
		propertyManager.setProperty("player-idle-timeout", Integer.valueOf(i));
		this.a();
	}

	@Override
	public boolean m() {
		return propertyManager.getBoolean("broadcast-rcon-to-ops", true);
	}

	@Override
	public boolean at() {
		return propertyManager.getBoolean("announce-player-achievements", true);
	}

	protected boolean aE() {
		server.getLogger().info("**** Beginning UUID conversion, this may take A LONG time ****"); // Spigot, let the user know whats up!
		boolean flag = false;

		int i;

		for (i = 0; !flag && i <= 2; ++i) {
			if (i > 0) {
				// CraftBukkit - Fix decompiler stomping on field
				DedicatedServer.i.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
				aG();
			}

			flag = NameReferencingFileConverter.a(this);
		}

		boolean flag1 = false;

		for (i = 0; !flag1 && i <= 2; ++i) {
			if (i > 0) {
				// CraftBukkit - Fix decompiler stomping on field
				DedicatedServer.i.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
				aG();
			}

			flag1 = NameReferencingFileConverter.b(this);
		}

		boolean flag2 = false;

		for (i = 0; !flag2 && i <= 2; ++i) {
			if (i > 0) {
				// CraftBukkit - Fix decompiler stomping on field
				DedicatedServer.i.warn("Encountered a problem while converting the op list, retrying in a few seconds");
				aG();
			}

			flag2 = NameReferencingFileConverter.c(this);
		}

		boolean flag3 = false;

		for (i = 0; !flag3 && i <= 2; ++i) {
			if (i > 0) {
				// CraftBukkit - Fix decompiler stomping on field
				DedicatedServer.i.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
				aG();
			}

			flag3 = NameReferencingFileConverter.d(this);
		}

		boolean flag4 = false;

		for (i = 0; !flag4 && i <= 2; ++i) {
			if (i > 0) {
				// CraftBukkit - Fix decompiler stomping on field
				DedicatedServer.i.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
				aG();
			}

			flag4 = NameReferencingFileConverter.a(this, propertyManager);
		}

		return flag || flag1 || flag2 || flag3 || flag4;
	}

	private void aG() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException interruptedexception) {
			;
		}
	}

	@Override
	public PlayerList getPlayerList() {
		return aC();
	}

	static Logger aF() {
		return i;
	}
}
