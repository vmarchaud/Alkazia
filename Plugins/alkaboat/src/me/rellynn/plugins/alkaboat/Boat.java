package me.rellynn.plugins.alkaboat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

public class Boat extends BukkitRunnable implements Listener {
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

	public BossBarCountdown(final BoatPlugin plugin, final int prespawnDuration, final MathsUtils.Coord spawnCoord) {
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
		message.append("&eLe bateau va s'échouer dans&a ");
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
		message.append("&eCoordonnées de l'accident  X = &a");
		message.append(this.spawnCoord.getX());
		message.append(" &eet Z = &a");
		message.append(this.spawnCoord.getZ());
	    }

	    final String formattedMsg = ChatColor.translateAlternateColorCodes('&', message.toString());

	    final float progress = (float) remainingSeconds / prespawnDuration;

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

    public static class SpawnMobTask extends BukkitRunnable {
	private final Location middle;
	private final int radius;
	private final Random random = new Random();
	private final Material[] swords = new Material[] { null, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD, null };
	private final Material[] helmets = new Material[] { null, Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.CHAINMAIL_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET, null };
	private final Material[] plates = new Material[] { null, Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.LEATHER_CHESTPLATE, null };
	private final Material[] leggings = new Material[] { null, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLD_LEGGINGS, Material.LEATHER_LEGGINGS, null };
	private final Material[] boots = new Material[] { null, Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS, null };

	public SpawnMobTask(final BoatPlugin plugin, final Location middle, final int radius) {
	    this.middle = middle;
	    this.radius = radius;
	    this.runTaskTimer(plugin, 0L, 200L);
	}

	public Entity[] getNearbyEntities(final Location l, final int radius) {
	    final int chunkRadius = radius < 16 ? 1 : (radius - radius % 16) / 16;
	    final HashSet<Entity> radiusEntities = new HashSet<>();
	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++)
		for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
		    final int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
		    for (final Entity e : new Location(l.getWorld(), x + chX * 16, y, z + chZ * 16).getChunk().getEntities())
			if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
		}
	    return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}

	@Override
	public void run() {
	    for (final Entity entity : this.getNearbyEntities(this.middle, this.radius)) {
			if (!(entity instanceof Player)) continue;
			final Player player = (Player) entity;
			final Location location = player.getLocation();
			final Block down = location.getBlock().getRelative(BlockFace.DOWN);
			if (down.getTypeId() != 8 && down.getTypeId() != 9) {
			    final Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
			    final EntityEquipment equip = zombie.getEquipment();
			    final Material sword = this.swords[this.random.nextInt(this.swords.length)];
			    if (sword != null) {
				final int rand = this.random.nextInt(100);
				equip.setItemInHand(new ItemStack(sword, 1, (short) rand));
				equip.setItemInHandDropChance(100 - rand);
			    }
			    final Material helmet = this.helmets[this.random.nextInt(this.helmets.length)];
			    if (helmet != null) {
				final int rand = this.random.nextInt(100);
				equip.setHelmet(new ItemStack(helmet, 1, (short) rand));
				equip.setHelmetDropChance(100 - rand);
			    }
			    final Material plate = this.plates[this.random.nextInt(this.plates.length)];
			    if (plate != null) {
				final int rand = this.random.nextInt(100);
				equip.setChestplate(new ItemStack(plate, 1, (short) rand));
				equip.setChestplateDropChance(100 - rand);
			    }
			    final Material leggings = this.leggings[this.random.nextInt(this.leggings.length)];
			    if (leggings != null) {
				final int rand = this.random.nextInt(100);
				equip.setChestplate(new ItemStack(leggings, 1, (short) rand));
				equip.setChestplateDropChance(100 - rand);
			    }
			    final Material boots = this.boots[this.random.nextInt(this.boots.length)];
			    if (boots != null) {
				final int rand = this.random.nextInt(100);
				equip.setChestplate(new ItemStack(boots, 1, (short) rand));
				equip.setChestplateDropChance(100 - rand);
			    }
			}
	    }
	}

	public void unset() {
	    for (final Entity entity : this.getNearbyEntities(this.middle, this.radius))
		if (entity instanceof Zombie) entity.remove();
	    this.cancel();
	}
    }

    private final BoatPlugin plugin;
    private final World world;
    private final File schematicFile;
    private final List<MathsUtils.Coord> noClaimChunks = new ArrayList<>();
    private final int prespawnDuration;
    private Chunk spawnedChunk;
    private Location spawnLocation;
    private BossBarCountdown bossBarC;

    private final ActivationCooldown coolActivation;

    private SpawnMobTask spawnTask;
    private int elapsedDuration = 0;

    private int elapsedSecs = -1;

    private boolean spawned = false;

    private EditSession sessionUsedGenerateStructure;

    public Boat(final BoatPlugin plugin, final int prespawnDuration, final File schematic, final World world, final int activationDelay) {
	this.plugin = plugin;
	this.prespawnDuration = prespawnDuration;
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
		    final Chunk chunk = this.world.getChunkAt(x, z);
		    if (!chunk.isLoaded()) chunk.load(true);
		    x = chunk.getBlock(0, 0, 0).getX();
		    z = chunk.getBlock(0, 0, 0).getZ();
		    final Block block = this.world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);

		    boolean found = true;
		    for (final MathsUtils.Coord coord : MathsUtils.getPoints(1, new MathsUtils.Coord(x, z))) {
			final Biome biome = this.world.getBiome(coord.getX(), coord.getZ());
			if (biome != Biome.OCEAN && biome != Biome.DEEP_OCEAN) found = false;
		    }
		    if (found) {
			this.spawnLocation = block.getLocation().subtract(0, this.plugin.getUnderBlocks(), 0);
			return chunk;
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
		ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVous ne pouvez pas claim dans cette zone à cause du &cbateau"));
		ev.setCancelled(true);
	    }
    }

    public void regenerate() {
	if (this.sessionUsedGenerateStructure != null) this.sessionUsedGenerateStructure.undo(this.sessionUsedGenerateStructure);
	if (this.spawnTask != null) this.spawnTask.unset();
    }

    @Override
    public void run() {
	this.elapsedSecs += 1;
	if (this.elapsedSecs % 60 != 0 && this.elapsedDuration != 0) return;
	if (this.elapsedDuration == 0) {
	    if (Bukkit.getOnlinePlayers().size() == 0) return;

	    this.plugin.getLogger().info("Initialisation du bateau.");

	    this.spawnedChunk = this.getSpawnableChunk();
	    if (this.spawnedChunk == null) {
		this.plugin.getLogger().severe("Aucun emplacement correct n'a pu etre trouve pour spawner le bateau.");
		this.cancel();
		return;
	    }

	    this.noClaimChunks.addAll(MathsUtils.getPoints(this.plugin.getNoClaimRadius(), new MathsUtils.Coord(this.spawnedChunk.getX(), this.spawnedChunk.getZ())));

	    for (final Player player : Bukkit.getOnlinePlayers())
		player.playSound(player.getLocation(), Sound.WITHER_DEATH, 3.0F, 0.0F);

	    this.bossBarC = new BossBarCountdown(this.plugin, this.prespawnDuration, new MathsUtils.Coord(this.spawnLocation.getBlockX(), this.spawnLocation.getBlockZ()));
	    this.elapsedSecs = 0;
	} else if (this.elapsedDuration == this.prespawnDuration) {
	    this.plugin.getLogger().info("Generation du bateau.");
	    this.spawn();
	} else if (this.elapsedDuration >= 240) {
	    this.plugin.getLogger().info("Regeneration des chunks ...");
	    this.regenerate();
	    HandlerList.unregisterAll(this);
	    if (Bukkit.getOnlinePlayers().size() > 0) this.plugin.spawnBoat();
	    else this.plugin.unsetBoat();
	    this.cancel();
	    return;
	}

	this.elapsedDuration += 1;
    }

    private void spawn() {
	if (this.spawned) return;
	this.sessionUsedGenerateStructure = new EditSession(new BukkitWorld(this.world), 2147483647);
	try {
	    final CuboidClipboard cb = SchematicFormat.getFormat(this.schematicFile).load(this.schematicFile);
	    cb.paste(this.sessionUsedGenerateStructure, new Vector(this.spawnLocation.getBlockX(), this.spawnLocation.getBlockY(), this.spawnLocation.getBlockZ()), false);
	    this.plugin.getLogger().info("Position du bateau genere. X = " + this.spawnLocation.getBlockX() + " |---| Y = " + this.spawnLocation.getBlockY() + " |---| Z = " + this.spawnLocation.getBlockZ());
	} catch (IOException | DataException | MaxChangedBlocksException e) {
	    e.printStackTrace();
	}
	this.spawnTask = new SpawnMobTask(this.plugin, new Location(this.world, this.spawnLocation.getBlockX() + 8, this.spawnLocation.getBlockY(), this.spawnLocation.getBlockZ() + 8), 32);
	this.spawned = true;
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
}
