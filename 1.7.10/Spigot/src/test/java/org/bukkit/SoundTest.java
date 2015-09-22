package org.bukkit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.bukkit.craftbukkit.CraftSound;
import org.junit.Test;

public class SoundTest {

	@Test
	public void testGetSound() {
		for (Sound sound : Sound.values()) {
			assertThat(sound.name(), CraftSound.getSound(sound), is(not(nullValue())));
		}
	}
}
