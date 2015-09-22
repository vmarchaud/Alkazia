package fr.pluginmakers.alkatower;

import java.util.List;

import org.bukkit.Location;

public class Tower {
    public static boolean enable;// Does a game is running ?
    public static boolean saturday;

    public Location location1;
    public Location location2;
    public List<String> first;
    public List<String> second;
    public List<String> third;// Getting awards of players
    public int timer;// Count the number of seconds since the begginning of the
                     // game
    public int rank;

    // Just increment ; yeah, useless but useful to make to code clear
    public void incrementTimer() {
        this.timer++;
    }
}
