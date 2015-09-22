package net.minecraft.server;

public class PacketPlayOutEntityEffect extends Packet {

	private int a;
	private byte b;
	private byte c;
	private short d;

	public PacketPlayOutEntityEffect() {
	}

	public PacketPlayOutEntityEffect(int i, MobEffect mobeffect) {
		a = i;
		b = (byte) (mobeffect.getEffectId() & 255);
		c = (byte) (mobeffect.getAmplifier() & 255);
		if (mobeffect.getDuration() > 32767) {
			d = 32767;
		} else {
			d = (short) mobeffect.getDuration();
		}
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = packetdataserializer.readInt();
		b = packetdataserializer.readByte();
		c = packetdataserializer.readByte();
		d = packetdataserializer.readShort();
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol patch
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeInt(a);
			packetdataserializer.writeByte(b);
			packetdataserializer.writeByte(c);
			packetdataserializer.writeShort(d);
		} else {
			packetdataserializer.b(a);
			packetdataserializer.writeByte(b);
			packetdataserializer.writeByte(c);
			packetdataserializer.b(d);
			packetdataserializer.writeBoolean(false);
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
