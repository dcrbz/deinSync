package bz.dcr.deinsync.logging;

import bz.dcr.deinsync.config.ConfigKey;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class LogManager {

    private Plugin plugin;


    public LogManager(Plugin plugin) {
        this.plugin = plugin;
    }


    public void info(String message) {
        plugin.getLogger().log(Level.INFO, message);
    }

    public void warning(String message) {
        plugin.getLogger().log(Level.WARNING, message);
    }

    public void error(String message) {
        plugin.getLogger().log(Level.SEVERE, message);
    }

    public void debug(String message) {
        // Debug mode is not enabled
        if (!plugin.getConfig().getBoolean(ConfigKey.DEINSYNC_DEBUG)) {
            return;
        }

        info("[Debug] " + message);
    }

}
