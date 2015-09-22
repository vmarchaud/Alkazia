package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class PathfinderGoalHurtByTarget extends PathfinderGoalTarget {

	boolean a;
	private int b;

	public PathfinderGoalHurtByTarget(EntityCreature entitycreature, boolean flag) {
		super(entitycreature, false);
		a = flag;
		this.a(1);
	}

	@Override
	public boolean a() {
		int i = c.aK();

		return i != b && this.a(c.getLastDamager(), false);
	}

	@Override
	public void c() {
		c.setGoalTarget(c.getLastDamager());
		b = c.aK();
		if (a) {
			double d0 = f();
			List list = c.world.a(c.getClass(), AxisAlignedBB.a(c.locX, c.locY, c.locZ, c.locX + 1.0D, c.locY + 1.0D, c.locZ + 1.0D).grow(d0, 10.0D, d0));
			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityCreature entitycreature = (EntityCreature) iterator.next();

				if (c != entitycreature && entitycreature.getGoalTarget() == null && !entitycreature.c(c.getLastDamager())) {
					// CraftBukkit start - call EntityTargetEvent
					org.bukkit.event.entity.EntityTargetLivingEntityEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetLivingEvent(entitycreature, c.getLastDamager(), org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY);
					if (event.isCancelled()) {
						continue;
					}
					entitycreature.setGoalTarget(event.getTarget() == null ? null : ((org.bukkit.craftbukkit.entity.CraftLivingEntity) event.getTarget()).getHandle());
					// CraftBukkit end
				}
			}
		}

		super.c();
	}
}
