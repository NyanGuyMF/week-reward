package voidpointer.spigot.weekreward.config;

import java.util.Date;
import java.util.SortedSet;
import java.util.UUID;

public interface PlayedTimeStorage {
    SortedSet<PlayedTimeEntry> getAllUsersTimes();

    void setTimePlayedTotal(UUID uniqueId, long timePlayedInMillis);

    long getPlayedTime(UUID uniqueId);

    void clearPlayedTime();

    Date getLastWeekTrackingDate();

    void setLastWeekTrackingDate(Date date);

    void save();

    interface PlayedTimeEntry extends Comparable<PlayedTimeEntry> {
        UUID getUniqueId();

        long getPlayedTimeInMillis();
    }
}
