package org.spigotmc;

import java.util.List;

import net.minecraft.server.ChatComponentText;
import net.minecraft.server.ChatModifier;
import net.minecraft.server.EnumChatFormat;
import net.minecraft.server.IChatBaseComponent;

import org.bukkit.ChatColor;

public class SpigotComponentReverter {
	public static String toLegacy(IChatBaseComponent s) {
		StringBuilder builder = new StringBuilder();
		legacy(builder, s);
		return builder.toString();
	}

	private static void legacy(StringBuilder builder, IChatBaseComponent s) {
		ChatModifier modifier = s.getChatModifier();
		colorize(builder, modifier);
		if (s instanceof ChatComponentText) {
			builder.append(s.e());
		} else
			throw new RuntimeException("Unhandled type: " + s.getClass().getSimpleName());

		for (IChatBaseComponent c : getExtra(s)) {
			legacy(builder, c);
		}
	}

	private static void colorize(StringBuilder builder, ChatModifier modifier) {
		if (modifier == null)
			return;
		// Color first
		EnumChatFormat color = getColor(modifier);
		if (color == null) {
			color = EnumChatFormat.BLACK;
		}
		builder.append(color.toString());

		if (isBold(modifier)) {
			builder.append(ChatColor.BOLD);
		}
		if (isItalic(modifier)) {
			builder.append(ChatColor.ITALIC);
		}
		if (isRandom(modifier)) {
			builder.append(ChatColor.MAGIC);
		}
		if (isStrikethrough(modifier)) {
			builder.append(ChatColor.STRIKETHROUGH);
		}
		if (isUnderline(modifier)) {
			builder.append(ChatColor.UNDERLINE);
		}
	}

	// Helpers
	private static List<IChatBaseComponent> getExtra(IChatBaseComponent c) {
		return c.a();
	}

	private static EnumChatFormat getColor(ChatModifier c) {
		return c.a();
	}

	private static boolean isBold(ChatModifier c) {
		return c.b();
	}

	private static boolean isItalic(ChatModifier c) {
		return c.c();
	}

	private static boolean isStrikethrough(ChatModifier c) {
		return c.d();
	}

	private static boolean isUnderline(ChatModifier c) {
		return c.e();
	}

	private static boolean isRandom(ChatModifier c) {
		return c.f();
	}
}
