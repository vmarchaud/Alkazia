package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public class RenderGlobal implements IWorldAccess
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    public List tileEntities = new ArrayList();
    private WorldClient theWorld;

    /** The RenderEngine instance used by RenderGlobal */
    private final TextureManager renderEngine;
    private List worldRenderersToUpdate = new ArrayList();
    private WorldRenderer[] sortedWorldRenderers;
    private WorldRenderer[] worldRenderers;
    private int renderChunksWide;
    private int renderChunksTall;
    private int renderChunksDeep;

    /** OpenGL render lists base */
    private int glRenderListBase;

    /** A reference to the Minecraft object. */
    private Minecraft mc;
    private RenderBlocks renderBlocksRg;

    /** OpenGL occlusion query base */
    private IntBuffer glOcclusionQueryBase;

    /** Is occlusion testing enabled */
    private boolean occlusionEnabled;

    /**
     * counts the cloud render updates. Used with mod to stagger some updates
     */
    private int cloudTickCounter;

    /** The star GL Call list */
    private int starGLCallList;

    /** OpenGL sky list */
    private int glSkyList;

    /** OpenGL sky list 2 */
    private int glSkyList2;

    /** Minimum block X */
    private int minBlockX;

    /** Minimum block Y */
    private int minBlockY;

    /** Minimum block Z */
    private int minBlockZ;

    /** Maximum block X */
    private int maxBlockX;

    /** Maximum block Y */
    private int maxBlockY;

    /** Maximum block Z */
    private int maxBlockZ;

    /**
     * Stores blocks currently being broken. Key is entity ID of the thing doing the breaking. Value is a
     * DestroyBlockProgress
     */
    private final Map damagedBlocks = new HashMap();
    private final Map mapSoundPositions = Maps.newHashMap();
    private IIcon[] destroyBlockIcons;
    private boolean displayListEntitiesDirty;
    private int displayListEntities;
    private int renderDistanceChunks = -1;

    /** Render entities startup counter (init value=2) */
    private int renderEntitiesStartupCounter = 2;

    /** Count entities total */
    private int countEntitiesTotal;

    /** Count entities rendered */
    private int countEntitiesRendered;

    /** Count entities hidden */
    private int countEntitiesHidden;

    /** Occlusion query result */
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);

    /** How many renderers are loaded this frame that try to be rendered */
    private int renderersLoaded;

    /** How many renderers are being clipped by the frustrum this frame */
    private int renderersBeingClipped;

    /** How many renderers are being occluded this frame */
    private int renderersBeingOccluded;

    /** How many renderers are actually being rendered this frame */
    private int renderersBeingRendered;

    /**
     * How many renderers are skipping rendering due to not having a render pass this frame
     */
    private int renderersSkippingRenderPass;

    /** Dummy render int */
    private int dummyRenderInt;

    /** World renderers check index */
    private int worldRenderersCheckIndex;

    /** List of OpenGL lists for the current render pass */
    private List glRenderLists = new ArrayList();

    /** All render lists (fixed length 4) */
    private RenderList[] allRenderLists = new RenderList[] {new RenderList(), new RenderList(), new RenderList(), new RenderList()};

    /**
     * Previous x position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortX = -9999.0D;

    /**
     * Previous y position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortY = -9999.0D;

    /**
     * Previous Z position when the renderers were sorted. (Once the distance moves more than 4 units they will be
     * resorted)
     */
    double prevSortZ = -9999.0D;
    double prevRenderSortX = -9999.0D;
    double prevRenderSortY = -9999.0D;
    double prevRenderSortZ = -9999.0D;
    int prevChunkSortX = -999;
    int prevChunkSortY = -999;
    int prevChunkSortZ = -999;

    /**
     * The offset used to determine if a renderer is one of the sixteenth that are being updated this frame
     */
    int frustumCheckOffset;
    private static final String __OBFID = "CL_00000954";

    public RenderGlobal(Minecraft p_i1249_1_)
    {
        this.mc = p_i1249_1_;
        this.renderEngine = p_i1249_1_.getTextureManager();
        byte var2 = 34;
        byte var3 = 16;
        this.glRenderListBase = GLAllocation.generateDisplayLists(var2 * var2 * var3 * 3);
        this.displayListEntitiesDirty = false;
        this.displayListEntities = GLAllocation.generateDisplayLists(1);
        this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();

        if (this.occlusionEnabled)
        {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var2 * var2 * var3);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(var2 * var2 * var3);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator var4 = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        byte var6 = 64;
        int var7 = 256 / var6 + 2;
        float var5 = 16.0F;
        int var8;
        int var9;

        for (var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6)
        {
            for (var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6)
            {
                var4.startDrawingQuads();
                var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
                var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
                var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
                var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
                var4.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        var5 = -16.0F;
        var4.startDrawingQuads();

        for (var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6)
        {
            for (var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6)
            {
                var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + 0));
                var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + 0));
                var4.addVertex((double)(var8 + 0), (double)var5, (double)(var9 + var6));
                var4.addVertex((double)(var8 + var6), (double)var5, (double)(var9 + var6));
            }
        }

        var4.draw();
        GL11.glEndList();
    }

    private void renderStars()
    {
        Random var1 = new Random(10842L);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int var3 = 0; var3 < 1500; ++var3)
        {
            double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
            double var10 = (double)(0.15F + var1.nextFloat() * 0.1F);
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D)
            {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                double var14 = var4 * 100.0D;
                double var16 = var6 * 100.0D;
                double var18 = var8 * 100.0D;
                double var20 = Math.atan2(var4, var8);
                double var22 = Math.sin(var20);
                double var24 = Math.cos(var20);
                double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                double var28 = Math.sin(var26);
                double var30 = Math.cos(var26);
                double var32 = var1.nextDouble() * Math.PI * 2.0D;
                double var34 = Math.sin(var32);
                double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38)
                {
                    double var39 = 0.0D;
                    double var41 = (double)((var38 & 2) - 1) * var10;
                    double var43 = (double)((var38 + 1 & 2) - 1) * var10;
                    double var47 = var41 * var36 - var43 * var34;
                    double var49 = var43 * var36 + var41 * var34;
                    double var53 = var47 * var28 + var39 * var30;
                    double var55 = var39 * var28 - var47 * var30;
                    double var57 = var55 * var22 - var49 * var24;
                    double var61 = var49 * var22 + var55 * var24;
                    var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
                }
            }
        }

        var2.draw();
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(WorldClient p_72732_1_)
    {
        if (this.theWorld != null)
        {
            this.theWorld.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0D;
        this.prevSortY = -9999.0D;
        this.prevSortZ = -9999.0D;
        this.prevRenderSortX = -9999.0D;
        this.prevRenderSortY = -9999.0D;
        this.prevRenderSortZ = -9999.0D;
        this.prevChunkSortX = -9999;
        this.prevChunkSortY = -9999;
        this.prevChunkSortZ = -9999;
        RenderManager.instance.set(p_72732_1_);
        this.theWorld = p_72732_1_;
        this.renderBlocksRg = new RenderBlocks(p_72732_1_);

        if (p_72732_1_ != null)
        {
            p_72732_1_.addWorldAccess(this);
            this.loadRenderers();
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.theWorld != null)
        {
            Blocks.leaves.func_150122_b(this.mc.gameSettings.fancyGraphics);
            Blocks.leaves2.func_150122_b(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            int var1;

            if (this.worldRenderers != null)
            {
                for (var1 = 0; var1 < this.worldRenderers.length; ++var1)
                {
                    this.worldRenderers[var1].stopRendering();
                }
            }

            var1 = this.renderDistanceChunks * 2 + 1;
            this.renderChunksWide = var1;
            this.renderChunksTall = 16;
            this.renderChunksDeep = var1;
            this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            int var2 = 0;
            int var3 = 0;
            this.minBlockX = 0;
            this.minBlockY = 0;
            this.minBlockZ = 0;
            this.maxBlockX = this.renderChunksWide;
            this.maxBlockY = this.renderChunksTall;
            this.maxBlockZ = this.renderChunksDeep;
            int var4;

            for (var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4)
            {
                ((WorldRenderer)this.worldRenderersToUpdate.get(var4)).needsUpdate = false;
            }

            this.worldRenderersToUpdate.clear();
            this.tileEntities.clear();
            this.onStaticEntitiesChanged();

            for (var4 = 0; var4 < this.renderChunksWide; ++var4)
            {
                for (int var5 = 0; var5 < this.renderChunksTall; ++var5)
                {
                    for (int var6 = 0; var6 < this.renderChunksDeep; ++var6)
                    {
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = new WorldRenderer(this.theWorld, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, this.glRenderListBase + var2);

                        if (this.occlusionEnabled)
                        {
                            this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
                        }

                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isWaitingOnOcclusionQuery = false;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isVisible = true;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].isInFrustum = true;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].chunkIndex = var3++;
                        this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4].markDirty();
                        this.sortedWorldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4] = this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4];
                        this.worldRenderersToUpdate.add(this.worldRenderers[(var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4]);
                        var2 += 3;
                    }
                }
            }

            if (this.theWorld != null)
            {
                EntityLivingBase var7 = this.mc.renderViewEntity;

                if (var7 != null)
                {
                    this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
                    Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    public void renderEntities(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float p_147589_3_)
    {
        if (this.renderEntitiesStartupCounter > 0)
        {
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double var4 = p_147589_1_.prevPosX + (p_147589_1_.posX - p_147589_1_.prevPosX) * (double)p_147589_3_;
            double var6 = p_147589_1_.prevPosY + (p_147589_1_.posY - p_147589_1_.prevPosY) * (double)p_147589_3_;
            double var8 = p_147589_1_.prevPosZ + (p_147589_1_.posZ - p_147589_1_.prevPosZ) * (double)p_147589_3_;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.func_147542_a(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, p_147589_3_);
            RenderManager.instance.func_147938_a(this.theWorld, this.mc.getTextureManager(), this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, p_147589_3_);
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            EntityLivingBase var10 = this.mc.renderViewEntity;
            double var11 = var10.lastTickPosX + (var10.posX - var10.lastTickPosX) * (double)p_147589_3_;
            double var13 = var10.lastTickPosY + (var10.posY - var10.lastTickPosY) * (double)p_147589_3_;
            double var15 = var10.lastTickPosZ + (var10.posZ - var10.lastTickPosZ) * (double)p_147589_3_;
            TileEntityRendererDispatcher.staticPlayerX = var11;
            TileEntityRendererDispatcher.staticPlayerY = var13;
            TileEntityRendererDispatcher.staticPlayerZ = var15;
            this.theWorld.theProfiler.endStartSection("staticentities");

            if (this.displayListEntitiesDirty)
            {
                RenderManager.renderPosX = 0.0D;
                RenderManager.renderPosY = 0.0D;
                RenderManager.renderPosZ = 0.0D;
                this.rebuildDisplayListEntities();
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glTranslated(-var11, -var13, -var15);
            GL11.glCallList(this.displayListEntities);
            GL11.glPopMatrix();
            RenderManager.renderPosX = var11;
            RenderManager.renderPosY = var13;
            RenderManager.renderPosZ = var15;
            this.mc.entityRenderer.enableLightmap((double)p_147589_3_);
            this.theWorld.theProfiler.endStartSection("global");
            List var17 = this.theWorld.getLoadedEntityList();
            this.countEntitiesTotal = var17.size();
            int var18;
            Entity var19;

            for (var18 = 0; var18 < this.theWorld.weatherEffects.size(); ++var18)
            {
                var19 = (Entity)this.theWorld.weatherEffects.get(var18);
                ++this.countEntitiesRendered;

                if (var19.isInRangeToRender3d(var4, var6, var8))
                {
                    RenderManager.instance.func_147937_a(var19, p_147589_3_);
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");

            for (var18 = 0; var18 < var17.size(); ++var18)
            {
                var19 = (Entity)var17.get(var18);
                boolean var20 = var19.isInRangeToRender3d(var4, var6, var8) && (var19.ignoreFrustumCheck || p_147589_2_.isBoundingBoxInFrustum(var19.boundingBox) || var19.riddenByEntity == this.mc.thePlayer);

                if (!var20 && var19 instanceof EntityLiving)
                {
                    EntityLiving var21 = (EntityLiving)var19;

                    if (var21.getLeashed() && var21.getLeashedToEntity() != null)
                    {
                        Entity var22 = var21.getLeashedToEntity();
                        var20 = p_147589_2_.isBoundingBoxInFrustum(var22.boundingBox);
                    }
                }

                if (var20 && (var19 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping()) && this.theWorld.blockExists(MathHelper.floor_double(var19.posX), 0, MathHelper.floor_double(var19.posZ)))
                {
                    ++this.countEntitiesRendered;
                    RenderManager.instance.func_147937_a(var19, p_147589_3_);
                }
            }

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();

            for (var18 = 0; var18 < this.tileEntities.size(); ++var18)
            {
                TileEntityRendererDispatcher.instance.func_147544_a((TileEntity)this.tileEntities.get(var18), p_147589_3_);
            }

            this.mc.entityRenderer.disableLightmap((double)p_147589_3_);
            this.theWorld.theProfiler.endSection();
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    public void onStaticEntitiesChanged()
    {
        this.displayListEntitiesDirty = true;
    }

    public void rebuildDisplayListEntities()
    {
        this.theWorld.theProfiler.startSection("staticentityrebuild");
        GL11.glPushMatrix();
        GL11.glNewList(this.displayListEntities, GL11.GL_COMPILE);
        List var1 = this.theWorld.getLoadedEntityList();
        this.displayListEntitiesDirty = false;

        for (int var2 = 0; var2 < var1.size(); ++var2)
        {
            Entity var3 = (Entity)var1.get(var2);

            if (RenderManager.instance.getEntityRenderObject(var3).func_147905_a())
            {
                this.displayListEntitiesDirty = this.displayListEntitiesDirty || !RenderManager.instance.func_147936_a(var3, 0.0F, true);
            }
        }

        GL11.glEndList();
        GL11.glPopMatrix();
        this.theWorld.theProfiler.endSection();
    }

    /**
     * Goes through all the renderers setting new positions on them and those that have their position changed are
     * adding to be updated
     */
    private void markRenderersForNewPosition(int p_72722_1_, int p_72722_2_, int p_72722_3_)
    {
        p_72722_1_ -= 8;
        p_72722_2_ -= 8;
        p_72722_3_ -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int var4 = this.renderChunksWide * 16;
        int var5 = var4 / 2;

        for (int var6 = 0; var6 < this.renderChunksWide; ++var6)
        {
            int var7 = var6 * 16;
            int var8 = var7 + var5 - p_72722_1_;

            if (var8 < 0)
            {
                var8 -= var4 - 1;
            }

            var8 /= var4;
            var7 -= var8 * var4;

            if (var7 < this.minBlockX)
            {
                this.minBlockX = var7;
            }

            if (var7 > this.maxBlockX)
            {
                this.maxBlockX = var7;
            }

            for (int var9 = 0; var9 < this.renderChunksDeep; ++var9)
            {
                int var10 = var9 * 16;
                int var11 = var10 + var5 - p_72722_3_;

                if (var11 < 0)
                {
                    var11 -= var4 - 1;
                }

                var11 /= var4;
                var10 -= var11 * var4;

                if (var10 < this.minBlockZ)
                {
                    this.minBlockZ = var10;
                }

                if (var10 > this.maxBlockZ)
                {
                    this.maxBlockZ = var10;
                }

                for (int var12 = 0; var12 < this.renderChunksTall; ++var12)
                {
                    int var13 = var12 * 16;

                    if (var13 < this.minBlockY)
                    {
                        this.minBlockY = var13;
                    }

                    if (var13 > this.maxBlockY)
                    {
                        this.maxBlockY = var13;
                    }

                    WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
                    boolean var15 = var14.needsUpdate;
                    var14.setPosition(var7, var13, var10);

                    if (!var15 && var14.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var14);
                    }
                }
            }
        }
    }

    /**
     * Sorts all renderers based on the passed in entity. Args: entityLiving, renderPass, partialTickTime
     */
    public int sortAndRender(EntityLivingBase p_72719_1_, int p_72719_2_, double p_72719_3_)
    {
        this.theWorld.theProfiler.startSection("sortchunks");

        for (int var5 = 0; var5 < 10; ++var5)
        {
            this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
            WorldRenderer var6 = this.worldRenderers[this.worldRenderersCheckIndex];

            if (var6.needsUpdate && !this.worldRenderersToUpdate.contains(var6))
            {
                this.worldRenderersToUpdate.add(var6);
            }
        }

        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        if (p_72719_2_ == 0)
        {
            this.renderersLoaded = 0;
            this.dummyRenderInt = 0;
            this.renderersBeingClipped = 0;
            this.renderersBeingOccluded = 0;
            this.renderersBeingRendered = 0;
            this.renderersSkippingRenderPass = 0;
        }

        double var39 = p_72719_1_.lastTickPosX + (p_72719_1_.posX - p_72719_1_.lastTickPosX) * p_72719_3_;
        double var7 = p_72719_1_.lastTickPosY + (p_72719_1_.posY - p_72719_1_.lastTickPosY) * p_72719_3_;
        double var9 = p_72719_1_.lastTickPosZ + (p_72719_1_.posZ - p_72719_1_.lastTickPosZ) * p_72719_3_;
        double var11 = p_72719_1_.posX - this.prevSortX;
        double var13 = p_72719_1_.posY - this.prevSortY;
        double var15 = p_72719_1_.posZ - this.prevSortZ;

        if (this.prevChunkSortX != p_72719_1_.chunkCoordX || this.prevChunkSortY != p_72719_1_.chunkCoordY || this.prevChunkSortZ != p_72719_1_.chunkCoordZ || var11 * var11 + var13 * var13 + var15 * var15 > 16.0D)
        {
            this.prevSortX = p_72719_1_.posX;
            this.prevSortY = p_72719_1_.posY;
            this.prevSortZ = p_72719_1_.posZ;
            this.prevChunkSortX = p_72719_1_.chunkCoordX;
            this.prevChunkSortY = p_72719_1_.chunkCoordY;
            this.prevChunkSortZ = p_72719_1_.chunkCoordZ;
            this.markRenderersForNewPosition(MathHelper.floor_double(p_72719_1_.posX), MathHelper.floor_double(p_72719_1_.posY), MathHelper.floor_double(p_72719_1_.posZ));
            Arrays.sort(this.sortedWorldRenderers, new EntitySorter(p_72719_1_));
        }

        double var17 = p_72719_1_.posX - this.prevRenderSortX;
        double var19 = p_72719_1_.posY - this.prevRenderSortY;
        double var21 = p_72719_1_.posZ - this.prevRenderSortZ;
        int var23;

        if (var17 * var17 + var19 * var19 + var21 * var21 > 1.0D)
        {
            this.prevRenderSortX = p_72719_1_.posX;
            this.prevRenderSortY = p_72719_1_.posY;
            this.prevRenderSortZ = p_72719_1_.posZ;

            for (var23 = 0; var23 < 27; ++var23)
            {
                this.sortedWorldRenderers[var23].updateRendererSort(p_72719_1_);
            }
        }

        RenderHelper.disableStandardItemLighting();
        byte var40 = 0;

        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && p_72719_2_ == 0)
        {
            byte var24 = 0;
            int var25 = 16;
            this.checkOcclusionQueryResult(var24, var25);

            for (int var26 = var24; var26 < var25; ++var26)
            {
                this.sortedWorldRenderers[var26].isVisible = true;
            }

            this.theWorld.theProfiler.endStartSection("render");
            var23 = var40 + this.renderSortedRenderers(var24, var25, p_72719_2_, p_72719_3_);

            do
            {
                this.theWorld.theProfiler.endStartSection("occ");
                int var41 = var25;
                var25 *= 2;

                if (var25 > this.sortedWorldRenderers.length)
                {
                    var25 = this.sortedWorldRenderers.length;
                }

                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_FOG);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                this.theWorld.theProfiler.startSection("check");
                this.checkOcclusionQueryResult(var41, var25);
                this.theWorld.theProfiler.endSection();
                GL11.glPushMatrix();
                float var42 = 0.0F;
                float var27 = 0.0F;
                float var28 = 0.0F;

                for (int var29 = var41; var29 < var25; ++var29)
                {
                    if (this.sortedWorldRenderers[var29].skipAllRenderPasses())
                    {
                        this.sortedWorldRenderers[var29].isInFrustum = false;
                    }
                    else
                    {
                        if (!this.sortedWorldRenderers[var29].isInFrustum)
                        {
                            this.sortedWorldRenderers[var29].isVisible = true;
                        }

                        if (this.sortedWorldRenderers[var29].isInFrustum && !this.sortedWorldRenderers[var29].isWaitingOnOcclusionQuery)
                        {
                            float var30 = MathHelper.sqrt_float(this.sortedWorldRenderers[var29].distanceToEntitySquared(p_72719_1_));
                            int var31 = (int)(1.0F + var30 / 128.0F);

                            if (this.cloudTickCounter % var31 == var29 % var31)
                            {
                                WorldRenderer var32 = this.sortedWorldRenderers[var29];
                                float var33 = (float)((double)var32.posXMinus - var39);
                                float var34 = (float)((double)var32.posYMinus - var7);
                                float var35 = (float)((double)var32.posZMinus - var9);
                                float var36 = var33 - var42;
                                float var37 = var34 - var27;
                                float var38 = var35 - var28;

                                if (var36 != 0.0F || var37 != 0.0F || var38 != 0.0F)
                                {
                                    GL11.glTranslatef(var36, var37, var38);
                                    var42 += var36;
                                    var27 += var37;
                                    var28 += var38;
                                }

                                this.theWorld.theProfiler.startSection("bb");
                                ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, this.sortedWorldRenderers[var29].glOcclusionQuery);
                                this.sortedWorldRenderers[var29].callOcclusionQueryList();
                                ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
                                this.theWorld.theProfiler.endSection();
                                this.sortedWorldRenderers[var29].isWaitingOnOcclusionQuery = true;
                            }
                        }
                    }
                }

                GL11.glPopMatrix();

                if (this.mc.gameSettings.anaglyph)
                {
                    if (EntityRenderer.anaglyphField == 0)
                    {
                        GL11.glColorMask(false, true, true, true);
                    }
                    else
                    {
                        GL11.glColorMask(true, false, false, true);
                    }
                }
                else
                {
                    GL11.glColorMask(true, true, true, true);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_FOG);
                this.theWorld.theProfiler.endStartSection("render");
                var23 += this.renderSortedRenderers(var41, var25, p_72719_2_, p_72719_3_);
            }
            while (var25 < this.sortedWorldRenderers.length);
        }
        else
        {
            this.theWorld.theProfiler.endStartSection("render");
            var23 = var40 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, p_72719_2_, p_72719_3_);
        }

        this.theWorld.theProfiler.endSection();
        return var23;
    }

    private void checkOcclusionQueryResult(int p_72720_1_, int p_72720_2_)
    {
        for (int var3 = p_72720_1_; var3 < p_72720_2_; ++var3)
        {
            if (this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery)
            {
                this.occlusionResult.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_AVAILABLE_ARB, this.occlusionResult);

                if (this.occlusionResult.get(0) != 0)
                {
                    this.sortedWorldRenderers[var3].isWaitingOnOcclusionQuery = false;
                    this.occlusionResult.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[var3].glOcclusionQuery, ARBOcclusionQuery.GL_QUERY_RESULT_ARB, this.occlusionResult);
                    this.sortedWorldRenderers[var3].isVisible = this.occlusionResult.get(0) != 0;
                }
            }
        }
    }

    /**
     * Renders the sorted renders for the specified render pass. Args: startRenderer, numRenderers, renderPass,
     * partialTickTime
     */
    private int renderSortedRenderers(int p_72724_1_, int p_72724_2_, int p_72724_3_, double p_72724_4_)
    {
        this.glRenderLists.clear();
        int var6 = 0;
        int var7 = p_72724_1_;
        int var8 = p_72724_2_;
        byte var9 = 1;

        if (p_72724_3_ == 1)
        {
            var7 = this.sortedWorldRenderers.length - 1 - p_72724_1_;
            var8 = this.sortedWorldRenderers.length - 1 - p_72724_2_;
            var9 = -1;
        }

        for (int var10 = var7; var10 != var8; var10 += var9)
        {
            if (p_72724_3_ == 0)
            {
                ++this.renderersLoaded;

                if (this.sortedWorldRenderers[var10].skipRenderPass[p_72724_3_])
                {
                    ++this.renderersSkippingRenderPass;
                }
                else if (!this.sortedWorldRenderers[var10].isInFrustum)
                {
                    ++this.renderersBeingClipped;
                }
                else if (this.occlusionEnabled && !this.sortedWorldRenderers[var10].isVisible)
                {
                    ++this.renderersBeingOccluded;
                }
                else
                {
                    ++this.renderersBeingRendered;
                }
            }

            if (!this.sortedWorldRenderers[var10].skipRenderPass[p_72724_3_] && this.sortedWorldRenderers[var10].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[var10].isVisible))
            {
                int var11 = this.sortedWorldRenderers[var10].getGLCallListForPass(p_72724_3_);

                if (var11 >= 0)
                {
                    this.glRenderLists.add(this.sortedWorldRenderers[var10]);
                    ++var6;
                }
            }
        }

        EntityLivingBase var22 = this.mc.renderViewEntity;
        double var23 = var22.lastTickPosX + (var22.posX - var22.lastTickPosX) * p_72724_4_;
        double var13 = var22.lastTickPosY + (var22.posY - var22.lastTickPosY) * p_72724_4_;
        double var15 = var22.lastTickPosZ + (var22.posZ - var22.lastTickPosZ) * p_72724_4_;
        int var17 = 0;
        int var18;

        for (var18 = 0; var18 < this.allRenderLists.length; ++var18)
        {
            this.allRenderLists[var18].resetList();
        }

        int var20;
        int var21;

        for (var18 = 0; var18 < this.glRenderLists.size(); ++var18)
        {
            WorldRenderer var19 = (WorldRenderer)this.glRenderLists.get(var18);
            var20 = -1;

            for (var21 = 0; var21 < var17; ++var21)
            {
                if (this.allRenderLists[var21].rendersChunk(var19.posXMinus, var19.posYMinus, var19.posZMinus))
                {
                    var20 = var21;
                }
            }

            if (var20 < 0)
            {
                var20 = var17++;
                this.allRenderLists[var20].setupRenderList(var19.posXMinus, var19.posYMinus, var19.posZMinus, var23, var13, var15);
            }

            this.allRenderLists[var20].addGLRenderList(var19.getGLCallListForPass(p_72724_3_));
        }

        var18 = MathHelper.floor_double(var23);
        int var24 = MathHelper.floor_double(var15);
        var20 = var18 - (var18 & 1023);
        var21 = var24 - (var24 & 1023);
        Arrays.sort(this.allRenderLists, new RenderDistanceSorter(var20, var21));
        this.renderAllRenderLists(p_72724_3_, p_72724_4_);
        return var6;
    }

    /**
     * Render all render lists
     */
    public void renderAllRenderLists(int p_72733_1_, double p_72733_2_)
    {
        this.mc.entityRenderer.enableLightmap(p_72733_2_);

        for (int var4 = 0; var4 < this.allRenderLists.length; ++var4)
        {
            this.allRenderLists[var4].callLists();
        }

        this.mc.entityRenderer.disableLightmap(p_72733_2_);
    }

    public void updateClouds()
    {
        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0)
        {
            Iterator var1 = this.damagedBlocks.values().iterator();

            while (var1.hasNext())
            {
                DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
                int var3 = var2.getCreationCloudUpdateTick();

                if (this.cloudTickCounter - var3 > 400)
                {
                    var1.remove();
                }
            }
        }
    }

    /**
     * Renders the sky with the partial tick time. Args: partialTickTime
     */
    public void renderSky(float p_72714_1_)
    {
        if (this.mc.theWorld.provider.dimensionId == 1)
        {
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            GL11.glDepthMask(false);
            this.renderEngine.bindTexture(locationEndSkyPng);
            Tessellator var21 = Tessellator.instance;

            for (int var22 = 0; var22 < 6; ++var22)
            {
                GL11.glPushMatrix();

                if (var22 == 1)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 2)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 3)
                {
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var22 == 4)
                {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (var22 == 5)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                var21.startDrawingQuads();
                var21.setColorOpaque_I(2631720);
                var21.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
                var21.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
                var21.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
                var21.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
                var21.draw();
                GL11.glPopMatrix();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
        else if (this.mc.theWorld.provider.isSurfaceWorld())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 var2 = this.theWorld.getSkyColor(this.mc.renderViewEntity, p_72714_1_);
            float var3 = (float)var2.xCoord;
            float var4 = (float)var2.yCoord;
            float var5 = (float)var2.zCoord;
            float var8;

            if (this.mc.gameSettings.anaglyph)
            {
                float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
                float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
                var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
                var3 = var6;
                var4 = var7;
                var5 = var8;
            }

            GL11.glColor3f(var3, var4, var5);
            Tessellator var23 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(var3, var4, var5);
            GL11.glCallList(this.glSkyList);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            float[] var24 = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(p_72714_1_), p_72714_1_);
            float var9;
            float var10;
            float var11;
            float var12;

            if (var24 != null)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(this.theWorld.getCelestialAngleRadians(p_72714_1_)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                var8 = var24[0];
                var9 = var24[1];
                var10 = var24[2];
                float var13;

                if (this.mc.gameSettings.anaglyph)
                {
                    var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
                    var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
                    var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
                    var8 = var11;
                    var9 = var12;
                    var10 = var13;
                }

                var23.startDrawing(6);
                var23.setColorRGBA_F(var8, var9, var10, var24[3]);
                var23.addVertex(0.0D, 100.0D, 0.0D);
                byte var26 = 16;
                var23.setColorRGBA_F(var24[0], var24[1], var24[2], 0.0F);

                for (int var27 = 0; var27 <= var26; ++var27)
                {
                    var13 = (float)var27 * (float)Math.PI * 2.0F / (float)var26;
                    float var14 = MathHelper.sin(var13);
                    float var15 = MathHelper.cos(var13);
                    var23.addVertex((double)(var14 * 120.0F), (double)(var15 * 120.0F), (double)(-var15 * 40.0F * var24[3]));
                }

                var23.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            GL11.glPushMatrix();
            var8 = 1.0F - this.theWorld.getRainStrength(p_72714_1_);
            var9 = 0.0F;
            var10 = 0.0F;
            var11 = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
            GL11.glTranslatef(var9, var10, var11);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.theWorld.getCelestialAngle(p_72714_1_) * 360.0F, 1.0F, 0.0F, 0.0F);
            var12 = 30.0F;
            this.renderEngine.bindTexture(locationSunPng);
            var23.startDrawingQuads();
            var23.addVertexWithUV((double)(-var12), 100.0D, (double)(-var12), 0.0D, 0.0D);
            var23.addVertexWithUV((double)var12, 100.0D, (double)(-var12), 1.0D, 0.0D);
            var23.addVertexWithUV((double)var12, 100.0D, (double)var12, 1.0D, 1.0D);
            var23.addVertexWithUV((double)(-var12), 100.0D, (double)var12, 0.0D, 1.0D);
            var23.draw();
            var12 = 20.0F;
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int var28 = this.theWorld.getMoonPhase();
            int var29 = var28 % 4;
            int var30 = var28 / 4 % 2;
            float var16 = (float)(var29 + 0) / 4.0F;
            float var17 = (float)(var30 + 0) / 2.0F;
            float var18 = (float)(var29 + 1) / 4.0F;
            float var19 = (float)(var30 + 1) / 2.0F;
            var23.startDrawingQuads();
            var23.addVertexWithUV((double)(-var12), -100.0D, (double)var12, (double)var18, (double)var19);
            var23.addVertexWithUV((double)var12, -100.0D, (double)var12, (double)var16, (double)var19);
            var23.addVertexWithUV((double)var12, -100.0D, (double)(-var12), (double)var16, (double)var17);
            var23.addVertexWithUV((double)(-var12), -100.0D, (double)(-var12), (double)var18, (double)var17);
            var23.draw();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            float var20 = this.theWorld.getStarBrightness(p_72714_1_) * var8;

            if (var20 > 0.0F)
            {
                GL11.glColor4f(var20, var20, var20, var20);
                GL11.glCallList(this.starGLCallList);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double var25 = this.mc.thePlayer.getPosition(p_72714_1_).yCoord - this.theWorld.getHorizon();

            if (var25 < 0.0D)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.glSkyList2);
                GL11.glPopMatrix();
                var10 = 1.0F;
                var11 = -((float)(var25 + 65.0D));
                var12 = -var10;
                var23.startDrawingQuads();
                var23.setColorRGBA_I(0, 255);
                var23.addVertex((double)(-var10), (double)var11, (double)var10);
                var23.addVertex((double)var10, (double)var11, (double)var10);
                var23.addVertex((double)var10, (double)var12, (double)var10);
                var23.addVertex((double)(-var10), (double)var12, (double)var10);
                var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var23.addVertex((double)var10, (double)var12, (double)(-var10));
                var23.addVertex((double)var10, (double)var11, (double)(-var10));
                var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
                var23.addVertex((double)var10, (double)var12, (double)(-var10));
                var23.addVertex((double)var10, (double)var12, (double)var10);
                var23.addVertex((double)var10, (double)var11, (double)var10);
                var23.addVertex((double)var10, (double)var11, (double)(-var10));
                var23.addVertex((double)(-var10), (double)var11, (double)(-var10));
                var23.addVertex((double)(-var10), (double)var11, (double)var10);
                var23.addVertex((double)(-var10), (double)var12, (double)var10);
                var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var23.addVertex((double)(-var10), (double)var12, (double)(-var10));
                var23.addVertex((double)(-var10), (double)var12, (double)var10);
                var23.addVertex((double)var10, (double)var12, (double)var10);
                var23.addVertex((double)var10, (double)var12, (double)(-var10));
                var23.draw();
            }

            if (this.theWorld.provider.isSkyColored())
            {
                GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(var3, var4, var5);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(var25 - 16.0D)), 0.0F);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

    public void renderClouds(float p_72718_1_)
    {
        if (this.mc.theWorld.provider.isSurfaceWorld())
        {
            if (this.mc.gameSettings.fancyGraphics)
            {
                this.renderCloudsFancy(p_72718_1_);
            }
            else
            {
                GL11.glDisable(GL11.GL_CULL_FACE);
                float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)p_72718_1_);
                byte var3 = 32;
                int var4 = 256 / var3;
                Tessellator var5 = Tessellator.instance;
                this.renderEngine.bindTexture(locationCloudsPng);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                Vec3 var6 = this.theWorld.getCloudColour(p_72718_1_);
                float var7 = (float)var6.xCoord;
                float var8 = (float)var6.yCoord;
                float var9 = (float)var6.zCoord;
                float var10;

                if (this.mc.gameSettings.anaglyph)
                {
                    var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
                    float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
                    float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
                    var7 = var10;
                    var8 = var11;
                    var9 = var12;
                }

                var10 = 4.8828125E-4F;
                double var24 = (double)((float)this.cloudTickCounter + p_72718_1_);
                double var13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)p_72718_1_ + var24 * 0.029999999329447746D;
                double var15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)p_72718_1_;
                int var17 = MathHelper.floor_double(var13 / 2048.0D);
                int var18 = MathHelper.floor_double(var15 / 2048.0D);
                var13 -= (double)(var17 * 2048);
                var15 -= (double)(var18 * 2048);
                float var19 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
                float var20 = (float)(var13 * (double)var10);
                float var21 = (float)(var15 * (double)var10);
                var5.startDrawingQuads();
                var5.setColorRGBA_F(var7, var8, var9, 0.8F);

                for (int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3)
                {
                    for (int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3)
                    {
                        var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + var3), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + var3), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + var3) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + var3), (double)var19, (double)(var23 + 0), (double)((float)(var22 + var3) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                        var5.addVertexWithUV((double)(var22 + 0), (double)var19, (double)(var23 + 0), (double)((float)(var22 + 0) * var10 + var20), (double)((float)(var23 + 0) * var10 + var21));
                    }
                }

                var5.draw();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }
        }
    }

    /**
     * Checks if the given position is to be rendered with cloud fog
     */
    public boolean hasCloudFog(double p_72721_1_, double p_72721_3_, double p_72721_5_, float p_72721_7_)
    {
        return false;
    }

    /**
     * Renders the 3d fancy clouds
     */
    public void renderCloudsFancy(float p_72736_1_)
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)p_72736_1_);
        Tessellator var3 = Tessellator.instance;
        float var4 = 12.0F;
        float var5 = 4.0F;
        double var6 = (double)((float)this.cloudTickCounter + p_72736_1_);
        double var8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)p_72736_1_ + var6 * 0.029999999329447746D) / (double)var4;
        double var10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)p_72736_1_) / (double)var4 + 0.33000001311302185D;
        float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33F;
        int var13 = MathHelper.floor_double(var8 / 2048.0D);
        int var14 = MathHelper.floor_double(var10 / 2048.0D);
        var8 -= (double)(var13 * 2048);
        var10 -= (double)(var14 * 2048);
        this.renderEngine.bindTexture(locationCloudsPng);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Vec3 var15 = this.theWorld.getCloudColour(p_72736_1_);
        float var16 = (float)var15.xCoord;
        float var17 = (float)var15.yCoord;
        float var18 = (float)var15.zCoord;
        float var19;
        float var20;
        float var21;

        if (this.mc.gameSettings.anaglyph)
        {
            var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
            var20 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
            var21 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
            var16 = var19;
            var17 = var20;
            var18 = var21;
        }

        var19 = (float)(var8 * 0.0D);
        var20 = (float)(var10 * 0.0D);
        var21 = 0.00390625F;
        var19 = (float)MathHelper.floor_double(var8) * var21;
        var20 = (float)MathHelper.floor_double(var10) * var21;
        float var22 = (float)(var8 - (double)MathHelper.floor_double(var8));
        float var23 = (float)(var10 - (double)MathHelper.floor_double(var10));
        byte var24 = 8;
        byte var25 = 4;
        float var26 = 9.765625E-4F;
        GL11.glScalef(var4, 1.0F, var4);

        for (int var27 = 0; var27 < 2; ++var27)
        {
            if (var27 == 0)
            {
                GL11.glColorMask(false, false, false, false);
            }
            else if (this.mc.gameSettings.anaglyph)
            {
                if (EntityRenderer.anaglyphField == 0)
                {
                    GL11.glColorMask(false, true, true, true);
                }
                else
                {
                    GL11.glColorMask(true, false, false, true);
                }
            }
            else
            {
                GL11.glColorMask(true, true, true, true);
            }

            for (int var28 = -var25 + 1; var28 <= var25; ++var28)
            {
                for (int var29 = -var25 + 1; var29 <= var25; ++var29)
                {
                    var3.startDrawingQuads();
                    float var30 = (float)(var28 * var24);
                    float var31 = (float)(var29 * var24);
                    float var32 = var30 - var22;
                    float var33 = var31 - var23;

                    if (var12 > -var5 - 1.0F)
                    {
                        var3.setColorRGBA_F(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                        var3.setNormal(0.0F, -1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                    }

                    if (var12 <= var5 + 1.0F)
                    {
                        var3.setColorRGBA_F(var16, var17, var18, 0.8F);
                        var3.setNormal(0.0F, 1.0F, 0.0F);
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + (float)var24), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5 - var26), (double)(var33 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                    }

                    var3.setColorRGBA_F(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
                    int var34;

                    if (var28 > -1)
                    {
                        var3.setNormal(-1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        }
                    }

                    if (var28 <= 1)
                    {
                        var3.setNormal(1.0F, 0.0F, 0.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + (float)var24), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + (float)var24) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + var5), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var34 + 1.0F - var26), (double)(var12 + 0.0F), (double)(var33 + 0.0F), (double)((var30 + (float)var34 + 0.5F) * var21 + var19), (double)((var31 + 0.0F) * var21 + var20));
                        }
                    }

                    var3.setColorRGBA_F(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);

                    if (var29 > -1)
                    {
                        var3.setNormal(0.0F, 0.0F, -1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 0.0F), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                        }
                    }

                    if (var29 <= 1)
                    {
                        var3.setNormal(0.0F, 0.0F, 1.0F);

                        for (var34 = 0; var34 < var24; ++var34)
                        {
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + var5), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + (float)var24), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + (float)var24) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                            var3.addVertexWithUV((double)(var32 + 0.0F), (double)(var12 + 0.0F), (double)(var33 + (float)var34 + 1.0F - var26), (double)((var30 + 0.0F) * var21 + var19), (double)((var31 + (float)var34 + 0.5F) * var21 + var20));
                        }
                    }

                    var3.draw();
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Updates some of the renderers sorted by distance from the player
     */
    public boolean updateRenderers(EntityLivingBase p_72716_1_, boolean p_72716_2_)
    {
        byte var3 = 2;
        RenderSorter var4 = new RenderSorter(p_72716_1_);
        WorldRenderer[] var5 = new WorldRenderer[var3];
        ArrayList var6 = null;
        int var7 = this.worldRenderersToUpdate.size();
        int var8 = 0;
        this.theWorld.theProfiler.startSection("nearChunksSearch");
        int var9;
        WorldRenderer var10;
        int var11;
        int var12;
        label136:

        for (var9 = 0; var9 < var7; ++var9)
        {
            var10 = (WorldRenderer)this.worldRenderersToUpdate.get(var9);

            if (var10 != null)
            {
                if (!p_72716_2_)
                {
                    if (var10.distanceToEntitySquared(p_72716_1_) > 272.0F)
                    {
                        for (var11 = 0; var11 < var3 && (var5[var11] == null || var4.compare(var5[var11], var10) <= 0); ++var11)
                        {
                            ;
                        }

                        --var11;

                        if (var11 > 0)
                        {
                            var12 = var11;

                            while (true)
                            {
                                --var12;

                                if (var12 == 0)
                                {
                                    var5[var11] = var10;
                                    continue label136;
                                }

                                var5[var12 - 1] = var5[var12];
                            }
                        }

                        continue;
                    }
                }
                else if (!var10.isInFrustum)
                {
                    continue;
                }

                if (var6 == null)
                {
                    var6 = new ArrayList();
                }

                ++var8;
                var6.add(var10);
                this.worldRenderersToUpdate.set(var9, (Object)null);
            }
        }

        this.theWorld.theProfiler.endSection();
        this.theWorld.theProfiler.startSection("sort");

        if (var6 != null)
        {
            if (var6.size() > 1)
            {
                Collections.sort(var6, var4);
            }

            for (var9 = var6.size() - 1; var9 >= 0; --var9)
            {
                var10 = (WorldRenderer)var6.get(var9);
                var10.updateRenderer(p_72716_1_);
                var10.needsUpdate = false;
            }
        }

        this.theWorld.theProfiler.endSection();
        var9 = 0;
        this.theWorld.theProfiler.startSection("rebuild");
        int var16;

        for (var16 = var3 - 1; var16 >= 0; --var16)
        {
            WorldRenderer var17 = var5[var16];

            if (var17 != null)
            {
                if (!var17.isInFrustum && var16 != var3 - 1)
                {
                    var5[var16] = null;
                    var5[0] = null;
                    break;
                }

                var5[var16].updateRenderer(p_72716_1_);
                var5[var16].needsUpdate = false;
                ++var9;
            }
        }

        this.theWorld.theProfiler.endSection();
        this.theWorld.theProfiler.startSection("cleanup");
        var16 = 0;
        var11 = 0;

        for (var12 = this.worldRenderersToUpdate.size(); var16 != var12; ++var16)
        {
            WorldRenderer var13 = (WorldRenderer)this.worldRenderersToUpdate.get(var16);

            if (var13 != null)
            {
                boolean var14 = false;

                for (int var15 = 0; var15 < var3 && !var14; ++var15)
                {
                    if (var13 == var5[var15])
                    {
                        var14 = true;
                    }
                }

                if (!var14)
                {
                    if (var11 != var16)
                    {
                        this.worldRenderersToUpdate.set(var11, var13);
                    }

                    ++var11;
                }
            }
        }

        this.theWorld.theProfiler.endSection();
        this.theWorld.theProfiler.startSection("trim");

        while (true)
        {
            --var16;

            if (var16 < var11)
            {
                this.theWorld.theProfiler.endSection();
                return var7 == var8 + var9;
            }

            this.worldRenderersToUpdate.remove(var16);
        }
    }

    public void drawBlockDamageTexture(Tessellator p_72717_1_, EntityPlayer p_72717_2_, float p_72717_3_)
    {
        double var4 = p_72717_2_.lastTickPosX + (p_72717_2_.posX - p_72717_2_.lastTickPosX) * (double)p_72717_3_;
        double var6 = p_72717_2_.lastTickPosY + (p_72717_2_.posY - p_72717_2_.lastTickPosY) * (double)p_72717_3_;
        double var8 = p_72717_2_.lastTickPosZ + (p_72717_2_.posZ - p_72717_2_.lastTickPosZ) * (double)p_72717_3_;

        if (!this.damagedBlocks.isEmpty())
        {
            OpenGlHelper.glBlendFunc(774, 768, 1, 0);
            this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            GL11.glPushMatrix();
            GL11.glPolygonOffset(-3.0F, -3.0F);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            p_72717_1_.startDrawingQuads();
            p_72717_1_.setTranslation(-var4, -var6, -var8);
            p_72717_1_.disableColor();
            Iterator var10 = this.damagedBlocks.values().iterator();

            while (var10.hasNext())
            {
                DestroyBlockProgress var11 = (DestroyBlockProgress)var10.next();
                double var12 = (double)var11.getPartialBlockX() - var4;
                double var14 = (double)var11.getPartialBlockY() - var6;
                double var16 = (double)var11.getPartialBlockZ() - var8;

                if (var12 * var12 + var14 * var14 + var16 * var16 > 1024.0D)
                {
                    var10.remove();
                }
                else
                {
                    Block var18 = this.theWorld.getBlock(var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ());

                    if (var18.getMaterial() != Material.air)
                    {
                        this.renderBlocksRg.renderBlockUsingTexture(var18, var11.getPartialBlockX(), var11.getPartialBlockY(), var11.getPartialBlockZ(), this.destroyBlockIcons[var11.getPartialBlockDamage()]);
                    }
                }
            }

            p_72717_1_.draw();
            p_72717_1_.setTranslation(0.0D, 0.0D, 0.0D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }

    /**
     * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
     */
    public void drawSelectionBox(EntityPlayer p_72731_1_, MovingObjectPosition p_72731_2_, int p_72731_3_, float p_72731_4_)
    {
        if (p_72731_3_ == 0 && p_72731_2_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            float var5 = 0.002F;
            Block var6 = this.theWorld.getBlock(p_72731_2_.blockX, p_72731_2_.blockY, p_72731_2_.blockZ);

            if (var6.getMaterial() != Material.air)
            {
                var6.setBlockBoundsBasedOnState(this.theWorld, p_72731_2_.blockX, p_72731_2_.blockY, p_72731_2_.blockZ);
                double var7 = p_72731_1_.lastTickPosX + (p_72731_1_.posX - p_72731_1_.lastTickPosX) * (double)p_72731_4_;
                double var9 = p_72731_1_.lastTickPosY + (p_72731_1_.posY - p_72731_1_.lastTickPosY) * (double)p_72731_4_;
                double var11 = p_72731_1_.lastTickPosZ + (p_72731_1_.posZ - p_72731_1_.lastTickPosZ) * (double)p_72731_4_;
                drawOutlinedBoundingBox(var6.getSelectedBoundingBoxFromPool(this.theWorld, p_72731_2_.blockX, p_72731_2_.blockY, p_72731_2_.blockZ).expand((double)var5, (double)var5, (double)var5).getOffsetBoundingBox(-var7, -var9, -var11), -1);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    /**
     * Draws lines for the edges of the bounding box.
     */
    public static void drawOutlinedBoundingBox(AxisAlignedBB p_147590_0_, int p_147590_1_)
    {
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);

        if (p_147590_1_ != -1)
        {
            var2.setColorOpaque_I(p_147590_1_);
        }

        var2.addVertex(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ);
        var2.draw();
        var2.startDrawing(3);

        if (p_147590_1_ != -1)
        {
            var2.setColorOpaque_I(p_147590_1_);
        }

        var2.addVertex(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ);
        var2.draw();
        var2.startDrawing(1);

        if (p_147590_1_ != -1)
        {
            var2.setColorOpaque_I(p_147590_1_);
        }

        var2.addVertex(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.minZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.minY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.maxX, p_147590_0_.maxY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.minY, p_147590_0_.maxZ);
        var2.addVertex(p_147590_0_.minX, p_147590_0_.maxY, p_147590_0_.maxZ);
        var2.draw();
    }

    /**
     * Marks the blocks in the given range for update
     */
    public void markBlocksForUpdate(int p_72725_1_, int p_72725_2_, int p_72725_3_, int p_72725_4_, int p_72725_5_, int p_72725_6_)
    {
        int var7 = MathHelper.bucketInt(p_72725_1_, 16);
        int var8 = MathHelper.bucketInt(p_72725_2_, 16);
        int var9 = MathHelper.bucketInt(p_72725_3_, 16);
        int var10 = MathHelper.bucketInt(p_72725_4_, 16);
        int var11 = MathHelper.bucketInt(p_72725_5_, 16);
        int var12 = MathHelper.bucketInt(p_72725_6_, 16);

        for (int var13 = var7; var13 <= var10; ++var13)
        {
            int var14 = var13 % this.renderChunksWide;

            if (var14 < 0)
            {
                var14 += this.renderChunksWide;
            }

            for (int var15 = var8; var15 <= var11; ++var15)
            {
                int var16 = var15 % this.renderChunksTall;

                if (var16 < 0)
                {
                    var16 += this.renderChunksTall;
                }

                for (int var17 = var9; var17 <= var12; ++var17)
                {
                    int var18 = var17 % this.renderChunksDeep;

                    if (var18 < 0)
                    {
                        var18 += this.renderChunksDeep;
                    }

                    int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
                    WorldRenderer var20 = this.worldRenderers[var19];

                    if (var20 != null && !var20.needsUpdate)
                    {
                        this.worldRenderersToUpdate.add(var20);
                        var20.markDirty();
                    }
                }
            }
        }
    }

    /**
     * On the client, re-renders the block. On the server, sends the block to the client (which will re-render it),
     * including the tile entity description packet if applicable. Args: x, y, z
     */
    public void markBlockForUpdate(int p_147586_1_, int p_147586_2_, int p_147586_3_)
    {
        this.markBlocksForUpdate(p_147586_1_ - 1, p_147586_2_ - 1, p_147586_3_ - 1, p_147586_1_ + 1, p_147586_2_ + 1, p_147586_3_ + 1);
    }

    /**
     * On the client, re-renders this block. On the server, does nothing. Used for lighting updates.
     */
    public void markBlockForRenderUpdate(int p_147588_1_, int p_147588_2_, int p_147588_3_)
    {
        this.markBlocksForUpdate(p_147588_1_ - 1, p_147588_2_ - 1, p_147588_3_ - 1, p_147588_1_ + 1, p_147588_2_ + 1, p_147588_3_ + 1);
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_)
    {
        this.markBlocksForUpdate(p_147585_1_ - 1, p_147585_2_ - 1, p_147585_3_ - 1, p_147585_4_ + 1, p_147585_5_ + 1, p_147585_6_ + 1);
    }

    /**
     * Checks all renderers that previously weren't in the frustum and 1/16th of those that previously were in the
     * frustum for frustum clipping Args: frustum, partialTickTime
     */
    public void clipRenderersByFrustum(ICamera p_72729_1_, float p_72729_2_)
    {
        for (int var3 = 0; var3 < this.worldRenderers.length; ++var3)
        {
            if (!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustumCheckOffset & 15) == 0))
            {
                this.worldRenderers[var3].updateInFrustum(p_72729_1_);
            }
        }

        ++this.frustumCheckOffset;
    }

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(String p_72702_1_, int p_72702_2_, int p_72702_3_, int p_72702_4_)
    {
        ChunkCoordinates var5 = new ChunkCoordinates(p_72702_2_, p_72702_3_, p_72702_4_);
        ISound var6 = (ISound)this.mapSoundPositions.get(var5);

        if (var6 != null)
        {
            this.mc.getSoundHandler().func_147683_b(var6);
            this.mapSoundPositions.remove(var5);
        }

        if (p_72702_1_ != null)
        {
            ItemRecord var7 = ItemRecord.func_150926_b(p_72702_1_);

            if (var7 != null)
            {
                this.mc.ingameGUI.setRecordPlayingMessage(var7.func_150927_i());
            }

            PositionedSoundRecord var8 = PositionedSoundRecord.func_147675_a(new ResourceLocation(p_72702_1_), (float)p_72702_2_, (float)p_72702_3_, (float)p_72702_4_);
            this.mapSoundPositions.put(var5, var8);
            this.mc.getSoundHandler().playSound(var8);
        }
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(String p_72704_1_, double p_72704_2_, double p_72704_4_, double p_72704_6_, float p_72704_8_, float p_72704_9_) {}

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(EntityPlayer p_85102_1_, String p_85102_2_, double p_85102_3_, double p_85102_5_, double p_85102_7_, float p_85102_9_, float p_85102_10_) {}

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String p_72708_1_, final double p_72708_2_, final double p_72708_4_, final double p_72708_6_, double p_72708_8_, double p_72708_10_, double p_72708_12_)
    {
        try
        {
            this.doSpawnParticle(p_72708_1_, p_72708_2_, p_72708_4_, p_72708_6_, p_72708_8_, p_72708_10_, p_72708_12_);
        }
        catch (Throwable var17)
        {
            CrashReport var15 = CrashReport.makeCrashReport(var17, "Exception while adding particle");
            CrashReportCategory var16 = var15.makeCategory("Particle being added");
            var16.addCrashSection("Name", p_72708_1_);
            var16.addCrashSectionCallable("Position", new Callable()
            {
                private static final String __OBFID = "CL_00000955";
                public String call()
                {
                    return CrashReportCategory.func_85074_a(p_72708_2_, p_72708_4_, p_72708_6_);
                }
            });
            throw new ReportedException(var15);
        }
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public EntityFX doSpawnParticle(String p_72726_1_, double p_72726_2_, double p_72726_4_, double p_72726_6_, double p_72726_8_, double p_72726_10_, double p_72726_12_)
    {
        if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null)
        {
            int var14 = this.mc.gameSettings.particleSetting;

            if (var14 == 1 && this.theWorld.rand.nextInt(3) == 0)
            {
                var14 = 2;
            }

            double var15 = this.mc.renderViewEntity.posX - p_72726_2_;
            double var17 = this.mc.renderViewEntity.posY - p_72726_4_;
            double var19 = this.mc.renderViewEntity.posZ - p_72726_6_;
            EntityFX var21 = null;

            if (p_72726_1_.equals("hugeexplosion"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityHugeExplodeFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_));
            }
            else if (p_72726_1_.equals("largeexplode"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityLargeExplodeFX(this.renderEngine, this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_));
            }
            else if (p_72726_1_.equals("fireworksSpark"))
            {
                this.mc.effectRenderer.addEffect(var21 = new EntityFireworkSparkFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, this.mc.effectRenderer));
            }

            if (var21 != null)
            {
                return (EntityFX)var21;
            }
            else
            {
                double var22 = 16.0D;

                if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
                {
                    return null;
                }
                else if (var14 > 1)
                {
                    return null;
                }
                else
                {
                    if (p_72726_1_.equals("bubble"))
                    {
                        var21 = new EntityBubbleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("suspended"))
                    {
                        var21 = new EntitySuspendFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("depthsuspend"))
                    {
                        var21 = new EntityAuraFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("townaura"))
                    {
                        var21 = new EntityAuraFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("crit"))
                    {
                        var21 = new EntityCritFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("magicCrit"))
                    {
                        var21 = new EntityCritFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                        ((EntityFX)var21).setRBGColorF(((EntityFX)var21).getRedColorF() * 0.3F, ((EntityFX)var21).getGreenColorF() * 0.8F, ((EntityFX)var21).getBlueColorF());
                        ((EntityFX)var21).nextTextureIndexX();
                    }
                    else if (p_72726_1_.equals("smoke"))
                    {
                        var21 = new EntitySmokeFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("mobSpell"))
                    {
                        var21 = new EntitySpellParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, 0.0D, 0.0D, 0.0D);
                        ((EntityFX)var21).setRBGColorF((float)p_72726_8_, (float)p_72726_10_, (float)p_72726_12_);
                    }
                    else if (p_72726_1_.equals("mobSpellAmbient"))
                    {
                        var21 = new EntitySpellParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, 0.0D, 0.0D, 0.0D);
                        ((EntityFX)var21).setAlphaF(0.15F);
                        ((EntityFX)var21).setRBGColorF((float)p_72726_8_, (float)p_72726_10_, (float)p_72726_12_);
                    }
                    else if (p_72726_1_.equals("spell"))
                    {
                        var21 = new EntitySpellParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("instantSpell"))
                    {
                        var21 = new EntitySpellParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                        ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
                    }
                    else if (p_72726_1_.equals("witchMagic"))
                    {
                        var21 = new EntitySpellParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                        ((EntitySpellParticleFX)var21).setBaseSpellTextureIndex(144);
                        float var24 = this.theWorld.rand.nextFloat() * 0.5F + 0.35F;
                        ((EntityFX)var21).setRBGColorF(1.0F * var24, 0.0F * var24, 1.0F * var24);
                    }
                    else if (p_72726_1_.equals("note"))
                    {
                        var21 = new EntityNoteFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("portal"))
                    {
                        var21 = new EntityPortalFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("enchantmenttable"))
                    {
                        var21 = new EntityEnchantmentTableParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("explode"))
                    {
                        var21 = new EntityExplodeFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("flame"))
                    {
                        var21 = new EntityFlameFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("lava"))
                    {
                        var21 = new EntityLavaFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_);
                    }
                    else if (p_72726_1_.equals("footstep"))
                    {
                        var21 = new EntityFootStepFX(this.renderEngine, this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_);
                    }
                    else if (p_72726_1_.equals("splash"))
                    {
                        var21 = new EntitySplashFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("wake"))
                    {
                        var21 = new EntityFishWakeFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("largesmoke"))
                    {
                        var21 = new EntitySmokeFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, 2.5F);
                    }
                    else if (p_72726_1_.equals("cloud"))
                    {
                        var21 = new EntityCloudFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("reddust"))
                    {
                        var21 = new EntityReddustFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, (float)p_72726_8_, (float)p_72726_10_, (float)p_72726_12_);
                    }
                    else if (p_72726_1_.equals("snowballpoof"))
                    {
                        var21 = new EntityBreakingFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, Items.snowball);
                    }
                    else if (p_72726_1_.equals("dripWater"))
                    {
                        var21 = new EntityDropParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, Material.water);
                    }
                    else if (p_72726_1_.equals("dripLava"))
                    {
                        var21 = new EntityDropParticleFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, Material.lava);
                    }
                    else if (p_72726_1_.equals("snowshovel"))
                    {
                        var21 = new EntitySnowShovelFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("slime"))
                    {
                        var21 = new EntityBreakingFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, Items.slime_ball);
                    }
                    else if (p_72726_1_.equals("heart"))
                    {
                        var21 = new EntityHeartFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                    }
                    else if (p_72726_1_.equals("angryVillager"))
                    {
                        var21 = new EntityHeartFX(this.theWorld, p_72726_2_, p_72726_4_ + 0.5D, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                        ((EntityFX)var21).setParticleTextureIndex(81);
                        ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else if (p_72726_1_.equals("happyVillager"))
                    {
                        var21 = new EntityAuraFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_);
                        ((EntityFX)var21).setParticleTextureIndex(82);
                        ((EntityFX)var21).setRBGColorF(1.0F, 1.0F, 1.0F);
                    }
                    else
                    {
                        int var26;
                        String[] var27;

                        if (p_72726_1_.startsWith("iconcrack_"))
                        {
                            var27 = p_72726_1_.split("_", 3);
                            int var25 = Integer.parseInt(var27[1]);

                            if (var27.length > 2)
                            {
                                var26 = Integer.parseInt(var27[2]);
                                var21 = new EntityBreakingFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, Item.getItemById(var25), var26);
                            }
                            else
                            {
                                var21 = new EntityBreakingFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, Item.getItemById(var25), 0);
                            }
                        }
                        else
                        {
                            Block var28;

                            if (p_72726_1_.startsWith("blockcrack_"))
                            {
                                var27 = p_72726_1_.split("_", 3);
                                var28 = Block.getBlockById(Integer.parseInt(var27[1]));
                                var26 = Integer.parseInt(var27[2]);
                                var21 = (new EntityDiggingFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, var28, var26)).applyRenderColor(var26);
                            }
                            else if (p_72726_1_.startsWith("blockdust_"))
                            {
                                var27 = p_72726_1_.split("_", 3);
                                var28 = Block.getBlockById(Integer.parseInt(var27[1]));
                                var26 = Integer.parseInt(var27[2]);
                                var21 = (new EntityBlockDustFX(this.theWorld, p_72726_2_, p_72726_4_, p_72726_6_, p_72726_8_, p_72726_10_, p_72726_12_, var28, var26)).applyRenderColor(var26);
                            }
                        }
                    }

                    if (var21 != null)
                    {
                        this.mc.effectRenderer.addEffect((EntityFX)var21);
                    }

                    return (EntityFX)var21;
                }
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityCreate(Entity p_72703_1_) {}

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityDestroy(Entity p_72709_1_) {}

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists()
    {
        GLAllocation.deleteDisplayLists(this.glRenderListBase);
    }

    public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_, int p_82746_4_, int p_82746_5_)
    {
        Random var6 = this.theWorld.rand;

        switch (p_82746_1_)
        {
            case 1013:
            case 1018:
                if (this.mc.renderViewEntity != null)
                {
                    double var7 = (double)p_82746_2_ - this.mc.renderViewEntity.posX;
                    double var9 = (double)p_82746_3_ - this.mc.renderViewEntity.posY;
                    double var11 = (double)p_82746_4_ - this.mc.renderViewEntity.posZ;
                    double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
                    double var15 = this.mc.renderViewEntity.posX;
                    double var17 = this.mc.renderViewEntity.posY;
                    double var19 = this.mc.renderViewEntity.posZ;

                    if (var13 > 0.0D)
                    {
                        var15 += var7 / var13 * 2.0D;
                        var17 += var9 / var13 * 2.0D;
                        var19 += var11 / var13 * 2.0D;
                    }

                    if (p_82746_1_ == 1013)
                    {
                        this.theWorld.playSound(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
                    }
                    else if (p_82746_1_ == 1018)
                    {
                        this.theWorld.playSound(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_, int p_72706_3_, int p_72706_4_, int p_72706_5_, int p_72706_6_)
    {
        Random var7 = this.theWorld.rand;
        Block var8 = null;
        double var9;
        double var11;
        double var13;
        String var15;
        int var16;
        double var22;
        double var26;
        double var28;
        double var30;
        int var40;
        double var41;

        switch (p_72706_2_)
        {
            case 1000:
                this.theWorld.playSound((double)p_72706_3_, (double)p_72706_4_, (double)p_72706_5_, "random.click", 1.0F, 1.0F, false);
                break;

            case 1001:
                this.theWorld.playSound((double)p_72706_3_, (double)p_72706_4_, (double)p_72706_5_, "random.click", 1.0F, 1.2F, false);
                break;

            case 1002:
                this.theWorld.playSound((double)p_72706_3_, (double)p_72706_4_, (double)p_72706_5_, "random.bow", 1.0F, 1.2F, false);
                break;

            case 1003:
                if (Math.random() < 0.5D)
                {
                    this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                }
                else
                {
                    this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                }

                break;

            case 1004:
                this.theWorld.playSound((double)((float)p_72706_3_ + 0.5F), (double)((float)p_72706_4_ + 0.5F), (double)((float)p_72706_5_ + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F, false);
                break;

            case 1005:
                if (Item.getItemById(p_72706_6_) instanceof ItemRecord)
                {
                    this.theWorld.playRecord("records." + ((ItemRecord)Item.getItemById(p_72706_6_)).field_150929_a, p_72706_3_, p_72706_4_, p_72706_5_);
                }
                else
                {
                    this.theWorld.playRecord((String)null, p_72706_3_, p_72706_4_, p_72706_5_);
                }

                break;

            case 1007:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.ghast.charge", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1008:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.ghast.fireball", 10.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1009:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.ghast.fireball", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1010:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.zombie.wood", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1011:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.zombie.metal", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1012:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.zombie.woodbreak", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1014:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.wither.shoot", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1015:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.bat.takeoff", 0.05F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.zombie.infect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "mob.zombie.unfect", 2.0F, (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.theWorld.playSound((double)((float)p_72706_3_ + 0.5F), (double)((float)p_72706_4_ + 0.5F), (double)((float)p_72706_5_ + 0.5F), "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1021:
                this.theWorld.playSound((double)((float)p_72706_3_ + 0.5F), (double)((float)p_72706_4_ + 0.5F), (double)((float)p_72706_5_ + 0.5F), "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1022:
                this.theWorld.playSound((double)((float)p_72706_3_ + 0.5F), (double)((float)p_72706_4_ + 0.5F), (double)((float)p_72706_5_ + 0.5F), "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2000:
                int var34 = p_72706_6_ % 3 - 1;
                int var10 = p_72706_6_ / 3 % 3 - 1;
                var11 = (double)p_72706_3_ + (double)var34 * 0.6D + 0.5D;
                var13 = (double)p_72706_4_ + 0.5D;
                double var35 = (double)p_72706_5_ + (double)var10 * 0.6D + 0.5D;

                for (int var37 = 0; var37 < 10; ++var37)
                {
                    double var38 = var7.nextDouble() * 0.2D + 0.01D;
                    double var39 = var11 + (double)var34 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var10 * 0.5D;
                    var22 = var13 + (var7.nextDouble() - 0.5D) * 0.5D;
                    var41 = var35 + (double)var10 * 0.01D + (var7.nextDouble() - 0.5D) * (double)var34 * 0.5D;
                    var26 = (double)var34 * var38 + var7.nextGaussian() * 0.01D;
                    var28 = -0.03D + var7.nextGaussian() * 0.01D;
                    var30 = (double)var10 * var38 + var7.nextGaussian() * 0.01D;
                    this.spawnParticle("smoke", var39, var22, var41, var26, var28, var30);
                }

                return;

            case 2001:
                var8 = Block.getBlockById(p_72706_6_ & 4095);

                if (var8.getMaterial() != Material.air)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(var8.stepSound.func_150495_a()), (var8.stepSound.func_150497_c() + 1.0F) / 2.0F, var8.stepSound.func_150494_d() * 0.8F, (float)p_72706_3_ + 0.5F, (float)p_72706_4_ + 0.5F, (float)p_72706_5_ + 0.5F));
                }

                this.mc.effectRenderer.func_147215_a(p_72706_3_, p_72706_4_, p_72706_5_, var8, p_72706_6_ >> 12 & 255);
                break;

            case 2002:
                var9 = (double)p_72706_3_;
                var11 = (double)p_72706_4_;
                var13 = (double)p_72706_5_;
                var15 = "iconcrack_" + Item.getIdFromItem(Items.potionitem) + "_" + p_72706_6_;

                for (var16 = 0; var16 < 8; ++var16)
                {
                    this.spawnParticle(var15, var9, var11, var13, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                var16 = Items.potionitem.getColorFromDamage(p_72706_6_);
                float var17 = (float)(var16 >> 16 & 255) / 255.0F;
                float var18 = (float)(var16 >> 8 & 255) / 255.0F;
                float var19 = (float)(var16 >> 0 & 255) / 255.0F;
                String var20 = "spell";

                if (Items.potionitem.isEffectInstant(p_72706_6_))
                {
                    var20 = "instantSpell";
                }

                for (var40 = 0; var40 < 100; ++var40)
                {
                    var22 = var7.nextDouble() * 4.0D;
                    var41 = var7.nextDouble() * Math.PI * 2.0D;
                    var26 = Math.cos(var41) * var22;
                    var28 = 0.01D + var7.nextDouble() * 0.5D;
                    var30 = Math.sin(var41) * var22;
                    EntityFX var42 = this.doSpawnParticle(var20, var9 + var26 * 0.1D, var11 + 0.3D, var13 + var30 * 0.1D, var26, var28, var30);

                    if (var42 != null)
                    {
                        float var33 = 0.75F + var7.nextFloat() * 0.25F;
                        var42.setRBGColorF(var17 * var33, var18 * var33, var19 * var33);
                        var42.multiplyVelocity((float)var22);
                    }
                }

                this.theWorld.playSound((double)p_72706_3_ + 0.5D, (double)p_72706_4_ + 0.5D, (double)p_72706_5_ + 0.5D, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                var9 = (double)p_72706_3_ + 0.5D;
                var11 = (double)p_72706_4_;
                var13 = (double)p_72706_5_ + 0.5D;
                var15 = "iconcrack_" + Item.getIdFromItem(Items.ender_eye);

                for (var16 = 0; var16 < 8; ++var16)
                {
                    this.spawnParticle(var15, var9, var11, var13, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
                }

                for (double var36 = 0.0D; var36 < (Math.PI * 2D); var36 += 0.15707963267948966D)
                {
                    this.spawnParticle("portal", var9 + Math.cos(var36) * 5.0D, var11 - 0.4D, var13 + Math.sin(var36) * 5.0D, Math.cos(var36) * -5.0D, 0.0D, Math.sin(var36) * -5.0D);
                    this.spawnParticle("portal", var9 + Math.cos(var36) * 5.0D, var11 - 0.4D, var13 + Math.sin(var36) * 5.0D, Math.cos(var36) * -7.0D, 0.0D, Math.sin(var36) * -7.0D);
                }

                return;

            case 2004:
                for (var40 = 0; var40 < 20; ++var40)
                {
                    var22 = (double)p_72706_3_ + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    var41 = (double)p_72706_4_ + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    var26 = (double)p_72706_5_ + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle("smoke", var22, var41, var26, 0.0D, 0.0D, 0.0D);
                    this.theWorld.spawnParticle("flame", var22, var41, var26, 0.0D, 0.0D, 0.0D);
                }

                return;

            case 2005:
                ItemDye.func_150918_a(this.theWorld, p_72706_3_, p_72706_4_, p_72706_5_, p_72706_6_);
                break;

            case 2006:
                var8 = this.theWorld.getBlock(p_72706_3_, p_72706_4_, p_72706_5_);

                if (var8.getMaterial() != Material.air)
                {
                    double var21 = (double)Math.min(0.2F + (float)p_72706_6_ / 15.0F, 10.0F);

                    if (var21 > 2.5D)
                    {
                        var21 = 2.5D;
                    }

                    int var23 = (int)(150.0D * var21);

                    for (int var24 = 0; var24 < var23; ++var24)
                    {
                        float var25 = MathHelper.randomFloatClamp(var7, 0.0F, ((float)Math.PI * 2F));
                        var26 = (double)MathHelper.randomFloatClamp(var7, 0.75F, 1.0F);
                        var28 = 0.20000000298023224D + var21 / 100.0D;
                        var30 = (double)(MathHelper.cos(var25) * 0.2F) * var26 * var26 * (var21 + 0.2D);
                        double var32 = (double)(MathHelper.sin(var25) * 0.2F) * var26 * var26 * (var21 + 0.2D);
                        this.theWorld.spawnParticle("blockdust_" + Block.getIdFromBlock(var8) + "_" + this.theWorld.getBlockMetadata(p_72706_3_, p_72706_4_, p_72706_5_), (double)((float)p_72706_3_ + 0.5F), (double)((float)p_72706_4_ + 1.0F), (double)((float)p_72706_5_ + 0.5F), var30, var28, var32);
                    }
                }
        }
    }

    /**
     * Starts (or continues) destroying a block with given ID at the given coordinates for the given partially destroyed
     * value
     */
    public void destroyBlockPartially(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_, int p_147587_5_)
    {
        if (p_147587_5_ >= 0 && p_147587_5_ < 10)
        {
            DestroyBlockProgress var6 = (DestroyBlockProgress)this.damagedBlocks.get(Integer.valueOf(p_147587_1_));

            if (var6 == null || var6.getPartialBlockX() != p_147587_2_ || var6.getPartialBlockY() != p_147587_3_ || var6.getPartialBlockZ() != p_147587_4_)
            {
                var6 = new DestroyBlockProgress(p_147587_1_, p_147587_2_, p_147587_3_, p_147587_4_);
                this.damagedBlocks.put(Integer.valueOf(p_147587_1_), var6);
            }

            var6.setPartialBlockDamage(p_147587_5_);
            var6.setCloudUpdateTick(this.cloudTickCounter);
        }
        else
        {
            this.damagedBlocks.remove(Integer.valueOf(p_147587_1_));
        }
    }

    public void registerDestroyBlockIcons(IIconRegister p_94140_1_)
    {
        this.destroyBlockIcons = new IIcon[10];

        for (int var2 = 0; var2 < this.destroyBlockIcons.length; ++var2)
        {
            this.destroyBlockIcons[var2] = p_94140_1_.registerIcon("destroy_stage_" + var2);
        }
    }
}
