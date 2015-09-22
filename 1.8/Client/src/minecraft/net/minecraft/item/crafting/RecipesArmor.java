package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesArmor
{
    private String[][] recipePatterns = new String[][] {{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
    private Object[][] recipeItems;
    private static final String __OBFID = "CL_00000080";

    public RecipesArmor()
    {
    	// Alkazia - add armor crafting
        this.recipeItems = new Object[][] {{Items.leather, Blocks.fire, Items.iron_ingot, Items.diamond, Items.gold_ingot, Items.bauxite_ingot, Items.graniteItem, Items.opale, Items.meteor_ingot, Items.ninjaPowder}, 
        		{Items.leather_helmet, Items.chainmail_helmet, Items.iron_helmet, Items.diamond_helmet, Items.golden_helmet, Items.bauxite_helmet, Items.granite_helmet, Items.opale_helmet, Items.meteor_helmet, Items.ninja_helmet},
        		{Items.leather_chestplate, Items.chainmail_chestplate, Items.iron_chestplate, Items.diamond_chestplate, Items.golden_chestplate, Items.bauxite_chestplate, Items.granite_chestplate, Items.opale_chestplate, Items.meteor_chestplate, Items.ninja_chestplate}, 
        		{Items.leather_leggings, Items.chainmail_leggings, Items.iron_leggings, Items.diamond_leggings, Items.golden_leggings, Items.bauxite_leggings, Items.granite_leggings, Items.opale_leggings, Items.meteor_leggings, Items.ninja_leggings}, 
        		{Items.leather_boots, Items.chainmail_boots, Items.iron_boots, Items.diamond_boots, Items.golden_boots, Items.bauxite_boots, Items.granite_boots, Items.opale_boots, Items.meteor_boots, Items.ninja_boots}}; 
    }

    /**
     * Adds the armor recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        for (int var2 = 0; var2 < this.recipeItems[0].length; ++var2)
        {
            Object var3 = this.recipeItems[0][var2];

            for (int var4 = 0; var4 < this.recipeItems.length - 1; ++var4)
            {
                Item var5 = (Item)this.recipeItems[var4 + 1][var2];
                par1CraftingManager.addRecipe(new ItemStack(var5), new Object[] {this.recipePatterns[var4], 'X', var3});
            }
        }
    }
}
