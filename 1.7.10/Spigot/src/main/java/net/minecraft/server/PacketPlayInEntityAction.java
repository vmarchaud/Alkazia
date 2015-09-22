package net.minecraft.server;

public class PacketPlayInEntityAction extends Packet {

	private int a;
	private int animation;
	private int c;

	public PacketPlayInEntityAction() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			animation = packetdataserializer.readByte();
			c = packetdataserializer.readInt();
		} else {
			a = packetdataserializer.a();
			animation = packetdataserializer.readUnsignedByte() + 1;
			c = packetdataserializer.a();
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeByte(animation);
		packetdataserializer.writeInt(c);
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public int d() {
		return animation;
	}

	public int e() {
		return c;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
