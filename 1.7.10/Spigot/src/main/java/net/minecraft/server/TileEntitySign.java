package net.minecraft.server;

public class TileEntitySign extends TileEntity {

	public String[] lines = new String[] { "", "", "", "" };
	public int i = -1;
	public boolean isEditable = true; // CraftBukkit - private -> public
	private EntityHuman k;

	public TileEntitySign() {
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setString("Text1", lines[0]);
		nbttagcompound.setString("Text2", lines[1]);
		nbttagcompound.setString("Text3", lines[2]);
		nbttagcompound.setString("Text4", lines[3]);
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		isEditable = false;
		super.a(nbttagcompound);

		for (int i = 0; i < 4; ++i) {
			lines[i] = nbttagcompound.getString("Text" + (i + 1));
			if (lines[i].length() > 15) {
				lines[i] = lines[i].substring(0, 15);
			}
		}
	}

	@Override
	public Packet getUpdatePacket() {
		String[] astring = sanitizeLines(lines); // CraftBukkit - call sign line sanitizer to limit line length

		return new PacketPlayOutUpdateSign(x, y, z, astring);
	}

	public boolean a() {
		return isEditable;
	}

	public void a(EntityHuman entityhuman) {
		k = entityhuman;
	}

	public EntityHuman b() {
		return k;
	}

	// CraftBukkit start - central method to limit sign text to 15 chars per line
	public static String[] sanitizeLines(String[] lines) {
		String[] astring = new String[4];
		for (int i = 0; i < 4; ++i) {
			astring[i] = lines[i];

			if (lines[i].length() > 15) {
				astring[i] = lines[i].substring(0, 15);
			}
		}
		return astring;
	}
	// CraftBukkit end
}
