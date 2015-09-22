package com.massivecraft.factions;

import org.bukkit.ChatColor;
import org.junit.Before;

import be.maximvdw.animatednames.api.PlaceholderAPI;
import be.maximvdw.animatednames.api.PlaceholderAPI.PlaceholderRequestEvent;
import be.maximvdw.animatednames.api.PlaceholderAPI.PlaceholderRequestEventHandler;

public class PlaceHolderFactions2 {
	
	private P p;
	
	public PlaceHolderFactions2(P pl) {
		this.p = pl;
	}
	@Before
	public void init() {
		PlaceholderAPI.registerPlaceholder("factions", 
				new PlaceholderRequestEventHandler() {
					@Override
					public String onPlaceholderRequest(final PlaceholderRequestEvent e) {
						
						return FPlayers.i.get(e.getPlayer()).getFaction().isNone() ? ChatColor.DARK_GREEN + "Wilderness" : FPlayers.i.get(e.getPlayer()).getFaction().getTag();
					}
		});
		
		
		PlaceholderAPI.registerPlaceholder("factions_level", 
				new PlaceholderRequestEventHandler() {
					@Override
					public String onPlaceholderRequest(final PlaceholderRequestEvent e) {
						return String.valueOf(FPlayers.i.get(e.getPlayer()).getFaction().getLevel().getLevel());
					}
		});
		
		
		PlaceholderAPI.registerPlaceholder("factions_xp", 
				new PlaceholderRequestEventHandler() {
					@Override
					public String onPlaceholderRequest(final PlaceholderRequestEvent e) {
						final Faction f = Factions.i.get(FPlayers.i.get(e.getPlayer()).getFactionId());
						final Level nextLevel = Levels.i.get(String.valueOf(f.getLevel().getLevel()));
						return new String((int)f.getXP() + "/" + Math.round(nextLevel.getXP()));
					}
		});
	}

}
