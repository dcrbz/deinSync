package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.event.EventHandler;
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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getExecutorService().execute(() ->
                plugin.getSyncManager().savePlayer(event.getPlayer()));
    }


}
