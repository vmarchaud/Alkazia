package net.minecraft.server;

import java.util.List;
import java.util.Random;

import org.bukkit.event.entity.EntityPortalEnterEvent; // CraftBukkit

public class BlockEnderPortal extends BlockContainer {

	public static boolean a;

	protected BlockEnderPortal(Material material) {
		super(material);
		this.a(1.0F);
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityEnderPortal();
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
		float f = 0.0625F;

		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public void a(World world, int i, int j, int k, Entity entity) {
		if (entity.vehicle == null && entity.passenger == null && !world.isStatic) {
			// CraftBukkit start - Entity in portal
			EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(), new org.bukkit.Location(world.getWorld(), i, j, k));
			world.getServer().getPluginManager().callEvent(event);
			// CraftBukkit end
			entity.b(1);
		}
	}

	@Override
	public int b() {
		return -1;
	}

	@Override
	public void onPlace(World world, int i, int j, int k) {
		if (!a) {
			if (world.worldProvider.dimension != 0) {
				world.setAir(i, j, k);
			}
		}
	}

	@Override
	public MaterialMapColor f(int i) {
		return MaterialMapColor.J;
	}
}
