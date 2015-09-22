package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityIronChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityIronChestRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/ironchest.png");
    private ModelChest simpleChest = new ModelChest();
    // Alkazia - Iron Chest

    public void func_180538_a(TileEntityIronChest p_180538_1_, double p_180538_2_, double p_180538_4_, double p_180538_6_, float p_180538_8_, int p_180538_9_)
    {
        int var10;

        if (!p_180538_1_.hasWorldObj())
        {
            var10 = 0;
        }
        else
        {
            Block var11 = p_180538_1_.getBlockType();
            var10 = p_180538_1_.getBlockMetadata();

        }

            ModelChest var15 = this.simpleChest;

                if (p_180538_9_ >= 0)
                {
                    this.bindTexture(DESTROY_STAGES[p_180538_9_]);
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(4.0F, 4.0F, 1.0F);
                    GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                    GlStateManager.matrixMode(5888);
                }
                else
                {
                    this.bindTexture(textureNormal);
                }
           

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();

            if (p_180538_9_ < 0)
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GlStateManager.translate((float)p_180538_2_, (float)p_180538_4_ + 1.0F, (float)p_180538_6_ + 1.0F);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            short var12 = 0;

            if (var10 == 2)
            {
                var12 = 180;
            }

            if (var10 == 3)
            {
                var12 = 0;
            }

            if (var10 == 4)
            {
                var12 = 90;
            }

            if (var10 == 5)
            {
                var12 = -90;
            }

           

            GlStateManager.rotate((float)var12, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            float var13 = p_180538_1_.prevLidAngle + (p_180538_1_.lidAngle - p_180538_1_.prevLidAngle) * p_180538_8_;
            float var14;

            var13 = 1.0F - var13;
            var13 = 1.0F - var13 * var13 * var13;
            var15.chestLid.rotateAngleX = -(var13 * (float)Math.PI / 2.0F);
            var15.renderAll();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (p_180538_9_ >= 0)
            {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
        
    }

    public void renderTileEntityAt(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_)
    {
        this.func_180538_a((TileEntityIronChest)p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
    }
}
