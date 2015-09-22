package com.massivecraft.factions.adapters;

import java.lang.reflect.Type;
import java.util.logging.Level;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import com.massivecraft.factions.P;
import com.massivecraft.factions.util.LazyLocation;

public class LocationTypeAdapter implements JsonDeserializer<LazyLocation>, JsonSerializer<LazyLocation> {
    private static final String WORLD = "world";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String YAW = "yaw";
    private static final String PITCH = "pitch";

    @Override
    public LazyLocation deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        try {
            final JsonObject obj = json.getAsJsonObject();

            final String worldName = obj.get(LocationTypeAdapter.WORLD).getAsString();
            final double x = obj.get(LocationTypeAdapter.X).getAsDouble();
            final double y = obj.get(LocationTypeAdapter.Y).getAsDouble();
            final double z = obj.get(LocationTypeAdapter.Z).getAsDouble();
            final float yaw = obj.get(LocationTypeAdapter.YAW).getAsFloat();
            final float pitch = obj.get(LocationTypeAdapter.PITCH).getAsFloat();

            return new LazyLocation(worldName, x, y, z, yaw, pitch);

        } catch (final Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while deserializing a LazyLocation.");
            return null;
        }
    }

    @Override
    public JsonElement serialize(final LazyLocation src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();

        try {
            obj.addProperty(LocationTypeAdapter.WORLD, src.getWorldName());
            obj.addProperty(LocationTypeAdapter.X, src.getX());
            obj.addProperty(LocationTypeAdapter.Y, src.getY());
            obj.addProperty(LocationTypeAdapter.Z, src.getZ());
            obj.addProperty(LocationTypeAdapter.YAW, src.getYaw());
            obj.addProperty(LocationTypeAdapter.PITCH, src.getPitch());

            return obj;
        } catch (final Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while serializing a LazyLocation.");
            return obj;
        }
    }
}
