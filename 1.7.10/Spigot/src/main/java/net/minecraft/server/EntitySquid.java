package net.minecraft.server;

import org.bukkit.craftbukkit.TrigMath; // CraftBukkit

public class EntitySquid extends EntityWaterAnimal {

	public float bp;
	public float bq;
	public float br;
	public float bs;
	public float bt;
	public float bu;
	public float bv;
	public float bw;
	private float bx;
	private float by;
	private float bz;
	private float bA;
	private float bB;
	private float bC;

	public EntitySquid(World world) {
		super(world);
		this.a(0.95F, 0.95F);
		by = 1.0F / (random.nextFloat() + 1.0F) * 0.2F;
	}

	@Override
	protected void aD() {
		super.aD();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
	}

	@Override
	protected String t() {
		return null;
	}

	@Override
	protected String aT() {
		return null;
	}

	@Override
	protected String aU() {
		return null;
	}

	@Override
	protected float bf() {
		return 0.4F;
	}

	@Override
	protected Item getLoot() {
		return Item.getById(0);
	}

	@Override
	protected boolean g_() {
		return false;
	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
		int j = random.nextInt(3 + i) + 1;

		for (int k = 0; k < j; ++k) {
			this.a(new ItemStack(Items.INK_SACK, 1, 0), 0.0F);
		}
	}

	/* CraftBukkit start - Delegate to Entity to use existing inWater value
	public boolean M() {
	    return this.world.a(this.boundingBox.grow(0.0D, -0.6000000238418579D, 0.0D), Material.WATER, (Entity) this);
	}
	// CraftBukkit end */

	@Override
	public void e() {
		super.e();
		bq = bp;
		bs = br;
		bu = bt;
		bw = bv;
		bt += by;
		if (bt > 6.2831855F) {
			bt -= 6.2831855F;
			if (random.nextInt(10) == 0) {
				by = 1.0F / (random.nextFloat() + 1.0F) * 0.2F;
			}
		}

		if (M()) {
			float f;

			if (bt < 3.1415927F) {
				f = bt / 3.1415927F;
				bv = MathHelper.sin(f * f * 3.1415927F) * 3.1415927F * 0.25F;
				if (f > 0.75D) {
					bx = 1.0F;
					bz = 1.0F;
				} else {
					bz *= 0.8F;
				}
			} else {
				bv = 0.0F;
				bx *= 0.9F;
				bz *= 0.99F;
			}

			if (!world.isStatic) {
				motX = bA * bx;
				motY = bB * bx;
				motZ = bC * bx;
			}

			f = MathHelper.sqrt(motX * motX + motZ * motZ);
			// CraftBukkit - Math -> TrigMath
			aM += (-((float) TrigMath.atan2(motX, motZ)) * 180.0F / 3.1415927F - aM) * 0.1F;
			yaw = aM;
			br += 3.1415927F * bz * 1.5F;
			// CraftBukkit - Math -> TrigMath
			bp += (-((float) TrigMath.atan2(f, motY)) * 180.0F / 3.1415927F - bp) * 0.1F;
		} else {
			bv = MathHelper.abs(MathHelper.sin(bt)) * 3.1415927F * 0.25F;
			if (!world.isStatic) {
				motX = 0.0D;
				motY -= 0.08D;
				motY *= 0.9800000190734863D;
				motZ = 0.0D;
			}

			bp = (float) (bp + (-90.0F - bp) * 0.02D);
		}
	}

	@Override
	public void e(float f, float f1) {
		move(motX, motY, motZ);
	}

	@Override
	protected void bq() {
		++aU;
		if (aU > 100) {
			bA = bB = bC = 0.0F;
		} else if (random.nextInt(50) == 0 || !inWater || bA == 0.0F && bB == 0.0F && bC == 0.0F) {
			float f = random.nextFloat() * 3.1415927F * 2.0F;

			bA = MathHelper.cos(f) * 0.2F;
			bB = -0.1F + random.nextFloat() * 0.2F;
			bC = MathHelper.sin(f) * 0.2F;
		}

		w();
	}

	@Override
	public boolean canSpawn() {
		// PaperSpigot - Configurable squid spawn height range
		return locY > world.paperSpigotConfig.squidMinSpawnHeight && locY < world.paperSpigotConfig.squidMaxSpawnHeight && super.canSpawn();
	}
}
