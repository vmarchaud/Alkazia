package net.minecraft.server;

public class PacketPlayOutRelEntityMove extends PacketPlayOutEntity {

	private boolean onGround; // Spigot - protocol patch

	public PacketPlayOutRelEntityMove() {
	}

	public PacketPlayOutRelEntityMove(int i, byte b0, byte b1, byte b2, boolean onGround) { // Spigot - protocol patch
		super(i);
		b = b0;
		c = b1;
		d = b2;
		this.onGround = onGround; // Spigot - protocol patch
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		super.a(packetdataserializer);
		b = packetdataserializer.readByte();
		c = packetdataserializer.readByte();
		d = packetdataserializer.readByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		super.b(packetdataserializer);
		packetdataserializer.writeByte(b);
		packetdataserializer.writeByte(c);
		packetdataserializer.writeByte(d);
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 22) {
			packetdataserializer.writeBoolean(onGround);
		}
		// Spigot end
	}

	@Override
	public String b() {
		return super.b() + String.format(", xa=%d, ya=%d, za=%d", new Object[] { Byte.valueOf(b), Byte.valueOf(c), Byte.valueOf(d) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		super.a((PacketPlayOutListener) packetlistener);
	}
}
