package org.bukkit.craftbukkit.command;

import net.minecraft.server.ICommandListener;
import net.minecraft.server.TileEntityCommandListener;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;

/**
 * Represents input from a command block
 */
public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
	private final TileEntityCommandListener commandBlock;

	public CraftBlockCommandSender(TileEntityCommandListener commandBlockListenerAbstract) {
		super();
		commandBlock = commandBlockListenerAbstract;
	}

	@Override
	public Block getBlock() {
		return commandBlock.getWorld().getWorld().getBlockAt(commandBlock.getChunkCoordinates().x, commandBlock.getChunkCoordinates().y, commandBlock.getChunkCoordinates().z);
	}

	@Override
	public void sendMessage(String message) {
	}

	@Override
	public void sendMessage(String[] messages) {
	}

	@Override
	public String getName() {
		return commandBlock.getName();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException("Cannot change operator status of a block");
	}

	public ICommandListener getTileEntity() {
		return commandBlock;
	}
}
