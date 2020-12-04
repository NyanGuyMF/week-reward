/*
 * Copyright (c) 2020.
 * Vasiliy Bely (vk.me/voidpointer) _voidpointer
 */

package voidpointer.spigot.weekreward.config;

import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import voidpointer.spigot.weekreward.locale.Locale;
import voidpointer.spigot.weekreward.locale.LocalizedMessage;
import voidpointer.spigot.weekreward.locale.Message;

import java.util.logging.Logger;

public final class LocaleConfig implements Locale {
    private final Logger logger;
    @Setter(AccessLevel.PACKAGE)
    private ConfigurationSection config;

    public LocaleConfig(final Logger logger, final ConfigurationSection config) {
        this.logger = logger;
        this.config = config;
    }

    @Override public LocalizedMessage getLocalized(final Message message) {
        String rawMessage = config.getString(message.getPath());
        if (rawMessage == null) {
            logger.warning(
                    "Undefined locale key: «" + message.getPath()
                    + "» using default value instead."
            );
            rawMessage = message.getDefaultMessage();
        }
        return LocalizedMessageBuilder.build(rawMessage);
    }
}
