package voidpointer.spigot.weekreward.message;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.weekreward.command.GetRewardCommand;

public class AvailableRewardMessageBuilder {
    public static TextComponent build(Locale locale) {
        String rawMessage = locale.localize(WeekRewardMessage.AVAILABLE_REWARDS)
                .colorize()
                .getRawMessage();
        String onHover = locale.localize(WeekRewardMessage.AVAILABLE_REWARDS_ON_HOVER)
                .colorize()
                .getRawMessage();
        TextComponent message = new TextComponent(TextComponent.fromLegacyText(rawMessage));
        message.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text(TextComponent.fromLegacyText(onHover))
        ));
        message.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/" + GetRewardCommand.COMMAND_NAME
        ));
        return message;
    }
}
