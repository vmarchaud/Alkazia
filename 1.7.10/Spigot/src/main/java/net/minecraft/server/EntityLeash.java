package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityLeash extends EntityHanging {

	public EntityLeash(World world) {
		super(world);
	}

	public EntityLeash(World world, int i, int j, int k) {
		super(world, i, j, k, 0);
		setPosition(i + 0.5D, j + 0.5D, k + 0.5D);
	}

	@Override
	protected void c() {
		super.c();
	}

	@Override
	public void setDirection(int i) {
	}

	@Override
	public int f() {
		return 9;
	}

	@Override
	public int i() {
		return 9;
	}

	@Override
	public void b(Entity entity) {
	}

	@Override
	public boolean d(NBTTagCompound nbttagcompound) {
		return false;
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
	}

	@Override
	public boolean c(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.be();
		boolean flag = false;
		double d0;
		List list;
		Iterator iterator;
		EntityInsentient entityinsentient;

		if (itemstack != null && itemstack.getItem() == Items.LEASH && !world.isStatic) {
			d0 = 7.0D;
			list = world.a(EntityInsentient.class, AxisAlignedBB.a(locX - d0, locY - d0, locZ - d0, locX + d0, locY + d0, locZ + d0));
			if (list != null) {
				iterator = list.iterator();

				while (iterator.hasNext()) {
					entityinsentient = (EntityInsentient) iterator.next();
					if (entityinsentient.bN() && entityinsentient.getLeashHolder() == entityhuman) {
						// CraftBukkit start
						if (CraftEventFactory.callPlayerLeashEntityEvent(entityinsentient, this, entityhuman).isCancelled()) {
							((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, entityinsentient, entityinsentient.getLeashHolder()));
							continue;
						}
						// CraftBukkit end
						entityinsentient.setLeashHolder(this, true);
						flag = true;
					}
				}
			}
		}

		if (!world.isStatic && !flag) {
			// CraftBukkit start - Move below
			// this.die();
			boolean die = true;
			// CraftBukkit end
			if (true || entityhuman.abilities.canInstantlyBuild) { // CraftBukkit - Process for non-creative as well
				d0 = 7.0D;
				list = world.a(EntityInsentient.class, AxisAlignedBB.a(locX - d0, locY - d0, locZ - d0, locX + d0, locY + d0, locZ + d0));
				if (list != null) {
					iterator = list.iterator();

					while (iterator.hasNext()) {
						entityinsentient = (EntityInsentient) iterator.next();
						if (entityinsentient.bN() && entityinsentient.getLeashHolder() == this) {
							// CraftBukkit start
							if (CraftEventFactory.callPlayerUnleashEntityEvent(entityinsentient, entityhuman).isCancelled()) {
								die = false;
								continue;
							}
							entityinsentient.unleash(true, !entityhuman.abilities.canInstantlyBuild); // false -> survival mode boolean
							// CraftBukkit end
						}
					}
				}
			}
			// CraftBukkit start
			if (die) {
				die();
			}
			// CraftBukkit end
		}

		return true;
	}

	@Override
	public boolean survives() {
		return world.getType(x, y, z).b() == 11;
	}

	public static EntityLeash a(World world, int i, int j, int k) {
		EntityLeash entityleash = new EntityLeash(world, i, j, k);

		entityleash.attachedToPlayer = true;
		world.addEntity(entityleash);
		return entityleash;
	}

	public static EntityLeash b(World world, int i, int j, int k) {
		List list = world.a(EntityLeash.class, AxisAlignedBB.a(i - 1.0D, j - 1.0D, k - 1.0D, i + 1.0D, j + 1.0D, k + 1.0D));

		if (list != null) {
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityLeash entityleash = (EntityLeash) iterator.next();

				if (entityleash.x == i && entityleash.y == j && entityleash.z == k)
					return entityleash;
			}
		}

		return null;
	}
}
