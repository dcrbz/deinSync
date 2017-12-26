package bz.dcr.deinsync.listener.packet;

import bz.dcr.deinsync.DeinSyncPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class ExperiencePacketListener extends PacketAdapter {

    private DeinSyncPlugin plugin;


    public ExperiencePacketListener(DeinSyncPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.EXPERIENCE);
        this.plugin = plugin;
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        // Save player profile
        plugin.getExecutorService().execute(() ->
                plugin.getSyncManager().savePlayer(event.getPlayer()));
    }
}
