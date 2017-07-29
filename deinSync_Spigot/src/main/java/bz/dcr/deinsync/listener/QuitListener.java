package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private DeinSyncPlugin plugin;


    public QuitListener(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getSyncManager().savePlayer(event.getPlayer());
    }

}
