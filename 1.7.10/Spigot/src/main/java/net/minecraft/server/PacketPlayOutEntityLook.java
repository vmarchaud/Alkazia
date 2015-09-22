package net.minecraft.server;

public class PacketPlayOutEntityLook extends PacketPlayOutEntity {

	private boolean onGround; // Spigot - protocol patch

	public PacketPlayOutEntityLook() {
		g = true;
	}

	public PacketPlayOutEntityLook(int i, byte b0, byte b1, boolean onGround) { // Spigot - protocol patch
		super(i);
		e = b0;
		f = b1;
		g = true;
		this.onGround = onGround; // Spigot - protocol patch
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		super.a(packetdataserializer);
		e = packetdataserializer.readByte();
		f = packetdataserializer.readByte();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		super.b(packetdataserializer);
		packetdataserializer.writeByte(e);
		packetdataserializer.writeByte(f);
		// Spigot start - protocol patch
		if (packetdataserializer.version >= 22) {
			packetdataserializer.writeBoolean(onGround);
		}
		// Spigot end
	}

	@Override
	public String b() {
		return super.b() + String.format(", yRot=%d, xRot=%d", new Object[] { Byte.valueOf(e), Byte.valueOf(f) });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		super.a((PacketPlayOutListener) packetlistener);
	}
}
