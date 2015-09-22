package net.minecraft.server;

public class Path {

	private PathPoint[] a = new PathPoint[128]; // CraftBukkit - reduce default size
	private int b;

	public Path() {
	}

	public PathPoint a(PathPoint pathpoint) {
		if (pathpoint.d >= 0)
			throw new IllegalStateException("OW KNOWS!");
		else {
			if (b == a.length) {
				PathPoint[] apathpoint = new PathPoint[b << 1];

				System.arraycopy(a, 0, apathpoint, 0, b);
				a = apathpoint;
			}

			a[b] = pathpoint;
			pathpoint.d = b;
			this.a(b++);
			return pathpoint;
		}
	}

	public void a() {
		b = 0;
	}

	public PathPoint c() {
		PathPoint pathpoint = a[0];

		a[0] = a[--b];
		a[b] = null;
		if (b > 0) {
			b(0);
		}

		pathpoint.d = -1;
		return pathpoint;
	}

	public void a(PathPoint pathpoint, float f) {
		float f1 = pathpoint.g;

		pathpoint.g = f;
		if (f < f1) {
			this.a(pathpoint.d);
		} else {
			b(pathpoint.d);
		}
	}

	private void a(int i) {
		PathPoint pathpoint = a[i];

		int j;

		for (float f = pathpoint.g; i > 0; i = j) {
			j = i - 1 >> 1;
			PathPoint pathpoint1 = a[j];

			if (f >= pathpoint1.g) {
				break;
			}

			a[i] = pathpoint1;
			pathpoint1.d = i;
		}

		a[i] = pathpoint;
		pathpoint.d = i;
	}

	private void b(int i) {
		PathPoint pathpoint = a[i];
		float f = pathpoint.g;

		while (true) {
			int j = 1 + (i << 1);
			int k = j + 1;

			if (j >= b) {
				break;
			}

			PathPoint pathpoint1 = a[j];
			float f1 = pathpoint1.g;
			PathPoint pathpoint2;
			float f2;

			if (k >= b) {
				pathpoint2 = null;
				f2 = Float.POSITIVE_INFINITY;
			} else {
				pathpoint2 = a[k];
				f2 = pathpoint2.g;
			}

			if (f1 < f2) {
				if (f1 >= f) {
					break;
				}

				a[i] = pathpoint1;
				pathpoint1.d = i;
				i = j;
			} else {
				if (f2 >= f) {
					break;
				}

				a[i] = pathpoint2;
				pathpoint2.d = i;
				i = k;
			}
		}

		a[i] = pathpoint;
		pathpoint.d = i;
	}

	public boolean e() {
		return b == 0;
	}
}
