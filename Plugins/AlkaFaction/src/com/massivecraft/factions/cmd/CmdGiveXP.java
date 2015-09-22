package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdGiveXP extends FCommand {
    public CmdGiveXP() {
        this.aliases.add("givexp");
        this.requiredArgs.add("#");
        this.optionalArgs.put("faction", "your");
        this.permission = Permission.GIVEXP.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeOfficer = false;
        this.senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        Faction faction = this.myFaction;
        if (this.argIsSet(1)) {
            faction = this.argAsFaction(1);
            if (faction == null) return;
        }
        try {
            faction.addXP(Double.parseDouble(this.argAsString(0).replace(",", ".")));
            this.msg("<i>Vous venez de donner " + this.argAsDouble(0).toString().replace(".", ",") + " XP à la faction " + faction.getTag() + ".");
        } catch (final NumberFormatException exception) {
            this.msg("<b>Vous devez spéficier un nombre décimal.");
        }
    }
}
