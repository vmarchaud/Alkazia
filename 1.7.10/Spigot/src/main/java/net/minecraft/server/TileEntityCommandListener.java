package net.minecraft.server;

// CraftBukkit - package-private -> public
public class TileEntityCommandListener extends CommandBlockListenerAbstract {

	final TileEntityCommand a;

	TileEntityCommandListener(TileEntityCommand tileentitycommand) {
		a = tileentitycommand;
		sender = new org.bukkit.craftbukkit.command.CraftBlockCommandSender(this); // CraftBukkit - add sender
	}

	@Override
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(a.x, a.y, a.z);
	}

	@Override
	public World getWorld() {
		return a.getWorld();
	}

	@Override
	public void setCommand(String s) {
		super.setCommand(s);
		a.update();
	}

	@Override
	public void e() {
		a.getWorld().notify(a.x, a.y, a.z);
	}
}
