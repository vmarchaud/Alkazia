package net.minecraft.server;

public class ItemPotionMilk extends Item {

    public ItemPotionMilk() {
        this.e(1);
    }

    public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            --itemstack.count;
        }

        if (!world.isStatic) {
            entityhuman.clearNegativeEffect();
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