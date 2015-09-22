package org.spigotmc;

import java.util.zip.Deflater;

import net.minecraft.server.PacketDataSerializer;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.MessageToByteEncoder;

public class SpigotCompressor extends MessageToByteEncoder {

	private final byte[] buffer = new byte[8192];
	private final Deflater deflater = new Deflater();

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		int origSize = in.readableBytes();
		PacketDataSerializer serializer = new PacketDataSerializer(out);

		if (origSize < 256) {
			serializer.b(0);
			serializer.writeBytes(in);
		} else {
			byte[] data = new byte[origSize];
			in.readBytes(data);

			serializer.b(data.length);

			deflater.setInput(data);
			deflater.finish();
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				serializer.writeBytes(buffer, 0, count);
			}
			deflater.reset();
		}
	}
}
