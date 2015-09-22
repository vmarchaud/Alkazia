package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons
{
    private String[][] recipePatterns = new String[][] {{"X", "X", "#"}};
    private Object[][] recipeItems;
    private static final String __OBFID = "CL_00000097";

    public RecipesWeapons()
    {
    	/** Alkazia */
        this.recipeItems = new Object[][] {{Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot, Items.bauxite_ingot, Items.granite, Items.opale, Items.meteor_fragment},
        		{Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword, Items.bauxite_sword, Items.granite_sword, Items.opale_sword, Items.meteor_sword}};
    }

    /**
     * Adds the weapon recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        for (int var2 = 0; var2 < this.recipeItems[0].length; ++var2)
        {
            Object var3 = this.recipeItems[0][var2];

            for (int var4 = 0; var4 < this.recipeItems.length - 1; ++var4)
            {
                Item var5 = (Item)this.recipeItems[var4 + 1][var2];
                par1CraftingManager.addRecipe(new ItemStack(var5), new Object[] {this.recipePatterns[var4], '#', Items.stick, 'X', var3});
            }
        }

        par1CraftingManager.addRecipe(new ItemStack(Items.bow, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.stick});
        par1CraftingManager.addRecipe(new ItemStack(Items.bowBauxite, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.bauxite_ingot});
        par1CraftingManager.addRecipe(new ItemStack(Items.bowOpale, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.opale});
        par1CraftingManager.addRecipe(new ItemStack(Items.bowGranite, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.granite});
        par1CraftingManager.addRecipe(new ItemStack(Items.bowMeteor, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.meteor_fragment});
        
        par1CraftingManager.addRecipe(new ItemStack(Items.arrow, 4), new Object[] {"X", "#", "Y", 'Y', Items.feather, 'X', Items.flint, '#', Items.stick});
    }
}
