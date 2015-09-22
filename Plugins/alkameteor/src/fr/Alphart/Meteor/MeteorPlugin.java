package fr.Alphart.Meteor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.wimbli.WorldBorder.WorldBorder;

public class MeteorPlugin extends JavaPlugin implements Listener {
    private WorldBorder wb;
    private Meteor meteor;
    private File schematicFile;
    private File schematicFileNuit;
    private World world;
    private int taggedMaterial;
    private Economy economy;
    private static MeteorPlugin plugin;

    public int getActivationDelay() {
	return this.getConfig().getInt("delaiActivationRedemarrage");
    }


    public Meteor getMeteor() {
	return this.meteor;
    }
    
    public static MeteorPlugin getPlugin() {
    	return plugin;
    }


    public int getNoClaimRadius() {
	return this.getConfig().getInt("rayonClaimInterdit");
    }

    public List<Integer> getNoSpawnItem() {
	return this.getConfig().getIntegerList("noSpawnItems");
    }

    public int getRollbackRadius() {
	return this.getConfig().getInt("rayonRollback");
    }

    public int getTaggedMaterial() {
	return this.taggedMaterial;
    }

    public WorldBorder getWB() {
	return this.wb;
    }

    public void loadConfig() throws IllegalArgumentException {
	this.getConfig().options().header("Fichier de configuration de MeteorPlugin\nToutes les durées sont en minutes.\nAttention, le fichier schematic doit être dans le répertoire du plugin.\nLa liste du nom des items est disponible ici : http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html\nIl est important d'indiquer le nom du monde afin de prendre en compte la worldborder.");
	this.getConfig().options().copyDefaults(true);

	this.getConfig().addDefault("dureePrespawn", 7);
	this.getConfig().addDefault("dureeTag", 12);
	this.getConfig().addDefault("rayonClaimInterdit", 3);
	this.getConfig().addDefault("nomFichierSchematic", "alkazia.schematic");
	this.getConfig().addDefault("nomFichierSchematicNuit", "alkazia.schematic");
	this.getConfig().addDefault("IdItemEntrainantTag", 203);
	this.getConfig().addDefault("monde", "world");
	this.getConfig().addDefault("delaiActivationRedemarrage", 30);
	this.getConfig().addDefault("noSpawnItems", Arrays.asList(17, 18));

	this.saveConfig();

	if (this.getConfig().getInt("dureePrespawn") >= 120 || this.getConfig().getInt("dureeTag") >= 120) throw new IllegalArgumentException("L'une des durees specifies est incorrect.");

	this.schematicFile = new File(this.getDataFolder() + File.separator + this.getConfig().getString("nomFichierSchematic"));
	if (!this.schematicFile.exists()) throw new IllegalArgumentException("Le fichier schematic n'a pas pu etre trouve.");
	
	this.schematicFileNuit = new File(this.getDataFolder() + File.separator + this.getConfig().getString("nomFichierSchematicNuit"));
	if (!this.schematicFileNuit.exists()) throw new IllegalArgumentException("Le fichier schematic de nuit n'a pas pu etre trouve.");

	this.world = Bukkit.getWorld(this.getConfig().getString("monde"));
	if (this.world == null) throw new IllegalArgumentException("Le monde indique est incorrect.");
	if (this.wb.GetWorldBorder(this.world.getName()) == null) throw new IllegalArgumentException("Le monde indique n'a pas de worldborder.");

	this.taggedMaterial = this.getConfig().getInt("IdItemEntrainantTag");
    }

    @Override
    public void onDisable() {
	if (this.meteor != null) {
	    final Meteor.BossBarCountdown countdown = this.meteor.getBossBarC();
	    if (countdown != null) countdown.unset();
	    this.meteor.regenerate();
	}
    }

    @Override
    public void onEnable() {
    this.plugin = this;
	this.wb = (WorldBorder) this.getServer().getPluginManager().getPlugin("WorldBorder");
	this.setupEconomy();
	try {
	    this.loadConfig();
	} catch (final IllegalArgumentException e) {
	    this.getLogger().severe(e.getMessage());
	    this.setEnabled(false);
	    return;
	}

	this.getCommand("meteorite").setExecutor(new CmdExec(this));

	this.getServer().getPluginManager().registerEvents(this, this);

	spawnMeteor();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent ev) {
    	if (this.meteor == null) spawnMeteor();
    }

    public void spawnMeteor() {
		this.meteor = new Meteor(this, this.getConfig().getInt("dureePrespawn"), this.getConfig().getInt("dureeTag"), this.schematicFile, this.world, this.getActivationDelay());
	}

    public void unsetMeteor() {
    	this.meteor = null;
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
}
