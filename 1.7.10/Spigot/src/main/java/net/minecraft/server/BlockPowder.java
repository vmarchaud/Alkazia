package net.minecraft.server;

import java.util.Random;

public class BlockPowder extends Block{

	protected BlockPowder() {
		super(Material.STONE);
		this.a(CreativeModeTab.b);
	}
	
	public Item getDropType(int i, Random random, int j) {
        return Items.SULPHUR;
    }

    public int a(Random random) {
        return 3 + random.nextInt(5);
    }

}
