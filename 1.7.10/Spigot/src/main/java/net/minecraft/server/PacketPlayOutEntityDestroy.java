package net.minecraft.server;

public class PacketPlayOutEntityDestroy extends Packet {

	private int[] a;

	public PacketPlayOutEntityDestroy() {
	}

	public PacketPlayOutEntityDestroy(int... aint) {
		a = aint;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) {
		a = new int[packetdataserializer.readByte()];

		for (int i = 0; i < a.length; ++i) {
			a[i] = packetdataserializer.readInt();
		}
	}

	@Override
	public void b(PacketDataSerializer packetdataserializer) {
		// Spigot start - protocol lib
		if (packetdataserializer.version < 16) {
			packetdataserializer.writeByte(a.length);

			for (int i = 0; i < a.length; ++i) {
				packetdataserializer.writeInt(a[i]);
			}
		} else {
			packetdataserializer.b(a.length);
			for (int i : a) {
				packetdataserializer.b(i);
			}
		}
		// Spigot end
	}

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public String b() {
		StringBuilder stringbuilder = new StringBuilder();

		for (int i = 0; i < a.length; ++i) {
			if (i > 0) {
				stringbuilder.append(", ");
			}

			stringbuilder.append(a[i]);
		}

		return String.format("entities=%d[%s]", new Object[] { Integer.valueOf(a.length), stringbuilder });
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}
}
