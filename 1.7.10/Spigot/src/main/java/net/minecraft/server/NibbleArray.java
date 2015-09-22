package net.minecraft.server;

public class NibbleArray {

	public final byte[] a;
	private final int b;
	private final int c;

	public NibbleArray(int i, int j) {
		a = new byte[i >> 1];
		b = j;
		c = j + 4;
	}

	public NibbleArray(byte[] abyte, int i) {
		a = abyte;
		b = i;
		c = i + 4;
	}

	public int a(int i, int j, int k) {
		int l = j << c | k << b | i;
		int i1 = l >> 1;
		int j1 = l & 1;

		return j1 == 0 ? a[i1] & 15 : a[i1] >> 4 & 15;
	}

	public void a(int i, int j, int k, int l) {
		int i1 = j << c | k << b | i;
		int j1 = i1 >> 1;
		int k1 = i1 & 1;

		if (k1 == 0) {
			a[j1] = (byte) (a[j1] & 240 | l & 15);
		} else {
			a[j1] = (byte) (a[j1] & 15 | (l & 15) << 4);
		}
	}
}
