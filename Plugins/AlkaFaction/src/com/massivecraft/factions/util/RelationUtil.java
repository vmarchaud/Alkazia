package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.util.TextUtil;

public class RelationUtil {
    public static String describeThatToMe(final RelationParticipator that, final RelationParticipator me, final boolean ucfirst) {
        String ret = "";

        if (that == null) return "A server admin";

        final Faction thatFaction = RelationUtil.getFaction(that);
        if (thatFaction == null) return "ERROR"; // ERROR

        final Faction myFaction = RelationUtil.getFaction(me);
        //		if (myFaction == null) return thatFaction.getTag(); // no relation, but can show basic name or tag

        if (that instanceof Faction) {
            if (me instanceof FPlayer && myFaction == thatFaction) {
                ret = "your faction";
            } else {
                ret = thatFaction.getTag();
            }
        } else if (that instanceof FPlayer) {
            final FPlayer fplayerthat = (FPlayer) that;
            if (that == me) {
                ret = "you";
            } else if (thatFaction == myFaction) {
                ret = fplayerthat.getNameAndTitle();
            } else {
                ret = fplayerthat.getNameAndTag();
            }
        }

        if (ucfirst) {
            ret = TextUtil.upperCaseFirst(ret);
        }

        return "" + RelationUtil.getColorOfThatToMe(that, me) + ret;
    }

    public static String describeThatToMe(final RelationParticipator that, final RelationParticipator me) {
        return RelationUtil.describeThatToMe(that, me, false);
    }

    public static Rel getRelationOfThatToMe(final RelationParticipator that, final RelationParticipator me) {
        return RelationUtil.getRelationOfThatToMe(that, me, false);
    }

    public static Rel getRelationOfThatToMe(final RelationParticipator that, final RelationParticipator me, final boolean ignorePeaceful) {
        Rel ret = null;

        final Faction myFaction = RelationUtil.getFaction(me);
        if (myFaction == null) return Rel.NEUTRAL; // ERROR

        final Faction thatFaction = RelationUtil.getFaction(that);
        if (thatFaction == null) return Rel.NEUTRAL; // ERROR

        // The faction with the lowest wish "wins"
        if (thatFaction.getRelationWish(myFaction).isLessThan(myFaction.getRelationWish(thatFaction))) {
            ret = thatFaction.getRelationWish(myFaction);
        } else {
            ret = myFaction.getRelationWish(thatFaction);
        }

        if (myFaction.equals(thatFaction)) {
            ret = Rel.MEMBER;
            // Do officer and leader check
            //P.p.log("getRelationOfThatToMe the factions are the same for "+that.getClass().getSimpleName()+" and observer "+me.getClass().getSimpleName());
            if (that instanceof FPlayer) {
                ret = ((FPlayer) that).getRole();
                //P.p.log("getRelationOfThatToMe it was a player and role is "+ret);
            }
        } else if (!ignorePeaceful && (thatFaction.getFlag(FFlag.PEACEFUL) || myFaction.getFlag(FFlag.PEACEFUL))) {
            ret = Rel.TRUCE;
        }

        return ret;
    }

    public static Faction getFaction(final RelationParticipator rp) {
        if (rp instanceof Faction) return (Faction) rp;

        if (rp instanceof FPlayer) return ((FPlayer) rp).getFaction();

        // ERROR
        return null;
    }

    public static ChatColor getColorOfThatToMe(final RelationParticipator that, final RelationParticipator me) {
        final Faction thatFaction = RelationUtil.getFaction(that);
        if (thatFaction != null && thatFaction != RelationUtil.getFaction(me)) {
            if (thatFaction.getFlag(FFlag.FRIENDLYFIRE) == true) return Conf.colorFriendlyFire;

            if (thatFaction.getFlag(FFlag.PVP) == false) return Conf.colorNoPVP;
        }
        return RelationUtil.getRelationOfThatToMe(that, me).getColor();
    }
}
