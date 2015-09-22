package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackManager implements IResourceManagerReloadListener
{
  static List<String> textureToCheck = new ArrayList();
  static HashMap<String, String> modelToCheck = new HashMap<String, String>();
  protected static Minecraft mc;
  public static String warning = "La triche est interdite sur Alkazia ! Veuillez désactiver votre pack invisible pour continuer à jouer !";
  
  // Alkazia - forbinden xray pack
  public PackManager(Minecraft mc) {
	  this.mc = mc;
  }
  
  private void init()
  {
    if (textureToCheck.size() == 0)
    {
    	textureToCheck.add("textures/blocks/stone.png");
    	textureToCheck.add("textures/blocks/dirt.png");
    	textureToCheck.add("textures/blocks/sand.png");
    	textureToCheck.add("textures/blocks/grass_top.png");
    	textureToCheck.add("textures/blocks/grass_side.png");
    	textureToCheck.add("textures/blocks/crafting_table_top.png");
      	textureToCheck.add("textures/blocks/crafting_table_side.png");
      	textureToCheck.add("textures/blocks/crafting_table_front.png");
      	textureToCheck.add("textures/blocks/pousse.png");
      	textureToCheck.add("textures/blocks/gravel.png");
      	textureToCheck.add("textures/blocks/obsidian.png");
    }
    
    if (modelToCheck.size() == 0)
    {
    	modelToCheck.put("models/block/cube.json", "0a0b7d130d27b58817bad465cc275a54");
    }
    
  }
  private void checkTexturePack(IResourceManager par1ResourceManager)
  {
    init();
    
    for(String file : textureToCheck) {
    	try
        {
           IResource res = par1ResourceManager.getResource(new ResourceLocation(file));
          InputStream stream = res.getInputStream();
          BufferedImage img = ImageIO.read(stream);
          int pixnb = countTransparentPixels(img);
          if (pixnb > 0)
          {
          	new JOptionPane(); 
          	JOptionPane.showMessageDialog(new Frame(), warning, "Alkazia", 0);
          	mc.shutdownMinecraftApplet();
          }
        }
        catch (IOException e)
        {
      	  e.printStackTrace();
        }
    }
    
    for(Entry<String, String> file : modelToCheck.entrySet()) {
    	try
        {
           IResource res = par1ResourceManager.getResource(new ResourceLocation(file.getKey()));
           InputStream stream = res.getInputStream();
           BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
           StringBuilder finale = new StringBuilder();
           String line = "";
           while ((line = buf.readLine()) != null)   {
        	   finale.append(line);
             }
    	   buf.close();
    	   stream.close();
    	   
           if(!MD5(finale.toString()).equalsIgnoreCase(modelToCheck.get(file.getKey()))) {
             	JOptionPane.showMessageDialog(new Frame(), warning, "Alkazia", 0);
             	mc.shutdownMinecraftApplet();
           }
        }
        catch (IOException e)
        {
      	  e.printStackTrace();
        }
    }
    
  }
  
  private String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
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
	@Override
	public void onResourceManagerReload(IResourceManager p_110549_1_) {
		checkTexturePack(p_110549_1_);
		
	}
}