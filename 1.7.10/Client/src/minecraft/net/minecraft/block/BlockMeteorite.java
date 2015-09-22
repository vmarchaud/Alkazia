package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockMeteorite extends Block
{
    public BlockMeteorite() {
		super(Material.rock);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}


    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 0;
    }
    
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        super.randomDisplayTick(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_, p_149734_5_);

        if (p_149734_5_.nextInt(1) == 0)
        {
            p_149734_1_.spawnParticle("flame", (double)((float)p_149734_2_ + p_149734_5_.nextFloat()), (double)((float)p_149734_3_ + 1.0F), (double)((float)p_149734_4_ + p_149734_5_.nextFloat()), 0.0D, 0.1D, 0.0D);
        }
    }
}
