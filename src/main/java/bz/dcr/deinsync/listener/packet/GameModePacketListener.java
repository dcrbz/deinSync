package bz.dcr.deinsync.listener.packet;

import bz.dcr.deinsync.DeinSyncPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class GameModePacketListener extends PacketAdapter {

    private DeinSyncPlugin plugin;


    public GameModePacketListener(DeinSyncPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.GAME_STATE_CHANGE);
        this.plugin = plugin;
    }


    @Override
    public void onPacketSending(PacketEvent event) {
        // Changed state is not gamemode
        if (event.getPacket().getIntegers().read(0) != 3) {
            return;
        }

        // Save player profile
        plugin.getExecutorService().execute(() ->
                plugin.getSyncManager().savePlayer(event.getPlayer()));
    }

}
