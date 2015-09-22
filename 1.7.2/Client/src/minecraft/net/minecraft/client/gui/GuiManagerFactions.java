package net.minecraft.client.gui;

import java.awt.Desktop;
import java.awt.Menu;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;


public class GuiManagerFactions extends GuiScreen
{
    /** Also counts the number of updates, not certain as to why yet. */
    private int updateCounter2 = 0;

    /** Counts the number of screen updates. */
    private int updateCounter = 0;
    
    private GuiTextField nomFaction;
    private GuiTextField player;
    private GuiTextField player2;
    
    private int use = 1;
    private int use2 = 1;
    private int use3 = 1;
    String moneygui = "0";
    
    private static final ResourceLocation background = new ResourceLocation("textures/gui/demo_background.png");
    
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.updateCounter2 = 0;
        this.buttonList.clear();
        byte b0 = -16;
        
        
        this.buttonList.add(new GuiButton(50, this.width / 2 + 60, this.height / 4 + 48, 70, 20, StatCollector.translateToLocal("Claim")));
        this.buttonList.add(new GuiButton(60, this.width / 2 + 60, this.height / 4 + 72, 70, 20, StatCollector.translateToLocal("Unclaim")));
        
        this.buttonList.add(new GuiButton(70, this.width / 2 + 60, this.height / 4 , 70, 20, StatCollector.translateToLocal("Sethome")));
        this.buttonList.add(new GuiButton(80, this.width / 2 + 60, this.height / 4 + 24, 70, 20, StatCollector.translateToLocal("Home")));
        
       // this.buttonList.add(new GuiButton(70, this.width / 2 + 50, this.height / 4 + 114, 80, 20, StatCollector.translateToLocal("Factions >")));
        
        this.buttonList.add(new GuiButton(90, this.width / 2 - 130, this.height / 4 + 114, 80, 20, StatCollector.translateToLocal("< Retour")));
        
        this.buttonList.add(new GuiButton(100, this.width / 2 - 25, this.height / 4 + 10, 70, 20, StatCollector.translateToLocal("Crée faction")));
        this.buttonList.add(new GuiButton(110, this.width / 2 - 25, this.height / 4 + 40, 70, 20, StatCollector.translateToLocal("Inviter")));
        this.buttonList.add(new GuiButton(120, this.width / 2 - 25, this.height / 4 + 70, 70, 20, StatCollector.translateToLocal("Kicker")));
        
        
        nomFaction = new GuiTextField(this.fontRendererObj, this.width / 2- 135, this.height / 4 + 10, 100, 20);
        //nomFaction.setMaxStringLength(19);
        nomFaction.setText("Nom de la faction");
        
        player = new GuiTextField(fontRendererObj, this.width / 2- 135, this.height / 4 + 40, 100, 20);
        //player.setMaxStringLength(19);
        player.setText("Nom du joueur");
        
        player2 = new GuiTextField(fontRendererObj, this.width / 2 - 135, this.height / 4 + 70, 100, 20);
        //player2.setMaxStringLength(19);
        player2.setText("Nom du joueur");
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
          
            case 1:
                par1GuiButton.enabled = false;
                this.mc.displayGuiScreen(new GuiMainMenu());
            case 2:
           
            case 50:
            	mc.thePlayer.sendChatMessage("/f claim");
            	mc.displayGuiScreen(null);
            	break;
            case 60:
            	mc.thePlayer.sendChatMessage("/f unclaim");
            	mc.displayGuiScreen(null);
            	break;
            case 70:
            	mc.thePlayer.sendChatMessage("/f sethome");
            	mc.displayGuiScreen(null);
            	break;
            case 80:
            	mc.thePlayer.sendChatMessage("/f home");
            	mc.displayGuiScreen(null);
            	break;
            case 90:
            	this.mc.displayGuiScreen(new GuiIngameMenu());
            	break;
            case 100:
            	mc.thePlayer.sendChatMessage("/f create " + nomFaction.getText());
            	mc.displayGuiScreen(null);
            	break;
            case 110:
            	mc.thePlayer.sendChatMessage("/f invite " + player.getText());
            	mc.displayGuiScreen(null);
            	break;
            case 120:
            	mc.thePlayer.sendChatMessage("/f kick " + player2.getText());
            	mc.displayGuiScreen(null);
            	break;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCounter;
        nomFaction.updateCursorCounter();
        player.updateCursorCounter();
        player2.updateCursorCounter();
    }

    /**
     * 
     * Afficher pour afficher une texture de dimension uSize x vSize (largeur x hauteur)
     */
    public void drawTexturedModalRectWithOptionnalSize(int x, int y, int u, int v, int width, int height, int uSize, int vSize)
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
    
    
    protected void getBackgroundGui()
    { 	
    	mc.getTextureManager().bindTexture(background);
        drawTexturedModalRectWithOptionnalSize(this.width / 2 - 150, this.height / 4 - 30 , 0, 0, 301, 181, 301, 181);
    }
    
    protected void keyTyped(char c, int i)
    {
        this.nomFaction.textboxKeyTyped(c, i);
        this.player.textboxKeyTyped(c, i);
        this.player2.textboxKeyTyped(c, i);
        super.keyTyped(c, i);
    }
    
    protected void mouseClicked(int i, int j, int k)
    {
    	
    	nomFaction.mouseClicked(i, j, k);
        player.mouseClicked(i, j, k);
        player2.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
    }
    

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        System.out.println("salut");
        // On affiche l'image du GUI
        getBackgroundGui();
        
        // Si le joueur clique sur la case, on la vide
       if(nomFaction.isFocused() && use == 1)
       {
    	   nomFaction.setText("");
    	   use = 0;
       }
       if(player.isFocused() && use2 == 1 )
       {
    	   player.setText("");
    	   use2 = 0;
       }
       if(player2.isFocused() && use3 == 1)
       {
    	   player2.setText("");
    	   use3 = 0;
       }
        // Affichage des textbox pour envoyer des sous 
        player.drawTextBox();
       	nomFaction.drawTextBox();
        player2.drawTextBox();
        
        // Affichage du titre du GUI (en cours)
        this.drawString(this.fontRendererObj, (EnumChatFormatting.GRAY + "- Menu Faction -"), this.width / 2 - 40, this.height / 4 - 20, 2677722);
        
        super.drawScreen(par1, par2, par3);
    }
}
