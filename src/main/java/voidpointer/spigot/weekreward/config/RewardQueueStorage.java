package voidpointer.spigot.weekreward.config;

import lombok.RequiredArgsConstructor;
import voidpointer.spigot.weekreward.reward.RewardNotFoundException;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class RewardQueueStorage implements voidpointer.spigot.weekreward.reward.RewardQueueStorage {
    private final Map<UUID, RewardQueue> cache = new HashMap<>();

    @Override public RewardQueue getRewards(final UUID uuid) throws RewardNotFoundException {
        if (!cache.containsKey(uuid))
            throw new RewardNotFoundException(uuid);
        return cache.get(uuid);
    }

    @Override public void addReward(final UUID uuid, final RewardTier rewardTier) {
        RewardQueue playerRewards;
        if (cache.containsKey(uuid)) {
            playerRewards = cache.get(uuid);
        } else {
            playerRewards = new RewardQueue();
            cache.put(uuid, playerRewards);
        }
        playerRewards.addReward(rewardTier);
    }

    @Override public void clearRewards(final UUID uuid) {
        cache.remove(uuid);
    }

    @Override public boolean hasRewards(final UUID uuid) {
        return cache.containsKey(uuid) && !cache.get(uuid).isEmpty();
    }

    @Override public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        cache.entrySet().forEach(entry -> serialized.put(
                entry.getKey().toString(),
                entry.getValue().getRewards().stream()
                        .map(RewardTier::getTierPath)
                        .collect(Collectors.toList())
        ));
        return serialized;
    }

    public static RewardQueueStorage deserialize(final Map<String, Object> serialized) {
        RewardQueueStorage deserialized = new RewardQueueStorage();
        serialized.entrySet().forEach(entry -> deserialized.cache.put(
                UUID.fromString(entry.getKey()),
                new RewardQueue((Collection<String>) entry.getValue())
        ));
        return deserialized;
    }
}
