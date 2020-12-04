package voidpointer.spigot.weekreward.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class NewWeeklyWinnersEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    @NonNull
    private final Map<UUID, RewardTier> winners;

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
