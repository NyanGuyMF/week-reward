package voidpointer.spigot.weekreward.reward;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.UUID;

public interface RewardQueueStorage extends ConfigurationSerializable {
    RewardQueue getRewards(UUID uuid) throws RewardNotFoundException;

    void addReward(UUID uuid, RewardTier rewardTier);

    void clearRewards(UUID uuid);

    boolean hasRewards(UUID uuid);
}
