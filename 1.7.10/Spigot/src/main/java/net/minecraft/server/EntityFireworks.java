package net.minecraft.server;

public class EntityFireworks extends Entity {

	private int ticksFlown;
	public int expectedLifespan; // CraftBukkit - private -> public

	// Spigot Start
	@Override
	public void inactiveTick() {
		ticksFlown += 19;
		super.inactiveTick();
	}

	// Spigot End

	public EntityFireworks(World world) {
		super(world);
		this.a(0.25F, 0.25F);
	}

	@Override
	protected void c() {
		datawatcher.add(8, 5);
	}

	public EntityFireworks(World world, double d0, double d1, double d2, ItemStack itemstack) {
		super(world);
		ticksFlown = 0;
		this.a(0.25F, 0.25F);
		setPosition(d0, d1, d2);
		height = 0.0F;
		int i = 1;

		if (itemstack != null && itemstack.hasTag()) {
			datawatcher.watch(8, itemstack);
			NBTTagCompound nbttagcompound = itemstack.getTag();
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Fireworks");

			if (nbttagcompound1 != null) {
				i += nbttagcompound1.getByte("Flight");
			}
		}

		motX = random.nextGaussian() * 0.001D;
		motZ = random.nextGaussian() * 0.001D;
		motY = 0.05D;
		expectedLifespan = 10 * i + random.nextInt(6) + random.nextInt(7);
	}

	@Override
	public void h() {
		S = locX;
		T = locY;
		U = locZ;
		super.h();
		motX *= 1.15D;
		motZ *= 1.15D;
		motY += 0.04D;
		move(motX, motY, motZ);
		float f = MathHelper.sqrt(motX * motX + motZ * motZ);

		yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.1415927410125732D);

		for (pitch = (float) (Math.atan2(motY, f) * 180.0D / 3.1415927410125732D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F) {
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
		if (ticksFlown == 0) {
			world.makeSound(this, "fireworks.launch", 3.0F, 1.0F);
		}

		++ticksFlown;
		if (world.isStatic && ticksFlown % 2 < 2) {
			world.addParticle("fireworksSpark", locX, locY - 0.3D, locZ, random.nextGaussian() * 0.05D, -motY * 0.5D, random.nextGaussian() * 0.05D);
		}

		if (!world.isStatic && ticksFlown > expectedLifespan) {
			world.broadcastEntityEffect(this, (byte) 17);
			die();
		}
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInt("Life", ticksFlown);
		nbttagcompound.setInt("LifeTime", expectedLifespan);
		ItemStack itemstack = datawatcher.getItemStack(8);

		if (itemstack != null) {
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();

			itemstack.save(nbttagcompound1);
			nbttagcompound.set("FireworksItem", nbttagcompound1);
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		ticksFlown = nbttagcompound.getInt("Life");
		expectedLifespan = nbttagcompound.getInt("LifeTime");
		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("FireworksItem");

		if (nbttagcompound1 != null) {
			ItemStack itemstack = ItemStack.createStack(nbttagcompound1);

			if (itemstack != null) {
				datawatcher.watch(8, itemstack);
			}
		}
	}

	@Override
	public float d(float f) {
		return super.d(f);
	}

	public boolean au() {
		return false;
	}
}
