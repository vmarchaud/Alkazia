package me.rellynn.plugins.alkaboat;

import java.util.ArrayList;
import java.util.List;

public class MathsUtils {
    public static class Coord {
	private final int x;
	private final int z;

	public Coord(final int x, final int z) {
	    this.x = x;
	    this.z = z;
	}

	@Override
	public boolean equals(final Object object) {
	    if (object instanceof Coord) {
		final Coord comparedObject = (Coord) object;
		if (comparedObject.getX() == this.getX() && comparedObject.getZ() == this.getZ()) return true;
	    }
	    return false;
	}

	public int getX() {
	    return this.x;
	}

	public int getZ() {
	    return this.z;
	}

	@Override
	public String toString() {
	    return "x=" + this.x + ";z=" + this.z;
	}
    }

    public static List<Coord> getPoints(final int r, final Coord initCord) {
	final List<Coord> coords = new ArrayList<>();

	final int cx = initCord.getX();
	final int cz = initCord.getZ();
	final int rSquared = r * r;
	for (int x = cx - r; x <= cx + r; x++)
	    for (int z = cz - r; z <= cz + r; z++)
		if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) coords.add(new Coord(x, z));

	return coords;
    }
}
