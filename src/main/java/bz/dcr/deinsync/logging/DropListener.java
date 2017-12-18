package bz.dcr.deinsync.logging;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropListener implements Listener {

    private DeinSyncPlugin plugin;


    public DropListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        plugin.getExecutorService().execute(() -> {
            plugin.getSyncManager().savePlayer(event.getPlayer());
        });
    }

}
