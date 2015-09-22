package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;

// CraftBukkit end

public class EntityExperienceOrb extends Entity {

	public int a;
	public int b;
	public int c;
	private int d = 5;
	public int value; // CraftBukkit - private -> public
	private EntityHuman targetPlayer;
	private int targetTime;

	public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
		super(world);
		this.a(0.5F, 0.5F);
		height = length / 2.0F;
		setPosition(d0, d1, d2);
		yaw = (float) (Math.random() * 360.0D);
		motX = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
		motY = (float) (Math.random() * 0.2D) * 2.0F;
		motZ = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F;
		value = i;
	}

	@Override
	protected boolean g_() {
		return false;
	}

	public EntityExperienceOrb(World world) {
		super(world);
		this.a(0.25F, 0.25F);
		height = length / 2.0F;
	}

	@Override
	protected void c() {
	}

	@Override
	public void h() {
		super.h();
		if (c > 0) {
			--c;
		}

		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		motY -= 0.029999999329447746D;
		if (world.getType(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)).getMaterial() == Material.LAVA) {
			motY = 0.20000000298023224D;
			motX = (random.nextFloat() - random.nextFloat()) * 0.2F;
			motZ = (random.nextFloat() - random.nextFloat()) * 0.2F;
			makeSound("random.fizz", 0.4F, 2.0F + random.nextFloat() * 0.4F);
		}

		this.j(locX, (boundingBox.b + boundingBox.e) / 2.0D, locZ);
		double d0 = 8.0D;

		if (targetTime < a - 20 + getId() % 100) {
			if (targetPlayer == null || targetPlayer.f(this) > d0 * d0) {
				targetPlayer = world.findNearbyPlayer(this, d0);
			}

			targetTime = a;
		}

		if (targetPlayer != null) {
			// CraftBukkit start
			EntityTargetEvent event = CraftEventFactory.callEntityTargetEvent(this, targetPlayer, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
			Entity target = event.getTarget() == null ? null : ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();

			if (!event.isCancelled() && target != null) {
				double d1 = (target.locX - locX) / d0;
				double d2 = (target.locY + target.getHeadHeight() - locY) / d0;
				double d3 = (target.locZ - locZ) / d0;
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
				double d5 = 1.0D - d4;
				if (d5 > 0.0D) {
					d5 *= d5;
					motX += d1 / d4 * d5 * 0.1D;
					motY += d2 / d4 * d5 * 0.1D;
					motZ += d3 / d4 * d5 * 0.1D;
				}
				// CraftBukkit end
			}
		}

		move(motX, motY, motZ);
		float f = 0.98F;

		if (onGround) {
			f = world.getType(MathHelper.floor(locX), MathHelper.floor(boundingBox.b) - 1, MathHelper.floor(locZ)).frictionFactor * 0.98F;
		}

		motX *= f;
		motY *= 0.9800000190734863D;
		motZ *= f;
		if (onGround) {
			motY *= -0.8999999761581421D;
		}

		++a;
		++b;
		if (b >= 6000) {
			die();
		}
	}

	@Override
	public boolean N() {
		return world.a(boundingBox, Material.WATER, this);
	}

	protected void burn(int i) {
		damageEntity(DamageSource.FIRE, i);
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (isInvulnerable())
			return false;
		else {
			Q();
			d = (int) (d - f);
			if (d <= 0) {
				die();
			}

			return false;
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("Health", (byte) d);
		nbttagcompound.setShort("Age", (short) b);
		nbttagcompound.setShort("Value", (short) value);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		d = nbttagcompound.getShort("Health") & 255;
		b = nbttagcompound.getShort("Age");
		value = nbttagcompound.getShort("Value");
	}

	@Override
	public void b_(EntityHuman entityhuman) {
		if (!world.isStatic) {
			if (c == 0 && entityhuman.bt == 0) {
				entityhuman.bt = 2;
				world.makeSound(entityhuman, "random.orb", 0.1F, 0.5F * ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.8F));
				entityhuman.receive(this, 1);
				entityhuman.giveExp(CraftEventFactory.callPlayerExpChangeEvent(entityhuman, value).getAmount()); // CraftBukkit - this.value -> event.getAmount()
				die();
			}
		}
	}

	public int e() {
		return value;
	}

	public static int getOrbValue(int i) {
		// CraftBukkit start
		if (i > 162670129)
			return i - 100000;
		if (i > 81335063)
			return 81335063;
		if (i > 40667527)
			return 40667527;
		if (i > 20333759)
			return 20333759;
		if (i > 10166857)
			return 10166857;
		if (i > 5083423)
			return 5083423;
		if (i > 2541701)
			return 2541701;
		if (i > 1270849)
			return 1270849;
		if (i > 635413)
			return 635413;
		if (i > 317701)
			return 317701;
		if (i > 158849)
			return 158849;
		if (i > 79423)
			return 79423;
		if (i > 39709)
			return 39709;
		if (i > 19853)
			return 19853;
		if (i > 9923)
			return 9923;
		if (i > 4957)
			return 4957;
		// CraftBukkit end

		return i >= 2477 ? 2477 : i >= 1237 ? 1237 : i >= 617 ? 617 : i >= 307 ? 307 : i >= 149 ? 149 : i >= 73 ? 73 : i >= 37 ? 37 : i >= 17 ? 17 : i >= 7 ? 7 : i >= 3 ? 3 : 1;
	}

	@Override
	public boolean av() {
		return false;
	}
}
