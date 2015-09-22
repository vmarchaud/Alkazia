package com.massivecraft.factions.zcore.util;

import java.io.File;

import com.massivecraft.factions.zcore.MPlugin;

public class LibLoader {
    MPlugin p;

    public LibLoader(final MPlugin p) {
        this.p = p;
        new File("./lib").mkdirs();
    }

    public boolean require(final String filename, final String url) {
        if (!this.include(filename, url)) {
            this.p.log("Failed to load the required library " + filename);
            this.p.suicide();
            return false;
        }
        return true;
    }

    public boolean include(final String filename, final String url) {
        final File file = LibLoader.getFile(filename);
        if (!file.exists()) {
            this.p.log("Downloading library " + filename);
            if (!DiscUtil.downloadUrl(url, file)) {
                this.p.log("Failed to download " + filename);
                return false;
            }
        }

        return ClassLoadHack.load(file);
    }

    private static File getFile(final String filename) {
        return new File("./lib/" + filename);
    }
}
