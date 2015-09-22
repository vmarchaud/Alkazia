package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.util.SmokeUtil;

public class CmdHome extends FCommand {

    public CmdHome() {
        super();
        this.aliases.add("home");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.HOME.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        // TODO: Hide this command on help also.
        if (!Conf.homesEnabled) {
            this.fme.msg("<b>Sorry, Faction homes are disabled on this server.");
            return;
        }

        if (!Conf.homesTeleportCommandEnabled) {
            this.fme.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
            return;
        }

        if (!this.myFaction.hasHome()) {
            this.fme.msg("<b>Your faction does not have a home. " + (this.fme.getRole().isLessThan(Rel.OFFICER) ? "<i> Ask your leader to:" : "<i>You should:"));
            this.fme.sendMessage(this.p.cmdBase.cmdSethome.getUseageTemplate());
            return;
        }

        if (!Conf.homesTeleportAllowedFromEnemyTerritory && this.fme.isInEnemyTerritory()) {
            this.fme.msg("<b>You cannot teleport to your faction home while in the territory of an enemy faction.");
            return;
        }

        if (!Conf.homesTeleportAllowedFromDifferentWorld && this.me.getWorld().getUID() != this.myFaction.getHome().getWorld().getUID()) {
            this.fme.msg("<b>You cannot teleport to your faction home while in a different world.");
            return;
        }

        final Faction faction = Board.getFactionAt(new FLocation(this.me.getLocation()));
        final Location loc = this.me.getLocation().clone();

        // if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
        if (Conf.homesTeleportAllowedEnemyDistance > 0 && faction.getFlag(FFlag.PVP) && (!this.fme.isInOwnTerritory() || this.fme.isInOwnTerritory() && !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory)) {
            final World w = loc.getWorld();
            final double x = loc.getX();
            final double y = loc.getY();
            final double z = loc.getZ();

            for (final Player p : this.me.getServer().getOnlinePlayers()) {
                if (p == null || !p.isOnline() || p.isDead() || p == this.me || p.getWorld() != w) {
                    continue;
                }

                final FPlayer fp = FPlayers.i.get(p);
                if (this.fme.getRelationTo(fp) != Rel.ENEMY) {
                    continue;
                }

                final Location l = p.getLocation();
                final double dx = Math.abs(x - l.getX());
                final double dy = Math.abs(y - l.getY());
                final double dz = Math.abs(z - l.getZ());
                final double max = Conf.homesTeleportAllowedEnemyDistance;

                // box-shaped distance check
                if (dx > max || dy > max || dz > max) {
                    continue;
                }

                this.fme.msg("<b>You cannot teleport to your faction home while an enemy is within " + Conf.homesTeleportAllowedEnemyDistance + " blocks of you.");
                return;
            }
        }
        Location home = this.myFaction.getHome();

        
        if(home.clone().getBlock().getType() != Material.AIR || home.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            home.clone().getBlock().setType(Material.AIR);
            home.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
            home.clone().add(0, 2, 0).getBlock().setType(Material.STONE);
        }
        
        // if Essentials teleport handling is enabled and available, pass the teleport off to it (for delay and cooldown)
        if (EssentialsFeatures.handleTeleport(this.me, this.myFaction.getHome())) return;

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostHome, "to teleport to your faction home", "for teleporting to your faction home")) return;

        // Create a smoke effect
        if (Conf.homesTeleportCommandSmokeEffectEnabled) {
            final List<Location> smokeLocations = new ArrayList<Location>();
            smokeLocations.add(loc);
            smokeLocations.add(loc.add(0, 1, 0));
            smokeLocations.add(this.myFaction.getHome());
            smokeLocations.add(this.myFaction.getHome().clone().add(0, 1, 0));
            SmokeUtil.spawnCloudRandom(smokeLocations, 3f);
        }
        
        
        
        this.me.teleport(this.myFaction.getHome());
    }

}
