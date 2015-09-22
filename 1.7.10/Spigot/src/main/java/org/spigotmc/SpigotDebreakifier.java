package org.spigotmc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.Item;
import net.minecraft.server.Items;
import net.minecraft.util.gnu.trove.map.hash.TIntIntHashMap;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SpigotDebreakifier {

	private static final boolean[] validBlocks = new boolean[198 << 4];
	private static final int[] correctedValues = new int[198];

	static {
		Arrays.fill(correctedValues, -1);
		InputStream in = SpigotDebreakifier.class.getResourceAsStream("/blocks.json");
		try {
			JsonArray e = new JsonParser().parse(new InputStreamReader(in, Charsets.UTF_8)).getAsJsonArray();
			for (JsonElement entry : e) {
				String[] parts = entry.getAsString().split(":");
				int id = Integer.parseInt(parts[0]);
				int data = Integer.parseInt(parts[1]);
				validBlocks[id << 4 | data] = true;
				if (correctedValues[id] == -1 || data < correctedValues[id]) {
					correctedValues[id] = data;
				}
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static int getCorrectedData(int id, int data) {
		if (id > 197)
			return data;
		if (id == 175 && data > 8) {
			data = 8;
		}
		if (validBlocks[id << 4 | data])
			return data;
		else
			return correctedValues[id] & 0xF;
	}

	private static TIntIntHashMap invalidItems = new TIntIntHashMap();
	static {
		replace(Blocks.WATER, Items.WATER_BUCKET);
		replace(Blocks.STATIONARY_WATER, Items.WATER_BUCKET);
		replace(Blocks.LAVA, Items.LAVA_BUCKET);
		replace(Blocks.STATIONARY_LAVA, Items.LAVA_BUCKET);
		replace(Blocks.PORTAL, Items.NETHER_BRICK);
		replace(Blocks.DOUBLE_STEP, Blocks.STEP);
		replace(Blocks.FIRE, Items.FLINT_AND_STEEL);
		replace(Blocks.ENDER_PORTAL, Blocks.ENDER_PORTAL_FRAME);
		replace(Blocks.WOOD_DOUBLE_STEP, Blocks.WOOD_STEP);
		replace(Blocks.COCOA, Items.SEEDS);
		replace(Blocks.CARROTS, Items.CARROT);
		replace(Blocks.POTATOES, Items.POTATO);
	}

	public static int getItemId(int id) {
		return invalidItems.containsKey(id) ? invalidItems.get(id) : id;
	}

	private static void replace(Block block, Block other) {
		replace(Block.getId(block), Block.getId(other));
	}

	private static void replace(Block block, Item other) {
		replace(Block.getId(block), Item.getId(other));
	}

	private static void replace(int block, int other) {
		invalidItems.put(block, other);
	}
}
