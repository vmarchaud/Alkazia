package net.minecraft.server;

public class RecipeIngots
{
  private Object[][] a = { { Blocks.GOLD_BLOCK, new ItemStack(Items.GOLD_INGOT, 9) }, 
		  { Blocks.IRON_BLOCK, new ItemStack(Items.IRON_INGOT, 9) }, 
		  { Blocks.DIAMOND_BLOCK, new ItemStack(Items.DIAMOND, 9) }, 
		  { Blocks.EMERALD_BLOCK, new ItemStack(Items.EMERALD, 9) }, 
		  { Blocks.LAPIS_BLOCK, new ItemStack(Items.DYE, 9, EnumColor.BLUE.getInvColorIndex()) }, 
		  { Blocks.REDSTONE_BLOCK, new ItemStack(Items.REDSTONE, 9) }, 
		  { Blocks.COAL_BLOCK, new ItemStack(Items.COAL, 9, 0) }, 
		  { Blocks.HAY_BLOCK, new ItemStack(Items.WHEAT, 9) }, 
		  { Blocks.SLIME, new ItemStack(Items.SLIME, 9) },
  		{ Blocks.BAUXITE_BLOCK, new ItemStack(Items.BAUXITE_INGOT, 9, 0)}, 
  		{ Blocks.GRANITE_BLOCK, new ItemStack(Items.GRANITE, 9, 0)}, 
  		{ Blocks.OPALE_BLOCK, new ItemStack(Items.OPALE, 9, 0)}, 
  		{ Blocks.METEOR_BLOCK, new ItemStack(Items.METEOR_FRAGMENT, 9, 0)}};

  public void a(CraftingManager paramCraftingManager)
  {
    for (int i = 0; i < this.a.length; i++) {
      Block localBlock = (Block)this.a[i][0];
      ItemStack localItemStack = (ItemStack)this.a[i][1];
      paramCraftingManager.registerShapedRecipe(new ItemStack(localBlock), new Object[] { "###", "###", "###", Character.valueOf('#'), localItemStack });

      paramCraftingManager.registerShapedRecipe(localItemStack, new Object[] { "#", Character.valueOf('#'), localBlock });
    }

    paramCraftingManager.registerShapedRecipe(new ItemStack(Items.GOLD_INGOT), new Object[] { "###", "###", "###", Character.valueOf('#'), Items.GOLD_NUGGET });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Items.GOLD_NUGGET, 9), new Object[] { "#", Character.valueOf('#'), Items.GOLD_INGOT });
  }
}