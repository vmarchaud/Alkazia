package net.minecraft.server;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Fish;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

// CraftBukkit end

public class EntityFishingHook extends Entity {

// Alkazia - so much fishing drop
	
    private static final List d = Arrays.asList(new PossibleFishingResult[]
    		{ (new PossibleFishingResult(new ItemStack(Items.INK_SACK, 3, 0), 10)), 
    		new PossibleFishingResult(new ItemStack(Items.MELON_SEEDS), 10),
    		new PossibleFishingResult(new ItemStack(Blocks.POUSSE), 10),
    		new PossibleFishingResult(new ItemStack(Items.potionResistance), 10),
    		new PossibleFishingResult(new ItemStack(Items.BAUXITE_INGOT), 5), 
    		(new PossibleFishingResult(new ItemStack(Items.FISHING_ROD), 2)).a(0.9F),
    		new PossibleFishingResult(new ItemStack(Items.potionHaste), 10),
    		new PossibleFishingResult(new ItemStack(Items.potionJump), 5),
    		new PossibleFishingResult(new ItemStack(Items.INK_SACK, 10, 0), 1),
    		new PossibleFishingResult(new ItemStack(Items.NAME_TAG), 10),
    		new PossibleFishingResult(new ItemStack(Items.BOOK), 10)});
    
    private static final List e = Arrays.asList(new PossibleFishingResult[] 
    		{ new PossibleFishingResult(new ItemStack(Items.RECORD_12), 1),
    		new PossibleFishingResult(new ItemStack(Items.RECORD_2), 1),
    		new PossibleFishingResult(new ItemStack(Items.RECORD_5), 1),
    		(new PossibleFishingResult(new ItemStack(Items.RECORD_8), 1)),
    		(new PossibleFishingResult(new ItemStack(Items.RECORD_3), 1)),
    		(new PossibleFishingResult(new ItemStack(Items.RECORD_7), 1))});
    
    private static final List f = Arrays.asList(new PossibleFishingResult[]
    		{ new PossibleFishingResult(new ItemStack(Items.RAW_FISH, 1, EnumFish.COD.a()), 30),
    		new PossibleFishingResult(new ItemStack(Items.RAW_FISH, 1, EnumFish.SALMON.a()), 20),
    		new PossibleFishingResult(new ItemStack(Items.RAW_FISH, 1, EnumFish.CLOWNFISH.a()), 15),
    		new PossibleFishingResult(new ItemStack(Items.RAW_FISH, 1, EnumFish.PUFFERFISH.a()), 20),
    		new PossibleFishingResult(new ItemStack(Items.RAW_FISH, 1, EnumFish.NINJAFISH.a()), 30)});
    
	private int g = -1;
	private int h = -1;
	private int i = -1;
	private Block at;
	private boolean au;
	public int a;
	public EntityHuman owner;
	private int av;
	private int aw;
	private int ax;
	private int ay;
	private int az;
	private float aA;
	public Entity hooked;
	private int aB;
	private double aC;
	private double aD;
	private double aE;
	private double aF;
	private double aG;

	public EntityFishingHook(World world) {
		super(world);
		this.a(0.25F, 0.25F);
		ak = true;
	}

	public EntityFishingHook(World world, EntityHuman entityhuman) {
		super(world);
		ak = true;
		owner = entityhuman;
		owner.hookedFish = this;
		this.a(0.25F, 0.25F);
		setPositionRotation(entityhuman.locX, entityhuman.locY + 1.62D - entityhuman.height, entityhuman.locZ, entityhuman.yaw, entityhuman.pitch);
		locX -= MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F;
		locY -= 0.10000000149011612D;
		locZ -= MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F;
		setPosition(locX, locY, locZ);
		height = 0.0F;
		float f = 0.4F;

		motX = -MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
		motZ = MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f;
		motY = -MathHelper.sin(pitch / 180.0F * 3.1415927F) * f;
		this.c(motX, motY, motZ, 1.5F, 1.0F);
	}

	@Override
	protected void c() {
	}

	public void c(double d0, double d1, double d2, float f, float f1) {
		float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

		d0 /= f2;
		d1 /= f2;
		d2 /= f2;
		d0 += random.nextGaussian() * 0.007499999832361937D * f1;
		d1 += random.nextGaussian() * 0.007499999832361937D * f1;
		d2 += random.nextGaussian() * 0.007499999832361937D * f1;
		d0 *= f;
		d1 *= f;
		d2 *= f;
		motX = d0;
		motY = d1;
		motZ = d2;
		float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

		lastYaw = yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
		lastPitch = pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
		av = 0;
	}

	@Override
	public void h() {
		super.h();
		if (aB > 0) {
			double d0 = locX + (aC - locX) / aB;
			double d1 = locY + (aD - locY) / aB;
			double d2 = locZ + (aE - locZ) / aB;
			double d3 = MathHelper.g(aF - yaw);

			yaw = (float) (yaw + d3 / aB);
			pitch = (float) (pitch + (aG - pitch) / aB);
			--aB;
			setPosition(d0, d1, d2);
			this.b(yaw, pitch);
		} else {
			if (!world.isStatic) {
				ItemStack itemstack = owner.bF();

				if (owner.dead || !owner.isAlive() || itemstack == null || itemstack.getItem() != Items.FISHING_ROD || this.f(owner) > 1024.0D) {
					die();
					owner.hookedFish = null;
					return;
				}

				if (hooked != null) {
					if (!hooked.dead) {
						locX = hooked.locX;
						locY = hooked.boundingBox.b + hooked.length * 0.8D;
						locZ = hooked.locZ;
						return;
					}

					hooked = null;
				}
			}

			if (a > 0) {
				--a;
			}

			if (au) {
				if (world.getType(g, h, i) == at) {
					++av;
					if (av == 1200) {
						die();
					}

					return;
				}

				au = false;
				motX *= random.nextFloat() * 0.2F;
				motY *= random.nextFloat() * 0.2F;
				motZ *= random.nextFloat() * 0.2F;
				av = 0;
				aw = 0;
			} else {
				++aw;
			}

			Vec3D vec3d = Vec3D.a(locX, locY, locZ);
			Vec3D vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition movingobjectposition = world.a(vec3d, vec3d1);

			vec3d = Vec3D.a(locX, locY, locZ);
			vec3d1 = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
			if (movingobjectposition != null) {
				vec3d1 = Vec3D.a(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
			}

			Entity entity = null;
			List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double d4 = 0.0D;

			double d5;

			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.R() && (entity1 != owner || aw >= 5)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(f, f, f);
					MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);

					if (movingobjectposition1 != null) {
						d5 = vec3d.distanceSquared(movingobjectposition1.pos); // CraftBukkit - distance efficiency
						if (d5 < d4 || d4 == 0.0D) {
							entity = entity1;
							d4 = d5;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if (movingobjectposition != null) {
				org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this); // Craftbukkit - Call event
				if (movingobjectposition.entity != null) {
					if (movingobjectposition.entity.damageEntity(DamageSource.projectile(this, owner), 0.0F)) {
						hooked = movingobjectposition.entity;
					}
				} else {
					au = true;
				}
			}

			if (!au) {
				move(motX, motY, motZ);
				float f1 = MathHelper.sqrt(motX * motX + motZ * motZ);

				yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.1415927410125732D);

				for (pitch = (float) (Math.atan2(motY, f1) * 180.0D / 3.1415927410125732D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F) {
					;
				}

				while (pitch - lastPitch >= 180.0F) {
					lastPitch += 360.0F;
				}

				while (yaw - lastYaw < -180.0F) {
					lastYaw -= 360.0F;
				}

				while (yaw - lastYaw >= 180.0F) {
					lastYaw += 360.0F;
				}

				pitch = lastPitch + (pitch - lastPitch) * 0.2F;
				yaw = lastYaw + (yaw - lastYaw) * 0.2F;
				float f2 = 0.92F;

				if (onGround || positionChanged) {
					f2 = 0.5F;
				}

				byte b0 = 5;
				double d6 = 0.0D;

				for (int j = 0; j < b0; ++j) {
					double d7 = boundingBox.b + (boundingBox.e - boundingBox.b) * (j + 0) / b0 - 0.125D + 0.125D;
					double d8 = boundingBox.b + (boundingBox.e - boundingBox.b) * (j + 1) / b0 - 0.125D + 0.125D;
					AxisAlignedBB axisalignedbb1 = AxisAlignedBB.a(boundingBox.a, d7, boundingBox.c, boundingBox.d, d8, boundingBox.f);

					if (world.b(axisalignedbb1, Material.WATER)) {
						d6 += 1.0D / b0;
					}
				}

				if (!world.isStatic && d6 > 0.0D) {
					WorldServer worldserver = (WorldServer) world;
					int k = 1;

					if (random.nextFloat() < 0.25F && world.isRainingAt(MathHelper.floor(locX), MathHelper.floor(locY) + 1, MathHelper.floor(locZ))) {
						k = 2;
					}

					if (random.nextFloat() < 0.5F && !world.i(MathHelper.floor(locX), MathHelper.floor(locY) + 1, MathHelper.floor(locZ))) {
						--k;
					}

					if (ax > 0) {
						--ax;
						if (ax <= 0) {
							ay = 0;
							az = 0;
						}
					} else {
						float f3;
						double d9;
						float f4;
						float f5;
						double d10;
						double d11;

						if (az > 0) {
							az -= k;
							if (az <= 0) {
								motY -= 0.20000000298023224D;
								makeSound("random.splash", 0.25F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
								f4 = MathHelper.floor(boundingBox.b);
								worldserver.a("bubble", locX, f4 + 1.0F, locZ, (int) (1.0F + width * 20.0F), width, 0.0D, width, 0.20000000298023224D);
								worldserver.a("wake", locX, f4 + 1.0F, locZ, (int) (1.0F + width * 20.0F), width, 0.0D, width, 0.20000000298023224D);
								ax = MathHelper.nextInt(random, 10, 30);
							} else {
								aA = (float) (aA + random.nextGaussian() * 4.0D);
								f4 = aA * 0.017453292F;
								f5 = MathHelper.sin(f4);
								f3 = MathHelper.cos(f4);
								d9 = locX + f5 * az * 0.1F;
								d11 = MathHelper.floor(boundingBox.b) + 1.0F;
								d10 = locZ + f3 * az * 0.1F;
								if (random.nextFloat() < 0.15F) {
									worldserver.a("bubble", d9, d11 - 0.10000000149011612D, d10, 1, f5, 0.1D, f3, 0.0D);
								}

								float f6 = f5 * 0.04F;
								float f7 = f3 * 0.04F;

								worldserver.a("wake", d9, d11, d10, 0, f7, 0.01D, -f6, 1.0D);
								worldserver.a("wake", d9, d11, d10, 0, -f7, 0.01D, f6, 1.0D);
							}
						} else if (ay > 0) {
							ay -= k;
							f4 = 0.15F;
							if (ay < 20) {
								f4 = (float) (f4 + (20 - ay) * 0.05D);
							} else if (ay < 40) {
								f4 = (float) (f4 + (40 - ay) * 0.02D);
							} else if (ay < 60) {
								f4 = (float) (f4 + (60 - ay) * 0.01D);
							}

							if (random.nextFloat() < f4) {
								f5 = MathHelper.a(random, 0.0F, 360.0F) * 0.017453292F;
								f3 = MathHelper.a(random, 25.0F, 60.0F);
								d9 = locX + MathHelper.sin(f5) * f3 * 0.1F;
								d11 = MathHelper.floor(boundingBox.b) + 1.0F;
								d10 = locZ + MathHelper.cos(f5) * f3 * 0.1F;
								worldserver.a("splash", d9, d11, d10, 2 + random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
							}

							if (ay <= 0) {
								aA = MathHelper.a(random, 0.0F, 360.0F);
								az = MathHelper.nextInt(random, 20, 80);
							}
						} else {
							// PaperSpigot - Configurable fishing tick range
							ay = MathHelper.nextInt(random, world.paperSpigotConfig.fishingMinTicks, world.paperSpigotConfig.fishingMaxTicks);
							ay -= EnchantmentManager.getLureEnchantmentLevel(owner) * 20 * 5;
						}
					}

					if (ax > 0) {
						motY -= random.nextFloat() * random.nextFloat() * random.nextFloat() * 0.2D;
					}
				}

				d5 = d6 * 2.0D - 1.0D;
				motY += 0.03999999910593033D * d5;
				if (d6 > 0.0D) {
					f2 = (float) (f2 * 0.9D);
					motY *= 0.8D;
				}

				motX *= f2;
				motY *= f2;
				motZ *= f2;
				setPosition(locX, locY, locZ);
			}
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short) g);
		nbttagcompound.setShort("yTile", (short) h);
		nbttagcompound.setShort("zTile", (short) i);
		nbttagcompound.setByte("inTile", (byte) Block.getId(at));
		nbttagcompound.setByte("shake", (byte) a);
		nbttagcompound.setByte("inGround", (byte) (au ? 1 : 0));
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		g = nbttagcompound.getShort("xTile");
		h = nbttagcompound.getShort("yTile");
		i = nbttagcompound.getShort("zTile");
		at = Block.getById(nbttagcompound.getByte("inTile") & 255);
		a = nbttagcompound.getByte("shake") & 255;
		au = nbttagcompound.getByte("inGround") == 1;
	}

	public int e() {
		if (world.isStatic)
			return 0;
		else {
			byte b0 = 0;

			if (hooked != null) {
				// CraftBukkit start
				PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) owner.getBukkitEntity(), hooked.getBukkitEntity(), (Fish) getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
				world.getServer().getPluginManager().callEvent(playerFishEvent);

				if (playerFishEvent.isCancelled())
					return 0;

				double d0 = owner.locX - locX;
				double d1 = owner.locY - locY;
				double d2 = owner.locZ - locZ;
				double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				double d4 = 0.1D;

				hooked.motX += d0 * d4;
				hooked.motY += d1 * d4 + MathHelper.sqrt(d3) * 0.08D;
				hooked.motZ += d2 * d4;
				b0 = 3;
			} else if (ax > 0) {
				EntityItem entityitem = new EntityItem(world, locX, locY, locZ, this.f());
				// CraftBukkit start
				PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) owner.getBukkitEntity(), entityitem.getBukkitEntity(), (Fish) getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
				playerFishEvent.setExpToDrop(random.nextInt(6) + 1);
				world.getServer().getPluginManager().callEvent(playerFishEvent);

				if (playerFishEvent.isCancelled())
					return 0;

				double d5 = owner.locX - locX;
				double d6 = owner.locY - locY;
				double d7 = owner.locZ - locZ;
				double d8 = MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
				double d9 = 0.1D;

				entityitem.motX = d5 * d9;
				entityitem.motY = d6 * d9 + MathHelper.sqrt(d8) * 0.08D;
				entityitem.motZ = d7 * d9;
				world.addEntity(entityitem);
				// CraftBukkit - this.random.nextInt(6) + 1 -> playerFishEvent.getExpToDrop()
				owner.world.addEntity(new EntityExperienceOrb(owner.world, owner.locX, owner.locY + 0.5D, owner.locZ + 0.5D, playerFishEvent.getExpToDrop()));
				b0 = 1;
			}

			if (au) {
				// CraftBukkit start
				PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) owner.getBukkitEntity(), null, (Fish) getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
				world.getServer().getPluginManager().callEvent(playerFishEvent);

				if (playerFishEvent.isCancelled())
					return 0;

				b0 = 2;
			}

			// CraftBukkit start
			if (b0 == 0) {
				PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) owner.getBukkitEntity(), null, (Fish) getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
				world.getServer().getPluginManager().callEvent(playerFishEvent);
				if (playerFishEvent.isCancelled())
					return 0;
			}
			// CraftBukkit end

			die();
			owner.hookedFish = null;
			return b0;
		}
	}

	private ItemStack f() {
		float f = world.random.nextFloat();
		int i = EnchantmentManager.getLuckEnchantmentLevel(owner);
		int j = EnchantmentManager.getLureEnchantmentLevel(owner);
		float f1 = 0.1F - i * 0.025F - j * 0.01F;
		float f2 = 0.05F + i * 0.01F - j * 0.01F;

		f1 = MathHelper.a(f1, 0.0F, 1.0F);
		f2 = MathHelper.a(f2, 0.0F, 1.0F);
		if (f < f1) {
			owner.a(StatisticList.A, 1);
			return ((PossibleFishingResult) WeightedRandom.a(random, d)).a(random);
		} else {
			f -= f1;
			if (f < f2) {
				owner.a(StatisticList.B, 1);
				return ((PossibleFishingResult) WeightedRandom.a(random, e)).a(random);
			} else {
				float f3 = f - f2;

				owner.a(StatisticList.z, 1);
				return ((PossibleFishingResult) WeightedRandom.a(random, EntityFishingHook.f)).a(random); // CraftBukkit - fix static reference to fish list
			}
		}
	}

	@Override
	public void die() {
		super.die();
		if (owner != null) {
			owner.hookedFish = null;
		}
	}
}
