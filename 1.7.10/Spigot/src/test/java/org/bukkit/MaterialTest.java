package org.bukkit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.Item;

import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import com.google.common.collect.Maps;

public class MaterialTest extends AbstractTestingBase {

	@Test
	public void verifyMapping() {
		Map<Integer, Material> materials = Maps.newHashMap();
		for (Material material : Material.values()) {
			if (INVALIDATED_MATERIALS.contains(material)) {
				continue;
			}

			materials.put(material.getId(), material);
		}
		materials.remove(0); // Purge air.

		Iterator<Item> items = Item.REGISTRY.iterator();

		while (items.hasNext()) {
			Item item = items.next();
			if (item == null) {
				continue;
			}

			int id = CraftMagicNumbers.getId(item);
			String name = item.getName();

			Material material = materials.remove(id);

			assertThat("Missing " + name + "(" + id + ")", material, is(not(nullValue())));
		}

		assertThat(materials, is(Collections.EMPTY_MAP));
	}
}
