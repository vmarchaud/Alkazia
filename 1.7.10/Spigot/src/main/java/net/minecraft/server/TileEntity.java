package net.minecraft.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.inventory.InventoryHolder; // CraftBukkit
import org.spigotmc.CustomTimingsHandler; // Spigot

public class TileEntity {

	public CustomTimingsHandler tickTimer = org.bukkit.craftbukkit.SpigotTimings.getTileEntityTimings(this); // Spigot
	private static final Logger a = LogManager.getLogger();
	private static Map i = new HashMap();
	private static Map j = new HashMap();
	protected World world;
	public int x;
	public int y;
	public int z;
	protected boolean f;
	public int g = -1;
	public Block h;

	// Spigot start
	// Helper method for scheduleTicks. If the hopper at x0, y0, z0 is pointed
	// at this tile entity, then make it active.
	private void scheduleTick(int x0, int y0, int z0) {
		TileEntity tileEntity = world.getTileEntity(x0, y0, z0);
		if (tileEntity instanceof TileEntityHopper && tileEntity.world != null) {
			// i is the metadeta assoiated with the direction the hopper faces.
			int i = BlockHopper.b(tileEntity.p());
			// Facing class provides arrays for direction offset.
			if (tileEntity.x + Facing.b[i] == x && tileEntity.y + Facing.c[i] == y && tileEntity.z + Facing.d[i] == z) {
				((TileEntityHopper) tileEntity).makeTick();
			}
		}
	}

	// Called from update when the contents have changed, so hoppers need updates.
	// Check all 6 faces.
	public void scheduleTicks() {
		if (world != null && world.spigotConfig.altHopperTicking) {
			// Check the top
			scheduleTick(x, y + 1, z);
			// Check the sides
			for (int i = 2; i < 6; i++) {
				scheduleTick(x + Facing.b[i], y, z + Facing.d[i]);
			}
			// Check the bottom.
			TileEntity tileEntity = world.getTileEntity(x, y - 1, z);
			if (tileEntity instanceof TileEntityHopper && tileEntity.world != null) {
				((TileEntityHopper) tileEntity).makeTick();
			}
		}
	}

	// Optimized TileEntity Tick changes
	private static int tileEntityCounter = 0;
	public boolean isAdded = false;
	public int tileId = tileEntityCounter++;

	// Spigot end

	public TileEntity() {
	}

	private static void a(Class oclass, String s) {
		if (i.containsKey(s))
			throw new IllegalArgumentException("Duplicate id: " + s);
		else {
			i.put(s, oclass);
			j.put(oclass, s);
		}
	}

	public World getWorld() {
		return world;
	}

	public void a(World world) {
		this.world = world;
	}

	public boolean o() {
		return world != null;
	}

	public void a(NBTTagCompound nbttagcompound) {
		x = nbttagcompound.getInt("x");
		y = nbttagcompound.getInt("y");
		z = nbttagcompound.getInt("z");
	}

	public void b(NBTTagCompound nbttagcompound) {
		String s = (String) j.get(this.getClass());

		if (s == null)
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		else {
			nbttagcompound.setString("id", s);
			nbttagcompound.setInt("x", x);
			nbttagcompound.setInt("y", y);
			nbttagcompound.setInt("z", z);
		}
	}

	public void h() {
	}

	public static TileEntity c(NBTTagCompound nbttagcompound) {
		TileEntity tileentity = null;

		try {
			Class oclass = (Class) i.get(nbttagcompound.getString("id"));

			if (oclass != null) {
				tileentity = (TileEntity) oclass.newInstance();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (tileentity != null) {
			tileentity.a(nbttagcompound);
		} else {
			a.warn("Skipping BlockEntity with id " + nbttagcompound.getString("id"));
		}

		return tileentity;
	}

	public int p() {
		if (g == -1) {
			g = world.getData(x, y, z);
		}

		return g;
	}

	public void update() {
		if (world != null) {
			g = world.getData(x, y, z);
			world.b(x, y, z, this);
			if (q() != Blocks.AIR) {
				world.updateAdjacentComparators(x, y, z, q());
			}
			// Spigot start - Called when the contents have changed, so hoppers around this
			// tile need updating.
			scheduleTicks();
			// Spigot end
		}
	}

	public Block q() {
		if (h == null) {
			h = world.getType(x, y, z);
		}

		return h;
	}

	public Packet getUpdatePacket() {
		return null;
	}

	public boolean r() {
		return f;
	}

	public void s() {
		f = true;
	}

	public void t() {
		f = false;
	}

	public boolean c(int i, int j) {
		return false;
	}

	public void u() {
		h = null;
		g = -1;
	}

	public void a(CrashReportSystemDetails crashreportsystemdetails) {
		crashreportsystemdetails.a("Name", new CrashReportTileEntityName(this));
		CrashReportSystemDetails.a(crashreportsystemdetails, x, y, z, q(), p());
		crashreportsystemdetails.a("Actual block type", new CrashReportTileEntityType(this));
		crashreportsystemdetails.a("Actual block data value", new CrashReportTileEntityData(this));
	}

	static Map v() {
		return j;
	}

	static {
		a(TileEntityFurnace.class, "Furnace");
		a(TileEntityChest.class, "Chest");
        // Alkazia - add iron chest
        a(TileEntityIronChest.class, "IronChest");
		a(TileEntityEnderChest.class, "EnderChest");
		a(TileEntityRecordPlayer.class, "RecordPlayer");
		a(TileEntityDispenser.class, "Trap");
		a(TileEntityDropper.class, "Dropper");
		a(TileEntitySign.class, "Sign");
		a(TileEntityMobSpawner.class, "MobSpawner");
		a(TileEntityNote.class, "Music");
		a(TileEntityPiston.class, "Piston");
		a(TileEntityBrewingStand.class, "Cauldron");
		a(TileEntityEnchantTable.class, "EnchantTable");
		a(TileEntityEnderPortal.class, "Airportal");
		a(TileEntityCommand.class, "Control");
		a(TileEntityBeacon.class, "Beacon");
		a(TileEntitySkull.class, "Skull");
		a(TileEntityLightDetector.class, "DLDetector");
		a(TileEntityHopper.class, "Hopper");
		a(TileEntityComparator.class, "Comparator");
		a(TileEntityFlowerPot.class, "FlowerPot");
	}

	// CraftBukkit start - add method
	public InventoryHolder getOwner() {
		// Spigot start
		org.bukkit.block.Block block = world.getWorld().getBlockAt(x, y, z);
		if (block == null) {
			org.bukkit.Bukkit.getLogger().log(java.util.logging.Level.WARNING, "No block for owner at %s %d %d %d", new Object[] { world.getWorld(), x, y, z });
			return null;
		}
		// Spigot end
		org.bukkit.block.BlockState state = block.getState();
		if (state instanceof InventoryHolder)
			return (InventoryHolder) state;
		return null;
	}
	// CraftBukkit end
}
