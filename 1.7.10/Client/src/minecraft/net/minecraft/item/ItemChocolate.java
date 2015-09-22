package net.minecraft.item;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemChocolate extends ItemFood
{
	protected Minecraft mc;
	
    public ItemChocolate(int p_i45341_1_, float p_i45341_2_, boolean p_i45341_3_)
    {
        super(p_i45341_1_, p_i45341_2_, p_i45341_3_);
    }


    protected void onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
     
        if (!par2World.isClient)
         {
        	if(par3EntityPlayer.getFoodStats().getFoodLevel() == 20) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Chocolat : Non mais regarde toi mec, tu continues Ã  t'engraisser alors que t'as plus faim"));
        	}
        	else if(par3EntityPlayer.getFoodStats().getFoodLevel() < 6) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Chocolat : Heuresement que tu manges sinon tu te seras transformÃ© en squelette !"));
        	}
        	else if(par3EntityPlayer.getFoodStats().getFoodLevel() > 6 && par3EntityPlayer.getFoodStats().getFoodLevel() < 16) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Chocolat : Ah sa fait du bien de bouffer de la graisse hein ?! Aller maintenant va tuer du noobs pour la perdre !"));
        	}
        	int i = new Random().nextInt(2);
        	if(i == 0) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Candy : Arrête de manger, t'es plus gros le père noël !"));
        	}
        	else if(i == 1) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Candy : Je pense qu'une baleine est plus mince que toi."));
        	}
        	else if(i == 2) {
        		par3EntityPlayer.addChatMessage(new ChatComponentText("Candy : Continue comme ça et tu pourras même plus passer les portes chez toi."));
        	}
        	
        	par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 1200, 0));
        	
        }
      
    }

}
