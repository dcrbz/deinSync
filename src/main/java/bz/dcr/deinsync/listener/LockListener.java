package bz.dcr.deinsync.listener;

import bz.dcr.deinsync.DeinSyncPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class LockListener implements Listener {

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
