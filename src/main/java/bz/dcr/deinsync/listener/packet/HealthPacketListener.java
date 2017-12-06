package bz.dcr.deinsync.listener.packet;

import bz.dcr.deinsync.DeinSyncPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;

public class HealthPacketListener extends PacketAdapter {

    private DeinSyncPlugin plugin;


    public HealthPacketListener(DeinSyncPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_HEALTH);
        this.plugin = plugin;
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        // Save player profile
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getSyncManager().savePlayer(event.getPlayer()));
    }
}
