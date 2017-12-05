package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.config.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class JoinListener implements Listener {

    private DeinSyncPlugin plugin;


    public JoinListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Lock/Unlock player
        if (plugin.getConfig().getBoolean(ConfigKey.DEINSYNC_SECURITY_LOCK_ENABLED)) {
            event.getPlayer().setMetadata(DeinSyncPlugin.LOCK_PLAYER_TAG, new FixedMetadataValue(plugin, true));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                event.getPlayer().removeMetadata(DeinSyncPlugin.LOCK_PLAYER_TAG, plugin);
            }, plugin.getConfig().getLong(ConfigKey.DEINSYNC_SECURITY_LOCK_DURATION));
        }

        final boolean hasPendingLogin = plugin.getSyncManager().checkAndDeletePendingLogin(
                event.getPlayer().getUniqueId()
        );

        // No pending login
        if(!hasPendingLogin) {
            plugin.getLogManager().debug("No pending login for " + event.getPlayer().getUniqueId().toString() + ".");
            return;
        }

        // Load player data
        plugin.getSyncManager().loadPlayer(event.getPlayer());
        plugin.getLogManager().debug("Successfully loaded profile for " + event.getPlayer().getUniqueId().toString() + ".");
    }

}
