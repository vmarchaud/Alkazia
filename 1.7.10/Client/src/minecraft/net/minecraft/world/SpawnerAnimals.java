package net.minecraft.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals
{
    /** The 17x17 area around the player where mobs can spawn */
    private HashMap eligibleChunksForSpawning = new HashMap();
    private static final String __OBFID = "CL_00000152";

    protected static ChunkPosition func_151350_a(World p_151350_0_, int p_151350_1_, int p_151350_2_)
    {
        Chunk var3 = p_151350_0_.getChunkFromChunkCoords(p_151350_1_, p_151350_2_);
        int var4 = p_151350_1_ * 16 + p_151350_0_.rand.nextInt(16);
        int var5 = p_151350_2_ * 16 + p_151350_0_.rand.nextInt(16);
        int var6 = p_151350_0_.rand.nextInt(var3 == null ? p_151350_0_.getActualHeight() : var3.getTopFilledSegment() + 16 - 1);
        return new ChunkPosition(var4, var6, var5);
    }

    /**
     * adds all chunks within the spawn radius of the players to eligibleChunksForSpawning. pars: the world,
     * hostileCreatures, passiveCreatures. returns number of eligible chunks.
     */
    public int findChunksForSpawning(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_)
    {
        if (!p_77192_2_ && !p_77192_3_)
        {
            return 0;
        }
        else
        {
            this.eligibleChunksForSpawning.clear();
            int var5;
            int var8;

            for (var5 = 0; var5 < p_77192_1_.playerEntities.size(); ++var5)
            {
                EntityPlayer var6 = (EntityPlayer)p_77192_1_.playerEntities.get(var5);
                int var7 = MathHelper.floor_double(var6.posX / 16.0D);
                var8 = MathHelper.floor_double(var6.posZ / 16.0D);
                byte var9 = 8;

                for (int var10 = -var9; var10 <= var9; ++var10)
                {
                    for (int var11 = -var9; var11 <= var9; ++var11)
                    {
                        boolean var12 = var10 == -var9 || var10 == var9 || var11 == -var9 || var11 == var9;
                        ChunkCoordIntPair var13 = new ChunkCoordIntPair(var10 + var7, var11 + var8);

                        if (!var12)
                        {
                            this.eligibleChunksForSpawning.put(var13, Boolean.valueOf(false));
                        }
                        else if (!this.eligibleChunksForSpawning.containsKey(var13))
                        {
                            this.eligibleChunksForSpawning.put(var13, Boolean.valueOf(true));
                        }
                    }
                }
            }

            var5 = 0;
            ChunkCoordinates var34 = p_77192_1_.getSpawnPoint();
            EnumCreatureType[] var35 = EnumCreatureType.values();
            var8 = var35.length;

            for (int var36 = 0; var36 < var8; ++var36)
            {
                EnumCreatureType var37 = var35[var36];

                if ((!var37.getPeacefulCreature() || p_77192_3_) && (var37.getPeacefulCreature() || p_77192_2_) && (!var37.getAnimal() || p_77192_4_) && p_77192_1_.countEntities(var37.getCreatureClass()) <= var37.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256)
                {
                    Iterator var38 = this.eligibleChunksForSpawning.keySet().iterator();
                    label110:

                    while (var38.hasNext())
                    {
                        ChunkCoordIntPair var39 = (ChunkCoordIntPair)var38.next();

                        if (!((Boolean)this.eligibleChunksForSpawning.get(var39)).booleanValue())
                        {
                            ChunkPosition var40 = func_151350_a(p_77192_1_, var39.chunkXPos, var39.chunkZPos);
                            int var14 = var40.field_151329_a;
                            int var15 = var40.field_151327_b;
                            int var16 = var40.field_151328_c;

                            if (!p_77192_1_.getBlock(var14, var15, var16).isNormalCube() && p_77192_1_.getBlock(var14, var15, var16).getMaterial() == var37.getCreatureMaterial())
                            {
                                int var17 = 0;
                                int var18 = 0;

                                while (var18 < 3)
                                {
                                    int var19 = var14;
                                    int var20 = var15;
                                    int var21 = var16;
                                    byte var22 = 6;
                                    BiomeGenBase.SpawnListEntry var23 = null;
                                    IEntityLivingData var24 = null;
                                    int var25 = 0;

                                    while (true)
                                    {
                                        if (var25 < 4)
                                        {
                                            label103:
                                            {
                                                var19 += p_77192_1_.rand.nextInt(var22) - p_77192_1_.rand.nextInt(var22);
                                                var20 += p_77192_1_.rand.nextInt(1) - p_77192_1_.rand.nextInt(1);
                                                var21 += p_77192_1_.rand.nextInt(var22) - p_77192_1_.rand.nextInt(var22);

                                                if (canCreatureTypeSpawnAtLocation(var37, p_77192_1_, var19, var20, var21))
                                                {
                                                    float var26 = (float)var19 + 0.5F;
                                                    float var27 = (float)var20;
                                                    float var28 = (float)var21 + 0.5F;

                                                    if (p_77192_1_.getClosestPlayer((double)var26, (double)var27, (double)var28, 24.0D) == null)
                                                    {
                                                        float var29 = var26 - (float)var34.posX;
                                                        float var30 = var27 - (float)var34.posY;
                                                        float var31 = var28 - (float)var34.posZ;
                                                        float var32 = var29 * var29 + var30 * var30 + var31 * var31;

                                                        if (var32 >= 576.0F)
                                                        {
                                                            if (var23 == null)
                                                            {
                                                                var23 = p_77192_1_.spawnRandomCreature(var37, var19, var20, var21);

                                                                if (var23 == null)
                                                                {
                                                                    break label103;
                                                                }
                                                            }

                                                            EntityLiving var41;

                                                            try
                                                            {
                                                                var41 = (EntityLiving)var23.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {p_77192_1_});
                                                            }
                                                            catch (Exception var33)
                                                            {
                                                                var33.printStackTrace();
                                                                return var5;
                                                            }

                                                            var41.setLocationAndAngles((double)var26, (double)var27, (double)var28, p_77192_1_.rand.nextFloat() * 360.0F, 0.0F);

                                                            if (var41.getCanSpawnHere())
                                                            {
                                                                ++var17;
                                                                p_77192_1_.spawnEntityInWorld(var41);
                                                                var24 = var41.onSpawnWithEgg(var24);

                                                                if (var17 >= var41.getMaxSpawnedInChunk())
                                                                {
                                                                    continue label110;
                                                                }
                                                            }

                                                            var5 += var17;
                                                        }
                                                    }
                                                }

                                                ++var25;
                                                continue;
                                            }
                                        }

                                        ++var18;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return var5;
        }
    }

    /**
     * Returns whether or not the specified creature type can spawn at the specified location.
     */
    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType p_77190_0_, World p_77190_1_, int p_77190_2_, int p_77190_3_, int p_77190_4_)
    {
        if (p_77190_0_.getCreatureMaterial() == Material.water)
        {
            return p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_).getMaterial().isLiquid() && p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_).getMaterial().isLiquid() && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_).isNormalCube();
        }
        else if (!World.doesBlockHaveSolidTopSurface(p_77190_1_, p_77190_2_, p_77190_3_ - 1, p_77190_4_))
        {
            return false;
        }
        else
        {
            Block var5 = p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_);
            return var5 != Blocks.bedrock && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_).isNormalCube() && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_, p_77190_4_).getMaterial().isLiquid() && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1, p_77190_4_).isNormalCube();
        }
    }

    /**
     * Called during chunk generation to spawn initial creatures.
     */
    public static void performWorldGenSpawning(World p_77191_0_, BiomeGenBase p_77191_1_, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random p_77191_6_)
    {
        List var7 = p_77191_1_.getSpawnableList(EnumCreatureType.creature);

        if (!var7.isEmpty())
        {
            while (p_77191_6_.nextFloat() < p_77191_1_.getSpawningChance())
            {
                BiomeGenBase.SpawnListEntry var8 = (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(p_77191_0_.rand, var7);
                IEntityLivingData var9 = null;
                int var10 = var8.minGroupCount + p_77191_6_.nextInt(1 + var8.maxGroupCount - var8.minGroupCount);
                int var11 = p_77191_2_ + p_77191_6_.nextInt(p_77191_4_);
                int var12 = p_77191_3_ + p_77191_6_.nextInt(p_77191_5_);
                int var13 = var11;
                int var14 = var12;

                for (int var15 = 0; var15 < var10; ++var15)
                {
                    boolean var16 = false;

                    for (int var17 = 0; !var16 && var17 < 4; ++var17)
                    {
                        int var18 = p_77191_0_.getTopSolidOrLiquidBlock(var11, var12);

                        if (canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, p_77191_0_, var11, var18, var12))
                        {
                            float var19 = (float)var11 + 0.5F;
                            float var20 = (float)var18;
                            float var21 = (float)var12 + 0.5F;
                            EntityLiving var22;

                            try
                            {
                                var22 = (EntityLiving)var8.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {p_77191_0_});
                            }
                            catch (Exception var24)
                            {
                                var24.printStackTrace();
                                continue;
                            }

                            var22.setLocationAndAngles((double)var19, (double)var20, (double)var21, p_77191_6_.nextFloat() * 360.0F, 0.0F);
                            p_77191_0_.spawnEntityInWorld(var22);
                            var9 = var22.onSpawnWithEgg(var9);
                            var16 = true;
                        }

                        var11 += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);

                        for (var12 += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5); var11 < p_77191_2_ || var11 >= p_77191_2_ + p_77191_4_ || var12 < p_77191_3_ || var12 >= p_77191_3_ + p_77191_4_; var12 = var14 + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5))
                        {
                            var11 = var13 + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);
                        }
                    }
                }
            }
        }
    }
}
