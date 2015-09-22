package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutSpawnEntityPainting extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private String f;

	public PacketPlayOutSpawnEntityPainting() {
	}

	public PacketPlayOutSpawnEntityPainting(EntityPainting entitypainting) {
		a = entitypainting.getId();
		b = entitypainting.x;
		c = entitypainting.y;
		d = entitypainting.z;
		e = entitypainting.direction;
		f = entitypainting.art.B;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.a();
		f = packetdataserializer.c(EnumArt.A);
		b = packetdataserializer.readInt();
		c = packetdataserializer.readInt();
		d = packetdataserializer.readInt();
		e = packetdataserializer.readInt();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		packetdataserializer.b(a);
		packetdataserializer.a(f);
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 28) {
			// North: 0   256
			// West:  64  192
			// South: 128 128
			// East:  192 320
			switch (e) {
			case 0:
				d += 1;
				break;
			case 1:
				b -= 1;
				break;
			case 2:
				d -= 1;
				break;
			case 3:
				b += 1;
				break;
			}
		}
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(b);
			packetdataserializer.writeInt(c);
			packetdataserializer.writeInt(d);
			packetdataserializer.writeInt(e);
		} else {
			packetdataserializer.writePosition(b, c, d);
			packetdataserializer.writeByte(e);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, type=%s, x=%d, y=%d, z=%d", new Object[] { Integer.valueOf(a), f, Integer.valueOf(b), Integer.valueOf(c), Integer.valueOf(d) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
