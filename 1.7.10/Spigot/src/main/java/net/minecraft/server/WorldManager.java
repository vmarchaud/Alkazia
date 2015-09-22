package net.minecraft.server;

import java.util.Iterator;

public class WorldManager implements IWorldAccess {

	private MinecraftServer server;
	public WorldServer world; // CraftBukkit - private -> public

	public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
		server = minecraftserver;
		world = worldserver;
	}

	@Override
	public void a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {
	}

	@Override
	public void a(Entity entity) {
		world.getTracker().track(entity);
	}

	@Override
	public void b(Entity entity) {
		world.getTracker().untrackEntity(entity);
	}

	@Override
	public void a(String s, double d0, double d1, double d2, float f, float f1) {
		// CraftBukkit - this.world.dimension
		server.getPlayerList().sendPacketNearby(d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
	}

	@Override
	public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
		// CraftBukkit - this.world.dimension
		server.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
	}

	@Override
	public void a(int i, int j, int k, int l, int i1, int j1) {
	}

	@Override
	public void a(int i, int j, int k) {
		world.getPlayerChunkMap().flagDirty(i, j, k);
	}

	@Override
	public void b(int i, int j, int k) {
	}

	@Override
	public void a(String s, int i, int j, int k) {
	}

	@Override
	public void a(EntityHuman entityhuman, int i, int j, int k, int l, int i1) {
		// CraftBukkit - this.world.dimension
		server.getPlayerList().sendPacketNearby(entityhuman, j, k, l, 64.0D, world.dimension, new PacketPlayOutWorldEvent(i, j, k, l, i1, false));
	}

	@Override
	public void a(int i, int j, int k, int l, int i1) {
		server.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, j, k, l, i1, true));
	}

	@Override
	public void b(int i, int j, int k, int l, int i1) {
		Iterator iterator = server.getPlayerList().players.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			if (entityplayer != null && entityplayer.world == world && entityplayer.getId() != i) {
				double d0 = j - entityplayer.locX;
				double d1 = k - entityplayer.locY;
				double d2 = l - entityplayer.locZ;

				if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
					entityplayer.playerConnection.sendPacket(new PacketPlayOutBlockBreakAnimation(i, j, k, l, i1));
				}
			}
		}
	}

	@Override
	public void b() {
	}
}
