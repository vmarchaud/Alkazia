package net.minecraft.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.util.com.google.gson.JsonObject;

public abstract class ExpirableListEntry extends JsonListEntry {

	public static final SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date b;
	protected final String c;
	protected final Date d;
	protected final String e;

	public ExpirableListEntry(Object object, Date date, String s, Date date1, String s1) {
		super(object);
		b = date == null ? new Date() : date;
		c = s == null ? "(Unknown)" : s;
		d = date1;
		e = s1 == null ? "Banned by an operator." : s1;
	}

	protected ExpirableListEntry(Object object, JsonObject jsonobject) {
		super(checkExpiry(object, jsonobject), jsonobject); // CraftBukkit - check expiry

		Date date;

		try {
			date = jsonobject.has("created") ? a.parse(jsonobject.get("created").getAsString()) : new Date();
		} catch (ParseException parseexception) {
			date = new Date();
		}

		b = date;
		c = jsonobject.has("source") ? jsonobject.get("source").getAsString() : "(Unknown)";

		Date date1;

		try {
			date1 = jsonobject.has("expires") ? a.parse(jsonobject.get("expires").getAsString()) : null;
		} catch (ParseException parseexception1) {
			date1 = null;
		}

		d = date1;
		e = jsonobject.has("reason") ? jsonobject.get("reason").getAsString() : "Banned by an operator.";
	}

	public Date getExpires() {
		return d;
	}

	public String getReason() {
		return e;
	}

	@Override
	boolean hasExpired() {
		return d == null ? false : d.before(new Date());
	}

	@Override
	protected void a(JsonObject jsonobject) {
		jsonobject.addProperty("created", a.format(b));
		jsonobject.addProperty("source", c);
		jsonobject.addProperty("expires", d == null ? "forever" : a.format(d));
		jsonobject.addProperty("reason", e);
	}

	// CraftBukkit start
	public String getSource() {
		return c;
	}

	public Date getCreated() {
		return b;
	}

	private static Object checkExpiry(Object object, JsonObject jsonobject) {
		Date expires = null;

		try {
			expires = jsonobject.has("expires") ? a.parse(jsonobject.get("expires").getAsString()) : null;
		} catch (ParseException ex) {
			// Guess we don't have a date
		}

		if (expires == null || expires.after(new Date()))
			return object;
		else
			return null;
	}
	// CraftBukkit end
}
