package com.massivecraft.factions.listeners;

import java.util.Map;

import net.minecraft.server.v1_8_R1.BlockSlime;
import net.minecraft.server.v1_8_R1.Blocks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Level;
import com.massivecraft.factions.Levels;
import com.massivecraft.factions.P;
import com.massivecraft.factions.TitleManager;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;

public class FactionsBlockListener implements Listener {
    public P p;

    public FactionsBlockListener(final P p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (event.isCancelled()) return;
        if (event.getSource().getTypeId() != 51) return; // Must be Fire
        final Faction faction = Board.getFactionAt(event.getBlock());
        if (faction.getFlag(FFlag.FIRESPREAD) == false) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBurn(final BlockBurnEvent event) {
        if (event.isCancelled()) return;
        final Faction faction = Board.getFactionAt(event.getBlock());
        if (faction.getFlag(FFlag.FIRESPREAD) == false) {
            event.setCancelled(true);
        }
    }

    public static boolean playerCanBuildDestroyBlock(final Player player, final Block block, final String action, final boolean justCheck) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), action, justCheck);
    }

    public static boolean playerCanBuildDestroyBlock(final Player player, final Location location, final String action, final boolean justCheck) {
        final String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) return true;

        final FPlayer me = FPlayers.i.get(name);
        if (me.hasAdminMode()) return true;

        final FLocation loc = new FLocation(location);
        final Faction factionHere = Board.getFactionAt(loc);

        if (!FPerm.BUILD.has(me, location) && FPerm.PAINBUILD.has(me, location)) {
            if (!justCheck) {
                me.msg("<b>C'est inutile de %s dans le claim de %s<b>.", action, factionHere.describeTo(me));
                player.damage(Conf.actionDeniedPainAmount);
            }
            return true;
        }

        return FPerm.BUILD.has(me, loc, true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (!event.canBuild()) return;

        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "build", false)) {
            event.setCancelled(true);
        } else {
            final FPlayer me = FPlayers.i.get(event.getPlayer());
            final Faction pfaction = me.getFaction();
            final Location loc = event.getBlock().getLocation();
            if (me.hasFaction() && pfaction.isNormal() && pfaction == Board.getFactionAt(loc) && pfaction.hasHome()) {
                final Location home = pfaction.getHome();
                if (home.getBlockX() == loc.getBlockX() && (home.getBlockY() == loc.getBlockY() || home.getBlockY() + 1 == loc.getBlockY()) && home.getBlockZ() == loc.getBlockZ()) {
                    event.setCancelled(true);
                }
            }
        }
        // End AlkaziaFactions
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) return;

        // AlkaziaFactions
        final Player player = event.getPlayer();
        final FPlayer me = FPlayers.i.get(player);
        final Faction factionHere = Board.getFactionAt(event.getBlock());
        final Faction pfaction = me.getFaction();
        final Location location = event.getBlock().getLocation();
        if (event.getBlock().getType() == Conf.factionBlockMaterial && me.hasFaction() && factionHere != pfaction && !factionHere.isBrokenFactionBlock() && factionHere.hasHome()) {
            final Location home = factionHere.getHome();
            if (home.getChunk() == location.getChunk()) {
                event.setCancelled(true);
                if (!P.homes.contains(pfaction)) {
                    me.msg("<b>Vous ne pouvez pas attaquer de fakub tant que vous n'en avez pas. (Il faut au moins l'avoir depuis une journée)");
                } else if (factionHere.getRelationTo(pfaction) == Rel.ALLY) {
                    me.msg("<b>Vous ne pouvez pas piller vos alliés.");
                } else if (pfaction.isNone()) {
                    me.msg("<b>Vous devez avoir une faction pour en attaquer une autre.");
                } else if (factionHere.getLevel().getLevel() < 5) {
                    me.msg("<b>Vous ne pouvez pas piller une faction qui a moins de 5 niveaux.");
                } else if (P.allies.containsKey(pfaction.getTag()) && P.allies.get(pfaction.getTag()).contains(factionHere.getTag())) {
                    me.msg("<b>Vous ne pouvez pas piller une faction avec laquelle vous étiez alliés il y a moins de 24 heures.");
                } else {
                    final Level level = factionHere.getLevel();
                    for (int i = 1; i < 4; i++) {
                        pfaction.addXP(Levels.i.get(String.valueOf(level.getLevel() - i)).getXP());
                    }
                    factionHere.setBrokenFactionBlock(true);
                    factionHere.setLevel(Levels.i.get(String.valueOf(factionHere.getLevel().getLevel() - 4)));
                    event.getBlock().setType(Conf.brokenFactionBlockMaterial);
                    me.msg("<g>Vous venez de piller et de récupérer 3 niveaux de la faction " + factionHere.getTag() + ".");
                    for(FPlayer p : me.getFaction().getFPlayers()) {
                    	if(p.isOnline()) {
                    		TitleManager.sendTitle(p.getPlayer(), "", ChatColor.GREEN + "Un joueur de ta faction a cassé le fakub de la faction " + me.getFaction().getTag());
                    	}
                    }
                    for(FPlayer p : factionHere.getFPlayers()) {
                    	if(p.isOnline()) {
                    		TitleManager.sendTitle(p.getPlayer(), "", ChatColor.RED + "Un joueur de la faction " + me.getFaction().getTag() + " a cassé votre Fakub !");
                    	}
                    }
                }
                return;
            }
        }

        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy", false) || factionHere == pfaction && (event.getBlock().getType() == Conf.factionBlockMaterial || event.getBlock().getType() == Conf.brokenFactionBlockMaterial) && pfaction.hasHome() && pfaction.getHome().getChunk() == location.getChunk()) {
            event.setCancelled(true);
        } else {
            final Map<Enchantment, Integer> enchants = player.getItemInHand().getEnchantments();
            if ((event.getBlock().getType() == Material.POWDER_BLOCK || event.getBlock().getType() == Material.METEOR_ORE) && !enchants.containsKey(Enchantment.SILK_TOUCH) && !pfaction.isNone()) {
                pfaction.addXP(20);
            }
        }
        // End AlkaziaFactions
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDamage(final BlockDamageEvent event) {
        if (event.isCancelled()) return;
        if (!event.getInstaBreak()) return;

        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;

        final Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

        // target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
        final Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

        // members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
        final Faction targetFaction = Board.getFactionAt(new FLocation(targetBlock));
        // AlkaziaFactions
        final Location targetLoc = targetBlock.getLocation();
        if (targetFaction == pistonFaction) {
            if (targetFaction.hasHome()) {
                final Location home = targetFaction.getHome();
                
                if (home.getBlockX() == targetLoc.getBlockX() && (home.getBlockY() == targetLoc.getBlockY() || home.getBlockY() + 1 == targetLoc.getBlockY()) && home.getBlockZ() == targetLoc.getBlockZ()) {
                    event.setCancelled(true);
                }
            }
            return;
        }
        // End AlkaziaFactions
        

        if (!Conf.pistonProtectionThroughDenyBuild) return;
        
        // if potentially pushing into air/water/lava in another territory, we need to check it out
        // AlkaziaFactions
        // if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !FPerm.BUILD.has(pistonFaction, targetBlock.getLocation())) event.setCancelled(true);
        if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !FPerm.BUILD.has(pistonFaction, targetLoc)) {
            event.setCancelled(true);
            // End AlkaziaFactions
        }

        /*
         * note that I originally was testing the territory of each affected block, but since I found that pistons can only push
         * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
         * only the final target block as done above
         */
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        // if not a sticky piston, retraction should be fine
        if (event.isCancelled() || !event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) return;

        final Location targetLoc = event.getRetractLocation();

        // if potentially retracted block is just air/water/lava, no worries
        if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) return;

        final Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

        // members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
        final Faction targetFaction = Board.getFactionAt(new FLocation(targetLoc));
        // AlkaziaFactions
        if (targetFaction == pistonFaction) {
            if (targetFaction.hasHome()) {
                final Location home = targetFaction.getHome();
                if (home.getBlockX() == targetLoc.getBlockX() && (home.getBlockY() == targetLoc.getBlockY() || home.getBlockY() + 1 == targetLoc.getBlockY()) && home.getBlockZ() == targetLoc.getBlockZ()) {
                    event.setCancelled(true);
                }
            }
            return;
        }
        // End AlkaziaFactions

        if (!FPerm.BUILD.has(pistonFaction, targetLoc)) {
            event.setCancelled(true);
        }
    }
}
