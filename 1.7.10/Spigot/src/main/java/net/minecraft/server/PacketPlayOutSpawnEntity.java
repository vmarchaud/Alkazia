package net.minecraft.server;

public class PacketPlayOutSpawnEntity extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private int f;
	private int g;
	private int h;
	private int i;
	private int j;
	private int k;

	public PacketPlayOutSpawnEntity() {
	}

	public PacketPlayOutSpawnEntity(Entity entity, int i) {
		this(entity, i, 0);
	}

	public PacketPlayOutSpawnEntity(Entity entity, int i, int j) {
		a = entity.getId();
		b = MathHelper.floor(entity.locX * 32.0D);
		c = MathHelper.floor(entity.locY * 32.0D);
		d = MathHelper.floor(entity.locZ * 32.0D);
		h = MathHelper.d(entity.pitch * 256.0F / 360.0F);
		this.i = MathHelper.d(entity.yaw * 256.0F / 360.0F);
		this.j = i;
		k = j;
		if (j > 0) {
			double d0 = entity.motX;
			double d1 = entity.motY;
			double d2 = entity.motZ;
			double d3 = 3.9D;

			if (d0 < -d3) {
				d0 = -d3;
			}

			if (d1 < -d3) {
				d1 = -d3;
			}

			if (d2 < -d3) {
				d2 = -d3;
			}

			if (d0 > d3) {
				d0 = d3;
			}

			if (d1 > d3) {
				d1 = d3;
			}

			if (d2 > d3) {
				d2 = d3;
			}

			e = (int) (d0 * 8000.0D);
			f = (int) (d1 * 8000.0D);
			g = (int) (d2 * 8000.0D);
		}
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.a();
		j = packetdataserializer.readByte();
		b = packetdataserializer.readInt();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readInt();
		h = packetdataserializer.readByte();
		i = packetdataserializer.readByte();
		k = packetdataserializer.readInt();
		if (k > 0) {
			e = packetdataserializer.readShort();
			f = packetdataserializer.readShort();
			g = packetdataserializer.readShort();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.b(a);
		packetdataserializer.writeByte(j);
		// Spigot start - protocol patch
		if (j == 71 && packetdataserializer.version >= 28) {
			// North: 0   256
			// West:  64  192
			// South: 128 128
			// East:  192 320
			switch (k) {
			case 0:
				d += 32;
				i = 0;
				break;
			case 1:
				b -= 32;
				i = 64;
				break;
			case 2:
				d -= 32;
				i = 128;
				break;
			case 3:
				b += 32;
				i = 192;
				break;
			}
		}
		if (j == 70 && packetdataserializer.version >= 36) {
			int id = k & 0xFFFF;
			int data = k >> 16;
			k = id | data << 12;
		}
		
		if ((j == 50 || j == 70 || j == 74) && packetdataserializer.version >= 16) // Spigot Update - 20140916
			 c -= 16;                                                              // Spigot Update - 20140916

		// Spigot end
		packetdataserializer.writeInt(b);
		packetdataserializer.writeInt(c);
		packetdataserializer.writeInt(d);
		packetdataserializer.writeByte(h);
		packetdataserializer.writeByte(i);
		packetdataserializer.writeInt(k);
		if (k > 0) {
			packetdataserializer.writeShort(e);
			packetdataserializer.writeShort(f);
			packetdataserializer.writeShort(g);
		}
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f", new Object[] { Integer.valueOf(a), Integer.valueOf(j), Float.valueOf(b / 32.0F), Float.valueOf(c / 32.0F), Float.valueOf(d / 32.0F) });
	}

	public void a(int i) {
		b = i;
	}

	public void b(int i) {
		c = i;
	}

	public void c(int i) {
		d = i;
	}

	public void d(int i) {
		e = i;
	}

	public void e(int i) {
		f = i;
	}

	public void f(int i) {
		g = i;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
