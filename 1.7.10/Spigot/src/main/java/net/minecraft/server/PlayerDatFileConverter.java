package net.minecraft.server;

import java.io.File;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.util.com.mojang.authlib.yggdrasil.ProfileNotFoundException;

final class PlayerDatFileConverter implements ProfileLookupCallback {

	final DedicatedServer a;
	final File b;
	final File c;
	final File d;
	final String[] e;

	PlayerDatFileConverter(DedicatedServer dedicatedserver, File file1, File file2, File file3, String[] astring) {
		a = dedicatedserver;
		b = file1;
		c = file2;
		d = file3;
		e = astring;
	}

	@Override
	public void onProfileLookupSucceeded(GameProfile gameprofile) {
		a.getUserCache().a(gameprofile);
		UUID uuid = gameprofile.getId();

		if (uuid == null)
			throw new FileConversionException("Missing UUID for user profile " + gameprofile.getName(), (PredicateEmptyList) null);
		else {
			this.a(b, this.a(gameprofile), uuid.toString());
		}
	}

	@Override
	public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
		NameReferencingFileConverter.a().warn("Could not lookup user uuid for " + gameprofile.getName(), exception);
		if (exception instanceof ProfileNotFoundException) {
			String s = this.a(gameprofile);

			this.a(c, s, s);
		} else
			throw new FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception, (PredicateEmptyList) null);
	}

	private void a(File file1, String s, String s1) {
		File file2 = new File(d, s + ".dat");
		File file3 = new File(file1, s1 + ".dat");

		// CraftBukkit start - Use old file name to seed lastKnownName
		NBTTagCompound root = null;

		try {
			root = NBTCompressedStreamTools.a(new java.io.FileInputStream(file2));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (root != null) {
			if (!root.hasKey("bukkit")) {
				root.set("bukkit", new NBTTagCompound());
			}
			NBTTagCompound data = root.getCompound("bukkit");
			data.setString("lastKnownName", s);

			try {
				NBTCompressedStreamTools.a(root, new java.io.FileOutputStream(file2));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		// CraftBukkit end

		NameReferencingFileConverter.a(file1);
		if (!file2.renameTo(file3))
			throw new FileConversionException("Could not convert file for " + s, (PredicateEmptyList) null);
	}

	private String a(GameProfile gameprofile) {
		String s = null;

		for (int i = 0; i < e.length; ++i) {
			if (e[i] != null && e[i].equalsIgnoreCase(gameprofile.getName())) {
				s = e[i];
				break;
			}
		}

		if (s == null)
			throw new FileConversionException("Could not find the filename for " + gameprofile.getName() + " anymore", (PredicateEmptyList) null);
		else
			return s;
	}
}
