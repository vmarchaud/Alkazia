package net.minecraft.server;

public class PacketPlayOutEntityHeadRotation extends Packet {

	private int a;
	private byte b;

	public PacketPlayOutEntityHeadRotation() {
	}

	public PacketPlayOutEntityHeadRotation(Entity entity, byte b0) {
		a = entity.getId();
		b = b0;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readByte();
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
		packetdataserializer.writeByte(b);
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d, rot=%d", new Object[] { Integer.valueOf(a), Byte.valueOf(b) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
