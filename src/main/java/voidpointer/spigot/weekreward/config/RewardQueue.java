package voidpointer.spigot.weekreward.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import static voidpointer.spigot.weekreward.reward.RewardTier.TIERS;

@NoArgsConstructor
final class RewardQueue implements voidpointer.spigot.weekreward.reward.RewardQueue {
    @Getter(AccessLevel.PACKAGE)
    private Queue<RewardTier> rewards = new LinkedList<>();

    protected RewardQueue(Collection<String> rewards) {
        for (String reward : rewards) {
            if (TIERS.containsKey(reward))
                this.rewards.add(TIERS.get(reward));
        }
    }

    @Override public boolean addReward(final RewardTier rewardTier) {
        return rewards.add(rewardTier);
    }

    @Override public RewardTier pollReward() {
        return rewards.poll();
    }

    @Override public RewardTier peekReward() {
        return rewards.peek();
    }

    @Override public int size() {
        return rewards.size();
    }

    @Override public boolean isEmpty() {
        return rewards.isEmpty();
    }

    @Override public Iterator<RewardTier> iterator() {
        return rewards.iterator();
    }
}
