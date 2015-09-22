package net.minecraft.server;

public class PacketPlayInPosition extends PacketPlayInFlying {

	public PacketPlayInPosition() {
		hasPos = true;
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
		super.a(packetdataserializer);
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeDouble(x);
		packetdataserializer.writeDouble(y);
		packetdataserializer.writeDouble(stance);
		packetdataserializer.writeDouble(z);
		super.b(packetdataserializer);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		super.a((PacketPlayInListener) packetlistener);
	}
}
