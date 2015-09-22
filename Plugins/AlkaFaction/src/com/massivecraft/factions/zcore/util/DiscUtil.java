package com.massivecraft.factions.zcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DiscUtil {
    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //

    private final static String UTF8 = "UTF-8";

    // -------------------------------------------- //
    // BYTE 
    // -------------------------------------------- //

    public static byte[] readBytes(final File file) throws IOException {
        final int length = (int) file.length();
        final byte[] output = new byte[length];
        final InputStream in = new FileInputStream(file);
        int offset = 0;
        // normally it should be able to read the entire file with just a single iteration below, but it depends on the whims of the FileInputStream
        while (offset < length) {
            offset += in.read(output, offset, length - offset);
        }
        in.close();
        return output;
    }

    public static void writeBytes(final File file, final byte[] bytes) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    // -------------------------------------------- //
    // STRING
    // -------------------------------------------- //

    public static void write(final File file, final String content) throws IOException {
        DiscUtil.writeBytes(file, DiscUtil.utf8(content));
    }

    public static String read(final File file) throws IOException {
        return DiscUtil.utf8(DiscUtil.readBytes(file));
    }

    // -------------------------------------------- //
    // CATCH
    // -------------------------------------------- //

    public static boolean writeCatch(final File file, final String content) {
        try {
            DiscUtil.write(file, content);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public static String readCatch(final File file) {
        try {
            return DiscUtil.read(file);
        } catch (final IOException e) {
            return null;
        }
    }

    // -------------------------------------------- //
    // DOWNLOAD
    // -------------------------------------------- //

    public static boolean downloadUrl(final String urlstring, final File file) {
        try {
            final URL url = new URL(urlstring);
            final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            final FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            fos.close();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean downloadUrl(final String urlstring, final String filename) {
        return DiscUtil.downloadUrl(urlstring, new File(filename));
    }

    // -------------------------------------------- //
    // FILE DELETION
    // -------------------------------------------- //

    public static boolean deleteRecursive(final File path) throws FileNotFoundException {
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()) {
            for (final File f : path.listFiles()) {
                ret = ret && DiscUtil.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    // -------------------------------------------- //
    // UTF8 ENCODE AND DECODE
    // -------------------------------------------- //

    public static byte[] utf8(final String string) {
        try {
            return string.getBytes(DiscUtil.UTF8);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String utf8(final byte[] bytes) {
        try {
            return new String(bytes, DiscUtil.UTF8);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}