package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private DeinSyncPlugin plugin;


    public JoinQuitListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getExecutorService().execute(() ->
                plugin.getSyncManager().loadPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        // Get player
        final Player player = event.getPlayer();

        plugin.getExecutorService().execute(() -> {
            // Update player profile
            plugin.getSyncManager().savePlayer(player);
        });
    }


}
