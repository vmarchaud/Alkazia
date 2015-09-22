package net.minecraft.client.gui;

import java.net.ConnectException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonAlkazia extends GuiButton

{
    private static int F = 0;
    GuiMainMenu gui = new GuiMainMenu();
    // Alkazia - Setup button perso

    public static final ResourceLocation button = new ResourceLocation("textures/gui/button.png");

    public GuiButtonAlkazia(int buttonId, int x, int y, String buttonText)
    {
        super(buttonId, x, y, 200, 20, buttonText);
    }

    protected int getHoverState(boolean paramBoolean)

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

    public void drawButton(Minecraft mc, int mouseX, int mouseY)

    {
        if (!visible)
        {
            return;
        }
        FontRenderer var4 = mc.fontRendererObj;
        mc.getTextureManager().bindTexture(button);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int var5 = this.getHoverState(this.hovered);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 20, 106 + var5 * 20, this.width / 2, this.height);
        this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 220 - this.width / 2, 106 + var5 * 20, this.width / 2, this.height);
        this.mouseDragged(mc, mouseX, mouseY);
        int j = 14737632;

        if (!enabled)
        {
            j = -6250336;
        }
        
        var4.FONT_HEIGHT = 10;
        
        if(var5 == 6)
        {
        	this.drawCenteredString(var4, EnumChatFormatting.GREEN  + "Rejoins nous dés maintenant", this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
        else
        {
        	this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }
}