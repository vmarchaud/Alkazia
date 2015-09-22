package net.minecraft.server;

import java.util.Random;

public class BiomeDecorator {

	protected World a;
	protected Random b;
	protected int c;
	protected int d;
	protected WorldGenerator e = new WorldGenClay(4);
	protected WorldGenerator f;
	protected WorldGenerator g;
	protected WorldGenerator h;
	protected WorldGenerator i;
	protected WorldGenerator j;
	protected WorldGenerator k;
	protected WorldGenerator l;
	protected WorldGenerator m;
	protected WorldGenerator n;
	protected WorldGenerator o;
	protected WorldGenFlowers p;
	protected WorldGenerator q;
	protected WorldGenerator r;
	protected WorldGenerator s;
	protected WorldGenerator t;
	protected WorldGenerator u;
	protected WorldGenerator v;
	protected int w;
	protected int x;
	protected int y;
	protected int z;
	protected int A;
	protected int B;
	protected int C;
	protected int D;
	protected int E;
	protected int F;
	protected int G;
	protected int H;
	public boolean I;
    /** Alkazia - Declare ore */
    protected WorldGenerator oreBauxite;
    protected WorldGenerator oreGranite;
    protected WorldGenerator oreOpale;
    

    protected WorldGenerator alumite;
    protected WorldGenerator andesite;
    protected WorldGenerator diorite;

	public BiomeDecorator() {
		f = new WorldGenSand(Blocks.SAND, 7);
		g = new WorldGenSand(Blocks.GRAVEL, 6);
		h = new WorldGenMinable(Blocks.DIRT, 32);
		i = new WorldGenMinable(Blocks.GRAVEL, 32);
		j = new WorldGenMinable(Blocks.COAL_ORE, 16);
		k = new WorldGenMinable(Blocks.IRON_ORE, 8);
		l = new WorldGenMinable(Blocks.GOLD_ORE, 8);
		m = new WorldGenMinable(Blocks.REDSTONE_ORE, 7);
		n = new WorldGenMinable(Blocks.DIAMOND_ORE, 7);
		o = new WorldGenMinable(Blocks.LAPIS_ORE, 6);
        /** Alkazia */
        this.oreBauxite = new WorldGenMinable(Blocks.BAUXITE_ORE, 8);
        this.oreGranite = new WorldGenMinable(Blocks.GRANITE_ORE, 7);
        this.oreOpale = new WorldGenMinable(Blocks.OPALE_ORE, 6);
        
        this.alumite = new WorldGenMinable(Blocks.alumite, 20);
        this.diorite = new WorldGenMinable(Blocks.diorite, 20);
        this.andesite = new WorldGenMinable(Blocks.andesite, 20);
		p = new WorldGenFlowers(Blocks.YELLOW_FLOWER);
		q = new WorldGenFlowers(Blocks.BROWN_MUSHROOM);
		r = new WorldGenFlowers(Blocks.RED_MUSHROOM);
		s = new WorldGenHugeMushroom();
		t = new WorldGenReed();
		u = new WorldGenCactus();
		v = new WorldGenWaterLily();
		y = 2;
		z = 1;
		E = 1;
		F = 3;
		G = 1;
		I = true;
	}

	public void a(World world, Random random, BiomeBase biomebase, int i, int j) {
		if (a != null)
			throw new RuntimeException("Already decorating!!");
		else {
			a = world;
			b = random;
			c = i;
			d = j;
			this.a(biomebase);
			a = null;
			b = null;
		}
	}

	protected void a(BiomeBase biomebase) {
		this.a();

		int i;
		int j;
		int k;

		for (i = 0; i < F; ++i) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			f.generate(a, b, j, a.i(j, k), k);
		}

		for (i = 0; i < G; ++i) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			e.generate(a, b, j, a.i(j, k), k);
		}

		for (i = 0; i < E; ++i) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			g.generate(a, b, j, a.i(j, k), k);
		}

		i = x;
		if (b.nextInt(10) == 0) {
			++i;
		}

		int l;
		int i1;

		for (j = 0; j < i; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = a.getHighestBlockYAt(k, l);
			WorldGenTreeAbstract worldgentreeabstract = biomebase.a(b);

			worldgentreeabstract.a(1.0D, 1.0D, 1.0D);
			if (worldgentreeabstract.generate(a, b, k, i1, l)) {
				worldgentreeabstract.b(a, b, k, i1, l);
			}
		}

		for (j = 0; j < H; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			s.generate(a, b, k, a.getHighestBlockYAt(k, l), l);
		}

		for (j = 0; j < y; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(a.getHighestBlockYAt(k, l) + 32);
			String s = biomebase.a(b, k, i1, l);
			BlockFlowers blockflowers = BlockFlowers.e(s);

			if (blockflowers.getMaterial() != Material.AIR) {
				p.a(blockflowers, BlockFlowers.f(s));
				p.generate(a, b, k, i1, l);
			}
		}

		for (j = 0; j < z; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
			WorldGenerator worldgenerator = biomebase.b(b);

			worldgenerator.generate(a, b, k, i1, l);
		}

		for (j = 0; j < A; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
			new WorldGenDeadBush(Blocks.DEAD_BUSH).generate(a, b, k, i1, l);
		}

		for (j = 0; j < w; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;

			for (i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); i1 > 0 && a.isEmpty(k, i1 - 1, l); --i1) { // Spigot
				;
			}

			v.generate(a, b, k, i1, l);
		}

		for (j = 0; j < B; ++j) {
			if (b.nextInt(4) == 0) {
				k = c + b.nextInt(16) + 8;
				l = d + b.nextInt(16) + 8;
				i1 = a.getHighestBlockYAt(k, l);
				q.generate(a, b, k, i1, l);
			}

			if (b.nextInt(8) == 0) {
				k = c + b.nextInt(16) + 8;
				l = d + b.nextInt(16) + 8;
				i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
				r.generate(a, b, k, i1, l);
			}
		}

		if (b.nextInt(4) == 0) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			l = b.nextInt(getHighestBlockYAt(j, k) * 2); // Spigot
			q.generate(a, b, j, l, k);
		}

		if (b.nextInt(8) == 0) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			l = b.nextInt(getHighestBlockYAt(j, k) * 2); // Spigot
			r.generate(a, b, j, l, k);
		}

		for (j = 0; j < C; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
			t.generate(a, b, k, i1, l);
		}

		for (j = 0; j < 10; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
			t.generate(a, b, k, i1, l);
		}

		if (b.nextInt(32) == 0) {
			j = c + b.nextInt(16) + 8;
			k = d + b.nextInt(16) + 8;
			l = b.nextInt(getHighestBlockYAt(j, k) * 2); // Spigot
			new WorldGenPumpkin().generate(a, b, j, l, k);
		}

		for (j = 0; j < D; ++j) {
			k = c + b.nextInt(16) + 8;
			l = d + b.nextInt(16) + 8;
			i1 = b.nextInt(getHighestBlockYAt(k, l) * 2); // Spigot
			u.generate(a, b, k, i1, l);
		}

		if (I) {
			for (j = 0; j < 50; ++j) {
				k = c + b.nextInt(16) + 8;
				l = b.nextInt(b.nextInt(248) + 8);
				i1 = d + b.nextInt(16) + 8;
				new WorldGenLiquids(Blocks.WATER).generate(a, b, k, l, i1);
			}

			for (j = 0; j < 20; ++j) {
				k = c + b.nextInt(16) + 8;
				l = b.nextInt(b.nextInt(b.nextInt(240) + 8) + 8);
				i1 = d + b.nextInt(16) + 8;
				new WorldGenLiquids(Blocks.LAVA).generate(a, b, k, l, i1);
			}
		}
	}

	protected void a(int i, WorldGenerator worldgenerator, int j, int k) {
		for (int l = 0; l < i; ++l) {
			int i1 = c + b.nextInt(16);
			int j1 = b.nextInt(k - j) + j;
			int k1 = d + b.nextInt(16);

			worldgenerator.generate(a, b, i1, j1, k1);
		}
	}

	protected void b(int i, WorldGenerator worldgenerator, int j, int k) {
		for (int l = 0; l < i; ++l) {
			int i1 = c + b.nextInt(16);
			int j1 = b.nextInt(k) + b.nextInt(k) + j - k;
			int k1 = d + b.nextInt(16);

			worldgenerator.generate(a, b, i1, j1, k1);
		}
	}

	protected void a() {
		this.a(20, h, 0, 256);
		this.a(10, i, 0, 256);
		this.a(20, j, 0, 128);
		this.a(20, k, 0, 64);
		this.a(2, l, 0, 32);
		this.a(8, m, 0, 16);
		this.a(1, n, 0, 16);
		b(1, o, 16, 16);
        this.a(8, oreBauxite, 0, 64);
        this.a(3, oreGranite, 0, 32);
        this.a(2, oreOpale, 0, 16);

        this.a(10, alumite, 40, 256);
        this.a(10, andesite, 40, 256);
        this.a(10, diorite, 40, 256);
	}

	// Spigot Start
	private int getHighestBlockYAt(int x, int z) {
		return Math.max(1, a.getHighestBlockYAt(x, z));
	}
	// Spigot End
}
