package voidpointer.spigot.weekreward.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class PluginEventManager {
    @NonNull private final Plugin plugin;

    public void callEventSync(final Event event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getServer().getPluginManager().callEvent(event);
        });
    }
}
