package net.minecraft.server;

import java.util.Random;

public class BlockPresent extends Block{

	protected BlockPresent() {
		super(Material.STONE);
		this.a(CreativeModeTab.b);
	}
	
	public Item getDropType(int i, Random random, int j) {
        return Items.stuff;
    }

    public int a(Random random) {
        return 3 + random.nextInt(5);
    }

}
