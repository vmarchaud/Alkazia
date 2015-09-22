package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityIronChest;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntityIronChestRenderer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation field_147504_g = new ResourceLocation("textures/entity/chest/ironchest.png");
    private ModelChest field_147510_h = new ModelChest();

    public TileEntityIronChestRenderer() { }

    public void renderTileEntityAt(TileEntityIronChest p_147502_1_, double p_147502_2_, double p_147502_4_, double p_147502_6_, float p_147502_8_)
    {
        int var9;

        if (!p_147502_1_.hasWorldObj())
        {
            var9 = 0;
        }
        else
        {
            Block var10 = p_147502_1_.getBlockType();
            var9 = p_147502_1_.getBlockMetadata();
        }

        if (p_147502_1_.field_145992_i == null && p_147502_1_.field_145991_k == null)
        {
             
             ModelChest var14 = this.field_147510_h;
            this.bindTexture(field_147504_g);

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float)p_147502_2_, (float)p_147502_4_ + 1.0F, (float)p_147502_6_ + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short var11 = 0;

            if (var9 == 2)
            {
                var11 = 180;
            }

            if (var9 == 3)
            {
                var11 = 0;
            }

            if (var9 == 4)
            {
                var11 = 90;
            }

            if (var9 == 5)
            {
                var11 = -90;
            }

            if (var9 == 2 && p_147502_1_.field_145990_j != null)
            {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (var9 == 5 && p_147502_1_.field_145988_l != null)
            {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef((float)var11, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float var12 = p_147502_1_.field_145986_n + (p_147502_1_.field_145989_m - p_147502_1_.field_145986_n) * p_147502_8_;
            float var13;

            if (p_147502_1_.field_145992_i != null)
            {
                var13 = p_147502_1_.field_145992_i.field_145986_n + (p_147502_1_.field_145992_i.field_145989_m - p_147502_1_.field_145992_i.field_145986_n) * p_147502_8_;

                if (var13 > var12)
                {
                    var12 = var13;
                }
            }

            if (p_147502_1_.field_145991_k != null)
            {
                var13 = p_147502_1_.field_145991_k.field_145986_n + (p_147502_1_.field_145991_k.field_145989_m - p_147502_1_.field_145991_k.field_145986_n) * p_147502_8_;

                if (var13 > var12)
                {
                    var12 = var13;
                }
            }

            var12 = 1.0F - var12;
            var12 = 1.0F - var12 * var12 * var12;
            var14.chestLid.rotateAngleX = -(var12 * (float)Math.PI / 2.0F);
            var14.renderAll();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.renderTileEntityAt((TileEntityIronChest)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}
