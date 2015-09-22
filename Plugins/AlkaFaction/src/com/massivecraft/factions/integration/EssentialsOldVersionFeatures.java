package com.massivecraft.factions.integration;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.IEssentialsChatListener;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

/*
 * This Essentials integration handler is for older 2.x.x versions of Essentials which have "IEssentialsChatListener"
 */

public class EssentialsOldVersionFeatures {
    private static EssentialsChat essChat;

    public static void integrateChat(final EssentialsChat instance) {
        EssentialsOldVersionFeatures.essChat = instance;
        try {
            EssentialsOldVersionFeatures.essChat.addEssentialsChatListener("Factions", new IEssentialsChatListener() {
                @Override
                public boolean shouldHandleThisChat(final AsyncPlayerChatEvent event) {
                    return P.p.shouldLetFactionsHandleThisChat(event);
                }

                @Override
                public String modifyMessage(final AsyncPlayerChatEvent event, final Player target, final String message) {
                    return FactionsChatListener.parseTags(message, event.getPlayer(), target);
                    //return message.replace(Conf.chatTagReplaceString, P.p.getPlayerFactionTagRelation(event.getPlayer(), target)).replace("[FACTION_TITLE]", P.p.getPlayerTitle(event.getPlayer()));
                }
            });
            P.p.log("Found and will integrate chat with " + EssentialsOldVersionFeatures.essChat.getDescription().getFullName());
        } catch (final NoSuchMethodError ex) {
            EssentialsOldVersionFeatures.essChat = null;
        }
    }

    public static void unhookChat() {
        if (EssentialsOldVersionFeatures.essChat != null) {
            EssentialsOldVersionFeatures.essChat.removeEssentialsChatListener("Factions");
        }
    }
}
