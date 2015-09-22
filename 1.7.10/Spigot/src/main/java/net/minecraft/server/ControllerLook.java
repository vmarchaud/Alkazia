package net.minecraft.server;

import org.bukkit.craftbukkit.TrigMath; // CraftBukkit

public class ControllerLook {

	private EntityInsentient a;
	private float b;
	private float c;
	private boolean d;
	private double e;
	private double f;
	private double g;

	public ControllerLook(EntityInsentient entityinsentient) {
		a = entityinsentient;
	}

	public void a(Entity entity, float f, float f1) {
		e = entity.locX;
		if (entity instanceof EntityLiving) {
			this.f = entity.locY + entity.getHeadHeight();
		} else {
			this.f = (entity.boundingBox.b + entity.boundingBox.e) / 2.0D;
		}

		g = entity.locZ;
		b = f;
		c = f1;
		d = true;
	}

	public void a(double d0, double d1, double d2, float f, float f1) {
		e = d0;
		this.f = d1;
		g = d2;
		b = f;
		c = f1;
		d = true;
	}

	public void a() {
		a.pitch = 0.0F;
		if (d) {
			d = false;
			double d0 = e - a.locX;
			double d1 = f - (a.locY + a.getHeadHeight());
			double d2 = g - a.locZ;
			double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
			// CraftBukkit start - Math -> TrigMath
			float f = (float) (TrigMath.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
			float f1 = (float) -(TrigMath.atan2(d1, d3) * 180.0D / 3.1415927410125732D);
			// CraftBukkit end

			a.pitch = this.a(a.pitch, f1, c);
			a.aO = this.a(a.aO, f, b);
		} else {
			a.aO = this.a(a.aO, a.aM, 10.0F);
		}

		float f2 = MathHelper.g(a.aO - a.aM);

		if (!a.getNavigation().g()) {
			if (f2 < -75.0F) {
				a.aO = a.aM - 75.0F;
			}

			if (f2 > 75.0F) {
				a.aO = a.aM + 75.0F;
			}
		}
	}

	private float a(float f, float f1, float f2) {
		float f3 = MathHelper.g(f1 - f);

		if (f3 > f2) {
			f3 = f2;
		}

		if (f3 < -f2) {
			f3 = -f2;
		}

		return f + f3;
	}
}
