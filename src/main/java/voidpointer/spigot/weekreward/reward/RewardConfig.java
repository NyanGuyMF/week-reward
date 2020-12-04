package voidpointer.spigot.weekreward.reward;

public interface RewardConfig {
    RewardQueueStorage getRewardQueueStorage();

    RewardTierConfig getRewardTierConfig();

    void save();

    void reload();

    void load();
}
