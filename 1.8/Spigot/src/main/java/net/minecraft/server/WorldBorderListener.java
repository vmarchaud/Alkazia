package net.minecraft.server;

class WorldBorderListener implements IWorldBorderListener {

    final PlayerList a;

    WorldBorderListener(PlayerList playerlist) {
        this.a = playerlist;
    }

    public void a(WorldBorder worldborder, double d0) {
        this.a.sendAll(new PacketPlayOutWorldBorder(worldborder, EnumWorldBorderAction.SET_SIZE), worldborder.world); // CraftBukkit
    }

    public void a(WorldBorder worldborder, double d0, double d1, long i) {
        this.a.sendAll(new PacketPlayOutWorldBorder(worldborder, EnumWorldBorderAction.LERP_SIZE), worldborder.world); // CraftBukkit
    }

    public void a(WorldBorder worldborder, double d0, double d1) {
        this.a.sendAll(new PacketPlayOutWorldBorder(worldborder, EnumWorldBorderAction.SET_CENTER), worldborder.world); // CraftBukkit
    }

    public void a(WorldBorder worldborder, int i) {
        this.a.sendAll(new PacketPlayOutWorldBorder(worldborder, EnumWorldBorderAction.SET_WARNING_TIME), worldborder.world); // CraftBukkit
    }

    public void b(WorldBorder worldborder, int i) {
        this.a.sendAll(new PacketPlayOutWorldBorder(worldborder, EnumWorldBorderAction.SET_WARNING_BLOCKS), worldborder.world); // CraftBukkit
    }

    public void b(WorldBorder worldborder, double d0) {}

    public void c(WorldBorder worldborder, double d0) {}
}
