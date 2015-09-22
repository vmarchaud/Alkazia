package net.minecraft.client.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiVideoSettings extends GuiScreen
{
    private GuiScreen field_146498_f;
    protected String field_146500_a = "Video Settings";
    private GameSettings field_146499_g;
    private GuiListExtended field_146501_h;
    private static final GameSettings.Options[] field_146502_i = new GameSettings.Options[] {GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.ANAGLYPH, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.ADVANCED_OPENGL, GameSettings.Options.GAMMA, GameSettings.Options.RENDER_CLOUDS, GameSettings.Options.PARTICLES, GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.ENABLE_VSYNC, GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.ANISOTROPIC_FILTERING};
    private static final String __OBFID = "CL_00000718";

    public GuiVideoSettings(GuiScreen p_i1062_1_, GameSettings p_i1062_2_)
    {
        this.field_146498_f = p_i1062_1_;
        this.field_146499_g = p_i1062_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.field_146500_a = I18n.format("options.videoTitle", new Object[0]);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, I18n.format("gui.done", new Object[0])));

        if (OpenGlHelper.field_153197_d)
        {
            this.field_146501_h = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, field_146502_i);
        }
        else
        {
            GameSettings.Options[] var1 = new GameSettings.Options[field_146502_i.length - 1];
            int var2 = 0;
            GameSettings.Options[] var3 = field_146502_i;
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                GameSettings.Options var6 = var3[var5];

                if (var6 != GameSettings.Options.ADVANCED_OPENGL)
                {
                    var1[var2] = var6;
                    ++var2;
                }
            }

            this.field_146501_h = new GuiOptionsRowList(this.mc, this.width, this.height, 32, this.height - 32, 25, var1);
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.field_146498_f);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        int var4 = this.field_146499_g.guiScale;
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_146501_h.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);

        if (this.field_146499_g.guiScale != var4)
        {
            ScaledResolution var5 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int var6 = var5.getScaledWidth();
            int var7 = var5.getScaledHeight();
            this.setWorldAndResolution(this.mc, var6, var7);
        }
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        int var4 = this.field_146499_g.guiScale;
        super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
        this.field_146501_h.func_148181_b(p_146286_1_, p_146286_2_, p_146286_3_);

        if (this.field_146499_g.guiScale != var4)
        {
            ScaledResolution var5 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int var6 = var5.getScaledWidth();
            int var7 = var5.getScaledHeight();
            this.setWorldAndResolution(this.mc, var6, var7);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();
        this.field_146501_h.func_148128_a(p_73863_1_, p_73863_2_, p_73863_3_);
        this.drawCenteredString(this.fontRendererObj, this.field_146500_a, this.width / 2, 5, 16777215);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
