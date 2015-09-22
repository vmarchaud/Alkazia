package net.minecraft.server;

public class ItemStuff extends ItemFood {

    public ItemStuff(int i, float f, boolean flag) {
        super(i, f, flag);
    }

    protected void c(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isStatic) {
            entityhuman.addEffect(new MobEffect(MobEffectList.INCREASE_DAMAGE.id, 1200, 0));
        }
    }
}