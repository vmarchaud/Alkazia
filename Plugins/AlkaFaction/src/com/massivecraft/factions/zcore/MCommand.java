package com.massivecraft.factions.zcore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.zcore.util.TextUtil;

public abstract class MCommand<T extends MPlugin> {
    public T p;

    // The sub-commands to this command
    public List<MCommand<?>> subCommands;

    public void addSubCommand(final MCommand<?> subCommand) {
        subCommand.commandChain.addAll(this.commandChain);
        subCommand.commandChain.add(this);
        this.subCommands.add(subCommand);
    }

    // The different names this commands will react to  
    public List<String> aliases;

    // Information on the args
    public List<String> requiredArgs;
    public LinkedHashMap<String, String> optionalArgs;
    public boolean errorOnToManyArgs = true;

    // FIELD: Help Short
    // This field may be left blank and will in such case be loaded from the permissions node instead.
    // Thus make sure the permissions node description is an action description like "eat hamburgers" or "do admin stuff".
    private String helpShort;

    public void setHelpShort(final String val) {
        this.helpShort = val;
    }

    public String getHelpShort() {
        if (this.helpShort == null) {
            final String pdesc = this.p.perm.getPermissionDescription(this.permission);
            if (pdesc != null) return pdesc;
            return "*info unavailable*";
        }
        return this.helpShort;
    }

    public List<String> helpLong;
    public CommandVisibility visibility;

    // Some information on permissions
    public boolean senderMustBePlayer;
    public String permission;

    // Information available on execution of the command
    public CommandSender sender; // Will always be set
    public Player me; // Will only be set when the sender is a player
    public boolean senderIsConsole;
    public List<String> args; // Will contain the arguments, or and empty list if there are none.
    public List<MCommand<?>> commandChain = new ArrayList<MCommand<?>>(); // The command chain used to execute this command

    public MCommand(final T p) {
        this.p = p;

        this.permission = null;

        this.subCommands = new ArrayList<MCommand<?>>();
        this.aliases = new ArrayList<String>();

        this.requiredArgs = new ArrayList<String>();
        this.optionalArgs = new LinkedHashMap<String, String>();

        this.helpShort = null;
        this.helpLong = new ArrayList<String>();
        this.visibility = CommandVisibility.VISIBLE;
    }

    // The commandChain is a list of the parent command chain used to get to this command.
    public void execute(final CommandSender sender, final List<String> args, final List<MCommand<?>> commandChain) {
        // Set the execution-time specific variables
        this.sender = sender;
        if (sender instanceof Player) {
            this.me = (Player) sender;
            this.senderIsConsole = false;
        } else {
            this.me = null;
            this.senderIsConsole = true;
        }
        this.args = args;
        this.commandChain = commandChain;

        // Is there a matching sub command?
        if (args.size() > 0) {
            for (final MCommand<?> subCommand : this.subCommands)
                if (subCommand.aliases.contains(args.get(0))) {
                    args.remove(0);
                    commandChain.add(this);
                    subCommand.execute(sender, args, commandChain);
                    return;
                }
        }

        if (!this.validCall(this.sender, this.args)) return;

        if (!this.isEnabled()) return;

        this.perform();
    }

    public void execute(final CommandSender sender, final List<String> args) {
        this.execute(sender, args, new ArrayList<MCommand<?>>());
    }

    // This is where the command action is performed.
    public abstract void perform();

    // -------------------------------------------- //
    // Call Validation
    // -------------------------------------------- //

    /**
     * In this method we validate that all prerequisites to perform this command has been met.
     */
    // TODO: There should be a boolean for silence 
    public boolean validCall(final CommandSender sender, final List<String> args) {
        if (!this.validSenderType(sender, true)) return false;

        if (!this.validSenderPermissions(sender, true)) return false;

        if (!this.validArgs(args, sender)) return false;

        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean validSenderType(final CommandSender sender, final boolean informSenderIfNot) {
        if (this.senderMustBePlayer && !(sender instanceof Player)) {
            if (informSenderIfNot) {
                this.msg(Lang.commandSenderMustBePlayer);
            }
            return false;
        }
        return true;
    }

    public boolean validSenderPermissions(final CommandSender sender, final boolean informSenderIfNot) {
        if (this.permission == null) return true;
        return this.p.perm.has(sender, this.permission, informSenderIfNot);
    }

    public boolean validArgs(final List<String> args, final CommandSender sender) {
        if (args.size() < this.requiredArgs.size()) {
            if (sender != null) {
                this.msg(Lang.commandToFewArgs);
                sender.sendMessage(this.getUseageTemplate());
            }
            return false;
        }

        if (args.size() > this.requiredArgs.size() + this.optionalArgs.size() && this.errorOnToManyArgs) {
            if (sender != null) {
                // Get the to many string slice
                final List<String> theToMany = args.subList(this.requiredArgs.size() + this.optionalArgs.size(), args.size());
                this.msg(Lang.commandToManyArgs, TextUtil.implode(theToMany, " "));
                sender.sendMessage(this.getUseageTemplate());
            }
            return false;
        }
        return true;
    }

    public boolean validArgs(final List<String> args) {
        return this.validArgs(args, null);
    }

    // -------------------------------------------- //
    // Help and Usage information
    // -------------------------------------------- //

    public String getUseageTemplate(final List<MCommand<?>> commandChain, final boolean addShortHelp) {
        final StringBuilder ret = new StringBuilder();
        ret.append(this.p.txt.parseTags("<c>"));
        ret.append('/');

        for (final MCommand<?> mc : commandChain) {
            ret.append(TextUtil.implode(mc.aliases, ","));
            ret.append(' ');
        }

        ret.append(TextUtil.implode(this.aliases, ","));

        final List<String> args = new ArrayList<String>();

        for (final String requiredArg : this.requiredArgs) {
            args.add("<" + requiredArg + ">");
        }

        for (final Entry<String, String> optionalArg : this.optionalArgs.entrySet()) {
            String val = optionalArg.getValue();
            if (val == null) {
                val = "";
            } else {
                val = "=" + val;
            }
            args.add("[" + optionalArg.getKey() + val + "]");
        }

        if (args.size() > 0) {
            ret.append(this.p.txt.parseTags("<p> "));
            ret.append(TextUtil.implode(args, " "));
        }

        if (addShortHelp) {
            ret.append(this.p.txt.parseTags(" <i>"));
            ret.append(this.getHelpShort());
        }

        return ret.toString();
    }

    public String getUseageTemplate(final boolean addShortHelp) {
        return this.getUseageTemplate(this.commandChain, addShortHelp);
    }

    public String getUseageTemplate() {
        return this.getUseageTemplate(false);
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public void msg(final String str, final Object... args) {
        this.sender.sendMessage(this.p.txt.parse(str, args));
    }

    public void sendMessage(final String msg) {
        this.sender.sendMessage(msg);
    }

    public void sendMessage(final List<String> msgs) {
        for (final String msg : msgs) {
            this.sendMessage(msg);
        }
    }

    // -------------------------------------------- //
    // Argument Readers
    // -------------------------------------------- //

    // Is set? ======================
    public boolean argIsSet(final int idx) {
        if (this.args.size() < idx + 1) return false;
        return true;
    }

    // STRING ======================
    public String argAsString(final int idx, final String def) {
        if (this.args.size() < idx + 1) return def;
        return this.args.get(idx);
    }

    public String argAsString(final int idx) {
        return this.argAsString(idx, null);
    }

    // INT ======================
    public Integer strAsInt(final String str, final Integer def) {
        if (str == null) return def;
        try {
            final Integer ret = Integer.parseInt(str);
            return ret;
        } catch (final Exception e) {
            return def;
        }
    }

    public Integer argAsInt(final int idx, final Integer def) {
        return this.strAsInt(this.argAsString(idx), def);
    }

    public Integer argAsInt(final int idx) {
        return this.argAsInt(idx, null);
    }

    // Double ======================
    public Double strAsDouble(final String str, final Double def) {
        if (str == null) return def;
        try {
            final Double ret = Double.parseDouble(str);
            return ret;
        } catch (final Exception e) {
            return def;
        }
    }

    public Double argAsDouble(final int idx, final Double def) {
        return this.strAsDouble(this.argAsString(idx), def);
    }

    public Double argAsDouble(final int idx) {
        return this.argAsDouble(idx, null);
    }

    // TODO: Go through the str conversion for the other arg-readers as well.
    // Boolean ======================
    public Boolean strAsBool(String str) {
        str = str.toLowerCase();
        if (str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1")) return true;
        return false;
    }

    public Boolean argAsBool(final int idx, final Boolean def) {
        final String str = this.argAsString(idx);
        if (str == null) return def;

        return this.strAsBool(str);
    }

    public Boolean argAsBool(final int idx) {
        return this.argAsBool(idx, false);
    }

    // PLAYER ======================
    public Player strAsPlayer(final String name, final Player def, final boolean msg) {
        Player ret = def;

        if (name != null) {
            final Player player = Bukkit.getServer().getPlayer(name);
            if (player != null) {
                ret = player;
            }
        }

        if (msg && ret == null) {
            this.msg("<b>No player \"<p>%s<b>\" could not be found.", name);
        }

        return ret;
    }

    public Player argAsPlayer(final int idx, final Player def, final boolean msg) {
        return this.strAsPlayer(this.argAsString(idx), def, msg);
    }

    public Player argAsPlayer(final int idx, final Player def) {
        return this.argAsPlayer(idx, def, true);
    }

    public Player argAsPlayer(final int idx) {
        return this.argAsPlayer(idx, null);
    }

    // BEST PLAYER MATCH ======================
    public Player strAsBestPlayerMatch(final String name, final Player def, final boolean msg) {
        Player ret = def;

        if (name != null) {
            final List<Player> players = Bukkit.getServer().matchPlayer(name);
            if (players.size() > 0) {
                ret = players.get(0);
            }
        }

        if (msg && ret == null) {
            this.msg("<b>No player match found for \"<p>%s<b>\".", name);
        }

        return ret;
    }

    public Player argAsBestPlayerMatch(final int idx, final Player def, final boolean msg) {
        return this.strAsBestPlayerMatch(this.argAsString(idx), def, msg);
    }

    public Player argAsBestPlayerMatch(final int idx, final Player def) {
        return this.argAsBestPlayerMatch(idx, def, true);
    }

    public Player argAsBestPlayerMatch(final int idx) {
        return this.argAsPlayer(idx, null);
    }
}
