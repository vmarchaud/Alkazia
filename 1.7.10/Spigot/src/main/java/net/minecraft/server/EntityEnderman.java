package net.minecraft.server;

import java.util.UUID;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
// CraftBukkit end
import org.spigotmc.ProtocolData; // Spigot - protocol patch

public class EntityEnderman extends EntityMonster {

	private static final UUID bp = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier bq = new AttributeModifier(bp, "Attacking speed boost", 6.199999809265137D, 0).a(false);
	private static boolean[] br = new boolean[256];
	private int bs;
	private int bt;
	private Entity bu;
	private boolean bv;

	public EntityEnderman(World world) {
		super(world);
		this.a(0.6F, 2.9F);
		W = 1.0F;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
		getAttributeInstance(GenericAttributes.d).setValue(0.30000001192092896D);
		getAttributeInstance(GenericAttributes.e).setValue(7.0D);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, new ProtocolData.ByteShort((short) 0)); // Spigot - protocol patch, handle metadata change
		datawatcher.a(17, new Byte((byte) 0));
		datawatcher.a(18, new Byte((byte) 0));
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("carried", (short) Block.getId(getCarried()));
		nbttagcompound.setShort("carriedData", (short) getCarriedData());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setCarried(Block.getById(nbttagcompound.getShort("carried")));
		setCarriedData(nbttagcompound.getShort("carriedData"));
	}

	@Override
	protected Entity findTarget() {
		EntityHuman entityhuman = world.findNearbyVulnerablePlayer(this, 64.0D);

		if (entityhuman != null) {
			if (this.f(entityhuman)) {
				bv = true;
				if (bt == 0) {
					world.makeSound(entityhuman.locX, entityhuman.locY, entityhuman.locZ, "mob.endermen.stare", 1.0F, 1.0F);
				}

				if (bt++ == 5) {
					bt = 0;
					this.a(true);
					return entityhuman;
				}
			} else {
				bt = 0;
			}
		}

		return null;
	}

	private boolean f(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.armor[3];

		if (itemstack != null && itemstack.getItem() == Item.getItemOf(Blocks.PUMPKIN))
			return false;
		else {
			Vec3D vec3d = entityhuman.j(1.0F).a();
			Vec3D vec3d1 = Vec3D.a(locX - entityhuman.locX, boundingBox.b + length / 2.0F - (entityhuman.locY + entityhuman.getHeadHeight()), locZ - entityhuman.locZ);
			double d0 = vec3d1.b();

			vec3d1 = vec3d1.a();
			double d1 = vec3d.b(vec3d1);

			return d1 > 1.0D - 0.025D / d0 && entityhuman.hasLineOfSight(this);
		}
	}

	@Override
	public void e() {
		if (L()) {
			damageEntity(DamageSource.DROWN, 1.0F);
		}

		if (bu != target) {
			AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.d);

			attributeinstance.b(bq);
			if (target != null) {
				attributeinstance.a(bq);
			}
		}

		bu = target;
		int i;

		if (!world.isStatic && world.getGameRules().getBoolean("mobGriefing")) {
			int j;
			int k;
			Block block;

			if (getCarried().getMaterial() == Material.AIR) {
				if (random.nextInt(20) == 0) {
					i = MathHelper.floor(locX - 2.0D + random.nextDouble() * 4.0D);
					j = MathHelper.floor(locY + random.nextDouble() * 3.0D);
					k = MathHelper.floor(locZ - 2.0D + random.nextDouble() * 4.0D);
					block = world.getType(i, j, k);
					if (br[Block.getId(block)]) {
						// CraftBukkit start - Pickup event
						if (!CraftEventFactory.callEntityChangeBlockEvent(this, world.getWorld().getBlockAt(i, j, k), org.bukkit.Material.AIR).isCancelled()) {
							setCarried(block);
							setCarriedData(world.getData(i, j, k));
							world.setTypeUpdate(i, j, k, Blocks.AIR);
						}
						// CraftBukkit end
					}
				}
			} else if (random.nextInt(2000) == 0) {
				i = MathHelper.floor(locX - 1.0D + random.nextDouble() * 2.0D);
				j = MathHelper.floor(locY + random.nextDouble() * 2.0D);
				k = MathHelper.floor(locZ - 1.0D + random.nextDouble() * 2.0D);
				block = world.getType(i, j, k);
				Block block1 = world.getType(i, j - 1, k);

				if (block.getMaterial() == Material.AIR && block1.getMaterial() != Material.AIR && block1.d()) {
					// CraftBukkit start - Place event
					if (!CraftEventFactory.callEntityChangeBlockEvent(this, i, j, k, getCarried(), getCarriedData()).isCancelled()) {
						world.setTypeAndData(i, j, k, getCarried(), getCarriedData(), 3);
						setCarried(Blocks.AIR);
					}
					// CraftBukkit end
				}
			}
		}

		for (i = 0; i < 2; ++i) {
			world.addParticle("portal", locX + (random.nextDouble() - 0.5D) * width, locY + random.nextDouble() * length - 0.25D, locZ + (random.nextDouble() - 0.5D) * width, (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(), (random.nextDouble() - 0.5D) * 2.0D);
		}

		if (world.w() && !world.isStatic) {
			float f = this.d(1.0F);

			if (f > 0.5F && world.i(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)) && random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				target = null;
				this.a(false);
				bv = false;
				bZ();
			}
		}

		if (L() || isBurning()) {
			target = null;
			this.a(false);
			bv = false;
			bZ();
		}

		if (cd() && !bv && random.nextInt(100) == 0) {
			this.a(false);
		}

		bc = false;
		if (target != null) {
			this.a(target, 100.0F, 100.0F);
		}

		if (!world.isStatic && isAlive()) {
			if (target != null) {
				if (target instanceof EntityHuman && this.f((EntityHuman) target)) {
					if (target.f(this) < 16.0D) {
						bZ();
					}

					bs = 0;
				} else if (target.f(this) > 256.0D && bs++ >= 30 && this.c(target)) {
					bs = 0;
				}
			} else {
				this.a(false);
				bs = 0;
			}
		}

		super.e();
	}

	protected boolean bZ() {
		double d0 = locX + (random.nextDouble() - 0.5D) * 64.0D;
		double d1 = locY + (random.nextInt(64) - 32);
		double d2 = locZ + (random.nextDouble() - 0.5D) * 64.0D;

		return this.k(d0, d1, d2);
	}

	protected boolean c(Entity entity) {
		Vec3D vec3d = Vec3D.a(locX - entity.locX, boundingBox.b + length / 2.0F - entity.locY + entity.getHeadHeight(), locZ - entity.locZ);

		vec3d = vec3d.a();
		double d0 = 16.0D;
		double d1 = locX + (random.nextDouble() - 0.5D) * 8.0D - vec3d.a * d0;
		double d2 = locY + (random.nextInt(16) - 8) - vec3d.b * d0;
		double d3 = locZ + (random.nextDouble() - 0.5D) * 8.0D - vec3d.c * d0;

		return this.k(d1, d2, d3);
	}

	protected boolean k(double d0, double d1, double d2) {
		double d3 = locX;
		double d4 = locY;
		double d5 = locZ;

		locX = d0;
		locY = d1;
		locZ = d2;
		boolean flag = false;
		int i = MathHelper.floor(locX);
		int j = MathHelper.floor(locY);
		int k = MathHelper.floor(locZ);

		if (world.isLoaded(i, j, k)) {
			boolean flag1 = false;

			while (!flag1 && j > 0) {
				Block block = world.getType(i, j - 1, k);

				if (block.getMaterial().isSolid()) {
					flag1 = true;
				} else {
					--locY;
					--j;
				}
			}

			if (flag1) {
				// CraftBukkit start - Teleport event
				EntityTeleportEvent teleport = new EntityTeleportEvent(getBukkitEntity(), new Location(world.getWorld(), d3, d4, d5), new Location(world.getWorld(), locX, locY, locZ));
				world.getServer().getPluginManager().callEvent(teleport);
				if (teleport.isCancelled())
					return false;

				Location to = teleport.getTo();
				setPosition(to.getX(), to.getY(), to.getZ());
				// CraftBukkit end

				if (world.getCubes(this, boundingBox).isEmpty() && !world.containsLiquid(boundingBox)) {
					flag = true;
				}
			}
		}

		if (!flag) {
			setPosition(d3, d4, d5);
			return false;
		} else {
			short short1 = 128;

			for (int l = 0; l < short1; ++l) {
				double d6 = l / (short1 - 1.0D);
				float f = (random.nextFloat() - 0.5F) * 0.2F;
				float f1 = (random.nextFloat() - 0.5F) * 0.2F;
				float f2 = (random.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (locX - d3) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;
				double d8 = d4 + (locY - d4) * d6 + random.nextDouble() * length;
				double d9 = d5 + (locZ - d5) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;

				world.addParticle("portal", d7, d8, d9, f, f1, f2);
			}

			world.makeSound(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
			makeSound("mob.endermen.portal", 1.0F, 1.0F);
			return true;
		}
	}

	@Override
	protected String t() {
		return cd() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	@Override
	protected String aT() {
		return "mob.endermen.hit";
	}

	@Override
	protected String aU() {
		return "mob.endermen.death";
	}

	@Override
	protected Item getLoot() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		Item item = getLoot();

		if (item != null) {
			int j = random.nextInt(2 + i);

			for (int k = 0; k < j; ++k) {
				this.a(item, 1);
			}
		}
	}

	public void setCarried(Block block) {
		datawatcher.watch(16, new ProtocolData.ByteShort((short) Block.getId(block))); // Spigot - protocol patch, handle metadata change
	}

	public Block getCarried() {
		return Block.getById(datawatcher.getShort(16)); // Spigot - protocol patch, handle metadata change
	}

	public void setCarriedData(int i) {
		datawatcher.watch(17, Byte.valueOf((byte) (i & 255)));
	}

	public int getCarriedData() {
		return datawatcher.getByte(17);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			this.a(true);
			if (damagesource instanceof EntityDamageSource && damagesource.getEntity() instanceof EntityHuman) {
				bv = true;
			}

			if (damagesource instanceof EntityDamageSourceIndirect) {
				bv = false;

				for (int i = 0; i < 64; ++i) {
					if (bZ())
						return true;
				}

				return false;
			} else
				return super.damageEntity(damagesource, f);
		}
	}

	public boolean cd() {
		return datawatcher.getByte(18) > 0;
	}

	public void a(boolean flag) {
		datawatcher.watch(18, Byte.valueOf((byte) (flag ? 1 : 0)));
	}

	static {
		br[Block.getId(Blocks.GRASS)] = true;
		br[Block.getId(Blocks.DIRT)] = true;
		br[Block.getId(Blocks.SAND)] = true;
		br[Block.getId(Blocks.GRAVEL)] = true;
		br[Block.getId(Blocks.YELLOW_FLOWER)] = true;
		br[Block.getId(Blocks.RED_ROSE)] = true;
		br[Block.getId(Blocks.BROWN_MUSHROOM)] = true;
		br[Block.getId(Blocks.RED_MUSHROOM)] = true;
		br[Block.getId(Blocks.TNT)] = true;
		br[Block.getId(Blocks.CACTUS)] = true;
		br[Block.getId(Blocks.CLAY)] = true;
		br[Block.getId(Blocks.PUMPKIN)] = true;
		br[Block.getId(Blocks.MELON)] = true;
		br[Block.getId(Blocks.MYCEL)] = true;
	}
}
