package net.minecraft.client.gui;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.ExceptionRetryCall;
import net.minecraft.client.mco.GuiScreenClientOutdated;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen
{
    private static final AtomicInteger field_146973_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    /** The RNG used by the Main Menu Screen. */
    private static final Random rand = new Random();

    /** Counts the number of screen updates. */
    private float updateCounter;

    /** The splash message. */
    private String splashText;
    private GuiButton buttonResetDemo;

    /** Timer used to rotate the panorama, increases every tick. */
    private int panoramaTimer;

    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private boolean field_96141_q = true;
    private static boolean field_96140_r;
    private static boolean field_96139_s;
    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;
    private String field_104024_v;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");

    /** An array of all the paths to the panorama pictures. */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation field_110351_G;
    private GuiButton minecraftRealmsButton;
    private static final String __OBFID = "CL_00001154";
    
    /**
     * Alkazia - import ressource
     */
    private static final ResourceLocation logo = new ResourceLocation("textures/gui/logo.png");
    private static final ResourceLocation background = new ResourceLocation("textures/gui/background.png");
    private int textPosition = 440;
    private String text = "Mise-à-jour 3.1 : Résolutions des bugs majeurs de la nouvelle version.";
    
    

    public GuiMainMenu()
    {
        this.field_146972_A = field_96138_a;
        this.splashText = "missingno";
        BufferedReader var1 = null;

        try
        {
            ArrayList var2 = new ArrayList();
            var1 = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String var3;

            while ((var3 = var1.readLine()) != null)
            {
                var3 = var3.trim();

                if (!var3.isEmpty())
                {
                    var2.add(var3);
                }
            }

            if (!var2.isEmpty())
            {
                do
                {
                    this.splashText = (String)var2.get(rand.nextInt(var2.size()));
                }
                while (this.splashText.hashCode() == 125780783);
            }
        }
        catch (IOException var12)
        {
            ;
        }
        finally
        {
            if (var1 != null)
            {
                try
                {
                    var1.close();
                }
                catch (IOException var11)
                {
                    ;
                }
            }
        }

        this.updateCounter = rand.nextFloat();
        this.field_92025_p = "";

    }
    
   

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.panoramaTimer;
        
        if (textPosition < 0 - mc.fontRenderer.getStringWidth(text))
    	{
    	    textPosition = width / 2 + 200;
    	}
    	textPosition -= 3; 
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.field_110351_G = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);

        boolean var2 = true;
        int var3 = this.height / 4 + 48;

       
            this.addSingleplayerMultiplayerButtons(var3, 24);
        

        this.func_130020_g();

        // Alkazia - Setup button
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, var3 + 72 + 12, 98, 20, I18n.format("menu.quit")));
        
        
        this.buttonList.add(new GuiButton(18, this.width - 87, height - 17, 90, 20,I18n.format(EnumChatFormatting.GOLD + "by ThisIsMac.fr")));
        
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, var3 + 72 + 12));
        Object var4 = this.field_104025_t;

        
        synchronized (this.field_104025_t)
        {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
            int var5 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - var5) / 2;
            this.field_92021_u = ((GuiButton)this.buttonList.get(0)).field_146129_i - 24;
            this.field_92020_v = this.field_92022_t + var5;
            this.field_92019_w = this.field_92021_u + 24;
        }
    }

    private void func_130020_g()
    {
        if (this.field_96141_q)
        {
            if (!field_96140_r)
            {
                field_96140_r = true;
                (new Thread("MCO Availability Checker #" + field_146973_f.incrementAndGet())
                {
                    private static final String __OBFID = "CL_00001155";
                    public void run()
                    {
                        Session var1 = GuiMainMenu.this.mc.getSession();
                        McoClient var2 = new McoClient(var1.getSessionID(), var1.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());
                        boolean var3 = false;

                        for (int var4 = 0; var4 < 3; ++var4)
                        {
                            try
                            {
                                Boolean var5 = var2.func_148687_b();

                                if (var5.booleanValue())
                                {
                                    GuiMainMenu.this.func_130022_h();
                                }

                                GuiMainMenu.field_96139_s = var5.booleanValue();
                            }
                            catch (ExceptionRetryCall var7)
                            {
                                var3 = true;
                            }
                            catch (ExceptionMcoService var8)
                            {
                                GuiMainMenu.logger.error("Couldn\'t connect to Realms");
                            }
                            catch (IOException var9)
                            {
                                GuiMainMenu.logger.error("Couldn\'t parse response connecting to Realms");
                            }

                            if (!var3)
                            {
                                break;
                            }

                            try
                            {
                                Thread.sleep(10000L);
                            }
                            catch (InterruptedException var6)
                            {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }).start();
            }
            else if (field_96139_s)
            {
                this.func_130022_h();
            }
        }
    }

    private void func_130022_h()
    {
        this.minecraftRealmsButton.field_146125_m = true;
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int par1, int par2)
    {
    	this.buttonList.add(new GuiButton(1, this.width / 2 -100 , par1 + 60, 200, 20, I18n.format(EnumChatFormatting.GREEN + "Singleplayer")));
    	//this.buttonList.add(new GuiButtonServer(20, this.width / 2 - 200, par1 - 15, 110, 20, I18n.format(EnumChatFormatting.YELLOW + "Event"), "Event", "37.187.146.222:25565"));
    	
        this.buttonList.add(new GuiButtonAlkazia(19, this.width / 2 - 100, par1 + 35, 200, 20, I18n.format(EnumChatFormatting.DARK_RED + "Rejoindre Alkazia")));
        
        //this.buttonList.add(new GuiButtonServer(15, this.width / 2 - 130, par1 / 2 - 30, 110, 20, I18n.format(EnumChatFormatting.DARK_GREEN + "Hardcore"), "Hardcore", "37.187.146.222:25563"));
        //this.buttonList.add(new GuiButtonServer(16, this.width / 2 + 20, par1 / 2 - 30, 110, 20, I18n.format(EnumChatFormatting.BLUE + "Freebuild"), "Freebuild", "37.187.146.222:25564"));
        
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (p_146284_1_.id == 5)
        {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (p_146284_1_.id == 1)
        {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (p_146284_1_.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (p_146284_1_.id == 14 && this.minecraftRealmsButton.field_146125_m)
        {
            this.func_140005_i();
        }

        if (p_146284_1_.id == 4)
        {
            this.mc.shutdown();
        }
        
        if (p_146284_1_.id == 19)
        {
            //this.mc.displayGuiScreen(new GuiConnecting(this, mc, "37.187.146.222", 26666)); // PVP
        	this.mc.displayGuiScreen(new GuiConnecting(this, mc, new ServerData("alkazia", "37.187.146.222:25564"))); // PVP
        }
        
        if (p_146284_1_.id == 20)
        {
            //this.mc.displayGuiScreen(new GuiConnecting(this, mc, "37.187.146.222", 25565)); // Event
        }
        
        if (p_146284_1_.id == 15)
        {
        	 //this.mc.displayGuiScreen(new GuiConnecting(this, mc, "37.187.146.222", 25563)); // Hardcore
        }
        
        if (p_146284_1_.id == 16)
        {
        	 //this.mc.displayGuiScreen(new GuiConnecting(this, mc, "37.187.146.222", 25564)); // Build
        }

        if (p_146284_1_.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (p_146284_1_.id == 12)
        {
            ISaveFormat var2 = this.mc.getSaveLoader();
            WorldInfo var3 = var2.getWorldInfo("Demo_World");

            if (var3 != null)
            {
                GuiYesNo var4 = GuiSelectWorld.func_146623_a(this, var3.getWorldName(), 12);
                this.mc.displayGuiScreen(var4);
            }
        }
        
        
        
        if (p_146284_1_.id == 18)
        {
        	try
            {
                URI var2 = new URI("http://thisismac.fr");

                if (Desktop.isDesktopSupported())
                {
                	Desktop.getDesktop().browse(var2);
                }
            }
            catch (Exception var7)
            {
                var7.printStackTrace();
            }
        }
        
       
    }

    private void func_140005_i()
    {
        Session var1 = this.mc.getSession();
        McoClient var2 = new McoClient(var1.getSessionID(), var1.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            if (var2.func_148695_c().booleanValue())
            {
                this.mc.displayGuiScreen(new GuiScreenClientOutdated(this));
            }
            else
            {
                this.mc.displayGuiScreen(new GuiScreenOnlineServers(this));
            }
        }
        catch (ExceptionMcoService var4)
        {
            logger.error("Couldn\'t connect to realms");
        }
        catch (IOException var5)
        {
            logger.error("Couldn\'t connect to realms");
        }
    }

    public void confirmClicked(boolean par1, int par2)
    {
        if (par1 && par2 == 12)
        {
            ISaveFormat var6 = this.mc.getSaveLoader();
            var6.flushCache();
            var6.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        }
        else if (par2 == 13)
        {
            if (par1)
            {
                try
                {
                    Class var3 = Class.forName("java.awt.Desktop");
                    Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    var3.getMethod("browse", new Class[] {URI.class}).invoke(var4, new Object[] {new URI(this.field_104024_v)});
                }
                catch (Throwable var5)
                {
                    logger.error("Couldn\'t open link", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }
    
    public static String readFile(String url) 
    {
        String text = "0";
        try 
        {
            URL location = new URL(url); 
            BufferedReader in = new BufferedReader(new InputStreamReader(location.openStream()));
            text = in.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return text; 
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
    
    public void drawLogo()
    { 	
    	GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.getTextureManager().bindTexture(logo);
        this.drawTextureWithOptionalSize(width / 2 - 50, height / 4 - 15, 0, 0, 100, 100, 100, 100);
    }

    public void drawBackground()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var2 = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        mc.getTextureManager().bindTexture(background);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var2.startDrawingQuads();
        var2.addVertexWithUV(0.0D, height, 0.0D, 0.0D, 1.0D);
        var2.addVertexWithUV(width, height, 0.0D, 1.0D, 1.0D);
        var2.addVertexWithUV(width, 0.0D, 0.0D, 1.0D, 0.0D);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.draw();
    }
    
    	

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
    	
    	drawBackground();
        drawLogo();
        
        this.drawGradientRect(0, 0, width, 15, 2130706433, 2130706433);
        this.drawString(this.fontRendererObj, text, this.textPosition, 4, 0xB9121B);
        
        /*
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float var8 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        var8 = var8 * 80.0F / (float)(this.fontRendererObj.getStringWidth(this.splashText) + 32);
        GL11.glScalef(var8, var8, var8);
        this.drawCenteredString(this.fontRendererObj, "Allez viens ! On est bien !", - 10,  -15, -256);
        GL11.glPopMatrix();*/

        super.drawScreen(par1, par2, par3);
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        Object var4 = this.field_104025_t;

        synchronized (this.field_104025_t)
        {
            if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t && par1 <= this.field_92020_v && par2 >= this.field_92021_u && par2 <= this.field_92019_w)
            {
                GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this, this.field_104024_v, 13, true);
                var5.func_146358_g();
                this.mc.displayGuiScreen(var5);
            }
        }
    }

}
