package net.minecraft.server;

public class ControllerMove {

	private EntityInsentient a;
	private double b;
	private double c;
	private double d;
	private double e;
	private boolean f;

	public ControllerMove(EntityInsentient entityinsentient) {
		a = entityinsentient;
		b = entityinsentient.locX;
		c = entityinsentient.locY;
		d = entityinsentient.locZ;
	}

	public boolean a() {
		return f;
	}

	public double b() {
		return e;
	}

	public void a(double d0, double d1, double d2, double d3) {
		b = d0;
		c = d1;
		d = d2;
		e = d3;
		f = true;
	}

	public void c() {
		a.n(0.0F);
		if (f) {
			f = false;
			int i = MathHelper.floor(a.boundingBox.b + 0.5D);
			double d0 = b - a.locX;
			double d1 = d - a.locZ;
			double d2 = c - i;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;

			if (d3 >= 2.500000277905201E-7D) {
				// CraftBukkit - Math -> TrigMath
				float f = (float) (org.bukkit.craftbukkit.TrigMath.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;

				a.yaw = this.a(a.yaw, f, 30.0F);
				a.i((float) (e * a.getAttributeInstance(GenericAttributes.d).getValue()));
				if (d2 > 0.0D && d0 * d0 + d1 * d1 < 1.0D) {
					a.getControllerJump().a();
				}
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
