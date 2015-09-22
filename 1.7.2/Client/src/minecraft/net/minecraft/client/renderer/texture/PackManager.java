package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackManager
{
  static List<String> allow = new ArrayList();
  protected static Minecraft mc;
  public static String warning = "La triche est interdite sur Alkazia ! Veuillez désactiver votre pack invisible pour continuer à jouer !";
  
  // Alkazia - forbinden xray pack
  
  static void init()
  {
    if (allow.size() == 0)
    {
      allow.add("textures/blocks/stone.png");
      allow.add("textures/blocks/dirt.png");
      allow.add("textures/blocks/sand.png");
      allow.add("textures/blocks/grass_top.png");
      allow.add("textures/blocks/grass_side.png");
      allow.add("textures/blocks/crafting_table_top.png");
      allow.add("textures/blocks/crafting_table_side.png");
      allow.add("textures/blocks/crafting_table_front.png");
      allow.add("textures/blocks/pousse.png");
      allow.add("textures/blocks/gravel.png");
      allow.add("textures/blocks/obsidian.png");
    }
  }
  public static boolean checkTexturePack(IResourceManager par1ResourceManager)
  {
    init();
    for (int i = 0; i < allow.size(); i++) {
      String file = allow.get(i);
      try
      {
         IResource res = par1ResourceManager.getResource(new ResourceLocation(file));
        InputStream stream = res.getInputStream();
        BufferedImage img = ImageIO.read(stream);
        int pixnb = countTransparentPixels(img);
        
        //System.out.println("Check texture " + i + " transparency : " + file + " = " + pixnb);
        
        if (pixnb > 0)
        {
        	new JOptionPane(); 
        	JOptionPane.showMessageDialog(new Frame(), warning, "Alkazia", 0);
        	System.exit(0);
        }
      }
      catch (IOException e)
      {
    	  //System.out.println("Check texture " + i + " transparency : " + file + " not found");
      }
    }

    return true;
  }

  private static int countTransparentPixels(BufferedImage par1BufferedImage)
  {
    int pixnb = 0;
    int w = par1BufferedImage.getWidth();
    int h = par1BufferedImage.getHeight();
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        int rgb = par1BufferedImage.getRGB(x, y);
        int alpha = rgb >> 24 & 0xFF;
        if (alpha != 255)
        {
          pixnb++;
        }
      }
    }
    return pixnb;
  }
}