package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.util.UnsafeList; // CraftBukkit

public class PathfinderGoalSelector {

	private static final Logger a = LogManager.getLogger();
	// CraftBukkit start - ArrayList -> UnsafeList
	private List b = new UnsafeList();
	private List c = new UnsafeList();
	// CraftBukkit end
	private final MethodProfiler d;
	private int e;
	private int f = 3;

	public PathfinderGoalSelector(MethodProfiler methodprofiler) {
		d = methodprofiler;
	}

	public void a(int i, PathfinderGoal pathfindergoal) {
		b.add(new PathfinderGoalSelectorItem(this, i, pathfindergoal));
	}

	public void a(PathfinderGoal pathfindergoal) {
		Iterator iterator = b.iterator();

		while (iterator.hasNext()) {
			PathfinderGoalSelectorItem pathfindergoalselectoritem = (PathfinderGoalSelectorItem) iterator.next();
			PathfinderGoal pathfindergoal1 = pathfindergoalselectoritem.a;

			if (pathfindergoal1 == pathfindergoal) {
				if (c.contains(pathfindergoalselectoritem)) {
					pathfindergoal1.d();
					c.remove(pathfindergoalselectoritem);
				}

				iterator.remove();
			}
		}
	}

	public void a() {
		// ArrayList arraylist = new ArrayList(); // CraftBukkit - remove usage
		Iterator iterator;
		PathfinderGoalSelectorItem pathfindergoalselectoritem;

		if (e++ % f == 0) {
			iterator = b.iterator();

			while (iterator.hasNext()) {
				pathfindergoalselectoritem = (PathfinderGoalSelectorItem) iterator.next();
				boolean flag = c.contains(pathfindergoalselectoritem);

				if (flag) {
					if (b(pathfindergoalselectoritem) && this.a(pathfindergoalselectoritem)) {
						continue;
					}

					pathfindergoalselectoritem.a.d();
					c.remove(pathfindergoalselectoritem);
				}

				if (b(pathfindergoalselectoritem) && pathfindergoalselectoritem.a.a()) {
					// CraftBukkit start - call method now instead of queueing
					// arraylist.add(pathfindergoalselectoritem);
					pathfindergoalselectoritem.a.c();
					// CraftBukkit end
					c.add(pathfindergoalselectoritem);
				}
			}
		} else {
			iterator = c.iterator();

			while (iterator.hasNext()) {
				pathfindergoalselectoritem = (PathfinderGoalSelectorItem) iterator.next();
				if (!pathfindergoalselectoritem.a.b()) {
					pathfindergoalselectoritem.a.d();
					iterator.remove();
				}
			}
		}

		d.a("goalStart");
		// CraftBukkit start - removed usage of arraylist
		/*iterator = arraylist.iterator();

		while (iterator.hasNext()) {
		    pathfindergoalselectoritem = (PathfinderGoalSelectorItem) iterator.next();
		    this.d.a(pathfindergoalselectoritem.a.getClass().getSimpleName());
		    pathfindergoalselectoritem.a.c();
		    this.d.b();
		}*/
		// CraftBukkit end

		d.b();
		d.a("goalTick");
		iterator = c.iterator();

		while (iterator.hasNext()) {
			pathfindergoalselectoritem = (PathfinderGoalSelectorItem) iterator.next();
			pathfindergoalselectoritem.a.e();
		}

		d.b();
	}

	private boolean a(PathfinderGoalSelectorItem pathfindergoalselectoritem) {
		d.a("canContinue");
		boolean flag = pathfindergoalselectoritem.a.b();

		d.b();
		return flag;
	}

	private boolean b(PathfinderGoalSelectorItem pathfindergoalselectoritem) {
		d.a("canUse");
		Iterator iterator = b.iterator();

		while (iterator.hasNext()) {
			PathfinderGoalSelectorItem pathfindergoalselectoritem1 = (PathfinderGoalSelectorItem) iterator.next();

			if (pathfindergoalselectoritem1 != pathfindergoalselectoritem) {
				if (pathfindergoalselectoritem.b >= pathfindergoalselectoritem1.b) {
					// CraftBukkit - switch order
					if (!this.a(pathfindergoalselectoritem, pathfindergoalselectoritem1) && c.contains(pathfindergoalselectoritem1)) {
						d.b();
						((UnsafeList.Itr) iterator).valid = false; // CraftBukkit - mark iterator for reuse
						return false;
					}
					// CraftBukkit - switch order
				} else if (!pathfindergoalselectoritem1.a.i() && c.contains(pathfindergoalselectoritem1)) {
					d.b();
					((UnsafeList.Itr) iterator).valid = false; // CraftBukkit - mark iterator for reuse
					return false;
				}
			}
		}

		d.b();
		return true;
	}

	private boolean a(PathfinderGoalSelectorItem pathfindergoalselectoritem, PathfinderGoalSelectorItem pathfindergoalselectoritem1) {
		return (pathfindergoalselectoritem.a.j() & pathfindergoalselectoritem1.a.j()) == 0;
	}
}
