package net.minecraft.server;

public class PacketPlayInBlockPlace extends Packet {

	private int a;
	private int b;
	private int c;
	private int d;
	private ItemStack e;
	private float f;
	private float g;
	private float h;

	public PacketPlayInBlockPlace() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			b = packetdataserializer.readUnsignedByte();
			c = packetdataserializer.readInt();
		} else {
			long position = packetdataserializer.readLong();
			a = packetdataserializer.readPositionX(position);
			b = packetdataserializer.readPositionY(position);
			c = packetdataserializer.readPositionZ(position);
		}
		// Spigot end
		d = packetdataserializer.readUnsignedByte();
		e = packetdataserializer.c();
		f = packetdataserializer.readUnsignedByte() / 16.0F;
		g = packetdataserializer.readUnsignedByte() / 16.0F;
		h = packetdataserializer.readUnsignedByte() / 16.0F;
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeByte(b);
		packetdataserializer.writeInt(c);
		packetdataserializer.writeByte(d);
		packetdataserializer.a(e);
		packetdataserializer.writeByte((int) (f * 16.0F));
		packetdataserializer.writeByte((int) (g * 16.0F));
		packetdataserializer.writeByte((int) (h * 16.0F));
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public int c() {
		return a;
	}

	public int d() {
		return b;
	}

	public int e() {
		return c;
	}

	public int getFace() {
		return d;
	}

	public ItemStack getItemStack() {
		return e;
	}

	public float h() {
		return f;
	}

	public float i() {
		return g;
	}

	public float j() {
		return h;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
