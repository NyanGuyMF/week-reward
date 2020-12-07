package voidpointer.spigot.weekreward.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.weekreward.config.PluginConfig;
import voidpointer.spigot.weekreward.message.WeekRewardMessage;
import voidpointer.spigot.weekreward.reward.RewardTier;
import voidpointer.spigot.weekreward.reward.RewardTierConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class SetRewardCommand implements CommandExecutor {
    @NonNull private final PluginConfig config;
    @NonNull private final Locale locale;

    @Override public boolean onCommand(
            final CommandSender sender,
            final Command command,
            final String label,
            final String[] args
    ) {
        if (args.length < 1)
            return false;
        if (!(sender instanceof Player)) {
            onSenderNotPlayer(sender);
            return true;
        }
        Player player = (Player) sender;
        ItemStack rewardItem = player.getInventory().getItemInMainHand();
        if (isEmpty(rewardItem)) {
            onEmptyHand(sender);
            return true;
        }
        final String rewardTierString = args[0];
        if (!RewardTier.TIERS.containsKey(rewardTierString)) {
            onInvalidTier(sender, rewardTierString);
            return true;
        }
        RewardTier rewardTier = RewardTier.TIERS.get(rewardTierString);
        RewardTierConfig rewardTierConfig = config.getRewardTierConfig();
        rewardTierConfig.setRewardTier(rewardTier, rewardItem);
        config.save();
        onRewardSet(sender, rewardItem, rewardTier);
        return true;
    }

    private boolean isEmpty(final ItemStack rewardItem) {
        return (rewardItem == null) || (rewardItem.getType() == Material.AIR);
    }

    private void onSenderNotPlayer(final CommandSender sender) {
        locale.localize(WeekRewardMessage.SENDER_NOT_PLAYER)
                .colorize()
                .send(sender);
    }

    private void onEmptyHand(final CommandSender sender) {
        locale.localize(WeekRewardMessage.EMPTY_HAND)
                .colorize()
                .send(sender);
    }

    private void onInvalidTier(final CommandSender sender, final String arg) {
        locale.localize(WeekRewardMessage.INVALID_TIER)
                .colorize()
                .set("{valid-tiers}", RewardTier.TIERS.keySet().stream().collect(Collectors.joining(", ")))
                .send(sender);
    }

    private void onRewardSet(final CommandSender sender, final ItemStack rewardItem, final RewardTier rewardTier) {
        locale.localize(WeekRewardMessage.REWARD_SET)
                .colorize()
                .set("{reward}", rewardItem.toString())
                .set("{tier}", rewardTier.getTierPath())
                .send(sender);
    }

    public void register(final JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand("set-reward");
        command.setExecutor(this);
        command.setTabCompleter(this::onTabComplete);
    }

    private List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.unmodifiableList(new ArrayList<>(RewardTier.TIERS.keySet()));
    }
}
