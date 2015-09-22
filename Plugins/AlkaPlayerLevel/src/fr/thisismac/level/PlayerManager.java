package fr.thisismac.level;

public class PlayerManager {
	
	String playerName;
	int level = 1;
	int xp = 0;
	int kill = 0;

	public PlayerManager(String playerName, int level, int xp){
		this.playerName = playerName;
		this.level = level;
		this.xp = xp;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getLevel(){
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public void addLevel() {
		this.level++;
	}
	
	public void setXP(int xp) {
		this.xp = xp;
	}
	
	public int getxP(){
		return xp;
	}
	
	public void addXP(int xp) {
		this.xp = this.xp + xp;
	}
	
	public void resetXP() {
		this.xp = 0;
	}
	
	public void addKill() {
		this.kill++;
	}
	
	public int getKill() {
		return kill;
	}
}
