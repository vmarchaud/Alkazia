package org.bukkit.craftbukkit;

import java.io.IOException;
import java.util.Date;

import net.minecraft.server.IpBanEntry;
import net.minecraft.server.IpBanList;
import net.minecraft.server.MinecraftServer;

public final class CraftIpBanEntry implements org.bukkit.BanEntry {
	private final IpBanList list;
	private final String target;
	private Date created;
	private String source;
	private Date expiration;
	private String reason;

	public CraftIpBanEntry(String target, IpBanEntry entry, IpBanList list) {
		this.list = list;
		this.target = target;
		created = entry.getCreated() != null ? new Date(entry.getCreated().getTime()) : null;
		source = entry.getSource();
		expiration = entry.getExpires() != null ? new Date(entry.getExpires().getTime()) : null;
		reason = entry.getReason();
	}

	@Override
	public String getTarget() {
		return target;
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
		IpBanEntry entry = new IpBanEntry(target, created, source, expiration, reason);
		list.add(entry);
		try {
			list.save();
		} catch (IOException ex) {
			MinecraftServer.getLogger().error("Failed to save banned-ips.json, " + ex.getMessage());
		}
	}
}
