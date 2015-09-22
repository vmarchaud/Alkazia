package net.minecraft.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WorldGenLargeFeature extends StructureGenerator {

	private static List e = Arrays.asList(new BiomeBase[] { BiomeBase.DESERT, BiomeBase.DESERT_HILLS, BiomeBase.JUNGLE, BiomeBase.JUNGLE_HILLS, BiomeBase.SWAMPLAND });
	private List f;
	private int g;
	private int h;

	public WorldGenLargeFeature() {
		f = new ArrayList();
		g = 32;
		h = 8;
		f.add(new BiomeMeta(EntityWitch.class, 1, 1, 1));
	}

	public WorldGenLargeFeature(Map map) {
		this();
		Iterator iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();

			if (((String) entry.getKey()).equals("distance")) {
				g = MathHelper.a((String) entry.getValue(), g, h + 1);
			}
		}
	}

	@Override
	public String a() {
		return "Temple";
	}

	@Override
	protected boolean a(int i, int j) {
		int k = i;
		int l = j;

		if (i < 0) {
			i -= g - 1;
		}

		if (j < 0) {
			j -= g - 1;
		}

		int i1 = i / g;
		int j1 = j / g;
		Random random = c.A(i1, j1, c.spigotConfig.largeFeatureSeed); // Spigot

		i1 *= g;
		j1 *= g;
		i1 += random.nextInt(g - h);
		j1 += random.nextInt(g - h);
		if (k == i1 && l == j1) {
			BiomeBase biomebase = c.getWorldChunkManager().getBiome(k * 16 + 8, l * 16 + 8);
			Iterator iterator = e.iterator();

			while (iterator.hasNext()) {
				BiomeBase biomebase1 = (BiomeBase) iterator.next();

				if (biomebase == biomebase1)
					return true;
			}
		}

		return false;
	}

	@Override
	protected StructureStart b(int i, int j) {
		return new WorldGenLargeFeatureStart(c, b, i, j);
	}

	public boolean a(int i, int j, int k) {
		StructureStart structurestart = c(i, j, k);

		if (structurestart != null && structurestart instanceof WorldGenLargeFeatureStart && !structurestart.a.isEmpty()) {
			StructurePiece structurepiece = (StructurePiece) structurestart.a.getFirst();

			return structurepiece instanceof WorldGenWitchHut;
		} else
			return false;
	}

	public List b() {
		return f;
	}
}
