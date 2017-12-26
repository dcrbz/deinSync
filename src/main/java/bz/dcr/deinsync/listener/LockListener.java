package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.config.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class LockListener implements Listener {

    private DeinSyncPlugin plugin;


    public LockListener(DeinSyncPlugin plugin) {
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
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata(DeinSyncPlugin.LOCK_PLAYER_TAG)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        // Entity is not a player
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player) event.getEntity();

        if (player.hasMetadata(DeinSyncPlugin.LOCK_PLAYER_TAG)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().hasMetadata(DeinSyncPlugin.LOCK_PLAYER_TAG)) {
            event.setKeepLevel(true);
            event.setKeepInventory(true);
        }
    }

}
