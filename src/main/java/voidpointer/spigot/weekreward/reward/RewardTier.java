package voidpointer.spigot.weekreward.reward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum RewardTier {
    FIRST(1), SECOND(2), THIRD(3);

    public static final Map<String, RewardTier> TIERS = Arrays.stream(RewardTier.values())
            .collect(Collectors.toMap(reward -> reward.getTierPath(), reward -> reward));

    private final int tier;

    public String getTierPath() {
        return Integer.toUnsignedString(getTier());
    }
}
