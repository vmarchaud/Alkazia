package fr.thisismac.level;


import be.maximvdw.featherboard.api.PlaceholderAPI;
import be.maximvdw.featherboard.api.PlaceholderAPI.PlaceholderRequestEvent;
import be.maximvdw.featherboard.api.PlaceholderAPI.PlaceholderRequestEventHandler;

public class PlaceHolderLevels {
	
	private Main main;
	
	public PlaceHolderLevels(Main pl) {
		this.main = pl;
	}
	
	public void init() {
		PlaceholderAPI.registerPlaceholder("player_level", 
				new PlaceholderRequestEventHandler() {
					@Override
					public String onPlaceholderRequest(final PlaceholderRequestEvent e) {
						return String.valueOf(main.getPlayer(e.getPlayer().getName()).getLevel());
					}
		});
		
		
		PlaceholderAPI.registerPlaceholder("player_xp", 
				new PlaceholderRequestEventHandler() {
					@Override
					public String onPlaceholderRequest(final PlaceholderRequestEvent e) {
						PlayerManager p = main.getPlayer(e.getPlayer().getName());
						return new String(p.getxP() + "/" + main.getManager().getXPNeededFor(p.getLevel()));
					}
		});
		
	}

}
