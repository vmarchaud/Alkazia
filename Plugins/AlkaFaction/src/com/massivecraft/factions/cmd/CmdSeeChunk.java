package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.VisualizeUtil;

public class CmdSeeChunk extends FCommand {
    public CmdSeeChunk() {
        super();
        this.aliases.add("sc");
        this.aliases.add("seechunk");

        this.permission = Permission.SEE_CHUNK.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        final Location meLoc = this.me.getLocation();
        // Which chunk are we standing in ATM?
        // This bit shifting is something like divide by 16 :P
        final int chunkX = meLoc.getBlockX() >> 4;
        final int chunkZ = meLoc.getBlockZ() >> 4;

        // Get the pillars for that chunk
        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        this.showPillar(this.me, this.me.getWorld(), blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16;
        this.showPillar(this.me, this.me.getWorld(), blockX, blockZ);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 15;
        this.showPillar(this.me, this.me.getWorld(), blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16 + 15;
        this.showPillar(this.me, this.me.getWorld(), blockX, blockZ);
    }

    public void showPillar(final Player player, final World world, final int blockX, final int blockZ) {
        for (int blockY = 0; blockY < world.getMaxHeight(); blockY++) {
            final Location loc = new Location(world, blockX, blockY, blockZ);
            if (loc.getBlock().getTypeId() != 0) {
                continue;
            }
            final int typeId = blockY % 5 == 0 ? Material.GLOWSTONE.getId() : Material.GLASS.getId();
            VisualizeUtil.addLocation(player, loc, typeId);
        }
    }

}
