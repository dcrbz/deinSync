package bz.dcr.deinsync.commons.message;

import java.util.UUID;

public class PlayerProfileUpdateMessage {

    private UUID playerId;
    private String group;


    public PlayerProfileUpdateMessage() {
    }

    public PlayerProfileUpdateMessage(UUID playerId, String group) {
        this.playerId = playerId;
        this.group = group;
    }


    public UUID getPlayerId() {
        return playerId;
    }

    public String getGroup() {
        return group;
    }

}
