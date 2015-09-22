package org.bukkit.craftbukkit;

import java.io.IOException;
import java.util.Date;

import net.minecraft.server.GameProfileBanEntry;
import net.minecraft.server.GameProfileBanList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public final class CraftProfileBanEntry implements org.bukkit.BanEntry {
	private final GameProfileBanList list;
	private final GameProfile profile;
	private Date created;
	private String source;
	private Date expiration;
	private String reason;

	public CraftProfileBanEntry(GameProfile profile, GameProfileBanEntry entry, GameProfileBanList list) {
		this.list = list;
		this.profile = profile;
		created = entry.getCreated() != null ? new Date(entry.getCreated().getTime()) : null;
		source = entry.getSource();
		expiration = entry.getExpires() != null ? new Date(entry.getExpires().getTime()) : null;
		reason = entry.getReason();
	}

	@Override
	public String getTarget() {
		return profile.getName();
	}

	@Override
	public Date getCreated() {
		return created == null ? null : (Date) created.clone();
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public Date getExpiration() {
		return expiration == null ? null : (Date) expiration.clone();
	}

	@Override
	public void setExpiration(Date expiration) {
		if (expiration != null && expiration.getTime() == new Date(0, 0, 0, 0, 0, 0).getTime()) {
			expiration = null; // Forces "forever"
		}

		this.expiration = expiration;
	}

	@Override
	public String getReason() {
		return reason;
	}

	@Override
	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public void save() {
		GameProfileBanEntry entry = new GameProfileBanEntry(profile, created, source, expiration, reason);
		list.add(entry);
		try {
			list.save();
		} catch (IOException ex) {
			MinecraftServer.getLogger().error("Failed to save banned-players.json, " + ex.getMessage());
		}
	}
}
