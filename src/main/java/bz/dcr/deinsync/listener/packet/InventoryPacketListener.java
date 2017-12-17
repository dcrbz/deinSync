package bz.dcr.deinsync.listener.packet;

import bz.dcr.deinsync.DeinSyncPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class InventoryPacketListener extends PacketAdapter {

    private DeinSyncPlugin plugin;


    public InventoryPacketListener(DeinSyncPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT);
        this.plugin = plugin;
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        // Updated slot is not inside a player inventory
        if (event.getPacket().getIntegers().read(0) != 0) {
            return;
        }

        // Save player profile
        plugin.getExecutorService().execute(() ->
                plugin.getSyncManager().savePlayer(event.getPlayer()));
    }
}
