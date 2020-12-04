package voidpointer.spigot.weekreward.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.weekreward.event.NewWeeklyWinnersEvent;
import voidpointer.spigot.weekreward.locale.Locale;
import voidpointer.spigot.weekreward.message.AvailableRewardMessageBuilder;
import voidpointer.spigot.weekreward.message.WeekRewardMessage;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class NewWeeklyWinnersListener implements Listener {
    @NonNull private final Locale locale;

    @EventHandler public void onNewWeekWinners(final NewWeeklyWinnersEvent event) {
        Map<UUID, RewardTier> winners = event.getWinners();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!winners.containsKey(player.getUniqueId()))
                continue;

            String message = locale.getLocalized(WeekRewardMessage.WINNER).colorize().getRawMessage();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            player.spigot().sendMessage(AvailableRewardMessageBuilder.build(locale));
        }
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
