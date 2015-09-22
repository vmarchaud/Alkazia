package net.minecraft.client.gui;

import java.net.ConnectException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonServer extends GuiButton

{
    private static int F = 0;
    OldServerPinger info = new OldServerPinger();
    GuiMainMenu gui = new GuiMainMenu();
    protected static final ResourceLocation field_146122_a = new ResourceLocation("textures/gui/widgets.png");
    ServerData here;
    
    // Alkazia - Setup button perso
    

    public GuiButtonServer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString, String name, String ip)
    {
        super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString);
        field_146120_f = 200;
        field_146121_g = 20;
        field_146120_f = paramInt4;
        field_146121_g = paramInt5;
        
        here = new ServerData("Alka" + name, ip);
        try {
        	//if(gui.readFile("http://launcher.alkazia.net/startrender.txt").contains("true")) {
        		//info.func_147224_a(here);
        	//}
		}  catch (Exception e) {
			here.populationInfo = EnumChatFormatting.DARK_RED + "Serveur OFF";
			e.printStackTrace();
		}
     }

    protected int getHoverState(boolean p_146114_1_)
    {
        byte var2 = 1;

        if (!this.enabled)
        {
            var2 = 0;
        }
        else if (p_146114_1_)
        {
            var2 = 2;
        }

        return var2;
    }


    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        if (this.field_146125_m)
        {
            FontRenderer var4 = p_146112_1_.fontRenderer;
            p_146112_1_.getTextureManager().bindTexture(field_146122_a);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = p_146112_2_ >= this.field_146128_h && p_146112_3_ >= this.field_146129_i && p_146112_2_ < this.field_146128_h + this.field_146120_f && p_146112_3_ < this.field_146129_i + this.field_146121_g;
            int var5 = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, 0, 46 + var5 * 20, this.field_146120_f / 2, this.field_146121_g);
            this.drawTexturedModalRect(this.field_146128_h + this.field_146120_f / 2, this.field_146129_i, 200 - this.field_146120_f / 2, 46 + var5 * 20, this.field_146120_f / 2, this.field_146121_g);
            this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
            int var6 = 14737632;

            if (!this.enabled)
            {
                var6 = 10526880;
            }
            else if (this.field_146123_n)
            {
                var6 = 16777120;
            }

            if(var5 == 2)
            {
            	this.drawCenteredString(var4, here.populationInfo, this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, var6);
            }
            else
            {
            	this.drawCenteredString(var4, this.displayString, this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, var6);
            }
        }
    }   
}