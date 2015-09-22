package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityMinecartAbstract;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Minecart;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
	public CraftMinecart(CraftServer server, EntityMinecartAbstract entity) {
		super(server, entity);
	}

	@Override
	public void setDamage(double damage) {
		getHandle().setDamage((float) damage);
	}

	@Override
	public double getDamage() {
		return getHandle().getDamage();
	}

	@Override
	public double getMaxSpeed() {
		return getHandle().maxSpeed;
	}

	@Override
	public void setMaxSpeed(double speed) {
		if (speed >= 0D) {
			getHandle().maxSpeed = speed;
		}
	}

	@Override
	public boolean isSlowWhenEmpty() {
		return getHandle().slowWhenEmpty;
	}

	@Override
	public void setSlowWhenEmpty(boolean slow) {
		getHandle().slowWhenEmpty = slow;
	}

	@Override
	public Vector getFlyingVelocityMod() {
		return getHandle().getFlyingVelocityMod();
	}

	@Override
	public void setFlyingVelocityMod(Vector flying) {
		getHandle().setFlyingVelocityMod(flying);
	}

	@Override
	public Vector getDerailedVelocityMod() {
		return getHandle().getDerailedVelocityMod();
	}

	@Override
	public void setDerailedVelocityMod(Vector derailed) {
		getHandle().setDerailedVelocityMod(derailed);
	}

	@Override
	public EntityMinecartAbstract getHandle() {
		return (EntityMinecartAbstract) entity;
	}

	@Override
	@Deprecated
	public void _INVALID_setDamage(int damage) {
		setDamage(damage);
	}

	@Override
	@Deprecated
	public int _INVALID_getDamage() {
		return NumberConversions.ceil(getDamage());
	}
}
