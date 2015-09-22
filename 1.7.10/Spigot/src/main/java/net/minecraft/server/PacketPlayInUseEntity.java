package net.minecraft.server;

public class PacketPlayInUseEntity extends Packet {

	private int a;
	private EnumEntityUseAction action;

	public PacketPlayInUseEntity() {
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		// Spigot start
		if (packetdataserializer.version < 16) {
			a = packetdataserializer.readInt();
			action = EnumEntityUseAction.values()[packetdataserializer.readByte() % EnumEntityUseAction.values().length];
		} else {
			a = packetdataserializer.a();
			int val = packetdataserializer.a();
			if (val == 2) {
				packetdataserializer.readFloat();
				packetdataserializer.readFloat();
				packetdataserializer.readFloat();
			} else {
				action = EnumEntityUseAction.values()[val % EnumEntityUseAction.values().length];
			}
		}
		// Spigot end
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		packetdataserializer.writeInt(a);
		packetdataserializer.writeByte(action.ordinal());
	}

	public void a(PacketPlayInListener packetplayinlistener) {
		packetplayinlistener.a(this);
	}

	public Entity a(World world) {
		return world.getEntity(a);
	}

	public EnumEntityUseAction c() {
		return action;
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayInListener) packetlistener);
	}
}
