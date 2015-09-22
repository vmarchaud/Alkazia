package net.minecraft.server;

import java.util.Random;

public class WorldGenBigTree extends WorldGenTreeAbstract {

	static final byte[] a = new byte[] { (byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1 };
	Random b = new Random();
	World world;
	int[] d = new int[] { 0, 0, 0 };
	int e;
	int f;
	double g = 0.618D;
	double h = 1.0D;
	double i = 0.381D;
	double j = 1.0D;
	double k = 1.0D;
	int l = 1;
	int m = 12;
	int n = 4;
	int[][] o;

	public WorldGenBigTree(boolean flag) {
		super(flag);
	}

	void a() {
		f = (int) (e * g);
		if (f >= e) {
			f = e - 1;
		}

		int i = (int) (1.382D + Math.pow(k * e / 13.0D, 2.0D));

		if (i < 1) {
			i = 1;
		}

		int[][] aint = new int[i * e][4];
		int j = d[1] + e - n;
		int k = 1;
		int l = d[1] + f;
		int i1 = j - d[1];

		aint[0][0] = d[0];
		aint[0][1] = j;
		aint[0][2] = d[2];
		aint[0][3] = l;
		--j;

		while (i1 >= 0) {
			int j1 = 0;
			float f = this.a(i1);

			if (f < 0.0F) {
				--j;
				--i1;
			} else {
				for (double d0 = 0.5D; j1 < i; ++j1) {
					double d1 = this.j * f * (b.nextFloat() + 0.328D);
					double d2 = b.nextFloat() * 2.0D * 3.14159D;
					int k1 = MathHelper.floor(d1 * Math.sin(d2) + d[0] + d0);
					int l1 = MathHelper.floor(d1 * Math.cos(d2) + d[2] + d0);
					int[] aint1 = new int[] { k1, j, l1 };
					int[] aint2 = new int[] { k1, j + n, l1 };

					if (this.a(aint1, aint2) == -1) {
						int[] aint3 = new int[] { d[0], d[1], d[2] };
						double d3 = Math.sqrt(Math.pow(Math.abs(d[0] - aint1[0]), 2.0D) + Math.pow(Math.abs(d[2] - aint1[2]), 2.0D));
						double d4 = d3 * this.i;

						if (aint1[1] - d4 > l) {
							aint3[1] = l;
						} else {
							aint3[1] = (int) (aint1[1] - d4);
						}

						if (this.a(aint3, aint1) == -1) {
							aint[k][0] = k1;
							aint[k][1] = j;
							aint[k][2] = l1;
							aint[k][3] = aint3[1];
							++k;
						}
					}
				}

				--j;
				--i1;
			}
		}

		o = new int[k][4];
		System.arraycopy(aint, 0, o, 0, k);
	}

	void a(int i, int j, int k, float f, byte b0, Block block) {
		int l = (int) (f + 0.618D);
		byte b1 = a[b0];
		byte b2 = a[b0 + 3];
		int[] aint = new int[] { i, j, k };
		int[] aint1 = new int[] { 0, 0, 0 };
		int i1 = -l;
		int j1 = -l;

		for (aint1[b0] = aint[b0]; i1 <= l; ++i1) {
			aint1[b1] = aint[b1] + i1;
			j1 = -l;

			while (j1 <= l) {
				double d0 = Math.pow(Math.abs(i1) + 0.5D, 2.0D) + Math.pow(Math.abs(j1) + 0.5D, 2.0D);

				if (d0 > f * f) {
					++j1;
				} else {
					aint1[b2] = aint[b2] + j1;
					Block block1 = world.getType(aint1[0], aint1[1], aint1[2]);

					if (block1.getMaterial() != Material.AIR && block1.getMaterial() != Material.LEAVES) {
						++j1;
					} else {
						setTypeAndData(world, aint1[0], aint1[1], aint1[2], block, 0);
						++j1;
					}
				}
			}
		}
	}

	float a(int i) {
		if (i < e * 0.3D)
			return -1.618F;
		else {
			float f = e / 2.0F;
			float f1 = e / 2.0F - i;
			float f2;

			if (f1 == 0.0F) {
				f2 = f;
			} else if (Math.abs(f1) >= f) {
				f2 = 0.0F;
			} else {
				f2 = (float) Math.sqrt(Math.pow(Math.abs(f), 2.0D) - Math.pow(Math.abs(f1), 2.0D));
			}

			f2 *= 0.5F;
			return f2;
		}
	}

	float b(int i) {
		return i >= 0 && i < n ? i != 0 && i != n - 1 ? 3.0F : 2.0F : -1.0F;
	}

	void a(int i, int j, int k) {
		int l = j;

		for (int i1 = j + n; l < i1; ++l) {
			float f = this.b(l - j);

			this.a(i, l, k, f, (byte) 1, Blocks.LEAVES);
		}
	}

	void a(int[] aint, int[] aint1, Block block) {
		int[] aint2 = new int[] { 0, 0, 0 };
		byte b0 = 0;

		byte b1;

		for (b1 = 0; b0 < 3; ++b0) {
			aint2[b0] = aint1[b0] - aint[b0];
			if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
				b1 = b0;
			}
		}

		if (aint2[b1] != 0) {
			byte b2 = a[b1];
			byte b3 = a[b1 + 3];
			byte b4;

			if (aint2[b1] > 0) {
				b4 = 1;
			} else {
				b4 = -1;
			}

			double d0 = (double) aint2[b2] / (double) aint2[b1];
			double d1 = (double) aint2[b3] / (double) aint2[b1];
			int[] aint3 = new int[] { 0, 0, 0 };
			int i = 0;

			for (int j = aint2[b1] + b4; i != j; i += b4) {
				aint3[b1] = MathHelper.floor(aint[b1] + i + 0.5D);
				aint3[b2] = MathHelper.floor(aint[b2] + i * d0 + 0.5D);
				aint3[b3] = MathHelper.floor(aint[b3] + i * d1 + 0.5D);
				byte b5 = 0;
				int k = Math.abs(aint3[0] - aint[0]);
				int l = Math.abs(aint3[2] - aint[2]);
				int i1 = Math.max(k, l);

				if (i1 > 0) {
					if (k == i1) {
						b5 = 4;
					} else if (l == i1) {
						b5 = 8;
					}
				}

				setTypeAndData(world, aint3[0], aint3[1], aint3[2], block, b5);
			}
		}
	}

	void b() {
		int i = 0;

		for (int j = o.length; i < j; ++i) {
			int k = o[i][0];
			int l = o[i][1];
			int i1 = o[i][2];

			this.a(k, l, i1);
		}
	}

	boolean c(int i) {
		return i >= e * 0.2D;
	}

	void c() {
		int i = d[0];
		int j = d[1];
		int k = d[1] + f;
		int l = d[2];
		int[] aint = new int[] { i, j, l };
		int[] aint1 = new int[] { i, k, l };

		this.a(aint, aint1, Blocks.LOG);
		if (this.l == 2) {
			++aint[0];
			++aint1[0];
			this.a(aint, aint1, Blocks.LOG);
			++aint[2];
			++aint1[2];
			this.a(aint, aint1, Blocks.LOG);
			aint[0] += -1;
			aint1[0] += -1;
			this.a(aint, aint1, Blocks.LOG);
		}
	}

	void d() {
		int i = 0;
		int j = o.length;

		for (int[] aint = new int[] { d[0], d[1], d[2] }; i < j; ++i) {
			int[] aint1 = o[i];
			int[] aint2 = new int[] { aint1[0], aint1[1], aint1[2] };

			aint[1] = aint1[3];
			int k = aint[1] - d[1];

			if (this.c(k)) {
				this.a(aint, aint2, Blocks.LOG);
			}
		}
	}

	int a(int[] aint, int[] aint1) {
		int[] aint2 = new int[] { 0, 0, 0 };
		byte b0 = 0;

		byte b1;

		for (b1 = 0; b0 < 3; ++b0) {
			aint2[b0] = aint1[b0] - aint[b0];
			if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
				b1 = b0;
			}
		}

		if (aint2[b1] == 0)
			return -1;
		else {
			byte b2 = a[b1];
			byte b3 = a[b1 + 3];
			byte b4;

			if (aint2[b1] > 0) {
				b4 = 1;
			} else {
				b4 = -1;
			}

			double d0 = (double) aint2[b2] / (double) aint2[b1];
			double d1 = (double) aint2[b3] / (double) aint2[b1];
			int[] aint3 = new int[] { 0, 0, 0 };
			int i = 0;

			int j;

			for (j = aint2[b1] + b4; i != j; i += b4) {
				aint3[b1] = aint[b1] + i;
				aint3[b2] = MathHelper.floor(aint[b2] + i * d0);
				aint3[b3] = MathHelper.floor(aint[b3] + i * d1);
				Block block = world.getType(aint3[0], aint3[1], aint3[2]);

				if (!this.a(block) || aint[1] >= 256) { // CraftBukkit - fix trees wrapping around
					break;
				}
			}

			return i == j ? -1 : Math.abs(i);
		}
	}

	boolean e() {
		int[] aint = new int[] { d[0], d[1], d[2] };
		int[] aint1 = new int[] { d[0], d[1] + e - 1, d[2] };
		Block block = world.getType(d[0], d[1] - 1, d[2]);

		if (block != Blocks.DIRT && block != Blocks.GRASS && block != Blocks.SOIL)
			return false;
		else {
			int i = this.a(aint, aint1);

			if (i == -1)
				return true;
			else if (i < 6)
				return false;
			else {
				e = i;
				return true;
			}
		}
	}

	@Override
	public void a(double d0, double d1, double d2) {
		m = (int) (d0 * 12.0D);
		if (d0 > 0.5D) {
			n = 5;
		}

		j = d1;
		k = d2;
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		this.world = world;
		long l = random.nextLong();

		b.setSeed(l);
		d[0] = i;
		d[1] = j;
		d[2] = k;
		if (e == 0) {
			e = 5 + b.nextInt(m);
		}

		if (!e())
			return false;
		else {
			this.a();
			this.b();
			this.c();
			d();
			return true;
		}
	}
}
