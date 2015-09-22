package net.minecraft.server;

public class PathfinderGoalBreakDoor extends PathfinderGoalDoorInteract {

	private int i;
	private int j = -1;

	public PathfinderGoalBreakDoor(EntityInsentient entityinsentient) {
		super(entityinsentient);
	}

	@Override
	public boolean a() {
		return !super.a() ? false : !a.world.getGameRules().getBoolean("mobGriefing") ? false : !e.f((IBlockAccess) a.world, b, c, d); // CraftBukkit - Fix decompilation issue by casting world to IBlockAccess
	}

	@Override
	public void c() {
		super.c();
		i = 0;
	}

	@Override
	public boolean b() {
		double d0 = a.e(b, c, d);

		return i <= 240 && !e.f((IBlockAccess) a.world, b, c, d) && d0 < 4.0D; // CraftBukkit - Fix decompilation issue by casting world to IBlockAccess
	}

	@Override
	public void d() {
		super.d();
		a.world.d(a.getId(), b, c, d, -1);
	}

	@Override
	public void e() {
		super.e();
		if (a.aI().nextInt(20) == 0) {
			a.world.triggerEffect(1010, b, c, d, 0);
		}

		++i;
		int i = (int) (this.i / 240.0F * 10.0F);

		if (i != j) {
			a.world.d(a.getId(), b, c, d, i);
			j = i;
		}

		if (this.i == 240 && a.world.difficulty == EnumDifficulty.HARD) {
			// CraftBukkit start
			if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityBreakDoorEvent(a, b, c, d).isCancelled()) {
				c();
				return;
			}
			// CraftBukkit end

			a.world.setAir(b, c, d);
			a.world.triggerEffect(1012, b, c, d, 0);
			a.world.triggerEffect(2001, b, c, d, Block.getId(e));
		}
	}
}
