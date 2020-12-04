package voidpointer.spigot.weekreward.reward;

public final class RewardNotSetException extends RuntimeException {
    public RewardNotSetException(final RewardTier rewardTier) {
        super(String.format("Reward for tier %d not found", rewardTier.getTier()), null, false, false);
    }
}
