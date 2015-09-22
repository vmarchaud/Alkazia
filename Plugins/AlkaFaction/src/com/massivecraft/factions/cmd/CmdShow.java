package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdShow extends FCommand {
    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.SHOW.node;
        this.disableOnLock = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        Faction faction = this.myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
            if (faction == null) return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!this.payForCommand(Conf.econCostShow, "to show faction information", "for showing faction information")) return;

        final Collection<FPlayer> admins = faction.getFPlayersWhereRole(Rel.LEADER);
        final Collection<FPlayer> mods = faction.getFPlayersWhereRole(Rel.OFFICER);
        final Collection<FPlayer> normals = faction.getFPlayersWhereRole(Rel.MEMBER);
        final Collection<FPlayer> recruits = faction.getFPlayersWhereRole(Rel.RECRUIT);

        this.msg(this.p.txt.titleize(faction.getTag(this.fme)));
        this.msg("<a>Description: <i>%s", faction.getDescription());

        // Display important flags
        // TODO: Find the non default flags, and display them instead.
        if (faction.getFlag(FFlag.PERMANENT)) {
            this.msg("<a>This faction is permanent - remaining even with no members.");
        }

        if (faction.getFlag(FFlag.PEACEFUL)) {
            this.msg("<a>Cette faction est peacefull - en truce avec tout le monde.");
        }

        this.msg("<a>Rejoindre : <i>" + (faction.getOpen() ? "aucune invitation nécéssaire" : "une invitation est obligatoire"));

        final double powerBoost = faction.getPowerBoost();
        final String boost = powerBoost == 0.0 ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
        this.msg("<a>Claim / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);

        // AlkaziaFactions
        if (faction.isNormal()) {
            this.msg("<a>Level: <i>%d", faction.getLevel().getLevel());
        }

        // show the land value
        if (Econ.shouldBeUsed()) {
            final double value = Econ.calculateTotalLandValue(faction.getLandRounded());
            final double refund = value * Conf.econClaimRefundMultiplier;
            if (value > 0) {
                final String stringValue = Econ.moneyString(value);
                final String stringRefund = refund > 0.0 ? " (" + Econ.moneyString(refund) + " depreciated)" : "";
                this.msg("<a>Total land value: <i>" + stringValue + stringRefund);
            }

            //Show bank contents
            if (Conf.bankEnabled) {
                this.msg("<a>Bank contains: <i>" + Econ.moneyString(Econ.getBalance(faction.getAccountId())));
            }
        }

        final String sepparator = this.p.txt.parse("<i>") + ", ";

        // List the relations to other factions
        final Map<Rel, List<String>> relationTags = faction.getFactionTagsPerRelation(this.fme, true);

        if (faction.getFlag(FFlag.PEACEFUL)) {
            this.sendMessage(this.p.txt.parse("<a>Truce à :<i> *tous le monde*"));
        } else {
            this.sendMessage(this.p.txt.parse("<a>Truce à : ") + TextUtil.implode(relationTags.get(Rel.TRUCE), sepparator));
        }

        this.sendMessage(this.p.txt.parse("<a>Alliés à: ") + TextUtil.implode(relationTags.get(Rel.ALLY), sepparator));
        this.sendMessage(this.p.txt.parse("<a>Enemies avec: ") + TextUtil.implode(relationTags.get(Rel.ENEMY), sepparator));

        // List the members...
        final List<String> memberOnlineNames = new ArrayList<String>();
        final List<String> memberOfflineNames = new ArrayList<String>();

        for (final FPlayer follower : admins)
            if (follower.isOnlineAndVisibleTo(this.me)) {
                memberOnlineNames.add(follower.getNameAndTitle(this.fme));
            } else {
                memberOfflineNames.add(follower.getNameAndTitle(this.fme));
            }

        for (final FPlayer follower : mods)
            if (follower.isOnlineAndVisibleTo(this.me)) {
                memberOnlineNames.add(follower.getNameAndTitle(this.fme));
            } else {
                memberOfflineNames.add(follower.getNameAndTitle(this.fme));
            }

        for (final FPlayer follower : normals)
            if (follower.isOnlineAndVisibleTo(this.me)) {
                memberOnlineNames.add(follower.getNameAndTitle(this.fme));
            } else {
                memberOfflineNames.add(follower.getNameAndTitle(this.fme));
            }

        for (final FPlayer follower : recruits)
            if (follower.isOnline()) {
                memberOnlineNames.add(follower.getNameAndTitle(this.fme));
            } else {
                memberOfflineNames.add(follower.getNameAndTitle(this.fme));
            }
        this.sendMessage(this.p.txt.parse("<a>Membres connectés: ") + TextUtil.implode(memberOnlineNames, sepparator));
        this.sendMessage(this.p.txt.parse("<a>Membres déconnectés: ") + TextUtil.implode(memberOfflineNames, sepparator));
    }

}
