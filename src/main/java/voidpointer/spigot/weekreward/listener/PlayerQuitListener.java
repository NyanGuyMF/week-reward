package voidpointer.spigot.weekreward.listener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.weekreward.config.PlayedTimeStorage;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class PlayerQuitListener implements Listener {
    @NonNull private final Map<UUID, Long> joinTimeCache;
    @NonNull private final PlayedTimeStorage playedTimeStorage;

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        long quitTime = System.currentTimeMillis();
        UUID playerUniqueId = event.getPlayer().getUniqueId();
        long joinTime = joinTimeCache.remove(playerUniqueId);
        long timePlayedTotal = (quitTime - joinTime) + playedTimeStorage.getPlayedTime(playerUniqueId);
        playedTimeStorage.setTimePlayedTotal(playerUniqueId, timePlayedTotal);
        playedTimeStorage.save();
    }

    public void register(final Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
