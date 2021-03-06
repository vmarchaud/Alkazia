package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Block {

    private static final MinecraftKey a = new MinecraftKey("air");
    public static final RegistryBlocks REGISTRY = new RegistryBlocks(Block.a);
    public static final RegistryID d = new RegistryID();
    private CreativeModeTab creativeTab;
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
    public static final StepSound q = new StepSoundSlime("slime", 1.0F, 1.0F);
    protected boolean r;
    protected int s;
    protected boolean t;
    protected int u;
    protected boolean v;
    protected float strength;
    protected float durability;
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
    protected final BlockStateList blockStateList;
    private IBlockData blockData;
    private String name;

    public static int getId(Block block) {
        return Block.REGISTRY.b(block);
    }

    public static int getCombinedId(IBlockData iblockdata) {
        return getId(iblockdata.getBlock()) + (iblockdata.getBlock().toLegacyData(iblockdata) << 12);
    }

    public static Block getById(int i) {
        return (Block) Block.REGISTRY.a(i);
    }

    public static IBlockData getByCombinedId(int i) {
        int j = i & 4095;
        int k = i >> 12 & 15;

        return getById(j).fromLegacyData(k);
    }

    public static Block asBlock(Item item) {
        return item instanceof ItemBlock ? ((ItemBlock) item).d() : null;
    }

    public static Block getByName(String s) {
        MinecraftKey minecraftkey = new MinecraftKey(s);

        if (Block.REGISTRY.d(minecraftkey)) {
            return (Block) Block.REGISTRY.get(minecraftkey);
        } else {
            try {
                return (Block) Block.REGISTRY.a(Integer.parseInt(s));
            } catch (NumberFormatException numberformatexception) {
                return null;
            }
        }
    }

    public boolean m() {
        return this.r;
    }

    public int n() {
        return this.s;
    }

    public int p() {
        return this.u;
    }

    public boolean q() {
        return this.v;
    }

    public Material getMaterial() {
        return this.material;
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return this.getMaterial().r();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData();
    }

    public int toLegacyData(IBlockData iblockdata) {
        if (iblockdata != null && !iblockdata.a().isEmpty()) {
            throw new IllegalArgumentException("Don\'t know how to convert " + iblockdata + " back into data...");
        } else {
            return 0;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata;
    }

    protected Block(Material material) {
        this.stepSound = Block.e;
        this.I = 1.0F;
        this.frictionFactor = 0.6F;
        this.material = material;
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.r = this.c();
        this.s = this.c() ? 255 : 0;
        this.t = !material.blocksLight();
        this.blockStateList = this.getStateList();
        this.j(this.blockStateList.getBlockData());
    }

    protected Block a(StepSound stepsound) {
        this.stepSound = stepsound;
        return this;
    }

    protected Block e(int i) {
        this.s = i;
        return this;
    }

    protected Block a(float f) {
        this.u = (int) (15.0F * f);
        return this;
    }

    protected Block b(float f) {
        this.durability = f * 3.0F;
        return this;
    }

    public boolean s() {
        return this.material.isSolid() && this.d();
    }

    public boolean isOccluding() {
        return this.material.k() && this.d() && !this.isPowerSource();
    }

    public boolean u() {
        return this.material.isSolid() && this.d();
    }

    public boolean d() {
        return true;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !this.material.isSolid();
    }

    public int b() {
        return 3;
    }

    public boolean f(World world, BlockPosition blockposition) {
        return false;
    }

    protected Block c(float f) {
        this.strength = f;
        if (this.durability < f * 5.0F) {
            this.durability = f * 5.0F;
        }

        return this;
    }

    protected Block v() {
        this.c(-1.0F);
        return this;
    }

    public float g(World world, BlockPosition blockposition) {
        return this.strength;
    }

    protected Block a(boolean flag) {
        this.z = flag;
        return this;
    }

    public boolean isTicking() {
        return this.z;
    }

    public boolean isTileEntity() {
        return this.isTileEntity;
    }

    protected final void a(float f, float f1, float f2, float f3, float f4, float f5) {
        this.minX = (double) f;
        this.minY = (double) f1;
        this.minZ = (double) f2;
        this.maxX = (double) f3;
        this.maxY = (double) f4;
        this.maxZ = (double) f5;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockaccess.getType(blockposition).getBlock().getMaterial().isBuildable();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        AxisAlignedBB axisalignedbb1 = this.a(world, blockposition, iblockdata);

        if (axisalignedbb1 != null && axisalignedbb.b(axisalignedbb1)) {
            list.add(axisalignedbb1);
        }

    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new AxisAlignedBB((double) blockposition.getX() + this.minX, (double) blockposition.getY() + this.minY, (double) blockposition.getZ() + this.minZ, (double) blockposition.getX() + this.maxX, (double) blockposition.getY() + this.maxY, (double) blockposition.getZ() + this.maxZ);
    }

    public boolean c() {
        return true;
    }

    public boolean a(IBlockData iblockdata, boolean flag) {
        return this.y();
    }

    public boolean y() {
        return true;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.b(world, blockposition, iblockdata, random);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {}

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {}

    public int a(World world) {
        return 10;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        org.spigotmc.AsyncCatcher.catchOp( "block onPlace"); // Spigot
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        org.spigotmc.AsyncCatcher.catchOp( "block remove"); // Spigot
    }

    public int a(Random random) {
        return 1;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(this);
    }

    public float getDamage(EntityHuman entityhuman, World world, BlockPosition blockposition) {
        float f = this.g(world, blockposition);

        return f < 0.0F ? 0.0F : (!entityhuman.b(this) ? entityhuman.a(this) / f / 100.0F : entityhuman.a(this) / f / 30.0F);
    }

    public final void b(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        this.dropNaturally(world, blockposition, iblockdata, 1.0F, i);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isStatic) {
            int j = this.getDropCount(i, world.random);

            for (int k = 0; k < j; ++k) {
                // CraftBukkit - <= to < to allow for plugins to completely disable block drops from explosions
                if (world.random.nextFloat() < f) {
                    Item item = this.getDropType(iblockdata, world.random, i);

                    if (item != null) {
                        a(world, blockposition, new ItemStack(item, 1, this.getDropData(iblockdata)));
                    }
                }
            }

        }
    }

    public static void a(World world, BlockPosition blockposition, ItemStack itemstack) {
        if (!world.isStatic && world.getGameRules().getBoolean("doTileDrops")) {
            float f = 0.5F;
            double d0 = (double) (world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack);

            entityitem.p();
            world.addEntity(entityitem);
        }
    }

    protected void dropExperience(World world, BlockPosition blockposition, int i) {
        if (!world.isStatic) {
            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                world.addEntity(new EntityExperienceOrb(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, j));
            }
        }

    }

    public int getDropData(IBlockData iblockdata) {
        return 0;
    }

    public float a(Entity entity) {
        return this.durability / 5.0F;
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, blockposition);
        vec3d = vec3d.add((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()));
        vec3d1 = vec3d1.add((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()));
        Vec3D vec3d2 = vec3d.a(vec3d1, this.minX);
        Vec3D vec3d3 = vec3d.a(vec3d1, this.maxX);
        Vec3D vec3d4 = vec3d.b(vec3d1, this.minY);
        Vec3D vec3d5 = vec3d.b(vec3d1, this.maxY);
        Vec3D vec3d6 = vec3d.c(vec3d1, this.minZ);
        Vec3D vec3d7 = vec3d.c(vec3d1, this.maxZ);

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

        if (!this.c(vec3d6)) {
            vec3d6 = null;
        }

        if (!this.c(vec3d7)) {
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

        if (vec3d8 == null) {
            return null;
        } else {
            EnumDirection enumdirection = null;

            if (vec3d8 == vec3d2) {
                enumdirection = EnumDirection.WEST;
            }

            if (vec3d8 == vec3d3) {
                enumdirection = EnumDirection.EAST;
            }

            if (vec3d8 == vec3d4) {
                enumdirection = EnumDirection.DOWN;
            }

            if (vec3d8 == vec3d5) {
                enumdirection = EnumDirection.UP;
            }

            if (vec3d8 == vec3d6) {
                enumdirection = EnumDirection.NORTH;
            }

            if (vec3d8 == vec3d7) {
                enumdirection = EnumDirection.SOUTH;
            }

            return new MovingObjectPosition(vec3d8.add((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()), enumdirection, blockposition);
        }
    }

    private boolean a(Vec3D vec3d) {
        return vec3d == null ? false : vec3d.b >= this.minY && vec3d.b <= this.maxY && vec3d.c >= this.minZ && vec3d.c <= this.maxZ;
    }

    private boolean b(Vec3D vec3d) {
        return vec3d == null ? false : vec3d.a >= this.minX && vec3d.a <= this.maxX && vec3d.c >= this.minZ && vec3d.c <= this.maxZ;
    }

    private boolean c(Vec3D vec3d) {
        return vec3d == null ? false : vec3d.a >= this.minX && vec3d.a <= this.maxX && vec3d.b >= this.minY && vec3d.b <= this.maxY;
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {}

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        return this.canPlace(world, blockposition, enumdirection);
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.canPlace(world, blockposition);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return world.getType(blockposition).getBlock().material.isReplaceable();
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        return false;
    }

    public void a(World world, BlockPosition blockposition, Entity entity) {}

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.fromLegacyData(i);
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {}

    public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
        return vec3d;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {}

    public final double z() {
        return this.minX;
    }

    public final double A() {
        return this.maxX;
    }

    public final double B() {
        return this.minY;
    }

    public final double C() {
        return this.maxY;
    }

    public final double D() {
        return this.minZ;
    }

    public final double E() {
        return this.maxZ;
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return 0;
    }

    public boolean isPowerSource() {
        return false;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {}

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return 0;
    }

    public void h() {}

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        entityhuman.b(StatisticList.MINE_BLOCK_COUNT[getId(this)]);
        entityhuman.applyExhaustion(0.020F);
        if (this.G() && EnchantmentManager.hasSilkTouchEnchantment(entityhuman)  && this != Blocks.METEORITE && this != Blocks.POWDER_BLOCK && this != Blocks.METEOR_ORE) {
            ItemStack itemstack = this.i(iblockdata);

            if (itemstack != null) {
                a(world, blockposition, itemstack);
            }
        } else {
        	if(this == Blocks.METEOR_ORE) {
                  this.b(world, blockposition, iblockdata, 0); 
                  return;
            }
        	
            int i = EnchantmentManager.getBonusBlockLootEnchantmentLevel(entityhuman);

            this.b(world, blockposition, iblockdata, i);
        }

    }

    protected boolean G() {
        return this.d() && !this.isTileEntity;
    }

    protected ItemStack i(IBlockData iblockdata) {
        int i = 0;
        Item item = Item.getItemOf(this);

        if (item != null && item.k()) {
            i = this.toLegacyData(iblockdata);
        }

        return new ItemStack(item, 1, i);
    }

    public int getDropCount(int i, Random random) {
        return this.a(random);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {}

    public Block c(String s) {
        this.name = s;
        return this;
    }

    public String getName() {
        return LocaleI18n.get(this.a() + ".name");
    }

    public String a() {
        return "tile." + this.name;
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        return false;
    }

    public boolean I() {
        return this.y;
    }

    protected Block J() {
        this.y = false;
        return this;
    }

    public int i() {
        return this.material.getPushReaction();
    }

    public void a(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.e(f, 1.0F);
    }

    public void a(World world, Entity entity) {
        entity.motY = 0.0D;
    }

    public int getDropData(World world, BlockPosition blockposition) {
        return this.getDropData(world.getType(blockposition));
    }

    public Block a(CreativeModeTab creativemodetab) {
        this.creativeTab = creativemodetab;
        return this;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {}

    public void k(World world, BlockPosition blockposition) {}

    public boolean M() {
        return true;
    }

    public boolean a(Explosion explosion) {
        return true;
    }

    public boolean b(Block block) {
        return this == block;
    }

    public static boolean a(Block block, Block block1) {
        return block != null && block1 != null ? (block == block1 ? true : block.b(block1)) : false;
    }

    public boolean isComplexRedstone() {
        return false;
    }

    public int l(World world, BlockPosition blockposition) {
        return 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[0]);
    }

    public BlockStateList O() {
        return this.blockStateList;
    }

    protected final void j(IBlockData iblockdata) {
        this.blockData = iblockdata;
    }

    public final IBlockData getBlockData() {
        return this.blockData;
    }

    public static void R() {
        a(0, Block.a, (new BlockAir()).c("air"));
        a(1, "stone", (new BlockStone()).c(1.5F).b(10.0F).a(Block.i).c("stone"));
        a(2, "grass", (new BlockGrass()).c(0.6F).a(Block.h).c("grass"));
        a(3, "dirt", (new BlockDirt()).c(0.5F).a(Block.g).c("dirt"));
        Block block = (new Block(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("stonebrick").a(CreativeModeTab.b);

        a(4, "cobblestone", block);
        Block block1 = (new BlockWood()).c(2.0F).b(5.0F).a(Block.f).c("wood");

        a(5, "planks", block1);
        a(6, "sapling", (new BlockSapling()).c(0.0F).a(Block.h).c("sapling"));
        a(7, "bedrock", (new Block(Material.STONE)).v().b(6000000.0F).a(Block.i).c("bedrock").J().a(CreativeModeTab.b));
        a(8, "flowing_water", (new BlockFlowing(Material.WATER)).c(100.0F).e(3).c("water").J());
        a(9, "water", (new BlockStationary(Material.WATER)).c(100.0F).e(3).c("water").J());
        a(10, "flowing_lava", (new BlockFlowing(Material.LAVA)).c(100.0F).a(1.0F).c("lava").J());
        a(11, "lava", (new BlockStationary(Material.LAVA)).c(100.0F).a(1.0F).c("lava").J());
        a(12, "sand", (new BlockSand()).c(0.5F).a(Block.m).c("sand"));
        a(13, "gravel", (new BlockGravel()).c(0.6F).a(Block.g).c("gravel"));
        a(14, "gold_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreGold"));
        a(15, "iron_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreIron"));
        a(16, "coal_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreCoal"));
        a(17, "log", (new BlockLog1()).c("log"));
        a(18, "leaves", (new BlockLeaves1()).c("leaves"));
        a(19, "sponge", (new BlockSponge()).c(0.6F).a(Block.h).c("sponge"));
        a(20, "glass", (new BlockGlass(Material.SHATTERABLE, false)).c(0.3F).a(Block.k).c("glass"));
        a(21, "lapis_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreLapis"));
        a(22, "lapis_block", (new BlockOreBlock(MaterialMapColor.H)).c(3.0F).b(5.0F).a(Block.i).c("blockLapis").a(CreativeModeTab.b));
        a(23, "dispenser", (new BlockDispenser()).c(3.5F).a(Block.i).c("dispenser"));
        Block block2 = (new BlockSandStone()).a(Block.i).c(0.8F).c("sandStone");

        a(24, "sandstone", block2);
        a(25, "noteblock", (new BlockNote()).c(0.8F).c("musicBlock"));
        a(26, "bed", (new BlockBed()).a(Block.f).c(0.2F).c("bed").J());
        a(27, "golden_rail", (new BlockPoweredRail()).c(0.7F).a(Block.j).c("goldenRail"));
        a(28, "detector_rail", (new BlockMinecartDetector()).c(0.7F).a(Block.j).c("detectorRail"));
        a(29, "sticky_piston", (new BlockPiston(true)).c("pistonStickyBase"));
        a(30, "web", (new BlockWeb()).e(1).c(4.0F).c("web"));
        a(31, "tallgrass", (new BlockLongGrass()).c(0.0F).a(Block.h).c("tallgrass"));
        a(32, "deadbush", (new BlockDeadBush()).c(0.0F).a(Block.h).c("deadbush"));
        a(33, "piston", (new BlockPiston(false)).c("pistonBase"));
        a(34, "piston_head", new BlockPistonExtension());
        a(35, "wool", (new BlockCloth(Material.CLOTH)).c(0.8F).a(Block.l).c("cloth"));
        a(36, "piston_extension", new BlockPistonMoving());
        a(37, "yellow_flower", (new BlockYellowFlowers()).c(0.0F).a(Block.h).c("flower1"));
        a(38, "red_flower", (new BlockRedFlowers()).c(0.0F).a(Block.h).c("flower2"));
        Block block3 = (new BlockMushroom()).c(0.0F).a(Block.h).a(0.125F).c("mushroom");

        a(39, "brown_mushroom", block3);
        Block block4 = (new BlockMushroom()).c(0.0F).a(Block.h).c("mushroom");

        a(40, "red_mushroom", block4);
        a(41, "gold_block", (new BlockOreBlock(MaterialMapColor.F)).c(3.0F).b(10.0F).a(Block.j).c("blockGold"));
        a(42, "iron_block", (new BlockOreBlock(MaterialMapColor.h)).c(5.0F).b(10.0F).a(Block.j).c("blockIron"));
        a(43, "double_stone_slab", (new BlockDoubleStep()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab"));
        a(44, "stone_slab", (new BlockStep()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab"));
        Block block5 = (new Block(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("brick").a(CreativeModeTab.b);

        a(45, "brick_block", block5);
        a(46, "tnt", (new BlockTNT()).c(0.0F).a(Block.h).c("tnt"));
        a(47, "bookshelf", (new BlockBookshelf()).c(1.5F).a(Block.f).c("bookshelf"));
        a(48, "mossy_cobblestone", (new Block(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("stoneMoss").a(CreativeModeTab.b));
        a(49, "obsidian", (new BlockObsidian()).c(50.0F).b(20.0F).a(Block.i).c("obsidian"));
        a(50, "torch", (new BlockTorch()).c(0.0F).a(0.9375F).a(Block.f).c("torch"));
        a(51, "fire", (new BlockFire()).c(0.0F).a(1.0F).a(Block.l).c("fire").J());
        a(52, "mob_spawner", (new BlockMobSpawner()).c(5.0F).a(Block.j).c("mobSpawner").J());
        a(53, "oak_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.OAK))).c("stairsWood"));
        a(54, "chest", (new BlockChest(0)).c(2.5F).a(Block.f).c("chest"));
        a(55, "redstone_wire", (new BlockRedstoneWire()).c(0.0F).a(Block.e).c("redstoneDust").J());
        a(56, "diamond_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreDiamond"));
        a(57, "diamond_block", (new BlockOreBlock(MaterialMapColor.G)).c(5.0F).b(10.0F).a(Block.j).c("blockDiamond"));
        a(58, "crafting_table", (new BlockWorkbench()).c(2.5F).a(Block.f).c("workbench"));
        a(59, "wheat", (new BlockCrops()).c("crops"));
        Block block6 = (new BlockSoil()).c(0.6F).a(Block.g).c("farmland");

        a(60, "farmland", block6);
        a(61, "furnace", (new BlockFurnace(false)).c(3.5F).a(Block.i).c("furnace").a(CreativeModeTab.c));
        a(62, "lit_furnace", (new BlockFurnace(true)).c(3.5F).a(Block.i).a(0.875F).c("furnace"));
        a(63, "standing_sign", (new BlockFloorSign()).c(1.0F).a(Block.f).c("sign").J());
        a(64, "wooden_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorOak").J());
        a(65, "ladder", (new BlockLadder()).c(0.4F).a(Block.o).c("ladder"));
        a(66, "rail", (new BlockMinecartTrack()).c(0.7F).a(Block.j).c("rail"));
        a(67, "stone_stairs", (new BlockStairs(block.getBlockData())).c("stairsStone"));
        a(68, "wall_sign", (new BlockWallSign()).c(1.0F).a(Block.f).c("sign").J());
        a(69, "lever", (new BlockLever()).c(0.5F).a(Block.f).c("lever"));
        a(70, "stone_pressure_plate", (new BlockPressurePlateBinary(Material.STONE, EnumMobType.MOBS)).c(0.5F).a(Block.i).c("pressurePlateStone"));
        a(71, "iron_door", (new BlockDoor(Material.ORE)).c(5.0F).a(Block.j).c("doorIron").J());
        a(72, "wooden_pressure_plate", (new BlockPressurePlateBinary(Material.WOOD, EnumMobType.EVERYTHING)).c(0.5F).a(Block.f).c("pressurePlateWood"));
        a(73, "redstone_ore", (new BlockRedstoneOre(false)).c(3.0F).b(5.0F).a(Block.i).c("oreRedstone").a(CreativeModeTab.b));
        a(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).a(0.625F).c(3.0F).b(5.0F).a(Block.i).c("oreRedstone"));
        a(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).c(0.0F).a(Block.f).c("notGate"));
        a(76, "redstone_torch", (new BlockRedstoneTorch(true)).c(0.0F).a(0.5F).a(Block.f).c("notGate").a(CreativeModeTab.d));
        a(77, "stone_button", (new BlockStoneButton()).c(0.5F).a(Block.i).c("button"));
        a(78, "snow_layer", (new BlockSnow()).c(0.1F).a(Block.n).c("snow").e(0));
        a(79, "ice", (new BlockIce()).c(0.5F).e(3).a(Block.k).c("ice"));
        a(80, "snow", (new BlockSnowBlock()).c(0.2F).a(Block.n).c("snow"));
        a(81, "cactus", (new BlockCactus()).c(0.4F).a(Block.l).c("cactus"));
        a(82, "clay", (new BlockClay()).c(0.6F).a(Block.g).c("clay"));
        a(83, "reeds", (new BlockReed()).c(0.0F).a(Block.h).c("reeds").J());
        a(84, "jukebox", (new BlockJukeBox()).c(2.0F).b(10.0F).a(Block.i).c("jukebox"));
        a(85, "fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("fence"));
        Block block7 = (new BlockPumpkin()).c(1.0F).a(Block.f).c("pumpkin");

        a(86, "pumpkin", block7);
        a(87, "netherrack", (new BlockBloodStone()).c(0.4F).a(Block.i).c("hellrock"));
        a(88, "soul_sand", (new BlockSlowSand()).c(0.5F).a(Block.m).c("hellsand"));
        a(89, "glowstone", (new BlockLightStone(Material.SHATTERABLE)).c(0.3F).a(Block.k).a(1.0F).c("lightgem"));
        a(90, "portal", (new BlockPortal()).c(-1.0F).a(Block.k).a(0.75F).c("portal"));
        a(91, "lit_pumpkin", (new BlockPumpkin()).c(1.0F).a(Block.f).a(1.0F).c("litpumpkin"));
        a(92, "cake", (new BlockCake()).c(0.5F).a(Block.l).c("cake").J());
        a(93, "unpowered_repeater", (new BlockRepeater(false)).c(0.0F).a(Block.f).c("diode").J());
        a(94, "powered_repeater", (new BlockRepeater(true)).c(0.0F).a(Block.f).c("diode").J());
        a(95, "stained_glass", (new BlockStainedGlass(Material.SHATTERABLE)).c(0.3F).a(Block.k).c("stainedGlass"));
        a(96, "trapdoor", (new BlockTrapdoor(Material.WOOD)).c(3.0F).a(Block.f).c("trapdoor").J());
        a(97, "monster_egg", (new BlockMonsterEggs()).c(0.75F).c("monsterStoneEgg"));
        Block block8 = (new BlockSmoothBrick()).c(1.5F).b(10.0F).a(Block.i).c("stonebricksmooth");

        a(98, "stonebrick", block8);
        a(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.WOOD, block3)).c(0.2F).a(Block.f).c("mushroom"));
        a(100, "red_mushroom_block", (new BlockHugeMushroom(Material.WOOD, block4)).c(0.2F).a(Block.f).c("mushroom"));
        a(101, "iron_bars", (new BlockThin(Material.ORE, true)).c(5.0F).b(10.0F).a(Block.j).c("fenceIron"));
        a(102, "glass_pane", (new BlockThin(Material.SHATTERABLE, false)).c(0.3F).a(Block.k).c("thinGlass"));
        Block block9 = (new BlockMelon()).c(1.0F).a(Block.f).c("melon");

        a(103, "melon_block", block9);
        a(104, "pumpkin_stem", (new BlockStem(block7)).c(0.0F).a(Block.f).c("pumpkinStem"));
        a(105, "melon_stem", (new BlockStem(block9)).c(0.0F).a(Block.f).c("pumpkinStem"));
        a(106, "vine", (new BlockVine()).c(0.2F).a(Block.h).c("vine"));
        a(107, "fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("fenceGate"));
        a(108, "brick_stairs", (new BlockStairs(block5.getBlockData())).c("stairsBrick"));
        a(109, "stone_brick_stairs", (new BlockStairs(block8.getBlockData().set(BlockSmoothBrick.VARIANT, EnumStonebrickType.DEFAULT))).c("stairsStoneBrickSmooth"));
        a(110, "mycelium", (new BlockMycel()).c(0.6F).a(Block.h).c("mycel"));
        a(111, "waterlily", (new BlockWaterLily()).c(0.0F).a(Block.h).c("waterlily"));
        Block block10 = (new BlockNetherbrick()).c(2.0F).b(10.0F).a(Block.i).c("netherBrick").a(CreativeModeTab.b);

        a(112, "nether_brick", block10);
        a(113, "nether_brick_fence", (new BlockFence(Material.STONE)).c(2.0F).b(10.0F).a(Block.i).c("netherFence"));
        a(114, "nether_brick_stairs", (new BlockStairs(block10.getBlockData())).c("stairsNetherBrick"));
        a(115, "nether_wart", (new BlockNetherWart()).c("netherStalk"));
        a(116, "enchanting_table", (new BlockEnchantmentTable()).c(5.0F).b(2000.0F).c("enchantmentTable"));
        a(117, "brewing_stand", (new BlockBrewingStand()).c(0.5F).a(0.125F).c("brewingStand"));
        a(118, "cauldron", (new BlockCauldron()).c(2.0F).c("cauldron"));
        a(119, "end_portal", (new BlockEnderPortal(Material.PORTAL)).c(-1.0F).b(6000000.0F));
        a(120, "end_portal_frame", (new BlockEnderPortalFrame()).a(Block.k).a(0.125F).c(-1.0F).c("endPortalFrame").b(6000000.0F).a(CreativeModeTab.c));
        a(121, "end_stone", (new Block(Material.STONE)).c(3.0F).b(15.0F).a(Block.i).c("whiteStone").a(CreativeModeTab.b));
        a(122, "dragon_egg", (new BlockDragonEgg()).c(3.0F).b(15.0F).a(Block.i).a(0.125F).c("dragonEgg"));
        a(123, "redstone_lamp", (new BlockRedstoneLamp(false)).c(0.3F).a(Block.k).c("redstoneLight").a(CreativeModeTab.d));
        a(124, "lit_redstone_lamp", (new BlockRedstoneLamp(true)).c(0.3F).a(Block.k).c("redstoneLight"));
        a(125, "double_wooden_slab", (new BlockDoubleWoodStep()).c(2.0F).b(5.0F).a(Block.f).c("woodSlab"));
        a(126, "wooden_slab", (new BlockWoodStep()).c(2.0F).b(5.0F).a(Block.f).c("woodSlab"));
        a(127, "cocoa", (new BlockCocoa()).c(0.2F).b(5.0F).a(Block.f).c("cocoa"));
        a(128, "sandstone_stairs", (new BlockStairs(block2.getBlockData().set(BlockSandStone.TYPE, EnumSandstoneVariant.SMOOTH))).c("stairsSandStone"));
        a(129, "emerald_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("oreEmerald"));
        a(130, "ender_chest", (new BlockEnderChest()).c(22.5F).b(10.0F).a(Block.i).c("enderChest").a(0.5F));
        a(131, "tripwire_hook", (new BlockTripwireHook()).c("tripWireSource"));
        a(132, "tripwire", (new BlockTripwire()).c("tripWire"));
        a(133, "emerald_block", (new BlockOreBlock(MaterialMapColor.I)).c(5.0F).b(10.0F).a(Block.j).c("blockEmerald"));
        a(134, "spruce_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.SPRUCE))).c("stairsWoodSpruce"));
        a(135, "birch_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.BIRCH))).c("stairsWoodBirch"));
        a(136, "jungle_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.JUNGLE))).c("stairsWoodJungle"));
        a(137, "command_block", (new BlockCommand()).v().b(6000000.0F).c("commandBlock"));
        a(138, "beacon", (new BlockBeacon()).c("beacon").a(1.0F));
        a(139, "cobblestone_wall", (new BlockCobbleWall(block)).c("cobbleWall"));
        a(140, "flower_pot", (new BlockFlowerPot()).c(0.0F).a(Block.e).c("flowerPot"));
        a(141, "carrots", (new BlockCarrots()).c("carrots"));
        a(142, "potatoes", (new BlockPotatoes()).c("potatoes"));
        a(143, "wooden_button", (new BlockWoodButton()).c(0.5F).a(Block.f).c("button"));
        a(144, "skull", (new BlockSkull()).c(1.0F).a(Block.i).c("skull"));
        a(145, "anvil", (new BlockAnvil()).c(5.0F).a(Block.p).b(2000.0F).c("anvil"));
        a(146, "trapped_chest", (new BlockChest(1)).c(2.5F).a(Block.f).c("chestTrap"));
        a(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted("gold_block", Material.ORE, 15)).c(0.5F).a(Block.f).c("weightedPlate_light"));
        a(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted("iron_block", Material.ORE, 150)).c(0.5F).a(Block.f).c("weightedPlate_heavy"));
        a(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).c(0.0F).a(Block.f).c("comparator").J());
        a(150, "powered_comparator", (new BlockRedstoneComparator(true)).c(0.0F).a(0.625F).a(Block.f).c("comparator").J());
        a(151, "daylight_detector", new BlockDaylightDetector(false));
        a(152, "redstone_block", (new BlockRedstone(MaterialMapColor.f)).c(5.0F).b(10.0F).a(Block.j).c("blockRedstone"));
        a(153, "quartz_ore", (new BlockOre()).c(3.0F).b(5.0F).a(Block.i).c("netherquartz"));
        a(154, "hopper", (new BlockHopper()).c(3.0F).b(8.0F).a(Block.j).c("hopper"));
        Block block11 = (new BlockQuartz()).a(Block.i).c(0.8F).c("quartzBlock");

        a(155, "quartz_block", block11);
        a(156, "quartz_stairs", (new BlockStairs(block11.getBlockData().set(BlockQuartz.VARIANT, EnumQuartzVariant.DEFAULT))).c("stairsQuartz"));
        a(157, "activator_rail", (new BlockPoweredRail()).c(0.7F).a(Block.j).c("activatorRail"));
        a(158, "dropper", (new BlockDropper()).c(3.5F).a(Block.i).c("dropper"));
        a(159, "stained_hardened_clay", (new BlockCloth(Material.STONE)).c(1.25F).b(7.0F).a(Block.i).c("clayHardenedStained"));
        a(160, "stained_glass_pane", (new BlockStainedGlassPane()).c(0.3F).a(Block.k).c("thinStainedGlass"));
        a(161, "leaves2", (new BlockLeaves2()).c("leaves"));
        a(162, "log2", (new BlockLog2()).c("log"));
        a(163, "acacia_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.ACACIA))).c("stairsWoodAcacia"));
        a(164, "dark_oak_stairs", (new BlockStairs(block1.getBlockData().set(BlockWood.VARIANT, EnumLogVariant.DARK_OAK))).c("stairsWoodDarkOak"));
        a(165, "slime", (new BlockSlime()).c("slime").a(Block.q));
        a(166, "barrier", (new BlockBarrier()).c("barrier"));
        a(167, "iron_trapdoor", (new BlockTrapdoor(Material.ORE)).c(5.0F).a(Block.j).c("ironTrapdoor").J());
        a(168, "prismarine", (new BlockPrismarine()).c(1.5F).b(10.0F).a(Block.i).c("prismarine"));
        a(169, "sea_lantern", (new BlockSeaLantern(Material.SHATTERABLE)).c(0.3F).a(Block.k).a(1.0F).c("seaLantern"));
        a(170, "hay_block", (new BlockHay()).c(0.5F).a(Block.h).c("hayBlock").a(CreativeModeTab.b));
        a(171, "carpet", (new BlockCarpet()).c(0.1F).a(Block.l).c("woolCarpet").e(0));
        a(172, "hardened_clay", (new BlockHardenedClay()).c(1.25F).b(7.0F).a(Block.i).c("clayHardened"));
        a(173, "coal_block", (new Block(Material.STONE)).c(5.0F).b(10.0F).a(Block.i).c("blockCoal").a(CreativeModeTab.b));
        a(174, "packed_ice", (new BlockPackedIce()).c(0.5F).a(Block.k).c("icePacked"));
        a(175, "double_plant", new BlockTallPlant());
        a(176, "standing_banner", (new BlockStandingBanner()).c(1.0F).a(Block.f).c("banner").J());
        a(177, "wall_banner", (new BlockWallBanner()).c(1.0F).a(Block.f).c("banner").J());
        a(178, "daylight_detector_inverted", new BlockDaylightDetector(true));
        Block block12 = (new BlockRedSandstone()).a(Block.i).c(0.8F).c("redSandStone");

        a(179, "red_sandstone", block12);
        a(180, "red_sandstone_stairs", (new BlockStairs(block12.getBlockData().set(BlockRedSandstone.TYPE, EnumRedSandstoneVariant.SMOOTH))).c("stairsRedSandStone"));
        a(181, "double_stone_slab2", (new BlockDoubleStoneStep2()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab2"));
        a(182, "stone_slab2", (new BlockStoneStep2()).c(2.0F).b(10.0F).a(Block.i).c("stoneSlab2"));
        a(183, "spruce_fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("spruceFenceGate"));
        a(184, "birch_fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("birchFenceGate"));
        a(185, "jungle_fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("jungleFenceGate"));
        a(186, "dark_oak_fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("darkOakFenceGate"));
        a(187, "acacia_fence_gate", (new BlockFenceGate()).c(2.0F).b(5.0F).a(Block.f).c("acaciaFenceGate"));
        a(188, "spruce_fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("spruceFence"));
        a(189, "birch_fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("birchFence"));
        a(190, "jungle_fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("jungleFence"));
        a(191, "dark_oak_fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("darkOakFence"));
        a(192, "acacia_fence", (new BlockFence(Material.WOOD)).c(2.0F).b(5.0F).a(Block.f).c("acaciaFence"));
        a(193, "spruce_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorSpruce").J());
        a(194, "birch_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorBirch").J());
        a(195, "jungle_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorJungle").J());
        a(196, "acacia_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorAcacia").J());
        a(197, "dark_oak_door", (new BlockDoor(Material.WOOD)).c(3.0F).a(Block.f).c("doorDarkOak").J());
        
         // Alkazia - 
        a(200, "bauxite_ore", (new BlockOre()).c(3.0F).b(5.0F).a(i).c("oreBauxite"));
        a(201, "granite_ore", (new BlockOre()).c(3.0F).b(5.0F).a(i).J().c("oreGranite"));
        a(202, "opale_ore", (new BlockOre()).c(3.0F).b(5.0F).a(i).J().c("oreOpale"));
        a(203, "meteor_ore", (new BlockOre()).c(3.0F).b(5.0F).a(i).J().c("oreMeteor"));
       
        a(204, "bauxite_block", (new BlockOreBlock(MaterialMapColor.G)).c(5.0F).b(10.0F).J().a(j).c("blockBauxite"));
        a(205, "granite_block", (new BlockOreBlock(MaterialMapColor.G)).c(5.0F).b(10.0F).J().a(j).c("blockGarnite"));
        a(206, "opale_block", (new BlockOreBlock(MaterialMapColor.G)).c(5.0F).b(10.0F).J().a(j).c("blockOpale"));
        a(207, "meteor_block", (new BlockOreBlock(MaterialMapColor.G)).c(5.0F).b(10.0F).J().a(j).c("blockMeteor"));
        
        a(208, "pousse", (new Block(Material.HEAVY)).c(5.0F).a(i).b(10.0F).c("pousse").a(CreativeModeTab.b)); 
        a(209, "iron_chest", (new BlockIronChest()).c(2.5F).a(j).c("iron_chest").a(CreativeModeTab.d));
        a(210, "meteorite", (new BlockMeteorite()).c(50.0F).b(23.0F).a(i).c("meteor_brut"));
        a(211, "invisible", (new BlockInvisible(Material.SHATTERABLE, false)).a(10.0F).a(k).a(1.0F).c("invisible"));
        a(213, "lumiere", (new BlockAir()).c(10.0F).a(1.0F).c("lumiere").a(CreativeModeTab.b));

        a(220, "iron_ladder", (new BlockLadder()).c(0.4F).a(o).c("iron_ladder"));
        a(221, "blockPowder", (new BlockOre()).c(20.0F).b(5.0F).a(i).c("blockPowder"));
        
        Block.REGISTRY.a();
        Iterator iterator = Block.REGISTRY.iterator();

        Block block13;

        while (iterator.hasNext()) {
            block13 = (Block) iterator.next();
            if (block13.material == Material.AIR) {
                block13.v = false;
            } else {
                boolean flag = false;
                boolean flag1 = block13 instanceof BlockStairs;
                boolean flag2 = block13 instanceof BlockStepAbstract;
                boolean flag3 = block13 == block6;
                boolean flag4 = block13.t;
                boolean flag5 = block13.s == 0;

                if (flag1 || flag2 || flag3 || flag4 || flag5) {
                    flag = true;
                }

                block13.v = flag;
            }
        }

        iterator = Block.REGISTRY.iterator();

        while (iterator.hasNext()) {
            block13 = (Block) iterator.next();
            Iterator iterator1 = block13.O().a().iterator();

            while (iterator1.hasNext()) {
                IBlockData iblockdata = (IBlockData) iterator1.next();
                int i = Block.REGISTRY.b(block13) << 4 | block13.toLegacyData(iblockdata);

                Block.d.a(iblockdata, i);
            }
        }

    }

    private static void a(int i, MinecraftKey minecraftkey, Block block) {
        Block.REGISTRY.a(i, minecraftkey, block);
    }

    private static void a(int i, String s, Block block) {
        a(i, new MinecraftKey(s), block);
    }

    // CraftBukkit start
    public int getExpDrop(World world, IBlockData data, int enchantmentLevel) {
        return 0;
    }
    // CraftBukkit end

    // Spigot start
    public static float range(float min, float value, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    // Spigot end
}
