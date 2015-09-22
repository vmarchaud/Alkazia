package net.minecraft.server;

public class Block18 extends Block {
	
	public static final String[] types_stone = { "stone", "granite", "polished_granite", "diorite", "polished_diorite", "andesite", "polished_andesite" };
	public static final String[] types_sponge = { "sponge", "wet_sponge" };
	public static final String[] types_prismarine = { "prismarine", "prismarine_bricks", "dark_prismarine" };
	public static final String[] types_red_sandstone = { "red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"};
	
	protected Block18(Material material) {
		super(material);
		a(CreativeModeTab.b);
	}
	
	@Override
	public int getDropData(int paramInt) {
		return paramInt;
	}
}