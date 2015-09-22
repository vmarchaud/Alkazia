package fr.pluginmakers.alkatower;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Main extends JavaPlugin {
    public Tower t;
    public Tower st;
    public File configFile;
    public FileConfiguration config;
    public File sconfigFile;
    public FileConfiguration sconfig; // saturday config
    private final int taskid = 0;
    public HashMap<String, Integer> players = new HashMap<String, Integer>();
    public HashMap<String, Integer> times = new HashMap<String, Integer>();
    public Killing kill;
    public Cuboid c;
    public Cuboid sc;

    public void addPoints(final String playerName, final int points) {
        for (final String keyMap : this.players.keySet())
            if (keyMap.equals(playerName)) if (points == 1) this.players.put(keyMap, this.players.get(keyMap) + points);
            else {
                int time = 0;
                for (final String key : this.times.keySet())
                    if (key.equals(playerName)) time = this.times.get(key);
                if (time < 10) this.players.put(keyMap, this.players.get(keyMap) + points);
            }
    }

    /**
     * 
     * @param playerName
     * @param rank
     *            rank = 1 -> First rank = 2 -> Second rank = 3 -> Third
     */
    @SuppressWarnings("deprecation")
    public void awardPlayer(final String playerName, final int rank) {
        final Player p = Bukkit.getPlayer(playerName);
        List<String> awards = null;
        final Tower t = Tower.saturday ? this.st : this.t;
        if (rank == 1) awards = t.first;// First awards list
        else if (rank == 2) awards = t.second;// Second awards list
        else if (rank == 3) awards = t.third;// Third awards list
        for (final String kit : awards)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kit " + kit + " " + p.getName());
    }

    public void displayScore() {
        final Tower t = Tower.saturday ? this.st : this.t;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Tower.enable) {
                    this.cancel();
                    return;
                }
                final String[] winners = Main.this.getWinners();
                final Integer[] winnersScores = Main.this.getWinnersScores();
                for (final Player player : Main.this.getServer().getOnlinePlayers()) {
                    Integer score = Main.this.players.get(player.getName());
                    score = score == null ? 0 : score;
                    final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                    final Objective stats = board.registerNewObjective("stats", "dummy");
                    stats.setDisplayName("AlkaTower");
                    stats.setDisplaySlot(DisplaySlot.SIDEBAR);
                    if (winnersScores[0] > 0) stats.getScore(Bukkit.getOfflinePlayer(StringUtils.abbreviate("1er " + winners[0], 16))).setScore(winnersScores[0]);
                    if (winnersScores[1] > 0) stats.getScore(Bukkit.getOfflinePlayer(StringUtils.abbreviate("2eme : " + winners[1], 16))).setScore(winnersScores[1]);
                    if (winnersScores[2] > 0) stats.getScore(Bukkit.getOfflinePlayer(StringUtils.abbreviate("3eme : " + winners[2], 16))).setScore(winnersScores[2]);
                    stats.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + "Mes points :")).setScore(score);
                    if (score == 0) stats.getScore(Bukkit.getOfflinePlayer("Aucun")).setScore(0);
                    final int time = (Tower.saturday ? 3600 : 1200) - t.timer;
                    final int minutes = time % 3600 / 60;
                    final int seconds = time % 3600 % 60;
                    stats.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "--------------")).setScore(-1);
                    stats.getScore(Bukkit.getOfflinePlayer(minutes + "mn" + seconds + "s")).setScore(-2);
                    player.setScoreboard(board);
                }
            }
        }.runTaskTimer(this, 5 * 20, 5 * 20);
    }

    public String[] getWinners() {// Return the winners
        final String[] winners = { "Aucun", "Aucun", "Aucun" };
        int score1 = 0, score2 = 0, score3 = 0;
        String player1 = "Aucun", player2 = "Aucun", player3 = "Aucun";
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score1) {
                score1 = this.players.get(keyMap);
                player1 = keyMap;
            }
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score2 && this.players.get(keyMap) != score1) {
                score2 = this.players.get(keyMap);
                player2 = keyMap;
            }
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score3 && this.players.get(keyMap) != score1 && this.players.get(keyMap) != score2) {
                score3 = this.players.get(keyMap);
                player3 = keyMap;
            }
        winners[0] = player1;
        winners[1] = player2;
        winners[2] = player3;
        return winners;
    }

    public Integer[] getWinnersScores() {// Return the winners
        final Integer[] winners = { 0, 0, 0 };
        int score1 = 0, score2 = 0, score3 = 0;
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score1) score1 = this.players.get(keyMap);
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score2 && this.players.get(keyMap) != score1) score2 = this.players.get(keyMap);
        for (final String keyMap : this.players.keySet())
            if (this.players.get(keyMap) > score3 && this.players.get(keyMap) != score1 && this.players.get(keyMap) != score2) score3 = this.players.get(keyMap);
        winners[0] = score1;
        winners[1] = score2;
        winners[2] = score3;
        return winners;
    }

    public void givePoints(final String playerName, final int points) {
        for (final String mapKey : this.players.keySet())
            if (mapKey.equals(playerName)) this.players.put(mapKey, this.players.get(mapKey) + points);
    }

    // Loading of the variables to the Tower's
    public void loadSVariables() {
        final World world = this.getServer().getWorld(this.sconfig.getString("Location1.world"));
        final Double x = this.sconfig.getDouble("Location1.x");
        final Double y = this.sconfig.getDouble("Location1.y");
        final Double z = this.sconfig.getDouble("Location1.z");
        final World world2 = this.getServer().getWorld(this.sconfig.getString("Location2.world"));
        final Double x2 = this.sconfig.getDouble("Location2.x");
        final Double y2 = this.sconfig.getDouble("Location2.y");
        final Double z2 = this.sconfig.getDouble("Location2.z");
        this.st.location1 = new Location(world, x, y, z);
        this.st.location2 = new Location(world2, x2, y2, z2);
        this.st.first = this.sconfig.getStringList("Recompense_premier");
        this.st.second = this.sconfig.getStringList("Recompense_deuxieme");
        this.st.third = this.sconfig.getStringList("Recompense_troisieme");
    }

    // Loading of the variables to the Tower's
    public void loadVariables() {
        final World world = this.getServer().getWorld(this.config.getString("Location1.world"));
        final Double x = this.config.getDouble("Location1.x");
        final Double y = this.config.getDouble("Location1.y");
        final Double z = this.config.getDouble("Location1.z");
        final World world2 = this.getServer().getWorld(this.config.getString("Location2.world"));
        final Double x2 = this.config.getDouble("Location2.x");
        final Double y2 = this.config.getDouble("Location2.y");
        final Double z2 = this.config.getDouble("Location2.z");
        this.t.location1 = new Location(world, x, y, z);
        this.t.location2 = new Location(world2, x2, y2, z2);
        this.t.first = this.config.getStringList("Recompense_premier");
        this.t.second = this.config.getStringList("Recompense_deuxieme");
        this.t.third = this.config.getStringList("Recompense_troisieme");
    }

    @Override
    public void onDisable() {
        this.saveVariables();// Save locations
        this.saveSVariables();// Save locations
        if (Tower.enable) this.stopTheGame();
    }

    @Override
    public void onEnable() {
        this.t = new Tower();
        this.st = new Tower();
        this.setupData();
        this.setupSData();
        this.usualChecking();
        this.getServer().getPluginManager().registerEvents(new Killing(this), this);
        this.c = new Cuboid(this.t.location1, this.t.location2);
        this.sc = new Cuboid(this.st.location1, this.st.location2);
        this.kill = new Killing(this);
        this.kill.scheduleTasks(this);
    }

    /**
     * Play a sound when the game is gonna start
     */
    public void playSound() {
        for (final Player p : this.getServer().getOnlinePlayers())
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1F, 0);
    }

    /**
     * Save variables
     */
    public void saveSVariables() {
        this.sconfig.set("Location1.x", this.st.location1.getX());
        this.sconfig.set("Location1.y", this.st.location1.getY());
        this.sconfig.set("Location1.z", this.st.location1.getZ());
        this.sconfig.set("Location2.world", this.st.location1.getWorld().getName());
        this.sconfig.set("Location2.x", this.st.location2.getX());
        this.sconfig.set("Location2.y", this.st.location2.getY());
        this.sconfig.set("Location2.z", this.st.location2.getZ());
        this.sconfig.set("Location2.world", this.st.location2.getWorld().getName());
        try {
            this.sconfig.save(this.sconfigFile);
        } catch (final IOException e) {
            this.getLogger().warning("Cannot save sconfig.yml at starting!");
            e.printStackTrace();
        }
    }

    /**
     * Save variables
     */
    public void saveVariables() {
        this.config.set("Location1.x", this.t.location1.getX());
        this.config.set("Location1.y", this.t.location1.getY());
        this.config.set("Location1.z", this.t.location1.getZ());
        this.config.set("Location2.world", this.t.location1.getWorld().getName());
        this.config.set("Location2.x", this.t.location2.getX());
        this.config.set("Location2.y", this.t.location2.getY());
        this.config.set("Location2.z", this.t.location2.getZ());
        this.config.set("Location2.world", this.t.location2.getWorld().getName());
        try {
            this.config.save(this.configFile);
        } catch (final IOException e) {
            this.getLogger().warning("Cannot save config.yml at starting!");
            e.printStackTrace();
        }
    }

    /**
     * Send a broadcasted message to players' server
     * 
     * @param message
     * @param type
     *            true = green color false = red color
     */
    public void sayToOthers(final String message, final boolean type) {
        for (final Player player : this.getServer().getOnlinePlayers())
            if (type) player.sendMessage("[AlkaTower] " + ChatColor.GREEN + message);
            else player.sendMessage("[AlkaTower] " + ChatColor.RED + message);
    }

    public void secondsUtil() {
        final Tower t = Tower.saturday ? this.st : this.t;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Tower.enable) {
                    this.cancel();
                    return;
                }
                t.incrementTimer();
                if (!Tower.saturday) {
                    if (t.timer == 300) {
                        Main.this.sayToOthers("Il vous reste 15 minutes pour capture la tour.", false);
                        Main.this.sayToOthers("Classement des joueurs ayant le plus de points", true);
                        Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                        Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                        Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                        Main.this.sayToOthers("Classement des joueurs ayant le plus de points", true);
                    } else if (t.timer == 600) {
                        Main.this.sayToOthers("Il vous reste 10 minutes pour capture la tour.", false);
                        Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                        Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                        Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                    } else if (t.timer == 900) {
                        Main.this.sayToOthers("Il vous reste 5 minutes pour capture la tour.", false);
                        Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                        Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                        Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                    } else if (t.timer == 1200) {
                        Main.this.sayToOthers("Les trois premiers sont " + Main.this.getWinners()[0] + ", " + Main.this.getWinners()[1] + " et " + Main.this.getWinners()[2] + " ils vont recevoir leur récompense dans quelques instants, la tour n'est plus à capturer.", false);
                        Main.this.stopTheGame();
                    }
                } else if (t.timer == 900) {
                    Main.this.sayToOthers("Il vous reste 45 minutes pour capture la tour.", false);
                    Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                    Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                    Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                } else if (t.timer == 1800) {
                    Main.this.sayToOthers("Il vous reste 30 minutes pour capture la tour.", false);
                    Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                    Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                    Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                } else if (t.timer == 2700) {
                    Main.this.sayToOthers("Il vous reste 15 minutes pour capture la tour.", false);
                    Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                    Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                    Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                } else if (t.timer == 3000) {
                    Main.this.sayToOthers("Il vous reste 10 minutes pour capture la tour.", false);
                    Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                    Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                    Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                } else if (t.timer == 3300) {
                    Main.this.sayToOthers("Il vous reste 5 minutes pour capture la tour.", false);
                    Main.this.sayToOthers("1er : " + Main.this.getWinners()[0] + " avec " + Main.this.getWinnersScores()[0] + " points", true);
                    Main.this.sayToOthers("2ème : " + Main.this.getWinners()[1] + " avec " + Main.this.getWinnersScores()[1] + " points", true);
                    Main.this.sayToOthers("3ème : " + Main.this.getWinners()[2] + " avec " + Main.this.getWinnersScores()[2] + " points", true);
                } else if (t.timer == 3600) {
                    Main.this.sayToOthers("Les trois premiers sont " + Main.this.getWinners()[0] + ", " + Main.this.getWinners()[1] + " et " + Main.this.getWinners()[2] + " ils vont recevoir leur récompense dans quelques instants, la tour n'est plus à capturer.", false);
                    Main.this.stopTheGame();
                }
            }
        }.runTaskTimer(this, 20L, 20L);// Each seconds
    }

    // Setting up of config.yml
    public void setupData() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        if (!this.configFile.exists()) try {
            final String[] tableau = { "bonhomme", "oklm", "trkl" };
            final List<String> tabList = Arrays.asList(tableau);
            this.config.set("Location1.x", 12);
            this.config.set("Location1.y", 42);
            this.config.set("Location1.z", 156);
            this.config.set("Location1.world", "world");
            this.config.set("Location2.x", 35);
            this.config.set("Location2.y", 44);
            this.config.set("Location2.z", 22);
            this.config.set("Location2.world", "world");
            this.config.set("Recompense_premier", tabList);
            this.config.set("Recompense_deuxieme", tabList);
            this.config.set("Recompense_troisieme", tabList);
            this.config.save(this.configFile);
        } catch (final IOException e) {
            this.getLogger().warning("Cannot save config.yml at starting!");
            e.printStackTrace();
        }
        this.loadVariables();
    }

    // Setting up of sconfig.yml
    public void setupSData() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        this.sconfigFile = new File(this.getDataFolder(), "sconfig.yml");
        this.sconfig = YamlConfiguration.loadConfiguration(this.sconfigFile);
        if (!this.sconfigFile.exists()) try {
            final String[] tableau = { "bonhomme", "oklm", "trkl" };
            final List<String> tabList = Arrays.asList(tableau);
            this.sconfig.set("Location1.x", 12);
            this.sconfig.set("Location1.y", 42);
            this.sconfig.set("Location1.z", 156);
            this.sconfig.set("Location1.world", "world");
            this.sconfig.set("Location2.x", 35);
            this.sconfig.set("Location2.y", 44);
            this.sconfig.set("Location2.z", 22);
            this.sconfig.set("Location2.world", "world");
            this.sconfig.set("Recompense_premier", tabList);
            this.sconfig.set("Recompense_deuxieme", tabList);
            this.sconfig.set("Recompense_troisieme", tabList);
            this.sconfig.save(this.sconfigFile);
        } catch (final IOException e) {
            this.getLogger().warning("Cannot save sconfig.yml at starting!");
            e.printStackTrace();
        }
        this.loadSVariables();
    }

    public void stopTheGame() {
        for (final Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().equals(this.getWinners()[0])) this.awardPlayer(p.getName(), 1);
            else if (p.getName().equals(this.getWinners()[1])) this.awardPlayer(p.getName(), 2);
            else if (p.getName().equals(this.getWinners()[2])) this.awardPlayer(p.getName(), 3);
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        final Tower t = Tower.saturday ? this.st : this.t;
        this.players.clear();// Clearing the HashMap
        t.timer = 0;// Reset the timer
        t.rank = 0;
        Tower.enable = Tower.saturday = false;// Stop the game
    }

    /*
     * Checking each hours if there is 16, 18 or 21 hours to starting the game.
     */
    public void usualChecking() {
        new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                final Date date = new Date();
                boolean ok = false;
                final boolean saturday = false;
                if ((date.getHours() == 16 || date.getHours() == 18 || date.getHours() == 21) && date.getMinutes() == 0) {
                    ok = true;
                    // TODO: if (date.getHours() == 21 && date.getDay() == 6)
                    // saturday = true;
                    for (final Player player : Main.this.getServer().getOnlinePlayers()) {
                        player.sendMessage("[AlkaTower] " + ChatColor.GOLD + "Capture de la tour possible, " + (saturday ? "1 heure restante" : "20 minutes restantes") + "...");
                        Main.this.players.put(player.getName(), 0);
                        Main.this.times.put(player.getName(), 0);
                    }
                }
                if (ok) {
                    Tower.saturday = saturday;
                    Tower.enable = true;
                    Main.this.playSound();
                    Main.this.displayScore();
                    Main.this.secondsUtil();
                }
            }
        }.runTaskTimer(this, 0, 60 * 20);// Each minutes
    }
}
