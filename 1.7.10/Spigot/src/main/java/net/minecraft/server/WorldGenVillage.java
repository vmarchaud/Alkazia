package net.minecraft.server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WorldGenVillage extends StructureGenerator {

	public static final List e = Arrays.asList(new BiomeBase[] { BiomeBase.PLAINS, BiomeBase.DESERT, BiomeBase.SAVANNA });
	private int f;
	private int g;
	private int h;

	public WorldGenVillage() {
		g = 32;
		h = 8;
	}

	public WorldGenVillage(Map map) {
		this();
		Iterator iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();

			if (((String) entry.getKey()).equals("size")) {
				f = MathHelper.a((String) entry.getValue(), f, 0);
			} else if (((String) entry.getKey()).equals("distance")) {
				g = MathHelper.a((String) entry.getValue(), g, h + 1);
			}
		}
	}

	@Override
	public String a() {
		return "Village";
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
		Random random = c.A(i1, j1, c.spigotConfig.villageSeed); // Spigot

		i1 *= g;
		j1 *= g;
		i1 += random.nextInt(g - h);
		j1 += random.nextInt(g - h);
		if (k == i1 && l == j1) {
			boolean flag = c.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 0, e);

			if (flag)
				return true;
		}

		return false;
	}

	@Override
	protected StructureStart b(int i, int j) {
		return new WorldGenVillageStart(c, b, i, j, f);
	}
}
