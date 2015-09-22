package net.minecraft.server;

import org.bukkit.event.entity.EntityInteractEvent; // CraftBukkit

public class BlockPressurePlateWeighted extends BlockPressurePlateAbstract {
	private final int a;

	protected BlockPressurePlateWeighted(String s, Material material, int i) {
		super(s, material);
		a = i;
	}

	@Override
	protected int e(World world, int i, int j, int k) {
		// CraftBukkit start
		int l = 0;
		java.util.Iterator iterator = world.a(Entity.class, this.a(i, j, k)).iterator();

		while (iterator.hasNext()) {
			Entity entity = (Entity) iterator.next();

			org.bukkit.event.Cancellable cancellable;

			if (entity instanceof EntityHuman) {
				cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, i, j, k, -1, null);
			} else {
				cancellable = new EntityInteractEvent(entity.getBukkitEntity(), world.getWorld().getBlockAt(i, j, k));
				world.getServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
			}

			// We only want to block turning the plate on if all events are cancelled
			if (!cancellable.isCancelled()) {
				l++;
			}
		}

		l = Math.min(l, a);
		// CraftBukkit end

		if (l <= 0)
			return 0;

		float f = (float) Math.min(a, l) / (float) a;
		return MathHelper.f(f * 15.0F);
	}

	@Override
	protected int c(int i) {
		return i;
	}

	@Override
	protected int d(int i) {
		return i;
	}

	@Override
	public int a(World world) {
		return 10;
	}
}
