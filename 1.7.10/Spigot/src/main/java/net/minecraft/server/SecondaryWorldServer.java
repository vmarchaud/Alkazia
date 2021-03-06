package net.minecraft.server;

public class SecondaryWorldServer extends WorldServer {
	// CraftBukkit start - Add Environment and ChunkGenerator arguments
	public SecondaryWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, WorldSettings worldsettings, WorldServer worldserver, MethodProfiler methodprofiler, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen) {
		super(minecraftserver, idatamanager, s, i, worldsettings, methodprofiler, env, gen);
		// CraftBukkit end
		worldMaps = worldserver.worldMaps;
		scoreboard = worldserver.getScoreboard();
		// this.worldData = new SecondaryWorldData(worldserver.getWorldData()); // CraftBukkit - use unique worlddata
	}

	// protected void a() {} // CraftBukkit - save world data!
}
