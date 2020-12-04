package voidpointer.spigot.weekreward.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PlayedTimeConfig implements PlayedTimeStorage {
    private static final String PLAYED_TIMES_PATH = "played-time-cache";
    private static final String LAST_WEEK_PATH = "last-week";
    private static final String CONFIG_FILENAME = "played-time-cache.yml";
    private static final Long DEFAULT_PLAYED_TIME = new Long(0L);

    private final Logger logger;
    private final File configFile;
    private Map<UUID, Long> cache = new HashMap<>();
    private FileConfiguration fileConfiguration;
    private ConfigurationSection config;
    @Getter private Date lastWeekTrackingDate;

    public PlayedTimeConfig(final Plugin plugin) throws IOException {
        logger = plugin.getLogger();
        configFile = new File(plugin.getDataFolder(), CONFIG_FILENAME);
        Locale.setDefault(Locale.FRANCE);
        load();
    }

    @Override public SortedSet<PlayedTimeStorage.PlayedTimeEntry> getAllUsersTimes() {
        TreeSet<PlayedTimeStorage.PlayedTimeEntry> allUsersTimes = new TreeSet<>(Comparator.reverseOrder());
        cache.entrySet().forEach(entry -> allUsersTimes.add(new PlayedTimeEntry(entry.getKey(), entry.getValue())));
        return Collections.unmodifiableSortedSet(allUsersTimes);
    }

    @Override public void setTimePlayedTotal(final UUID uniqueId, final long timePlayedInMillis) {
        cache.put(uniqueId, timePlayedInMillis);
        config.set(uniqueId.toString(), timePlayedInMillis);
    }

    @Override public long getPlayedTime(final UUID uniqueId) {
        return cache.getOrDefault(uniqueId, DEFAULT_PLAYED_TIME);
    }

    @Override public void clearPlayedTime() {
        cache.clear();
        config = fileConfiguration.createSection(PLAYED_TIMES_PATH);
    }

    @Override public void setLastWeekTrackingDate(final Date date) {
        lastWeekTrackingDate = date;
        fileConfiguration.set(LAST_WEEK_PATH, date.toInstant().toEpochMilli());
    }

    @Override public void save() {
        try {
            fileConfiguration.save(configFile);
        } catch (IOException ioException) {
            logger.severe(String.format(
                    "Unable to save %s config: %s",
                    CONFIG_FILENAME,
                    ioException.getMessage()
            ));
        }
    }

    private void load() throws IOException {
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        if (fileConfiguration.isSet(PLAYED_TIMES_PATH))
            config = fileConfiguration.getConfigurationSection(PLAYED_TIMES_PATH);
        else
            config = fileConfiguration.createSection(PLAYED_TIMES_PATH);
        loadLastWeekTrackingDate();
        loadEntries();
    }

    private void loadLastWeekTrackingDate() {
        if (!fileConfiguration.isSet(LAST_WEEK_PATH)) {
            lastWeekTrackingDate = getCurrentWeekStartDate();
            fileConfiguration.set(LAST_WEEK_PATH, lastWeekTrackingDate.toInstant().toEpochMilli());
            save();
        } else {
            lastWeekTrackingDate = new Date(fileConfiguration.getLong(LAST_WEEK_PATH));
        }
    }

    private Date getCurrentWeekStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void loadEntries() {
        config.getValues(false).forEach((uniqueIdString, playedTimeInMillisObj) -> {
            UUID uniqueId = UUID.fromString(uniqueIdString);
            long playedTimeInMillis = Long.parseLong(playedTimeInMillisObj.toString());
            cache.put(uniqueId, playedTimeInMillis);
        });
    }

    @Data private static class PlayedTimeEntry implements PlayedTimeStorage.PlayedTimeEntry {
        private final UUID uniqueId;
        @EqualsAndHashCode.Exclude
        private final long playedTimeInMillis;

        @Override public int compareTo(final PlayedTimeStorage.PlayedTimeEntry o) {
            return Long.compare(playedTimeInMillis, o.getPlayedTimeInMillis());
        }
    }
}
