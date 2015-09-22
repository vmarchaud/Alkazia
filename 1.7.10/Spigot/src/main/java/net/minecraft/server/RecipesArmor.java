package net.minecraft.server;

public class RecipesArmor {

    private String[][] a = new String[][] { { "XXX", "X X"}, { "X X", "XXX", "XXX"}, { "XXX", "X X", "X X"}, { "X X", "X X"}};
    private Object[][] b;

    public RecipesArmor() {
    	/** Alkazia */
        this.b = new Object[][] { { Items.LEATHER, Blocks.FIRE, Items.IRON_INGOT, Items.DIAMOND, Items.GOLD_INGOT, Items.BAUXITE_INGOT, Items.GRANITE, Items.OPALE, Items.METEOR_FRAGMENT, Items.ninjaPowder}, 
        		{ Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.GOLD_HELMET, Items.BAUXITE_HELMET, Items.GRANITE_HELMET, Items.OPALE_HELMET, Items.METEOR_HELMET, Items.NINJA_HELMET}, 
        		{ Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.GOLD_CHESTPLATE, Items.BAUXITE_CHESTPLATE, Items.GRANITE_CHESTPLATE, Items.OPALE_CHESTPLATE, Items.METEOR_CHESTPLATE, Items.NINJA_CHESTPLATE}, 
        		{ Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.GOLD_LEGGINGS, Items.BAUXITE_LEGGINGS, Items.GRANITE_LEGGINGS, Items.OPALE_LEGGINGS, Items.METEOR_LEGGINGS, Items.NINJA_LEGGINGS}, 
        		{ Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.GOLD_BOOTS, Items.BAUXITE_BOOTS, Items.GRANITE_BOOTS, Items.OPALE_BOOTS, Items.METEOR_BOOTS, Items.NINJA_BOOTS}};
    }

    public void a(CraftingManager craftingmanager) {
        for (int i = 0; i < this.b[0].length; ++i) {
            Object object = this.b[0][i];

            for (int j = 0; j < this.b.length - 1; ++j) {
                Item item = (Item) this.b[j + 1][i];

                craftingmanager.registerShapedRecipe(new ItemStack(item), new Object[] { this.a[j], Character.valueOf('X'), object});
            }
        }
    }
}