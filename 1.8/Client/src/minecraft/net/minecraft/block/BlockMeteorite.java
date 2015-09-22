package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
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
    
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.randomDisplayTick(worldIn, pos, state, rand);

        if (rand.nextInt(2) == 0)
        {
            worldIn.spawnParticle(EnumParticleTypes.FLAME, (double)((float)pos.getX() + rand.nextFloat()), (double)((float)pos.getY() + 1.1F), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.1D, 0.0D, new int[0]);
        }
    }
}
