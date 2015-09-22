package org.spigotmc;

import java.util.List;
import java.util.zip.Inflater;

import net.minecraft.server.PacketDataSerializer;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.ByteToMessageDecoder;

public class SpigotDecompressor extends ByteToMessageDecoder {

	private final Inflater inflater = new Inflater();

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
		if (byteBuf.readableBytes() == 0)
			return;

		PacketDataSerializer serializer = new PacketDataSerializer(byteBuf);
		int size = serializer.a();
		if (size == 0) {
			objects.add(serializer.readBytes(serializer.readableBytes()));
		} else {
			byte[] compressedData = new byte[serializer.readableBytes()];
			serializer.readBytes(compressedData);
			inflater.setInput(compressedData);

			byte[] data = new byte[size];
			inflater.inflate(data);
			objects.add(Unpooled.wrappedBuffer(data));
			inflater.reset();
		}
	}
}
