package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Block {

	public static final RegistryMaterials REGISTRY = new RegistryBlocks("air");
	private CreativeModeTab creativeTab;
	protected String d;
	public static final StepSound e = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound f = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound g = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound h = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound i = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound j = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound k = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound l = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound m = new StepSound("sand", 1.0F, 1.0F);
	public static final StepSound n = new StepSound("snow", 1.0F, 1.0F);
	public static final StepSound o = new StepSoundLadder("ladder", 1.0F, 1.0F);
	public static final StepSound p = new StepSoundAnvil("anvil", 0.3F, 1.0F);
	public static final StepSound slimeStepSound = new StepSoundSlime("slime", 1.0F, 1.0F); // Slime step sound
	protected boolean q;
	protected int r;
	protected boolean s;
	protected int t;
	protected boolean u;
	protected float strength;
	protected float durability;
	protected boolean x = true;
	protected boolean y = true;
	protected boolean z;
	protected boolean isTileEntity;
	protected double minX;
	protected double minY;
	protected double minZ;
	protected double maxX;
	protected double maxY;
	protected double maxZ;
	public StepSound stepSound;
	public float I;
	protected final Material material;
	public float frictionFactor;
	private String name;

	public static int getId(Block block) {
		return REGISTRY.b(block);
	}

	public static Block getById(int i) {
		return (Block) REGISTRY.a(i);
	}

	public static Block setSound(Item item) {
		return getById(Item.getId(item));
	}

	public static Block setResistance(String s) {
		if (REGISTRY.b(s))
			return (Block) REGISTRY.get(s);
		else {
			try {
				return (Block) REGISTRY.a(Integer.parseInt(s));
			} catch (NumberFormatException numberformatexception) {
				return null;
			}
		}
	}

	public boolean j() {
		return q;
	}

	public int k() {
		return r;
	}

	public int m() {
		return t;
	}

	public boolean n() {
		return u;
	}

	public Material getMaterial() {
		return material;
	}

	public MaterialMapColor f(int i) {
		return getMaterial().r();
	}

	/**
	 * @deprecated Compatibility class.
	 */
	public Block c(String name) {
		return setName(name);
	}
	
	/**
	 * @deprecated Compatibility class.
	 */
	public Block c(float hardness) {
		return setHardness(hardness);
	}
	
	/**
	 * @deprecated Compatibility class.
	 */
	public Block b(float resistance) {
		return setResistance(resistance);
	}
	
	/**
	 * @deprecated Compatibility class.
	 */
	public Block d(String name) {
		return setTextureName(name);
	}
	
	/**
	 * @deprecated compatibility reasons
	 */
	protected final void a(float f, float f1, float f2, float f3, float f4, float f5) {
		setBounds(f, f1, f2, f3, f4, f5);
	}
	
	/**
	 * @deprecated compatibility reasons
	 */
	protected Block a(StepSound sound) {
		return setSound(sound);
	}
	
	public static void p() {
		REGISTRY.a(0, "air", new BlockAir().setName("air"));
		REGISTRY.a(1, "stone", new BlockStone().setHardness(1.5F).setResistance(10.0F).setSound(i).setName("stone").setTextureName("stone"));
		REGISTRY.a(2, "grass", new BlockGrass().setHardness(0.6F).setSound(h).setName("grass").setTextureName("grass"));
		REGISTRY.a(3, "dirt", new BlockDirt().setHardness(0.5F).setSound(g).setName("dirt").setTextureName("dirt"));
		Block block = new Block(Material.STONE).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stonebrick").a(CreativeModeTab.b).setTextureName("cobblestone");

		REGISTRY.a(4, "cobblestone", block);
		Block woodenPlanks = new BlockWood().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("wood").setTextureName("planks");

		REGISTRY.a(5, "planks", woodenPlanks);
		REGISTRY.a(6, "sapling", new BlockSapling().setHardness(0.0F).setSound(h).setName("sapling").setTextureName("sapling"));
		REGISTRY.a(7, "bedrock", new Block(Material.STONE).s().setResistance(6000000.0F).setSound(i).setName("bedrock").H().a(CreativeModeTab.b).setTextureName("bedrock"));
		REGISTRY.a(8, "flowing_water", new BlockFlowing(Material.WATER).setHardness(100.0F).g(3).setName("water").H().setTextureName("water_flow"));
		REGISTRY.a(9, "water", new BlockStationary(Material.WATER).setHardness(100.0F).g(3).setName("water").H().setTextureName("water_still"));
		REGISTRY.a(10, "flowing_lava", new BlockFlowing(Material.LAVA).setHardness(100.0F).a(1.0F).setName("lava").H().setTextureName("lava_flow"));
		REGISTRY.a(11, "lava", new BlockStationary(Material.LAVA).setHardness(100.0F).a(1.0F).setName("lava").H().setTextureName("lava_still"));
		REGISTRY.a(12, "sand", new BlockSand().setHardness(0.5F).setSound(m).setName("sand").setTextureName("sand"));
		REGISTRY.a(13, "gravel", new BlockGravel().setHardness(0.6F).setSound(g).setName("gravel").setTextureName("gravel"));
		REGISTRY.a(14, "gold_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreGold").setTextureName("gold_ore"));
		REGISTRY.a(15, "iron_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreIron").setTextureName("iron_ore"));
		REGISTRY.a(16, "coal_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreCoal").setTextureName("coal_ore"));
		REGISTRY.a(17, "log", new BlockLog1().setName("log").setTextureName("log"));
		REGISTRY.a(18, "leaves", new BlockLeaves1().setName("leaves").setTextureName("leaves"));
		REGISTRY.a(19, "sponge", new Block18(Material.SPONGE).setHardness(0.6F).setSound(h).setName("sponge").setTextureName("sponge"));
		REGISTRY.a(20, "glass", new BlockGlass(Material.SHATTERABLE, false).setHardness(0.3F).setSound(k).setName("glass").setTextureName("glass"));
		REGISTRY.a(21, "lapis_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreLapis").setTextureName("lapis_ore"));
		REGISTRY.a(22, "lapis_block", new BlockOreBlock(MaterialMapColor.H).setHardness(3.0F).setResistance(5.0F).setSound(i).setName("blockLapis").a(CreativeModeTab.b).setTextureName("lapis_block"));
		REGISTRY.a(23, "dispenser", new BlockDispenser().setHardness(3.5F).setSound(i).setName("dispenser").setTextureName("dispenser"));
		Block sandstone = new BlockSandStone().setSound(i).setHardness(0.8F).setName("sandStone").setTextureName("sandstone");

		REGISTRY.a(24, "sandstone", sandstone);
		REGISTRY.a(25, "noteblock", new BlockNote().setHardness(0.8F).setName("musicBlock").setTextureName("noteblock"));
		REGISTRY.a(26, "bed", new BlockBed().setHardness(0.2F).setName("bed").H().setTextureName("bed"));
		REGISTRY.a(27, "golden_rail", new BlockPoweredRail().setHardness(0.7F).setSound(j).setName("goldenRail").setTextureName("rail_golden"));
		REGISTRY.a(28, "detector_rail", new BlockMinecartDetector().setHardness(0.7F).setSound(j).setName("detectorRail").setTextureName("rail_detector"));
		REGISTRY.a(29, "sticky_piston", new BlockPiston(true).setName("pistonStickyBase"));
		REGISTRY.a(30, "web", new BlockWeb().g(1).setHardness(4.0F).setName("web").setTextureName("web"));
		REGISTRY.a(31, "tallgrass", new BlockLongGrass().setHardness(0.0F).setSound(h).setName("tallgrass"));
		REGISTRY.a(32, "deadbush", new BlockDeadBush().setHardness(0.0F).setSound(h).setName("deadbush").setTextureName("deadbush"));
		REGISTRY.a(33, "piston", new BlockPiston(false).setName("pistonBase"));
		REGISTRY.a(34, "piston_head", new BlockPistonExtension());
		REGISTRY.a(35, "wool", new BlockCloth(Material.CLOTH).setHardness(0.8F).setSound(l).setName("cloth").setTextureName("wool_colored"));
		REGISTRY.a(36, "piston_extension", new BlockPistonMoving());
		REGISTRY.a(37, "yellow_flower", new BlockFlowers(0).setHardness(0.0F).setSound(h).setName("flower1").setTextureName("flower_dandelion"));
		REGISTRY.a(38, "red_flower", new BlockFlowers(1).setHardness(0.0F).setSound(h).setName("flower2").setTextureName("flower_rose"));
		REGISTRY.a(39, "brown_mushroom", new BlockMushroom().setHardness(0.0F).setSound(h).a(0.125F).setName("mushroom").setTextureName("mushroom_brown"));
		REGISTRY.a(40, "red_mushroom", new BlockMushroom().setHardness(0.0F).setSound(h).setName("mushroom").setTextureName("mushroom_red"));
		REGISTRY.a(41, "gold_block", new BlockOreBlock(MaterialMapColor.F).setHardness(3.0F).setResistance(10.0F).setSound(j).setName("blockGold").setTextureName("gold_block"));
		REGISTRY.a(42, "iron_block", new BlockOreBlock(MaterialMapColor.h).setHardness(5.0F).setResistance(10.0F).setSound(j).setName("blockIron").setTextureName("iron_block"));
		REGISTRY.a(43, "double_stone_slab", new BlockStep(true).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stoneSlab"));
		REGISTRY.a(44, "stone_slab", new BlockStep(false).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stoneSlab"));
		Block stoneBrick = new Block(Material.STONE).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("brick").a(CreativeModeTab.b).setTextureName("brick");

		REGISTRY.a(45, "brick_block", stoneBrick);
		REGISTRY.a(46, "tnt", new BlockTNT().setHardness(0.0F).setSound(h).setName("tnt").setTextureName("tnt"));
		REGISTRY.a(47, "bookshelf", new BlockBookshelf().setHardness(1.5F).setSound(f).setName("bookshelf").setTextureName("bookshelf"));
		REGISTRY.a(48, "mossy_cobblestone", new Block(Material.STONE).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stoneMoss").a(CreativeModeTab.b).setTextureName("cobblestone_mossy"));
		REGISTRY.a(49, "obsidian", new BlockObsidian().setHardness(50.0F).setResistance(2000.0F).setSound(i).setName("obsidian").setTextureName("obsidian"));
		REGISTRY.a(50, "torch", new BlockTorch().setHardness(0.0F).a(0.9375F).setSound(f).setName("torch").setTextureName("torch_on"));
		REGISTRY.a(51, "fire", new BlockFire().setHardness(0.0F).a(1.0F).setSound(f).setName("fire").H().setTextureName("fire"));
		REGISTRY.a(52, "mob_spawner", new BlockMobSpawner().setHardness(5.0F).setSound(j).setName("mobSpawner").H().setTextureName("mob_spawner"));
		REGISTRY.a(53, "oak_stairs", new BlockStairs(woodenPlanks, 0).setName("stairsWood"));
		REGISTRY.a(54, "chest", new BlockChest(0).setHardness(2.5F).setSound(f).setName("chest"));
		REGISTRY.a(55, "redstone_wire", new BlockRedstoneWire().setHardness(0.0F).setSound(e).setName("redstoneDust").H().setTextureName("redstone_dust"));
		REGISTRY.a(56, "diamond_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreDiamond").setTextureName("diamond_ore"));
		REGISTRY.a(57, "diamond_block", new BlockOreBlock(MaterialMapColor.G).setHardness(5.0F).setResistance(10.0F).setSound(j).setName("blockDiamond").setTextureName("diamond_block"));
		REGISTRY.a(58, "crafting_table", new BlockWorkbench().setHardness(2.5F).setSound(f).setName("workbench").setTextureName("crafting_table"));
		REGISTRY.a(59, "wheat", new BlockCrops().setName("crops").setTextureName("wheat"));
		Block farmLand = new BlockSoil().setHardness(0.6F).setSound(g).setName("farmland").setTextureName("farmland");

		REGISTRY.a(60, "farmland", farmLand);
		REGISTRY.a(61, "furnace", new BlockFurnace(false).setHardness(3.5F).setSound(i).setName("furnace").a(CreativeModeTab.c));
		REGISTRY.a(62, "lit_furnace", new BlockFurnace(true).setHardness(3.5F).setSound(i).a(0.875F).setName("furnace"));
		REGISTRY.a(63, "standing_sign", new BlockSign(TileEntitySign.class, true).setHardness(1.0F).setSound(f).setName("sign").H());
		REGISTRY.a(64, "wooden_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorWood").H().setTextureName("door_wood"));
		REGISTRY.a(65, "ladder", new BlockLadder().setHardness(0.4F).setSound(o).setName("ladder").setTextureName("ladder"));
		REGISTRY.a(66, "rail", new BlockMinecartTrack().setHardness(0.7F).setSound(j).setName("rail").setTextureName("rail_normal"));
		REGISTRY.a(67, "stone_stairs", new BlockStairs(block, 0).setName("stairsStone"));
		REGISTRY.a(68, "wall_sign", new BlockSign(TileEntitySign.class, false).setHardness(1.0F).setSound(f).setName("sign").H());
		REGISTRY.a(69, "lever", new BlockLever().setHardness(0.5F).setSound(f).setName("lever").setTextureName("lever"));
		REGISTRY.a(70, "stone_pressure_plate", new BlockPressurePlateBinary("stone", Material.STONE, EnumMobType.MOBS).setHardness(0.5F).setSound(i).setName("pressurePlate"));
		REGISTRY.a(71, "iron_door", new BlockDoor(Material.ORE).setHardness(5.0F).setSound(j).setName("doorIron").H().setTextureName("door_iron"));
		REGISTRY.a(72, "wooden_pressure_plate", new BlockPressurePlateBinary("planks_oak", Material.WOOD, EnumMobType.EVERYTHING).setHardness(0.5F).setSound(f).setName("pressurePlate"));
		REGISTRY.a(73, "redstone_ore", new BlockRedstoneOre(false).setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreRedstone").a(CreativeModeTab.b).setTextureName("redstone_ore"));
		REGISTRY.a(74, "lit_redstone_ore", new BlockRedstoneOre(true).a(0.625F).setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreRedstone").setTextureName("redstone_ore"));
		REGISTRY.a(75, "unlit_redstone_torch", new BlockRedstoneTorch(false).setHardness(0.0F).setSound(f).setName("notGate").setTextureName("redstone_torch_off"));
		REGISTRY.a(76, "redstone_torch", new BlockRedstoneTorch(true).setHardness(0.0F).a(0.5F).setSound(f).setName("notGate").a(CreativeModeTab.d).setTextureName("redstone_torch_on"));
		REGISTRY.a(77, "stone_button", new BlockStoneButton().setHardness(0.5F).setSound(i).setName("button"));
		REGISTRY.a(78, "snow_layer", new BlockSnow().setHardness(0.1F).setSound(n).setName("snow").g(0).setTextureName("snow"));
		REGISTRY.a(79, "ice", new BlockIce().setHardness(0.5F).g(3).setSound(k).setName("ice").setTextureName("ice"));
		REGISTRY.a(80, "snow", new BlockSnowBlock().setHardness(0.2F).setSound(n).setName("snow").setTextureName("snow"));
		REGISTRY.a(81, "cactus", new BlockCactus().setHardness(0.4F).setSound(l).setName("cactus").setTextureName("cactus"));
		REGISTRY.a(82, "clay", new BlockClay().setHardness(0.6F).setSound(g).setName("clay").setTextureName("clay"));
		REGISTRY.a(83, "reeds", new BlockReed().setHardness(0.0F).setSound(h).setName("reeds").H().setTextureName("reeds"));
		REGISTRY.a(84, "jukebox", new BlockJukeBox().setHardness(2.0F).setResistance(10.0F).setSound(i).setName("jukebox").setTextureName("jukebox"));
		REGISTRY.a(85, "fence", new BlockFence("planks_oak", Material.WOOD).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("fence"));
		Block pumpkin = new BlockPumpkin(false).setHardness(1.0F).setSound(f).setName("pumpkin").setTextureName("pumpkin");

		REGISTRY.a(86, "pumpkin", pumpkin);
		REGISTRY.a(87, "netherrack", new BlockBloodStone().setHardness(0.4F).setSound(i).setName("hellrock").setTextureName("netherrack"));
		REGISTRY.a(88, "soul_sand", new BlockSlowSand().setHardness(0.5F).setSound(m).setName("hellsand").setTextureName("soul_sand"));
		REGISTRY.a(89, "glowstone", new BlockLightStone(Material.SHATTERABLE).setHardness(0.3F).setSound(k).a(1.0F).setName("lightgem").setTextureName("glowstone"));
		REGISTRY.a(90, "portal", new BlockPortal().setHardness(-1.0F).setSound(k).a(0.75F).setName("portal").setTextureName("portal"));
		REGISTRY.a(91, "lit_pumpkin", new BlockPumpkin(true).setHardness(1.0F).setSound(f).a(1.0F).setName("litpumpkin").setTextureName("pumpkin"));
		REGISTRY.a(92, "cake", new BlockCake().setHardness(0.5F).setSound(l).setName("cake").H().setTextureName("cake"));
		REGISTRY.a(93, "unpowered_repeater", new BlockRepeater(false).setHardness(0.0F).setSound(f).setName("diode").H().setTextureName("repeater_off"));
		REGISTRY.a(94, "powered_repeater", new BlockRepeater(true).setHardness(0.0F).a(0.625F).setSound(f).setName("diode").H().setTextureName("repeater_on"));
		REGISTRY.a(95, "stained_glass", new BlockStainedGlass(Material.SHATTERABLE).setHardness(0.3F).setSound(k).setName("stainedGlass").setTextureName("glass"));
		REGISTRY.a(96, "trapdoor", new BlockTrapdoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("trapdoor").H().setTextureName("trapdoor"));
		REGISTRY.a(97, "monster_egg", new BlockMonsterEggs().setHardness(0.75F).setName("monsterStoneEgg"));
		Block smoothBrick = new BlockSmoothBrick().setHardness(1.5F).setResistance(10.0F).setSound(i).setName("stonebricksmooth").setTextureName("stonebrick");

		REGISTRY.a(98, "stonebrick", smoothBrick);
		REGISTRY.a(99, "brown_mushroom_block", new BlockHugeMushroom(Material.WOOD, 0).setHardness(0.2F).setSound(f).setName("mushroom").setTextureName("mushroom_block"));
		REGISTRY.a(100, "red_mushroom_block", new BlockHugeMushroom(Material.WOOD, 1).setHardness(0.2F).setSound(f).setName("mushroom").setTextureName("mushroom_block"));
		REGISTRY.a(101, "iron_bars", new BlockThin("iron_bars", "iron_bars", Material.ORE, true).setHardness(5.0F).setResistance(10.0F).setSound(j).setName("fenceIron"));
		REGISTRY.a(102, "glass_pane", new BlockThin("glass", "glass_pane_top", Material.SHATTERABLE, false).setHardness(0.3F).setSound(k).setName("thinGlass"));
		Block melon = new BlockMelon().setHardness(1.0F).setSound(f).setName("melon").setTextureName("melon");

		REGISTRY.a(103, "melon_block", melon);
		REGISTRY.a(104, "pumpkin_stem", new BlockStem(pumpkin).setHardness(0.0F).setSound(f).setName("pumpkinStem").setTextureName("pumpkin_stem"));
		REGISTRY.a(105, "melon_stem", new BlockStem(melon).setHardness(0.0F).setSound(f).setName("pumpkinStem").setTextureName("melon_stem"));
		REGISTRY.a(106, "vine", new BlockVine().setHardness(0.2F).setSound(h).setName("vine").setTextureName("vine"));
		REGISTRY.a(107, "fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("fenceGate"));
		REGISTRY.a(108, "brick_stairs", new BlockStairs(stoneBrick, 0).setName("stairsBrick"));
		REGISTRY.a(109, "stone_brick_stairs", new BlockStairs(smoothBrick, 0).setName("stairsStoneBrickSmooth"));
		REGISTRY.a(110, "mycelium", new BlockMycel().setHardness(0.6F).setSound(h).setName("mycel").setTextureName("mycelium"));
		REGISTRY.a(111, "waterlily", new BlockWaterLily().setHardness(0.0F).setSound(h).setName("waterlily").setTextureName("waterlily"));
		Block netherBrick = new Block(Material.STONE).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("netherBrick").a(CreativeModeTab.b).setTextureName("nether_brick");

		REGISTRY.a(112, "nether_brick", netherBrick);
		REGISTRY.a(113, "nether_brick_fence", new BlockFence("nether_brick", Material.STONE).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("netherFence"));
		REGISTRY.a(114, "nether_brick_stairs", new BlockStairs(netherBrick, 0).setName("stairsNetherBrick"));
		REGISTRY.a(115, "nether_wart", new BlockNetherWart().setName("netherStalk").setTextureName("nether_wart"));
		REGISTRY.a(116, "enchanting_table", new BlockEnchantmentTable().setHardness(5.0F).setResistance(2000.0F).setName("enchantmentTable").setTextureName("enchanting_table"));
		REGISTRY.a(117, "brewing_stand", new BlockBrewingStand().setHardness(0.5F).a(0.125F).setName("brewingStand").setTextureName("brewing_stand"));
		REGISTRY.a(118, "cauldron", new BlockCauldron().setHardness(2.0F).setName("cauldron").setTextureName("cauldron"));
		REGISTRY.a(119, "end_portal", new BlockEnderPortal(Material.PORTAL).setHardness(-1.0F).setResistance(6000000.0F));
		REGISTRY.a(120, "end_portal_frame", new BlockEnderPortalFrame().setSound(k).a(0.125F).setHardness(-1.0F).setName("endPortalFrame").setResistance(6000000.0F).a(CreativeModeTab.c).setTextureName("endframe"));
		REGISTRY.a(121, "end_stone", new Block(Material.STONE).setHardness(3.0F).setResistance(15.0F).setSound(i).setName("whiteStone").a(CreativeModeTab.b).setTextureName("end_stone"));
		REGISTRY.a(122, "dragon_egg", new BlockDragonEgg().setHardness(3.0F).setResistance(15.0F).setSound(i).a(0.125F).setName("dragonEgg").setTextureName("dragon_egg"));
		REGISTRY.a(123, "redstone_lamp", new BlockRedstoneLamp(false).setHardness(0.3F).setSound(k).setName("redstoneLight").a(CreativeModeTab.d).setTextureName("redstone_lamp_off"));
		REGISTRY.a(124, "lit_redstone_lamp", new BlockRedstoneLamp(true).setHardness(0.3F).setSound(k).setName("redstoneLight").setTextureName("redstone_lamp_on"));
		REGISTRY.a(125, "double_wooden_slab", new BlockWoodStep(true).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("woodSlab"));
		REGISTRY.a(126, "wooden_slab", new BlockWoodStep(false).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("woodSlab"));
		REGISTRY.a(127, "cocoa", new BlockCocoa().setHardness(0.2F).setResistance(5.0F).setSound(f).setName("cocoa").setTextureName("cocoa"));
		REGISTRY.a(128, "sandstone_stairs", new BlockStairs(sandstone, 0).setName("stairsSandStone"));
		REGISTRY.a(129, "emerald_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreEmerald").setTextureName("emerald_ore"));
		REGISTRY.a(130, "ender_chest", new BlockEnderChest().setHardness(22.5F).setResistance(1000.0F).setSound(i).setName("enderChest").a(0.5F));
		REGISTRY.a(131, "tripwire_hook", new BlockTripwireHook().setName("tripWireSource").setTextureName("trip_wire_source"));
		REGISTRY.a(132, "tripwire", new BlockTripwire().setName("tripWire").setTextureName("trip_wire"));
		REGISTRY.a(133, "emerald_block", new BlockOreBlock(MaterialMapColor.I).setHardness(5.0F).setResistance(10.0F).setSound(j).setName("blockEmerald").setTextureName("emerald_block"));
		REGISTRY.a(134, "spruce_stairs", new BlockStairs(woodenPlanks, 1).setName("stairsWoodSpruce"));
		REGISTRY.a(135, "birch_stairs", new BlockStairs(woodenPlanks, 2).setName("stairsWoodBirch"));
		REGISTRY.a(136, "jungle_stairs", new BlockStairs(woodenPlanks, 3).setName("stairsWoodJungle"));
		REGISTRY.a(137, "command_block", new BlockCommand().s().setResistance(6000000.0F).setName("commandBlock").setTextureName("command_block"));
		REGISTRY.a(138, "beacon", new BlockBeacon().setName("beacon").a(1.0F).setTextureName("beacon"));
		REGISTRY.a(139, "cobblestone_wall", new BlockCobbleWall(block).setName("cobbleWall"));
		REGISTRY.a(140, "flower_pot", new BlockFlowerPot().setHardness(0.0F).setSound(e).setName("flowerPot").setTextureName("flower_pot"));
		REGISTRY.a(141, "carrots", new BlockCarrots().setName("carrots").setTextureName("carrots"));
		REGISTRY.a(142, "potatoes", new BlockPotatoes().setName("potatoes").setTextureName("potatoes"));
		REGISTRY.a(143, "wooden_button", new BlockWoodButton().setHardness(0.5F).setSound(f).setName("button"));
		REGISTRY.a(144, "skull", new BlockSkull().setHardness(1.0F).setSound(i).setName("skull").setTextureName("skull"));
		REGISTRY.a(145, "anvil", new BlockAnvil().setHardness(5.0F).setSound(p).setResistance(2000.0F).setName("anvil"));
		REGISTRY.a(146, "trapped_chest", new BlockChest(1).setHardness(2.5F).setSound(f).setName("chestTrap"));
		REGISTRY.a(147, "light_weighted_pressure_plate", new BlockPressurePlateWeighted("gold_block", Material.ORE, 15).setHardness(0.5F).setSound(f).setName("weightedPlate_light"));
		REGISTRY.a(148, "heavy_weighted_pressure_plate", new BlockPressurePlateWeighted("iron_block", Material.ORE, 150).setHardness(0.5F).setSound(f).setName("weightedPlate_heavy"));
		REGISTRY.a(149, "unpowered_comparator", new BlockRedstoneComparator(false).setHardness(0.0F).setSound(f).setName("comparator").H().setTextureName("comparator_off"));
		REGISTRY.a(150, "powered_comparator", new BlockRedstoneComparator(true).setHardness(0.0F).a(0.625F).setSound(f).setName("comparator").H().setTextureName("comparator_on"));
		REGISTRY.a(151, "daylight_detector", new BlockDaylightDetector().setHardness(0.2F).setSound(f).setName("daylightDetector").setTextureName("daylight_detector"));
		REGISTRY.a(152, "redstone_block", new BlockRedstone(MaterialMapColor.f).setHardness(5.0F).setResistance(10.0F).setSound(j).setName("blockRedstone").setTextureName("redstone_block"));
		REGISTRY.a(153, "quartz_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setSound(i).setName("netherquartz").setTextureName("quartz_ore"));
		REGISTRY.a(154, "hopper", new BlockHopper().setHardness(3.0F).setResistance(8.0F).setSound(f).setName("hopper").setTextureName("hopper"));
		Block quartzBlock = new BlockQuartz().setSound(i).setHardness(0.8F).setName("quartzBlock").setTextureName("quartz_block");

		REGISTRY.a(155, "quartz_block", quartzBlock);
		REGISTRY.a(156, "quartz_stairs", new BlockStairs(quartzBlock, 0).setName("stairsQuartz"));
		REGISTRY.a(157, "activator_rail", new BlockPoweredRail().setHardness(0.7F).setSound(j).setName("activatorRail").setTextureName("rail_activator"));
		REGISTRY.a(158, "dropper", new BlockDropper().setHardness(3.5F).setSound(i).setName("dropper").setTextureName("dropper"));
		REGISTRY.a(159, "stained_hardened_clay", new BlockCloth(Material.STONE).setHardness(1.25F).setResistance(7.0F).setSound(i).setName("clayHardenedStained").setTextureName("hardened_clay_stained"));
		REGISTRY.a(160, "stained_glass_pane", new BlockStainedGlassPane().setHardness(0.3F).setSound(k).setName("thinStainedGlass").setTextureName("glass"));
		REGISTRY.a(161, "leaves2", new BlockLeaves2().setName("leaves").setTextureName("leaves"));
		REGISTRY.a(162, "log2", new BlockLog2().setName("log").setTextureName("log"));
		REGISTRY.a(163, "acacia_stairs", new BlockStairs(woodenPlanks, 4).setName("stairsWoodAcacia"));
		REGISTRY.a(164, "dark_oak_stairs", new BlockStairs(woodenPlanks, 5).setName("stairsWoodDarkOak"));
		REGISTRY.a(170, "hay_block", new BlockHay().setHardness(0.5F).setSound(h).setName("hayBlock").a(CreativeModeTab.b).setTextureName("hay_block"));
		REGISTRY.a(171, "carpet", new BlockCarpet().setHardness(0.1F).setSound(l).setName("woolCarpet").g(0));
		REGISTRY.a(172, "hardened_clay", new BlockHardenedClay().setHardness(1.25F).setResistance(7.0F).setSound(i).setName("clayHardened").setTextureName("hardened_clay"));
		REGISTRY.a(173, "coal_block", new Block(Material.STONE).setHardness(5.0F).setResistance(10.0F).setSound(i).setName("blockCoal").a(CreativeModeTab.b).setTextureName("coal_block"));
		REGISTRY.a(174, "packed_ice", new BlockPackedIce().setHardness(0.5F).setSound(k).setName("icePacked").setTextureName("ice_packed"));
		REGISTRY.a(175, "double_plant", new BlockTallPlant());
		
		// ClipSpigot start - hardcode 1.8
		
		REGISTRY.a(165, "slime_block", new Block18(Material.TNT).setHardness(0.6F).setSound(slimeStepSound).setName("slimeBlock").setTextureName("slime_block"));
		REGISTRY.a(166, "barrier", new Block(Material.STONE).s().setResistance(6000000.0F).setSound(i).setName("barrier").H().setTextureName("barrier"));
		REGISTRY.a(167, "iron_trapdoor", new BlockTrapdoor(Material.ORE).setHardness(5.0F).setSound(f).setName("ironTrapdoor").H().setTextureName("ironTrapdoor"));
		REGISTRY.a(168, "prismarine", new Block18(Material.STONE).setHardness(1.5F).setResistance(10.0F).setSound(i).setName("prismarine").setTextureName("prismarine"));
		REGISTRY.a(169, "sea_lantern", new BlockLightStone(Material.SHATTERABLE).setHardness(0.3F).setSound(k).setResistance(1.0F).setName("seaLantern").setTextureName("sea_lantern"));
		REGISTRY.a(179, "red_sandstone", new BlockSandStone().setSound(i).setHardness(0.8F).setName("redSandstone").setTextureName("red_sandstone"));
		
		Block resSandstone = new BlockSandStone().setSound(i).setHardness(0.8F).setName("redSandStone");
		REGISTRY.a(179, "red_sandstone", resSandstone);
		REGISTRY.a(180, "red_sandstone_stairs", new BlockStairs(resSandstone, 0).setName("stairsRedSandStone"));
		REGISTRY.a(181, "double_stone_slab2", new BlockStep(true).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stoneSlab2"));
		REGISTRY.a(182, "stone_slab2", new BlockStep(false).setHardness(2.0F).setResistance(10.0F).setSound(i).setName("stoneSlab2"));
		REGISTRY.a(183, "spruce_fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("spruceFenceGate"));
		REGISTRY.a(184, "birch_fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("birchFenceGate"));
		REGISTRY.a(185, "jungle_fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("jungleFenceGate"));
		REGISTRY.a(186, "dark_oak_fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("darkOakFenceGate"));
		REGISTRY.a(187, "acacia_fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setSound(f).setName("acaciaFenceGate"));
		REGISTRY.a(188, "spruce_fence", new BlockFence("planks_spruce", Material.WOOD).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("spruceFence"));
		REGISTRY.a(189, "birch_fence", new BlockFence("planks_birch", Material.WOOD).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("birchFence"));
		REGISTRY.a(190, "jungle_fence", new BlockFence("planks_jungle", Material.WOOD).setHardness(2.0F).setResistance(5.0F).setSound(f).setName("jungleFence"));
		REGISTRY.a(191, "dark_oak_fence", new BlockFence("planks_dark_oak", Material.WOOD).setHardness(2.0F).setResistance(5.0F).a(5).setName("darkOakFence"));
		REGISTRY.a(192, "acacia_fence", new BlockFence("planks_acacia", Material.WOOD).setHardness(2.0F).setResistance(5.0F).a(5).setName("acaciaFence"));
		REGISTRY.a(193, "spruce_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorSpruce"));
		REGISTRY.a(194, "birch_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorBirch"));
		REGISTRY.a(195, "jungle_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorJungle"));
		REGISTRY.a(196, "acacia_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorAcacia"));
		REGISTRY.a(197, "dark_oak_door", new BlockDoor(Material.WOOD).setHardness(3.0F).setSound(f).setName("doorDarkOak"));
		// ClipSpigot end
		
		// Alkazia - Define new block -V1-
        
        REGISTRY.a(200, "bauxite_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setSound(i).setName("oreBauxite").setTextureName("bauxite_ore"));
        REGISTRY.a(201, "granite_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setSound(i).H().setName("oreGranite").setTextureName("granite_ore"));
        REGISTRY.a(202, "opale_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setSound(i).H().setName("oreOpale").setTextureName("opale_ore"));
        REGISTRY.a(203, "meteor_ore", (new BlockOre()).setHardness(3.0F).setResistance(5.0F).setSound(i).H().setName("oreMeteor").setTextureName("meteor_ore"));
       
        REGISTRY.a(204, "bauxite_block", (new BlockOreBlock(MaterialMapColor.G)).setHardness(5.0F).setResistance(10.0F).H().setSound(j).setName("blockBauxite").setTextureName("bauxite_block"));
        REGISTRY.a(205, "granite_block", (new BlockOreBlock(MaterialMapColor.G)).setHardness(5.0F).setResistance(10.0F).H().setSound(j).setName("blockGarnite").setTextureName("granite_block"));
        REGISTRY.a(206, "opale_block", (new BlockOreBlock(MaterialMapColor.G)).setHardness(5.0F).setResistance(10.0F).H().setSound(j).setName("blockOpale").setTextureName("opale_block"));
        REGISTRY.a(207, "meteor_block", (new BlockOreBlock(MaterialMapColor.G)).setHardness(5.0F).setResistance(10.0F).H().setSound(j).setName("blockMeteor").setTextureName("meteor_block"));
        
        REGISTRY.a(208, "pousse", (new Block(Material.HEAVY)).setHardness(5.0F).setSound(i).setResistance(10.0F).setName("pousse").a(CreativeModeTab.b).setTextureName("pousse")); 
        REGISTRY.a(209, "iron_chest", (new BlockIronChest(0)).setHardness(2.5F).setSound(j).setName("chestIron").a(CreativeModeTab.d)); //modif
        REGISTRY.a(210, "meteorite", (new BlockMeteorite()).setHardness(50.0F).setResistance(23.0F).setSound(i).setName("meteor_brut").setTextureName("meteor_brut")); //modif
        REGISTRY.a(211, "invisible", (new BlockInvisible(Material.SHATTERABLE, false)).setHardness(10.0F).setSound(k).a(1.0F).setName("invisible").setTextureName("invisible")); //modif
        //REGISTRY.a(212, "invisible_door", (new BlockDoor(Material.STONE)).setHardness(5.0F).setSound(soundTypeMetal).setName("doorInvisible").H().setTextureName("door_invisible").setCreativeTab(CreativeModeTab.tabRedstone)); //modif
        REGISTRY.a(213, "lumiere", (new BlockAir()).setHardness(10.0F).a(1.0F).setName("lumiere").setTextureName("lumiere").a(CreativeModeTab.b)); //modif
        
        // Alkazia - Define new block -V2-
        REGISTRY.a(214, "andesite", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("andesite").a(CreativeModeTab.b).setTextureName("andesite")); 
        REGISTRY.a(215, "andesite_smooth", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("andesite_smooth").a(CreativeModeTab.b).setTextureName("andesite_smooth")); 
        REGISTRY.a(216, "alumite", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("alumite").a(CreativeModeTab.b).setTextureName("alumite")); 
        REGISTRY.a(217, "alumite_smooth", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("alumite_smooth").a(CreativeModeTab.b).setTextureName("alumite_smooth")); 
        REGISTRY.a(218, "diorite", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("diorite").a(CreativeModeTab.b).setTextureName("diorite")); 
        REGISTRY.a(219, "diorite_smooth", (new Block(Material.STONE)).setHardness(5.0F).setSound(i).setResistance(5.0F).setName("diorite_smooth").a(CreativeModeTab.b).setTextureName("diorite_smooth")); 
        REGISTRY.a(220, "ironLadder", (new BlockLadder()).setHardness(0.4F).setSound(o).setName("iron_ladder").setTextureName("iron_ladder"));
        REGISTRY.a(221, "powderBlock", (new BlockPowder()).setHardness(20.0F).setResistance(5.0F).setSound(i).setName("powderBlock").setTextureName("powder"));
        
        // Alkazia - V3 bitch
        REGISTRY.a(222, "fakub", (new Block(Material.STONE)).setHardness(50.0F).setResistance(2000.0F).setSound(i).setName("fakub").setTextureName("fakub").a(CreativeModeTab.b));
        REGISTRY.a(223, "fakub_off", (new Block(Material.STONE)).setHardness(50.0F).setResistance(2000.0F).setSound(i).setName("fakub_off").setTextureName("fakub_off").a(CreativeModeTab.b));
        
        // Alkazia - V4 
        REGISTRY.a(224, "presentBlock", (new BlockPresent()).setHardness(10.0F).setResistance(5.0F).setSound(l).setName("presentBlock").setTextureName("present"));
		
		Iterator registryIterator = REGISTRY.iterator();

		while (registryIterator.hasNext()) {
			Block theBlock = (Block) registryIterator.next();

			if (theBlock.material == Material.AIR) {
				theBlock.u = false;
			} else {
				boolean flag = false;
				boolean flag1 = theBlock.b() == 10;
				boolean isStep = theBlock instanceof BlockStepAbstract;
				boolean isFarmland = theBlock == farmLand;
				boolean flag4 = theBlock.s;
				boolean flag5 = theBlock.r == 0;

				if (flag1 || isStep || isFarmland || flag4 || flag5) {
					flag = true;
				}

				theBlock.u = flag;
			}
		}
	}

	protected Block(Material material) {
		stepSound = e;
		I = 1.0F;
		frictionFactor = 0.6F;
		this.material = material;
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		q = this.c();
		r = this.c() ? 255 : 0;
		s = !material.blocksLight();
	}

	protected Block setSound(StepSound stepsound) {
		stepSound = stepsound;
		return this;
	}

	protected Block g(int i) {
		r = i;
		return this;
	}

	protected Block a(float f) {
		t = (int) (15.0F * f);
		return this;
	}

	protected Block setResistance(float f) {
		durability = f * 3.0F;
		return this;
	}

	public boolean r() {
		return material.k() && this.d() && !isPowerSource();
	}

	public boolean d() {
		return true;
	}

	public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
		return !material.isSolid();
	}

	public int b() {
		return 0;
	}

	protected Block setHardness(float f) {
		strength = f;
		if (durability < f * 5.0F) {
			durability = f * 5.0F;
		}

		return this;
	}

	protected Block s() {
		this.setHardness(-1.0F);
		return this;
	}

	public float f(World world, int i, int j, int k) {
		return strength;
	}

	protected Block a(boolean flag) {
		z = flag;
		return this;
	}

	public boolean isTicking() {
		return z;
	}

	public boolean isTileEntity() {
		return isTileEntity;
	}
	
	protected final void setBounds(float f, float f1, float f2, float f3, float f4, float f5) {
		minX = f;
		minY = f1;
		minZ = f2;
		maxX = f3;
		maxY = f4;
		maxZ = f5;
	}

	public boolean d(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getType(i, j, k).getMaterial().isBuildable();
	}

	public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		AxisAlignedBB axisalignedbb1 = this.a(world, i, j, k);

		if (axisalignedbb1 != null && axisalignedbb.b(axisalignedbb1)) {
			list.add(axisalignedbb1);
		}
	}

	public AxisAlignedBB a(World world, int i, int j, int k) {
		return AxisAlignedBB.a(i + minX, j + minY, k + minZ, i + maxX, j + maxY, k + maxZ);
	}

	public boolean c() {
		return true;
	}

	public boolean a(int i, boolean flag) {
		return v();
	}

	public boolean v() {
		return true;
	}

	public void a(World world, int i, int j, int k, Random random) {
	}

	public void postBreak(World world, int i, int j, int k, int l) {
	}

	public void doPhysics(World world, int i, int j, int k, Block block) {
	}

	public int a(World world) {
		return 10;
	}

	public void onPlace(World world, int i, int j, int k) {
		org.spigotmc.AsyncCatcher.catchOp("block onPlace"); // Spigot
	}

	public void remove(World world, int i, int j, int k, Block block, int l) {
		org.spigotmc.AsyncCatcher.catchOp("block remove"); // Spigot
	}

	public int a(Random random) {
		return 1;
	}

	public Item getDropType(int i, Random random, int j) {
		return Item.getItemOf(this);
	}

	public float getDamage(EntityHuman entityhuman, World world, int i, int j, int k) {
		float f = this.f(world, i, j, k);

		return f < 0.0F ? 0.0F : !entityhuman.a(this) ? entityhuman.a(this, false) / f / 100.0F : entityhuman.a(this, true) / f / 30.0F;
	}

	public final void b(World world, int i, int j, int k, int l, int i1) {
		dropNaturally(world, i, j, k, l, 1.0F, i1);
	}

	public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
		if (!world.isStatic) {
			int j1 = getDropCount(i1, world.random);

			for (int k1 = 0; k1 < j1; ++k1) {
				// CraftBukkit - <= to < to allow for plugins to completely disable block drops from explosions
				if (world.random.nextFloat() < f) {
					Item item = getDropType(l, world.random, i1);

					if (item != null) {
						this.a(world, i, j, k, new ItemStack(item, 1, this.getDropData(l)));
					}
				}
			}
		}
	}

	protected void a(World world, int i, int j, int k, ItemStack itemstack) {
		if (!world.isStatic && world.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.7F;
			double d0 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
			double d1 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
			double d2 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, i + d0, j + d1, k + d2, itemstack);

			entityitem.pickupDelay = 10;
			world.addEntity(entityitem);
		}
	}

	protected void dropExperience(World world, int i, int j, int k, int l) {
		if (!world.isStatic) {
			while (l > 0) {
				int i1 = EntityExperienceOrb.getOrbValue(l);

				l -= i1;
				world.addEntity(new EntityExperienceOrb(world, i + 0.5D, j + 0.5D, k + 0.5D, i1));
			}
		}
	}

	public int getDropData(int i) {
		return 0;
	}

	public float a(Entity entity) {
		return durability / 5.0F;
	}

	public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
		updateShape(world, i, j, k);
		vec3d = vec3d.add(-i, -j, -k);
		vec3d1 = vec3d1.add(-i, -j, -k);
		Vec3D vec3d2 = vec3d.b(vec3d1, minX);
		Vec3D vec3d3 = vec3d.b(vec3d1, maxX);
		Vec3D vec3d4 = vec3d.c(vec3d1, minY);
		Vec3D vec3d5 = vec3d.c(vec3d1, maxY);
		Vec3D vec3d6 = vec3d.d(vec3d1, minZ);
		Vec3D vec3d7 = vec3d.d(vec3d1, maxZ);

		if (!this.a(vec3d2)) {
			vec3d2 = null;
		}

		if (!this.a(vec3d3)) {
			vec3d3 = null;
		}

		if (!this.b(vec3d4)) {
			vec3d4 = null;
		}

		if (!this.b(vec3d5)) {
			vec3d5 = null;
		}

		if (!this.setHardness(vec3d6)) {
			vec3d6 = null;
		}

		if (!this.setHardness(vec3d7)) {
			vec3d7 = null;
		}

		Vec3D vec3d8 = null;

		if (vec3d2 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d2) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d2;
		}

		if (vec3d3 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d3) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d3;
		}

		if (vec3d4 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d4) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d4;
		}

		if (vec3d5 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d5) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d5;
		}

		if (vec3d6 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d6) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d6;
		}

		if (vec3d7 != null && (vec3d8 == null || vec3d.distanceSquared(vec3d7) < vec3d.distanceSquared(vec3d8))) {
			vec3d8 = vec3d7;
		}

		if (vec3d8 == null)
			return null;
		else {
			byte b0 = -1;

			if (vec3d8 == vec3d2) {
				b0 = 4;
			}

			if (vec3d8 == vec3d3) {
				b0 = 5;
			}

			if (vec3d8 == vec3d4) {
				b0 = 0;
			}

			if (vec3d8 == vec3d5) {
				b0 = 1;
			}

			if (vec3d8 == vec3d6) {
				b0 = 2;
			}

			if (vec3d8 == vec3d7) {
				b0 = 3;
			}

			return new MovingObjectPosition(i, j, k, b0, vec3d8.add(i, j, k));
		}
	}

	private boolean a(Vec3D vec3d) {
		return vec3d == null ? false : vec3d.b >= minY && vec3d.b <= maxY && vec3d.c >= minZ && vec3d.c <= maxZ;
	}

	private boolean b(Vec3D vec3d) {
		return vec3d == null ? false : vec3d.a >= minX && vec3d.a <= maxX && vec3d.c >= minZ && vec3d.c <= maxZ;
	}

	private boolean setHardness(Vec3D vec3d) {
		return vec3d == null ? false : vec3d.a >= minX && vec3d.a <= maxX && vec3d.b >= minY && vec3d.b <= maxY;
	}

	public void wasExploded(World world, int i, int j, int k, Explosion explosion) {
	}

	public boolean canPlace(World world, int i, int j, int k, int l, ItemStack itemstack) {
		return this.canPlace(world, i, j, k, l);
	}

	public boolean canPlace(World world, int i, int j, int k, int l) {
		return this.canPlace(world, i, j, k);
	}

	public boolean canPlace(World world, int i, int j, int k) {
		return world.getType(i, j, k).material.isReplaceable();
	}

	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
		return false;
	}

	public void b(World world, int i, int j, int k, Entity entity) {
	}

	public int getPlacedData(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
		return i1;
	}

	public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
	}

	public void a(World world, int i, int j, int k, Entity entity, Vec3D vec3d) {
	}

	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
	}

	public final double x() {
		return minX;
	}

	public final double y() {
		return maxX;
	}

	public final double z() {
		return minY;
	}

	public final double A() {
		return maxY;
	}

	public final double B() {
		return minZ;
	}

	public final double C() {
		return maxZ;
	}

	public int b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return 0;
	}

	public boolean isPowerSource() {
		return false;
	}

	public void a(World world, int i, int j, int k, Entity entity) {
	}

	public int c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return 0;
	}

	public void g() {
	}

	public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
		entityhuman.a(StatisticList.MINE_BLOCK_COUNT[getId(this)], 1);
		entityhuman.applyExhaustion(world.paperSpigotConfig.blockBreakExhaustion); // PaperSpigot - Configurable block break exhaustion
		if (E() && EnchantmentManager.hasSilkTouchEnchantment(entityhuman)) {
			ItemStack itemstack = this.j(l);

			if (itemstack != null) {
				this.a(world, i, j, k, itemstack);
			}
		} else {
			int i1 = EnchantmentManager.getBonusBlockLootEnchantmentLevel(entityhuman);

			this.b(world, i, j, k, l, i1);
		}
	}

	protected boolean E() {
		return this.d() && !isTileEntity;
	}

	protected ItemStack j(int i) {
		int j = 0;
		Item item = Item.getItemOf(this);

		if (item != null && item.n()) {
			j = i;
		}

		return new ItemStack(item, 1, j);
	}

	public int getDropCount(int i, Random random) {
		return this.a(random);
	}

	public boolean j(World world, int i, int j, int k) {
		return true;
	}

	public void postPlace(World world, int i, int j, int k, EntityLiving entityliving, ItemStack itemstack) {
	}

	public void postPlace(World world, int i, int j, int k, int l) {
	}

	public Block setName(String s) {
		name = s;
		return this;
	}

	public String getName() {
		return LocaleI18n.get(this.a() + ".name");
	}

	public String a() {
		return "tile." + name;
	}

	public boolean a(World world, int i, int j, int k, int l, int i1) {
		return false;
	}

	public boolean G() {
		return y;
	}

	protected Block H() {
		y = false;
		return this;
	}

	public int h() {
		return material.getPushReaction();
	}

	public void a(World world, int i, int j, int k, Entity entity, float f) {
	}

	public int getDropData(World world, int i, int j, int k) {
		return this.getDropData(world.getData(i, j, k));
	}

	public Block a(CreativeModeTab creativemodetab) {
		creativeTab = creativemodetab;
		return this;
	}

	public void a(World world, int i, int j, int k, int l, EntityHuman entityhuman) {
	}

	public void f(World world, int i, int j, int k, int l) {
	}

	public void l(World world, int i, int j, int k) {
	}

	public boolean L() {
		return true;
	}

	public boolean a(Explosion explosion) {
		return true;
	}

	public boolean c(Block block) {
		return this == block;
	}

	public static boolean a(Block block, Block block1) {
		return block != null && block1 != null ? block == block1 ? true : block.c(block1) : false;
	}

	public boolean isComplexRedstone() {
		return false;
	}

	public int g(World world, int i, int j, int k, int l) {
		return 0;
	}

	protected Block setTextureName(String s) {
		d = s;
		return this;
	}

	// CraftBukkit start
	public int getExpDrop(World world, int data, int enchantmentLevel) {
		return 0;
	}

	// CraftBukkit end

	// Spigot start
	public static float range(float min, float value, float max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
	// Spigot end
}
