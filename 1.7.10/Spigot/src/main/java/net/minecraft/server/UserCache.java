package net.minecraft.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.collect.Iterators;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import net.minecraft.util.com.google.common.io.Files;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import net.minecraft.util.com.mojang.authlib.Agent;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.org.apache.commons.io.IOUtils;

public class UserCache {

	public static final SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final Map c = Maps.newHashMap();
	private final Map d = Maps.newHashMap();
	private final LinkedList e = Lists.newLinkedList();
	private final MinecraftServer f;
	protected final Gson b;
	private final File g;
	private static final ParameterizedType h = new UserCacheEntryType();

	public UserCache(MinecraftServer minecraftserver, File file1) {
		f = minecraftserver;
		g = file1;
		GsonBuilder gsonbuilder = new GsonBuilder();

		gsonbuilder.registerTypeHierarchyAdapter(UserCacheEntry.class, new BanEntrySerializer(this, (GameProfileLookup) null));
		b = gsonbuilder.create();
		this.b();
	}

	private static GameProfile a(MinecraftServer minecraftserver, String s) {
		GameProfile[] agameprofile = new GameProfile[1];
		GameProfileLookup gameprofilelookup = new GameProfileLookup(agameprofile);

		minecraftserver.getGameProfileRepository().findProfilesByNames(new String[] { s }, Agent.MINECRAFT, gameprofilelookup);
		if (!minecraftserver.getOnlineMode() && agameprofile[0] == null) {
			UUID uuid = EntityHuman.a(new GameProfile((UUID) null, s));
			GameProfile gameprofile = new GameProfile(uuid, s);

			gameprofilelookup.onProfileLookupSucceeded(gameprofile);
		}

		return agameprofile[0];
	}

	public void a(GameProfile gameprofile) {
		this.a(gameprofile, (Date) null);
	}

	private void a(GameProfile gameprofile, Date date) {
		UUID uuid = gameprofile.getId();

		if (date == null) {
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(new Date());
			calendar.add(2, 1);
			date = calendar.getTime();
		}

		String s = gameprofile.getName().toLowerCase(Locale.ROOT);
		UserCacheEntry usercacheentry = new UserCacheEntry(this, gameprofile, date, (GameProfileLookup) null);
		LinkedList linkedlist = e;

		synchronized (e) {
			if (d.containsKey(uuid)) {
				UserCacheEntry usercacheentry1 = (UserCacheEntry) d.get(uuid);

				c.remove(usercacheentry1.a().getName().toLowerCase(Locale.ROOT));
				c.put(gameprofile.getName().toLowerCase(Locale.ROOT), usercacheentry);
				e.remove(gameprofile);
			} else {
				d.put(uuid, usercacheentry);
				c.put(s, usercacheentry);
			}

			e.addFirst(gameprofile);
		}
	}

	public GameProfile getProfile(String s) {
		String s1 = s.toLowerCase(Locale.ROOT);
		UserCacheEntry usercacheentry = (UserCacheEntry) c.get(s1);

		if (usercacheentry != null && new Date().getTime() >= UserCacheEntry.a(usercacheentry).getTime()) {
			d.remove(usercacheentry.a().getId());
			c.remove(usercacheentry.a().getName().toLowerCase(Locale.ROOT));
			LinkedList linkedlist = e;

			synchronized (e) {
				e.remove(usercacheentry.a());
			}

			usercacheentry = null;
		}

		GameProfile gameprofile;

		if (usercacheentry != null) {
			gameprofile = usercacheentry.a();
			LinkedList linkedlist1 = e;

			synchronized (e) {
				e.remove(gameprofile);
				e.addFirst(gameprofile);
			}
		} else {
			gameprofile = a(f, s); // Spigot - use correct case for offline players
			if (gameprofile != null) {
				this.a(gameprofile);
				usercacheentry = (UserCacheEntry) c.get(s1);
			}
		}

		if (!org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly) {
			c(); // Spigot - skip saving if disabled
		}
		return usercacheentry == null ? null : usercacheentry.a();
	}

	public String[] a() {
		ArrayList arraylist = Lists.newArrayList(c.keySet());

		return (String[]) arraylist.toArray(new String[arraylist.size()]);
	}

	public GameProfile a(UUID uuid) {
		UserCacheEntry usercacheentry = (UserCacheEntry) d.get(uuid);

		return usercacheentry == null ? null : usercacheentry.a();
	}

	private UserCacheEntry b(UUID uuid) {
		UserCacheEntry usercacheentry = (UserCacheEntry) d.get(uuid);

		if (usercacheentry != null) {
			GameProfile gameprofile = usercacheentry.a();
			LinkedList linkedlist = e;

			synchronized (e) {
				e.remove(gameprofile);
				e.addFirst(gameprofile);
			}
		}

		return usercacheentry;
	}

	public void b() {
		List list = null;
		BufferedReader bufferedreader = null;

		label81: {
			try {
				bufferedreader = Files.newReader(g, Charsets.UTF_8);
				list = (List) b.fromJson(bufferedreader, h);
				break label81;
			} catch (FileNotFoundException filenotfoundexception) {
				;
				// Spigot Start
			} catch (net.minecraft.util.com.google.gson.JsonSyntaxException ex) {
				JsonList.a.warn("Usercache.json is corrupted or has bad formatting. Deleting it to prevent further issues.");
				g.delete();
				// Spigot End
			} finally {
				IOUtils.closeQuietly(bufferedreader);
			}

			return;
		}

		if (list != null) {
			c.clear();
			d.clear();
			LinkedList linkedlist = e;

			synchronized (e) {
				e.clear();
			}

			list = Lists.reverse(list);
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				UserCacheEntry usercacheentry = (UserCacheEntry) iterator.next();

				if (usercacheentry != null) {
					this.a(usercacheentry.a(), usercacheentry.b());
				}
			}
		}
	}

	public void c() {
		String s = b.toJson(this.a(org.spigotmc.SpigotConfig.userCacheCap));
		BufferedWriter bufferedwriter = null;

		try {
			bufferedwriter = Files.newWriter(g, Charsets.UTF_8);
			bufferedwriter.write(s);
			return;
		} catch (FileNotFoundException filenotfoundexception) {
			return;
		} catch (IOException ioexception) {
			;
		} finally {
			IOUtils.closeQuietly(bufferedwriter);
		}
	}

	private List a(int i) {
		ArrayList arraylist = Lists.newArrayList();
		LinkedList linkedlist = e;
		ArrayList arraylist1;

		synchronized (e) {
			arraylist1 = Lists.newArrayList(Iterators.limit(e.iterator(), i));
		}

		Iterator iterator = arraylist1.iterator();

		while (iterator.hasNext()) {
			GameProfile gameprofile = (GameProfile) iterator.next();
			UserCacheEntry usercacheentry = this.b(gameprofile.getId());

			if (usercacheentry != null) {
				arraylist.add(usercacheentry);
			}
		}

		return arraylist;
	}
}
