package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityPiston extends TileEntity {

	private Block a;
	private int i;
	private int j;
	private boolean k;
	private boolean l;
	private float m;
	private float n;
	private List o = new ArrayList();

	public TileEntityPiston() {
	}

	public TileEntityPiston(Block block, int i, int j, boolean flag, boolean flag1) {
		a = block;
		this.i = i;
		this.j = j;
		k = flag;
		l = flag1;
	}

	public Block a() {
		return a;
	}

	@Override
	public int p() {
		return i;
	}

	public boolean b() {
		return k;
	}

	public int c() {
		return j;
	}

	public float a(float f) {
		if (f > 1.0F) {
			f = 1.0F;
		}

		return n + (m - n) * f;
	}

	private void a(float f, float f1) {
		if (k) {
			f = 1.0F - f;
		} else {
			--f;
		}

		AxisAlignedBB axisalignedbb = Blocks.PISTON_MOVING.a(world, x, y, z, a, f, j);

		if (axisalignedbb != null) {
			List list = world.getEntities((Entity) null, axisalignedbb);

			if (!list.isEmpty()) {
				o.addAll(list);
				Iterator iterator = o.iterator();

				while (iterator.hasNext()) {
					Entity entity = (Entity) iterator.next();

					entity.move(f1 * Facing.b[j], f1 * Facing.c[j], f1 * Facing.d[j]);
				}

				o.clear();
			}
		}
	}

	public void f() {
		if (n < 1.0F && world != null) {
			n = m = 1.0F;
			world.p(x, y, z);
			s();
			if (world.getType(x, y, z) == Blocks.PISTON_MOVING) {
				world.setTypeAndData(x, y, z, a, i, 3);
				world.e(x, y, z, a);
			}
		}
	}

	@Override
	public void h() {
		if (world == null)
			return; // CraftBukkit

		n = m;
		if (n >= 1.0F) {
			this.a(1.0F, 0.25F);
			world.p(x, y, z);
			s();
			if (world.getType(x, y, z) == Blocks.PISTON_MOVING) {
				world.setTypeAndData(x, y, z, a, i, 3);
				world.e(x, y, z, a);
			}
		} else {
			m += 0.5F;
			if (m >= 1.0F) {
				m = 1.0F;
			}

			if (k) {
				this.a(m, m - n + 0.0625F);
			}
		}
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		a = Block.getById(nbttagcompound.getInt("blockId"));
		i = nbttagcompound.getInt("blockData");
		j = nbttagcompound.getInt("facing");
		n = m = nbttagcompound.getFloat("progress");
		k = nbttagcompound.getBoolean("extending");
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("blockId", Block.getId(a));
		nbttagcompound.setInt("blockData", i);
		nbttagcompound.setInt("facing", j);
		nbttagcompound.setFloat("progress", n);
		nbttagcompound.setBoolean("extending", k);
	}
}
