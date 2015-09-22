package net.minecraft.server;

public class RecipesCrafting
{
  public void a(CraftingManager paramCraftingManager)
  {
    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.CHEST), new Object[] { "###", "# #", "###", Character.valueOf('#'), Blocks.PLANKS });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.TRAPPED_CHEST), new Object[] { "#-", Character.valueOf('#'), Blocks.CHEST, Character.valueOf('-'), Blocks.TRIPWIRE_HOOK });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.ENDER_CHEST), new Object[] { "###", "#E#", "###", Character.valueOf('#'), Blocks.OBSIDIAN, Character.valueOf('E'), Items.ENDER_EYE });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.FURNACE), new Object[] { "###", "# #", "###", Character.valueOf('#'), Blocks.COBBLESTONE });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.CRAFTING_TABLE), new Object[] { "##", "##", Character.valueOf('#'), Blocks.PLANKS });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.SAND, 1, EnumSandVariant.SAND.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.SAND, 1, EnumSandVariant.RED_SAND.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 4, EnumSandstoneVariant.SMOOTH.a()), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.SANDSTONE, 1, EnumSandstoneVariant.DEFAULT.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE, 4, EnumRedSandstoneVariant.SMOOTH.a()), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.RED_SANDSTONE, 1, EnumRedSandstoneVariant.DEFAULT.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE, 1, EnumSandstoneVariant.CHISELED.a()), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.SAND.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE, 1, EnumRedSandstoneVariant.CHISELED.a()), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STONE_SLAB2, 1, EnumStoneSlab2Variant.RED_SANDSTONE.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1, EnumQuartzVariant.CHISELED.a()), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.QUARTZ.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 2, EnumQuartzVariant.LINES_Y.a()), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.QUARTZ_BLOCK, 1, EnumQuartzVariant.DEFAULT.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 4), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.STONE.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.O), new Object[] { "#", "#", Character.valueOf('#'), new ItemStack(Blocks.STONE_SLAB, 1, EnumStoneSlabVariant.SMOOTHBRICK.a()) });

    paramCraftingManager.registerShapelessRecipe(new ItemStack(Blocks.STONEBRICK, 1, BlockSmoothBrick.M), new Object[] { Blocks.STONEBRICK, Blocks.VINE });

    paramCraftingManager.registerShapelessRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1), new Object[] { Blocks.COBBLESTONE, Blocks.VINE });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.IRON_BARS, 16), new Object[] { "###", "###", Character.valueOf('#'), Items.IRON_INGOT });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.GLASS_PANE, 16), new Object[] { "###", "###", Character.valueOf('#'), Blocks.GLASS });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_LAMP, 1), new Object[] { " R ", "RGR", " R ", Character.valueOf('R'), Items.REDSTONE, Character.valueOf('G'), Blocks.GLOWSTONE });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.BEACON, 1), new Object[] { "GGG", "GSG", "OOO", Character.valueOf('G'), Blocks.GLASS, Character.valueOf('S'), Items.NETHER_STAR, Character.valueOf('O'), Blocks.OBSIDIAN });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK, 1), new Object[] { "NN", "NN", Character.valueOf('N'), Items.NETHERBRICK });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONE, 2, EnumStoneVariant.DIORITE.a()), new Object[] { "CQ", "QC", Character.valueOf('C'), Blocks.COBBLESTONE, Character.valueOf('Q'), Items.QUARTZ });

    paramCraftingManager.registerShapelessRecipe(new ItemStack(Blocks.STONE, 1, EnumStoneVariant.GRANITE.a()), new Object[] { new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()), Items.QUARTZ });

    paramCraftingManager.registerShapelessRecipe(new ItemStack(Blocks.STONE, 2, EnumStoneVariant.ANDESITE.a()), new Object[] { new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()), Blocks.COBBLESTONE });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.DIRT, 4, EnumDirtVariant.COARSE_DIRT.a()), new Object[] { "DG", "GD", Character.valueOf('D'), new ItemStack(Blocks.DIRT, 1, EnumDirtVariant.DIRT.a()), Character.valueOf('G'), Blocks.GRAVEL });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.DIORITE_SMOOTH.a()), new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.GRANITE_SMOOTH.a()), new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.GRANITE.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.STONE, 4, EnumStoneVariant.ANDESITE_SMOOTH.a()), new Object[] { "SS", "SS", Character.valueOf('S'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.ANDESITE.a()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.b), new Object[] { "SS", "SS", Character.valueOf('S'), Items.PRISMARINE_SHARD });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.M), new Object[] { "SSS", "SSS", "SSS", Character.valueOf('S'), Items.PRISMARINE_SHARD });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.PRISMARINE, 1, BlockPrismarine.N), new Object[] { "SSS", "SIS", "SSS", Character.valueOf('S'), Items.PRISMARINE_SHARD, Character.valueOf('I'), new ItemStack(Items.DYE, 1, EnumColor.BLACK.getInvColorIndex()) });

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.SEA_LANTERN, 1, 0), new Object[] { "SCS", "CCC", "SCS", Character.valueOf('S'), Items.PRISMARINE_SHARD, Character.valueOf('C'), Items.PRISMARINE_CRYSTALS });
    

    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.POUSSE, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'), Blocks.COBBLESTONE});
    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.POUSSE, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.DIORITE.a()) });
    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.POUSSE, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.GRANITE.a()) });
    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.POUSSE, 1), new Object[] { "# #", " # ", "# #", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, EnumStoneVariant.ANDESITE.a()) });
    paramCraftingManager.registerShapedRecipe(new ItemStack(Items.NAME_TAG), new Object[] { "P##", Character.valueOf('#'), Items.PAPER, Character.valueOf('P'), Items.STRING });
    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.IRON_CHEST, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('X'), Blocks.CHEST});


    paramCraftingManager.registerShapedRecipe(new ItemStack(Blocks.IRON_LADDER, 4), new Object[] { "# #", "###", "# #", Character.valueOf('#'), Items.IRON_INGOT});
    paramCraftingManager.registerShapedRecipe(new ItemStack(Items.potionFall), new Object[] { "#O#", Character.valueOf('#'), Items.FEATHER, Character.valueOf('O'), Items.POTION});
  }
}