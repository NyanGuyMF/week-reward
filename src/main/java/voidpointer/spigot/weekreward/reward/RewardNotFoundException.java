package voidpointer.spigot.weekreward.reward;

import java.util.UUID;

public final class RewardNotFoundException extends RuntimeException {
    public RewardNotFoundException(final UUID uuid) {
        super(String.format("Reward for player with UUID %s not found", uuid.toString()), null, false, false);
    }
}
