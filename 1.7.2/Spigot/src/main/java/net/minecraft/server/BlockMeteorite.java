package net.minecraft.server;

import java.util.Random;

public class BlockMeteorite extends Block {

    public BlockMeteorite() {
    	super(Material.STONE);
    	this.a(true);
        this.a(CreativeModeTab.b);
    }

    public int a(Random random) {
        return 0;
    }

    public Item getDropType(int i, Random random, int j) {
        return Item.getItemOf(Blocks.METEORITE);
    }

}