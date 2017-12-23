package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private DeinSyncPlugin plugin;


    public DeathListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        // Clear inventory
        player.getInventory().clear();

        plugin.getExecutorService().execute(() -> {
            // Save player profile
            plugin.getSyncManager().savePlayer(event.getEntity());

            // Broadcast death
            plugin.getSyncManager().broadcastPlayerDeath(player.getUniqueId());
        });
    }

}
