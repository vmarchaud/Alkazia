package net.minecraft.server;

import java.net.SocketAddress;
import java.util.Queue;

import javax.crypto.SecretKey;

import net.minecraft.util.com.google.common.collect.Queues;
import net.minecraft.util.com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelFutureListener;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.util.io.netty.channel.local.LocalChannel;
import net.minecraft.util.io.netty.channel.local.LocalServerChannel;
import net.minecraft.util.io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.io.netty.handler.timeout.TimeoutException;
import net.minecraft.util.io.netty.util.AttributeKey;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.org.apache.commons.lang3.Validate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.spigotmc.SpigotCompressor;
import org.spigotmc.SpigotDecompressor;
// Spigot end

// Spigot start
import com.google.common.collect.ImmutableSet;

public class NetworkManager extends SimpleChannelInboundHandler {

	private static final Logger i = LogManager.getLogger();
	public static final Marker a = MarkerManager.getMarker("NETWORK");
	public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", a);
	public static final Marker c = MarkerManager.getMarker("NETWORK_STAT", a);
	public static final AttributeKey d = new AttributeKey("protocol");
	public static final AttributeKey e = new AttributeKey("receivable_packets");
	public static final AttributeKey f = new AttributeKey("sendable_packets");
	public static final NioEventLoopGroup g = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
	public static final NetworkStatistics h = new NetworkStatistics();
	private final boolean j;
	private final Queue k = Queues.newConcurrentLinkedQueue();
	private final Queue l = Queues.newConcurrentLinkedQueue();
	private Channel m;
	// Spigot Start
	public SocketAddress n;
	public java.util.UUID spoofedUUID;
	public Property[] spoofedProfile;
	public boolean preparing = true;
	// Spigot End
	private PacketListener o;
	private EnumProtocol p;
	private IChatBaseComponent q;
	private boolean r;
	// Spigot Start
	public static final AttributeKey<Integer> protocolVersion = new AttributeKey<Integer>("protocol_version");
	public static final ImmutableSet<Integer> SUPPORTED_VERSIONS = ImmutableSet.of(4, 5, 47);
	public static final int CURRENT_VERSION = 5;

	public static int getVersion(Channel attr) {
		Integer ver = attr.attr(protocolVersion).get();
		return ver != null ? ver : CURRENT_VERSION;
	}

	public int getVersion() {
		return getVersion(m);
	}

	// Spigot End

	public NetworkManager(boolean flag) {
		j = flag;
	}

	@Override
	public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception { // CraftBukkit - throws Exception
		super.channelActive(channelhandlercontext);
		m = channelhandlercontext.channel();
		n = m.remoteAddress();
		// Spigot Start
		preparing = false;
		// Spigot End
		this.a(EnumProtocol.HANDSHAKING);
	}

	public void a(EnumProtocol enumprotocol) {
		p = (EnumProtocol) m.attr(d).getAndSet(enumprotocol);
		m.attr(e).set(enumprotocol.a(j));
		m.attr(f).set(enumprotocol.b(j));
		m.config().setAutoRead(true);
		i.debug("Enabled auto read");
	}

	@Override
	public void channelInactive(ChannelHandlerContext channelhandlercontext) {
		close(new ChatMessage("disconnect.endOfStream", new Object[0]));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
		ChatMessage chatmessage;

		if (throwable instanceof TimeoutException) {
			chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
		} else {
			chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable });
		}

		close(chatmessage);
		if (MinecraftServer.getServer().isDebugging()) {
			throwable.printStackTrace(); // Spigot
		}
	}

	protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {
		if (m.isOpen()) {
			if (packet.a()) {
				packet.handle(o);
			} else {
				k.add(packet);
			}
		}
	}

	public void a(PacketListener packetlistener) {
		Validate.notNull(packetlistener, "packetListener", new Object[0]);
		i.debug("Set listener of {} to {}", new Object[] { this, packetlistener });
		o = packetlistener;
	}

	public void handle(Packet packet, GenericFutureListener... agenericfuturelistener) {
		if (m != null && m.isOpen()) {
			i();
			b(packet, agenericfuturelistener);
		} else {
			l.add(new QueuedPacket(packet, agenericfuturelistener));
		}
	}

	private void b(Packet packet, GenericFutureListener[] agenericfuturelistener) {
		EnumProtocol enumprotocol = EnumProtocol.a(packet);
		EnumProtocol enumprotocol1 = (EnumProtocol) m.attr(d).get();

		if (enumprotocol1 != enumprotocol) {
			i.debug("Disabled auto read");
			m.config().setAutoRead(false);
		}

		if (m.eventLoop().inEventLoop()) {
			if (enumprotocol != enumprotocol1) {
				this.a(enumprotocol);
			}

			m.writeAndFlush(packet).addListeners(agenericfuturelistener).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			m.eventLoop().execute(new QueuedProtocolSwitch(this, enumprotocol, enumprotocol1, packet, agenericfuturelistener));
		}
	}

	private void i() {
		if (m != null && m.isOpen()) {
			while (!l.isEmpty()) {
				QueuedPacket queuedpacket = (QueuedPacket) l.poll();

				b(QueuedPacket.a(queuedpacket), QueuedPacket.b(queuedpacket));
			}
		}
	}

	public void a() {
		i();
		EnumProtocol enumprotocol = (EnumProtocol) m.attr(d).get();

		if (p != enumprotocol) {
			if (p != null) {
				o.a(p, enumprotocol);
			}

			p = enumprotocol;
		}

		if (o != null) {
			for (int i = 1000; !k.isEmpty() && i >= 0; --i) {
				Packet packet = (Packet) k.poll();

				// CraftBukkit start
				if (!isConnected() || !m.config().isAutoRead()) {
					continue;
				}
				// CraftBukkit end
				packet.handle(o);
			}

			o.a();
		}

		m.flush();
	}

	public SocketAddress getSocketAddress() {
		return n;
	}

	public void close(IChatBaseComponent ichatbasecomponent) {
		// Spigot Start
		preparing = false;
		// Spigot End
		if (m.isOpen()) {
			m.close();
			q = ichatbasecomponent;
		}
	}

	public boolean c() {
		return m instanceof LocalChannel || m instanceof LocalServerChannel;
	}

	public void a(SecretKey secretkey) {
		m.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
		m.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
		r = true;
	}

	public boolean isConnected() {
		return m != null && m.isOpen();
	}

	public PacketListener getPacketListener() {
		return o;
	}

	public IChatBaseComponent f() {
		return q;
	}

	public void g() {
		m.config().setAutoRead(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelhandlercontext, Object object) {
		this.a(channelhandlercontext, (Packet) object);
	}

	static Channel a(NetworkManager networkmanager) {
		return networkmanager.m;
	}

	// Spigot Start
	public SocketAddress getRawAddress() {
		return m.remoteAddress();
	}

	// Spigot End

	// Spigot start - protocol patch
	public void enableCompression() {
		// Fix ProtocolLib compatibility
		if (m.pipeline().get("protocol_lib_decoder") != null) {
			m.pipeline().addBefore("protocol_lib_decoder", "decompress", new SpigotDecompressor());
		} else {
			m.pipeline().addBefore("decoder", "decompress", new SpigotDecompressor());
		}

		m.pipeline().addBefore("encoder", "compress", new SpigotCompressor());
	}
	// Spigot end
}
