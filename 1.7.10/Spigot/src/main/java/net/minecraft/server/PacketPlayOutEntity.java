package net.minecraft.server;

public class PacketPlayOutEntity extends Packet {

	protected int a;
	protected byte b;
	protected byte c;
	protected byte d;
	protected byte e;
	protected byte f;
	protected boolean g;

	public PacketPlayOutEntity() {
	}

	public PacketPlayOutEntity(int i) {
		a = i;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
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
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		return String.format("id=%d", new Object[] { Integer.valueOf(a) });
	}

	@Override
	public String toString() {
		return "Entity_" + super.toString();
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
