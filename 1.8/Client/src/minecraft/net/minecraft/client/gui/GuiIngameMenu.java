package net.minecraft.client.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;

public class GuiIngameMenu extends GuiScreen
{
    private int field_146445_a;
    private int field_146444_f;
    private static final String __OBFID = "CL_00000703";

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.field_146445_a = 0;
        this.buttonList.clear();
        byte var1 = -16;
        boolean var2 = true;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 160 + var1, I18n.format(EnumChatFormatting.DARK_GREEN + "Déconnexion", new Object[0])));

        if (!this.mc.isIntegratedServerRunning())
        {
            ((GuiButton)this.buttonList.get(0)).displayString = EnumChatFormatting.DARK_GREEN + "Déconnexion";
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 130 + var1, I18n.format(EnumChatFormatting.GOLD + "Revenir en jeu", new Object[0])));
        

        this.buttonList.add(new GuiButton(2, this.width / 2 - 200, this.height / 4 + 66 + var1, 98, 20, I18n.format(EnumChatFormatting.BLUE + "Site", new Object[0])));
        
        this.buttonList.add( new GuiButton(3, this.width / 2 + 102, this.height / 4 + 66 + var1, 98, 20, I18n.format(EnumChatFormatting.DARK_PURPLE + "Wiki", new Object[0])));
        
        this.buttonList.add(new GuiButton(0, this.width / 2 - 200, this.height / 4 + 96 + var1, 98, 20, I18n.format(EnumChatFormatting.GREEN + "Options", new Object[0])));
        
        this.buttonList.add( new GuiButton(7, this.width / 2 + 102, this.height / 4 + 96 + var1, 98, 20, I18n.format(EnumChatFormatting.YELLOW + "Teamspeak", new Object[0])));
        
        this.buttonList.add(new GuiButton(5, this.width / 2 - 180, this.height / 4 + 28 + var1, 98, 20, I18n.format(EnumChatFormatting.DARK_AQUA +"Achivements", new Object[0])));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 82, this.height / 4 + 28 + var1, 98, 20, I18n.format(EnumChatFormatting.RED +"Statistiques", new Object[0])));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);
                this.mc.displayGuiScreen(new GuiMainMenu());
                break;
            case 2:
            	try
                {
                    URI var2 = new URI("http://alkazia.fr");

                    if (Desktop.isDesktopSupported())
                    {
                    	Desktop.getDesktop().browse(var2);
                    }
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            	break;
            case 3:
            	try
                {
                    URI var2 = new URI("http://alkazia.fr/wiki/");

                    if (Desktop.isDesktopSupported())
                    {
                    	Desktop.getDesktop().browse(var2);
                    }
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            	break;
            case 4:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 7:
            	try
                {
                    URI var2 = new URI("ts3server://ts.alkazia.net?nickname=" + mc.getSession().getUsername());

                    if (Desktop.isDesktopSupported())
                    {
                    	Desktop.getDesktop().browse(var2);
                    }
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * Draws the entity to the screen. Args: xPos, yPos, scale, mouseX, mouseY, entityLiving
     */
    public static void drawEntityOnScreen(int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)p_147046_0_, (float)p_147046_1_, 50.0F);
        GlStateManager.scale((float)(-p_147046_2_), (float)p_147046_2_, (float)p_147046_2_);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float var6 = p_147046_5_.renderYawOffset;
        float var7 = p_147046_5_.rotationYaw;
        float var8 = p_147046_5_.rotationPitch;
        float var9 = p_147046_5_.prevRotationYawHead;
        float var10 = p_147046_5_.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        p_147046_5_.renderYawOffset = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 20.0F;
        p_147046_5_.rotationYaw = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 40.0F;
        p_147046_5_.rotationPitch = -((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F;
        p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
        p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager var11 = Minecraft.getMinecraft().getRenderManager();
        var11.func_178631_a(180.0F);
        var11.func_178633_a(false);
        var11.renderEntityWithPosYaw(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        var11.func_178633_a(true);
        p_147046_5_.renderYawOffset = var6;
        p_147046_5_.rotationYaw = var7;
        p_147046_5_.rotationPitch = var8;
        p_147046_5_.prevRotationYawHead = var9;
        p_147046_5_.rotationYawHead = var10;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.func_179090_x();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.field_146444_f;
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        drawEntityOnScreen(this.width / 2, height / 2 + 30, 35, (float)((this.width - 176) / 2 + height / 8) - mouseX, (float)((this.height - 166) / 2 + 75) - mouseY, this.mc.thePlayer);
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "- Alkazia Menu -", this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
