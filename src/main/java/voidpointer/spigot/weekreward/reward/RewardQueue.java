package voidpointer.spigot.weekreward.reward;

public interface RewardQueue extends Iterable<RewardTier> {
    boolean addReward(RewardTier rewardTier);

    /** Retrieve and remove reward. */
    RewardTier pollReward();

    /** Retrieve, but not remove reward. */
    RewardTier peekReward();

    int size();

    boolean isEmpty();
}
