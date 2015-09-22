package net.minecraft.server;


import org.bukkit.ChatColor;

public class ItemPotionFall extends ItemFood {

    public ItemPotionFall() {
    	super(0, 0, false);
    	c(1);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
        	entityhuman.addEffect(new MobEffect(MobEffectList.FALL.id, 2400, 0));
        }

        return itemstack.count <= 0 ? new ItemStack(Items.GLASS_BOTTLE) : itemstack;
    }

    public int d(ItemStack itemstack) {
        return 15;
    }
    
    public String n(ItemStack itemstack) {
    	return ChatColor.GRAY + "Falling Resistance (2:00)";
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