package org.spigotmc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.ChatSerializer;
import net.minecraft.server.EnumProtocol;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketDataSerializer;
import net.minecraft.server.PacketListener;
import net.minecraft.util.com.google.common.collect.BiMap;

public class ProtocolInjector {
	public static void inject() {
		try {
			addPacket(EnumProtocol.LOGIN, true, 0x3, PacketLoginCompression.class);

			addPacket(EnumProtocol.PLAY, true, 0x45, PacketTitle.class);
			addPacket(EnumProtocol.PLAY, true, 0x47, PacketTabHeader.class);
			addPacket(EnumProtocol.PLAY, true, 0x48, PacketPlayResourcePackSend.class);
			addPacket(EnumProtocol.PLAY, false, 0x19, PacketPlayResourcePackStatus.class);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static void addPacket(EnumProtocol protocol, boolean clientbound, int id, Class<? extends Packet> packet) throws NoSuchFieldException, IllegalAccessException {
		Field packets;
		if (!clientbound) {
			packets = EnumProtocol.class.getDeclaredField("h");
		} else {
			packets = EnumProtocol.class.getDeclaredField("i");
		}
		packets.setAccessible(true);
		BiMap<Integer, Class<? extends Packet>> pMap = (BiMap<Integer, Class<? extends Packet>>) packets.get(protocol);
		pMap.put(id, packet);
		Field map = EnumProtocol.class.getDeclaredField("f");
		map.setAccessible(true);
		Map<Class<? extends Packet>, EnumProtocol> protocolMap = (Map<Class<? extends Packet>, EnumProtocol>) map.get(null);
		protocolMap.put(packet, protocol);
	}

	public static class PacketPlayResourcePackStatus extends Packet {

		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.c(255); // Hash
			packetdataserializer.a(); // Result
		}

		@Override
		public void b(PacketDataSerializer packetdataserializer) throws IOException {

		}

		@Override
		public void handle(PacketListener packetlistener) {

		}
	}

	public static class PacketPlayResourcePackSend extends Packet {

		private String url;
		private String hash;

		public PacketPlayResourcePackSend(String url, String hash) {
			this.url = url;
			this.hash = hash;
		}

		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {

		}

		@Override
		public void b(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.a(url);
			packetdataserializer.a(hash);
		}

		@Override
		public void handle(PacketListener packetlistener) {

		}
	}

	public static class PacketLoginCompression extends Packet {

		private int threshold;

		public PacketLoginCompression(int threshold) {
			this.threshold = threshold;
		}

		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {

		}

		@Override
		public void b(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.b(threshold);
		}

		@Override
		public void handle(PacketListener packetlistener) {

		}
	}

	public static class PacketTabHeader extends Packet {

		private IChatBaseComponent header;
		private IChatBaseComponent footer;

		public PacketTabHeader() {
		}

		public PacketTabHeader(IChatBaseComponent header, IChatBaseComponent footer) {
			this.header = header;
			this.footer = footer;
		}

		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {
			header = ChatSerializer.a(packetdataserializer.c(32767));
			footer = ChatSerializer.a(packetdataserializer.c(32767));
		}

		@Override
		public void b(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.a(ChatSerializer.a(header));
			packetdataserializer.a(ChatSerializer.a(footer));
		}

		@Override
		public void handle(PacketListener packetlistener) {
		}
	}

	public static class PacketTitle extends Packet {
		private Action action;

		// TITLE & SUBTITLE
		private IChatBaseComponent text;

		// TIMES
		private int fadeIn = -1;
		private int stay = -1;
		private int fadeOut = -1;

		public PacketTitle() {
		}

		public PacketTitle(Action action) {
			this.action = action;
		}

		public PacketTitle(Action action, IChatBaseComponent text) {
			this(action);
			this.text = text;
		}

		public PacketTitle(Action action, int fadeIn, int stay, int fadeOut) {
			this(action);
			this.fadeIn = fadeIn;
			this.stay = stay;
			this.fadeOut = fadeOut;
		}

		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {
			action = Action.values()[packetdataserializer.a()];
			switch (action) {
			case TITLE:
			case SUBTITLE:
				text = ChatSerializer.a(packetdataserializer.c(32767));
				break;
			case TIMES:
				fadeIn = packetdataserializer.readInt();
				stay = packetdataserializer.readInt();
				fadeOut = packetdataserializer.readInt();
				break;
			}
		}

		@Override
		public void b(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.b(action.ordinal());
			switch (action) {
			case TITLE:
			case SUBTITLE:
				packetdataserializer.a(ChatSerializer.a(text));
				break;
			case TIMES:
				packetdataserializer.writeInt(fadeIn);
				packetdataserializer.writeInt(stay);
				packetdataserializer.writeInt(fadeOut);
				break;
			}
		}

		@Override
		public void handle(PacketListener packetlistener) {
		}

		public static enum Action {
			TITLE, SUBTITLE, TIMES, CLEAR, RESET
		}
	}
}
