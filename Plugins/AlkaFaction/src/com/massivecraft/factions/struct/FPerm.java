package com.massivecraft.factions.struct;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.RelationParticipator;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FPerm {
    BUILD("build", "constuire", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY), PAINBUILD("painbuild", "edit, take damage"), DOOR("door", "ouvrir les portes", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY), BUTTON("button", "utiliser les boutons", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY), LEVER("lever", "use levers", Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY), CONTAINER("container", "utiliser les containers", Rel.LEADER, Rel.OFFICER, Rel.MEMBER), INVITE("invite", "inviter des joueurs", Rel.LEADER, Rel.OFFICER), KICK("kick", "kick des membres", Rel.LEADER, Rel.OFFICER), SETHOME("sethome", "placer le home", Rel.LEADER, Rel.OFFICER), WITHDRAW("withdraw", "retirer de l'argent", Rel.LEADER, Rel.OFFICER), TERRITORY("territory", "claim ou unclaim", Rel.LEADER, Rel.OFFICER), CAPE("cape", "set the cape", Rel.LEADER, Rel.OFFICER), ACCESS("access", "autoriser l'accés", Rel.LEADER, Rel.OFFICER), DISBAND("disband", "disband la faction", Rel.LEADER), PERMS("perms", "manager les permissions", Rel.LEADER), ;

    private final String nicename;
    private final String desc;
    public final Set<Rel> defaultDefaultValue;

    private FPerm(final String nicename, final String desc, final Rel... rels) {
        this.nicename = nicename;
        this.desc = desc;
        this.defaultDefaultValue = new HashSet<Rel>();
        this.defaultDefaultValue.addAll(Arrays.asList(rels));
    }

    public String getNicename() {
        return this.nicename;
    }

    public String getDescription() {
        return this.desc;
    }

    public Set<Rel> getDefault() {
        final Set<Rel> ret = Conf.factionPermDefaults.get(this);
        if (ret == null) return this.defaultDefaultValue;
        return ret;
    }

    public static FPerm parse(String str) {
        str = str.toLowerCase();
        if (str.startsWith("a")) return ACCESS;
        if (str.startsWith("bui")) return BUILD;
        if (str.startsWith("pa")) return PAINBUILD;
        if (str.startsWith("do")) return DOOR;
        if (str.startsWith("but")) return BUTTON;
        if (str.startsWith("l")) return LEVER;
        if (str.startsWith("co")) return CONTAINER;
        if (str.startsWith("i")) return INVITE;
        if (str.startsWith("k")) return KICK;
        if (str.startsWith("s")) return SETHOME;
        if (str.startsWith("w")) return WITHDRAW;
        if (str.startsWith("t")) return TERRITORY;
        if (str.startsWith("ca")) return CAPE;
        if (str.startsWith("di")) return DISBAND;
        if (str.startsWith("pe")) return PERMS;
        return null;
    }

    public static String getStateHeaders() {
        String ret = "";
        for (final Rel rel : Rel.values()) {
            ret += rel.getColor().toString();
            ret += rel.toString().substring(0, 3);
            ret += " ";
        }

        return ret;
    }

    public String getStateInfo(final Set<Rel> value, final boolean withDesc) {
        String ret = "";

        for (final Rel rel : Rel.values()) {
            if (value.contains(rel)) {
                ret += "<g>YES";
            } else {
                ret += "<b>NOO";
            }
            ret += " ";
        }

        ret += "<c>" + this.getNicename();
        if (withDesc) {
            ret += " <i>" + this.getDescription();
        }
        return ret;
    }

    // Perms which apply strictly to granting territory access
    private static final Set<FPerm> TerritoryPerms = EnumSet.of(BUILD, DOOR, BUTTON, LEVER, CONTAINER);

    public boolean isTerritoryPerm() {
        return FPerm.TerritoryPerms.contains(this);
    }

    private static final String errorpattern = "%s<b> ne vous autorise pas à utiliser les %s <b>.";

    public boolean has(final Object testSubject, final Faction hostFaction, final boolean informIfNot) {
        if (testSubject instanceof ConsoleCommandSender) return true;

        RelationParticipator rpSubject = null;

        if (testSubject instanceof Player) {
            rpSubject = FPlayers.i.get((Player) testSubject);
        } else if (testSubject instanceof RelationParticipator) {
            rpSubject = (RelationParticipator) testSubject;
        } else return false;

        final Rel rel = rpSubject.getRelationTo(hostFaction);

        // TODO: Create better description messages like: "You must at least be officer".
        boolean ret = hostFaction.getPermittedRelations(this).contains(rel);

        if (rpSubject instanceof FPlayer && ret == false && ((FPlayer) rpSubject).hasAdminMode()) {
            ret = true;
        }

        if (!ret && informIfNot && rpSubject instanceof FPlayer) {
            final FPlayer fplayer = (FPlayer) rpSubject;
            fplayer.msg(FPerm.errorpattern, hostFaction.describeTo(fplayer, true), this.getDescription());
            if (Permission.ADMIN.has(fplayer.getPlayer())) {
                fplayer.msg("<i>Vous pouvez bypass avec " + P.p.cmdBase.cmdBypass.getUseageTemplate(false));
            }
        }
        return ret;
    }

    public boolean has(final Object testSubject, final Faction hostFaction) {
        return this.has(testSubject, hostFaction, false);
    }

    public boolean has(final Object testSubject, final FLocation floc, final boolean informIfNot) {
        final TerritoryAccess access = Board.getTerritoryAccessAt(floc);
        if (this.isTerritoryPerm()) {
            if (access.subjectHasAccess(testSubject)) return true;
            if (access.subjectAccessIsRestricted(testSubject)) {
                if (informIfNot) {
                    FPlayer notify = null;
                    if (testSubject instanceof Player) {
                        notify = FPlayers.i.get((Player) testSubject);
                    } else if (testSubject instanceof FPlayer) {
                        notify = (FPlayer) testSubject;
                    }
                    if (notify != null) {
                        notify.msg("<b>This territory owned by your faction has restricted access.");
                    }
                }
                return false;
            }
        }
        return this.has(testSubject, access.getHostFaction(), informIfNot);
    }

    public boolean has(final Object testSubject, final Location loc, final boolean informIfNot) {
        final FLocation floc = new FLocation(loc);
        return this.has(testSubject, floc, informIfNot);
    }

    public boolean has(final Object testSubject, final Location loc) {
        return this.has(testSubject, loc, false);
    }

    public boolean has(final Object testSubject, final FLocation floc) {
        return this.has(testSubject, floc, false);
    }
}
