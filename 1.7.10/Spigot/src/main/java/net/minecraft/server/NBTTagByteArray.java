package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {

	private byte[] data;

	NBTTagByteArray() {
	}

	public NBTTagByteArray(byte[] abyte) {
		data = abyte;
	}

	@Override
	void write(DataOutput dataoutput) throws IOException {
		dataoutput.writeInt(data.length);
		dataoutput.write(data);
	}

	@Override
	void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
		int j = datainput.readInt();
		com.google.common.base.Preconditions.checkArgument(j < 1 << 24);

		nbtreadlimiter.a(8 * j);
		data = new byte[j];
		datainput.readFully(data);
	}

	@Override
	public byte getTypeId() {
		return (byte) 7;
	}

	@Override
	public String toString() {
		return "[" + data.length + " bytes]";
	}

	@Override
	public NBTBase clone() {
		byte[] abyte = new byte[data.length];

		System.arraycopy(data, 0, abyte, 0, data.length);
		return new NBTTagByteArray(abyte);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) ? Arrays.equals(data, ((NBTTagByteArray) object).data) : false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(data);
	}

	public byte[] c() {
		return data;
	}
}
