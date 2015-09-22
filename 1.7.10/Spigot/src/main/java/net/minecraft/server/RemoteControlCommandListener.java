package net.minecraft.server;

public class RemoteControlCommandListener implements ICommandListener {

	public static final RemoteControlCommandListener instance = new RemoteControlCommandListener();
	private StringBuffer b = new StringBuffer();

	public RemoteControlCommandListener() {
	}

	public void e() {
		b.setLength(0);
	}

	public String f() {
		return b.toString();
	}

	@Override
	public String getName() {
		return "Rcon";
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatComponentText(getName());
	}

	// CraftBukkit start - Send a String
	public void sendMessage(String message) {
		b.append(message);
	}

	// CraftBukkit end

	@Override
	public void sendMessage(IChatBaseComponent ichatbasecomponent) {
		b.append(ichatbasecomponent.c());
	}

	@Override
	public boolean a(int i, String s) {
		return true;
	}

	@Override
	public ChunkCoordinates getChunkCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}

	@Override
	public World getWorld() {
		return MinecraftServer.getServer().getWorld();
	}
}
