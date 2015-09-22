package net.minecraft.server;


public enum EnumToolMaterial {
	
	/** Alkazia */
    WOOD("WOOD", 0, 0, 59, 2.0F, 0.0F, 15), 
    STONE("STONE", 1, 1, 131, 4.0F, 1.0F, 5), 
    IRON("IRON", 2, 2, 250, 6.0F, 2.0F, 14),
    DIAMOND("EMERALD", 3, 3, 1561, 7.0F, 3.0F, 10),
    GOLD("GOLD", 4, 0, 32, 12.0F, 0.0F, 22),
    
    BAUXITE("BAUXITE", 5, 2, 900, 7.0F, 4.0F, 14),
    GRANITE("GRANITE", 6, 3, 2000, 11.0F, 5.0F, 10),
    OPALE("OPALE", 7, 3, 3000, 13.0F, 6.0F, 10),
    METEOR("METEOR", 8, 3, 3600, 15.0F, 7.0F, 10);
    private final int f;
    private final int g;
    private final float h;
    private final float i;
    private final int j;
    private static final EnumToolMaterial[] k = new EnumToolMaterial[] { WOOD, STONE, IRON, DIAMOND, GOLD, BAUXITE, GRANITE, OPALE, METEOR}; /** Alkazia */

    private EnumToolMaterial(String s, int i, int j, int k, float f, float f1, int l) {
        this.f = j;
        this.g = k;
        this.h = f;
        this.i = f1;
        this.j = l;
    }

    public int a() {
        return this.g;
    }

    public float b() {
        return this.h;
    }

    public float c() {
        return this.i;
    }

    public int d() {
        return this.f;
    }

    public int e() {
        return this.j;
    }

    public Item f() {
        return this == WOOD ? Item.getItemOf(Blocks.WOOD) : 
        	(this == STONE ? Item.getItemOf(Blocks.COBBLESTONE) : 
        		(this == GOLD ? Items.GOLD_INGOT : 
        			(this == IRON ? Items.IRON_INGOT : 
        				(this == DIAMOND ? Items.DIAMOND : 
        					(this == BAUXITE ? Items.BAUXITE_INGOT :
        						(this == OPALE ? Items.OPALE :
        							(this == GRANITE ? Items.GRANITE :
        								(this == METEOR ? Items.METEOR_FRAGMENT :
        					null))))))));
    }
}