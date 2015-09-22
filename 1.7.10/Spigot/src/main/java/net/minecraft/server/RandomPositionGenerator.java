package net.minecraft.server;

import java.util.Random;

public class RandomPositionGenerator {

	private static Vec3D a = Vec3D.a(0.0D, 0.0D, 0.0D);

	public static Vec3D a(EntityCreature entitycreature, int i, int j) {
		return c(entitycreature, i, j, (Vec3D) null);
	}

	public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
		a.a = vec3d.a - entitycreature.locX;
		a.b = vec3d.b - entitycreature.locY;
		a.c = vec3d.c - entitycreature.locZ;
		return c(entitycreature, i, j, a);
	}

	public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
		a.a = entitycreature.locX - vec3d.a;
		a.b = entitycreature.locY - vec3d.b;
		a.c = entitycreature.locZ - vec3d.c;
		return c(entitycreature, i, j, a);
	}

	private static Vec3D c(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
		Random random = entitycreature.aI();
		boolean flag = false;
		// PaperSpigot start - int -> double
		double k = 0;
		double l = 0;
		double i1 = 0;
		// PaperSpigot end
		float f = -99999.0F;
		boolean flag1;

		if (entitycreature.bY()) {
			double d0 = entitycreature.bV().e(MathHelper.floor(entitycreature.locX), MathHelper.floor(entitycreature.locY), MathHelper.floor(entitycreature.locZ)) + 4.0F;
			double d1 = entitycreature.bW() + i;

			flag1 = d0 < d1 * d1;
		} else {
			flag1 = false;
		}

		for (int j1 = 0; j1 < 10; ++j1) {
			// PaperSpigot start - Even distribution and average of 0
			int k1 = random.nextInt(2 * i + 1) - i;
			int l1 = random.nextInt(2 * j + 1) - j;
			int i2 = random.nextInt(2 * i + 1) - i;
			// PaperSpigot end

			if (vec3d == null || k1 * vec3d.a + i2 * vec3d.c >= 0.0D) {
				// PaperSpigot start - Use truncated absolute destination position for checking things
				int k1Mod = k1 + MathHelper.floor(entitycreature.locX);
				int l1Mod = l1 + MathHelper.floor(entitycreature.locY);
				int i2Mod = i2 + MathHelper.floor(entitycreature.locZ);
				if (!flag1 || entitycreature.b(k1Mod, l1Mod, i2Mod)) {
					float f1 = entitycreature.a(k1Mod, l1Mod, i2Mod);

					if (f1 > f) {
						f = f1;
						// but then the full value to set where to move
						k = entitycreature.locX + k1;
						l = entitycreature.locY + l1;
						i1 = entitycreature.locZ + i2;
						// PaperSpigot end
						flag = true;
					}
				}
			}
		}

		if (flag)
			return Vec3D.a(k, l, i1); // PaperSpigot remove unnecessary cast
		else
			return null;
	}
}
