package voidpointer.spigot.weekreward.reward;

import org.bukkit.inventory.ItemStack;

public interface RewardTierConfig {
    ItemStack getItemStack(final RewardTier rewardTier);

    void setRewardTier(final RewardTier rewardTier, final ItemStack itemStack);

    boolean isSet(final RewardTier rewardTier);
}
