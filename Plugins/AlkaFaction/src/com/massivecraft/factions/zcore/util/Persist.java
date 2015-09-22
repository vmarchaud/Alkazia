package com.massivecraft.factions.zcore.util;

import java.io.File;
import java.lang.reflect.Type;
import java.util.logging.Level;

import com.massivecraft.factions.zcore.MPlugin;

// TODO: Give better name and place to differenciate from the entity-orm-ish system in "com.massivecraft.core.persist".

public class Persist {

    private final MPlugin p;

    public Persist(final MPlugin p) {
        this.p = p;
    }

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //

    public static String getName(final Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    public static String getName(final Object o) {
        return Persist.getName(o.getClass());
    }

    public static String getName(final Type type) {
        return Persist.getName(type.getClass());
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object? 
    // ------------------------------------------------------------ //

    public File getFile(final String name) {
        return new File(this.p.getDataFolder(), name + ".json");
    }

    public File getFile(final Class<?> clazz) {
        return this.getFile(Persist.getName(clazz));
    }

    public File getFile(final Object obj) {
        return this.getFile(Persist.getName(obj));
    }

    public File getFile(final Type type) {
        return this.getFile(Persist.getName(type));
    }

    // NICE WRAPPERS

    public <T> T loadOrSaveDefault(final T def, final Class<T> clazz) {
        return this.loadOrSaveDefault(def, clazz, this.getFile(clazz));
    }

    public <T> T loadOrSaveDefault(final T def, final Class<T> clazz, final String name) {
        return this.loadOrSaveDefault(def, clazz, this.getFile(name));
    }

    public <T> T loadOrSaveDefault(final T def, final Class<T> clazz, final File file) {
        if (!file.exists()) {
            this.p.log("Creating default: " + file);
            this.save(def, file);
            return def;
        }

        final T loaded = this.load(clazz, file);

        if (loaded == null) {
            this.p.log(Level.WARNING, "Using default as I failed to load: " + file);

            // backup bad file, so user can attempt to recover their changes from it
            final File backup = new File(file.getPath() + "_bad");
            if (backup.exists()) {
                backup.delete();
            }
            this.p.log(Level.WARNING, "Backing up copy of bad file to: " + backup);
            file.renameTo(backup);

            return def;
        }

        return loaded;
    }

    // SAVE

    public boolean save(final Object instance) {
        return this.save(instance, this.getFile(instance));
    }

    public boolean save(final Object instance, final String name) {
        return this.save(instance, this.getFile(name));
    }

    public boolean save(final Object instance, final File file) {
        return DiscUtil.writeCatch(file, this.p.gson.toJson(instance));
    }

    // LOAD BY CLASS

    public <T> T load(final Class<T> clazz) {
        return this.load(clazz, this.getFile(clazz));
    }

    public <T> T load(final Class<T> clazz, final String name) {
        return this.load(clazz, this.getFile(name));
    }

    public <T> T load(final Class<T> clazz, final File file) {
        final String content = DiscUtil.readCatch(file);
        if (content == null) return null;

        try {
            final T instance = this.p.gson.fromJson(content, clazz);
            return instance;
        } catch (final Exception ex) { // output the error message rather than full stack trace; error parsing the file, most likely
            this.p.log(Level.WARNING, ex.getMessage());
        }

        return null;
    }

    // LOAD BY TYPE
    @SuppressWarnings("unchecked")
    public <T> T load(final Type typeOfT, final String name) {
        return (T) this.load(typeOfT, this.getFile(name));
    }

    @SuppressWarnings("unchecked")
    public <T> T load(final Type typeOfT, final File file) {
        final String content = DiscUtil.readCatch(file);
        if (content == null) return null;

        try {
            return (T) this.p.gson.fromJson(content, typeOfT);
        } catch (final Exception ex) { // output the error message rather than full stack trace; error parsing the file, most likely
            this.p.log(Level.WARNING, ex.getMessage());
        }

        return null;
    }
}
