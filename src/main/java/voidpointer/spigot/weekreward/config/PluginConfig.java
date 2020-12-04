package voidpointer.spigot.weekreward.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.weekreward.reward.RewardConfig;
import voidpointer.spigot.weekreward.reward.RewardQueueStorage;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class PluginConfig implements RewardConfig {
    private static final String REWARDS_PATH = "rewards";
    private static final String TIER_PATH = "tiers";

    @NonNull private final Plugin plugin;
    @Getter private RewardQueueStorage rewardQueueStorage;
    private RewardTierConfig rewardTierConfig;

    public CompletableFuture<Void> loadAsync() {
        return CompletableFuture.runAsync(() -> load())
                .thenAccept((nullObject) -> plugin.getLogger().info("Main configuration loaded"));
    }

    @Override public voidpointer.spigot.weekreward.reward.RewardTierConfig getRewardTierConfig() {
        return rewardTierConfig;
    }

    @Override public void save() {
        plugin.getConfig().set(REWARDS_PATH, rewardQueueStorage.serialize());
        plugin.saveConfig();
    }

    @Override public void reload() {
        plugin.reloadConfig();
        loadRewardQueueStorage();
        rewardTierConfig.reload(plugin.getConfig().getConfigurationSection(TIER_PATH));
    }

    @Override public void load() {
        loadRewardQueueStorage();
        loadRewardTierMapper();
    }

    private void loadRewardQueueStorage() {
        if (!plugin.getConfig().isSet(REWARDS_PATH)) {
            rewardQueueStorage = new voidpointer.spigot.weekreward.config.RewardQueueStorage();
        } else {
            rewardQueueStorage = voidpointer.spigot.weekreward.config.RewardQueueStorage.deserialize(
                    plugin.getConfig().getConfigurationSection(REWARDS_PATH).getValues(true)
            );
        }
    }

    private void loadRewardTierMapper() {
        ConfigurationSection tierSection;
        if (plugin.getConfig().isSet(TIER_PATH)) {
            tierSection = plugin.getConfig().getConfigurationSection(TIER_PATH);
        } else {
            tierSection = plugin.getConfig().createSection(TIER_PATH);
        }
        rewardTierConfig = new RewardTierConfig(tierSection);
    }
}
