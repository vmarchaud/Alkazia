package net.minecraft.server;

public class PacketPlayOutPosition extends Packet {

	private double a;
	private double b;
	private double c;
	private float d;
	private float e;
	private boolean f;

	public PacketPlayOutPosition() {
	}

	public PacketPlayOutPosition(double d0, double d1, double d2, float f, float f1, boolean flag) {
		a = d0;
		b = d1;
		c = d2;
		d = f;
		e = f1;
		this.f = flag;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readDouble();
		b = packetdataserializer.readDouble();
		c = packetdataserializer.readDouble();
		d = packetdataserializer.readFloat();
		e = packetdataserializer.readFloat();
		f = packetdataserializer.readBoolean();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		packetdataserializer.writeDouble(a);
		packetdataserializer.writeDouble(b - (packetdataserializer.version >= 16 ? 1.62 : 0));
		packetdataserializer.writeDouble(c);
		packetdataserializer.writeFloat(d);
		packetdataserializer.writeFloat(e);
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeBoolean(f);
		} else {
			packetdataserializer.writeByte(0);
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
