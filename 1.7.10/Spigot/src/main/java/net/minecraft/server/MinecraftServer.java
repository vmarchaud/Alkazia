package net.minecraft.server;

import java.awt.image.BufferedImage;
import java.io.File;
// CraftBukkit start
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import jline.console.ConsoleReader;
import joptsimple.OptionSet;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.GameProfileRepository;
import net.minecraft.util.com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.ByteBufOutputStream;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.handler.codec.base64.Base64;
import net.minecraft.util.org.apache.commons.lang3.Validate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.SpigotTimings; // Spigot
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.world.WorldSaveEvent;

// CraftBukkit end

public abstract class MinecraftServer implements ICommandListener, Runnable {

	private static final Logger i = LogManager.getLogger();
	private static final File a = new File("usercache.json");
	private static MinecraftServer j;
	public Convertable convertable; // CraftBukkit - private final -> public
	public File universe; // CraftBukkit - private final -> public
	private final List n = new ArrayList();
	private final ICommandHandler o;
	public final MethodProfiler methodProfiler = new MethodProfiler();
	private ServerConnection p; // Spigot
	private final ServerPing q = new ServerPing();
	private final Random r = new Random();
	private String serverIp;
	private int t = -1;
	public WorldServer[] worldServer;
	private PlayerList u;
	private boolean isRunning = true;
	private boolean isStopped;
	private int ticks;
	protected final Proxy d;
	public String e;
	public int f;
	private boolean onlineMode;
	private boolean spawnAnimals;
	private boolean spawnNPCs;
	private boolean pvpMode;
	private boolean allowFlight;
	private String motd;
	private int E;
	private int F = 0;
	public final long[] g = new long[100];
	public long[][] h;
	private KeyPair G;
	private String H;
	private String I;
	private boolean demoMode;
	private boolean L;
	private boolean M;
	private String N = "";
	private boolean O;
	private long P;
	private String Q;
	private boolean R;
	private boolean S;
	private final YggdrasilAuthenticationService T;
	private final MinecraftSessionService U;
	private long V = 0L;
	private final GameProfileRepository W;
	private final UserCache X;

	// CraftBukkit start - add fields
	public List<WorldServer> worlds = new ArrayList<WorldServer>();
	public org.bukkit.craftbukkit.CraftServer server;
	public OptionSet options;
	public org.bukkit.command.ConsoleCommandSender console;
	public org.bukkit.command.RemoteConsoleCommandSender remoteConsole;
	public ConsoleReader reader;
	public static int currentTick = (int) (System.currentTimeMillis() / 50);
	public final Thread primaryThread;
	public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
	public int autosavePeriod;
	// CraftBukkit end
	// Spigot start
	private static final int TPS = 20;
	private static final int TICK_TIME = 1000000000 / TPS;
	private static final int SAMPLE_INTERVAL = 100;
	public final double[] recentTps = new double[3];

	// Spigot end
	
	private boolean fullConsole = true; // ClipSpigot

	public MinecraftServer(OptionSet options, Proxy proxy) { // CraftBukkit - signature file -> OptionSet
		net.minecraft.util.io.netty.util.ResourceLeakDetector.setEnabled(false); // Spigot - disable
		X = new UserCache(this, a);
		j = this;
		d = proxy;
		// this.universe = file1; // CraftBukkit
		// this.p = new ServerConnection(this); // Spigot
		o = new CommandDispatcher();
		// this.convertable = new WorldLoaderServer(file1); // CraftBukkit - moved to DedicatedServer.init
		T = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
		U = T.createMinecraftSessionService();
		W = T.createProfileRepository();
		// CraftBukkit start
		this.options = options;
 
		// ClipSpigot start
		if (options.has("eclipseConsole"))
			fullConsole = false;
		// ClipSpigot end
		
		// Try to see if we're actually running in a terminal, disable jline if not
		if (System.console() == null) {
			System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
			org.bukkit.craftbukkit.Main.useJline = false;
		}

		try {
			reader = new ConsoleReader(System.in, System.out);
			reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
		} catch (Throwable e) {
			try {
				// Try again with jline disabled for Windows users without C++ 2008 Redistributable
				System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
				System.setProperty("user.language", "en");
				org.bukkit.craftbukkit.Main.useJline = false;
				reader = new ConsoleReader(System.in, System.out);
				reader.setExpandEvents(false);
			} catch (IOException ex) {
				i.warn((String) null, ex);
			}
		}
		Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(this));

		primaryThread = new ThreadServerApplication(this, "Server thread"); // Moved from main
	}

	public abstract PropertyManager getPropertyManager();

	// CraftBukkit end

	protected abstract boolean init(boolean fullConsole) throws java.net.UnknownHostException; // ClipSpigot - full console // CraftBukkit - throws UnknownHostException

	protected void a(String s) {
		if (getConvertable().isConvertable(s)) {
			i.info("Converting map!");
			this.b("menu.convertingLevel");
			getConvertable().convert(s, new ConvertProgressUpdater(this));
		}
	}

	protected synchronized void b(String s) {
		Q = s;
	}

	protected void a(String s, String s1, long i, WorldType worldtype, String s2) {
		this.a(s);
		this.b("menu.loadingLevel");
		worldServer = new WorldServer[3];
		// this.h = new long[this.worldServer.length][100]; // CraftBukkit - Removed ticktime arrays
		// IDataManager idatamanager = this.convertable.a(s, true);
		// WorldData worlddata = idatamanager.getWorldData();
		/* CraftBukkit start - Removed worldsettings
		WorldSettings worldsettings;

		if (worlddata == null) {
		    worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
		    worldsettings.a(s2);
		} else {
		    worldsettings = new WorldSettings(worlddata);
		}

		if (this.L) {
		    worldsettings.a();
		}
		// */
		int worldCount = 3;

		for (int j = 0; j < worldCount; ++j) {
			WorldServer world;
			int dimension = 0;

			if (j == 1) {
				if (getAllowNether()) {
					dimension = -1;
				} else {
					continue;
				}
			}

			if (j == 2) {
				if (server.getAllowEnd()) {
					dimension = 1;
				} else {
					continue;
				}
			}

			String worldType = Environment.getEnvironment(dimension).toString().toLowerCase();
			String name = dimension == 0 ? s : s + "_" + worldType;

			org.bukkit.generator.ChunkGenerator gen = server.getGenerator(name);
			WorldSettings worldsettings = new WorldSettings(i, getGamemode(), getGenerateStructures(), isHardcore(), worldtype);
			worldsettings.a(s2);

			if (j == 0) {
				IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), s1, true);
				if (R()) {
					world = new DemoWorldServer(this, idatamanager, s1, dimension, methodProfiler);
				} else {
					// world =, b0 to dimension, added Environment and gen
					world = new WorldServer(this, idatamanager, s1, dimension, worldsettings, methodProfiler, Environment.getEnvironment(dimension), gen);
				}
				server.scoreboardManager = new org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager(this, world.getScoreboard());
			} else {
				String dim = "DIM" + dimension;

				File newWorld = new File(new File(name), dim);
				File oldWorld = new File(new File(s), dim);

				if (!newWorld.isDirectory() && oldWorld.isDirectory()) {
					MinecraftServer.i.info("---- Migration of old " + worldType + " folder required ----");
					MinecraftServer.i.info("Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your " + worldType + " folder to a new location in order to operate correctly.");
					MinecraftServer.i.info("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
					MinecraftServer.i.info("Attempting to move " + oldWorld + " to " + newWorld + "...");

					if (newWorld.exists()) {
						MinecraftServer.i.warn("A file or folder already exists at " + newWorld + "!");
						MinecraftServer.i.info("---- Migration of old " + worldType + " folder failed ----");
					} else if (newWorld.getParentFile().mkdirs()) {
						if (oldWorld.renameTo(newWorld)) {
							MinecraftServer.i.info("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);
							// Migrate world data too.
							try {
								com.google.common.io.Files.copy(new File(new File(s), "level.dat"), new File(new File(name), "level.dat"));
							} catch (IOException exception) {
								MinecraftServer.i.warn("Unable to migrate world data.");
							}
							MinecraftServer.i.info("---- Migration of old " + worldType + " folder complete ----");
						} else {
							MinecraftServer.i.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
							MinecraftServer.i.info("---- Migration of old " + worldType + " folder failed ----");
						}
					} else {
						MinecraftServer.i.warn("Could not create path for " + newWorld + "!");
						MinecraftServer.i.info("---- Migration of old " + worldType + " folder failed ----");
					}
				}

				IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), name, true);
				// world =, b0 to dimension, s1 to name, added Environment and gen
				world = new SecondaryWorldServer(this, idatamanager, name, dimension, worldsettings, worlds.get(0), methodProfiler, Environment.getEnvironment(dimension), gen);
			}

			if (gen != null) {
				world.getWorld().getPopulators().addAll(gen.getDefaultPopulators(world.getWorld()));
			}

			server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(world.getWorld()));

			world.addIWorldAccess(new WorldManager(this, world));
			if (!N()) {
				world.getWorldData().setGameType(getGamemode());
			}

			worlds.add(world);
			u.setPlayerFileData(worlds.toArray(new WorldServer[worlds.size()]));
			// CraftBukkit end
		}

		this.a(getDifficulty());
		this.g();
	}

	protected void g() {
		boolean flag = true;
		boolean flag1 = true;
		boolean flag2 = true;
		boolean flag3 = true;
		int i = 0;

		this.b("menu.generatingTerrain");
		byte b0 = 0;

		// CraftBukkit start - fire WorldLoadEvent and handle whether or not to keep the spawn in memory
		for (int m = 0; m < worlds.size(); ++m) {
			WorldServer worldserver = worlds.get(m);
			MinecraftServer.i.info("Preparing start region for level " + m + " (Seed: " + worldserver.getSeed() + ")");
			if (!worldserver.getWorld().getKeepSpawnInMemory()) {
				continue;
			}

			ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
			long j = ar();
			i = 0;

			for (int k = -192; k <= 192 && isRunning(); k += 16) {
				for (int l = -192; l <= 192 && isRunning(); l += 16) {
					long i1 = ar();

					if (i1 - j > 1000L) {
						a_("Preparing spawn area", i * 100 / 625);
						j = i1;
					}

					++i;
					worldserver.chunkProviderServer.getChunkAt(chunkcoordinates.x + k >> 4, chunkcoordinates.z + l >> 4);
				}
			}
		}

		for (WorldServer world : worlds) {
			server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
		}
		// CraftBukkit end
		n();
	}

	public abstract boolean getGenerateStructures();

	public abstract EnumGamemode getGamemode();

	public abstract EnumDifficulty getDifficulty();

	public abstract boolean isHardcore();

	public abstract int l();

	public abstract boolean m();

	protected void a_(String s, int i) {
		e = s;
		f = i;
		// CraftBukkit - Use FQN to work around decompiler issue
		MinecraftServer.i.info(s + ": " + i + "%");
	}

	protected void n() {
		e = null;
		f = 0;

		server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD); // CraftBukkit
	}

	protected void saveChunks(boolean flag) throws ExceptionWorldConflict { // CraftBukkit - added throws
		if (!M) {
			// CraftBukkit start - fire WorldSaveEvent
			// WorldServer[] aworldserver = this.worldServer;
			int i = worlds.size();

			for (int j = 0; j < i; ++j) {
				WorldServer worldserver = worlds.get(j);

				if (worldserver != null) {
					if (!flag) {
						MinecraftServer.i.info("Saving chunks for level \'" + worldserver.getWorldData().getName() + "\'/" + worldserver.worldProvider.getName());
					}

					worldserver.save(true, (IProgressUpdate) null);
					worldserver.saveLevel();

					WorldSaveEvent event = new WorldSaveEvent(worldserver.getWorld());
					server.getPluginManager().callEvent(event);
					// CraftBukkit end
				}
			}
		}
	}

	public void stop() throws ExceptionWorldConflict { // CraftBukkit - added throws
		if (!M) {
			i.info("Stopping server");
			// CraftBukkit start
			if (server != null) {
				server.disablePlugins();
			}
			// CraftBukkit end

			if (ai() != null) {
				ai().b();
			}

			if (u != null) {
				i.info("Saving players");
				u.savePlayers();
				u.u();
			}

			if (worldServer != null) {
				i.info("Saving worlds");
				saveChunks(false);

				/* CraftBukkit start - Handled in saveChunks
				for (int i = 0; i < this.worldServer.length; ++i) {
				    WorldServer worldserver = this.worldServer[i];

				    worldserver.saveLevel();
				}
				// CraftBukkit end */
			}

			// Spigot start
			if (org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly) {
				i.info("Saving usercache.json");
				X.c();
			}
			//Spigot end
		}
	}

	public String getServerIp() {
		return serverIp;
	}

	public void c(String s) {
		serverIp = s;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void safeShutdown() {
		isRunning = false;
	}

	// Spigot Start
	private static double calcTps(double avg, double exp, double tps) {
		return avg * exp + tps * (1 - exp);
	}

	// Spigot End

	@Override
	public void run() {
		try {
			if (init(fullConsole)) {
				long i = ar();
				long j = 0L;

				q.setMOTD(new ChatComponentText(motd));
				q.setServerInfo(new ServerPingServerData("1.7.10", 5));
				this.a(q);

				// Spigot start
				Arrays.fill(recentTps, 20);
				long lastTick = System.nanoTime(), catchupTime = 0, curTime, wait, tickSection = lastTick;
				while (isRunning) {
					curTime = System.nanoTime();
					wait = TICK_TIME - (curTime - lastTick) - catchupTime;
					if (wait > 0) {
						Thread.sleep(wait / 1000000);
						catchupTime = 0;
						continue;
					} else {
						catchupTime = Math.min(1000000000, Math.abs(wait));
					}

					if (MinecraftServer.currentTick++ % SAMPLE_INTERVAL == 0) {
						double currentTps = 1E9 / (curTime - tickSection) * SAMPLE_INTERVAL;
						recentTps[0] = calcTps(recentTps[0], 0.92, currentTps); // 1/exp(5sec/1min)
						recentTps[1] = calcTps(recentTps[1], 0.9835, currentTps); // 1/exp(5sec/5min)
						recentTps[2] = calcTps(recentTps[2], 0.9945, currentTps); // 1/exp(5sec/15min)
						tickSection = curTime;
					}
					lastTick = curTime;

					u();
					O = true;
				}
				// Spigot end
			} else {
				this.a((CrashReport) null);
			}
		} catch (Throwable throwable) {
			i.error("Encountered an unexpected exception", throwable);
			// Spigot Start
			if (throwable.getCause() != null) {
				i.error("\tCause of unexpected exception was", throwable.getCause());
			}
			// Spigot End
			CrashReport crashreport = null;

			if (throwable instanceof ReportedException) {
				crashreport = this.b(((ReportedException) throwable).a());
			} else {
				crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
			}

			File file1 = new File(new File(s(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");

			if (crashreport.a(file1)) {
				i.error("This crash report has been saved to: " + file1.getAbsolutePath());
			} else {
				i.error("We were unable to save this crash report to disk.");
			}

			this.a(crashreport);
		} finally {
			try {
				org.spigotmc.WatchdogThread.doStop();
				stop();
				isStopped = true;
			} catch (Throwable throwable1) {
				i.error("Exception stopping the server", throwable1);
			} finally {
				// CraftBukkit start - Restore terminal to original settings
				try {
					reader.getTerminal().restore();
				} catch (Exception e) {
				}
				// CraftBukkit end
				t();
			}
		}
	}

	private void a(ServerPing serverping) {
		File file1 = d("server-icon.png");

		if (file1.isFile()) {
			ByteBuf bytebuf = Unpooled.buffer();

			try {
				BufferedImage bufferedimage = ImageIO.read(file1);

				Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
				Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
				ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
				ByteBuf bytebuf1 = Base64.encode(bytebuf);

				serverping.setFavicon("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
			} catch (Exception exception) {
				i.error("Couldn\'t load server icon", exception);
			} finally {
				bytebuf.release();
			}
		}
	}

	protected File s() {
		return new File(".");
	}

	protected void a(CrashReport crashreport) {
	}

	protected void t() {
	}

	protected void u() throws ExceptionWorldConflict { // CraftBukkit - added throws
		SpigotTimings.serverTickTimer.startTiming(); // Spigot
		long i = System.nanoTime();

		++ticks;
		if (R) {
			R = false;
			methodProfiler.a = true;
			methodProfiler.a();
		}

		methodProfiler.a("root");
		v();
		if (i - V >= 5000000000L) {
			V = i;
			q.setPlayerSample(new ServerPingPlayerSample(D(), C()));
			GameProfile[] agameprofile = new GameProfile[Math.min(C(), 12)];
			int j = MathHelper.nextInt(r, 0, C() - agameprofile.length);

			for (int k = 0; k < agameprofile.length; ++k) {
				agameprofile[k] = ((EntityPlayer) u.players.get(j + k)).getProfile();
			}

			Collections.shuffle(Arrays.asList(agameprofile));
			q.b().a(agameprofile);
		}

		if (autosavePeriod > 0 && ticks % autosavePeriod == 0) { // CraftBukkit
			SpigotTimings.worldSaveTimer.startTiming(); // Spigot
			methodProfiler.a("save");
			u.savePlayers();
			// Spigot Start
			// We replace this with saving each individual world as this.saveChunks(...) is broken,
			// and causes the main thread to sleep for random amounts of time depending on chunk activity
			server.playerCommandState = true;
			for (World world : worlds) {
				world.getWorld().save();
			}
			server.playerCommandState = false;
			// this.saveChunks(true);
			// Spigot End
			methodProfiler.b();
			SpigotTimings.worldSaveTimer.stopTiming(); // Spigot
		}

		methodProfiler.a("tallying");
		g[ticks % 100] = System.nanoTime() - i;
		methodProfiler.b();
		
		// ClipSpigot start - cut NSA
		/*methodProfiler.a("snooper");
		if (getSnooperEnabled() && !l.d() && ticks > 100) { // Spigot
			l.a();
		}

		if (getSnooperEnabled() && ticks % 6000 == 0) { // Spigot
			l.b();
		}*/
		// ClipSpigot end

		methodProfiler.b();
		methodProfiler.b();
		org.spigotmc.WatchdogThread.tick(); // Spigot
		SpigotTimings.serverTickTimer.stopTiming(); // Spigot
		org.spigotmc.CustomTimingsHandler.tick(); // Spigot
	}

	public void v() {
		methodProfiler.a("levels");

		SpigotTimings.schedulerTimer.startTiming(); // Spigot
		// CraftBukkit start
		server.getScheduler().mainThreadHeartbeat(ticks);
		SpigotTimings.schedulerTimer.stopTiming(); // Spigot

		// Run tasks that are waiting on processing
		SpigotTimings.processQueueTimer.startTiming(); // Spigot
		while (!processQueue.isEmpty()) {
			processQueue.remove().run();
		}
		SpigotTimings.processQueueTimer.stopTiming(); // Spigot

		SpigotTimings.chunkIOTickTimer.startTiming(); // Spigot
		org.bukkit.craftbukkit.chunkio.ChunkIOExecutor.tick();
		SpigotTimings.chunkIOTickTimer.stopTiming(); // Spigot

		SpigotTimings.timeUpdateTimer.startTiming(); // Spigot
		// Send time updates to everyone, it will get the right time from the world the player is in.
		if (ticks % 20 == 0) {
			for (int i = 0; i < getPlayerList().players.size(); ++i) {
				EntityPlayer entityplayer = (EntityPlayer) getPlayerList().players.get(i);
				entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateTime(entityplayer.world.getTime(), entityplayer.getPlayerTime(), entityplayer.world.getGameRules().getBoolean("doDaylightCycle"))); // Add support for per player time
			}
		}
		SpigotTimings.timeUpdateTimer.stopTiming(); // Spigot

		int i;

		for (i = 0; i < worlds.size(); ++i) {
			long j = System.nanoTime();

			// if (i == 0 || this.getAllowNether()) {
			WorldServer worldserver = worlds.get(i);

			methodProfiler.a(worldserver.getWorldData().getName());
			methodProfiler.a("pools");
			methodProfiler.b();
			/* Drop global time updates
			if (this.ticks % 20 == 0) {
			    this.methodProfiler.a("timeSync");
			    this.t.a(new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.worldProvider.dimension);
			    this.methodProfiler.b();
			}
			// CraftBukkit end */

			methodProfiler.a("tick");

			CrashReport crashreport;

			try {
				worldserver.timings.doTick.startTiming(); // Spigot
				worldserver.doTick();
				worldserver.timings.doTick.stopTiming(); // Spigot
			} catch (Throwable throwable) {
				// Spigot Start
				try {
					crashreport = CrashReport.a(throwable, "Exception ticking world");
				} catch (Throwable t) {
					throw new RuntimeException("Error generating crash report", t);
				}
				// Spigot End
				worldserver.a(crashreport);
				throw new ReportedException(crashreport);
			}

			try {
				worldserver.timings.tickEntities.startTiming(); // Spigot
				worldserver.tickEntities();
				worldserver.timings.tickEntities.stopTiming(); // Spigot
			} catch (Throwable throwable1) {
				// Spigot Start
				try {
					crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
				} catch (Throwable t) {
					throw new RuntimeException("Error generating crash report", t);
				}
				// Spigot End
				worldserver.a(crashreport);
				throw new ReportedException(crashreport);
			}

			methodProfiler.b();
			methodProfiler.a("tracker");
			worldserver.timings.tracker.startTiming(); // Spigot
			worldserver.getTracker().updatePlayers();
			worldserver.timings.tracker.stopTiming(); // Spigot
			methodProfiler.b();
			methodProfiler.b();
			// } // CraftBukkit

			// this.h[i][this.ticks % 100] = System.nanoTime() - j; // CraftBukkit
		}

		methodProfiler.c("connection");
		SpigotTimings.connectionTimer.startTiming(); // Spigot
		ai().c();
		SpigotTimings.connectionTimer.stopTiming(); // Spigot
		methodProfiler.c("players");
		SpigotTimings.playerListTimer.startTiming(); // Spigot
		u.tick();
		SpigotTimings.playerListTimer.stopTiming(); // Spigot
		methodProfiler.c("tickables");

		SpigotTimings.tickablesTimer.startTiming(); // Spigot
		for (i = 0; i < n.size(); ++i) {
			((IUpdatePlayerListBox) n.get(i)).a();
		}
		SpigotTimings.tickablesTimer.stopTiming(); // Spigot

		methodProfiler.b();
	}

	public boolean getAllowNether() {
		return true;
	}

	public void a(IUpdatePlayerListBox iupdateplayerlistbox) {
		n.add(iupdateplayerlistbox);
	}

	public static void main(final OptionSet options) { // CraftBukkit - replaces main(String[] astring)
		DispenserRegistry.b();
		org.spigotmc.ProtocolInjector.inject();

		try {
			/* CraftBukkit start - Replace everything
			boolean flag = true;
			String s = null;
			String s1 = ".";
			String s2 = null;
			boolean flag1 = false;
			boolean flag2 = false;
			int i = -1;

			for (int j = 0; j < astring.length; ++j) {
			    String s3 = astring[j];
			    String s4 = j == astring.length - 1 ? null : astring[j + 1];
			    boolean flag3 = false;

			    if (!s3.equals("nogui") && !s3.equals("--nogui")) {
			        if (s3.equals("--port") && s4 != null) {
			            flag3 = true;

			            try {
			                i = Integer.parseInt(s4);
			            } catch (NumberFormatException numberformatexception) {
			                ;
			            }
			        } else if (s3.equals("--singleplayer") && s4 != null) {
			            flag3 = true;
			            s = s4;
			        } else if (s3.equals("--universe") && s4 != null) {
			            flag3 = true;
			            s1 = s4;
			        } else if (s3.equals("--world") && s4 != null) {
			            flag3 = true;
			            s2 = s4;
			        } else if (s3.equals("--demo")) {
			            flag1 = true;
			        } else if (s3.equals("--bonusChest")) {
			            flag2 = true;
			        }
			    } else {
			        flag = false;
			    }

			    if (flag3) {
			        ++j;
			    }
			}

			DedicatedServer dedicatedserver = new DedicatedServer(new File(s1));

			if (s != null) {
			    dedicatedserver.j(s);
			}

			if (s2 != null) {
			    dedicatedserver.k(s2);
			}

			if (i >= 0) {
			    dedicatedserver.setPort(i);
			}

			if (flag1) {
			    dedicatedserver.b(true);
			}

			if (flag2) {
			    dedicatedserver.c(true);
			}

			if (flag) {
			    dedicatedserver.aD();
			}
			// */

			DedicatedServer dedicatedserver = new DedicatedServer(options);

			if (options.has("port")) {
				int port = (Integer) options.valueOf("port");
				if (port > 0) {
					dedicatedserver.setPort(port);
				}
			}

			if (options.has("universe")) {
				dedicatedserver.universe = (File) options.valueOf("universe");
			}

			if (options.has("world")) {
				dedicatedserver.k((String) options.valueOf("world"));
			}

			dedicatedserver.primaryThread.start();
			// Runtime.getRuntime().addShutdownHook(new ThreadShutdown("Server Shutdown Thread", dedicatedserver));
			// CraftBukkit end
		} catch (Exception exception) {
			i.fatal("Failed to start the minecraft server", exception);
		}
	}

	public void x() {
		// (new ThreadServerApplication(this, "Server thread")).start(); // CraftBukkit - prevent abuse
	}

	public File d(String s) {
		return new File(s(), s);
	}

	public void info(String s) {
		i.info(s);
	}

	public void warning(String s) {
		i.warn(s);
	}

	public WorldServer getWorldServer(int i) {
		// CraftBukkit start
		for (WorldServer world : worlds) {
			if (world.dimension == i)
				return world;
		}

		return worlds.get(0);
		// CraftBukkit end
	}

	public String y() {
		return serverIp;
	}

	public int z() {
		return t;
	}

	public String A() {
		return motd;
	}

	public String getVersion() {
		return "1.7.10";
	}

	public int C() {
		return u.getPlayerCount();
	}

	public int D() {
		return u.getMaxPlayers();
	}

	public String[] getPlayers() {
		return u.f();
	}

	public GameProfile[] F() {
		return u.g();
	}

	public String getPlugins() {
		// CraftBukkit start - Whole method
		StringBuilder result = new StringBuilder();
		org.bukkit.plugin.Plugin[] plugins = server.getPluginManager().getPlugins();

		result.append(server.getName());
		result.append(" on Bukkit ");
		result.append(server.getBukkitVersion());

		if (plugins.length > 0 && server.getQueryPlugins()) {
			result.append(": ");

			for (int i = 0; i < plugins.length; i++) {
				if (i > 0) {
					result.append("; ");
				}

				result.append(plugins[i].getDescription().getName());
				result.append(" ");
				result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
			}
		}

		return result.toString();
		// CraftBukkit end
	}

	// CraftBukkit start - fire RemoteServerCommandEvent
	public String g(final String s) { // final parameter
		Waitable<String> waitable = new Waitable<String>() {
			@Override
			protected String evaluate() {
				RemoteControlCommandListener.instance.e();
				// Event changes start
				RemoteServerCommandEvent event = new RemoteServerCommandEvent(remoteConsole, s);
				server.getPluginManager().callEvent(event);
				// Event changes end
				ServerCommand servercommand = new ServerCommand(event.getCommand(), RemoteControlCommandListener.instance);
				server.dispatchServerCommand(remoteConsole, servercommand); // CraftBukkit
				// this.o.a(RemoteControlCommandListener.instance, s);
				return RemoteControlCommandListener.instance.f();
			}
		};
		processQueue.add(waitable);
		try {
			return waitable.get();
		} catch (java.util.concurrent.ExecutionException e) {
			throw new RuntimeException("Exception processing rcon command " + s, e.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Maintain interrupted state
			throw new RuntimeException("Interrupted processing rcon command " + s, e);
		}
		// CraftBukkit end
	}

	public boolean isDebugging() {
		return getPropertyManager().getBoolean("debug", false); // CraftBukkit - don't hardcode
	}

	public void h(String s) {
		i.error(s);
	}

	public void i(String s) {
		if (isDebugging()) {
			i.info(s);
		}
	}

	public String getServerModName() {
		return "ClipSpigot"; // ClipSpigot - ClipSpigot > // PaperSpigot - PaperSpigot > // Spigot - Spigot > // CraftBukkit - cb > vanilla!
	}

	public CrashReport b(CrashReport crashreport) {
		crashreport.g().a("Profiler Position", new CrashReportProfilerPosition(this));
		if (worlds != null && worlds.size() > 0 && worlds.get(0) != null) { // CraftBukkit
			crashreport.g().a("Vec3 Pool Size", new CrashReportVec3DPoolSize(this));
		}

		if (u != null) {
			crashreport.g().a("Player Count", new CrashReportPlayerCount(this));
		}

		return crashreport;
	}

	public List a(ICommandListener icommandlistener, String s) {
		// CraftBukkit start - Allow tab-completion of Bukkit commands
		/*
		ArrayList arraylist = new ArrayList();

		if (s.startsWith("/")) {
		    s = s.substring(1);
		    boolean flag = !s.contains(" ");
		    List list = this.o.b(icommandlistener, s);

		    if (list != null) {
		        Iterator iterator = list.iterator();

		        while (iterator.hasNext()) {
		            String s1 = (String) iterator.next();

		            if (flag) {
		                arraylist.add("/" + s1);
		            } else {
		                arraylist.add(s1);
		            }
		        }
		    }

		    return arraylist;
		} else {
		    String[] astring = s.split(" ", -1);
		    String s2 = astring[astring.length - 1];
		    String[] astring1 = this.u.f();
		    int i = astring1.length;

		    for (int j = 0; j < i; ++j) {
		        String s3 = astring1[j];

		        if (CommandAbstract.a(s2, s3)) {
		            arraylist.add(s3);
		        }
		    }

		    return arraylist;
		}
		*/
		return server.tabComplete(icommandlistener, s);
		// CraftBukkit end
	}

	public static MinecraftServer getServer() {
		return j;
	}

	@Override
	public String getName() {
		return "Server";
	}

	@Override
	public void sendMessage(IChatBaseComponent ichatbasecomponent) {
		i.info(ichatbasecomponent.c());
	}

	@Override
	public boolean a(int i, String s) {
		return true;
	}

	public ICommandHandler getCommandHandler() {
		return o;
	}

	public KeyPair K() {
		return G;
	}

	public int L() {
		return t;
	}

	public void setPort(int i) {
		t = i;
	}

	public String M() {
		return H;
	}

	public void j(String s) {
		H = s;
	}

	public boolean N() {
		return H != null;
	}

	public String O() {
		return I;
	}

	public void k(String s) {
		I = s;
	}

	public void a(KeyPair keypair) {
		G = keypair;
	}

	public void a(EnumDifficulty enumdifficulty) {
		// CraftBukkit start - Use worlds list for iteration
		for (int j = 0; j < worlds.size(); ++j) {
			WorldServer worldserver = worlds.get(j);
			// CraftBukkit end

			if (worldserver != null) {
				if (worldserver.getWorldData().isHardcore()) {
					worldserver.difficulty = EnumDifficulty.HARD;
					worldserver.setSpawnFlags(true, true);
				} else if (N()) {
					worldserver.difficulty = enumdifficulty;
					worldserver.setSpawnFlags(worldserver.difficulty != EnumDifficulty.PEACEFUL, true);
				} else {
					worldserver.difficulty = enumdifficulty;
					worldserver.setSpawnFlags(getSpawnMonsters(), spawnAnimals);
				}
			}
		}
	}

	protected boolean getSpawnMonsters() {
		return true;
	}

	public boolean R() {
		return demoMode;
	}

	public void b(boolean flag) {
		demoMode = flag;
	}

	public void c(boolean flag) {
		L = flag;
	}

	public Convertable getConvertable() {
		return convertable;
	}

	public void U() {
		M = true;
		getConvertable().d();

		// CraftBukkit start
		for (int i = 0; i < worlds.size(); ++i) {
			WorldServer worldserver = worlds.get(i);
			// CraftBukkit end

			if (worldserver != null) {
				worldserver.saveLevel();
			}
		}

		getConvertable().e(worlds.get(0).getDataManager().g()); // CraftBukkit
		safeShutdown();
	}

	public String getResourcePack() {
		return N;
	}

	public void setTexturePack(String s) {
		N = s;
	}

	public abstract boolean X();

	public boolean getOnlineMode() {
		return server.getOnlineMode(); // CraftBukkit
	}

	public void setOnlineMode(boolean flag) {
		onlineMode = flag;
	}

	public boolean getSpawnAnimals() {
		return spawnAnimals;
	}

	public void setSpawnAnimals(boolean flag) {
		spawnAnimals = flag;
	}

	public boolean getSpawnNPCs() {
		return spawnNPCs;
	}

	public void setSpawnNPCs(boolean flag) {
		spawnNPCs = flag;
	}

	public boolean getPvP() {
		return pvpMode;
	}

	public void setPvP(boolean flag) {
		pvpMode = flag;
	}

	public boolean getAllowFlight() {
		return allowFlight;
	}

	public void setAllowFlight(boolean flag) {
		allowFlight = flag;
	}

	public abstract boolean getEnableCommandBlock();

	public String getMotd() {
		return motd;
	}

	public void setMotd(String s) {
		motd = s;
	}

	public int getMaxBuildHeight() {
		return E;
	}

	public void c(int i) {
		E = i;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public PlayerList getPlayerList() {
		return u;
	}

	public void a(PlayerList playerlist) {
		u = playerlist;
	}

	public void a(EnumGamemode enumgamemode) {
		// CraftBukkit start - use worlds list for iteration
		for (int i = 0; i < worlds.size(); ++i) {
			getServer().worlds.get(i).getWorldData().setGameType(enumgamemode);
			// CraftBukkit end
		}
	}

	// Spigot Start
	public ServerConnection getServerConnection() {
		return p;
	}

	// Spigot End
	public ServerConnection ai() {
		return p == null ? p = new ServerConnection(this) : p; // Spigot
	}

	public boolean ak() {
		return false;
	}

	public abstract String a(EnumGamemode enumgamemode, boolean flag);

	public int al() {
		return ticks;
	}

	public void am() {
		R = true;
	}

	@Override
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getWorld() {
		return worlds.get(0); // CraftBukkit
	}

	public int getSpawnProtection() {
		return 16;
	}

	public boolean a(World world, int i, int j, int k, EntityHuman entityhuman) {
		return false;
	}

	public void setForceGamemode(boolean flag) {
		S = flag;
	}

	public boolean getForceGamemode() {
		return S;
	}

	public Proxy aq() {
		return d;
	}

	public static long ar() {
		return System.currentTimeMillis();
	}

	public int getIdleTimeout() {
		return F;
	}

	public void setIdleTimeout(int i) {
		F = i;
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatComponentText(getName());
	}

	public boolean at() {
		return true;
	}

	public MinecraftSessionService av() {
		return U;
	}

	public GameProfileRepository getGameProfileRepository() {
		return W;
	}

	public UserCache getUserCache() {
		return X;
	}

	public ServerPing ay() {
		return q;
	}

	public void az() {
		V = 0L;
	}

	public static Logger getLogger() {
		return i;
	}

	public static PlayerList a(MinecraftServer minecraftserver) {
		return minecraftserver.u;
	}
}
