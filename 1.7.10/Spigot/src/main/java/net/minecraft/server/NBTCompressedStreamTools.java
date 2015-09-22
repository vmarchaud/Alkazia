package net.minecraft.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompressedStreamTools {

	public static NBTTagCompound a(InputStream inputstream) {
		try {
			DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputstream)));

			NBTTagCompound nbttagcompound;

			try {
				nbttagcompound = a(datainputstream, NBTReadLimiter.a);
			} finally {
				datainputstream.close();
			}

			return nbttagcompound;
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
		return null;
	}

	public static void a(NBTTagCompound nbttagcompound, OutputStream outputstream) {
		try {
			DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputstream)));

			try {
				a(nbttagcompound, (DataOutput) dataoutputstream);
			} finally {
				dataoutputstream.close();
			}
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
	}

	public static NBTTagCompound a(byte[] abyte, NBTReadLimiter nbtreadlimiter) {
		try {
			DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new org.spigotmc.LimitStream(new GZIPInputStream(new ByteArrayInputStream(abyte)), nbtreadlimiter))); // Spigot

			NBTTagCompound nbttagcompound;

			try {
				nbttagcompound = a(datainputstream, nbtreadlimiter);
			} finally {
				datainputstream.close();
			}

			return nbttagcompound;
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
		return null;
	}

	public static byte[] a(NBTTagCompound nbttagcompound) {
		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

			try {
				a(nbttagcompound, (DataOutput) dataoutputstream);
			} finally {
				dataoutputstream.close();
			}

			return bytearrayoutputstream.toByteArray();
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
		return null;
	}

	public static NBTTagCompound a(DataInputStream datainputstream) {
		return a(datainputstream, NBTReadLimiter.a);
	}

	public static NBTTagCompound a(DataInput datainput, NBTReadLimiter nbtreadlimiter) {
		try {
			NBTBase nbtbase = a(datainput, 0, nbtreadlimiter);

			if (nbtbase instanceof NBTTagCompound)
				return (NBTTagCompound) nbtbase;
			else
				throw new IOException("Root tag must be a named compound tag");
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
		return null;
	}

	public static void a(NBTTagCompound nbttagcompound, DataOutput dataoutput) {
		a((NBTBase) nbttagcompound, dataoutput);
	}

	private static void a(NBTBase nbtbase, DataOutput dataoutput) {
		try {
			dataoutput.writeByte(nbtbase.getTypeId());
			if (nbtbase.getTypeId() != 0) {
				dataoutput.writeUTF("");
				nbtbase.write(dataoutput);
			}
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
	}

	private static NBTBase a(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
		try {
			byte b0 = datainput.readByte();

			if (b0 == 0)
				return new NBTTagEnd();
			else {
				datainput.readUTF();
				NBTBase nbtbase = NBTBase.createTag(b0);

				try {
					nbtbase.load(datainput, i, nbtreadlimiter);
					return nbtbase;
				} catch (IOException ioexception) {
					CrashReport crashreport = CrashReport.a(ioexception, "Loading NBT data");
					CrashReportSystemDetails crashreportsystemdetails = crashreport.a("NBT Tag");

					crashreportsystemdetails.a("Tag name", "[UNNAMED TAG]");
					crashreportsystemdetails.a("Tag type", Byte.valueOf(b0));
					throw new ReportedException(crashreport);
				}
			}
		} catch (IOException ex) {
			org.spigotmc.SneakyThrow.sneaky(ex);
		}
		return null;
	}
}
