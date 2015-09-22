package me.rellynn.plugins.alkaboat;

import java.io.File;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.wimbli.WorldBorder.WorldBorder;

import fr.thisismac.level.Main;

public class BoatPlugin extends JavaPlugin implements Listener {
    private WorldBorder wb;
    private Boat boat;
    private File schematicFile;
    private World world;
    private Economy economy;
    private static BoatPlugin plugin;

    public int getActivationDelay() {
	return this.getConfig().getInt("delaiActivationRedemarrage");
    }

    public Boat getBoat() {
    	return this.boat;
    }
    
    public int getNoClaimRadius() {
	return this.getConfig().getInt("rayonClaimInterdit");
    }

    public int getUnderBlocks() {
	return this.getConfig().getInt("blocsEnDessousDeLaMer");
    }

    public WorldBorder getWB() {
	return this.wb;
    }

    public void loadConfig() throws IllegalArgumentException {
	this.getConfig().options().header("Fichier de configuration de AlkaBoat\nToutes les durées sont en minutes.\nAttention, le fichier schematic doit être dans le répertoire du plugin.\nIl est important d'indiquer le nom du monde afin de prendre en compte la worldborder.");
	this.getConfig().options().copyDefaults(true);

	this.getConfig().addDefault("dureePrespawn", 7);
	this.getConfig().addDefault("rayonClaimInterdit", 3);
	this.getConfig().addDefault("nomFichierSchematic", "alkazia.schematic");
	this.getConfig().addDefault("blocsEnDessousDeLaMer", "3");
	this.getConfig().addDefault("monde", "world");
	this.getConfig().addDefault("delaiActivationRedemarrage", 30);
	this.getConfig().addDefault("enabled", false);

	this.saveConfig();

	if (this.getConfig().getInt("dureePrespawn") >= 240) throw new IllegalArgumentException("La duree specifiee est incorrect.");

	this.schematicFile = new File(this.getDataFolder() + File.separator + this.getConfig().getString("nomFichierSchematic"));
	if (!this.schematicFile.exists()) throw new IllegalArgumentException("Le fichier schematic n'a pas pu etre trouve.");

	this.world = Bukkit.getWorld(this.getConfig().getString("monde"));
	if (this.world == null) throw new IllegalArgumentException("Le monde indique est incorrect.");
	if (this.wb.GetWorldBorder(this.world.getName()) == null) throw new IllegalArgumentException("Le monde indique n'a pas de worldborder.");
    }

    @Override
    public void onDisable() {
	if (this.boat != null) {
	    final Boat.BossBarCountdown countdown = this.boat.getBossBarC();
	    if (countdown != null) countdown.unset();
	    this.boat.regenerate();
	}
    }

    @Override
    public void onEnable() {
    	this.plugin = this;
	this.wb = (WorldBorder) this.getServer().getPluginManager().getPlugin("WorldBorder");
	try {
	    this.loadConfig();
	} catch (final IllegalArgumentException e) {
	    this.getLogger().severe(e.getMessage());
	    this.setEnabled(false);
	    return;
	}

	this.getCommand("bateau").setExecutor(new CmdExec(this));

	this.getServer().getPluginManager().registerEvents(this, this);

	this.boat = new Boat(this, this.getConfig().getInt("dureePrespawn"), this.schematicFile, this.world, this.getActivationDelay());
	setupEconomy();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent ev) {
	if (this.boat == null) this.boat = new Boat(this, this.getConfig().getInt("dureePrespawn"), this.schematicFile, this.world, this.getActivationDelay());
    }

    public void spawnBoat() {
        	this.boat = new Boat(this, this.getConfig().getInt("dureePrespawn"), this.schematicFile, this.world, this.getActivationDelay());
    }

    public void unsetBoat() {
	this.boat = null;
    }
    
    private boolean setupEconomy() {
    	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }
    
    public Economy getEco() {
    	return economy;
    }
    
    public static BoatPlugin getInstance() {
    	return plugin;
    }
}
