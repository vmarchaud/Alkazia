package net.minecraft.server;

public class PacketPlayInPositionLook extends PacketPlayInFlying {

	public PacketPlayInPositionLook() {
		hasPos = true;
		hasLook = true;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		x = packetdataserializer.readDouble();
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			y = packetdataserializer.readDouble();
			stance = packetdataserializer.readDouble();
		} else {
			y = packetdataserializer.readDouble();
			stance = y + 1.62;
		}
		// Spigot end
		z = packetdataserializer.readDouble();
		yaw = packetdataserializer.readFloat();
		pitch = packetdataserializer.readFloat();
		super.a(packetdataserializer);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeDouble(x);
		packetdataserializer.writeDouble(y);
		packetdataserializer.writeDouble(stance);
		packetdataserializer.writeDouble(z);
		packetdataserializer.writeFloat(yaw);
		packetdataserializer.writeFloat(pitch);
		super.b(packetdataserializer);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		super.a((PacketPlayInListener) packetlistener);
	}
}
