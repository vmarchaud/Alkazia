package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SlimeSplitEvent;

// CraftBukkit end

public class EntitySlime extends EntityInsentient implements IMonster {

	public float h;
	public float i;
	public float bm;
	private int jumpDelay;
	private Entity lastTarget; // CraftBukkit

	public EntitySlime(World world) {
		super(world);
		int i = 1 << random.nextInt(3);

		height = 0.0F;
		jumpDelay = random.nextInt(20) + 10;
		setSize(i);
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(16, new Byte((byte) 1));
	}

	// CraftBukkit - protected -> public
	public void setSize(int i) {
		datawatcher.watch(16, new Byte((byte) i));
		this.a(0.6F * i, 0.6F * i);
		setPosition(locX, locY, locZ);
		getAttributeInstance(GenericAttributes.maxHealth).setValue(i * i);
		setHealth(getMaxHealth());
		b = i;
	}

	public int getSize() {
		return datawatcher.getByte(16);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("Size", getSize() - 1);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		int i = nbttagcompound.getInt("Size");

		if (i < 0) {
			i = 0;
		}

		setSize(i + 1);
	}

	protected String bP() {
		return "slime";
	}

	protected String bV() {
		return "mob.slime." + (getSize() > 1 ? "big" : "small");
	}

	@Override
	public void h() {
		if (!world.isStatic && world.difficulty == EnumDifficulty.PEACEFUL && getSize() > 0) {
			dead = true;
		}

		i += (h - i) * 0.5F;
		bm = i;
		boolean flag = onGround;

		super.h();
		int i;

		if (onGround && !flag) {
			i = getSize();

			for (int j = 0; j < i * 8; ++j) {
				float f = random.nextFloat() * 3.1415927F * 2.0F;
				float f1 = random.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * i * 0.5F * f1;

				world.addParticle(bP(), locX + f2, boundingBox.b, locZ + f3, 0.0D, 0.0D, 0.0D);
			}

			if (bW()) {
				makeSound(bV(), bf(), ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			}

			h = -0.5F;
		} else if (!onGround && flag) {
			h = 1.0F;
		}

		bS();
		if (world.isStatic) {
			i = getSize();
			this.a(0.6F * i, 0.6F * i);
		}
	}

	@Override
	protected void bq() {
		w();
		// CraftBukkit start
		Entity entityhuman = world.findNearbyVulnerablePlayer(this, 16.0D); // EntityHuman -> Entity
		EntityTargetEvent event = null;

		if (entityhuman != null && !entityhuman.equals(lastTarget)) {
			event = CraftEventFactory.callEntityTargetEvent(this, entityhuman, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
		} else if (lastTarget != null && entityhuman == null) {
			event = CraftEventFactory.callEntityTargetEvent(this, entityhuman, EntityTargetEvent.TargetReason.FORGOT_TARGET);
		}

		if (event != null && !event.isCancelled()) {
			entityhuman = event.getTarget() == null ? null : ((CraftEntity) event.getTarget()).getHandle();
		}

		lastTarget = entityhuman;
		// CraftBukkit end

		if (entityhuman != null) {
			this.a(entityhuman, 10.0F, 20.0F);
		}

		if (onGround && jumpDelay-- <= 0) {
			jumpDelay = bR();
			if (entityhuman != null) {
				jumpDelay /= 3;
			}

			bc = true;
			if (bY()) {
				makeSound(bV(), bf(), ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
			}

			bd = 1.0F - random.nextFloat() * 2.0F;
			be = 1 * getSize();
		} else {
			bc = false;
			if (onGround) {
				bd = be = 0.0F;
			}
		}
	}

	protected void bS() {
		h *= 0.6F;
	}

	protected int bR() {
		return random.nextInt(20) + 10;
	}

	protected EntitySlime bQ() {
		return new EntitySlime(world);
	}

	@Override
	public void die() {
		int i = getSize();

		if (!world.isStatic && i > 1 && getHealth() <= 0.0F) {
			int j = 2 + random.nextInt(3);

			// CraftBukkit start
			SlimeSplitEvent event = new SlimeSplitEvent((org.bukkit.entity.Slime) getBukkitEntity(), j);
			world.getServer().getPluginManager().callEvent(event);

			if (!event.isCancelled() && event.getCount() > 0) {
				j = event.getCount();
			} else {
				super.die();
				return;
			}
			// CraftBukkit end

			for (int k = 0; k < j; ++k) {
				float f = (k % 2 - 0.5F) * i / 4.0F;
				float f1 = (k / 2 - 0.5F) * i / 4.0F;
				EntitySlime entityslime = bQ();

				entityslime.setSize(i / 2);
				entityslime.setPositionRotation(locX + f, locY + 0.5D, locZ + f1, random.nextFloat() * 360.0F, 0.0F);
				world.addEntity(entityslime, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SLIME_SPLIT); // CraftBukkit - SpawnReason
			}
		}

		super.die();
	}

	@Override
	public void b_(EntityHuman entityhuman) {
		if (bT()) {
			int i = getSize();

			if (hasLineOfSight(entityhuman) && this.f(entityhuman) < 0.6D * i * 0.6D * i && entityhuman.damageEntity(DamageSource.mobAttack(this), bU())) {
				makeSound("mob.attack", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	protected boolean bT() {
		return getSize() > 1;
	}

	protected int bU() {
		return getSize();
	}

	@Override
	protected String aT() {
		return "mob.slime." + (getSize() > 1 ? "big" : "small");
	}

	@Override
	protected String aU() {
		return "mob.slime." + (getSize() > 1 ? "big" : "small");
	}

	@Override
	protected Item getLoot() {
		return getSize() == 1 ? Items.SLIME_BALL : Item.getById(0);
	}

	@Override
	public boolean canSpawn() {
		Chunk chunk = world.getChunkAtWorldCoords(MathHelper.floor(locX), MathHelper.floor(locZ));

		if (world.getWorldData().getType() == WorldType.FLAT && random.nextInt(4) != 1)
			return false;
		else {
			if (getSize() == 1 || world.difficulty != EnumDifficulty.PEACEFUL) {
				BiomeBase biomebase = world.getBiome(MathHelper.floor(locX), MathHelper.floor(locZ));

				if (biomebase == BiomeBase.SWAMPLAND && locY > 50.0D && locY < 70.0D && random.nextFloat() < 0.5F && random.nextFloat() < world.y() && world.getLightLevel(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)) <= random.nextInt(8))
					return super.canSpawn();

				if (random.nextInt(10) == 0 && chunk.a(987234911L).nextInt(10) == 0 && locY < 40.0D)
					return super.canSpawn();
			}

			return false;
		}
	}

	@Override
	protected float bf() {
		return 0.4F * getSize();
	}

	@Override
	public int x() {
		return 0;
	}

	protected boolean bY() {
		return getSize() > 0;
	}

	protected boolean bW() {
		return getSize() > 2;
	}
}
