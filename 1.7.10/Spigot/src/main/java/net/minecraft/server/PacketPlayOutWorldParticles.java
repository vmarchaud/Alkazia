package net.minecraft.server;

import java.io.IOException;
import java.util.HashMap; // Spigot

public class PacketPlayOutWorldParticles extends Packet {

	private String a;
	private float b;
	private float c;
	private float d;
	private float e;
	private float f;
	private float g;
	private float h;
	private int i;

	public PacketPlayOutWorldParticles() {
	}

	public PacketPlayOutWorldParticles(String s, float f, float f1, float f2, float f3, float f4, float f5, float f6, int i) {
		a = s;
		b = f;
		c = f1;
		d = f2;
		e = f3;
		this.f = f4;
		g = f5;
		h = f6;
		this.i = i;
	}

	@Override
	public void a(PacketDataSerializer packetdataserializer) throws IOException {
		a = packetdataserializer.c(64);
		b = packetdataserializer.readFloat();
		c = packetdataserializer.readFloat();
		d = packetdataserializer.readFloat();
		e = packetdataserializer.readFloat();
		f = packetdataserializer.readFloat();
		g = packetdataserializer.readFloat();
		h = packetdataserializer.readFloat();
		i = packetdataserializer.readInt();
	}

	// Spigot start - protocol patch
	@Override
	public void b(PacketDataSerializer packetdataserializer) throws IOException {
		String[] parts = a.split("_");
		Particle particle = Particle.find(parts[0]);
		if (particle == null) {
			particle = Particle.CRIT;
		}
		if (packetdataserializer.version < 17) {
			packetdataserializer.a(a);
		} else {
			packetdataserializer.writeInt(particle.ordinal());
			packetdataserializer.writeBoolean(false);
		}
		packetdataserializer.writeFloat(b);
		packetdataserializer.writeFloat(c);
		packetdataserializer.writeFloat(d);
		packetdataserializer.writeFloat(e);
		packetdataserializer.writeFloat(f);
		packetdataserializer.writeFloat(g);
		packetdataserializer.writeFloat(h);
		packetdataserializer.writeInt(i);
		if (packetdataserializer.version >= 17) {
			for (int i = 0; i < particle.extra; i++) {
				int toWrite = 0;
				if (parts.length - 1 > i) {
					try {
						toWrite = Integer.parseInt(parts[i + 1]);
						if (particle.extra == 1 && parts.length == 3) {
							i++;
							toWrite = toWrite | Integer.parseInt(parts[i + 1]) << 12;
						}
					} catch (NumberFormatException e) {

					}
				}
				packetdataserializer.b(toWrite);
			}
		}
	}

	// Spigot end

	public void a(PacketPlayOutListener packetplayoutlistener) {
		packetplayoutlistener.a(this);
	}

	@Override
	public void handle(PacketListener packetlistener) {
		this.a((PacketPlayOutListener) packetlistener);
	}

	// Spigot start - protocol patch
	private enum Particle {
		EXPLOSION_NORMAL("explode"), EXPLOSION_LARGE("largeexplode"), EXPLOSION_HUGE("hugeexplosion"), FIREWORKS_SPARK("fireworksSpark"), WATER_BUBBLE("bubble"), WATER_SPLASH("splash"), WATER_WAKE("wake"), SUSPENDED("suspended"), SUSPENDED_DEPTH("depthsuspend"), CRIT("crit"), CRIT_MAGIC("magicCrit"), SMOKE_NORMAL("smoke"), SMOKE_LARGE("largesmoke"), SPELL("spell"), SPELL_INSTANT("instantSpell"), SPELL_MOB(
				"mobSpell"), SPELL_MOB_AMBIENT("mobSpellAmbient"), SPELL_WITCH("witchMagic"), DRIP_WATER("dripWater"), DRIP_LAVA("dripLava"), VILLAGER_ANGRY("angryVillager"), VILLAGER_HAPPY("happyVillager"), TOWN_AURA("townaura"), NOTE("note"), PORTAL("portal"), ENCHANTMENT_TABLE("enchantmenttable"), FLAME("flame"), LAVA("lava"), FOOTSTEP("footstep"), CLOUD("cloud"), REDSTONE("reddust"), SNOWBALL(
				"snowballpoof"), SNOW_SHOVEL("snowshovel"), SLIME("slime"), HEART("heart"), BARRIER("barrier"), ICON_CRACK("iconcrack", 2), BLOCK_CRACK("blockcrack", 1), BLOCK_DUST("blockdust", 1), WATER_DROP("droplet"), ITEM_TAKE("take"), MOB_APPEARANCE("mobappearance");

		public final String name;
		public final int extra;
		private final static HashMap<String, Particle> particleMap = new HashMap<String, Particle>();

		Particle(String name) {
			this(name, 0);
		}

		Particle(String name, int extra) {
			this.name = name;
			this.extra = extra;
		}

		public static Particle find(String part) {
			return particleMap.get(part);
		}

		static {
			for (Particle particle : values()) {
				particleMap.put(particle.name, particle);
			}
		}
	}
	// Spigot end
}
