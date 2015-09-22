package net.minecraft.client.gui;

import java.net.ConnectException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonAlkazia extends GuiButton

{
    private static int F = 0;
    public static final ResourceLocation button = new ResourceLocation("textures/gui/button.png");
    GuiMainMenu gui = new GuiMainMenu();
    //ServerData reborn = new ServerData("Alkazia", "37.187.147.216:26662" );
    OldServerPinger info = new OldServerPinger();
    // Alkazia - Setup button perso
    String co = gui.readFile("http://alkazia.net/scripts/all_connect.php");
    

    public GuiButtonAlkazia(int paramInt1, int paramInt2, int paramInt3, String paramString)

    {
        this(paramInt1, paramInt2, paramInt3, 200, 20, paramString);
        
    }

    public GuiButtonAlkazia(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)

    {
        super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString);
        field_146120_f = 200;
        field_146121_g = 20;
        field_146120_f = paramInt4;
        field_146121_g = paramInt5;
        
			try {
				//info.func_147224_a(reborn);
			} catch (Exception e) {
				e.printStackTrace();
			}
     }

    protected int jdMethod_for(boolean paramBoolean)

    {
        int i = 0;
        F += 1;

        if (F < 5)
        {
            i = 0;
        }
        else if (F < 10)
        {
            i = 1;
        }
        else if (F < 15)
        {
            i = 2;
        }
        else if (F < 20)
        {
            i = 3;
        }
        else if (F < 25)
        {
            i = 4;
        }
        else if (F < 30)
        {
            i = 5;
        }
        else if (F < 35)
        {
            i = 4;
        }
        else if (F < 40)
        {
            i = 3;
        }
        else if (F < 45)
        {
            i = 2;
        }
        else if (F < 50)
        {
            i = 1;
        }
        else if (F > 55)
        {
            i = 0;
            F = 0;
        }

        if (!enabled)
        {
            i = -5;
        }
        else if (paramBoolean)
        {
            i = 6;
            F = 0;
        }

        return i;
    }

    public void drawButton(Minecraft paramMinecraft, int paramInt1, int paramInt2)

    {
        if (!field_146125_m)
        {
            return;
        }
        FontRenderer fontrenderer = paramMinecraft.fontRenderer;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        paramMinecraft.getTextureManager().bindTexture(button);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean bool = (paramInt1 >= field_146128_h) && (paramInt2 >= field_146129_i) && (paramInt1 < field_146128_h + field_146120_f) && (paramInt2 < field_146129_i + field_146121_g);
        int i = jdMethod_for(bool);
        drawTexturedModalRect(field_146128_h, field_146129_i, 20, 106 + i * 20, field_146120_f / 2, field_146121_g);
        drawTexturedModalRect(field_146128_h + field_146120_f / 2, field_146129_i, 220 - field_146120_f / 2, 106 + i * 20, field_146120_f / 2, field_146121_g);
        mouseDragged(paramMinecraft, paramInt1, paramInt2);
        int j = 14737632;

        if (!enabled)
        {
            j = -6250336;
        }
        
        fontrenderer.FONT_HEIGHT = 10;
        if(i == 6)
        {
        	this.drawCenteredString(fontrenderer, EnumChatFormatting.GREEN  + co + " joueurs connectés", this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, j);
        }
        else
        {
        	this.drawCenteredString(fontrenderer, this.displayString, this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, j);
        }
    }
}