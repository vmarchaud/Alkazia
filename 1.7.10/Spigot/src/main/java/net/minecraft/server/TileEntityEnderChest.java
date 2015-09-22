package net.minecraft.server;

public class TileEntityEnderChest extends TileEntity {

	public float a;
	public float i;
	public int j;
	private int k;

	public TileEntityEnderChest() {
	}

	@Override
	public void h() {
		super.h();
		if (++k % 4 == 0) { // PaperSpigot Reduced (20 * 4) -> 4 interval due to reduced tick rate from Improved Tick Handling
			world.playBlockAction(x, y, z, Blocks.ENDER_CHEST, 1, j);
		}

		i = a;

		// PaperSpigot start - Move chest sound handling out of the tick loop
		/*
		float f = 0.1F;
		double d0;

		if (this.j > 0 && this.a == 0.0F) {
		    double d1 = (double) this.x + 0.5D;

		    d0 = (double) this.z + 0.5D;
		    this.world.makeSound(d1, (double) this.y + 0.5D, d0, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (this.j == 0 && this.a > 0.0F || this.j > 0 && this.a < 1.0F) {
		    float f1 = this.a;

		    if (this.j > 0) {
		        this.a += f;
		    } else {
		        this.a -= f;
		    }

		    if (this.a > 1.0F) {
		        this.a = 1.0F;
		    }

		    float f2 = 0.5F;

		    if (this.a < f2 && f1 >= f2) {
		        d0 = (double) this.x + 0.5D;
		        double d2 = (double) this.z + 0.5D;

		        this.world.makeSound(d0, (double) this.y + 0.5D, d2, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		    }

		    if (this.a < 0.0F) {
		        this.a = 0.0F;
		    }
		}
		*/
		// PaperSpigot end
	}

	@Override
	public boolean c(int i, int j) {
		if (i == 1) {
			this.j = j;
			return true;
		} else
			return super.c(i, j);
	}

	@Override
	public void s() {
		u();
		super.s();
	}

	public void a() {
		++j;
		world.playBlockAction(x, y, z, Blocks.ENDER_CHEST, 1, j);

		// PaperSpigot start - Move chest open sound handling down to here
		double d0;

		if (j > 0 && a == 0.0F) {
			double d1 = x + 0.5D;

			d0 = z + 0.5D;
			world.makeSound(d1, y + 0.5D, d0, "random.chestopen", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}
		// PaperSpigot end
	}

	public void b() {
		--j;
		world.playBlockAction(x, y, z, Blocks.ENDER_CHEST, 1, j);

		// PaperSpigot start - Move chest close sound handling down to here
		float f = 0.1F;
		double d0;

		if (j == 0 && a == 0.0F || j > 0 && a < 1.0F) {
			float f1 = a;
			d0 = x + 0.5D;
			double d2 = z + 0.5D;

			world.makeSound(d0, y + 0.5D, d2, "random.chestclosed", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

			if (a < 0.0F) {
				a = 0.0F;
			}
		}
		// PaperSpigot end
	}

	public boolean a(EntityHuman entityhuman) {
		return world.getTileEntity(x, y, z) != this ? false : entityhuman.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
	}
}
