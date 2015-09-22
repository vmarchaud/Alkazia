package fr.thisismac.level;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleManager {

	/*
	 * Temps où l'affichage des titles apparait, reste et part par défaut, à modifier directement si besoin Attention
	 * quand vous envoyez un packet TIME au joueur cet dernier lui fixe les timer par défaut et les GARDE pour les
	 * prochain title
	 */
	public static int	defaultFadeIn	= 20;
	public static int	defaultFadeOut	= 20;
	public static int	defaultTimeStay	= 60;

	/**
	 * Titre et sous titre avec les valeurs par défaut (modifiables)
	 * @param player
	 * @param title
	 * @param subTitle
	 */
	public static void sendTitle(Player player, String title, String subTitle) {
		sendTitle(player, title, subTitle, defaultFadeIn, defaultFadeOut, defaultTimeStay);
	}

	/**
	 * Envois le title
	 * @param player
	 * @param title Le titre
	 * @param subTitle Sous titre
	 * @param fadeIn Temps d'apparition
	 * @param fadeOut Temps de disparition
	 * @param time durée de l'affichage
	 */
	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int fadeOut, int time) {
		// Les simples quotes le jason aime po :o
		title = title.replace("'", "\\'");
		subTitle = subTitle.replace("'", "\\'");
		CraftPlayer craftplayer = (CraftPlayer) player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
		IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subTitle + "'}");
		// Mojang débile le times est nécessaire alors que le packet prend tous les arguments en compte, en plus si le
		// times a un argument de "titre" il est ignoré... et seul les param du temps reste, super GG, quel intérêt
		// d'envoyer 3 packets au lieu d'un... -_-
		PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, titleJSON, fadeIn, time, fadeOut);
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
		connection.sendPacket(timesPacket);
		connection.sendPacket(titlePacket);
		connection.sendPacket(subtitlePacket);
	}
	/**
	 * Envoyer à plusieurs joueurs
	 * @param players
	 * @param title
	 * @param subTitle
	 */
	public static void sendTitleToPlayers(Player[] players, String title, String subTitle) {
		for (Player player : players) {
			sendTitle(player, title, subTitle);
		}
	}

	/**
	 * Envoyer à plusieurs joueurs (configurable)
	 * @param players
	 * @param title
	 * @param subTitle
	 * @param fadeIn
	 * @param fadeOut
	 * @param time
	 */
	public static void sendTitleToPlayers(Player[] players, String title, String subTitle, int fadeIn, int fadeOut, int time) {
		for (Player player : players) {
			sendTitle(player, title, subTitle, fadeIn, fadeOut, time);
		}
	}

	/**
	 * Envoyer un title à tous les joueurs (configurable)
	 * @param players
	 * @param title
	 * @param subTitle
	 * @param fadeIn
	 * @param fadeOut
	 * @param time
	 */
	public static void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int fadeOut, int time) {
		sendTitleToPlayers(Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]), title, subTitle, fadeIn, fadeOut, time);
	}

	/**
	 * Envoyer un title à tous les joueurs
	 * @param title
	 * @param subTitle
	 */
	public static void sendTitleToAllPlayers(String title, String subTitle) {
		sendTitleToPlayers(Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]), title, subTitle);
	}

	/**
	 * Retire le title
	 * @param player
	 */
	public static void clearTitle(Player player) {
		sendTitle(player, "", "");
	}

}
