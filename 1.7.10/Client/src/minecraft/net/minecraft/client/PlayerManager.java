package net.minecraft.client;

public class PlayerManager {
	
	private int level;
	private int xp;
	private int facLevel;
	private int facXP;
	private String facName;
	
	public PlayerManager(int level, int xp, int facLevel, int facXP, String facName) {
		this.level = level;
		this.xp = xp;
		this.facLevel = xp;
		this.facXP = facXP;
		this.facName = facName;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setXP(int xp) {
		this.xp = xp;
	}
	
	public void setFacLevel(int facLevel) {
		this.facLevel = facLevel;
	}
	
	public void setfacXP(int facXP) {
		this.facXP = facXP;
	}
	
	public void setfacName(String name) {
		this.facName = name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getXP() {
		return xp;
	}
	
	public int getfacLevel() {
		return facLevel;
	}
	
	public int getfacXP() {
		return facXP;
	}
	
	public String getFacName() {
		return facName;
	}
	
	
}
