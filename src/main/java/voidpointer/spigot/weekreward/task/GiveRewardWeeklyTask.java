package voidpointer.spigot.weekreward.task;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import voidpointer.spigot.weekreward.config.PlayedTimeStorage;
import voidpointer.spigot.weekreward.config.PluginConfig;
import voidpointer.spigot.weekreward.event.NewWeeklyWinnersEvent;
import voidpointer.spigot.weekreward.event.PluginEventManager;
import voidpointer.spigot.weekreward.reward.RewardTier;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class GiveRewardWeeklyTask extends BukkitRunnable {
    private static final SortedSet<RewardTier> sortedTiers = Arrays.stream(RewardTier.values())
            .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(RewardTier::getTier))));

    @NonNull private final PlayedTimeStorage playedTimeStorage;
    @NonNull private final PluginConfig pluginConfig;
    @NonNull private final PluginEventManager eventManager;

    @Override public void run() {
        Iterator<PlayedTimeStorage.PlayedTimeEntry> winnersIterator = playedTimeStorage.getAllUsersTimes().iterator();
        Iterator<RewardTier> winnerTiers = sortedTiers.iterator();
        Map<UUID, RewardTier> winners = new HashMap<>();
        while (winnersIterator.hasNext() && winnerTiers.hasNext()) {
            UUID winnerUniqueId = winnersIterator.next().getUniqueId();
            RewardTier winnerTier = winnerTiers.next();
            winners.put(winnerUniqueId, winnerTier);
            pluginConfig.getRewardQueueStorage().addReward(winnerUniqueId, winnerTier);
        }
        pluginConfig.save();

        playedTimeStorage.clearPlayedTime();
        if (isCurrentWeek(playedTimeStorage.getLastWeekTrackingDate()))
            playedTimeStorage.setLastWeekTrackingDate(getNextWeekDate());
        else
            playedTimeStorage.setLastWeekTrackingDate(getCurrentWeekDate());
        playedTimeStorage.save();

        eventManager.callEventSync(new NewWeeklyWinnersEvent(Collections.unmodifiableMap(winners)));
    }

    public void schedule(final Plugin plugin) {
        long taskRunDelayInSeconds;
        if (!isCurrentWeek(playedTimeStorage.getLastWeekTrackingDate())) {
            taskRunDelayInSeconds = 0;
        } else {
            taskRunDelayInSeconds = calculateDelayUntilNextWeek();
        }
        runTaskTimer(plugin, toTicks(taskRunDelayInSeconds), toTicks(TimeUnit.DAYS.toSeconds(7)));
    }

    private long toTicks(long seconds) {
        return seconds * 20L;
    }

    private boolean isCurrentWeek(final Date date) {
        Calendar currentWeekCal = Calendar.getInstance(Locale.FRANCE);
        Calendar otherWeekCal = Calendar.getInstance(Locale.FRANCE);
        currentWeekCal.setTime(new Date());
        otherWeekCal.setTime(date);
        resetCalendarsToWeek(currentWeekCal, otherWeekCal);
        return currentWeekCal.getTime().equals(otherWeekCal.getTime());
    }

    private long calculateDelayUntilNextWeek() {
        Date currentDate = new Date();
        Date nextWeekDate = getNextWeekDate();
        if (!nextWeekDate.after(currentDate))
            throw new IllegalStateException("Current date cannot be on next week");
        return nextWeekDate.toInstant().getEpochSecond() - currentDate.toInstant().getEpochSecond();
    }

    private Date getNextWeekDate() {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(new Date());
        resetCalendarsToWeek(cal);
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    private Date getCurrentWeekDate() {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(new Date());
        resetCalendarsToWeek(cal);
        return cal.getTime();
    }

    private static void resetCalendarsToWeek(final Calendar...calendars) {
        for (Calendar cal : calendars) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
    }
}
