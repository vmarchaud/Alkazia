package net.minecraft.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import net.minecraft.util.com.google.common.io.Files;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.org.apache.commons.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonList {

	protected static final Logger a = LogManager.getLogger();
	protected final Gson b;
	private final File c;
	private final Map d = Maps.newHashMap();
	private boolean e = true;
	private static final ParameterizedType f = new JsonListType();

	public JsonList(File file1) {
		c = file1;
		GsonBuilder gsonbuilder = new GsonBuilder().setPrettyPrinting();

		gsonbuilder.registerTypeHierarchyAdapter(JsonListEntry.class, new JsonListEntrySerializer(this, (JsonListType) null));
		b = gsonbuilder.create();
	}

	public boolean isEnabled() {
		return e;
	}

	public void a(boolean flag) {
		e = flag;
	}

	public File c() {
		return c;
	}

	public void add(JsonListEntry jsonlistentry) {
		d.put(this.a(jsonlistentry.getKey()), jsonlistentry);

		try {
			save();
		} catch (IOException ioexception) {
			a.warn("Could not save the list after adding a user.", ioexception);
		}
	}

	public JsonListEntry get(Object object) {
		h();
		return (JsonListEntry) d.get(this.a(object));
	}

	public void remove(Object object) {
		d.remove(this.a(object));

		try {
			save();
		} catch (IOException ioexception) {
			a.warn("Could not save the list after removing a user.", ioexception);
		}
	}

	public String[] getEntries() {
		return (String[]) d.keySet().toArray(new String[d.size()]);
	}

	// CraftBukkit start
	public Collection<JsonListEntry> getValues() {
		return d.values();
	}

	// CraftBukkit end

	public boolean isEmpty() {
		return d.size() < 1;
	}

	protected String a(Object object) {
		return object.toString();
	}

	protected boolean d(Object object) {
		return d.containsKey(this.a(object));
	}

	private void h() {
		ArrayList arraylist = Lists.newArrayList();
		Iterator iterator = d.values().iterator();

		while (iterator.hasNext()) {
			JsonListEntry jsonlistentry = (JsonListEntry) iterator.next();

			if (jsonlistentry.hasExpired()) {
				arraylist.add(jsonlistentry.getKey());
			}
		}

		iterator = arraylist.iterator();

		while (iterator.hasNext()) {
			Object object = iterator.next();

			d.remove(object);
		}
	}

	protected JsonListEntry a(JsonObject jsonobject) {
		return new JsonListEntry(null, jsonobject);
	}

	protected Map e() {
		return d;
	}

	public void save() throws IOException { // CraftBukkit - Added throws
		Collection collection = d.values();
		String s = b.toJson(collection);
		BufferedWriter bufferedwriter = null;

		try {
			bufferedwriter = Files.newWriter(c, Charsets.UTF_8);
			bufferedwriter.write(s);
		} finally {
			IOUtils.closeQuietly(bufferedwriter);
		}
	}

	public void load() throws IOException { // CraftBukkit - Added throws
		Collection collection = null;
		BufferedReader bufferedreader = null;

		try {
			bufferedreader = Files.newReader(c, Charsets.UTF_8);
			collection = (Collection) b.fromJson(bufferedreader, f);
			// Spigot Start
		} catch (java.io.FileNotFoundException ex) {
			org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.INFO, "Unable to find file {0}, creating it.", c);
		} catch (net.minecraft.util.com.google.gson.JsonSyntaxException ex) {
			org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "Unable to read file {0}, backing it up to {0}.backup and creating new copy.", c);
			File backup = new File(c + ".backup");
			c.renameTo(backup);
			c.delete();
			// Spigot End
		} finally {
			IOUtils.closeQuietly(bufferedreader);
		}

		if (collection != null) {
			d.clear();
			Iterator iterator = collection.iterator();

			while (iterator.hasNext()) {
				JsonListEntry jsonlistentry = (JsonListEntry) iterator.next();

				if (jsonlistentry.getKey() != null) {
					d.put(this.a(jsonlistentry.getKey()), jsonlistentry);
				}
			}
		}
	}
}
