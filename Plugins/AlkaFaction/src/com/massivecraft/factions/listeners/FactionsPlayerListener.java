package com.massivecraft.factions.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.NumberConversions;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.TitleManager;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.struct.TerritoryAccess;
import com.massivecraft.factions.util.VisualizeUtil;

import be.maximvdw.featherboard.api.PlaceholderAPI;

public class FactionsPlayerListener implements Listener {
    public P p;

    public FactionsPlayerListener(final P p) {
        this.p = p;
    }

    // AlkaziaFactions
    @EventHandler(ignoreCancelled = true)
    public void onFPlayerLeave(final FPlayerLeaveEvent event) {
        for (final String permission : event.getFaction().getLevel().getPermissions()) {
            event.getFPlayer().getPlayer().addAttachment(this.p, permission, false);
        }
    }

    // End AlkaziaFactions

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Make sure that all online players do have a fplayer.
        final FPlayer me = FPlayers.i.get(event.getPlayer());

        // Update the lastLoginTime for this fplayer
        me.setLastLoginTime(System.currentTimeMillis());

        // Store player's current FLocation and notify them where they are
        me.setLastStoodAt(new FLocation(event.getPlayer().getLocation()));
        if (!SpoutFeatures.updateTerritoryDisplay(me)) {
            me.sendFactionHereMessage();
        }

        // Set NoBoom timer update.
        final Faction faction = me.getFaction();
        if (me.hasFaction() && Conf.protectOfflineFactionsFromExplosions) {
            faction.updateLastOnlineTime();
        }

        // AlkaziaFactions
        if (me.hasFaction()) {
            for (final String permission : faction.getLevel().getPermissions()) {
                event.getPlayer().addAttachment(this.p, permission, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final FPlayer me = FPlayers.i.get(event.getPlayer());

        // AlkaziaFactions
        if (me.hasFaction()) {
            for (final String permission : me.getFaction().getLevel().getPermissions()) {
                event.getPlayer().addAttachment(P.p, permission, false);
            }
        }

        // Make sure player's power is up to date when they log off.
        me.getPower();
        // and update their last login time to point to when the logged off, for
        // auto-remove routine
        me.setLastLoginTime(System.currentTimeMillis());

        SpoutFeatures.playerDisconnect(me);

        // Set NoBoom timer update.
        final Faction faction = me.getFaction();
        if (me.hasFaction() && Conf.protectOfflineFactionsFromExplosions) {
            faction.updateLastOnlineTime();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        // AlkaziaFactions
        final Location locFrom = event.getFrom();
        final Location locTo = event.getTo();
        if (locFrom.getBlockX() == locTo.getBlockX() && locFrom.getBlockY() == locTo.getBlockY() && locFrom.getBlockZ() == locTo.getBlockZ()) return;

        final Player player = event.getPlayer();
        final FPlayer me = FPlayers.i.get(player);

        // Did we change coord?
        final FLocation from = me.getLastStoodAt();
        final FLocation to = new FLocation(event.getTo());

        // AlkaziaFactions
        final Faction faction = Board.getFactionAt(to);
        final Location factionHome = me.hasFaction() ? faction.getHome() : null;
        if (factionHome != null && !me.getFaction().isBrokenFactionBlock() && me.getFaction() != faction) {
            final String key = "factions.fakub." + faction.getTag();
            if (locTo.distanceSquared(factionHome) <= 4 && (!player.hasMetadata(key) || player.getMetadata(key).size() == 0 || System.currentTimeMillis() - player.getMetadata(key).get(0).asLong() > 300000)) {
                if (me.getFaction().getRelationTo(faction, true) != Rel.ALLY) {
                    player.removeMetadata(key, this.p);
                    player.setMetadata(key, new FixedMetadataValue(this.p, System.currentTimeMillis()));
                    for(FPlayer p : faction.getFPlayers()) {
                    	if(p.isOnline()) {
                    		TitleManager.sendTitle(p.getPlayer(), "", ChatColor.RED + "Un joueur de la faction " + me.getFaction().getTag() + " est proche de votre Fakub !");
                    	}
                    }
                    for(FPlayer p : me.getFaction().getFPlayers()) {
                    	if(p.isOnline()) {
                    		TitleManager.sendTitle(p.getPlayer(), "", ChatColor.GREEN + "Un joueur de ta faction est proche du fakub de la faction " + faction.getTag());
                    	}
                    }
					
				}
                
            } else if (factionHome.getChunk() == player.getLocation().getChunk()) if (me.getFaction().getRelationTo(faction, true) != Rel.ALLY) {
                faction.setLastEnemy(System.currentTimeMillis());
            }
        }

        // quick check to make sure player is moving between chunks; good
        // performance boost
        if (locFrom.getBlockX() >> 4 == locTo.getBlockX() >> 4 && locFrom.getBlockZ() >> 4 == locTo.getBlockZ() >> 4 && locFrom.getWorld() == locTo.getWorld()) return;
        // End AlkaziaFactions

        if (from.equals(to)) return;

        // Yes we did change coord (:

        me.setLastStoodAt(to);
        final TerritoryAccess access = Board.getTerritoryAccessAt(to);

        // Did we change "host"(faction)?
        final boolean changedFaction = Board.getFactionAt(from) != access.getHostFaction();

        // let Spout handle most of this if it's available
        final boolean handledBySpout = changedFaction && SpoutFeatures.updateTerritoryDisplay(me);

        if (me.isMapAutoUpdating()) {
            me.sendMessage(Board.getMap(me.getFaction(), to, player.getLocation().getYaw()));
        } else if (changedFaction && !handledBySpout) {
            me.sendFactionHereMessage();
        }

        // show access info message if needed
        if (!handledBySpout && !SpoutFeatures.updateAccessInfo(me) && !access.isDefault()) if (access.subjectHasAccess(me)) {
            me.msg("<g>Vous avez accés à ce claim.");
        } else if (access.subjectAccessIsRestricted(me)) {
            me.msg("<b>Ce claim a un accés restreint.");
        }

        if (me.getAutoClaimFor() != null) {
            me.attemptClaim(me.getAutoClaimFor(), event.getTo(), true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        // only need to check right-clicks and physical as of MC 1.4+; good
        // performance boost
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;

        final Block block = event.getClickedBlock();
        final Player player = event.getPlayer();
        final FPlayer me = FPlayers.i.get(player.getName());
        
        if (block == null) return; // clicked in air, apparently
        
        if (!FactionsPlayerListener.canPlayerUseBlock(player, block, false)) {
            event.setCancelled(true);
            if (Conf.handleExploitInteractionSpam) {
                final String name = player.getName();
                InteractAttemptSpam attempt = this.interactSpammers.get(name);
                if (attempt == null) {
                    attempt = new InteractAttemptSpam();
                    this.interactSpammers.put(name, attempt);
                }
                final int count = attempt.increment();
                if (count >= 10) {
                    me.msg("<b> Sa ne sert à rien de forcer, il/elle ne s'ouvrira pas !");
                    player.damage(NumberConversions.floor((double) count / 10));
                }
            }
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; // only interested on right-clicks for below

        if (!FactionsPlayerListener.playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
            event.setCancelled(true);
            return;
        }
        
        if(event.getItem() != null && event.getItem().getType() == Material.ARMOR_STAND && Board.getFactionAt(player.getLocation()).getTag().contains("WarZone")) {
        	me.sendMessage("<b> Vous ne pouvez pas poser d'armorstand en warzone.");
        	event.setCancelled(true);
        	return;
        }
        
        // AlkaziaFactions
        else if (block.getType() == Conf.factionBlockMaterial) {
            final Location blockLoc = block.getLocation();
            final Faction faction = Board.getFactionAt(blockLoc);
            if (faction.isNormal() && faction.hasHome() && faction.getHome().getChunk() == blockLoc.getChunk() && faction.getRelationTo(FPlayers.i.get(player)) != Rel.ENEMY) {
                event.setCancelled(true);
                player.openInventory(faction.getInventory());
            }
            return;
        }
        // End AlkaziaFactions
    }

    // for handling people who repeatedly spam attempts to open a door (or
    // similar) in another faction's territory
    private final Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();

    private static class InteractAttemptSpam {
        private int attempts = 0;
        private long lastAttempt = System.currentTimeMillis();

        // returns the current attempt count
        public int increment() {
            final long Now = System.currentTimeMillis();
            if (Now > this.lastAttempt + 2000) {
                this.attempts = 1;
            } else {
                this.attempts++;
            }
            this.lastAttempt = Now;
            return this.attempts;
        }
    }

    // TODO: Refactor ! justCheck -> to informIfNot
    // TODO: Possibly incorporate pain build...
    public static boolean playerCanUseItemHere(final Player player, final Location loc, final Material material, final boolean justCheck) {
        final String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) return true;

        final FPlayer me = FPlayers.i.get(name);
        if (me.hasAdminMode()) return true;
        if (Conf.materialsEditTools.contains(material) && !FPerm.BUILD.has(me, loc, !justCheck)) return false;
        return true;
    }

    public static boolean canPlayerUseBlock(final Player player, final Block block, final boolean justCheck) {
        final String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) return true;

        final FPlayer me = FPlayers.i.get(name);
        if (me.hasAdminMode()) return true;
        final Location loc = block.getLocation();
        final Material material = block.getType();

        if (Conf.materialsEditOnInteract.contains(material) && !FPerm.BUILD.has(me, loc, !justCheck)) return false;
        if (Conf.materialsContainer.contains(material) && !FPerm.CONTAINER.has(me, loc, !justCheck)) return false;
        if (Conf.materialsDoor.contains(material) && !FPerm.DOOR.has(me, loc, !justCheck)) return false;
        if (material == Material.STONE_BUTTON && !FPerm.BUTTON.has(me, loc, !justCheck)) return false;
        if (material == Material.LEVER && !FPerm.LEVER.has(me, loc, !justCheck)) return false;
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final FPlayer me = FPlayers.i.get(event.getPlayer());

        me.getPower(); // update power, so they won't have gained any while dead

        final Location home = me.getFaction().getHome(); // TODO: WARNING FOR NPE HERE
        // THE ORIO FOR RESPAWN
        // SHOULD BE ASSIGNABLE FROM
        // CONFIG.
        if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null && (Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
            event.setRespawnLocation(home);
        }
    }

    // For some reason onPlayerInteract() sometimes misses bucket events
    // depending on distance (something like 2-3 blocks away isn't detected),
    // but these separate bucket events below always fire without fail
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;

        final Block block = event.getBlockClicked();
        final Player player = event.getPlayer();

        if (!FactionsPlayerListener.playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        final Block block = event.getBlockClicked();
        final Player player = event.getPlayer();

        if (!FactionsPlayerListener.playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        // Get the player
        final Player player = event.getPlayer();
        final FPlayer me = FPlayers.i.get(player);

        // With adminmode no commands are denied.
        if (me.hasAdminMode()) return;

        // The full command is converted to lowercase and does include the slash
        // in the front
        final String fullCmd = event.getMessage().toLowerCase();

        if (me.hasFaction() && me.getFaction().getFlag(FFlag.PERMANENT) && FactionsPlayerListener.isCommandInList(fullCmd, Conf.permanentFactionMemberDenyCommands)) {
            me.msg("<b>Vous ne pouvez pas utiliser \"" + fullCmd + "\" parce que vous êtes dans une faction permanente.");
            event.setCancelled(true);
            return;
        }

        final Rel rel = me.getRelationToLocation();
        if (Board.getFactionAt(me.getLastStoodAt()).isNone()) return;
        
        if (rel == Rel.NEUTRAL && FactionsPlayerListener.isCommandInList(fullCmd, Conf.territoryNeutralDenyCommands)) {
            me.msg("<b>Vous ne pouvez pas utiliser cette commande dans un claim neutre.");
            event.setCancelled(true);
            return;
        }

        if (rel == Rel.ENEMY && FactionsPlayerListener.isCommandInList(fullCmd, Conf.territoryEnemyDenyCommands)) {
            me.msg("<b>Vous ne pouvez pas utiliser cette commande dans un claim enemie.");
            event.setCancelled(true);
            return;
        }

        return;
    }

    private static boolean isCommandInList(final String fullCmd, final Collection<String> strings) {
        final String shortCmd = fullCmd.substring(1);
        final Iterator<String> iter = strings.iterator();
        while (iter.hasNext()) {
            String cmdCheck = iter.next();
            if (cmdCheck == null) {
                iter.remove();
                continue;
            }
            cmdCheck = cmdCheck.toLowerCase();
            if (fullCmd.startsWith(cmdCheck)) return true;
            if (shortCmd.startsWith(cmdCheck)) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(final PlayerKickEvent event) {
        if (event.isCancelled()) return;

        final FPlayer badGuy = FPlayers.i.get(event.getPlayer());
        if (badGuy == null) return;

        SpoutFeatures.playerDisconnect(badGuy);

        // if player was banned (not just kicked), get rid of their stored info
        if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
            if (badGuy.getRole() == Rel.LEADER) {
                badGuy.getFaction().promoteNewLeader();
            }
            badGuy.leave(false);
            badGuy.detach();
        }
    }
    
    @EventHandler(priority= EventPriority.LOWEST, ignoreCancelled = true)
    public void rightclickarmor(PlayerInteractAtEntityEvent e)
    {
    	if(e.isCancelled()) return;
    	if (e.getRightClicked() == null || e.getRightClicked().getType() != EntityType.ARMOR_STAND) return;
    	if(e.getRightClicked().getCustomName() != null && (e.getRightClicked().getCustomName().contains("Nether") || e.getRightClicked().getCustomName().contains("Farm") || e.getRightClicked().getCustomName().contains("Gardien du KOTH"))) return;
    	
  	   FPlayer player = FPlayers.i.get((Player) e.getPlayer());
      if (!Board.getFactionAt(e.getRightClicked().getLocation()).isNone() && !(Board.getFactionAt(e.getRightClicked().getLocation()) == player.getFaction())) {
          player.msg("<b> Vous ne pouvez pas prendre des items sur un Armor Stand ici.");
          e.setCancelled(true);
      }
    }

    // -------------------------------------------- //
    // VisualizeUtil
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveClearVisualizations(final PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        final Block blockFrom = event.getFrom().getBlock();
        final Block blockTo = event.getTo().getBlock();
        if (blockFrom.equals(blockTo)) return;

        VisualizeUtil.clear(event.getPlayer());
    }
    
}
