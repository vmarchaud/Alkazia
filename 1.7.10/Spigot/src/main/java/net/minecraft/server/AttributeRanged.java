package net.minecraft.server;

public class AttributeRanged extends AttributeBase {

	private final double a;
	public double b; // Spigot
	private String c;

	public AttributeRanged(String s, double d0, double d1, double d2) {
		super(s, d0);
		a = d1;
		b = d2;
		if (d1 > d2)
			throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
		else if (d0 < d1)
			throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
		else if (d0 > d2)
			throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
	}

	public AttributeRanged a(String s) {
		c = s;
		return this;
	}

	public String f() {
		return c;
	}

	@Override
	public double a(double d0) {
		if (d0 < a) {
			d0 = a;
		}

		if (d0 > b) {
			d0 = b;
		}

		return d0;
	}
}
