package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPowder extends Block{

	private IIcon icon_top;
	private IIcon icon_bottom;
	
	
	public BlockPowder() {
		super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	/**
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_1_ == 1 ? this.icon_top : 
        	p_149691_1_ == 0 ? this.icon_bottom : 
        		this.blockIcon;
    }
    
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon(this.getTextureName() + "_side");
        this.icon_top = p_149651_1_.registerIcon(this.getTextureName() + "_top");
        this.icon_bottom = p_149651_1_.registerIcon(this.getTextureName() + "_bottom");
    }
    
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Items.gunpowder;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 3 + p_149745_1_.nextInt(5);
    }


}
