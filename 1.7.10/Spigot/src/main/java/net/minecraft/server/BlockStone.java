package net.minecraft.server;

import java.util.Random;

public class BlockStone extends Block {
	
	public BlockStone() {
		super(Material.STONE);
		a(CreativeModeTab.b);
	}

	@Override
	public Item getDropType(int data, Random rand, int arg2) {
		// ClipSpigot start - correct 1.8 stone data
		if(data == 0)
			return Item.getItemOf(Blocks.COBBLESTONE);		
		return super.getDropType(data, rand, arg2);
		// ClipSpigot end
	}

	@Override
	public int getDropData(int paramInt) {
		return paramInt;
	}
}