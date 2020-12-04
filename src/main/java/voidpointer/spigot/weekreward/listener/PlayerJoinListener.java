package voidpointer.spigot.weekreward.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.weekreward.locale.Locale;
import voidpointer.spigot.weekreward.message.AvailableRewardMessageBuilder;
import voidpointer.spigot.weekreward.reward.RewardQueueStorage;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class PlayerJoinListener implements Listener {
    @NonNull private final Map<UUID, Long> joinTimeCache;
    @NonNull private final RewardQueueStorage rewardQueueStorage;
    @NonNull private final Locale locale;

    @EventHandler public void addJoinTime(final PlayerJoinEvent event) {
        joinTimeCache.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler public void sendAvailableRewardsMessage(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!rewardQueueStorage.hasRewards(player.getUniqueId()))
            return;
        player.spigot().sendMessage(AvailableRewardMessageBuilder.build(locale));
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
