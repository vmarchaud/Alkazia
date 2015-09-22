package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
// CraftBukkit end
// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Explosion {

	public boolean a;
	public boolean b = true;
	private int i = 16;
	private Random j = new Random();
	private World world;
	public double posX;
	public double posY;
	public double posZ;
	public Entity source;
	public float size;
	public List blocks = new ArrayList();
	private Map l = new HashMap();
	public boolean wasCanceled = false; // CraftBukkit - add field

	public Explosion(World world, Entity entity, double d0, double d1, double d2, float f) {
		this.world = world;
		source = entity;
		size = (float) Math.max(f, 0.0); // CraftBukkit - clamp bad values
		posX = d0;
		posY = d1;
		posZ = d2;
	}

	public void a() {
		// CraftBukkit start
		if (size < 0.1F)
			return;

		float f = size;
		HashSet hashset = new HashSet();

		int i;
		int j;
		int k;
		double d0;
		double d1;
		double d2;

		for (i = 0; i < this.i; ++i) {
			for (j = 0; j < this.i; ++j) {
				for (k = 0; k < this.i; ++k) {
					if (i == 0 || i == this.i - 1 || j == 0 || j == this.i - 1 || k == 0 || k == this.i - 1) {
						double d3 = i / (this.i - 1.0F) * 2.0F - 1.0F;
						double d4 = j / (this.i - 1.0F) * 2.0F - 1.0F;
						double d5 = k / (this.i - 1.0F) * 2.0F - 1.0F;
						double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

						d3 /= d6;
						d4 /= d6;
						d5 /= d6;
						float f1 = size * (0.7F + world.random.nextFloat() * 0.6F);

						d0 = posX;
						d1 = posY;
						d2 = posZ;

						for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
							int l = MathHelper.floor(d0);
							int i1 = MathHelper.floor(d1);
							int j1 = MathHelper.floor(d2);
							Block block = world.getType(l, i1, j1);

							if (block.getMaterial() != Material.AIR) {
								float f3 = source != null ? source.a(this, world, l, i1, j1, block) : block.a(source);

								f1 -= (f3 + 0.3F) * f2;
							}

							if (f1 > 0.0F && (source == null || source.a(this, world, l, i1, j1, block, f1)) && i1 < 256 && i1 >= 0) { // CraftBukkit - don't wrap explosions
								hashset.add(new ChunkPosition(l, i1, j1));
							}

							d0 += d3 * f2;
							d1 += d4 * f2;
							d2 += d5 * f2;
						}
					}
				}
			}
		}

		blocks.addAll(hashset);
		size *= 2.0F;
		i = MathHelper.floor(posX - size - 1.0D);
		j = MathHelper.floor(posX + size + 1.0D);
		k = MathHelper.floor(posY - size - 1.0D);
		int k1 = MathHelper.floor(posY + size + 1.0D);
		int l1 = MathHelper.floor(posZ - size - 1.0D);
		int i2 = MathHelper.floor(posZ + size + 1.0D);
		List list = world.getEntities(source, AxisAlignedBB.a(i, k, l1, j, k1, i2));
		Vec3D vec3d = Vec3D.a(posX, posY, posZ);

		for (int j2 = 0; j2 < list.size(); ++j2) {
			Entity entity = (Entity) list.get(j2);
			double d7 = entity.f(posX, posY, posZ) / size;

			if (d7 <= 1.0D) {
				d0 = entity.locX - posX;
				d1 = entity.locY + entity.getHeadHeight() - posY;
				d2 = entity.locZ - posZ;
				double d8 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

				if (d8 != 0.0D) {
					d0 /= d8;
					d1 /= d8;
					d2 /= d8;
					double d9 = world.a(vec3d, entity.boundingBox);
					double d10 = (1.0D - d7) * d9;

					// CraftBukkit start
					CraftEventFactory.entityDamage = source;
					if (!entity.damageEntity(DamageSource.explosion(this), (int) ((d10 * d10 + d10) / 2.0D * 8.0D * size + 1.0D))) {

					}
					CraftEventFactory.entityDamage = null;
					// CraftBukkit end
					double d11 = EnchantmentProtection.a(entity, d10);

					entity.motX += d0 * d11;
					entity.motY += d1 * d11;
					entity.motZ += d2 * d11;
					if (entity instanceof EntityHuman) {
						l.put(entity, Vec3D.a(d0 * d10, d1 * d10, d2 * d10));
					}
				}
			}
		}

		size = f;
	}

	public void a(boolean flag) {
		world.makeSound(posX, posY, posZ, "random.explode", 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
		if (size >= 2.0F && b) {
			world.addParticle("hugeexplosion", posX, posY, posZ, 1.0D, 0.0D, 0.0D);
		} else {
			world.addParticle("largeexplode", posX, posY, posZ, 1.0D, 0.0D, 0.0D);
		}

		Iterator iterator;
		ChunkPosition chunkposition;
		int i;
		int j;
		int k;
		Block block;

		if (b) {
			// CraftBukkit start
			org.bukkit.World bworld = world.getWorld();
			org.bukkit.entity.Entity explode = source == null ? null : source.getBukkitEntity();
			Location location = new Location(bworld, posX, posY, posZ);

			List<org.bukkit.block.Block> blockList = new ArrayList<org.bukkit.block.Block>();
			for (int i1 = blocks.size() - 1; i1 >= 0; i1--) {
				ChunkPosition cpos = (ChunkPosition) blocks.get(i1);
				org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.x, cpos.y, cpos.z);
				if (bblock.getType() != org.bukkit.Material.AIR) {
					blockList.add(bblock);
				}
			}

			EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, 0.3F);
			world.getServer().getPluginManager().callEvent(event);

			blocks.clear();

			for (org.bukkit.block.Block bblock : event.blockList()) {
				ChunkPosition coords = new ChunkPosition(bblock.getX(), bblock.getY(), bblock.getZ());
				blocks.add(coords);
			}

			if (event.isCancelled()) {
				wasCanceled = true;
				return;
			}
			// CraftBukkit end

			iterator = blocks.iterator();

			while (iterator.hasNext()) {
				chunkposition = (ChunkPosition) iterator.next();
				i = chunkposition.x;
				j = chunkposition.y;
				k = chunkposition.z;
				block = world.getType(i, j, k);
				world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, i, j, k); // Spigot
				if (flag) {
					double d0 = i + world.random.nextFloat();
					double d1 = j + world.random.nextFloat();
					double d2 = k + world.random.nextFloat();
					double d3 = d0 - posX;
					double d4 = d1 - posY;
					double d5 = d2 - posZ;
					double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / size + 0.1D);

					d7 *= world.random.nextFloat() * world.random.nextFloat() + 0.3F;
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					world.addParticle("explode", (d0 + posX * 1.0D) / 2.0D, (d1 + posY * 1.0D) / 2.0D, (d2 + posZ * 1.0D) / 2.0D, d3, d4, d5);
					world.addParticle("smoke", d0, d1, d2, d3, d4, d5);
				}

				if (block.getMaterial() != Material.AIR) {
					if (block.a(this)) {
						// CraftBukkit - add yield
						block.dropNaturally(world, i, j, k, world.getData(i, j, k), event.getYield(), 0);
					}

					world.setTypeAndData(i, j, k, Blocks.AIR, 0, 3);
					block.wasExploded(world, i, j, k, this);
				}
			}
		}

		if (a) {
			iterator = blocks.iterator();

			while (iterator.hasNext()) {
				chunkposition = (ChunkPosition) iterator.next();
				i = chunkposition.x;
				j = chunkposition.y;
				k = chunkposition.z;
				block = world.getType(i, j, k);
				Block block1 = world.getType(i, j - 1, k);

				if (block.getMaterial() == Material.AIR && block1.j() && this.j.nextInt(3) == 0) {
					// CraftBukkit start - Ignition by explosion
					if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, i, j, k, this).isCancelled()) {
						world.setTypeUpdate(i, j, k, Blocks.FIRE);
					}
					// CraftBukkit end
				}
			}
		}
	}

	public Map b() {
		return l;
	}

	public EntityLiving c() {
		return source == null ? null : source instanceof EntityTNTPrimed ? ((EntityTNTPrimed) source).getSource() : source instanceof EntityLiving ? (EntityLiving) source : null;
	}
}
