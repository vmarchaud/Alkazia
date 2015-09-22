package net.minecraft.server;


import org.bukkit.ChatColor;

public class ItemPotionResistance extends ItemFood {
	
	private int meta;
    public ItemPotionResistance(int meta) {
    	super(0, 0, false);
    	this.meta = meta;
    	c(1);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
        	if(meta == 0 ) {
            	entityhuman.addEffect(new MobEffect(MobEffectList.RESISTANCE.id, 4800, 0));
        	}
        	else if(meta ==1) {
            	entityhuman.addEffect(new MobEffect(MobEffectList.RESISTANCE.id, 2400, 1));
        	}
        }

        return itemstack.count <= 0 ? new ItemStack(Items.GLASS_BOTTLE) : itemstack;
    }

    public int d(ItemStack itemstack) {
        return 20;
    }
    

    public EnumAnimation e(ItemStack paramItemStack)
    {
      return EnumAnimation.DRINK;
    }
    

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        entityhuman.a(itemstack, this.d(itemstack));
        return itemstack;
    }
    
}