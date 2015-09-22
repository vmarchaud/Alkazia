package org.clipspigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Throwables;

public class ClipSpigotConfig {

	private static final File CONFIG_FILE = new File("clip.yml");
	private static final String HEADER = "This is the main configuration file for ClipSpigot.\n" 
	+ "As you can see, there's tons to configure. Some options may impact gameplay, so use\n" 
			+ "with caution, and make sure you know what each option does before configuring.\n\n";
	/*========================================================================*/
	static YamlConfiguration config;
	static int version;
	static Map<String, Command> commands;

	/*========================================================================*/

	public static void init() {
		config = new YamlConfiguration();
		try {
			config.load(CONFIG_FILE);
		} catch (IOException ex) {
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load clip.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().header(HEADER);
		config.options().copyDefaults(true);

		commands = new HashMap<String, Command>();

		version = getInt("config-version", 6);
		set("config-version", 6);
		readConfig(ClipSpigotConfig.class, null);
	}

	public static void registerCommands() {
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "ClipSpigot", entry.getValue());
		}
	}

	static void readConfig(Class<?> clazz, Object instance) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isPrivate(method.getModifiers())) {
				if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
					try {
						method.setAccessible(true);
						method.invoke(instance);
					} catch (InvocationTargetException ex) {
						throw Throwables.propagate(ex.getCause());
					} catch (Exception ex) {
						Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
					}
				}
			}
		}

		try {
			config.save(CONFIG_FILE);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
		}
	}

	private static String transform(String s) {
		return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\n", "\n");
	}
	
	public static String only18ClientAllowedMessage = "Please use Minecraft 1.8 to join this server!";
	
	private static void messages() {
		only18ClientAllowedMessage = transform(getString("messages.outdated-client", only18ClientAllowedMessage));
	}
	
	private static void set(String path, Object val) {
		config.set(path, val);
	}

	private static boolean getBoolean(String path, boolean def) {
		config.addDefault(path, def);
		return config.getBoolean(path, config.getBoolean(path));
	}

	private static double getDouble(String path, double def) {
		config.addDefault(path, def);
		return config.getDouble(path, config.getDouble(path));
	}

	private static float getFloat(String path, float def) {
		config.addDefault(path, def);
		return (float)config.getInt(path, config.getInt(path));
	}

	private static int getInt(String path, int def) {
		config.addDefault(path, def);
		return config.getInt(path, config.getInt(path));
	}

	@SuppressWarnings("rawtypes")
	private static <T> List getList(String path, T def) {
		config.addDefault(path, def);
		return config.getList(path, config.getList(path));
	}

	private static String getString(String path, String def) {
		config.addDefault(path, def);
		return config.getString(path, config.getString(path));
	}

	public static double babyZombieMovementSpeed;

	private static void babyZombieMovementSpeed() {
		babyZombieMovementSpeed = getDouble("settings.baby-zombie-movement-speed", 0.5D); // Player moves at 0.1F, for reference
	}

	public static boolean asyncCatcherFeature;

	private static void asyncCatcherFeature() {
		asyncCatcherFeature = getBoolean("settings.async-plugin-bad-magic-catcher", true);
		if (!asyncCatcherFeature) {
			Bukkit.getLogger().log(Level.INFO, "Disabling async plugin bad ju-ju catcher, this may be bad depending on your plugins");
		}
	}

	public static boolean interactLimitEnabled;

	private static void interactLimitEnabled() {
		interactLimitEnabled = getBoolean("settings.limit-player-interactions", true);
		if (!interactLimitEnabled) {
			Bukkit.getLogger().log(Level.INFO, "Disabling player interaction limiter, your server may be more vulnerable to malicious users");
		}
	}

	public static double strengthEffectModifier;
	public static double weaknessEffectModifier;

	private static void effectModifiers() {
		strengthEffectModifier = getDouble("effect-modifiers.strength", 1.3D);
		weaknessEffectModifier = getDouble("effect-modifiers.weakness", -0.5D);
	}
	
	public static boolean allow18clientsOnly;

	private static void onlyAllow18ClientsJoin() {
		allow18clientsOnly = getBoolean("settings.allow-only-1-8-clients", false);
		if (!allow18clientsOnly)
			Bukkit.getLogger().log(Level.INFO, "1.7x clients are allowed to join, new blocks/features will not work for them!");
	}
}
