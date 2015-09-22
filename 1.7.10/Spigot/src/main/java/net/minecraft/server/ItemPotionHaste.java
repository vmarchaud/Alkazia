package net.minecraft.server;


import org.bukkit.ChatColor;

public class ItemPotionHaste extends Item {

	private int meta;
    public ItemPotionHaste(int meta) {
        this.e(1);
        this.meta = meta;
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
        	if(meta == 0 ) {
            	entityhuman.addEffect(new MobEffect(MobEffectList.FASTER_DIG.id, 4800, 0));
        	}
        	else if(meta ==1) {
            	entityhuman.addEffect(new MobEffect(MobEffectList.FASTER_DIG.id, 2400, 0));
        	}
        }

        return itemstack.count <= 0 ? new ItemStack(Items.GLASS_BOTTLE) : itemstack;
    }

    public int d_(ItemStack itemstack) {
        return 20;
    }

    public EnumAnimation d(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        entityhuman.a(itemstack, this.d_(itemstack));
        return itemstack;
    }
    
}