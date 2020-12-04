/*
 * Copyright (c) 2020.
 * Vasiliy Bely (vk.me/voidpointer) _voidpointer
 */

package voidpointer.spigot.weekreward.locale;

import org.bukkit.command.CommandSender;

public interface LocalizedMessage {
    LocalizedMessage send(CommandSender receiver);

    LocalizedMessage sendMultiline(CommandSender receiver);

    LocalizedMessage colorize();

    LocalizedMessage colorize(char colorChar);

    LocalizedMessage set(String placeholder, String replacement);

    String getRawMessage();
}
