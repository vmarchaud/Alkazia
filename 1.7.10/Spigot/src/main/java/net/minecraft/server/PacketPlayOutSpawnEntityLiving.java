package net.minecraft.server;

import java.util.List;

public class PacketPlayOutSpawnEntityLiving extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private int f;
	private int g;
	private int h;
	private byte i;
	private byte j;
	private byte k;
	private DataWatcher l;
	private List m;

	public PacketPlayOutSpawnEntityLiving() {
	}

	public PacketPlayOutSpawnEntityLiving(EntityLiving entityliving) {
		a = entityliving.getId();
		b = (byte) EntityTypes.a(entityliving);
		c = entityliving.as.a(entityliving.locX);
		d = MathHelper.floor(entityliving.locY * 32.0D);
		e = entityliving.as.a(entityliving.locZ);
		i = (byte) (int) (entityliving.yaw * 256.0F / 360.0F);
		j = (byte) (int) (entityliving.pitch * 256.0F / 360.0F);
		k = (byte) (int) (entityliving.aO * 256.0F / 360.0F);
		double d0 = 3.9D;
		double d1 = entityliving.motX;
		double d2 = entityliving.motY;
		double d3 = entityliving.motZ;

		if (d1 < -d0) {
			d1 = -d0;
		}

		if (d2 < -d0) {
			d2 = -d0;
		}

		if (d3 < -d0) {
			d3 = -d0;
		}

		if (d1 > d0) {
			d1 = d0;
		}

		if (d2 > d0) {
			d2 = d0;
		}

		if (d3 > d0) {
			d3 = d0;
		}

		f = (int) (d1 * 8000.0D);
		g = (int) (d2 * 8000.0D);
		h = (int) (d3 * 8000.0D);
		l = entityliving.getDataWatcher();
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.a();
		b = packetdataserializer.readByte() & 255;
		c = packetdataserializer.readInt();
		d = packetdataserializer.readInt();
		e = packetdataserializer.readInt();
		i = packetdataserializer.readByte();
		j = packetdataserializer.readByte();
		k = packetdataserializer.readByte();
		f = packetdataserializer.readShort();
		g = packetdataserializer.readShort();
		h = packetdataserializer.readShort();
		m = DataWatcher.b(packetdataserializer);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.b(a);
		packetdataserializer.writeByte(b & 255);
		packetdataserializer.writeInt(c);
		packetdataserializer.writeInt(d);
		packetdataserializer.writeInt(e);
		packetdataserializer.writeByte(i);
		packetdataserializer.writeByte(j);
		packetdataserializer.writeByte(k);
		packetdataserializer.writeShort(f);
		packetdataserializer.writeShort(g);
		packetdataserializer.writeShort(h);
		l.a(packetdataserializer, packetdataserializer.version); // Spigot
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f, xd=%.2f, yd=%.2f, zd=%.2f", new Object[] { Integer.valueOf(a), Integer.valueOf(b), Float.valueOf(c / 32.0F), Float.valueOf(d / 32.0F), Float.valueOf(e / 32.0F), Float.valueOf(f / 8000.0F), Float.valueOf(g / 8000.0F), Float.valueOf(h / 8000.0F) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
