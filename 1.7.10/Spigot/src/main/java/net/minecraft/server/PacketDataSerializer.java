package net.minecraft.server;

// Spigot start - protocol patch
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.ByteBufAllocator;
import net.minecraft.util.io.netty.buffer.ByteBufInputStream;
import net.minecraft.util.io.netty.buffer.ByteBufOutputStream;
import net.minecraft.util.io.netty.buffer.ByteBufProcessor;
import net.minecraft.util.io.netty.buffer.Unpooled;

import org.bukkit.craftbukkit.inventory.CraftItemStack; // CraftBukkit
import org.spigotmc.SpigotComponentReverter;

// Spigot end

public class PacketDataSerializer extends ByteBuf {

	private final ByteBuf a;
	// Spigot Start
	public final int version;

	public PacketDataSerializer(ByteBuf bytebuf) {
		this(bytebuf, NetworkManager.CURRENT_VERSION);
	}

	public PacketDataSerializer(ByteBuf bytebuf, int version) {
		a = bytebuf;
		this.version = version;
	}

	public void writePosition(int x, int y, int z) {
		writeLong((x & 0x3FFFFFFL) << 38 | (y & 0xFFFL) << 26 | z & 0x3FFFFFFL);
	}

	public int readPositionX(long val) {
		return (int) (val >> 38);
	}

	public int readPositionY(long val) {
		return (int) (val << 26 >> 52);
	}

	public int readPositionZ(long val) {
		return (int) (val << 38 >> 38);
	}

	public void writeUUID(UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	// Spigot End

	public static int a(int i) {
		return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (i & -2097152) == 0 ? 3 : (i & -268435456) == 0 ? 4 : 5;
	}

	public int a() {
		int i = 0;
		int j = 0;

		byte b0;

		do {
			b0 = readByte();
			i |= (b0 & 127) << j++ * 7;
			if (j > 5)
				throw new RuntimeException("VarInt too big");
		} while ((b0 & 128) == 128);

		return i;
	}

	public void b(int i) {
		while ((i & -128) != 0) {
			writeByte(i & 127 | 128);
			i >>>= 7;
		}

		writeByte(i);
	}

	// Spigot start - protocol patch
	public void a(NBTTagCompound nbttagcompound) {
		if (version < 28) {
			if (nbttagcompound == null) {
				writeShort(-1);
			} else {
				byte[] abyte = NBTCompressedStreamTools.a(nbttagcompound);

				writeShort((short) abyte.length);
				this.writeBytes(abyte);
			}
		} else {
			if (nbttagcompound == null) {
				writeByte(0);
			} else {
				ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());
				NBTCompressedStreamTools.a(nbttagcompound, (java.io.DataOutput) new DataOutputStream(out));
				writeBytes(out.buffer());
				out.buffer().release();
			}
		}
	}

	public NBTTagCompound b() {
		if (version < 28) {
			short short1 = readShort();

			if (short1 < 0)
				return null;
			else {
				byte[] abyte = new byte[short1];

				this.readBytes(abyte);
				return NBTCompressedStreamTools.a(abyte, new NBTReadLimiter(2097152L));
			}
		} else {
			int index = readerIndex();
			if (readByte() == 0)
				return null;
			readerIndex(index);
			return NBTCompressedStreamTools.a(new DataInputStream(new ByteBufInputStream(a)));
		}
	}

	// Spigot end

	public void a(ItemStack itemstack) {
		if (itemstack == null || itemstack.getItem() == null) { // CraftBukkit - NPE fix itemstack.getItem()
			writeShort(-1);
		} else {
			// Spigot start - protocol patch
			if (version >= 47) {
				writeShort(org.spigotmc.SpigotDebreakifier.getItemId(Item.getId(itemstack.getItem())));
			} else {
				writeShort(Item.getId(itemstack.getItem()));
			}
			// Spigot end
			writeByte(itemstack.count);
			writeShort(itemstack.getData());
			NBTTagCompound nbttagcompound = null;

			if (itemstack.getItem().usesDurability() || itemstack.getItem().s()) {
				// Spigot start - filter
				itemstack = itemstack.cloneItemStack();
				CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
				// Spigot end
				nbttagcompound = itemstack.tag;
			}

			// Spigot start - protocol patch
			if (nbttagcompound != null && version >= 29) {
				if (itemstack.getItem() == Items.WRITTEN_BOOK && nbttagcompound.hasKeyOfType("pages", 9)) {
					nbttagcompound = (NBTTagCompound) nbttagcompound.clone();
					NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);
					NBTTagList newList = new NBTTagList();
					for (int i = 0; i < nbttaglist.size(); ++i) {
						IChatBaseComponent[] parts = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(nbttaglist.getString(i));
						IChatBaseComponent root = parts[0];
						for (int i1 = 1; i1 < parts.length; i1++) {
							IChatBaseComponent c = parts[i1];
							root.a("\n");
							root.addSibling(c);
						}
						newList.add(new NBTTagString(ChatSerializer.a(root)));
					}
					nbttagcompound.set("pages", newList);
				}
			}
			// Spigot end

			this.a(nbttagcompound);
		}
	}

	public ItemStack c() {
		ItemStack itemstack = null;
		short short1 = readShort();

		if (short1 >= 0) {
			byte b0 = readByte();
			short short2 = readShort();

			itemstack = new ItemStack(Item.getById(short1), b0, short2);
			itemstack.tag = this.b();
			// CraftBukkit start
			if (itemstack.tag != null) {

				// Spigot start - protocol patch
				if (version >= 29 && itemstack.getItem() == Items.WRITTEN_BOOK && itemstack.tag.hasKeyOfType("pages", 9)) {
					NBTTagList nbttaglist = itemstack.tag.getList("pages", 8);
					NBTTagList newList = new NBTTagList();
					for (int i = 0; i < nbttaglist.size(); ++i) {
						IChatBaseComponent s = ChatSerializer.a(nbttaglist.getString(i));
						String newString = SpigotComponentReverter.toLegacy(s);
						newList.add(new NBTTagString(newString));
					}
					itemstack.tag.set("pages", newList);
				}
				// Spigot end

				CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
			}
			// CraftBukkit end
		}

		return itemstack;
	}

	public String c(int i) throws IOException { // CraftBukkit - throws IOException
		int j = this.a();

		if (j > i * 4)
			throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
		else if (j < 0)
			throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
		else {
			String s = new String(this.readBytes(j).array(), Charsets.UTF_8);

			if (s.length() > i)
				throw new IOException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
			else
				return s;
		}
	}

	public void a(String s) throws IOException { // CraftBukkit - throws IOException
		byte[] abyte = s.getBytes(Charsets.UTF_8);

		if (abyte.length > 32767)
			throw new IOException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
		else {
			this.b(abyte.length);
			this.writeBytes(abyte);
		}
	}

	@Override
	public int capacity() {
		return a.capacity();
	}

	@Override
	public ByteBuf capacity(int i) {
		return a.capacity(i);
	}

	@Override
	public int maxCapacity() {
		return a.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return a.alloc();
	}

	@Override
	public ByteOrder order() {
		return a.order();
	}

	@Override
	public ByteBuf order(ByteOrder byteorder) {
		return a.order(byteorder);
	}

	@Override
	public ByteBuf unwrap() {
		return a.unwrap();
	}

	@Override
	public boolean isDirect() {
		return a.isDirect();
	}

	@Override
	public int readerIndex() {
		return a.readerIndex();
	}

	@Override
	public ByteBuf readerIndex(int i) {
		return a.readerIndex(i);
	}

	@Override
	public int writerIndex() {
		return a.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int i) {
		return a.writerIndex(i);
	}

	@Override
	public ByteBuf setIndex(int i, int j) {
		return a.setIndex(i, j);
	}

	@Override
	public int readableBytes() {
		return a.readableBytes();
	}

	@Override
	public int writableBytes() {
		return a.writableBytes();
	}

	@Override
	public int maxWritableBytes() {
		return a.maxWritableBytes();
	}

	@Override
	public boolean isReadable() {
		return a.isReadable();
	}

	@Override
	public boolean isReadable(int i) {
		return a.isReadable(i);
	}

	@Override
	public boolean isWritable() {
		return a.isWritable();
	}

	@Override
	public boolean isWritable(int i) {
		return a.isWritable(i);
	}

	@Override
	public ByteBuf clear() {
		return a.clear();
	}

	@Override
	public ByteBuf markReaderIndex() {
		return a.markReaderIndex();
	}

	@Override
	public ByteBuf resetReaderIndex() {
		return a.resetReaderIndex();
	}

	@Override
	public ByteBuf markWriterIndex() {
		return a.markWriterIndex();
	}

	@Override
	public ByteBuf resetWriterIndex() {
		return a.resetWriterIndex();
	}

	@Override
	public ByteBuf discardReadBytes() {
		return a.discardReadBytes();
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		return a.discardSomeReadBytes();
	}

	@Override
	public ByteBuf ensureWritable(int i) {
		return a.ensureWritable(i);
	}

	@Override
	public int ensureWritable(int i, boolean flag) {
		return a.ensureWritable(i, flag);
	}

	@Override
	public boolean getBoolean(int i) {
		return a.getBoolean(i);
	}

	@Override
	public byte getByte(int i) {
		return a.getByte(i);
	}

	@Override
	public short getUnsignedByte(int i) {
		return a.getUnsignedByte(i);
	}

	@Override
	public short getShort(int i) {
		return a.getShort(i);
	}

	@Override
	public int getUnsignedShort(int i) {
		return a.getUnsignedShort(i);
	}

	@Override
	public int getMedium(int i) {
		return a.getMedium(i);
	}

	@Override
	public int getUnsignedMedium(int i) {
		return a.getUnsignedMedium(i);
	}

	@Override
	public int getInt(int i) {
		return a.getInt(i);
	}

	@Override
	public long getUnsignedInt(int i) {
		return a.getUnsignedInt(i);
	}

	@Override
	public long getLong(int i) {
		return a.getLong(i);
	}

	@Override
	public char getChar(int i) {
		return a.getChar(i);
	}

	@Override
	public float getFloat(int i) {
		return a.getFloat(i);
	}

	@Override
	public double getDouble(int i) {
		return a.getDouble(i);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf bytebuf) {
		return a.getBytes(i, bytebuf);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
		return a.getBytes(i, bytebuf, j);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
		return a.getBytes(i, bytebuf, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] abyte) {
		return a.getBytes(i, abyte);
	}

	@Override
	public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
		return a.getBytes(i, abyte, j, k);
	}

	@Override
	public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
		return a.getBytes(i, bytebuffer);
	}

	@Override
	public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException { // CraftBukkit - throws IOException
		return a.getBytes(i, outputstream, j);
	}

	@Override
	public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException { // CraftBukkit - throws IOException
		return a.getBytes(i, gatheringbytechannel, j);
	}

	@Override
	public ByteBuf setBoolean(int i, boolean flag) {
		return a.setBoolean(i, flag);
	}

	@Override
	public ByteBuf setByte(int i, int j) {
		return a.setByte(i, j);
	}

	@Override
	public ByteBuf setShort(int i, int j) {
		return a.setShort(i, j);
	}

	@Override
	public ByteBuf setMedium(int i, int j) {
		return a.setMedium(i, j);
	}

	@Override
	public ByteBuf setInt(int i, int j) {
		return a.setInt(i, j);
	}

	@Override
	public ByteBuf setLong(int i, long j) {
		return a.setLong(i, j);
	}

	@Override
	public ByteBuf setChar(int i, int j) {
		return a.setChar(i, j);
	}

	@Override
	public ByteBuf setFloat(int i, float f) {
		return a.setFloat(i, f);
	}

	@Override
	public ByteBuf setDouble(int i, double d0) {
		return a.setDouble(i, d0);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf bytebuf) {
		return a.setBytes(i, bytebuf);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
		return a.setBytes(i, bytebuf, j);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
		return a.setBytes(i, bytebuf, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] abyte) {
		return a.setBytes(i, abyte);
	}

	@Override
	public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
		return a.setBytes(i, abyte, j, k);
	}

	@Override
	public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
		return a.setBytes(i, bytebuffer);
	}

	@Override
	public int setBytes(int i, InputStream inputstream, int j) throws IOException { // CraftBukkit - throws IOException
		return a.setBytes(i, inputstream, j);
	}

	@Override
	public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException { // CraftBukkit - throws IOException
		return a.setBytes(i, scatteringbytechannel, j);
	}

	@Override
	public ByteBuf setZero(int i, int j) {
		return a.setZero(i, j);
	}

	@Override
	public boolean readBoolean() {
		return a.readBoolean();
	}

	@Override
	public byte readByte() {
		return a.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return a.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return a.readShort();
	}

	@Override
	public int readUnsignedShort() {
		return a.readUnsignedShort();
	}

	@Override
	public int readMedium() {
		return a.readMedium();
	}

	@Override
	public int readUnsignedMedium() {
		return a.readUnsignedMedium();
	}

	@Override
	public int readInt() {
		return a.readInt();
	}

	@Override
	public long readUnsignedInt() {
		return a.readUnsignedInt();
	}

	@Override
	public long readLong() {
		return a.readLong();
	}

	@Override
	public char readChar() {
		return a.readChar();
	}

	@Override
	public float readFloat() {
		return a.readFloat();
	}

	@Override
	public double readDouble() {
		return a.readDouble();
	}

	@Override
	public ByteBuf readBytes(int i) {
		return a.readBytes(i);
	}

	@Override
	public ByteBuf readSlice(int i) {
		return a.readSlice(i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf bytebuf) {
		return a.readBytes(bytebuf);
	}

	@Override
	public ByteBuf readBytes(ByteBuf bytebuf, int i) {
		return a.readBytes(bytebuf, i);
	}

	@Override
	public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
		return a.readBytes(bytebuf, i, j);
	}

	@Override
	public ByteBuf readBytes(byte[] abyte) {
		return a.readBytes(abyte);
	}

	@Override
	public ByteBuf readBytes(byte[] abyte, int i, int j) {
		return a.readBytes(abyte, i, j);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer bytebuffer) {
		return a.readBytes(bytebuffer);
	}

	@Override
	public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException { // CraftBukkit - throws IOException
		return a.readBytes(outputstream, i);
	}

	@Override
	public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException { // CraftBukkit - throws IOException
		return a.readBytes(gatheringbytechannel, i);
	}

	@Override
	public ByteBuf skipBytes(int i) {
		return a.skipBytes(i);
	}

	@Override
	public ByteBuf writeBoolean(boolean flag) {
		return a.writeBoolean(flag);
	}

	@Override
	public ByteBuf writeByte(int i) {
		return a.writeByte(i);
	}

	@Override
	public ByteBuf writeShort(int i) {
		return a.writeShort(i);
	}

	@Override
	public ByteBuf writeMedium(int i) {
		return a.writeMedium(i);
	}

	@Override
	public ByteBuf writeInt(int i) {
		return a.writeInt(i);
	}

	@Override
	public ByteBuf writeLong(long i) {
		return a.writeLong(i);
	}

	@Override
	public ByteBuf writeChar(int i) {
		return a.writeChar(i);
	}

	@Override
	public ByteBuf writeFloat(float f) {
		return a.writeFloat(f);
	}

	@Override
	public ByteBuf writeDouble(double d0) {
		return a.writeDouble(d0);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf bytebuf) {
		return a.writeBytes(bytebuf);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
		return a.writeBytes(bytebuf, i);
	}

	@Override
	public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
		return a.writeBytes(bytebuf, i, j);
	}

	@Override
	public ByteBuf writeBytes(byte[] abyte) {
		return a.writeBytes(abyte);
	}

	@Override
	public ByteBuf writeBytes(byte[] abyte, int i, int j) {
		return a.writeBytes(abyte, i, j);
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer bytebuffer) {
		return a.writeBytes(bytebuffer);
	}

	@Override
	public int writeBytes(InputStream inputstream, int i) throws IOException { // CraftBukkit - throws IOException
		return a.writeBytes(inputstream, i);
	}

	@Override
	public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException { // CraftBukkit - throws IOException
		return a.writeBytes(scatteringbytechannel, i);
	}

	@Override
	public ByteBuf writeZero(int i) {
		return a.writeZero(i);
	}

	@Override
	public int indexOf(int i, int j, byte b0) {
		return a.indexOf(i, j, b0);
	}

	@Override
	public int bytesBefore(byte b0) {
		return a.bytesBefore(b0);
	}

	@Override
	public int bytesBefore(int i, byte b0) {
		return a.bytesBefore(i, b0);
	}

	@Override
	public int bytesBefore(int i, int j, byte b0) {
		return a.bytesBefore(i, j, b0);
	}

	@Override
	public int forEachByte(ByteBufProcessor bytebufprocessor) {
		return a.forEachByte(bytebufprocessor);
	}

	@Override
	public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
		return a.forEachByte(i, j, bytebufprocessor);
	}

	@Override
	public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
		return a.forEachByteDesc(bytebufprocessor);
	}

	@Override
	public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
		return a.forEachByteDesc(i, j, bytebufprocessor);
	}

	@Override
	public ByteBuf copy() {
		return a.copy();
	}

	@Override
	public ByteBuf copy(int i, int j) {
		return a.copy(i, j);
	}

	@Override
	public ByteBuf slice() {
		return a.slice();
	}

	@Override
	public ByteBuf slice(int i, int j) {
		return a.slice(i, j);
	}

	@Override
	public ByteBuf duplicate() {
		return a.duplicate();
	}

	@Override
	public int nioBufferCount() {
		return a.nioBufferCount();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return a.nioBuffer();
	}

	@Override
	public ByteBuffer nioBuffer(int i, int j) {
		return a.nioBuffer(i, j);
	}

	@Override
	public ByteBuffer internalNioBuffer(int i, int j) {
		return a.internalNioBuffer(i, j);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return a.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int i, int j) {
		return a.nioBuffers(i, j);
	}

	@Override
	public boolean hasArray() {
		return a.hasArray();
	}

	@Override
	public byte[] array() {
		return a.array();
	}

	@Override
	public int arrayOffset() {
		return a.arrayOffset();
	}

	@Override
	public boolean hasMemoryAddress() {
		return a.hasMemoryAddress();
	}

	@Override
	public long memoryAddress() {
		return a.memoryAddress();
	}

	@Override
	public String toString(Charset charset) {
		return a.toString(charset);
	}

	@Override
	public String toString(int i, int j, Charset charset) {
		return a.toString(i, j, charset);
	}

	@Override
	public int hashCode() {
		return a.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return a.equals(object);
	}

	@Override
	public int compareTo(ByteBuf bytebuf) {
		return a.compareTo(bytebuf);
	}

	@Override
	public String toString() {
		return a.toString();
	}

	@Override
	public ByteBuf retain(int i) {
		return a.retain(i);
	}

	@Override
	public ByteBuf retain() {
		return a.retain();
	}

	@Override
	public int refCnt() {
		return a.refCnt();
	}

	@Override
	public boolean release() {
		return a.release();
	}

	@Override
	public boolean release(int i) {
		return a.release(i);
	}
}
