/*    */ package net.minecraft.server;
/*    */ 
/*    */ public class ItemChocolate extends ItemFood
/*    */ {
/*    */   public ItemChocolate(int paramInt, float paramFloat, boolean paramBoolean)
/*    */   {
/* 12 */     super(paramInt, paramFloat, paramBoolean);
/*    */   }
/*    */ 
/*    */   protected void c(ItemStack paramItemStack, World paramWorld, EntityHuman paramEntityHuman)
/*    */   {
/* 35 */       if (!(paramWorld.isStatic)) {
/* 36 */         paramEntityHuman.addEffect(new MobEffect(MobEffectList.INCREASE_DAMAGE.id, 250, 0));
/*    */       }
			}
/*    */ }

/* Location:           C:\Users\Mac'\.m2\repository\org\bukkit\minecraft-server\1.7.2\minecraft-server-1.7.2.jar
 * Qualified Name:     net.minecraft.server.ItemGoldenApple
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */