package net.minecraft.server;

import java.io.IOException; // CraftBukkit
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

public class PacketPlayOutNamedEntitySpawn extends Packet {

	private int a;
	private GameProfile b;
	private int c;
	private int d;
	private int e;
	private byte f;
	private byte g;
	private int h;
	private DataWatcher i;
	private List j;

	public PacketPlayOutNamedEntitySpawn() {
	}

	public PacketPlayOutNamedEntitySpawn(EntityHuman entityhuman) {
		a = entityhuman.getId();
		b = entityhuman.getProfile();
		c = MathHelper.floor(entityhuman.locX * 32.0D);
		d = MathHelper.floor(entityhuman.locY * 32.0D);
		e = MathHelper.floor(entityhuman.locZ * 32.0D);
		f = (byte) (int) (entityhuman.yaw * 256.0F / 360.0F);
		g = (byte) (int) (entityhuman.pitch * 256.0F / 360.0F);
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		h = itemstack == null ? 0 : Item.getId(itemstack.getItem());
		i = entityhuman.getDataWatcher();
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		a = packetdataserializer.a();
		UUID uuid = UUID.fromString(packetdataserializer.c(36));

		b = new GameProfile(uuid, packetdataserializer.c(16));
		int i = packetdataserializer.a();

		for (int j = 0; j < i; ++j) {
			String s = packetdataserializer.c(32767);
			String s1 = packetdataserializer.c(32767);
			String s2 = packetdataserializer.c(32767);

			b.getProperties().put(s, new Property(s, s1, s2));
		}

		c = packetdataserializer.readInt();
		d = packetdataserializer.readInt();
		e = packetdataserializer.readInt();
		f = packetdataserializer.readByte();
		g = packetdataserializer.readByte();
		h = packetdataserializer.readShort();
		j = DataWatcher.b(packetdataserializer);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException { // CraftBukkit - added throws
		packetdataserializer.b(a);

		UUID uuid = b.getId();
		// Spigot start - protocol patch
		if (packetdataserializer.version < 20) {
			packetdataserializer.a(uuid == null ? "" : packetdataserializer.version >= 5 ? uuid.toString() : uuid.toString().replaceAll("-", "")); // Spigot
			packetdataserializer.a(b.getName().length() > 16 ? b.getName().substring(0, 16) : b.getName()); // CraftBukkit - Limit name length to 16 characters
			if (packetdataserializer.version >= 5) { // Spigot
				packetdataserializer.b(b.getProperties().size());
				Iterator iterator = b.getProperties().values().iterator();

				while (iterator.hasNext()) {
					Property property = (Property) iterator.next();

					packetdataserializer.a(property.getName());
					packetdataserializer.a(property.getValue());
					packetdataserializer.a(property.getSignature());
				}
			}
		} else {
			packetdataserializer.writeUUID(uuid);
		}
		// Spigot end

		packetdataserializer.writeInt(c);
		packetdataserializer.writeInt(d);
		packetdataserializer.writeInt(e);
		packetdataserializer.writeByte(f);
		packetdataserializer.writeByte(g);
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 47) {
			packetdataserializer.writeShort(org.spigotmc.SpigotDebreakifier.getItemId(h));
		} else {
			packetdataserializer.writeShort(h);
		}
		i.a(packetdataserializer);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, gameProfile=\'%s\', x=%.2f, y=%.2f, z=%.2f, carried=%d", new Object[] { Integer.valueOf(a), b, Float.valueOf(c / 32.0F), Float.valueOf(d / 32.0F), Float.valueOf(e / 32.0F), Integer.valueOf(h) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
