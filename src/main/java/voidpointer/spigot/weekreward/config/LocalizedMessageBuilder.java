/*
 * Copyright (c) 2020.
 * Vasiliy Bely (vk.me/voidpointer) _voidpointer
 */
package voidpointer.spigot.weekreward.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.weekreward.locale.LocalizedMessage;

@Getter
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
class LocalizedMessageBuilder implements LocalizedMessage {
    private static final char DEFAULT_COLOR_CHAR = '&';
    @NonNull private String rawMessage;

    public static LocalizedMessage build(final String rawMessage) {
        return new LocalizedMessageBuilder(rawMessage);
    }

    @Override public LocalizedMessage sendMultiline(final CommandSender receiver) {
        receiver.sendMessage(rawMessage.split("\\n"));
        return this;
    }

    @Override public LocalizedMessage send(final CommandSender receiver) {
        receiver.sendMessage(rawMessage);
        return this;
    }

    @Override public LocalizedMessage colorize() {
        rawMessage = ChatColor.translateAlternateColorCodes(DEFAULT_COLOR_CHAR, rawMessage);
        return this;
    }

    @Override public LocalizedMessage colorize(final char colorChar) {
        rawMessage = ChatColor.translateAlternateColorCodes(colorChar, rawMessage);
        return this;
    }

    @Override public LocalizedMessage set(final String placeholder, final String replacement) {
        rawMessage = rawMessage.replace(placeholder, replacement);
        return this;
    }
}
