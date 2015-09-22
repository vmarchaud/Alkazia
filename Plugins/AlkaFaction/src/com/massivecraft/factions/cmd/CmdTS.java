package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;

public class CmdTS extends FCommand {

    public CmdTS() {
        this.aliases.add("ts");

        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = true;
    }

    @Override
    public void perform() {
    	
    	if(this.p.api.getChannelByName("Factions : " + fme.getFaction().getTag()) != null) {
    		 this.fme.msg("<b>Votre faction posséde déjà un channel Teamspeak !");
    		 return;
    	}
    	if(this.p.api.getClientByName(me.getName()) == null) {
    		this.fme.msg("<b>Vous devez être connecté sur le teamspeak avec exactement le même pseudo que celui en jeu.");
    		return;
    	}

    	
    	final HashMap<ChannelProperty, String> properties = new HashMap<>();
		properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
		properties.put(ChannelProperty.CPID, "19");
		
		
		int id = this.p.api.createChannel("Faction : " + fme.getFaction().getTag(), properties); 
		Client client = p.api.getClientByName(me.getName()).get(0);
		this.p.api.moveClient(client.getId(), id);
		this.p.api.setClientChannelGroup(5, id, client.getDatabaseId());
		
		final HashMap<ChannelProperty, String> propertiesSousChannel = new HashMap<>();
		properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
		properties.put(ChannelProperty.CPID, String.valueOf(id));
		
		this.p.api.createChannel("PVP >>", properties); 
		this.p.api.createChannel("FARM >> ", properties); 
    	this.fme.msg("<i>Votre channel teamspeak a bien été crée, vous avez également le channel Admin.");
        
    	
    }

}
