package voidpointer.spigot.weekreward.config;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.HashMap;
import java.util.Map;

public final class RewardTierConfig implements voidpointer.spigot.weekreward.reward.RewardTierConfig {
    @NonNull private Map<RewardTier, ItemStack> bareRewards = new HashMap<>();
    @NonNull private ConfigurationSection config;

    public RewardTierConfig(final ConfigurationSection config) {
        load(config);
    }

    @Override public ItemStack getItemStack(final RewardTier rewardTier) {
        return bareRewards.get(rewardTier);
    }

    @Override public void setRewardTier(final RewardTier rewardTier, final ItemStack itemStack) {
        bareRewards.put(rewardTier, itemStack);
        config.set(rewardTier.getTierPath(), itemStack.serialize());
    }

    @Override public boolean isSet(final RewardTier rewardTier) {
        return bareRewards.containsKey(rewardTier);
    }

    public void reload(final ConfigurationSection config) {
        bareRewards.clear();
        load(config);
    }

    private void load(final ConfigurationSection config) {
        this.config = config;
        for (Map.Entry<String, RewardTier> tierEntry : RewardTier.TIERS.entrySet()) {
            ConfigurationSection itemStackSection = config.getConfigurationSection(tierEntry.getKey());
            if (itemStackSection == null)
                continue;
            bareRewards.put(tierEntry.getValue(), ItemStack.deserialize(itemStackSection.getValues(true)));
        }
    }
}
