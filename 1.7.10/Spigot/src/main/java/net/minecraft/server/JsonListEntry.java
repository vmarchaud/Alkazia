package net.minecraft.server;

import net.minecraft.util.com.google.gson.JsonObject;

public class JsonListEntry {

	private final Object a;

	public JsonListEntry(Object object) {
		a = object;
	}

	protected JsonListEntry(Object object, JsonObject jsonobject) {
		a = object;
	}

	public Object getKey() { // CraftBukkit -> package private -> public
		return a;
	}

	boolean hasExpired() {
		return false;
	}

	protected void a(JsonObject jsonobject) {
	}
}
