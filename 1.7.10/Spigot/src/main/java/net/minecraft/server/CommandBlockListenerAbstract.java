package net.minecraft.server;

import java.text.SimpleDateFormat;
// CraftBukkit start
import java.util.ArrayList;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;

import com.google.common.base.Joiner;

// CraftBukkit end

public abstract class CommandBlockListenerAbstract implements ICommandListener {

	private static final SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
	private int b;
	private boolean c = true;
	private IChatBaseComponent d = null;
	public String e = ""; // CraftBukkit - private -> public
	private String f = "@";
	protected org.bukkit.command.CommandSender sender; // CraftBukkit - add sender;

	public CommandBlockListenerAbstract() {
	}

	public int g() {
		return b;
	}

	public IChatBaseComponent h() {
		return d;
	}

	public void a(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("Command", e);
		nbttagcompound.setInt("SuccessCount", b);
		nbttagcompound.setString("CustomName", f);
		if (d != null) {
			nbttagcompound.setString("LastOutput", ChatSerializer.a(d));
		}

		nbttagcompound.setBoolean("TrackOutput", c);
	}

	public void b(NBTTagCompound nbttagcompound) {
		e = nbttagcompound.getString("Command");
		b = nbttagcompound.getInt("SuccessCount");
		if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
			f = nbttagcompound.getString("CustomName");
		}

		if (nbttagcompound.hasKeyOfType("LastOutput", 8)) {
			d = ChatSerializer.a(nbttagcompound.getString("LastOutput"));
		}

		if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
			c = nbttagcompound.getBoolean("TrackOutput");
		}
	}

	@Override
	public boolean a(int i, String s) {
		return i <= 2;
	}

	public void setCommand(String s) {
		e = s;
	}

	public String getCommand() {
		return e;
	}

	public void a(World world) {
		if (world.isStatic) {
			b = 0;
		}

		MinecraftServer minecraftserver = MinecraftServer.getServer();

		if (minecraftserver != null && minecraftserver.getEnableCommandBlock()) {
			// CraftBukkit start - Handle command block commands using Bukkit dispatcher
			org.bukkit.command.SimpleCommandMap commandMap = minecraftserver.server.getCommandMap();
			Joiner joiner = Joiner.on(" ");
			String command = e;
			if (e.startsWith("/")) {
				command = e.substring(1);
			}
			String[] args = command.split(" ");
			ArrayList<String[]> commands = new ArrayList<String[]>();

			// Block disallowed commands
			if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("op") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("pardon") || args[0].equalsIgnoreCase("pardon-ip") || args[0].equalsIgnoreCase("reload")) {
				b = 0;
				return;
			}

			// If the world has no players don't run
			if (getWorld().players.isEmpty()) {
				b = 0;
				return;
			}

			// Handle vanilla commands;
			if (minecraftserver.server.getCommandBlockOverride(args[0])) {
				org.bukkit.command.Command commandBlockCommand = commandMap.getCommand("minecraft:" + args[0]);
				if (commandBlockCommand instanceof VanillaCommandWrapper) {
					b = ((VanillaCommandWrapper) commandBlockCommand).dispatchVanillaCommandBlock(this, e);
					return;
				}
			}

			// Spigot start - check for manually prefixed command or commands that don't need a prefix
			org.bukkit.command.Command commandBlockCommand = commandMap.getCommand(args[0]);
			if (commandBlockCommand instanceof VanillaCommandWrapper) {
				b = ((VanillaCommandWrapper) commandBlockCommand).dispatchVanillaCommandBlock(this, e);
				return;
			}
			// Spigot end

			// Make sure this is a valid command
			if (commandMap.getCommand(args[0]) == null) {
				b = 0;
				return;
			}

			// testfor command requires special handling
			if (args[0].equalsIgnoreCase("testfor")) {
				if (args.length < 2) {
					b = 0;
					return;
				}

				EntityPlayer[] players = PlayerSelector.getPlayers(this, args[1]);

				if (players != null && players.length > 0) {
					b = players.length;
					return;
				} else {
					EntityPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(args[1]);
					if (player == null) {
						b = 0;
						return;
					} else {
						b = 1;
						return;
					}
				}
			}

			commands.add(args);

			// Find positions of command block syntax, if any
			ArrayList<String[]> newCommands = new ArrayList<String[]>();
			for (int i = 0; i < args.length; i++) {
				if (PlayerSelector.isPattern(args[i])) {
					for (int j = 0; j < commands.size(); j++) {
						newCommands.addAll(buildCommands(commands.get(j), i));
					}
					ArrayList<String[]> temp = commands;
					commands = newCommands;
					newCommands = temp;
					newCommands.clear();
				}
			}

			int completed = 0;

			// Now dispatch all of the commands we ended up with
			for (int i = 0; i < commands.size(); i++) {
				try {
					if (commandMap.dispatch(sender, joiner.join(java.util.Arrays.asList(commands.get(i))))) {
						completed++;
					}
				} catch (Throwable exception) {
					if (this instanceof TileEntityCommandListener) {
						TileEntityCommandListener listener = (TileEntityCommandListener) this;
						MinecraftServer.getLogger().log(Level.WARN, String.format("CommandBlock at (%d,%d,%d) failed to handle command", listener.getChunkCoordinates().x, listener.getChunkCoordinates().y, listener.getChunkCoordinates().z), exception);
					} else if (this instanceof EntityMinecartCommandBlockListener) {
						EntityMinecartCommandBlockListener listener = (EntityMinecartCommandBlockListener) this;
						MinecraftServer.getLogger().log(Level.WARN, String.format("MinecartCommandBlock at (%d,%d,%d) failed to handle command", listener.getChunkCoordinates().x, listener.getChunkCoordinates().y, listener.getChunkCoordinates().z), exception);
					} else {
						MinecraftServer.getLogger().log(Level.WARN, String.format("Unknown CommandBlock failed to handle command"), exception);
					}
				}
			}

			b = completed;
			// CraftBukkit end
		} else {
			b = 0;
		}
	}

	// CraftBukkit start
	private ArrayList<String[]> buildCommands(String[] args, int pos) {
		ArrayList<String[]> commands = new ArrayList<String[]>();
		EntityPlayer[] players = PlayerSelector.getPlayers(this, args[pos]);
		if (players != null) {
			for (EntityPlayer player : players) {
				if (player.world != getWorld()) {
					continue;
				}
				String[] command = args.clone();
				command[pos] = player.getName();
				commands.add(command);
			}
		}

		return commands;
	}

	// CraftBukkit end

	@Override
	public String getName() {
		return f;
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatComponentText(getName());
	}

	public void setName(String s) {
		f = s;
	}

	@Override
	public void sendMessage(IChatBaseComponent ichatbasecomponent) {
		if (c && getWorld() != null && !getWorld().isStatic) {
			d = new ChatComponentText("[" + a.format(new Date()) + "] ").addSibling(ichatbasecomponent);
			e();
		}
	}

	public abstract void e();

	public void b(IChatBaseComponent ichatbasecomponent) {
		d = ichatbasecomponent;
	}
}
