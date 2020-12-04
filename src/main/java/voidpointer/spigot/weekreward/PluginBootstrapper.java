package voidpointer.spigot.weekreward;

import java.io.File;

final class PluginBootstrapper {
    public static void bootstrap(final WeekRewardPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
    }
}
