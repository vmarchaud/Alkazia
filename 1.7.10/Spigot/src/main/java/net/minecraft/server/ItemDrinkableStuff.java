package net.minecraft.server;

import org.bukkit.ChatColor;

public class ItemDrinkableStuff extends Item {

    public ItemDrinkableStuff() {
        this.e(1);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
        	entityhuman.addEffect(new MobEffect(MobEffectList.FIRE_RESISTANCE.id, 600, 0));
        	entityhuman.addEffect(new MobEffect(MobEffectList.RESISTANCE.id, 600, 0));
        }

        return itemstack;
    }

    public int d_(ItemStack itemstack) {
        return 40;
    }

    public EnumAnimation d(ItemStack itemstack) {
        return EnumAnimation.DRINK;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        entityhuman.a(itemstack, this.d_(itemstack));
        return itemstack;
    }
    

    public String n(ItemStack itemstack) {
    	return ChatColor.GRAY + "Réchauffez-vous avec ceci !";
    }
    
}