package net.minecraft.server;

public class WatchableObject {

	private final int a;
	private final int b;
	private Object c;
	private boolean d;

	public WatchableObject(int i, int j, Object object) {
		b = j;
		c = object;
		a = i;
		d = true;
	}

	public int a() {
		return b;
	}

	public void a(Object object) {
		c = object;
	}

	public Object b() {
		return c;
	}

	public int c() {
		return a;
	}

	public boolean d() {
		return d;
	}

	public void a(boolean flag) {
		d = flag;
	}

	static boolean a(WatchableObject watchableobject, boolean flag) {
		return watchableobject.d = flag;
	}
}
