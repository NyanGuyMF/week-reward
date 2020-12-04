package voidpointer.spigot.weekreward.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.weekreward.locale.Message;

@Getter
@RequiredArgsConstructor
public enum WeekRewardMessage implements Message {
    AVAILABLE_REWARDS("&3&l>> &eУ Вас есть доступные награды! Нажмите: &6&o/get-reward"),
    AVAILABLE_REWARDS_ON_HOVER("&eВыполнить &6/get-reward"),
    EMPTY_HAND("&eВозьмите желаемую награду в руки!"),
    GOT_REWARDS("&3&l>> &eВы получили: {rewards-gotten}&e."),
    INVALID_TIER("&eНеверно указано место награды, список верных: &6{valid-tiers}&e."),
    INVENTORY_FULL("&eВ вашем инвентаре нет свободных слотов, освободите место для награды."),
    NO_REWARDS("&eУ Вас нет доступных наград."),
    REWARD_LIST_DELIMITER("&6, "),
    REWARD_LIST_PREFIX("&c"),
    REWARD_SET("&eВы установили награду: &6{reward} &eдля &6{tier} &eместа."),
    SENDER_NOT_PLAYER("&cТолько игрок может выполнять эту команду."),
    WINNER("&6&lВы заняли &c&l{tier} &6&lместо по наигранным часам!")
    ;
    @NonNull private final String defaultMessage;

    @Override public String getPath() {
        return toString().toLowerCase().replace('_', '-');
    }
}
