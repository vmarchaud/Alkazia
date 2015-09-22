package net.minecraft.server;

public class PacketPlayOutRemoveEntityEffect extends Packet {

	private int a;
	private int b;

	public PacketPlayOutRemoveEntityEffect() {
	}

	public PacketPlayOutRemoveEntityEffect(int i, MobEffect mobeffect) {
		a = i;
		b = mobeffect.getEffectId();
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readUnsignedByte();
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
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
