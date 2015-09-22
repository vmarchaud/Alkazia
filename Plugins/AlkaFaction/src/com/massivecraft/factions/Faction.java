package com.massivecraft.factions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.persist.Entity;

public class Faction extends Entity implements EconomyParticipator {
    // FIELD: relationWish
    private final Map<String, Rel> relationWish;

    // FIELD: fplayers
    // speedy lookup of players in faction
    private transient Set<FPlayer> fplayers = new HashSet<FPlayer>();

    // FIELD: invites
    // Where string is a lowercase player name
    private final Set<String> invites;

    public void invite(final FPlayer fplayer) {
        this.invites.add(fplayer.getId().toLowerCase());
    }

    public void deinvite(final FPlayer fplayer) {
        this.invites.remove(fplayer.getId().toLowerCase());
    }

    public boolean isInvited(final FPlayer fplayer) {
        return this.invites.contains(fplayer.getId().toLowerCase());
    }

    // FIELD: open
    private boolean open;

    public boolean getOpen() {
        return this.open;
    }

    public void setOpen(final boolean isOpen) {
        this.open = isOpen;
    }

    // FIELD: tag
    private String tag;

    public String getTag() {
        return this.tag;
    }
    
   
    public String getTag(final String prefix) {
        return prefix + this.tag;
    }

    public String getTag(final RelationParticipator observer) {
        if (observer == null) return this.getTag();
        return this.getTag(this.getColorTo(observer).toString());
    }

    public void setTag(String str) {
        if (Conf.factionTagForceUpperCase) {
            str = str.toUpperCase();
        }
        this.tag = str;
    }

    public String getComparisonTag() {
        return MiscUtil.getComparisonString(this.tag);
    }

    // FIELD: description
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String value) {
        this.description = value;
    }

    // FIELD: home
    private LazyLocation home;

    public void setHome(final Location home) {
        this.home = new LazyLocation(home);
    }

    public boolean hasHome() {
        return this.getHome() != null;
    }

    public Location getHome() {
        this.confirmValidHome();
        return this.home != null ? this.home.getLocation().clone() : null;
    }

    // AlkaziaFactions
    public Block getFactionBlock() {
        final Location home = this.getHome();
        if (home == null) return null;
        return home.clone().subtract(0, 1, 0).getBlock();
    }

    // End AlkaziaFactions

    public void confirmValidHome() {
        if (!Conf.homesMustBeInClaimedTerritory || this.home == null || this.home.getLocation() != null && Board.getFactionAt(new FLocation(this.home.getLocation())) == this) return;
        else if (this.home != null) {
        	home.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Conf.factionBlockReplacementMaterial);
        }
        this.msg("<b>Your faction home has been un-set since it is no longer in your territory.");
        this.home = null;
    }

    // FIELD: lastOnlineTime
    private long lastOnlineTime;

    // FIELD: account (fake field)
    // Bank functions
    public double money;

    @Override
    public String getAccountId() {
        final String aid = "faction-" + this.getId();

        // We need to override the default money given to players.
        if (!Econ.hasAccount(aid)) {
            Econ.setBalance(aid, 0);
        }

        return aid;
    }

    // FIELD: cape
    private String cape;

    public String getCape() {
        return this.cape;
    }

    public void setCape(final String val) {
        this.cape = val;
        SpoutFeatures.updateCape(this, null);
    }

    // FIELD: powerBoost
    // special increase/decrease to default and max power for this faction
    private double powerBoost;

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(final double powerBoost) {
        this.powerBoost = powerBoost;
    }

    // FIELDS: Flag management
    // TODO: This will save... defaults if they where changed to...
    private final Map<FFlag, Boolean> flagOverrides; // Contains the modifications to the default values

    public boolean getFlag(final FFlag flag) {
        Boolean ret = this.flagOverrides.get(flag);
        if (ret == null) {
            ret = flag.getDefault();
        }
        return ret;
    }

    public void setFlag(final FFlag flag, final boolean value) {
        if (Conf.factionFlagDefaults.get(flag).equals(value)) {
            this.flagOverrides.remove(flag);
            return;
        }
        this.flagOverrides.put(flag, value);
    }

    // FIELDS: Permission <-> Groups management
    private final Map<FPerm, Set<Rel>> permOverrides; // Contains the modifications to the default values

    public Set<Rel> getPermittedRelations(final FPerm perm) {
        Set<Rel> ret = this.permOverrides.get(perm);
        if (ret == null) {
            ret = perm.getDefault();
        }
        return ret;
    }

    // AlkaziaFactions
    private transient boolean brokenFactionBlock = false;

    public boolean isBrokenFactionBlock() {
        return this.brokenFactionBlock;
    }

    public void setBrokenFactionBlock(final boolean brockenFactionBlock) {
        this.brokenFactionBlock = brockenFactionBlock;
    }

    private transient long lastEnemy = 0;

    public void setLastEnemy(final long lastEnemy) {
        this.lastEnemy = lastEnemy;
    }

    public long getLastEnemy() {
        return this.lastEnemy;
    }

    private transient Inventory inventory;

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    private double xp = 0;

    public double getXP() {
        return this.xp;
    }

    public void setXP(final double xp) {
        this.xp = xp;
    }

    public void addXP(final double xp) {
        final Level nextLevel = Levels.i.get(String.valueOf(this.level.getLevel()));
        if (this.level.getLevel() == 100 || nextLevel == null) {
            this.xp = Math.round(this.xp + xp);
            return;
        }
        final double rest = nextLevel.getXP() - (this.xp + xp);
        if (rest <= 0) {
            this.setLevel(nextLevel, true);
            this.xp = 0;
            this.addXP(-rest);
        } else {
            this.xp = Math.round(this.xp + xp);
        }
    }

    private String levelId;

    public String getLevelId() {
        return this.levelId;
    }

    private transient Level level;

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(final Level level) {
        this.setLevel(level, false);
    }

    public void setLevel(final Level level, final boolean rewards) {
        this.level = level;
        this.levelId = level.getId();
        if (rewards) {
        	for (final FPlayer fplayer : this.getFPlayers()) {
            	if(fplayer.isOnline()) {
                    TitleManager.sendTitle(fplayer.getPlayer(), "", ChatColor.GREEN + ">> Bravo, ta faction " + this.getTag() + " est désormais niveau " + level.getLevel() + " <<");
            	}
            	
                Econ.deposit(fplayer.getName(), level.getMoney());
            }
        }
    }

    public List<String> getAllies() {
        final List<String> allies = new ArrayList<>();
        for (final Faction faction : Factions.i.get()) {
            final Rel relation = faction.getRelationTo(this);
            if (relation != Rel.ALLY) {
                continue;
            }
            allies.add(faction.getTag());
        }
        return allies;
    }

    // End AlkaziaFactions

    /*
    public void addPermittedRelation(FPerm perm, Rel rel)
    {
    	Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
    	newPermittedRelations.addAll(this.getPermittedRelations(perm));
    	newPermittedRelations.add(rel);
    	this.setPermittedRelations(perm, newPermittedRelations);
    }
    
    public void removePermittedRelation(FPerm perm, Rel rel)
    {
    	Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
    	newPermittedRelations.addAll(this.getPermittedRelations(perm));
    	newPermittedRelations.remove(rel);
    	this.setPermittedRelations(perm, newPermittedRelations);
    }*/

    public void setRelationPermitted(final FPerm perm, final Rel rel, final boolean permitted) {
        final Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
        newPermittedRelations.addAll(this.getPermittedRelations(perm));
        if (permitted) {
            newPermittedRelations.add(rel);
        } else {
            newPermittedRelations.remove(rel);
        }
        this.setPermittedRelations(perm, newPermittedRelations);
    }

    public void setPermittedRelations(final FPerm perm, final Set<Rel> rels) {
        if (perm.getDefault().equals(rels)) {
            this.permOverrides.remove(perm);
            return;
        }
        this.permOverrides.put(perm, rels);
    }

    public void setPermittedRelations(final FPerm perm, final Rel... rels) {
        final Set<Rel> temp = new HashSet<Rel>();
        temp.addAll(Arrays.asList(rels));
        this.setPermittedRelations(perm, temp);
    }

    // -------------------------------------------- //
    // Construct
    // -------------------------------------------- //

    public Faction() {
        this.relationWish = new HashMap<String, Rel>();
        this.invites = new HashSet<String>();
        this.open = Conf.newFactionsDefaultOpen;
        this.tag = "???";
        this.description = "Default faction description :(";
        this.money = 0.0;
        this.powerBoost = 0.0;
        this.flagOverrides = new LinkedHashMap<FFlag, Boolean>();
        this.permOverrides = new LinkedHashMap<FPerm, Set<Rel>>();
        // AlkaziaFactions
        this.levelId = "0";
        // End AlkaziaFactions
    }

    // -------------------------------
    // Understand the types
    // -------------------------------

    // TODO: These should be gone after the refactoring...

    public boolean isNormal() {
        // AlkaziaFactions
        return !this.getId().equals("0") && !this.getId().equals("-1") && !this.getId().equals("-2");
        //return ! this.isNone();
        // End AlkaziaFactions
    }

    public boolean isNone() {
        return this.getId().equals("0");
    }

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    public String describeTo(final RelationParticipator observer, final boolean ucfirst) {
        return RelationUtil.describeThatToMe(this, observer, ucfirst);
    }

    @Override
    public String describeTo(final RelationParticipator observer) {
        return RelationUtil.describeThatToMe(this, observer);
    }

    @Override
    public Rel getRelationTo(final RelationParticipator observer) {
        return RelationUtil.getRelationOfThatToMe(this, observer);
    }

    @Override
    public Rel getRelationTo(final RelationParticipator observer, final boolean ignorePeaceful) {
        return RelationUtil.getRelationOfThatToMe(this, observer, ignorePeaceful);
    }

    @Override
    public ChatColor getColorTo(final RelationParticipator observer) {
        return RelationUtil.getColorOfThatToMe(this, observer);
    }

    public Rel getRelationWish(final Faction otherFaction) {
        if (this.relationWish.containsKey(otherFaction.getId())) return this.relationWish.get(otherFaction.getId());
        return Rel.NEUTRAL;
    }

    public void setRelationWish(final Faction otherFaction, final Rel relation) {
        if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Rel.NEUTRAL)) {
            this.relationWish.remove(otherFaction.getId());
        } else {
            this.relationWish.put(otherFaction.getId(), relation);
        }
    }

    public Map<Rel, List<String>> getFactionTagsPerRelation(final RelationParticipator rp) {
        return this.getFactionTagsPerRelation(rp, false);
    }

    // onlyNonNeutral option provides substantial performance boost on large servers for listing only non-neutral factions
    public Map<Rel, List<String>> getFactionTagsPerRelation(final RelationParticipator rp, final boolean onlyNonNeutral) {
        final Map<Rel, List<String>> ret = new HashMap<Rel, List<String>>();
        for (final Rel rel : Rel.values()) {
            ret.put(rel, new ArrayList<String>());
        }
        for (final Faction faction : Factions.i.get()) {
            final Rel relation = faction.getRelationTo(this);
            if (onlyNonNeutral && relation == Rel.NEUTRAL) {
                continue;
            }
            ret.get(relation).add(faction.getTag(rp));
        }
        return ret;
    }

    // TODO: Implement a has enough feature.
    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    public double getPower() {
        if (this.getFlag(FFlag.INFPOWER)) return 999999;

        double ret = 0;
        for (final FPlayer fplayer : this.fplayers) {
            ret += fplayer.getPower();
        }
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        } else if (this.level.getMaxPower() > 0 && ret > this.level.getMaxPower()) {
            ret = this.level.getMaxPower();
        }
        // End AlkaziaFactions
        return ret + this.powerBoost;
    }

    public double getPowerMax() {
        if (this.getFlag(FFlag.INFPOWER)) return 999999;

        double ret = 0;
        for (final FPlayer fplayer : this.fplayers) {
            ret += fplayer.getPowerMax();
        }
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        } else if (this.level.getMaxPower() > 0 && ret > this.level.getMaxPower()) {
            ret = this.level.getMaxPower();
        }
        // End AlkaziaFactions
        return ret + this.powerBoost;
    }

    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    public int getLandRounded() {
        return Board.getFactionCoordCount(this);
    }

    public int getLandRoundedInWorld(final String worldName) {
        return Board.getFactionCoordCountInWorld(this, worldName);
    }

    public boolean hasLandInflation() {
        return this.getLandRounded() > this.getPowerRounded();
    }

    // -------------------------------
    // FPlayers
    // -------------------------------

    // maintain the reference list of FPlayers in this faction
    public void refreshFPlayers() {
        this.fplayers.clear();
        if (this.isNone()) return;

        for (final FPlayer fplayer : FPlayers.i.get())
            if (fplayer.getFaction() == this) {
                this.fplayers.add(fplayer);
            }
    }

    protected boolean addFPlayer(final FPlayer fplayer) {
        if (this.isNone()) return false;

        return this.fplayers.add(fplayer);
    }

    protected boolean removeFPlayer(final FPlayer fplayer) {
        if (this.isNone()) return false;

        return this.fplayers.remove(fplayer);
    }

    public Set<FPlayer> getFPlayers() {
        // return a shallow copy of the FPlayer list, to prevent tampering and concurrency issues
        final Set<FPlayer> ret = new HashSet<FPlayer>(this.fplayers);
        return ret;
    }

    public Set<FPlayer> getFPlayersWhereOnline(final boolean online) {
        final Set<FPlayer> ret = new HashSet<FPlayer>();

        for (final FPlayer fplayer : this.fplayers)
            if (fplayer.isOnline() == online) {
                ret.add(fplayer);
            }

        return ret;
    }

    public FPlayer getFPlayerLeader() {
        //if ( ! this.isNormal()) return null;

        for (final FPlayer fplayer : this.fplayers)
            if (fplayer.getRole() == Rel.LEADER) return fplayer;
        return null;
    }

    public ArrayList<FPlayer> getFPlayersWhereRole(final Rel role) {
        final ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
        //if ( ! this.isNormal()) return ret;

        for (final FPlayer fplayer : this.fplayers)
            if (fplayer.getRole() == role) {
                ret.add(fplayer);
            }

        return ret;
    }

    public ArrayList<Player> getOnlinePlayers() {
        final ArrayList<Player> ret = new ArrayList<Player>();
        //if (this.isPlayerFreeType()) return ret;

        for (final Player player : P.p.getServer().getOnlinePlayers()) {
            final FPlayer fplayer = FPlayers.i.get(player);
            if (fplayer.getFaction() == this) {
                ret.add(player);
            }
        }

        return ret;
    }

    // used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
    public void promoteNewLeader() {
        if (!this.isNormal()) return;
        if (this.getFlag(FFlag.PERMANENT) && Conf.permanentFactionsDisableLeaderPromotion) return;

        final FPlayer oldLeader = this.getFPlayerLeader();

        // get list of officers, or list of normal members if there are no officers
        ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Rel.OFFICER);
        if (replacements == null || replacements.isEmpty()) {
            replacements = this.getFPlayersWhereRole(Rel.MEMBER);
        }

        if (replacements == null || replacements.isEmpty()) { // faction leader is the only member; one-man faction
            if (this.getFlag(FFlag.PERMANENT)) {
                if (oldLeader != null) {
                    oldLeader.setRole(Rel.MEMBER);
                }
                return;
            }

            // no members left and faction isn't permanent, so disband it
            if (Conf.logFactionDisband) {
                P.p.log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
            }

            for (final FPlayer fplayer : FPlayers.i.getOnline()) {
                fplayer.msg("The faction %s<i> was disbanded.", this.getTag(fplayer));
            }

            this.detach();
        } else { // promote new faction leader
            if (oldLeader != null) {
                oldLeader.setRole(Rel.MEMBER);
            }
            replacements.get(0).setRole(Rel.LEADER);
            this.msg("<i>Faction leader <h>%s<i> has been removed. %s<i> has been promoted as the new faction leader.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
            P.p.log("Faction " + this.getTag() + " (" + this.getId() + ") leader was removed. Replacement leader: " + replacements.get(0).getName());
        }
    }

    //----------------------------------------------//
    // Messages
    //----------------------------------------------//
    @Override
    public void msg(String message, final Object... args) {
        message = P.p.txt.parse(message, args);

        for (final FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(final String message) {
        for (final FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(final List<String> messages) {
        for (final FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(messages);
        }
    }

    //----------------------------------------------//
    // Offline Faction Protection
    //----------------------------------------------//
    public void updateLastOnlineTime() {
        // We have either gained or a lost a player.
        if (this.isNone()) return;

        this.lastOnlineTime = System.currentTimeMillis();
    }

    public boolean hasOfflineExplosionProtection() {
        if (!Conf.protectOfflineFactionsFromExplosions || this.isNone()) return false;

        final long timeUntilNoboom = this.getLastOnlineTime() + (long) (Conf.offlineExplosionProtectionDelay * 60 * 1000);

        //No protection if players are online.
        if (this.getOnlinePlayers().size() > 0) return false;

        // No Protection while timeUntilNoboom is greater than current system time.
        if (timeUntilNoboom > System.currentTimeMillis()) return false;

        return true;
    }

    public long getLastOnlineTime() {
        return this.lastOnlineTime;
    }

    //----------------------------------------------//
    // Deprecated
    //----------------------------------------------//
    /**
     * @deprecated  As of release 1.7, replaced by {@link #getFPlayerLeader()}
     */
    @Deprecated
    public FPlayer getFPlayerAdmin() {
        return this.getFPlayerLeader();
    }

    /**
     * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
     */
    @Deprecated
    public boolean isPeaceful() {
        return this.getFlag(FFlag.PEACEFUL);
    }

    /**
     * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
     */
    @Deprecated
    public boolean getPeacefulExplosionsEnabled() {
        return this.getFlag(FFlag.EXPLOSIONS);
    }

    /**
     * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
     */
    @Deprecated
    public boolean noExplosionsInTerritory() {
        return !this.getFlag(FFlag.EXPLOSIONS);
    }

    /**
     * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
     */
    @Deprecated
    public boolean isSafeZone() {
        return !this.getFlag(FFlag.EXPLOSIONS);
    }

    //----------------------------------------------//
    // Persistance and entity management
    //----------------------------------------------//

    @Override
    public void postDetach() {
        if (Econ.shouldBeUsed()) {
            Econ.setBalance(this.getAccountId(), 0);
        }

        // AlkaziaFactions
        if (this.home != null) {
            this.getFactionBlock().setType(Conf.factionBlockReplacementMaterial);
        }
        for (final Player player : this.getOnlinePlayers()) {
            for (final String permission : this.level.getPermissions()) {
                player.addAttachment(P.p, permission, false);
            }
        }

        // Clean the board
        Board.clean();

        // Clean the fplayers
        FPlayers.i.clean();
    }
}
