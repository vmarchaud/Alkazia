package net.minecraft.server;

public class PacketPlayOutEntityVelocity extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;

	public PacketPlayOutEntityVelocity() {
	}

	public PacketPlayOutEntityVelocity(Entity entity) {
		this(entity.getId(), entity.motX, entity.motY, entity.motZ);
	}

	public PacketPlayOutEntityVelocity(int i, double d0, double d1, double d2) {
		a = i;
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

		b = (int) (d0 * 8000.0D);
		c = (int) (d1 * 8000.0D);
		d = (int) (d2 * 8000.0D);
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readShort();
		c = packetdataserializer.readShort();
		d = packetdataserializer.readShort();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
		} else {
			packetdataserializer.b(a);
		}
		// Spigot end
		packetdataserializer.writeShort(b);
		packetdataserializer.writeShort(c);
		packetdataserializer.writeShort(d);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, x=%.2f, y=%.2f, z=%.2f", new Object[] { Integer.valueOf(a), Float.valueOf(b / 8000.0F), Float.valueOf(c / 8000.0F), Float.valueOf(d / 8000.0F) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
