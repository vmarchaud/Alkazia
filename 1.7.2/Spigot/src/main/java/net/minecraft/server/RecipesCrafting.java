package net.minecraft.server;


public class RecipesCrafting {

    public RecipesCrafting() {}

    public void a(CraftingManager craftingmanager) {
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.CHEST), new Object[] { "###", "# #", "###", Character.valueOf('#'), Blocks.WOOD});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.TRAPPED_CHEST), new Object[] { "#-", Character.valueOf('#'), Blocks.CHEST, Character.valueOf('-'), Blocks.TRIPWIRE_SOURCE});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.ENDER_CHEST), new Object[] { "###", "#E#", "###", Character.valueOf('#'), Blocks.OBSIDIAN, Character.valueOf('E'), Items.EYE_OF_ENDER});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.FURNACE), new Object[] { "###", "# #", "###", Character.valueOf('#'), Blocks.COBBLESTONE});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.WORKBENCH), new Object[] { "##", "##", Character.valueOf('#'), Blocks.WOOD});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.SAND, 1, 0)});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 4, 2), new Object[] { "##", "##", Character.valueOf('#'), Blocks.SANDSTONE});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 1, 1), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STEP, 1, 1)});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1, 1), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STEP, 1, 7)});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 2, 2), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.QUARTZ_BLOCK, 1, 0)});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.SMOOTH_BRICK, 4), new Object[] { "##", "##", Character.valueOf('#'), Blocks.STONE});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.IRON_FENCE, 16), new Object[] { "###", "###", Character.valueOf('#'), Items.IRON_INGOT});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.THIN_GLASS, 16), new Object[] { "###", "###", Character.valueOf('#'), Blocks.GLASS});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_LAMP_OFF, 1), new Object[] { " R ", "RGR", " R ", Character.valueOf('R'), Items.REDSTONE, Character.valueOf('G'), Blocks.GLOWSTONE});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.BEACON, 1), new Object[] { "GGG", "GSG", "OOO", Character.valueOf('G'), Blocks.GLASS, Character.valueOf('S'), Items.NETHER_STAR, Character.valueOf('O'), Blocks.OBSIDIAN});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK, 1), new Object[] { "NN", "NN", Character.valueOf('N'), Items.NETHER_BRICK});
        /** Alkazia */
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.POUSSE, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'), Blocks.COBBLESTONE });
        craftingmanager.registerShapedRecipe(new ItemStack(Items.NAME_TAG), new Object[] { "P##", Character.valueOf('#'), Items.PAPER, Character.valueOf('P'), Items.STRING });
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.IRON_CHEST, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('X'), Blocks.CHEST});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.chocolate, 1), new Object[] { "###", "###", Character.valueOf('#'),  new ItemStack(Items.INK_SACK, 1, 3)});
        
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.ironLadder, 4), new Object[] { "# #", "###", "# #", Character.valueOf('#'), Items.IRON_INGOT});

        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.alumite_smooth), new Object[] {"##", "##", Character.valueOf('#'), Blocks.alumite});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.andesite_smooth), new Object[] {"##", "##", Character.valueOf('#'), Blocks.andesite});
        craftingmanager.registerShapedRecipe(new ItemStack(Blocks.diorite_smooth), new Object[] {"##", "##", Character.valueOf('#'), Blocks.diorite});
        


    }
}