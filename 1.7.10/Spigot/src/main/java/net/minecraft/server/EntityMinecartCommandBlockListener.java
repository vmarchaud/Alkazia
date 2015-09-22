package net.minecraft.server;

// CraftBukkit - package-private -> public
public class EntityMinecartCommandBlockListener extends CommandBlockListenerAbstract {

	final EntityMinecartCommandBlock a;

	EntityMinecartCommandBlockListener(EntityMinecartCommandBlock entityminecartcommandblock) {
		a = entityminecartcommandblock;
		sender = (org.bukkit.craftbukkit.entity.CraftMinecartCommand) entityminecartcommandblock.getBukkitEntity(); // CraftBukkit - Set the sender
	}

	@Override
	public void e() {
		a.getDataWatcher().watch(23, getCommand());
		a.getDataWatcher().watch(24, ChatSerializer.a(h()));
	}

	@Override
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(MathHelper.floor(a.locX), MathHelper.floor(a.locY + 0.5D), MathHelper.floor(a.locZ));
	}

	@Override
	public World getWorld() {
		return a.world;
	}
}
