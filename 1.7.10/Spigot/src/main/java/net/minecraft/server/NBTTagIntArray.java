package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {

	private int[] data;

	NBTTagIntArray() {
	}

	public NBTTagIntArray(int[] aint) {
		data = aint;
	}

	@Override
	void write(DataOutput dataoutput) throws IOException {
		dataoutput.writeInt(data.length);

		for (int i = 0; i < data.length; ++i) {
			dataoutput.writeInt(data[i]);
		}
	}

	@Override
	void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
		int j = datainput.readInt();
		com.google.common.base.Preconditions.checkArgument(j < 1 << 24);

		nbtreadlimiter.a(32 * j);
		data = new int[j];

		for (int k = 0; k < j; ++k) {
			data[k] = datainput.readInt();
		}
	}

	@Override
	public byte getTypeId() {
		return (byte) 11;
	}

	@Override
	public String toString() {
		String s = "[";
		int[] aint = data;
		int i = aint.length;

		for (int j = 0; j < i; ++j) {
			int k = aint[j];

			s = s + k + ",";
		}

		return s + "]";
	}

	@Override
	public NBTBase clone() {
		int[] aint = new int[data.length];

		System.arraycopy(data, 0, aint, 0, data.length);
		return new NBTTagIntArray(aint);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) ? Arrays.equals(data, ((NBTTagIntArray) object).data) : false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(data);
	}

	public int[] c() {
		return data;
	}
}
