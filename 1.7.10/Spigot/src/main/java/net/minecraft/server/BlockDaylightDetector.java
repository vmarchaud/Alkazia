package net.minecraft.server;

import java.util.Random;

public class BlockDaylightDetector extends BlockContainer {

	private IIcon[] a = new IIcon[2];

	public BlockDaylightDetector() {
		super(Material.WOOD);
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
		this.a(CreativeModeTab.d);
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
	}

	@Override
	public int b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getData(i, j, k);
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
	}

	@Override
	public void doPhysics(World world, int i, int j, int k, Block block) {
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
	}

	public void e(World world, int i, int j, int k) {
		if (!world.worldProvider.g) {
			int l = world.getData(i, j, k);
			int i1 = world.b(EnumSkyBlock.SKY, i, j, k) - world.j;
			float f = world.d(1.0F);

			if (f < 3.1415927F) {
				f += (0.0F - f) * 0.2F;
			} else {
				f += (6.2831855F - f) * 0.2F;
			}

			// PaperSpigot start - Configurable "inversion" for daylight detectors
			if (world.paperSpigotConfig.invertedDaylightDetectors) {
				i1 = Math.round(i1 * MathHelper.cos(f) * -1 + 15);
				if (i1 < 10) {
					i1 = 0;
				}

				if (i1 > 9) {
					i1 = 15;
				}
			} else {
				i1 = Math.round(i1 * MathHelper.cos(f));
				if (i1 < 0) {
					i1 = 0;
				}

				if (i1 > 15) {
					i1 = 15;
				}
			}
			// PaperSpigot end

			if (l != i1) {
				i1 = org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, i, j, k, l, i1).getNewCurrent(); // CraftBukkit - Call BlockRedstoneEvent
				world.setData(i, j, k, i1, 3);
			}
		}
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityLightDetector();
	}
}
