package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
// CraftBukkit end
// CraftBukkit start
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityEnderDragon extends EntityInsentient implements IComplex, IMonster {

	public double h;
	public double i;
	public double bm;
	public double[][] bn = new double[64][3];
	public int bo = -1;
	public EntityComplexPart[] children;
	public EntityComplexPart bq;
	public EntityComplexPart br;
	public EntityComplexPart bs;
	public EntityComplexPart bt;
	public EntityComplexPart bu;
	public EntityComplexPart bv;
	public EntityComplexPart bw;
	public float bx;
	public float by;
	public boolean bz;
	public boolean bA;
	private Entity bD;
	public int bB;
	public EntityEnderCrystal bC;
	private Explosion explosionSource = new Explosion(null, this, Double.NaN, Double.NaN, Double.NaN, Float.NaN); // CraftBukkit - reusable source for CraftTNTPrimed.getSource()

	public EntityEnderDragon(World world) {
		super(world);
		children = new EntityComplexPart[] { bq = new EntityComplexPart(this, "head", 6.0F, 6.0F), br = new EntityComplexPart(this, "body", 8.0F, 8.0F), bs = new EntityComplexPart(this, "tail", 4.0F, 4.0F), bt = new EntityComplexPart(this, "tail", 4.0F, 4.0F), bu = new EntityComplexPart(this, "tail", 4.0F, 4.0F), bv = new EntityComplexPart(this, "wing", 4.0F, 4.0F),
				bw = new EntityComplexPart(this, "wing", 4.0F, 4.0F) };
		setHealth(getMaxHealth());
		this.a(16.0F, 8.0F);
		X = true;
		fireProof = true;
		i = 100.0D;
		ak = true;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(200.0D);
	}

	@Override
	protected void c() {
		super.c();
	}

	public double[] b(int i, float f) {
		if (getHealth() <= 0.0F) {
			f = 0.0F;
		}

		f = 1.0F - f;
		int j = bo - i * 1 & 63;
		int k = bo - i * 1 - 1 & 63;
		double[] adouble = new double[3];
		double d0 = bn[j][0];
		double d1 = MathHelper.g(bn[k][0] - d0);

		adouble[0] = d0 + d1 * f;
		d0 = bn[j][1];
		d1 = bn[k][1] - d0;
		adouble[1] = d0 + d1 * f;
		adouble[2] = bn[j][2] + (bn[k][2] - bn[j][2]) * f;
		return adouble;
	}

	@Override
	public void e() {
		float f;
		float f1;

		if (world.isStatic) {
			f = MathHelper.cos(by * 3.1415927F * 2.0F);
			f1 = MathHelper.cos(bx * 3.1415927F * 2.0F);
			if (f1 <= -0.3F && f >= -0.3F) {
				world.a(locX, locY, locZ, "mob.enderdragon.wings", 5.0F, 0.8F + random.nextFloat() * 0.3F, false);
			}
		}

		bx = by;
		float f2;

		if (getHealth() <= 0.0F) {
			f = (random.nextFloat() - 0.5F) * 8.0F;
			f1 = (random.nextFloat() - 0.5F) * 4.0F;
			f2 = (random.nextFloat() - 0.5F) * 8.0F;
			world.addParticle("largeexplode", locX + f, locY + 2.0D + f1, locZ + f2, 0.0D, 0.0D, 0.0D);
		} else {
			bP();
			f = 0.2F / (MathHelper.sqrt(motX * motX + motZ * motZ) * 10.0F + 1.0F);
			f *= (float) Math.pow(2.0D, motY);
			if (bA) {
				by += f * 0.5F;
			} else {
				by += f;
			}

			yaw = MathHelper.g(yaw);
			if (bo < 0) {
				for (int d05 = 0; d05 < bn.length; ++d05) {
					bn[d05][0] = yaw;
					bn[d05][1] = locY;
				}
			}

			if (++bo == bn.length) {
				bo = 0;
			}

			bn[bo][0] = yaw;
			bn[bo][1] = locY;
			double d0;
			double d1;
			double d2;
			double d3;
			float f3;

			if (world.isStatic) {
				if (bg > 0) {
					d0 = locX + (bh - locX) / bg;
					d1 = locY + (bi - locY) / bg;
					d2 = locZ + (bj - locZ) / bg;
					d3 = MathHelper.g(bk - yaw);
					yaw = (float) (yaw + d3 / bg);
					pitch = (float) (pitch + (bl - pitch) / bg);
					--bg;
					setPosition(d0, d1, d2);
					this.b(yaw, pitch);
				}
			} else {
				d0 = h - locX;
				d1 = i - locY;
				d2 = bm - locZ;
				d3 = d0 * d0 + d1 * d1 + d2 * d2;
				if (bD != null) {
					h = bD.locX;
					bm = bD.locZ;
					double d4 = h - locX;
					double d5 = bm - locZ;
					double d6 = Math.sqrt(d4 * d4 + d5 * d5);
					double d7 = 0.4000000059604645D + d6 / 80.0D - 1.0D;

					if (d7 > 10.0D) {
						d7 = 10.0D;
					}

					i = bD.boundingBox.b + d7;
				} else {
					h += random.nextGaussian() * 2.0D;
					bm += random.nextGaussian() * 2.0D;
				}

				if (bz || d3 < 100.0D || d3 > 22500.0D || positionChanged || F) {
					bQ();
				}

				d1 /= MathHelper.sqrt(d0 * d0 + d2 * d2);
				f3 = 0.6F;
				if (d1 < -f3) {
					d1 = -f3;
				}

				if (d1 > f3) {
					d1 = f3;
				}

				motY += d1 * 0.10000000149011612D;
				yaw = MathHelper.g(yaw);
				double d8 = 180.0D - Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D;
				double d9 = MathHelper.g(d8 - yaw);

				if (d9 > 50.0D) {
					d9 = 50.0D;
				}

				if (d9 < -50.0D) {
					d9 = -50.0D;
				}

				Vec3D vec3d = Vec3D.a(h - locX, i - locY, bm - locZ).a();
				Vec3D vec3d1 = Vec3D.a(MathHelper.sin(yaw * 3.1415927F / 180.0F), motY, -MathHelper.cos(yaw * 3.1415927F / 180.0F)).a();
				float f4 = (float) (vec3d1.b(vec3d) + 0.5D) / 1.5F;

				if (f4 < 0.0F) {
					f4 = 0.0F;
				}

				bf *= 0.8F;
				float f5 = MathHelper.sqrt(motX * motX + motZ * motZ) * 1.0F + 1.0F;
				double d10 = Math.sqrt(motX * motX + motZ * motZ) * 1.0D + 1.0D;

				if (d10 > 40.0D) {
					d10 = 40.0D;
				}

				bf = (float) (bf + d9 * (0.699999988079071D / d10 / f5));
				yaw += bf * 0.1F;
				float f6 = (float) (2.0D / (d10 + 1.0D));
				float f7 = 0.06F;

				this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
				if (bA) {
					move(motX * 0.800000011920929D, motY * 0.800000011920929D, motZ * 0.800000011920929D);
				} else {
					move(motX, motY, motZ);
				}

				Vec3D vec3d2 = Vec3D.a(motX, motY, motZ).a();
				float f8 = (float) (vec3d2.b(vec3d1) + 1.0D) / 2.0F;

				f8 = 0.8F + 0.15F * f8;
				motX *= f8;
				motZ *= f8;
				motY *= 0.9100000262260437D;
			}

			aM = yaw;
			bq.width = bq.length = 3.0F;
			bs.width = bs.length = 2.0F;
			bt.width = bt.length = 2.0F;
			bu.width = bu.length = 2.0F;
			br.length = 3.0F;
			br.width = 5.0F;
			bv.length = 2.0F;
			bv.width = 4.0F;
			bw.length = 3.0F;
			bw.width = 4.0F;
			f1 = (float) (this.b(5, 1.0F)[1] - this.b(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
			f2 = MathHelper.cos(f1);
			float f9 = -MathHelper.sin(f1);
			float f10 = yaw * 3.1415927F / 180.0F;
			float f11 = MathHelper.sin(f10);
			float f12 = MathHelper.cos(f10);

			br.h();
			br.setPositionRotation(locX + f11 * 0.5F, locY, locZ - f12 * 0.5F, 0.0F, 0.0F);
			bv.h();
			bv.setPositionRotation(locX + f12 * 4.5F, locY + 2.0D, locZ + f11 * 4.5F, 0.0F, 0.0F);
			bw.h();
			bw.setPositionRotation(locX - f12 * 4.5F, locY + 2.0D, locZ - f11 * 4.5F, 0.0F, 0.0F);
			if (!world.isStatic && hurtTicks == 0) {
				this.a(world.getEntities(this, bv.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
				this.a(world.getEntities(this, bw.boundingBox.grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
				this.b(world.getEntities(this, bq.boundingBox.grow(1.0D, 1.0D, 1.0D)));
			}

			double[] adouble = this.b(5, 1.0F);
			double[] adouble1 = this.b(0, 1.0F);

			f3 = MathHelper.sin(yaw * 3.1415927F / 180.0F - bf * 0.01F);
			float f13 = MathHelper.cos(yaw * 3.1415927F / 180.0F - bf * 0.01F);

			bq.h();
			bq.setPositionRotation(locX + f3 * 5.5F * f2, locY + (adouble1[1] - adouble[1]) * 1.0D + f9 * 5.5F, locZ - f13 * 5.5F * f2, 0.0F, 0.0F);

			for (int j = 0; j < 3; ++j) {
				EntityComplexPart entitycomplexpart = null;

				if (j == 0) {
					entitycomplexpart = bs;
				}

				if (j == 1) {
					entitycomplexpart = bt;
				}

				if (j == 2) {
					entitycomplexpart = bu;
				}

				double[] adouble2 = this.b(12 + j * 2, 1.0F);
				float f14 = yaw * 3.1415927F / 180.0F + this.b(adouble2[0] - adouble[0]) * 3.1415927F / 180.0F * 1.0F;
				float f15 = MathHelper.sin(f14);
				float f16 = MathHelper.cos(f14);
				float f17 = 1.5F;
				float f18 = (j + 1) * 2.0F;

				entitycomplexpart.h();
				entitycomplexpart.setPositionRotation(locX - (f11 * f17 + f15 * f18) * f2, locY + (adouble2[1] - adouble[1]) * 1.0D - (f18 + f17) * f9 + 1.5D, locZ + (f12 * f17 + f16 * f18) * f2, 0.0F, 0.0F);
			}

			if (!world.isStatic) {
				bA = this.a(bq.boundingBox) | this.a(br.boundingBox);
			}
		}
	}

	private void bP() {
		if (bC != null) {
			if (bC.dead) {
				if (!world.isStatic) {
					CraftEventFactory.entityDamage = bC; // CraftBukkit
					this.a(bq, DamageSource.explosion((Explosion) null), 10.0F);
					CraftEventFactory.entityDamage = null; // CraftBukkit
				}

				bC = null;
			} else if (ticksLived % 10 == 0 && getHealth() < getMaxHealth()) {
				// CraftBukkit start
				EntityRegainHealthEvent event = new EntityRegainHealthEvent(getBukkitEntity(), 1.0D, EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
				world.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					setHealth((float) (getHealth() + event.getAmount()));
				}
				// CraftBukkit end
			}
		}

		if (random.nextInt(10) == 0) {
			float f = 32.0F;
			List list = world.a(EntityEnderCrystal.class, boundingBox.grow(f, f, f));
			EntityEnderCrystal entityendercrystal = null;
			double d0 = Double.MAX_VALUE;
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
				double d1 = entityendercrystal1.f(this);

				if (d1 < d0) {
					d0 = d1;
					entityendercrystal = entityendercrystal1;
				}
			}

			bC = entityendercrystal;
		}
	}

	private void a(List list) {
		double d0 = (br.boundingBox.a + br.boundingBox.d) / 2.0D;
		double d1 = (br.boundingBox.c + br.boundingBox.f) / 2.0D;
		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {
			Entity entity = (Entity) iterator.next();

			if (entity instanceof EntityLiving) {
				double d2 = entity.locX - d0;
				double d3 = entity.locZ - d1;
				double d4 = d2 * d2 + d3 * d3;

				entity.g(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
			}
		}
	}

	private void b(List list) {
		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);

			if (entity instanceof EntityLiving) {
				entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
			}
		}
	}

	private void bQ() {
		bz = false;
		if (random.nextInt(2) == 0 && !world.players.isEmpty()) {
			// CraftBukkit start
			Entity target = (Entity) world.players.get(random.nextInt(world.players.size()));
			EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(), target.getBukkitEntity(), EntityTargetEvent.TargetReason.RANDOM_TARGET);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				if (event.getTarget() == null) {
					bD = null;
				} else {
					bD = ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
				}
			}
			// CraftBukkit end
		} else {
			boolean flag = false;

			do {
				h = 0.0D;
				i = 70.0F + random.nextFloat() * 50.0F;
				bm = 0.0D;
				h += random.nextFloat() * 120.0F - 60.0F;
				bm += random.nextFloat() * 120.0F - 60.0F;
				double d0 = locX - h;
				double d1 = locY - i;
				double d2 = locZ - bm;

				flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
			} while (!flag);

			bD = null;
		}
	}

	private float b(double d0) {
		return (float) MathHelper.g(d0);
	}

	private boolean a(AxisAlignedBB axisalignedbb) {
		int i = MathHelper.floor(axisalignedbb.a);
		int j = MathHelper.floor(axisalignedbb.b);
		int k = MathHelper.floor(axisalignedbb.c);
		int l = MathHelper.floor(axisalignedbb.d);
		int i1 = MathHelper.floor(axisalignedbb.e);
		int j1 = MathHelper.floor(axisalignedbb.f);
		boolean flag = false;
		boolean flag1 = false;

		// CraftBukkit start - Create a list to hold all the destroyed blocks
		List<org.bukkit.block.Block> destroyedBlocks = new java.util.ArrayList<org.bukkit.block.Block>();
		org.bukkit.craftbukkit.CraftWorld craftWorld = world.getWorld();
		// CraftBukkit end

		for (int k1 = i; k1 <= l; ++k1) {
			for (int l1 = j; l1 <= i1; ++l1) {
				for (int i2 = k; i2 <= j1; ++i2) {
					Block block = world.getType(k1, l1, i2);

					if (block.getMaterial() != Material.AIR) {
						if (block != Blocks.OBSIDIAN && block != Blocks.WHITESTONE && block != Blocks.BEDROCK && world.getGameRules().getBoolean("mobGriefing")) {
							// CraftBukkit start - Add blocks to list rather than destroying them
							// flag1 = this.world.setAir(k1, l1, i2) || flag1;
							flag1 = true;
							destroyedBlocks.add(craftWorld.getBlockAt(k1, l1, i2));
							// CraftBukkit end
						} else {
							flag = true;
						}
					}
				}
			}
		}

		if (flag1) {
			// CraftBukkit start - Set off an EntityExplodeEvent for the dragon exploding all these blocks
			org.bukkit.entity.Entity bukkitEntity = getBukkitEntity();
			EntityExplodeEvent event = new EntityExplodeEvent(bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0F);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled())
				// This flag literally means 'Dragon hit something hard' (Obsidian, White Stone or Bedrock) and will cause the dragon to slow down.
				// We should consider adding an event extension for it, or perhaps returning true if the event is cancelled.
				return flag;
			else if (event.getYield() == 0F) {
				// Yield zero ==> no drops
				for (org.bukkit.block.Block block : event.blockList()) {
					world.setAir(block.getX(), block.getY(), block.getZ());
				}
			} else {
				for (org.bukkit.block.Block block : event.blockList()) {
					org.bukkit.Material blockId = block.getType();
					if (blockId == org.bukkit.Material.AIR) {
						continue;
					}

					int blockX = block.getX();
					int blockY = block.getY();
					int blockZ = block.getZ();

					Block nmsBlock = org.bukkit.craftbukkit.util.CraftMagicNumbers.getBlock(blockId);
					if (nmsBlock.a(explosionSource)) {
						nmsBlock.dropNaturally(world, blockX, blockY, blockZ, block.getData(), event.getYield(), 0);
					}
					nmsBlock.wasExploded(world, blockX, blockY, blockZ, explosionSource);

					world.setAir(blockX, blockY, blockZ);
				}
			}
			// CraftBukkit end

			double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * random.nextFloat();
			double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * random.nextFloat();
			double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * random.nextFloat();

			world.addParticle("largeexplode", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

		return flag;
	}

	@Override
	public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
		if (entitycomplexpart != bq) {
			f = f / 4.0F + 1.0F;
		}

		float f1 = yaw * 3.1415927F / 180.0F;
		float f2 = MathHelper.sin(f1);
		float f3 = MathHelper.cos(f1);

		h = locX + f2 * 5.0F + (random.nextFloat() - 0.5F) * 2.0F;
		i = locY + random.nextFloat() * 3.0F + 1.0D;
		bm = locZ - f3 * 5.0F + (random.nextFloat() - 0.5F) * 2.0F;
		bD = null;
		if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
			dealDamage(damagesource, f);
		}

		return true;
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		return false;
	}

	public boolean dealDamage(DamageSource damagesource, float f) { // CraftBukkit - protected -> public
		return super.damageEntity(damagesource, f);
	}

	@Override
	protected void aF() {
		if (dead)
			return; // CraftBukkit - can't kill what's already dead
		++bB;
		if (bB >= 180 && bB <= 200) {
			float f = (random.nextFloat() - 0.5F) * 8.0F;
			float f1 = (random.nextFloat() - 0.5F) * 4.0F;
			float f2 = (random.nextFloat() - 0.5F) * 8.0F;

			world.addParticle("hugeexplosion", locX + f, locY + 2.0D + f1, locZ + f2, 0.0D, 0.0D, 0.0D);
		}

		int i;
		int j;

		if (!world.isStatic) {
			if (bB > 150 && bB % 5 == 0) {
				i = expToDrop / 12; // CraftBukkit - drop experience as dragon falls from sky. use experience drop from death event. This is now set in getExpReward()

				while (i > 0) {
					j = EntityExperienceOrb.getOrbValue(i);
					i -= j;
					world.addEntity(new EntityExperienceOrb(world, locX, locY, locZ, j));
				}
			}

			if (bB == 1) {
				// CraftBukkit start - Use relative location for far away sounds
				//this.world.b(1018, (int) this.locX, (int) this.locY, (int) this.locZ, 0);
				int viewDistance = ((WorldServer) world).getServer().getViewDistance() * 16;
				for (EntityPlayer player : (List<EntityPlayer>) world.players) {
					double deltaX = locX - player.locX;
					double deltaZ = locZ - player.locZ;
					double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
					if (world.spigotConfig.dragonDeathSoundRadius > 0 && distanceSquared > world.spigotConfig.dragonDeathSoundRadius * world.spigotConfig.dragonDeathSoundRadius) {
						continue; // Spigot
					}
					if (distanceSquared > viewDistance * viewDistance) {
						double deltaLength = Math.sqrt(distanceSquared);
						double relativeX = player.locX + deltaX / deltaLength * viewDistance;
						double relativeZ = player.locZ + deltaZ / deltaLength * viewDistance;
						player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, (int) relativeX, (int) locY, (int) relativeZ, 0, true));
					} else {
						player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, (int) locX, (int) locY, (int) locZ, 0, true));
					}
				}
				// CraftBukkit end
			}
		}

		move(0.0D, 0.10000000149011612D, 0.0D);
		aM = yaw += 20.0F;
		if (bB == 200 && !world.isStatic) {
			i = expToDrop - 10 * expToDrop / 12; // CraftBukkit - drop the remaining experience

			while (i > 0) {
				j = EntityExperienceOrb.getOrbValue(i);
				i -= j;
				world.addEntity(new EntityExperienceOrb(world, locX, locY, locZ, j));
			}

			this.b(MathHelper.floor(locX), MathHelper.floor(locZ));
			this.die();
		}
	}

	private void b(int i, int j) {
		byte b0 = 64;

		BlockEnderPortal.a = true;
		byte b1 = 4;

		// CraftBukkit start - Replace any "this.world" in the following with just "world"!
		BlockStateListPopulator world = new BlockStateListPopulator(this.world.getWorld());

		for (int k = b0 - 1; k <= b0 + 32; ++k) {
			for (int l = i - b1; l <= i + b1; ++l) {
				for (int i1 = j - b1; i1 <= j + b1; ++i1) {
					double d0 = l - i;
					double d1 = i1 - j;
					double d2 = d0 * d0 + d1 * d1;

					if (d2 <= (b1 - 0.5D) * (b1 - 0.5D)) {
						if (k < b0) {
							if (d2 <= (b1 - 1 - 0.5D) * (b1 - 1 - 0.5D)) {
								world.setTypeUpdate(l, k, i1, Blocks.BEDROCK);
							}
						} else if (k > b0) {
							world.setTypeUpdate(l, k, i1, Blocks.AIR);
						} else if (d2 > (b1 - 1 - 0.5D) * (b1 - 1 - 0.5D)) {
							world.setTypeUpdate(l, k, i1, Blocks.BEDROCK);
						} else {
							world.setTypeUpdate(l, k, i1, Blocks.ENDER_PORTAL);
						}
					}
				}
			}
		}

		world.setType(i, b0 + 0, j, Blocks.BEDROCK);
		world.setType(i, b0 + 1, j, Blocks.BEDROCK);
		world.setType(i, b0 + 2, j, Blocks.BEDROCK);
		world.setTypeAndData(i - 1, b0 + 2, j, Blocks.TORCH, 2, 0);
		world.setTypeAndData(i + 1, b0 + 2, j, Blocks.TORCH, 1, 0);
		world.setTypeAndData(i, b0 + 2, j - 1, Blocks.TORCH, 4, 0);
		world.setTypeAndData(i, b0 + 2, j + 1, Blocks.TORCH, 3, 0);
		world.setType(i, b0 + 3, j, Blocks.BEDROCK);
		world.setType(i, b0 + 4, j, Blocks.DRAGON_EGG);

		EntityCreatePortalEvent event = new EntityCreatePortalEvent((org.bukkit.entity.LivingEntity) getBukkitEntity(), java.util.Collections.unmodifiableList(world.getList()), org.bukkit.PortalType.ENDER);
		this.world.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			for (BlockState state : event.getBlocks()) {
				state.update(true);
			}
		} else {
			for (BlockState state : event.getBlocks()) {
				PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(state.getX(), state.getY(), state.getZ(), this.world);
				for (Iterator it = this.world.players.iterator(); it.hasNext();) {
					EntityHuman entity = (EntityHuman) it.next();
					if (entity instanceof EntityPlayer) {
						((EntityPlayer) entity).playerConnection.sendPacket(packet);
					}
				}
			}
		}
		// CraftBukkit end

		BlockEnderPortal.a = false;
	}

	@Override
	protected void w() {
	}

	@Override
	public Entity[] at() {
		return children;
	}

	@Override
	public boolean R() {
		return false;
	}

	@Override
	public World a() {
		return world;
	}

	@Override
	protected String t() {
		return "mob.enderdragon.growl";
	}

	@Override
	protected String aT() {
		return "mob.enderdragon.hit";
	}

	@Override
	protected float bf() {
		return 5.0F;
	}

	// CraftBukkit start
	@Override
	public int getExpReward() {
		// This value is equal to the amount of experience dropped while falling from the sky (10 * 1000)
		// plus what is dropped when the dragon hits the ground (2000)
		return 12000;
	}
	// CraftBukkit end
}
