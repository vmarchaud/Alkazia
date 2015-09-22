package net.minecraft.server;

public enum EnumArmorMaterial {
	/** Alkazia */
    CLOTH("CLOTH", 0, 5, new int[] { 1, 3, 2, 1}, 15), 
    CHAIN("CHAIN", 1, 15, new int[] { 2, 5, 4, 1}, 12), 
    IRON("IRON", 2, 15, new int[] { 2, 5, 3, 2}, 9), 
    GOLD("GOLD", 3, 7, new int[] { 1, 4, 2, 1}, 25), 
    
    DIAMOND("DIAMOND", 4, 33, new int[] { 3, 7, 4, 3}, 10), 
    BAUXITE("BAUXITE", 5, 27, new int[] { 3, 7, 5, 4}, 9),
    
    GRANITE("GRANITE", 6, 40, new int[] { 4, 7, 5, 4}, 10),
    OPALE("OPALE", 7, 45, new int[] { 5, 7, 6, 5}, 10),
    METEOR("METEOR", 8, 50, new int[] {6, 8, 6, 5}, 10),
    
    
    NINJA("NINJA", 9, 18, new int[] { 2, 5, 3, 2}, 9);
    
    private int f;
    private int[] g;
    private int h;
    private static final EnumArmorMaterial[] i = new EnumArmorMaterial[] { CLOTH, CHAIN, IRON, GOLD, DIAMOND, BAUXITE, GRANITE, OPALE, METEOR, NINJA}; /** Alkazia */

    private EnumArmorMaterial(String s, int i, int j, int[] aint, int k) {
        this.f = j;
        this.g = aint;
        this.h = k;
    }

    public int a(int i) {
        return ItemArmor.e()[i] * this.f;
    }

    public int b(int i) {
        return this.g[i];
    }

    public int a() {
        return this.h;
    }

    public Item b() {
        return this == CLOTH ? Items.LEATHER : 
        	(this == CHAIN ? Items.IRON_INGOT : 
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