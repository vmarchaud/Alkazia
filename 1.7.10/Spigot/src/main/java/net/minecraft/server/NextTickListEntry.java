package net.minecraft.server;

public class NextTickListEntry implements Comparable {

	private static long f;
	private final Block g;
	public int a;
	public int b;
	public int c;
	public long d;
	public int e;
	private long h;

	public NextTickListEntry(int i, int j, int k, Block block) {
		h = f++;
		a = i;
		b = j;
		c = k;
		g = block;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof NextTickListEntry))
			return false;
		else {
			NextTickListEntry nextticklistentry = (NextTickListEntry) object;

			return a == nextticklistentry.a && b == nextticklistentry.b && c == nextticklistentry.c && Block.a(g, nextticklistentry.g);
		}
	}

	@Override
	public int hashCode() {
		return (a * 1024 * 1024 + c * 1024 + b) * 256;
	}

	public NextTickListEntry a(long i) {
		d = i;
		return this;
	}

	public void a(int i) {
		e = i;
	}

	public int compareTo(NextTickListEntry nextticklistentry) {
		return d < nextticklistentry.d ? -1 : d > nextticklistentry.d ? 1 : e != nextticklistentry.e ? e - nextticklistentry.e : h < nextticklistentry.h ? -1 : h > nextticklistentry.h ? 1 : 0;
	}

	@Override
	public String toString() {
		return Block.getId(g) + ": (" + a + ", " + b + ", " + c + "), " + d + ", " + e + ", " + h;
	}

	public Block a() {
		return g;
	}

	@Override
	public int compareTo(Object object) {
		return this.compareTo((NextTickListEntry) object);
	}
}
