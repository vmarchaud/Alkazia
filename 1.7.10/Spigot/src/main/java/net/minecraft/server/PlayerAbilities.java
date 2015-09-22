package net.minecraft.server;

public class PlayerAbilities {

	public boolean isInvulnerable;
	public boolean isFlying;
	public boolean canFly;
	public boolean canInstantlyBuild;
	public boolean mayBuild = true;
	public float flySpeed = 0.05F; // CraftBukkit private -> public
	public float walkSpeed = 0.1F; // CraftBukkit private -> public

	public PlayerAbilities() {
	}

	public void a(NBTTagCompound nbttagcompound) {
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();

		nbttagcompound1.setBoolean("invulnerable", isInvulnerable);
		nbttagcompound1.setBoolean("flying", isFlying);
		nbttagcompound1.setBoolean("mayfly", canFly);
		nbttagcompound1.setBoolean("instabuild", canInstantlyBuild);
		nbttagcompound1.setBoolean("mayBuild", mayBuild);
		nbttagcompound1.setFloat("flySpeed", flySpeed);
		nbttagcompound1.setFloat("walkSpeed", walkSpeed);
		nbttagcompound.set("abilities", nbttagcompound1);
	}

	public void b(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKeyOfType("abilities", 10)) {
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("abilities");

			isInvulnerable = nbttagcompound1.getBoolean("invulnerable");
			isFlying = nbttagcompound1.getBoolean("flying");
			canFly = nbttagcompound1.getBoolean("mayfly");
			canInstantlyBuild = nbttagcompound1.getBoolean("instabuild");
			if (nbttagcompound1.hasKeyOfType("flySpeed", 99)) {
				flySpeed = nbttagcompound1.getFloat("flySpeed");
				walkSpeed = nbttagcompound1.getFloat("walkSpeed");
			}

			if (nbttagcompound1.hasKeyOfType("mayBuild", 1)) {
				mayBuild = nbttagcompound1.getBoolean("mayBuild");
			}
		}
	}

	public float a() {
		return flySpeed;
	}

	public float b() {
		return walkSpeed;
	}
}
