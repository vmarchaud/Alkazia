package fr.Alphart.Meteor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.rellynn.plugins.meteor.ChunkBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.LandClaimEvent;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.wimbli.WorldBorder.BorderData;

import fr.thisismac.level.Main;

public class Meteor extends BukkitRunnable implements Listener {
    public static class ActivationCooldown extends BukkitRunnable {
	private final int activationDelay;
	private int elapsedSeconds;

	public ActivationCooldown(final int activationDelay) {
	    this.activationDelay = activationDelay * 60;
	}

	public int getRemainingSeconds() {
	    return this.activationDelay - this.elapsedSeconds;
	}

	@Override
	public void run() {
	    this.elapsedSeconds += 1;
	    if (this.elapsedSeconds == this.activationDelay) this.cancel();
	}
    }

    public static class BossBarCountdown extends BukkitRunnable {
	private final MathsUtils.Coord spawnCoord;
	private final int prespawnDuration;
	private int remainingSeconds;

	public BossBarCountdown(final MeteorPlugin plugin, final int prespawnDuration, final MathsUtils.Coord spawnCoord) {
	    this.spawnCoord = spawnCoord;
	    this.prespawnDuration = prespawnDuration * 60;
	    this.remainingSeconds = this.prespawnDuration;
	    this.runTaskTimer(plugin, 0L, 20L);
	}

	@Override
	public void run() {
	    if (this.remainingSeconds == 0) {
	    	for(Player p : Bukkit.getOnlinePlayers()) {
			    p.sendMessage("[BOSSBAR]REMOVE");
	    	}
		this.cancel();
		return;
	    }

	    final int minutes = this.remainingSeconds / 60;
	    final int seconds = this.remainingSeconds % 60;

	    final StringBuilder message = new StringBuilder();
	    if (this.remainingSeconds % 10 > 5) {
		message.append("&eLa météorite va s'écraser dans &a");
		if (minutes > 0) {
		    message.append(minutes);
		    message.append(minutes == 1 ? " minute" : " minutes");
		}
		if (minutes > 0 && seconds > 0) message.append(" &eet &a");
		if (seconds > 0) {
		    message.append(seconds);
		    message.append(seconds == 1 ? " seconde" : " secondes");
		}
	    } else {
		message.append("&eCoordonnées de crash X = &a");
		message.append(this.spawnCoord.getX());
		message.append(" &eet Z = &a");
		message.append(this.spawnCoord.getZ());
	    }

	    final String formattedMsg = ChatColor.translateAlternateColorCodes('&', message.toString());

	    final float progress = (float) this.remainingSeconds / this.prespawnDuration;

	    for (final Player player : Bukkit.getOnlinePlayers()) {
	    	player.sendMessage("[BOSSBAR]:" + progress + ":" + formattedMsg);
	    }

	    this.remainingSeconds -= 1;
	}

	public void unset() {
	    try {
		this.cancel();
	    } catch (final IllegalStateException localIllegalStateException) {
	    }
	    for(Player p : Bukkit.getOnlinePlayers()) {
		    p.sendMessage("[BOSSBAR]REMOVE");
    	}
	}
    }


    private final MeteorPlugin plugin;
    private final World world;
    private final File schematicFile;
    private final List<MathsUtils.Coord> noClaimChunks = new ArrayList<>();
    private final List<ChunkBlock> resetChunk = new ArrayList<>();
    private final int prespawnDuration;
    private final int taggedDuration;
    private Chunk spawnedChunk;
    private Location spawnLocation;
    private BossBarCountdown bossBarC;

    private BukkitTask tagTask;

    private final ActivationCooldown coolActivation;
    private int elapsedDuration = 0;

    private int elapsedSecs = -1;

    private boolean spawned = false;

    private EditSession sessionUsedGenerateStructure;

    public Meteor(final MeteorPlugin plugin, final int prespawnDuration, final int taggedDuration, final File schematic, final World world, final int activationDelay) {
	this.plugin = plugin;
	this.prespawnDuration = prespawnDuration;
	this.taggedDuration = taggedDuration;
	this.schematicFile = schematic;
	this.world = world;
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
	this.runTaskTimer(plugin, activationDelay * 60 * 20, 20L);
	this.coolActivation = new ActivationCooldown(activationDelay);
	this.coolActivation.runTaskTimer(plugin, 1L, 1L);
    }

    public BossBarCountdown getBossBarC() {
	return this.bossBarC;
    }

    public ActivationCooldown getCoolActivation() {
	return this.coolActivation;
    }

    public int getElapsedDuration() {
	return this.elapsedDuration;
    }

    public int getElapsedSeconds() {
	return this.elapsedSecs;
    }

    public int getPrespawnDuration() {
	return this.prespawnDuration;
    }
    

	public Location findPosition() {
		Random random = new Random();
		
		for(int i = 0; i < 50; i++) { 
			int x = (int)this.getSpawnLocation().getX() - (random.nextInt(500) + 250);
			int z = (int)this.getSpawnLocation().getZ() + (random.nextInt(500) + 250);
				
			Faction fac = Board.getFactionAt(new FLocation(world.getName(), x, z));
			
			if(fac != null && fac.getId().equals("0")) {
				Location loc = new Location(world, (double) x, (double)world.getHighestBlockAt(x, z).getY(), (double)z);
						
				if(loc.distance(getSpawnLocation()) > 400 && loc.distance(getSpawnLocation()) < 550) {
					return loc;
				}
			}
			
		}
		
		return null;
	}

    private Chunk getSpawnableChunk() {
	final Random rnd = new Random();
	final BorderData border = this.plugin.getWB().GetWorldBorder(this.world.getName());
	for (int i = 0; i < 100; i++) {
	    int x = 2147483647;
	    int z = 2147483647;
	    final int radiusX = border.getRadiusX();
	    final int borderX = (int) border.getX() / 16;
	    final int radiusZ = border.getRadiusZ();
	    final int borderZ = (int) border.getZ() / 16;
	    if (border.getX() >= 500.0D) border.getZ();

	    while (!border.insideBorder(x, z)) {
		final int maxRadiusX = radiusX - 400;
		x = rnd.nextInt(maxRadiusX);
		if (rnd.nextInt(2) == 1) x += borderX;
		else x = borderX - x;
		x /= 16;
		final int maxRadiusZ = radiusZ - 400;
		z = rnd.nextInt(maxRadiusZ);
		if (rnd.nextInt(2) == 1) z += borderZ;
		else z = borderZ - z;
		z /= 16;
	    }

	    FLocation pos = new FLocation(new Location(this.world, x * 16, 0.0D, z * 16));
	    Faction faction = Board.getFactionAt(pos);
	    if (faction != null && faction.getId().equals("0")) {
		boolean tooNear = false;
		for (final MathsUtils.Coord coord : MathsUtils.getPoints(5, new MathsUtils.Coord(x, z))) {
		    pos = new FLocation(new Location(this.world, coord.getX() * 16, 0.0D, coord.getZ() * 16));
		    faction = Board.getFactionAt(pos);
		    if (faction == null || !faction.getId().equals("0")) {
			tooNear = true;
			break;
		    }
		}
		if (!tooNear) {
		    this.sessionUsedGenerateStructure = new EditSession(new BukkitWorld(this.world), 2147483647);
		    try {
			final CuboidClipboard cb = SchematicFormat.getFormat(this.schematicFile).load(this.schematicFile);

			final Chunk chunk = this.world.getChunkAt(x, z);
			if (!chunk.isLoaded()) chunk.load(true);
			x = chunk.getBlock(0, 0, 0).getX();
			z = chunk.getBlock(0, 0, 0).getZ();
			Block block = this.world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
			Integer id = block.getTypeId();

			if (id != 8 && id != 9) {
			    final List<Integer> noSpawnItems = this.plugin.getNoSpawnItem();
			    while (noSpawnItems.contains(id)) {
				block = block.getRelative(BlockFace.DOWN);
				id = block.getTypeId();
			    }
			    this.spawnLocation = block.getLocation();
			    return chunk;
			}
		    } catch (IOException | DataException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return null;
    }

    public Location getSpawnLocation() {
	return this.spawnLocation;
    }

    public boolean isSpawned() {
	return this.spawned;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClaim(final LandClaimEvent ev) {
	final MathsUtils.Coord chunkCoord = new MathsUtils.Coord(ev.getPlayer().getLocation().getChunk().getX(), ev.getPlayer().getLocation().getChunk().getZ());
	for (final MathsUtils.Coord coord : this.noClaimChunks)
	    if (chunkCoord.equals(coord)) {
		ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVous ne pouvez pas claim dans cette zone � cause de la &cm�t�orite"));
		ev.setCancelled(true);
	    }
    }

    public void regenerate() {
    	if (this.sessionUsedGenerateStructure != null) this.sessionUsedGenerateStructure.undo(this.sessionUsedGenerateStructure);
    }

    @Override
    public void run() {
	this.elapsedSecs += 1;
	if (this.elapsedSecs % 60 != 0 && this.elapsedDuration != 0) return;
	if (this.elapsedDuration == 0) {
	    if (Bukkit.getOnlinePlayers().size() == 0) return;

	    this.plugin.getLogger().info("Initialisation de la meteorite.");

	    this.spawnedChunk = this.getSpawnableChunk();
	    if (this.spawnedChunk == null) {
		this.plugin.getLogger().severe("Aucun emplacement correct n'a pu etre trouve pour spawner la meteorite.");
		this.cancel();
		return;
	    }

	    this.noClaimChunks.addAll(MathsUtils.getPoints(this.plugin.getNoClaimRadius(), new MathsUtils.Coord(this.spawnedChunk.getX(), this.spawnedChunk.getZ())));

	    for (final Player player : Bukkit.getOnlinePlayers())
		player.playSound(player.getLocation(), Sound.WITHER_DEATH, 3.0F, 0.0F);

	    this.bossBarC = new BossBarCountdown(this.plugin, this.prespawnDuration, new MathsUtils.Coord(this.spawnLocation.getBlockX(), this.spawnLocation.getBlockZ()));
	    this.elapsedSecs = 0;
	} else if (this.elapsedDuration == this.prespawnDuration) {
	    this.plugin.getLogger().info("Generation de la meteorite et liaison avec CombatTag.");


	    this.spawn();
	    
	} else if (this.elapsedDuration == this.taggedDuration) {
	    if (this.tagTask != null) this.tagTask.cancel();
	    this.plugin.getLogger().info("Les joueurs ayant " + this.plugin.getTaggedMaterial() + " dans leur inventaire ne seront plus tagges.");
	} else if (this.elapsedDuration >= 120) {
	    this.plugin.getLogger().info("Regeneration du chunk ...");
	    this.regenerate();
	    HandlerList.unregisterAll(this);
	    if (Bukkit.getOnlinePlayers().size() > 0) this.plugin.spawnMeteor();
	    else this.plugin.unsetMeteor();
	    this.cancel();
	    return;
	}

	this.elapsedDuration += 1;
    }

    private void spawn() {
	if (this.spawned) return;
	new BukkitRunnable() {
	    Location spawn = new Location(Meteor.this.world, Meteor.this.spawnLocation.getBlockX(), Meteor.this.spawnLocation.getBlockY(), Meteor.this.spawnLocation.getBlockZ());
	    int ticks = 200;

	    @Override
	    public void run() {
		this.ticks--;
		if (this.ticks == 0) {
		    Meteor.this.sessionUsedGenerateStructure = new EditSession(new BukkitWorld(Meteor.this.world), 2147483647);
		    try {
			Meteor.this.world.createExplosion(this.spawn.getBlockX() + 8, this.spawn.getBlockY(), this.spawn.getBlockZ() + 8, 5F, false, false);
			final CuboidClipboard cb = SchematicFormat.getFormat(Meteor.this.schematicFile).load(Meteor.this.schematicFile);
			cb.paste(Meteor.this.sessionUsedGenerateStructure, new Vector(Meteor.this.spawnLocation.getBlockX(), Meteor.this.spawnLocation.getBlockY(), Meteor.this.spawnLocation.getBlockZ()), false);
			Meteor.this.plugin.getLogger().info("Position de la meteorite generee. X = " + Meteor.this.spawnLocation.getBlockX() + " |---| Y = " + Meteor.this.spawnLocation.getBlockY() + " |---| Z = " + Meteor.this.spawnLocation.getBlockZ());
		    } catch (IOException | DataException | MaxChangedBlocksException e) {
			e.printStackTrace();
		    }
		    this.cancel();
		    return;
		}
		final Location effect = this.spawn.clone().add(8, this.ticks, 8);
		Meteor.this.world.playEffect(effect, Effect.SMOKE, 10);
		Meteor.this.world.playEffect(effect, Effect.MOBSPAWNER_FLAMES, 8);
	    }

	}.runTaskTimer(this.plugin, 0L, 1L);
	Meteor.this.spawned = true;
    }
}