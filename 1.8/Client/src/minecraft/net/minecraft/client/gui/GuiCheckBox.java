package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCheckBox extends GuiButton
{
	private boolean isChecked;
    private static final ResourceLocation texture = new ResourceLocation("textures/gui/checkbox.png");
	
	public GuiCheckBox(int id, int xPosition, int yPosition, boolean initialState, String string)
	{
		super(id, xPosition, yPosition, 20, 20, string);
		this.isChecked = initialState;
	}

	public void setDisplayString(String textToDisplay)
	{
		this.displayString = textToDisplay;
	}
	
	public void setChecked(boolean checked)
	{
		this.isChecked = checked;
	}
	
	public boolean isChecked()
	{
		return this.isChecked;
	}
	
	public void actionPerformed()
	{
		this.isChecked = !this.isChecked;
	}

	public void drawButton(Minecraft mc, int xMousePosition, int yMousePosition)
	{
        this.hovered = xMousePosition >= this.xPosition && yMousePosition >= this.yPosition && xMousePosition < this.xPosition + this.width && yMousePosition < this.yPosition + this.height;
				
		int spriteX = this.isChecked ? 20 : 0;
		int spriteY = this.enabled ? this.hovered ? 40 : 20 : 0;
		int textColor = this.enabled ? this.hovered ? 16777120 : 14737632 : -6250336;

        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
		this.drawTexturedModalRect(this.xPosition, this.yPosition, spriteX, spriteY, 20, 20);
		
		if(this.displayString != null && !this.displayString.matches(""))
		{           
			this.drawString(mc.fontRendererObj, displayString, this.xPosition + this.width + 4, this.yPosition + (this.height - 8) / 2, textColor);
		}
	}
}