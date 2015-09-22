package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import joptsimple.OptionSet; // CraftBukkit

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {

	private static final Logger loggingAgent = LogManager.getLogger();
	public final Properties properties = new Properties(); // CraftBukkit - private -> public
	private final File c;

	public PropertyManager(File file1) {
		c = file1;
		if (file1.exists()) {
			FileInputStream fileinputstream = null;

			try {
				fileinputstream = new FileInputStream(file1);
				properties.load(fileinputstream);
			} catch (Exception exception) {
				loggingAgent.warn("Failed to load " + file1, exception);
				a();
			} finally {
				if (fileinputstream != null) {
					try {
						fileinputstream.close();
					} catch (IOException ioexception) {
						;
					}
				}
			}
		} else {
			loggingAgent.warn(file1 + " does not exist");
			a();
		}
	}

	// CraftBukkit start
	private OptionSet options = null;

	public PropertyManager(final OptionSet options) {
		this((File) options.valueOf("config"));

		this.options = options;
	}

	private <T> T getOverride(String name, T value) {
		if (options != null && options.has(name) && !name.equals("online-mode"))
			return (T) options.valueOf(name);

		return value;
	}

	// CraftBukkit end

	public void a() {
		loggingAgent.info("Generating new properties file");
		savePropertiesFile();
	}

	public void savePropertiesFile() {
		FileOutputStream fileoutputstream = null;

		try {
			// CraftBukkit start - Don't attempt writing to file if it's read only
			if (c.exists() && !c.canWrite())
				return;
			// CraftBukkit end
			fileoutputstream = new FileOutputStream(c);
			properties.store(fileoutputstream, "Minecraft server properties");
		} catch (Exception exception) {
			loggingAgent.warn("Failed to save " + c, exception);
			a();
		} finally {
			if (fileoutputstream != null) {
				try {
					fileoutputstream.close();
				} catch (IOException ioexception) {
					;
				}
			}
		}
	}

	public File c() {
		return c;
	}

	public String getString(String s, String s1) {
		if (!properties.containsKey(s)) {
			properties.setProperty(s, s1);
			savePropertiesFile();
			savePropertiesFile();
		}

		return this.getOverride(s, properties.getProperty(s, s1)); // CraftBukkit
	}

	public int getInt(String s, int i) {
		try {
			return this.getOverride(s, Integer.parseInt(getString(s, "" + i))); // CraftBukkit
		} catch (Exception exception) {
			properties.setProperty(s, "" + i);
			savePropertiesFile();
			return this.getOverride(s, i); // CraftBukkit
		}
	}

	public boolean getBoolean(String s, boolean flag) {
		try {
			return this.getOverride(s, Boolean.parseBoolean(getString(s, "" + flag))); // CraftBukkit
		} catch (Exception exception) {
			properties.setProperty(s, "" + flag);
			savePropertiesFile();
			return this.getOverride(s, flag); // CraftBukkit
		}
	}

	public void setProperty(String s, Object object) {
		properties.setProperty(s, "" + object);
	}
}
