package net.minecraft.server;

public class RecipesWeapons {

    private String[][] a = new String[][] { { "X", "X", "#"}};
    private Object[][] b;

    public RecipesWeapons() {
    	/** Alkazia */
        this.b = new Object[][] { { Blocks.PLANKS, Blocks.COBBLESTONE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT, Items.BAUXITE_INGOT, Items.GRANITE, Items.OPALE, Items.METEOR_FRAGMENT}, 
        		{ Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.GOLDEN_SWORD, Items.BAUXITE_SWORD, Items.GRANITE_SWORD, Items.OPALE_SWORD, Items.METEOR_SWORD}};
    }

    public void a(CraftingManager craftingmanager) {
        for (int i = 0; i < this.b[0].length; ++i) {
            Object object = this.b[0][i];

            for (int j = 0; j < this.b.length - 1; ++j) {
                Item item = (Item) this.b[j + 1][i];

                craftingmanager.registerShapedRecipe(new ItemStack(item), new Object[] { this.a[j], Character.valueOf('#'), Items.STICK, Character.valueOf('X'), object});
            }
        }

        craftingmanager.registerShapedRecipe(new ItemStack(Items.BOW, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.STRING, Character.valueOf('#'), Items.STICK});
        
        craftingmanager.registerShapedRecipe(new ItemStack(Items.bowBauxite, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.STRING, Character.valueOf('#'), Items.BAUXITE_INGOT});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.bowGranite, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.STRING, Character.valueOf('#'), Items.GRANITE});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.bowOpale, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.STRING, Character.valueOf('#'), Items.OPALE});
        craftingmanager.registerShapedRecipe(new ItemStack(Items.bowMeteor, 1), new Object[] { " #X", "# X", " #X", Character.valueOf('X'), Items.STRING, Character.valueOf('#'), Items.METEOR_FRAGMENT});
        
        craftingmanager.registerShapedRecipe(new ItemStack(Items.ARROW, 4), new Object[] { "X", "#", "Y", Character.valueOf('Y'), Items.FEATHER, Character.valueOf('X'), Items.FLINT, Character.valueOf('#'), Items.STICK});
    }
}