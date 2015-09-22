package com.massivecraft.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.massivecraft.factions.adapters.FFlagTypeAdapter;
import com.massivecraft.factions.adapters.FPermTypeAdapter;
import com.massivecraft.factions.adapters.ItemStackAdapter;
import com.massivecraft.factions.adapters.LocationTypeAdapter;
import com.massivecraft.factions.adapters.RelTypeAdapter;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CmdFacXP;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.holder.FactionHolder;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.capi.CapiFeatures;
import com.massivecraft.factions.integration.herochat.HerochatFeatures;
import com.massivecraft.factions.listeners.FactionsAppearanceListener;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsExploitListener;
import com.massivecraft.factions.listeners.FactionsInventoryListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.FactionsServerListener;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.struct.TerritoryAccess;
import com.massivecraft.factions.util.AutoLeaveTask;
import com.massivecraft.factions.util.EconLandRewardTask;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.MPlugin;

public class P extends MPlugin {
    // Our single plugin instance
    public static P p;

    // Listeners
    public final FactionsPlayerListener playerListener;
    public final FactionsChatListener chatListener;
    public final FactionsEntityListener entityListener;
    public final FactionsExploitListener exploitListener;
    public final FactionsBlockListener blockListener;
    public final FactionsServerListener serverListener;
    public final FactionsAppearanceListener appearanceListener;
    // AlkaziaFactions
    public final FactionsInventoryListener inventoryListener;
    public final String prefix =  ChatColor.DARK_RED + "[" + ChatColor.GOLD + "Alkazia" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
    public TS3Config config;
    public TS3Query query;
    public TS3Api api;
    // End AlkaziaFactions

    // Persistance related
    private boolean locked = false;

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(final boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    private Integer AutoLeaveTask = null;
    private Integer econLandRewardTaskID = null;

    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;

    // AlkaziaFactions
    public static Map<String, List<String>> allies = new HashMap<>();
    public static List<Faction> homes = new ArrayList<>();

    // End AlkaziaFactions

    public P() {
        P.p = this;
        this.playerListener = new FactionsPlayerListener(this);
        this.chatListener = new FactionsChatListener(this);
        this.entityListener = new FactionsEntityListener(this);
        this.exploitListener = new FactionsExploitListener();
        this.blockListener = new FactionsBlockListener(this);
        this.serverListener = new FactionsServerListener(this);
        this.appearanceListener = new FactionsAppearanceListener(this);
        // AlkaziaFactions
        this.inventoryListener = new FactionsInventoryListener(this);
        // End AlkaziaFactions
    }

    @Override
    public void onEnable() {
        // bit of (apparently absolutely necessary) idiot-proofing for CB version support due to changed GSON lib package name
        try {
            Class.forName("org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken");
        } catch (final ClassNotFoundException ex) {
            this.log(Level.SEVERE, "GSON lib not found. Your CraftBukkit build is too old (< 1.3.2) or otherwise not compatible.");
            this.suicide();
            return;
        }

        if (!this.preEnable()) return;
        this.loadSuccessful = false;

        // Load Conf from disk
        Conf.load();
        FPlayers.i.loadFromDisc();
        Factions.i.loadFromDisc();
        Board.load();

        // AlkaziaFactions
        Levels.i.loadFromDisc();
        for (final Faction faction : Factions.i.get()) {
            if (faction.hasHome()) {
                P.homes.add(faction);
                final Block factionBlock = faction.getFactionBlock();
                factionBlock.setType(Conf.factionBlockMaterial);
                final Location loc = factionBlock.getLocation();
                loc.add(0, 1, 0).getBlock().breakNaturally();
                loc.add(0, 1, 0).getBlock().breakNaturally();
            }
            faction.setLevel(Levels.i.get(faction.getLevelId()));
            if (faction.isNormal()) {
                final Inventory inventory = Bukkit.createInventory(new FactionHolder(), 54, faction.getTag());
                inventory.setContents(faction.getLevel().getItems());
                faction.setInventory(inventory);
            }
            P.allies.put(faction.getTag(), faction.getAllies());
        }
        this.getCommand("facxp").setExecutor(new CmdFacXP());
        // End AlkaziaFactions

        // Add Base Commands
        this.cmdAutoHelp = new CmdAutoHelp();
        this.cmdBase = new FCmdRoot();

        EssentialsFeatures.setup();
        SpoutFeatures.setup();
        Econ.setup();
        CapiFeatures.setup();
        HerochatFeatures.setup();
        LWCFeatures.setup();

        if (Conf.worldGuardChecking) {
            Worldguard.init(this);
        }

        // start up task which runs the autoLeaveAfterDaysOfInactivity routine
        this.startAutoLeaveTask(false);

        // start up task which runs the econLandRewardRoutine
        this.startEconLandRewardTask(false);

        // Register Event Handlers
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
        this.getServer().getPluginManager().registerEvents(this.chatListener, this);
        this.getServer().getPluginManager().registerEvents(this.entityListener, this);
        this.getServer().getPluginManager().registerEvents(this.exploitListener, this);
        this.getServer().getPluginManager().registerEvents(this.blockListener, this);
        this.getServer().getPluginManager().registerEvents(this.serverListener, this);
        this.getServer().getPluginManager().registerEvents(this.appearanceListener, this);
        // AlkaziaFactions
        this.getServer().getPluginManager().registerEvents(this.inventoryListener, this);
        PlaceHolderFactions placeHolder = new PlaceHolderFactions(this);
        placeHolder.init();
        PlaceHolderFactions2 placeHolder2 = new PlaceHolderFactions2(this);
        placeHolder2.init();
        
        try {
            final File lib= new File(getDataFolder(), "ts3api.jar");
            
                if (!lib.exists()) {
                    JavaUtils.extractFromJar(lib.getName(),
                            lib.getAbsolutePath());
                }
            
                if (!lib.exists()) {
                    getLogger().warning(
                            "There was a critical error loading My plugin! Could not find lib: "
                                    + lib.getName());
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
                addClassPath(JavaUtils.getJarUrl(lib));
            
        } catch (final Exception e) {
            e.printStackTrace();
        }
        
/*
        this.config = new TS3Config();
        config.setHost("ts.alkazia.net");
        config.setFloodRate(FloodRate.UNLIMITED);
        config.setDebugLevel(Level.SEVERE);
        config.setLoginCredentials("serveradmin", "AlkaziaTS");

        query = new TS3Query(config);
        query.connect();
        api = query.getApi();
        api.selectVirtualServerById(1);
        api.setNickname("AlkaziaFactions");
        */
        // End AlkaziaFactions

        this.postEnable();
        this.loadSuccessful = true;
    }
    
    private void addClassPath(final URL url) throws IOException {
        final URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        final Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            final Method method = sysclass.getDeclaredMethod("addURL",
                    new Class[] { URL.class });
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { url });
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url
                    + " to system classloader");
        }
    }
    
    @Override
    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).registerTypeAdapter(LazyLocation.class, new LocationTypeAdapter()).registerTypeAdapter(TerritoryAccess.class, new TerritoryAccess()).registerTypeAdapter(Rel.class, new RelTypeAdapter()).registerTypeAdapter(FPerm.class, new FPermTypeAdapter()).registerTypeAdapter(FFlag.class, new FFlagTypeAdapter())
        // AlkaziaFactions
        .registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
        // End AlkaziaFactions
    }

    @Override
    public void onDisable() {
        // only save data if plugin actually completely loaded successfully
        if (this.loadSuccessful) {
            Board.save();
            Conf.save();
        }
        EssentialsFeatures.unhookChat();
        if (this.AutoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(this.AutoLeaveTask);
            this.AutoLeaveTask = null;
        }
        super.onDisable();
    }

    public void startAutoLeaveTask(final boolean restartIfRunning) {
        if (this.AutoLeaveTask != null) {
            if (!restartIfRunning) return;
            this.getServer().getScheduler().cancelTask(this.AutoLeaveTask);
        }

        if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
            final long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
            this.AutoLeaveTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
        }
    }

    public void startEconLandRewardTask(final boolean restartIfRunning) {
        if (this.econLandRewardTaskID != null) {
            if (!restartIfRunning) return;
            this.getServer().getScheduler().cancelTask(this.econLandRewardTaskID);
        }

        if (Conf.econEnabled && Conf.econLandRewardTaskRunsEveryXMinutes > 0.0 && Conf.econLandReward > 0.0) {
            final long ticks = 20 * 60 * Conf.econLandRewardTaskRunsEveryXMinutes;
            this.econLandRewardTaskID = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new EconLandRewardTask(), ticks, ticks);
        }
    }

    @Override
    public void postAutoSave() {
        Board.save();
        Conf.save();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] split) {
        this.cmdBase.execute(sender, new ArrayList<String>(Arrays.asList(split)));
        return true;
    }

    // -------------------------------------------- //
    // Functions for other plugins to hook into
    // -------------------------------------------- //

    // This value will be updated whenever new hooks are added
    public int hookSupportVersion() {
        return 3;
    }

    // If another plugin is handling insertion of chat tags, this should be used to notify Factions
    public void handleFactionTagExternally(final boolean notByFactions) {
        Conf.chatTagHandledByAnotherPlugin = notByFactions;
    }

    // Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
    // enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

    public boolean shouldLetFactionsHandleThisChat(final AsyncPlayerChatEvent event) {
        if (event == null) return false;
        return this.isPlayerFactionChatting(event.getPlayer()) || this.isFactionsCommand(event.getMessage());
    }

    // Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
    // local chat, or anything else which targets individual recipients, so Faction Chat can be done
    /**
     * @deprecated  As of release 1.8, there is no built in faction chat.
     */
    @Deprecated
    public boolean isPlayerFactionChatting(final Player player) {
        return false;
    }

    // Is this chat message actually a Factions command, and thus should be left alone by other plugins?
    /**
     * @deprecated As of release 1.8.1 the normal Bukkit command-handling is used. 
     */
    @Deprecated
    public boolean isFactionsCommand(final String check) {
        return false;
    }

    // Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
    public String getPlayerFactionTag(final Player player) {
        return this.getPlayerFactionTagRelation(player, null);
    }

    // Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
    public String getPlayerFactionTagRelation(final Player speaker, final Player listener) {
        String tag = "~";

        if (speaker == null) return tag;

        final FPlayer me = FPlayers.i.get(speaker);
        if (me == null) return tag;

        // if listener isn't set, or config option is disabled, give back uncolored tag
        if (listener == null || !Conf.chatParseTagsColored) {
            tag = me.getChatTag().trim();
        } else {
            final FPlayer you = FPlayers.i.get(listener);
            if (you == null) {
                tag = me.getChatTag().trim();
            } else {
                tag = me.getChatTag(you).trim();
            }
        }
        if (tag.isEmpty()) {
            tag = "~";
        }

        return tag;
    }

    // Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
    public String getPlayerTitle(final Player player) {
        if (player == null) return "";

        final FPlayer me = FPlayers.i.get(player);
        if (me == null) return "";

        return me.getTitle().trim();
    }

    // Get a list of all faction tags (names)
    public Set<String> getFactionTags() {
        final Set<String> tags = new HashSet<String>();
        for (final Faction faction : Factions.i.get()) {
            tags.add(faction.getTag());
        }
        return tags;
    }

    // Get a list of all players in the specified faction
    public Set<String> getPlayersInFaction(final String factionTag) {
        final Set<String> players = new HashSet<String>();
        final Faction faction = Factions.i.getByTag(factionTag);
        if (faction != null) {
            for (final FPlayer fplayer : faction.getFPlayers()) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    // Get a list of all online players in the specified faction
    public Set<String> getOnlinePlayersInFaction(final String factionTag) {
        final Set<String> players = new HashSet<String>();
        final Faction faction = Factions.i.getByTag(factionTag);
        if (faction != null) {
            for (final FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    // check if player is allowed to build/destroy in a particular location
    public boolean isPlayerAllowedToBuildHere(final Player player, final Location location) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, location.getBlock(), "", true);
    }

    // check if player is allowed to interact with the specified block (doors/chests/whatever)
    public boolean isPlayerAllowedToInteractWith(final Player player, final Block block) {
        return FactionsPlayerListener.canPlayerUseBlock(player, block, true);
    }

    // check if player is allowed to use a specified item (flint&steel, buckets, etc) in a particular location
    public boolean isPlayerAllowedToUseThisHere(final Player player, final Location location, final Material material) {
        return FactionsPlayerListener.playerCanUseItemHere(player, location, material, true);
    }
}
