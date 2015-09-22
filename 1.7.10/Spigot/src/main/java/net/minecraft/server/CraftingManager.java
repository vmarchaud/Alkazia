package net.minecraft.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class CraftingManager {

	private static final CraftingManager a = new CraftingManager();
	// CraftBukkit start
	public List recipes = new ArrayList(); // private -> public
	public IRecipe lastRecipe;
	public org.bukkit.inventory.InventoryView lastCraftView;

	// CraftBukkit end

	public static final CraftingManager getInstance() {
		return a;
	}

	// CraftBukkit - private -> public
	public CraftingManager() {
		new RecipesTools().a(this);
		new RecipesWeapons().a(this);
		new RecipeIngots().a(this);
		new RecipesFood().a(this);
		new RecipesCrafting().a(this);
		new RecipesArmor().a(this);
		new RecipesDyes().a(this);
		recipes.add(new RecipeArmorDye());
		recipes.add(new RecipeBookClone());
		recipes.add(new RecipeMapClone());
		recipes.add(new RecipeMapExtend());
		recipes.add(new RecipeFireworks());
		registerShapedRecipe(new ItemStack(Items.PAPER, 3), new Object[] { "###", Character.valueOf('#'), Items.SUGAR_CANE });
		registerShapelessRecipe(new ItemStack(Items.BOOK, 1), new Object[] { Items.PAPER, Items.PAPER, Items.PAPER, Items.LEATHER });
		registerShapelessRecipe(new ItemStack(Items.BOOK_AND_QUILL, 1), new Object[] { Items.BOOK, new ItemStack(Items.INK_SACK, 1, 0), Items.FEATHER });
		registerShapedRecipe(new ItemStack(Blocks.FENCE, 2), new Object[] { "###", "###", Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.COBBLE_WALL, 6, 0), new Object[] { "###", "###", Character.valueOf('#'), Blocks.COBBLESTONE });
		registerShapedRecipe(new ItemStack(Blocks.COBBLE_WALL, 6, 1), new Object[] { "###", "###", Character.valueOf('#'), Blocks.MOSSY_COBBLESTONE });
		registerShapedRecipe(new ItemStack(Blocks.NETHER_FENCE, 6), new Object[] { "###", "###", Character.valueOf('#'), Blocks.NETHER_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.JUKEBOX, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Blocks.WOOD, Character.valueOf('X'), Items.DIAMOND });
		registerShapedRecipe(new ItemStack(Items.LEASH, 2), new Object[] { "~~ ", "~O ", "  ~", Character.valueOf('~'), Items.STRING, Character.valueOf('O'), Items.SLIME_BALL });
		registerShapedRecipe(new ItemStack(Blocks.NOTE_BLOCK, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Blocks.WOOD, Character.valueOf('X'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Blocks.BOOKSHELF, 1), new Object[] { "###", "XXX", "###", Character.valueOf('#'), Blocks.WOOD, Character.valueOf('X'), Items.BOOK });
		registerShapedRecipe(new ItemStack(Blocks.SNOW_BLOCK, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.SNOW_BALL });
		registerShapedRecipe(new ItemStack(Blocks.SNOW, 6), new Object[] { "###", Character.valueOf('#'), Blocks.SNOW_BLOCK });
		registerShapedRecipe(new ItemStack(Blocks.CLAY, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.CLAY_BALL });
		registerShapedRecipe(new ItemStack(Blocks.BRICK, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.CLAY_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.GLOWSTONE, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.GLOWSTONE_DUST });
		registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.QUARTZ });
		registerShapedRecipe(new ItemStack(Blocks.WOOL, 1), new Object[] { "##", "##", Character.valueOf('#'), Items.STRING });
		registerShapedRecipe(new ItemStack(Blocks.TNT, 1), new Object[] { "X#X", "#X#", "X#X", Character.valueOf('X'), Items.SULPHUR, Character.valueOf('#'), Blocks.SAND });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 3), new Object[] { "###", Character.valueOf('#'), Blocks.COBBLESTONE });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 0), new Object[] { "###", Character.valueOf('#'), Blocks.STONE });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 1), new Object[] { "###", Character.valueOf('#'), Blocks.SANDSTONE });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 4), new Object[] { "###", Character.valueOf('#'), Blocks.BRICK });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 5), new Object[] { "###", Character.valueOf('#'), Blocks.SMOOTH_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 6), new Object[] { "###", Character.valueOf('#'), Blocks.NETHER_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.STEP, 6, 7), new Object[] { "###", Character.valueOf('#'), Blocks.QUARTZ_BLOCK });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 0), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 0) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 2), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 2) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 1), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 1) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 3), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 3) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 4), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 4) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STEP, 6, 5), new Object[] { "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 5) });
		registerShapedRecipe(new ItemStack(Blocks.LADDER, 3), new Object[] { "# #", "###", "# #", Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Items.WOOD_DOOR, 1), new Object[] { "##", "##", "##", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.TRAP_DOOR, 2), new Object[] { "###", "###", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Items.IRON_DOOR, 1), new Object[] { "##", "##", "##", Character.valueOf('#'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Items.SIGN, 3), new Object[] { "###", "###", " X ", Character.valueOf('#'), Blocks.WOOD, Character.valueOf('X'), Items.STICK });
		registerShapedRecipe(new ItemStack(Items.CAKE, 1), new Object[] { "AAA", "BEB", "CCC", Character.valueOf('A'), Items.MILK_BUCKET, Character.valueOf('B'), Items.SUGAR, Character.valueOf('C'), Items.WHEAT, Character.valueOf('E'), Items.EGG });
		registerShapedRecipe(new ItemStack(Items.SUGAR, 1), new Object[] { "#", Character.valueOf('#'), Items.SUGAR_CANE });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 0), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG, 1, 0) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 1), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG, 1, 1) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 2), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG, 1, 2) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 3), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG, 1, 3) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 4), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG2, 1, 0) });
		registerShapedRecipe(new ItemStack(Blocks.WOOD, 4, 5), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.LOG2, 1, 1) });
		registerShapedRecipe(new ItemStack(Items.STICK, 4), new Object[] { "#", "#", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), new Object[] { "X", "#", Character.valueOf('X'), Items.COAL, Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), new Object[] { "X", "#", Character.valueOf('X'), new ItemStack(Items.COAL, 1, 1), Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Items.BOWL, 4), new Object[] { "# #", " # ", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Items.GLASS_BOTTLE, 3), new Object[] { "# #", " # ", Character.valueOf('#'), Blocks.GLASS });
		registerShapedRecipe(new ItemStack(Blocks.RAILS, 16), new Object[] { "X X", "X#X", "X X", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.GOLDEN_RAIL, 6), new Object[] { "X X", "X#X", "XRX", Character.valueOf('X'), Items.GOLD_INGOT, Character.valueOf('R'), Items.REDSTONE, Character.valueOf('#'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.ACTIVATOR_RAIL, 6), new Object[] { "XSX", "X#X", "XSX", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('#'), Blocks.REDSTONE_TORCH_ON, Character.valueOf('S'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.DETECTOR_RAIL, 6), new Object[] { "X X", "X#X", "XRX", Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('R'), Items.REDSTONE, Character.valueOf('#'), Blocks.STONE_PLATE });
		registerShapedRecipe(new ItemStack(Items.MINECART, 1), new Object[] { "# #", "###", Character.valueOf('#'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Items.CAULDRON, 1), new Object[] { "# #", "# #", "###", Character.valueOf('#'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Items.BREWING_STAND, 1), new Object[] { " B ", "###", Character.valueOf('#'), Blocks.COBBLESTONE, Character.valueOf('B'), Items.BLAZE_ROD });
		registerShapedRecipe(new ItemStack(Blocks.JACK_O_LANTERN, 1), new Object[] { "A", "B", Character.valueOf('A'), Blocks.PUMPKIN, Character.valueOf('B'), Blocks.TORCH });
		registerShapedRecipe(new ItemStack(Items.STORAGE_MINECART, 1), new Object[] { "A", "B", Character.valueOf('A'), Blocks.CHEST, Character.valueOf('B'), Items.MINECART });
		registerShapedRecipe(new ItemStack(Items.POWERED_MINECART, 1), new Object[] { "A", "B", Character.valueOf('A'), Blocks.FURNACE, Character.valueOf('B'), Items.MINECART });
		registerShapedRecipe(new ItemStack(Items.MINECART_TNT, 1), new Object[] { "A", "B", Character.valueOf('A'), Blocks.TNT, Character.valueOf('B'), Items.MINECART });
		registerShapedRecipe(new ItemStack(Items.MINECART_HOPPER, 1), new Object[] { "A", "B", Character.valueOf('A'), Blocks.HOPPER, Character.valueOf('B'), Items.MINECART });
		registerShapedRecipe(new ItemStack(Items.BOAT, 1), new Object[] { "# #", "###", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Items.BUCKET, 1), new Object[] { "# #", " # ", Character.valueOf('#'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Items.FLOWER_POT, 1), new Object[] { "# #", " # ", Character.valueOf('#'), Items.CLAY_BRICK });
		registerShapelessRecipe(new ItemStack(Items.FLINT_AND_STEEL, 1), new Object[] { new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.FLINT, 1) });
		registerShapedRecipe(new ItemStack(Items.BREAD, 1), new Object[] { "###", Character.valueOf('#'), Items.WHEAT });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 0) });
		registerShapedRecipe(new ItemStack(Blocks.BIRCH_WOOD_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 2) });
		registerShapedRecipe(new ItemStack(Blocks.SPRUCE_WOOD_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 1) });
		registerShapedRecipe(new ItemStack(Blocks.JUNGLE_WOOD_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 3) });
		registerShapedRecipe(new ItemStack(Blocks.ACACIA_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 4) });
		registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 5) });
		registerShapedRecipe(new ItemStack(Items.FISHING_ROD, 1), new Object[] { "  #", " #X", "# X", Character.valueOf('#'), Items.STICK, Character.valueOf('X'), Items.STRING });
		registerShapedRecipe(new ItemStack(Items.CARROT_STICK, 1), new Object[] { "# ", " X", Character.valueOf('#'), Items.FISHING_ROD, Character.valueOf('X'), Items.CARROT }).c();
		registerShapedRecipe(new ItemStack(Blocks.COBBLESTONE_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.COBBLESTONE });
		registerShapedRecipe(new ItemStack(Blocks.BRICK_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.BRICK });
		registerShapedRecipe(new ItemStack(Blocks.STONE_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.SMOOTH_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.NETHER_BRICK });
		registerShapedRecipe(new ItemStack(Blocks.SANDSTONE_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.SANDSTONE });
		registerShapedRecipe(new ItemStack(Blocks.QUARTZ_STAIRS, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Blocks.QUARTZ_BLOCK });
		registerShapedRecipe(new ItemStack(Items.PAINTING, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.STICK, Character.valueOf('X'), Blocks.WOOL });
		registerShapedRecipe(new ItemStack(Items.ITEM_FRAME, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.STICK, Character.valueOf('X'), Items.LEATHER });
		registerShapedRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 0), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.GOLD_INGOT, Character.valueOf('X'), Items.APPLE });
		registerShapedRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Blocks.GOLD_BLOCK, Character.valueOf('X'), Items.APPLE });
		registerShapedRecipe(new ItemStack(Items.CARROT_GOLDEN, 1, 0), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.GOLD_NUGGET, Character.valueOf('X'), Items.CARROT });
		registerShapedRecipe(new ItemStack(Items.SPECKLED_MELON, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.GOLD_NUGGET, Character.valueOf('X'), Items.MELON });
		registerShapedRecipe(new ItemStack(Blocks.LEVER, 1), new Object[] { "X", "#", Character.valueOf('#'), Blocks.COBBLESTONE, Character.valueOf('X'), Items.STICK });
		registerShapedRecipe(new ItemStack(Blocks.TRIPWIRE_SOURCE, 2), new Object[] { "I", "S", "#", Character.valueOf('#'), Blocks.WOOD, Character.valueOf('S'), Items.STICK, Character.valueOf('I'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Blocks.REDSTONE_TORCH_ON, 1), new Object[] { "X", "#", Character.valueOf('#'), Items.STICK, Character.valueOf('X'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Items.DIODE, 1), new Object[] { "#X#", "III", Character.valueOf('#'), Blocks.REDSTONE_TORCH_ON, Character.valueOf('X'), Items.REDSTONE, Character.valueOf('I'), Blocks.STONE });
		registerShapedRecipe(new ItemStack(Items.REDSTONE_COMPARATOR, 1), new Object[] { " # ", "#X#", "III", Character.valueOf('#'), Blocks.REDSTONE_TORCH_ON, Character.valueOf('X'), Items.QUARTZ, Character.valueOf('I'), Blocks.STONE });
		registerShapedRecipe(new ItemStack(Items.WATCH, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), Items.GOLD_INGOT, Character.valueOf('X'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Items.COMPASS, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), Items.IRON_INGOT, Character.valueOf('X'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Items.MAP_EMPTY, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Items.PAPER, Character.valueOf('X'), Items.COMPASS });
		registerShapedRecipe(new ItemStack(Blocks.STONE_BUTTON, 1), new Object[] { "#", Character.valueOf('#'), Blocks.STONE });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_BUTTON, 1), new Object[] { "#", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.STONE_PLATE, 1), new Object[] { "##", Character.valueOf('#'), Blocks.STONE });
		registerShapedRecipe(new ItemStack(Blocks.WOOD_PLATE, 1), new Object[] { "##", Character.valueOf('#'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.IRON_PLATE, 1), new Object[] { "##", Character.valueOf('#'), Items.IRON_INGOT });
		registerShapedRecipe(new ItemStack(Blocks.GOLD_PLATE, 1), new Object[] { "##", Character.valueOf('#'), Items.GOLD_INGOT });
		registerShapedRecipe(new ItemStack(Blocks.DISPENSER, 1), new Object[] { "###", "#X#", "#R#", Character.valueOf('#'), Blocks.COBBLESTONE, Character.valueOf('X'), Items.BOW, Character.valueOf('R'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Blocks.DROPPER, 1), new Object[] { "###", "# #", "#R#", Character.valueOf('#'), Blocks.COBBLESTONE, Character.valueOf('R'), Items.REDSTONE });
		registerShapedRecipe(new ItemStack(Blocks.PISTON, 1), new Object[] { "TTT", "#X#", "#R#", Character.valueOf('#'), Blocks.COBBLESTONE, Character.valueOf('X'), Items.IRON_INGOT, Character.valueOf('R'), Items.REDSTONE, Character.valueOf('T'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.PISTON_STICKY, 1), new Object[] { "S", "P", Character.valueOf('S'), Items.SLIME_BALL, Character.valueOf('P'), Blocks.PISTON });
		registerShapedRecipe(new ItemStack(Items.BED, 1), new Object[] { "###", "XXX", Character.valueOf('#'), Blocks.WOOL, Character.valueOf('X'), Blocks.WOOD });
		registerShapedRecipe(new ItemStack(Blocks.ENCHANTMENT_TABLE, 1), new Object[] { " B ", "D#D", "###", Character.valueOf('#'), Blocks.OBSIDIAN, Character.valueOf('B'), Items.BOOK, Character.valueOf('D'), Items.DIAMOND });
		registerShapedRecipe(new ItemStack(Blocks.ANVIL, 1), new Object[] { "III", " i ", "iii", Character.valueOf('I'), Blocks.IRON_BLOCK, Character.valueOf('i'), Items.IRON_INGOT });
		registerShapelessRecipe(new ItemStack(Items.EYE_OF_ENDER, 1), new Object[] { Items.ENDER_PEARL, Items.BLAZE_POWDER });
		registerShapelessRecipe(new ItemStack(Items.FIREBALL, 3), new Object[] { Items.SULPHUR, Items.BLAZE_POWDER, Items.COAL });
		registerShapelessRecipe(new ItemStack(Items.FIREBALL, 3), new Object[] { Items.SULPHUR, Items.BLAZE_POWDER, new ItemStack(Items.COAL, 1, 1) });
		registerShapedRecipe(new ItemStack(Blocks.DAYLIGHT_DETECTOR), new Object[] { "GGG", "QQQ", "WWW", Character.valueOf('G'), Blocks.GLASS, Character.valueOf('Q'), Items.QUARTZ, Character.valueOf('W'), Blocks.WOOD_STEP });
		registerShapedRecipe(new ItemStack(Blocks.HOPPER), new Object[] { "I I", "ICI", " I ", Character.valueOf('I'), Items.IRON_INGOT, Character.valueOf('C'), Blocks.CHEST });
		// ClipSpigot start - Register 1.8 recipes
		/*
		registerShapelessRecipe(new ItemStack(Blocks.MOSSY_COBBLESTONE), new Object[] { Blocks.VINE, Blocks.COBBLESTONE});
		registerShapelessRecipe(new ItemStack(Blocks.SMOOTH_BRICK, 1, 1), new Object[] { Blocks.VINE, Blocks.SMOOTH_BRICK});
		registerShapelessRecipe(new ItemStack(Blocks.SMOOTH_BRICK, 1, 3), new Object[] { new ItemStack(Blocks.STEP, 1, 5), new ItemStack(Blocks.STEP, 1, 5)});
		registerShapelessRecipe(new ItemStack(Blocks.STONE, 1, 1), new Object[] { new ItemStack(Blocks.STONE, 1, 3), Items.QUARTZ }); // Granite
		registerShapedRecipe(new ItemStack(Blocks.STONE, 4, 2), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, 1) }); // Polished Granite
		registerShapedRecipe(new ItemStack(Blocks.STONE, 2, 3), new Object[] { "CQ", "QC", Character.valueOf('C'), Blocks.COBBLESTONE, Character.valueOf('Q'), Items.QUARTZ }); // Diorite
		registerShapedRecipe(new ItemStack(Blocks.STONE, 4, 4), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, 3) }); // Polished Diorite
		registerShapelessRecipe(new ItemStack(Blocks.STONE, 2, 5), new Object[] { new ItemStack(Blocks.STONE, 1, 3), Blocks.COBBLESTONE }); // Andesite
		registerShapedRecipe(new ItemStack(Blocks.STONE, 4, 6), new Object[] { "##", "##", Character.valueOf('#'), new ItemStack(Blocks.STONE, 1, 5) }); // Polished Andesite
		registerShapedRecipe(new ItemStack(Blocks.SLIME_BLOCK), new Object[] { "###", "###", "###", Character.valueOf('#'), Items.SLIME_BALL }); // Slime Ball -> Slime Block
		registerShapedRecipe(new ItemStack(Items.SLIME_BALL, 9), new Object[] { "#", Character.valueOf('#'), new ItemStack(Blocks.SLIME_BLOCK, 1) }); // Slime Block -> Slime Ball
		registerShapedRecipe(new ItemStack(Blocks.SPRUCE_FENCE, 3), new Object[] { "#S#", "#S#", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 1), Character.valueOf('S'), Items.STICK }); // Spruce fence
		registerShapedRecipe(new ItemStack(Blocks.BIRCH_FENCE, 3), new Object[] { "#S#", "#S#", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 2), Character.valueOf('S'), Items.STICK }); // Birch fence
		registerShapedRecipe(new ItemStack(Blocks.JUNGLE_FENCE, 3), new Object[] { "#S#", "#S#", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 3), Character.valueOf('S'), Items.STICK }); // Jungle fence
		registerShapedRecipe(new ItemStack(Blocks.ACACIA_FENCE, 3), new Object[] { "#S#", "#S#", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 4), Character.valueOf('S'), Items.STICK }); // Acacia fence
		registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_FENCE, 3), new Object[] { "#S#", "#S#", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 5), Character.valueOf('S'), Items.STICK }); // Dark Oak fence
		registerShapedRecipe(new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), new ItemStack(Blocks.WOOD, 1, 1) }); // Spruce fence gate
		registerShapedRecipe(new ItemStack(Blocks.BIRCH_FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), new ItemStack(Blocks.WOOD, 1, 2) }); // Birch fence gate
		registerShapedRecipe(new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), new ItemStack(Blocks.WOOD, 1, 3) }); // Jungle fence gate
		registerShapedRecipe(new ItemStack(Blocks.ACACIA_FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), new ItemStack(Blocks.WOOD, 1, 4) }); // Acacia fence gate
		registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Items.STICK, Character.valueOf('W'), new ItemStack(Blocks.WOOD, 1, 5) }); // Dark Oak fence gate
		registerShapedRecipe(new ItemStack(Items.SPRUCE_DOOR, 3), new Object[] { "##", "##", "##", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 1) }); // Spruce door
		registerShapedRecipe(new ItemStack(Items.BIRCH_DOOR, 3), new Object[] { "##", "##", "##", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 2) }); // Birch door
		registerShapedRecipe(new ItemStack(Items.JUNGLE_DOOR, 3), new Object[] { "##", "##", "##", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 3) }); // Jungle door
		registerShapedRecipe(new ItemStack(Items.ACACIA_DOOR, 3), new Object[] { "##", "##", "##", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 4) }); // Acacia door
		registerShapedRecipe(new ItemStack(Items.DARK_OAK_DOOR, 3), new Object[] { "##", "##", "##", Character.valueOf('#'), new ItemStack(Blocks.WOOD, 1, 5) }); // Dark Oak door
		*/// ClipSpigot end
		// Collections.sort(this.recipes, new RecipeSorter(this)); // CraftBukkit - moved below
		sort(); // CraftBukkit - call new sort method
	}

	// CraftBukkit start
	public void sort() {
		Collections.sort(recipes, new RecipeSorter(this));
	}

	// CraftBukkit end

	// CraftBukkit - default -> public
	public ShapedRecipes registerShapedRecipe(ItemStack itemstack, Object... aobject) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;

		if (aobject[i] instanceof String[]) {
			String[] astring = (String[]) aobject[i++];

			for (int l = 0; l < astring.length; ++l) {
				String s1 = astring[l];

				++k;
				j = s1.length();
				s = s + s1;
			}
		} else {
			while (aobject[i] instanceof String) {
				String s2 = (String) aobject[i++];

				++k;
				j = s2.length();
				s = s + s2;
			}
		}

		HashMap hashmap;

		for (hashmap = new HashMap(); i < aobject.length; i += 2) {
			Character character = (Character) aobject[i];
			ItemStack itemstack1 = null;

			if (aobject[i + 1] instanceof Item) {
				itemstack1 = new ItemStack((Item) aobject[i + 1]);
			} else if (aobject[i + 1] instanceof Block) {
				itemstack1 = new ItemStack((Block) aobject[i + 1], 1, 32767);
			} else if (aobject[i + 1] instanceof ItemStack) {
				itemstack1 = (ItemStack) aobject[i + 1];
			}

			hashmap.put(character, itemstack1);
		}

		ItemStack[] aitemstack = new ItemStack[j * k];

		for (int i1 = 0; i1 < j * k; ++i1) {
			char c0 = s.charAt(i1);

			if (hashmap.containsKey(Character.valueOf(c0))) {
				aitemstack[i1] = ((ItemStack) hashmap.get(Character.valueOf(c0))).cloneItemStack();
			} else {
				aitemstack[i1] = null;
			}
		}

		ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, itemstack);

		recipes.add(shapedrecipes);
		return shapedrecipes;
	}

	// CraftBukkit - default -> public
	public void registerShapelessRecipe(ItemStack itemstack, Object... aobject) {
		ArrayList arraylist = new ArrayList();
		Object[] aobject1 = aobject;
		int i = aobject.length;

		for (int j = 0; j < i; ++j) {
			Object object = aobject1[j];

			if (object instanceof ItemStack) {
				arraylist.add(((ItemStack) object).cloneItemStack());
			} else if (object instanceof Item) {
				arraylist.add(new ItemStack((Item) object));
			} else {
				if (!(object instanceof Block))
					throw new RuntimeException("Invalid shapeless recipy!");

				arraylist.add(new ItemStack((Block) object));
			}
		}

		recipes.add(new ShapelessRecipes(itemstack, arraylist));
	}

	public ItemStack craft(InventoryCrafting inventorycrafting, World world) {
		int i = 0;
		ItemStack itemstack = null;
		ItemStack itemstack1 = null;

		int j;

		for (j = 0; j < inventorycrafting.getSize(); ++j) {
			ItemStack itemstack2 = inventorycrafting.getItem(j);

			if (itemstack2 != null) {
				if (i == 0) {
					itemstack = itemstack2;
				}

				if (i == 1) {
					itemstack1 = itemstack2;
				}

				++i;
			}
		}

		if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.count == 1 && itemstack1.count == 1 && itemstack.getItem().usesDurability()) {
			Item item = itemstack.getItem();
			int k = item.getMaxDurability() - itemstack.j();
			int l = item.getMaxDurability() - itemstack1.j();
			int i1 = k + l + item.getMaxDurability() * 5 / 100;
			int j1 = item.getMaxDurability() - i1;

			if (j1 < 0) {
				j1 = 0;
			}

			// CraftBukkit start - Construct a dummy repair recipe
			ItemStack result = new ItemStack(itemstack.getItem(), 1, j1);
			List<ItemStack> ingredients = new ArrayList<ItemStack>();
			ingredients.add(itemstack.cloneItemStack());
			ingredients.add(itemstack1.cloneItemStack());
			ShapelessRecipes recipe = new ShapelessRecipes(result.cloneItemStack(), ingredients);
			inventorycrafting.currentRecipe = recipe;
			result = CraftEventFactory.callPreCraftEvent(inventorycrafting, result, lastCraftView, true);
			return result;
			// CraftBukkit end
		} else {
			for (j = 0; j < recipes.size(); ++j) {
				IRecipe irecipe = (IRecipe) recipes.get(j);

				if (irecipe.a(inventorycrafting, world)) {
					// CraftBukkit start - INVENTORY_PRE_CRAFT event
					inventorycrafting.currentRecipe = irecipe;
					ItemStack result = irecipe.a(inventorycrafting);
					return CraftEventFactory.callPreCraftEvent(inventorycrafting, result, lastCraftView, false);
					// CraftBukkit end
				}
			}

			inventorycrafting.currentRecipe = null; // CraftBukkit - Clear recipe when no recipe is found
			return null;
		}
	}

	public List getRecipes() {
		return recipes;
	}
}
