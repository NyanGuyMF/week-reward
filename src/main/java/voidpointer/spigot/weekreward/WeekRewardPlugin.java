package voidpointer.spigot.weekreward;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.config.LocaleFileConfiguration;
import voidpointer.spigot.weekreward.command.GetRewardCommand;
import voidpointer.spigot.weekreward.command.SetRewardCommand;
import voidpointer.spigot.weekreward.config.PlayedTimeConfig;
import voidpointer.spigot.weekreward.config.PluginConfig;
import voidpointer.spigot.weekreward.event.PluginEventManager;
import voidpointer.spigot.weekreward.listener.NewWeeklyWinnersListener;
import voidpointer.spigot.weekreward.listener.PlayerJoinListener;
import voidpointer.spigot.weekreward.listener.PlayerQuitListener;
import voidpointer.spigot.weekreward.task.GiveRewardWeeklyTask;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WeekRewardPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private Locale locale;
    private PlayedTimeConfig playedTimeConfig;
    private boolean hasLoadErrors = false;
    private Map<UUID, Long> joinTimeCache;
    private PluginEventManager eventManager;

    @Override public void onLoad() {
        PluginBootstrapper.bootstrap(this);
        pluginConfig = new PluginConfig(this);
        pluginConfig.loadAsync().exceptionally(ex -> {
            getLogger().severe("Unable to load main configuration.");
            hasLoadErrors = true;
            return null;
        }).join();
        locale = new LocaleFileConfiguration(this);
        try {
            playedTimeConfig = new PlayedTimeConfig(this);
        } catch (IOException ioException) {
            getLogger().severe("Unable to load PlayedTimeConfig: " + ioException.getMessage());
            hasLoadErrors = true;
        }
    }

    @Override public void onEnable() {
        if (hasLoadErrors) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        eventManager = new PluginEventManager(this);
        initializeJoinTimeCache();

        registerCommands();
        registerListeners();

        new GiveRewardWeeklyTask(playedTimeConfig, pluginConfig, eventManager).schedule(this);
    }

    private void registerListeners() {
        new PlayerJoinListener(joinTimeCache, pluginConfig.getRewardQueueStorage(), locale)
                .register(this);
        new PlayerQuitListener(joinTimeCache, playedTimeConfig).register(this);
        new NewWeeklyWinnersListener(locale).register(this);
    }

    private void initializeJoinTimeCache() {
        joinTimeCache = new ConcurrentHashMap<>();
        Long timeInMillis = System.currentTimeMillis();
        Bukkit.getOnlinePlayers().forEach(player -> {
            joinTimeCache.put(player.getUniqueId(), timeInMillis);
        });
    }

    private void registerCommands() {
        new GetRewardCommand(pluginConfig, locale).register(this);
        new SetRewardCommand(pluginConfig, locale).register(this);
    }

    @Override public void onDisable() {
        long disableTime = System.currentTimeMillis();
        joinTimeCache.keySet().forEach(uniqueId -> {
            long joinTime = joinTimeCache.get(uniqueId);
            long playedTimeTotal = (disableTime - joinTime) + playedTimeConfig.getPlayedTime(uniqueId);
            playedTimeConfig.setTimePlayedTotal(uniqueId, playedTimeTotal);
        });
        playedTimeConfig.save();
    }
}
