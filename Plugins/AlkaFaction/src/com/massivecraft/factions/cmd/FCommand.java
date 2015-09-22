package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.MCommand;

public abstract class FCommand extends MCommand<P> {
    public boolean disableOnLock;

    public FPlayer fme;
    public Faction myFaction;
    public boolean senderMustBeMember;
    public boolean senderMustBeOfficer;
    public boolean senderMustBeLeader;

    public boolean isMoneyCommand;

    public FCommand() {
        super(P.p);

        // Due to safety reasons it defaults to disable on lock.
        this.disableOnLock = true;

        // The money commands must be disabled if money should not be used.
        this.isMoneyCommand = false;

        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void execute(final CommandSender sender, final List<String> args, final List<MCommand<?>> commandChain) {
        if (sender instanceof Player) {
            this.fme = FPlayers.i.get((Player) sender);
            this.myFaction = this.fme.getFaction();
        } else {
            this.fme = null;
            this.myFaction = null;
        }
        super.execute(sender, args, commandChain);
    }

    @Override
    public boolean isEnabled() {
        if (this.p.getLocked() && this.disableOnLock) {
            this.msg("<b>Factions was locked by an admin. Please try again later.");
            return false;
        }

        if (this.isMoneyCommand && !Conf.econEnabled) {
            this.msg("<b>Faction economy features are disabled on this server.");
            return false;
        }

        if (this.isMoneyCommand && !Conf.bankEnabled) {
            this.msg("<b>The faction bank system is disabled on this server.");
            return false;
        }

        return true;
    }

    @Override
    public boolean validSenderType(final CommandSender sender, final boolean informSenderIfNot) {
        final boolean superValid = super.validSenderType(sender, informSenderIfNot);
        if (!superValid) return false;

        if (!(this.senderMustBeMember || this.senderMustBeOfficer || this.senderMustBeLeader)) return true;

        if (!(sender instanceof Player)) return false;

        final FPlayer fplayer = FPlayers.i.get((Player) sender);

        if (!fplayer.hasFaction()) {
            sender.sendMessage(this.p.txt.parse("<b>You are not member of any faction."));
            return false;
        }

        if (this.senderMustBeOfficer && !fplayer.getRole().isAtLeast(Rel.OFFICER)) {
            sender.sendMessage(this.p.txt.parse("<b>Only faction moderators can %s.", this.getHelpShort()));
            return false;
        }

        if (this.senderMustBeLeader && !fplayer.getRole().isAtLeast(Rel.LEADER)) {
            sender.sendMessage(this.p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
            return false;
        }

        return true;
    }

    // -------------------------------------------- //
    // Assertions
    // -------------------------------------------- //

    public boolean assertHasFaction() {
        if (this.me == null) return true;

        if (!this.fme.hasFaction()) {
            this.sendMessage("You are not member of any faction.");
            return false;
        }
        return true;
    }

    public boolean assertMinRole(final Rel role) {
        if (this.me == null) return true;

        if (this.fme.getRole().isLessThan(role)) {
            this.msg("<b>You <h>must be " + role + "<b> to " + this.getHelpShort() + ".");
            return false;
        }
        return true;
    }

    // -------------------------------------------- //
    // Argument Readers
    // -------------------------------------------- //

    // FPLAYER ======================
    public FPlayer strAsFPlayer(final String name, final FPlayer def, final boolean msg) {
        FPlayer ret = def;

        if (name != null) {
            final FPlayer fplayer = FPlayers.i.get(name);
            if (fplayer != null) {
                ret = fplayer;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>No player \"<p>%s<b>\" could be found.", name);
        }

        return ret;
    }

    public FPlayer argAsFPlayer(final int idx, final FPlayer def, final boolean msg) {
        return this.strAsFPlayer(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsFPlayer(final int idx, final FPlayer def) {
        return this.argAsFPlayer(idx, def, true);
    }

    public FPlayer argAsFPlayer(final int idx) {
        return this.argAsFPlayer(idx, null);
    }

    // BEST FPLAYER MATCH ======================
    public FPlayer strAsBestFPlayerMatch(final String name, final FPlayer def, final boolean msg) {
        FPlayer ret = def;

        if (name != null) {
            final FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
            if (fplayer != null) {
                ret = fplayer;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>No player match found for \"<p>%s<b>\".", name);
        }

        return ret;
    }

    public FPlayer argAsBestFPlayerMatch(final int idx, final FPlayer def, final boolean msg) {
        return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(final int idx, final FPlayer def) {
        return this.argAsBestFPlayerMatch(idx, def, true);
    }

    public FPlayer argAsBestFPlayerMatch(final int idx) {
        return this.argAsBestFPlayerMatch(idx, null);
    }

    // FACTION ======================
    public Faction strAsFaction(final String name, final Faction def, final boolean msg) {
        Faction ret = def;

        if (name != null) {
            Faction faction = null;

            // First we try an exact match
            if (faction == null) {
                faction = Factions.i.getByTag(name);
            }

            // Next we match faction tags
            if (faction == null) {
                faction = Factions.i.getBestTagMatch(name);
            }

            // Next we match player names
            if (faction == null) {
                final FPlayer fplayer = FPlayers.i.getBestIdMatch(name);
                if (fplayer != null) {
                    faction = fplayer.getFaction();
                }
            }

            if (faction != null) {
                ret = faction;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>The faction or player \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public Faction argAsFaction(final int idx, final Faction def, final boolean msg) {
        return this.strAsFaction(this.argAsString(idx), def, msg);
    }

    public Faction argAsFaction(final int idx, final Faction def) {
        return this.argAsFaction(idx, def, true);
    }

    public Faction argAsFaction(final int idx) {
        return this.argAsFaction(idx, null);
    }

    // FACTION FLAG ======================
    public FFlag strAsFactionFlag(final String name, final FFlag def, final boolean msg) {
        FFlag ret = def;

        if (name != null) {
            final FFlag flag = FFlag.parse(name);
            if (flag != null) {
                ret = flag;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>The faction-flag \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public FFlag argAsFactionFlag(final int idx, final FFlag def, final boolean msg) {
        return this.strAsFactionFlag(this.argAsString(idx), def, msg);
    }

    public FFlag argAsFactionFlag(final int idx, final FFlag def) {
        return this.argAsFactionFlag(idx, def, true);
    }

    public FFlag argAsFactionFlag(final int idx) {
        return this.argAsFactionFlag(idx, null);
    }

    // FACTION PERM ======================
    public FPerm strAsFactionPerm(final String name, final FPerm def, final boolean msg) {
        FPerm ret = def;

        if (name != null) {
            final FPerm perm = FPerm.parse(name);
            if (perm != null) {
                ret = perm;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>The faction-perm \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public FPerm argAsFactionPerm(final int idx, final FPerm def, final boolean msg) {
        return this.strAsFactionPerm(this.argAsString(idx), def, msg);
    }

    public FPerm argAsFactionPerm(final int idx, final FPerm def) {
        return this.argAsFactionPerm(idx, def, true);
    }

    public FPerm argAsFactionPerm(final int idx) {
        return this.argAsFactionPerm(idx, null);
    }

    // FACTION REL ======================
    public Rel strAsRel(final String name, final Rel def, final boolean msg) {
        Rel ret = def;

        if (name != null) {
            final Rel perm = Rel.parse(name);
            if (perm != null) {
                ret = perm;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>The role \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public Rel argAsRel(final int idx, final Rel def, final boolean msg) {
        return this.strAsRel(this.argAsString(idx), def, msg);
    }

    public Rel argAsRel(final int idx, final Rel def) {
        return this.argAsRel(idx, def, true);
    }

    public Rel argAsRel(final int idx) {
        return this.argAsRel(idx, null);
    }

    // -------------------------------------------- //
    // Commonly used logic
    // -------------------------------------------- //

    public boolean canIAdministerYou(final FPlayer i, final FPlayer you) {
        if (!i.getFaction().equals(you.getFaction())) {
            i.sendMessage(this.p.txt.parse("%s <b>is not in the same faction as you.", you.describeTo(i, true)));
            return false;
        }

        if (i.getRole().isMoreThan(you.getRole()) || i.getRole().equals(Rel.LEADER)) return true;

        if (you.getRole().equals(Rel.LEADER)) {
            i.sendMessage(this.p.txt.parse("<b>Only the faction admin can do that."));
        } else if (i.getRole().equals(Rel.OFFICER)) {
            if (i == you) return true; //Moderators can control themselves
            else {
                i.sendMessage(this.p.txt.parse("<b>Moderators can't control each other..."));
            }
        } else {
            i.sendMessage(this.p.txt.parse("<b>You must be a faction moderator to do that."));
        }

        return false;
    }

    // if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
    public boolean payForCommand(final double cost, final String toDoThis, final String forDoingThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || this.fme.hasAdminMode()) return true;

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && this.fme.hasFaction()) return Econ.modifyMoney(this.myFaction, -cost, toDoThis, forDoingThis);
        else return Econ.modifyMoney(this.fme, -cost, toDoThis, forDoingThis);
    }

    // like above, but just make sure they can pay; returns true unless person can't afford the cost
    public boolean canAffordCommand(final double cost, final String toDoThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || this.fme.hasAdminMode()) return true;

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && this.fme.hasFaction()) return Econ.hasAtLeast(this.myFaction, cost, toDoThis);
        else return Econ.hasAtLeast(this.fme, cost, toDoThis);
    }
}
