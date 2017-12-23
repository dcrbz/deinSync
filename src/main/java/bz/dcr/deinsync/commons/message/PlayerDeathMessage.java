package bz.dcr.deinsync.commons.message;

import java.util.UUID;

public class PlayerDeathMessage {

    private UUID playerId;


    public PlayerDeathMessage() {
    }

    public PlayerDeathMessage(UUID playerId) {
        this.playerId = playerId;
    }


    public UUID getPlayerId() {
        return playerId;
    }

}
