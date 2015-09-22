package net.minecraft.client.gui;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


import net.minecraft.client.Minecraft.serverType;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiIngameMenu extends GuiScreen
{
    private int field_146445_a;
    private int field_146444_f;
    private static final String __OBFID = "CL_00000703";
    private static final ResourceLocation head = new ResourceLocation("textures/gui/head.png");

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
    	if(mc.server != null && mc.server == serverType.PVP) {
    		mc.querringLevel();
    		mc.querringFacLevel();
    	}
    	
        this.field_146445_a = 0;
        this.buttonList.clear();
        byte var1 = -16;
        boolean var2 = true;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 160 + var1, I18n.format(EnumChatFormatting.DARK_GREEN + "Déconnexion", new Object[0])));

        if (!this.mc.isIntegratedServerRunning())
        {
            ((GuiButton)this.buttonList.get(0)).displayString = I18n.format("Déconnexion", new Object[0]);
        }
        // Alkazia - echap menu
        
        
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 130 + var1, I18n.format(EnumChatFormatting.GOLD + "Revenir en jeu", new Object[0])));
        

        this.buttonList.add(new GuiButton(2, this.width / 2 - 200, this.height / 4 + 66 + var1, 98, 20, I18n.format(EnumChatFormatting.BLUE + "Site", new Object[0])));
        
        this.buttonList.add( new GuiButton(3, this.width / 2 + 102, this.height / 4 + 66 + var1, 98, 20, I18n.format(EnumChatFormatting.DARK_PURPLE + "Wiki", new Object[0])));
        
        //this.buttonList.add(new GuiButton(10, this.width / 2 - ((mc.getSession().getUsername().length() * 8 + 10) / 2), this.height / 4 + 50 , mc.getSession().getUsername().length() * 8 + 10, 20, I18n.format(EnumChatFormatting.UNDERLINE + mc.getSession().getUsername(), new Object[0])));
        
        this.buttonList.add(new GuiButton(0, this.width / 2 - 200, this.height / 4 + 96 + var1, 98, 20, I18n.format(EnumChatFormatting.GREEN + "Options", new Object[0])));
        
        this.buttonList.add( new GuiButton(7, this.width / 2 + 102, this.height / 4 + 96 + var1, 98, 20, I18n.format(EnumChatFormatting.YELLOW + "Teamspeak", new Object[0])));
        
        this.buttonList.add(new GuiButton(5, this.width / 2 - 180, this.height / 4 + 28 + var1, 98, 20, I18n.format(EnumChatFormatting.DARK_AQUA +"Achivements", new Object[0])));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 82, this.height / 4 + 28 + var1, 98, 20, I18n.format(EnumChatFormatting.RED +"Statistiques", new Object[0])));
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        switch (p_146284_1_.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                p_146284_1_.enabled = false;
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
            default:
                break;

            case 4:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.func_146107_m()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.func_146107_m()));
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
            case 10:
            	try
                {
                    URI var2 = new URI("http://alkazia.fr/membre/" + mc.getSession().getUsername());

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
        }
    }
    
    public static void func_147046_a(int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)p_147046_0_, (float)p_147046_1_, 50.0F);
        GL11.glScalef((float)(-p_147046_2_), (float)p_147046_2_, (float)p_147046_2_);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float var6 = p_147046_5_.renderYawOffset;
        float var7 = p_147046_5_.rotationYaw;
        float var8 = p_147046_5_.rotationPitch;
        float var9 = p_147046_5_.prevRotationYawHead;
        float var10 = p_147046_5_.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        p_147046_5_.renderYawOffset = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 20.0F;
        p_147046_5_.rotationYaw = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 40.0F;
        p_147046_5_.rotationPitch = -((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F;
        p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
        p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
        GL11.glTranslatef(0.0F, p_147046_5_.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.func_147940_a(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        p_147046_5_.renderYawOffset = var6;
        p_147046_5_.rotationYaw = var7;
        p_147046_5_.rotationPitch = var8;
        p_147046_5_.prevRotationYawHead = var9;
        p_147046_5_.rotationYawHead = var10;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    
    public void drawTextureWithOptionalSize(int x, int y, int u, int v, int width, int height, int uSize, int vSize)
    {
    	float scaledX = (float)1/uSize;
    	float scaledY = (float)1/vSize;
    	Tessellator tessellator = Tessellator.instance;
    	tessellator.startDrawingQuads();
    	tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(u + 0) * scaledX), (double)((float)(v + height) * scaledY));
    	tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(u + width) * scaledX), (double)((float)(v + height) * scaledY));
    	tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(u + width) * scaledX), (double)((float)(v + 0) * scaledY));
    	tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(u + 0) * scaledX), (double)((float)(v + 0) * scaledY));
    	tessellator.draw();
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
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        func_147046_a(this.width / 2, height / 2, 35, (float)((this.width - 176) / 2 + height / 8) - par1, (float)((this.height - 166) / 2 + 75) - par2, this.mc.thePlayer);
        
        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GOLD + "- Alkazia Menu -", this.width / 2, 40, 16777215);
        
        
        if(mc.server != null && mc.server == serverType.PVP) {
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "- " + mc.getSession().getUsername() + " -", 43, this.height - 50, 16777215);
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "Niveau " + mc.getPlayer().getLevel(), 43, this.height - 35, 16777215);
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "Exp : " + mc.getPlayer().getXP() + "/" + getXPNeededFor(mc.getPlayer().getLevel()), 45, this.height - 20, 16777215);
	        
	
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "- " + mc.getPlayer().getFacName() + " -", this.width - 43, this.height - 50, 16777215);
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "Niveau " + mc.getPlayer().getfacLevel(), this.width - 40, this.height - 35, 16777215);
	        this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.GREEN + "Exp : " + mc.getPlayer().getfacXP() + "/" + getXPNeededForFac(mc.getPlayer().getfacLevel()), this.width - 40, this.height - 20, 16777215);
    	}
        super.drawScreen(par1, par2, par3);
    }
    
    public int getXPNeededFor(int level) {
    	if(level == 0) {
    		return 0;
    	}
    	else if(level ==1) {
			return 35;
		}
		else if(level ==2) {
			return 40;
		}
		else if(level ==3) {
			return 46;
		}
		else if(level ==4) {
			return 53;
		}
		else if(level ==5) {
			return 61;
		}
		else if(level ==6) {
			return 71;
		}
		else if(level ==7) {
			return 82;
		}
		else if(level ==8) {
			return 95;
		}
		else if(level ==9) {
			return 110;
		}
		else if(level ==10) {
			return 128;
		}
		else if(level ==11) {
			return 149;
		}
		else if(level ==12) {
			return 173;
		}
		else if(level ==13) {
			return 201;
		}
		else if(level ==14) {
			return 234;
		}
		else if(level ==15) {
			return 273;
		}
		else if(level ==16) {
			return 318;
		}
		else if(level ==17) {
			return 371;
		}
		else if(level ==18) {
			return 432;
		}
		else if(level ==19) {
			return 504;
		}
		else if(level ==20) {
			return 588;
		}
		else if(level ==21) {
			return 686;
		}
		else if(level ==22) {
			return 800;
		}
		else if(level ==23) {
			return 933;
		}
		else if(level ==24) {
			return 1088;
		}
		else if(level ==25) {
			return 1269;
		}
		else if(level ==26) {
			return 1480;
		}
		else if(level ==27) {
			return 1726;
		}
		else if(level ==28) {
			return 2013;
		}
		else if(level ==29) {
			return 2348;
		}
		else if(level ==30) {
			return 2739;
		}
		else if(level ==31) {
			return 3195;
		}
		else if(level ==32) {
			return 3727;
		}
		else if(level ==33) {
			return 4348;
		}
		else if(level ==34) {
			return 5072;
		}
		else if(level ==35) {
			return 5917;
		}
		else if(level ==36) {
			return 6903;
		}
		else if(level ==37) {
			return 8053;
		}
		else if(level ==38) {
			return 9395;
		}
		else if(level ==39) {
			return 10960;
		}
		else if(level ==40) {
			return 11786;
		}
		else if(level ==41) {
			return 12917;
		}
		else if(level ==42) {
			return 14403;
		}
		else if(level ==43) {
			return 17303;
		}
		else if(level ==44) {
			return 20686;
		}
		else if(level ==45) {
			return 23633;
		}
		else if(level ==46) {
			return 27238;
		}
		else if(level ==47) {
			return 31611;
		}
		else if(level ==48) {
			return 43879;
		}
		else if(level ==49) {
			return 47192;
		}
		else if(level ==50) {
			return 52724;
		}
		else if(level ==51) {
			return 58678;
		}
		else if(level ==52) {
			return 64291;
		}
		else if(level ==53) {
			return 71839;
		}
		else if(level ==54) {
			return 79645;
		}
		else if(level ==55) {
			return 87085;
		}
		else if(level ==56) {
			return 99599;
		}
		else if(level ==57) {
			return 110698;
		}
		else if(level ==58) {
			return 120981;
		}
		else if(level ==59) {
			return 130144;
		}
		else if(level ==60) {
			return 141001;
		}
		else if(level ==61) {
			return 152501;
		}
		else if(level ==62) {
			return 164751;
		}
		else if(level ==63) {
			return 176042;
		}
		else if(level ==64) {
			return 188882;
		}
		else if(level ==65) {
			return 200029;
		}
		else if(level ==66) {
			return 224533;
		}
		else if(level ==67) {
			return 248788;
		}
		else if(level ==68) {
			return 272586;
		}
		else if(level ==69) {
			return 294183;
		}
		else if(level ==70) {
			return 315380;
		}
		else if(level ==71) {
			return 338610;
		}
		else if(level ==72) {
			return 358045;
		}
		else if(level ==73) {
			return 379719;
		}
		else if(level ==74) {
			return 399672;
		}
		else if(level ==75) {
			return 429117;
		}
		else if(level ==76) {
			return 454636;
		}
		else if(level ==77) {
			return 486408;
		}
		else if(level ==78) {
			return 510476;
		}
		else if(level ==79) {
			return 535055;
		}
		else if(level ==80) {
			return 572897;
		}
		else if(level ==81) {
			return 600713;
		}
		else if(level ==82) {
			return 630665;
		}
		else if(level ==83) {
			return 670942;
		}
		else if(level ==84) {
			return 700432;
		}
		else if(level ==85) {
			return 725846;
		}
		else if(level ==86) {
			return 752654;
		}
		else if(level ==87) {
			return 783645;
		}
		else if(level ==88) {
			return 805975;
		}
		else if(level ==89) {
			return 824456;
		}
		else if(level ==90) {
			return 853032;
		}
		else if(level ==91) {
			return 876870;
		}
		else if(level ==92) {
			return 899848;
		}
		else if(level ==93) {
			return 925851;
		}
		else if(level ==94) {
			return 945261;
		}
		else if(level ==95) {
			return 960446;
		}
		else if(level ==96) {
			return 984316;
		}
		else if(level ==97) {
			return 83682363;
		}
		else if(level ==98) {
			return 97629423;
		}
		else if(level ==99) {
			return 1000000;
		}
		else if(level ==100) {
			return 1000000000;
		}
		return 0;

	}
    
    public int getXPNeededForFac(int level) {
    	 if(level == 1) {
    		return 100; }
    		else if(level == 2) {
    		return 111; }
    		else if(level == 3) {
    		return 123; }
    		else if(level == 4) {
    		return 136; }
    		else if(level == 5) {
    		return 150; }
    		else if(level == 6) {
    		return 166; }
    		else if(level == 7) {
    		return 184; }
    		else if(level == 8) {
    		return 204; }
    		else if(level == 9) {
    		return 226; }
    		else if(level == 10) {
    		return 250; }
    		else if(level == 11) {
    		return 277; }
    		else if(level == 12) {
    		return 307; }
    		else if(level == 13) {
    		return 340; }
    		else if(level == 14) {
    		return 377; }
    		else if(level == 15) {
    		return 418; }
    		else if(level == 16) {
    		return 463; }
    		else if(level == 17) {
    		return 513; }
    		else if(level == 18) {
    		return 569; }
    		else if(level == 19) {
    		return 631; }
    		else if(level == 20) {
    		return 700; }
    		else if(level == 21) {
    		return 777; }
    		else if(level == 22) {
    		return 862; }
    		else if(level == 23) {
    		return 956; }
    		else if(level == 24) {
    		return 1061; }
    		else if(level == 25) {
    		return 1177; }
    		else if(level == 26) {
    		return 1306; }
    		else if(level == 27) {
    		return 1449; }
    		else if(level == 28) {
    		return 1608; }
    		else if(level == 29) {
    		return 1784; }
    		else if(level == 30) {
    		return 1980; }
    		else if(level == 31) {
    		return 2197; }
    		else if(level == 32) {
    		return 2438; }
    		else if(level == 33) {
    		return 2706; }
    		else if(level == 34) {
    		return 3003; }
    		else if(level == 35) {
    		return 3333; }
    		else if(level == 36) {
    		return 3699; }
    		else if(level == 37) {
    		return 4105; }
    		else if(level == 38) {
    		return 4556; }
    		else if(level == 39) {
    		return 5057; }
    		else if(level == 40) {
    		return 5613; }
    		else if(level == 41) {
    		return 6230; }
    		else if(level == 42) {
    		return 6915; }
    		else if(level == 43) {
    		return 7675; }
    		else if(level == 44) {
    		return 8519; }
    		else if(level == 45) {
    		return 9456; }
    		else if(level == 46) {
    		return 10496; }
    		else if(level == 47) {
    		return 11650; }
    		else if(level == 48) {
    		return 12931; }
    		else if(level == 49) {
    		return 14353; }
    		else if(level == 50) {
    		return 15931; }
    		else if(level == 51) {
    		return 17683; }
    		else if(level == 52) {
    		return 19628; }
    		else if(level == 53) {
    		return 21787; }
    		else if(level == 54) {
    		return 24183; }
    		else if(level == 55) {
    		return 26843; }
    		else if(level == 56) {
    		return 29795; }
    		else if(level == 57) {
    		return 33072; }
    		else if(level == 58) {
    		return 36709; }
    		else if(level == 59) {
    		return 40746; }
    		else if(level == 60) {
    		return 45228; }
    		else if(level == 61) {
    		return 50203; }
    		else if(level == 62) {
    		return 55725; }
    		else if(level == 63) {
    		return 61854; }
    		else if(level == 64) {
    		return 68657; }
    		else if(level == 65) {
    		return 76209; }
    		else if(level == 66) {
    		return 84591; }
    		else if(level == 67) {
    		return 93896; }
    		else if(level == 68) {
    		return 104224; }
    		else if(level == 69) {
    		return 115688; }
    		else if(level == 70) {
    		return 128413; }
    		else if(level == 71) {
    		return 142538; }
    		else if(level == 72) {
    		return 158217; }
    		else if(level == 73) {
    		return 175620; }
    		else if(level == 74) {
    		return 194938; }
    		else if(level == 75) {
    		return 216381; }
    		else if(level == 76) {
    		return 240182; }
    		else if(level == 77) {
    		return 266602; }
    		else if(level == 78) {
    		return 295928; }
    		else if(level == 79) {
    		return 328480; }
    		else if(level == 80) {
    		return 364612; }
    		else if(level == 81) {
    		return 404719; }
    		else if(level == 82) {
    		return 449238; }
    		else if(level == 83) {
    		return 498654; }
    		else if(level == 84) {
    		return 553505; }
    		else if(level == 85) {
    		return 614390; }
    		else if(level == 86) {
    		return 681972; }
    		else if(level == 87) {
    		return 756988; }
    		else if(level == 88) {
    		return 840256; }
    		else if(level == 89) {
    		return 932684; }
    		else if(level == 90) {
    		return 1035279; }
    		else if(level == 91) {
    		return 1149159; }
    		else if(level == 92) {
    		return 1275566; }
    		else if(level == 93) {
    		return 1415878; }
    		else if(level == 94) {
    		return 1571624; }
    		else if(level == 95) {
    		return 1744502; }
    		else if(level == 96) {
    		return 1936397; }
    		else if(level == 97) {
    		return 2149400; }
    		else if(level == 98) {
    		return 2385834; }
    		else if(level == 99) {
    		return 2648275; }
    		else if(level == 100) {
        		return 2939585; }

		return 0;

	}
}
