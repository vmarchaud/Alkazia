package net.minecraft.server;

// CraftBukkit start
import org.bukkit.Material;
// CraftBukkit end
import org.bukkit.craftbukkit.event.CraftEventFactory;

public class PathfinderGoalEatTile extends PathfinderGoal {

	private EntityInsentient b;
	private World c;
	int a;

	public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
		b = entityinsentient;
		c = entityinsentient.world;
		this.a(7);
	}

	@Override
	public boolean a() {
		if (b.aI().nextInt(b.isBaby() ? 50 : 1000) != 0)
			return false;
		else {
			int i = MathHelper.floor(b.locX);
			int j = MathHelper.floor(b.locY);
			int k = MathHelper.floor(b.locZ);

			return c.getType(i, j, k) == Blocks.LONG_GRASS && c.getData(i, j, k) == 1 ? true : c.getType(i, j - 1, k) == Blocks.GRASS;
		}
	}

	@Override
	public void c() {
		a = 40;
		c.broadcastEntityEffect(b, (byte) 10);
		b.getNavigation().h();
	}

	@Override
	public void d() {
		a = 0;
	}

	@Override
	public boolean b() {
		return a > 0;
	}

	public int f() {
		return a;
	}

	@Override
	public void e() {
		a = Math.max(0, a - 1);
		if (a == 4) {
			int i = MathHelper.floor(b.locX);
			int j = MathHelper.floor(b.locY);
			int k = MathHelper.floor(b.locZ);

			if (c.getType(i, j, k) == Blocks.LONG_GRASS) {
				// CraftBukkit
				if (!CraftEventFactory.callEntityChangeBlockEvent(b, b.world.getWorld().getBlockAt(i, j, k), Material.AIR, !c.getGameRules().getBoolean("mobGriefing")).isCancelled()) {
					c.setAir(i, j, k, false);
				}

				b.p();
			} else if (c.getType(i, j - 1, k) == Blocks.GRASS) {
				// CraftBukkit
				if (!CraftEventFactory.callEntityChangeBlockEvent(b, b.world.getWorld().getBlockAt(i, j - 1, k), Material.DIRT, !c.getGameRules().getBoolean("mobGriefing")).isCancelled()) {
					c.triggerEffect(2001, i, j - 1, k, Block.getId(Blocks.GRASS));
					c.setTypeAndData(i, j - 1, k, Blocks.DIRT, 0, 2);
				}

				b.p();
			}
		}
	}
}
