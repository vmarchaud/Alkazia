package net.minecraft.server;

public abstract class EntityAgeable extends EntityCreature {

	private float bp = -1.0F;
	private float bq;
	public boolean ageLocked = false; // CraftBukkit

	// Spigot start
	@Override
	public void inactiveTick() {
		super.inactiveTick();
		if (world.isStatic || ageLocked) { // CraftBukkit
			this.a(isBaby());
		} else {
			int i = getAge();

			if (i < 0) {
				++i;
				setAge(i);
			} else if (i > 0) {
				--i;
				setAge(i);
			}
		}
	}

	// Spigot end

	public EntityAgeable(World world) {
		super(world);
	}

	public abstract EntityAgeable createChild(EntityAgeable entityageable);

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (itemstack != null && itemstack.getItem() == Items.MONSTER_EGG) {
			if (!world.isStatic) {
				Class oclass = EntityTypes.a(itemstack.getData());

				if (oclass != null && oclass.isAssignableFrom(this.getClass())) {
					EntityAgeable entityageable = createChild(this);

					if (entityageable != null) {
						entityageable.setAge(-24000);
						entityageable.setPositionRotation(locX, locY, locZ, 0.0F, 0.0F);
						world.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG); // CraftBukkit
						if (itemstack.hasName()) {
							entityageable.setCustomName(itemstack.getName());
						}

						if (!entityhuman.abilities.canInstantlyBuild) {
							--itemstack.count;
							if (itemstack.count == 0) { // CraftBukkit - allow less than 0 stacks as "infinite"
								entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
							}
						}
					}
				}
			}

			return true;
		} else
			return false;
	}

	@Override
	protected void c() {
		super.c();
		datawatcher.a(12, new org.spigotmc.ProtocolData.IntByte(0, (byte) 0)); // Spigot - protocol patch
	}

	public int getAge() {
		return datawatcher.getIntByte(12).value; // Spigot - protocol patch
	}

	public void a(int i) {
		int j = getAge();

		j += i * 20;
		if (j > 0) {
			j = 0;
		}

		setAge(j);
	}

	public void setAge(int i) {
		datawatcher.watch(12, new org.spigotmc.ProtocolData.IntByte(i, (byte) (i < 0 ? -1 : i >= 6000 ? 1 : 0))); // Spigot - protocol patch
		this.a(isBaby());
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("Age", getAge());
		nbttagcompound.setBoolean("AgeLocked", ageLocked); // CraftBukkit
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		setAge(nbttagcompound.getInt("Age"));
		ageLocked = nbttagcompound.getBoolean("AgeLocked"); // CraftBukkit
	}

	@Override
	public void e() {
		super.e();
		if (world.isStatic || ageLocked) { // CraftBukkit
			this.a(isBaby());
		} else {
			int i = getAge();

			if (i < 0) {
				++i;
				setAge(i);
			} else if (i > 0) {
				--i;
				setAge(i);
			}
		}
	}

	@Override
	public boolean isBaby() {
		return getAge() < 0;
	}

	public void a(boolean flag) {
		this.a(flag ? 0.5F : 1.0F);
	}

	@Override
	protected final void a(float f, float f1) {
		boolean flag = bp > 0.0F;

		bp = f;
		bq = f1;
		if (!flag) {
			this.a(1.0F);
		}
	}

	protected final void a(float f) {
		super.a(bp * f, bq * f);
	}
}
