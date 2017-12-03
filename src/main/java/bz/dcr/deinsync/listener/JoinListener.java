package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private DeinSyncPlugin plugin;


    public JoinListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
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
