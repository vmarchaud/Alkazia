package net.minecraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotionResistance extends ItemFood
{
	private int meta;
	
	
    public ItemPotionResistance(int meta)
    {
    	super(0, 0, false);
    	this.meta = meta;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabBrewing);
        // Alkazia - Add milk Potion
    }
    
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }

    public ItemStack onItemUseFinish(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        if (!par2World.isRemote)
        {
        	if(this.meta == 0) {
            	par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 4800, 0));
            }
            else if(this.meta == 1){
            	par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 2400, 1));
            }
        }

        return par1ItemStack.stackSize <= 0 ? new ItemStack(Items.glass_bottle) : par1ItemStack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.DRINK;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }
    
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_)
    {
    	if(this.meta == 0) {
        	p_77624_3_.add(EnumChatFormatting.GRAY + "Resistance (4:00)");
            p_77624_3_.add("");
            p_77624_3_.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            p_77624_3_.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("+20% Resistance"));
    	}
    	else if(this.meta == 1) {
        	p_77624_3_.add(EnumChatFormatting.GRAY + "Resistance II (2:00)");
            p_77624_3_.add("");
            p_77624_3_.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            p_77624_3_.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("+40% Resistance"));
    	}
                
    }
}
