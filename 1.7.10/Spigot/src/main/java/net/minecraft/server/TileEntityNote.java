package net.minecraft.server;

public class TileEntityNote extends TileEntity {

	public byte note;
	public boolean i;

	public TileEntityNote() {
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setByte("note", note);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		note = nbttagcompound.getByte("note");
		if (note < 0) {
			note = 0;
		}

		if (note > 24) {
			note = 24;
		}
	}

	public void a() {
		note = (byte) ((note + 1) % 25);
		update();
	}

	public void play(World world, int i, int j, int k) {
		if (world.getType(i, j + 1, k).getMaterial() == Material.AIR) {
			Material material = world.getType(i, j - 1, k).getMaterial();
			byte b0 = 0;

			if (material == Material.STONE) {
				b0 = 1;
			}

			if (material == Material.SAND) {
				b0 = 2;
			}

			if (material == Material.SHATTERABLE) {
				b0 = 3;
			}

			if (material == Material.WOOD) {
				b0 = 4;
			}

			// CraftBukkit start
			org.bukkit.event.block.NotePlayEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callNotePlayEvent(this.world, i, j, k, b0, note);
			if (!event.isCancelled()) {
				this.world.playBlockAction(i, j, k, Blocks.NOTE_BLOCK, event.getInstrument().getType(), event.getNote().getId());
			}
			// CraftBukkit end
		}
	}
}
