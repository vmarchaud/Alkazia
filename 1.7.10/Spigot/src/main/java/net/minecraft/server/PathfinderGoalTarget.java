package net.minecraft.server;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;

// CraftBukkit end

public abstract class PathfinderGoalTarget extends PathfinderGoal {

	protected EntityCreature c;
	protected boolean d;
	private boolean a;
	private int b;
	private int e;
	private int f;

	public PathfinderGoalTarget(EntityCreature entitycreature, boolean flag) {
		this(entitycreature, flag, false);
	}

	public PathfinderGoalTarget(EntityCreature entitycreature, boolean flag, boolean flag1) {
		c = entitycreature;
		d = flag;
		a = flag1;
	}

	@Override
	public boolean b() {
		EntityLiving entityliving = c.getGoalTarget();

		if (entityliving == null)
			return false;
		else if (!entityliving.isAlive())
			return false;
		else {
			double d0 = f();

			if (c.f(entityliving) > d0 * d0)
				return false;
			else {
				if (d) {
					if (c.getEntitySenses().canSee(entityliving)) {
						f = 0;
					} else if (++f > 60)
						return false;
				}

				return !(entityliving instanceof EntityPlayer) || !((EntityPlayer) entityliving).playerInteractManager.isCreative();
			}
		}
	}

	protected double f() {
		AttributeInstance attributeinstance = c.getAttributeInstance(GenericAttributes.b);

		return attributeinstance == null ? 16.0D : attributeinstance.getValue();
	}

	@Override
	public void c() {
		b = 0;
		e = 0;
		f = 0;
	}

	@Override
	public void d() {
		c.setGoalTarget((EntityLiving) null);
	}

	protected boolean a(EntityLiving entityliving, boolean flag) {
		if (entityliving == null)
			return false;
		else if (entityliving == c)
			return false;
		else if (!entityliving.isAlive())
			return false;
		else if (!c.a(entityliving.getClass()))
			return false;
		else {
			if (c instanceof EntityOwnable && StringUtils.isNotEmpty(((EntityOwnable) c).getOwnerUUID())) {
				if (entityliving instanceof EntityOwnable && ((EntityOwnable) c).getOwnerUUID().equals(((EntityOwnable) entityliving).getOwnerUUID()))
					return false;

				if (entityliving == ((EntityOwnable) c).getOwner())
					return false;
			} else if (entityliving instanceof EntityHuman && !flag && ((EntityHuman) entityliving).abilities.isInvulnerable)
				return false;

			if (!c.b(MathHelper.floor(entityliving.locX), MathHelper.floor(entityliving.locY), MathHelper.floor(entityliving.locZ)))
				return false;
			else if (d && !c.getEntitySenses().canSee(entityliving))
				return false;
			else {
				if (a) {
					if (--e <= 0) {
						b = 0;
					}

					if (b == 0) {
						b = this.a(entityliving) ? 1 : 2;
					}

					if (b == 2)
						return false;
				}

				// CraftBukkit start - Check all the different target goals for the reason, default to RANDOM_TARGET
				EntityTargetEvent.TargetReason reason = EntityTargetEvent.TargetReason.RANDOM_TARGET;

				if (this instanceof PathfinderGoalDefendVillage) {
					reason = EntityTargetEvent.TargetReason.DEFEND_VILLAGE;
				} else if (this instanceof PathfinderGoalHurtByTarget) {
					reason = EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY;
				} else if (this instanceof PathfinderGoalNearestAttackableTarget) {
					if (entityliving instanceof EntityHuman) {
						reason = EntityTargetEvent.TargetReason.CLOSEST_PLAYER;
					}
				} else if (this instanceof PathfinderGoalOwnerHurtByTarget) {
					reason = EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER;
				} else if (this instanceof PathfinderGoalOwnerHurtTarget) {
					reason = EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET;
				}

				org.bukkit.event.entity.EntityTargetLivingEntityEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetLivingEvent(c, entityliving, reason);
				if (event.isCancelled() || event.getTarget() == null) {
					c.setGoalTarget(null);
					return false;
				} else if (entityliving.getBukkitEntity() != event.getTarget()) {
					c.setGoalTarget((EntityLiving) ((CraftEntity) event.getTarget()).getHandle());
				}
				if (c instanceof EntityCreature) {
					c.target = ((CraftEntity) event.getTarget()).getHandle();
				}
				// CraftBukkit end

				return true;
			}
		}
	}

	private boolean a(EntityLiving entityliving) {
		e = 10 + c.aI().nextInt(5);
		PathEntity pathentity = c.getNavigation().a(entityliving);

		if (pathentity == null)
			return false;
		else {
			PathPoint pathpoint = pathentity.c();

			if (pathpoint == null)
				return false;
			else {
				int i = pathpoint.a - MathHelper.floor(entityliving.locX);
				int j = pathpoint.c - MathHelper.floor(entityliving.locZ);

				return i * i + j * j <= 2.25D;
			}
		}
	}
}
