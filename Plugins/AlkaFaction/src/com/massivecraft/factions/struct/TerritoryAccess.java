package com.massivecraft.factions.struct;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;

public class TerritoryAccess implements JsonDeserializer<TerritoryAccess>, JsonSerializer<TerritoryAccess> {
    private String hostFactionID;
    private boolean hostFactionAllowed = true;
    private final Set<String> factionIDs = new LinkedHashSet<String>();
    private final Set<String> fplayerIDs = new LinkedHashSet<String>();

    public TerritoryAccess(final String factionID) {
        this.hostFactionID = factionID;
    }

    public TerritoryAccess() {}

    public void setHostFactionID(final String factionID) {
        this.hostFactionID = factionID;
        this.hostFactionAllowed = true;
        this.factionIDs.clear();
        this.fplayerIDs.clear();
    }

    public String getHostFactionID() {
        return this.hostFactionID;
    }

    public Faction getHostFaction() {
        return Factions.i.get(this.hostFactionID);
    }

    // considered "default" if host faction is still allowed and nobody has been granted access
    public boolean isDefault() {
        return this.hostFactionAllowed && this.factionIDs.isEmpty() && this.fplayerIDs.isEmpty();
    }

    public boolean isHostFactionAllowed() {
        return this.hostFactionAllowed;
    }

    public void setHostFactionAllowed(final boolean allowed) {
        this.hostFactionAllowed = allowed;
    }

    public boolean doesHostFactionMatch(final Object testSubject) {
        if (testSubject instanceof String) return this.hostFactionID.equals(testSubject);
        else if (testSubject instanceof Player) return this.hostFactionID.equals(FPlayers.i.get((Player) testSubject).getFactionId());
        else if (testSubject instanceof FPlayer) return this.hostFactionID.equals(((FPlayer) testSubject).getFactionId());
        else if (testSubject instanceof Faction) return this.hostFactionID.equals(((Faction) testSubject).getId());
        return false;
    }

    public void addFaction(final String factionID) {
        this.factionIDs.add(factionID);
    }

    public void addFaction(final Faction faction) {
        this.addFaction(faction.getId());
    }

    public void addFPlayer(final String fplayerID) {
        this.fplayerIDs.add(fplayerID);
    }

    public void addFPlayer(final FPlayer fplayer) {
        this.addFPlayer(fplayer.getId());
    }

    public void removeFaction(final String factionID) {
        this.factionIDs.remove(factionID);
    }

    public void removeFaction(final Faction faction) {
        this.removeFaction(faction.getId());
    }

    public void removeFPlayer(final String fplayerID) {
        this.fplayerIDs.remove(fplayerID);
    }

    public void removeFPlayer(final FPlayer fplayer) {
        this.removeFPlayer(fplayer.getId());
    }

    // return true if faction was added, false if it was removed
    public boolean toggleFaction(final String factionID) {
        // if the host faction, special handling
        if (this.doesHostFactionMatch(factionID)) {
            this.hostFactionAllowed ^= true;
            return this.hostFactionAllowed;
        }

        if (this.factionIDs.contains(factionID)) {
            this.removeFaction(factionID);
            return false;
        }
        this.addFaction(factionID);
        return true;
    }

    public boolean toggleFaction(final Faction faction) {
        return this.toggleFaction(faction.getId());
    }

    public boolean toggleFPlayer(final String fplayerID) {
        if (this.fplayerIDs.contains(fplayerID)) {
            this.removeFPlayer(fplayerID);
            return false;
        }
        this.addFPlayer(fplayerID);
        return true;
    }

    public boolean toggleFPlayer(final FPlayer fplayer) {
        return this.toggleFPlayer(fplayer.getId());
    }

    public String factionList() {
        final StringBuilder list = new StringBuilder();
        for (final String factionID : this.factionIDs) {
            if (list.length() > 0) {
                list.append(", ");
            }
            list.append(Factions.i.get(factionID).getTag());
        }
        return list.toString();
    }

    public String fplayerList() {
        final StringBuilder list = new StringBuilder();
        for (final String fplayerID : this.fplayerIDs) {
            if (list.length() > 0) {
                list.append(", ");
            }
            list.append(fplayerID);
        }
        return list.toString();
    }

    // these return false if not granted explicit access, or true if granted explicit access (in FPlayer or Faction lists)
    // they do not take into account hostFactionAllowed, which will need to be checked separately (as to not override FPerms which are denied for faction members and such)
    public boolean subjectHasAccess(final Object testSubject) {
        if (testSubject instanceof Player) return this.fPlayerHasAccess(FPlayers.i.get((Player) testSubject));
        else if (testSubject instanceof FPlayer) return this.fPlayerHasAccess((FPlayer) testSubject);
        else if (testSubject instanceof Faction) return this.factionHasAccess((Faction) testSubject);
        return false;
    }

    public boolean fPlayerHasAccess(final FPlayer fplayer) {
        if (this.factionHasAccess(fplayer.getFactionId())) return true;
        return this.fplayerIDs.contains(fplayer.getId());
    }

    public boolean factionHasAccess(final Faction faction) {
        return this.factionHasAccess(faction.getId());
    }

    public boolean factionHasAccess(final String factionID) {
        return this.factionIDs.contains(factionID);
    }

    // this should normally only be checked after running subjectHasAccess() or fPlayerHasAccess() above to see if they have access explicitly granted
    public boolean subjectAccessIsRestricted(final Object testSubject) {
        return !this.isHostFactionAllowed() && this.doesHostFactionMatch(testSubject) && !FPerm.ACCESS.has(testSubject, this.getHostFaction());
    }

    //----------------------------------------------//
    // JSON Serialize/Deserialize Type Adapters
    //----------------------------------------------//

    @Override
    public TerritoryAccess deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        try {
            // if stored as simple string, it's just the faction ID and default values are to be used
            if (json.isJsonPrimitive()) {
                final String factionID = json.getAsString();
                return new TerritoryAccess(factionID);
            }

            // otherwise, it's stored as an object and all data should be present
            final JsonObject obj = json.getAsJsonObject();
            if (obj == null) return null;

            final String factionID = obj.get("ID").getAsString();
            final boolean hostAllowed = obj.get("open").getAsBoolean();
            final JsonArray factions = obj.getAsJsonArray("factions");
            final JsonArray fplayers = obj.getAsJsonArray("fplayers");

            final TerritoryAccess access = new TerritoryAccess(factionID);
            access.setHostFactionAllowed(hostAllowed);

            Iterator<JsonElement> iter = factions.iterator();
            while (iter.hasNext()) {
                access.addFaction(iter.next().getAsString());
            }

            iter = fplayers.iterator();
            while (iter.hasNext()) {
                access.addFPlayer(iter.next().getAsString());
            }

            return access;

        } catch (final Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while deserializing TerritoryAccess data.");
            return null;
        }
    }

    @Override
    public JsonElement serialize(final TerritoryAccess src, final Type typeOfSrc, final JsonSerializationContext context) {
        try {
            if (src == null) return null;

            // if default values, store as simple string
            if (src.isDefault()) {
                // if Wilderness (faction "0") and default access values, no need to store it
                if (src.getHostFactionID().equals("0")) return null;

                return new JsonPrimitive(src.getHostFactionID());
            }

            // otherwise, store all data
            final JsonObject obj = new JsonObject();

            final JsonArray factions = new JsonArray();
            final JsonArray fplayers = new JsonArray();

            Iterator<String> iter = src.factionIDs.iterator();
            while (iter.hasNext()) {
                factions.add(new JsonPrimitive(iter.next()));
            }

            iter = src.fplayerIDs.iterator();
            while (iter.hasNext()) {
                fplayers.add(new JsonPrimitive(iter.next()));
            }

            obj.addProperty("ID", src.getHostFactionID());
            obj.addProperty("open", src.isHostFactionAllowed());
            obj.add("factions", factions);
            obj.add("fplayers", fplayers);

            return obj;

        } catch (final Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while serializing TerritoryAccess data.");
            return null;
        }
    }

    //----------------------------------------------//
    // Comparison
    //----------------------------------------------//

    @Override
    public int hashCode() {
        return this.hostFactionID.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TerritoryAccess)) return false;

        final TerritoryAccess that = (TerritoryAccess) obj;
        return this.hostFactionID.equals(that.hostFactionID) && this.hostFactionAllowed == that.hostFactionAllowed && this.factionIDs == that.factionIDs && this.fplayerIDs == that.fplayerIDs;
    }
}