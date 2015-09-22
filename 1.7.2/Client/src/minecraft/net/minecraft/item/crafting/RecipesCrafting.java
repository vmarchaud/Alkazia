package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesCrafting
{
    private static final String __OBFID = "CL_00000095";

    /**
     * Adds the crafting recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager par1CraftingManager)
    {
        par1CraftingManager.addRecipe(new ItemStack(Blocks.chest), new Object[] {"###", "# #", "###", '#', Blocks.planks});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.trapped_chest), new Object[] {"#-", '#', Blocks.chest, '-', Blocks.tripwire_hook});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.ender_chest), new Object[] {"###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.furnace), new Object[] {"###", "# #", "###", '#', Blocks.cobblestone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.crafting_table), new Object[] {"##", "##", '#', Blocks.planks});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone), new Object[] {"##", "##", '#', new ItemStack(Blocks.sand, 1, 0)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 4, 2), new Object[] {"##", "##", '#', Blocks.sandstone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.sandstone, 1, 1), new Object[] {"#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 1)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 1, 1), new Object[] {"#", "#", '#', new ItemStack(Blocks.stone_slab, 1, 7)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.quartz_block, 2, 2), new Object[] {"#", "#", '#', new ItemStack(Blocks.quartz_block, 1, 0)});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.stonebrick, 4), new Object[] {"##", "##", '#', Blocks.stone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.iron_bars, 16), new Object[] {"###", "###", '#', Items.iron_ingot});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.glass_pane, 16), new Object[] {"###", "###", '#', Blocks.glass});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), new Object[] {" R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.beacon, 1), new Object[] {"GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.nether_brick, 1), new Object[] {"NN", "NN", 'N', Items.netherbrick});
        
        // Alkazia - add new recipes
        par1CraftingManager.addRecipe(new ItemStack(Blocks.pousse), new Object[] {"# #", " # ", "# #", '#', Blocks.cobblestone});
        par1CraftingManager.addRecipe(new ItemStack(Items.name_tag), new Object[] {"*##", '#', Items.paper, '*', Items.string});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.iron_chest), new Object[] {"###", "#X#", "###", '#', Items.iron_ingot, 'X', Blocks.chest});
        
        par1CraftingManager.addRecipe(new ItemStack(Blocks.alumite_smooth), new Object[] {"##", "##", '#', Blocks.alumite});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.andesite_smooth), new Object[] {"##", "##", '#', Blocks.andesite});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.diorite_smooth), new Object[] {"##", "##", '#', Blocks.diorite});
        
        par1CraftingManager.addRecipe(new ItemStack(Items.potionFall), new Object[] {"#O#",'#', Items.feather, 'O', Items.potionitem});
        par1CraftingManager.addRecipe(new ItemStack(Blocks.ironLadder, 4), new Object[] {"# #", "###", "# #",'#', Items.iron_ingot});
        
        
    }
}
