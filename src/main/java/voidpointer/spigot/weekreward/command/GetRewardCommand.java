package voidpointer.spigot.weekreward.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.weekreward.config.PluginConfig;
import voidpointer.spigot.weekreward.locale.Locale;
import voidpointer.spigot.weekreward.message.WeekRewardMessage;
import voidpointer.spigot.weekreward.reward.RewardQueue;
import voidpointer.spigot.weekreward.reward.RewardQueueStorage;
import voidpointer.spigot.weekreward.reward.RewardTier;
import voidpointer.spigot.weekreward.reward.RewardTierConfig;

import java.util.*;

@RequiredArgsConstructor
public final class GetRewardCommand implements CommandExecutor {
    public static final String COMMAND_NAME = "get-reward";

    @NonNull private final PluginConfig config;
    @NonNull private final Locale locale;

    @Override public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            onSenderNotPlayer(sender);
            return true;
        }
        RewardQueueStorage rewardCache = config.getRewardQueueStorage();
        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        if (!rewardCache.hasRewards(playerUuid)) {
            onNoRewards(player);
            return true;
        }
        RewardQueue rewards = rewardCache.getRewards(playerUuid);
        PlayerInventory playerInventory = player.getInventory();
        if (playerInventory.firstEmpty() == -1) {
            onInventoryFull(sender);
            return true;
        }
        int rewardsNumberToReceive = Math.min(rewards.size(), getEmptySlotsNumber(playerInventory));
        String rewardsGotten = generateGottenRewardsMessage(rewards, rewardsNumberToReceive);
        giveRewards(playerInventory, rewards, rewardsNumberToReceive);
        onGotRewards(sender, rewardsGotten);
        if (rewards.isEmpty())
            rewardCache.clearRewards(player.getUniqueId());
        config.save();
        return true;
    }

    private String generateGottenRewardsMessage(final RewardQueue rewards, int rewardsNumberToReceive) {
        RewardTierConfig rewardTierConfig = config.getRewardTierConfig();
        StringBuilder message = new StringBuilder();
        String rewardDelimiter = locale.getLocalized(WeekRewardMessage.REWARD_LIST_DELIMITER).getRawMessage();
        String rewardPrefix = locale.getLocalized(WeekRewardMessage.REWARD_LIST_PREFIX).getRawMessage();

        Iterator<RewardTier> rewardsIterator = rewards.iterator();
        while (true) {
            ItemStack reward = rewardTierConfig.getItemStack(rewardsIterator.next());
            message.append(rewardPrefix).append(getDisplayName(reward));
            if (--rewardsNumberToReceive <= 0)
                break;
            message.append(rewardDelimiter);
        }
        return message.toString();
    }

    private void giveRewards(
            final PlayerInventory playerInventory,
            final RewardQueue rewards,
            int rewardsNumberToReceive
    ) {
        RewardTierConfig rewardTierConfig = config.getRewardTierConfig();
        for (;rewardsNumberToReceive > 0; rewardsNumberToReceive--) {
            playerInventory.addItem(rewardTierConfig.getItemStack(rewards.pollReward()));
        }
    }

    private int getEmptySlotsNumber(final PlayerInventory inventory) {
        return (int) Arrays.stream(inventory.getStorageContents())
                .filter(this::isEmpty)
                .count();
    }

    private boolean isEmpty(final ItemStack item) {
        return (item == null) || (item.getType() == Material.AIR);
    }

    private String getDisplayName(final ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            return itemStack.getItemMeta().getDisplayName();
        return itemStack.getType().name().toLowerCase().replace('_', ' ');
    }

    private void onSenderNotPlayer(final CommandSender sender) {
        locale.getLocalized(WeekRewardMessage.SENDER_NOT_PLAYER)
                .colorize()
                .send(sender);
    }

    private void onNoRewards(final CommandSender sender) {
        locale.getLocalized(WeekRewardMessage.NO_REWARDS)
                .colorize()
                .send(sender);
    }

    private void onInventoryFull(final CommandSender sender) {
        locale.getLocalized(WeekRewardMessage.INVENTORY_FULL)
                .colorize()
                .send(sender);
    }

    private void onGotRewards(final CommandSender sender, final String rewardsGotten) {
        locale.getLocalized(WeekRewardMessage.GOT_REWARDS)
                .set("{rewards-gotten}", rewardsGotten)
                .colorize()
                .send(sender);
    }

    public void register(final JavaPlugin plugin) {
        plugin.getCommand(COMMAND_NAME).setExecutor(this);
    }
}
