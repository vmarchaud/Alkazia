package com.massivecraft.factions.zcore.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoadHack {

    private static URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    public static boolean load(final String filename) {
        return ClassLoadHack.load(new File(filename));
    }

    public static boolean load(final File file) {
        try {
            return ClassLoadHack.load(file.toURI().toURL());
        } catch (final MalformedURLException e) {
            return false;
        }
    }

    public static boolean load(final URL url) {
        // If the file already is loaded we can skip it
        for (final URL otherUrl : ClassLoadHack.sysloader.getURLs())
            if (otherUrl.sameFile(url)) return true;

        try {
            final Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            addURLMethod.setAccessible(true);
            addURLMethod.invoke(ClassLoadHack.sysloader, new Object[] { url });
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

}