package net.minecraft.server;

public class TileEntityLightDetector extends TileEntity {

	public TileEntityLightDetector() {
	}

	@Override
	public void h() {
		if (world != null && !world.isStatic /*&& this.world.getTime() % 20L == 0L*/) { // PaperSpigot - interval controlled by Improved Tick Handling
			h = q();
			if (h instanceof BlockDaylightDetector) {
				((BlockDaylightDetector) h).e(world, x, y, z);
			}
		}
	}
}
