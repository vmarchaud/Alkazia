package org.clipspigot;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class ClipSpigotWorldConfig {

	private final String worldName;
	private final YamlConfiguration config;
	private boolean verbose;

	public ClipSpigotWorldConfig(String worldName) {
		this.worldName = worldName;
		config = ClipSpigotConfig.config;
		init();
	}

	public void init() {
		verbose = getBoolean("verbose", true);

		log("-------- World Settings For [" + worldName + "] --------");
		ClipSpigotConfig.readConfig(ClipSpigotWorldConfig.class, this);
	}

	private void log(String s) {
		if (verbose) {
			Bukkit.getLogger().info(s);
		}
	}

	private void set(String path, Object val) {
		config.set("world-settings.default." + path, val);
	}

	private boolean getBoolean(String path, boolean def) {
		config.addDefault("world-settings.default." + path, def);
		return config.getBoolean("world-settings." + worldName + "." + path, config.getBoolean("world-settings.default." + path));
	}

	private double getDouble(String path, double def) {
		config.addDefault("world-settings.default." + path, def);
		return config.getDouble("world-settings." + worldName + "." + path, config.getDouble("world-settings.default." + path));
	}

	private int getInt(String path, int def) {
		config.addDefault("world-settings.default." + path, def);
		return config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path));
	}

	private float getFloat(String path, float def) {
		config.addDefault("world-settings.default." + path, def);
		return (float)config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path));
	}

	@SuppressWarnings("rawtypes")
	private <T> List getList(String path, T def) {
		config.addDefault("world-settings.default." + path, def);
		return config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
	}

	private String getString(String path, String def) {
		config.addDefault("world-settings.default." + path, def);
		return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
	}

	public boolean allowUndeadHorseLeashing;

	private void allowUndeadHorseLeashing() {
		allowUndeadHorseLeashing = getBoolean("allow-undead-horse-leashing", true);
		log("Allow undead horse types to be leashed: " + allowUndeadHorseLeashing);
	}

	public double squidMinSpawnHeight;
	public double squidMaxSpawnHeight;

	private void squidSpawnHeight() {
		squidMinSpawnHeight = getDouble("squid-spawn-height.minimum", 45.0D);
		squidMaxSpawnHeight = getDouble("squid-spawn-height.maximum", 63.0D);
		log("Squids will spawn between Y: " + squidMinSpawnHeight + " and Y: " + squidMaxSpawnHeight);
	}

	public float playerBlockingDamageMultiplier;

	private void playerBlockingDamageMultiplier() {
		playerBlockingDamageMultiplier = getFloat("player-blocking-damage-multiplier", 0.5F);
		log("Player blocking damage multiplier set to " + playerBlockingDamageMultiplier);
	}

	public int cactusMaxHeight;
	public int reedMaxHeight;

	private void blockGrowthHeight() {
		cactusMaxHeight = getInt("max-growth-height.cactus", 3);
		reedMaxHeight = getInt("max-growth-height.reeds", 3);
		log("Max height for cactus growth " + cactusMaxHeight + ". Max height for reed growth " + reedMaxHeight);
	}

	public boolean invertedDaylightDetectors;

	private void invertedDaylightDetectors() {
		invertedDaylightDetectors = getBoolean("inverted-daylight-detectors", false);
		log("Inverted Redstone Lamps: " + invertedDaylightDetectors);
	}

	public int fishingMinTicks;
	public int fishingMaxTicks;

	private void fishingTickRange() {
		fishingMinTicks = getInt("fishing-time-range.MinimumTicks", 100);
		fishingMaxTicks = getInt("fishing-time-range.MaximumTicks", 900);
	}

	public float blockBreakExhaustion;
	public float playerSwimmingExhaustion;

	private void exhaustionValues() {
		blockBreakExhaustion = getFloat("player-exhaustion.block-break", 0.025F);
		playerSwimmingExhaustion = getFloat("player-exhaustion.swimming", 0.015F);
	}

	public Integer softDespawnDistance;
	public Integer hardDespawnDistance;

	private void despawnDistances() {
		softDespawnDistance = getInt("despawn-ranges.soft", 32); // 32^2 = 1024, Minecraft Default
		hardDespawnDistance = getInt("despawn-ranges.hard", 128); // 128^2 = 16384, Minecraft Default;

		if (softDespawnDistance > hardDespawnDistance) {
			softDespawnDistance = hardDespawnDistance;
		}

		log("Living Entity Despawn Ranges:  Soft: " + softDespawnDistance + " Hard: " + hardDespawnDistance);

		softDespawnDistance = softDespawnDistance * softDespawnDistance;
		hardDespawnDistance = hardDespawnDistance * hardDespawnDistance;
	}

	public boolean keepSpawnInMemory;

	private void keepSpawnInMemory() {
		keepSpawnInMemory = getBoolean("keep-spawn-loaded", true);
		log("Keep spawn chunk loaded: " + keepSpawnInMemory);
	}

	public double fallingBlockHeightNerf;

	private void fallingBlockheightNerf() {
		// Technically a little disingenuous as it applies to all falling blocks but alas, backwards compat prevails!
		fallingBlockHeightNerf = getDouble("tnt-entity-height-nerf", 0);
		if (fallingBlockHeightNerf != 0) {
			log("TNT/Falling Block Height Limit set to Y: " + fallingBlockHeightNerf);
		}
	}

	public int waterOverLavaFlowSpeed;

	private void waterOverLavaFlowSpeed() {
		waterOverLavaFlowSpeed = getInt("water-over-lava-flow-speed", 5);
		log("Water over lava flow speed: " + waterOverLavaFlowSpeed);
	}

	public boolean removeInvalidMobSpawnerTEs;

	private void removeInvalidMobSpawnerTEs() {
		removeInvalidMobSpawnerTEs = getBoolean("remove-invalid-mob-spawner-tile-entities", true);
		log("Remove invalid mob spawner tile entities: " + removeInvalidMobSpawnerTEs);
	}

	public boolean removeUnloadedEnderPearls;
	public boolean removeUnloadedTNTEntities;
	public boolean removeUnloadedFallingBlocks;

	private void removeUnloaded() {
		removeUnloadedEnderPearls = getBoolean("remove-unloaded.enderpearls", true);
		removeUnloadedTNTEntities = getBoolean("remove-unloaded.tnt-entities", true);
		removeUnloadedFallingBlocks = getBoolean("remove-unloaded.falling-blocks", true);
	}
}
