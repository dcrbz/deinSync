package bz.dcr.deinsync.sync;

import bz.dcr.bedrock.common.pubsub.Message;
import bz.dcr.bedrock.common.pubsub.MessageBuilder;
import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.commons.message.MessageChannel;
import bz.dcr.deinsync.commons.message.PlayerProfileUpdateMessage;
import bz.dcr.deinsync.config.ConfigKey;
import bz.dcr.deinsync.player.PlayerProfile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SyncManager {

    private DeinSyncPlugin plugin;


    public SyncManager(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    public void savePlayer(Player player) {
        PlayerProfile profile = fetchPlayerProfile(player.getUniqueId());

        if(profile != null) {
            // Update existing profile
            plugin.getLogManager().debug("Updating existing profile of " + player.getUniqueId().toString() + "...");
            profile = PlayerProfile.update(player, getServerGroup(), profile);
        } else {
            // Create new profile
            plugin.getLogManager().debug("Creating new profile for " + player.getUniqueId().toString() + "...");
            profile = createPlayerProfile(player);
        }

        // Save profile
        plugin.getPersistenceManager().savePlayerProfile(profile);
    }

    public void loadPlayer(Player player) {
        plugin.getLogManager().debug("Loading player...");

        // Get player profile
        final PlayerProfile profile = fetchOrCreatePlayerProfile(player);

        // Apply profile to player
        Bukkit.getScheduler().runTask(plugin, () -> profile.apply(player));

        plugin.getLogManager().debug("Applied player data.");
    }


    public PlayerProfile fetchOrCreatePlayerProfile(Player player) {
        PlayerProfile profile = fetchPlayerProfile(player.getUniqueId());

        if(profile != null) {
            plugin.getLogManager().debug("Found player profile of " + player.getUniqueId().toString() + ".");
        } else {
            plugin.getLogManager().debug("Creating new profile for " + player.getUniqueId().toString() + "...");
            profile = createPlayerProfile(player);
        }

        return profile;
    }

    public PlayerProfile fetchPlayerProfile(UUID playerId) {
        final MongoCollection<PlayerProfile> collection = plugin.getMongo().getMongoDatabase()
                .getCollection(PlayerProfile.COLLECTION_NAME, PlayerProfile.class);

        final PlayerProfile profile = collection.find(
                Filters.and(
                        Filters.eq("playerId", playerId.toString()),
                        Filters.eq("group", getServerGroup())
                )
        ).first();

        return profile;
    }

    public PlayerProfile createPlayerProfile(Player player) {
        return PlayerProfile.fromPlayer(player, getServerGroup());
    }

    public void broadcastProfileUpdate(UUID playerId, String group) {
        // Build message
        PlayerProfileUpdateMessage body = new PlayerProfileUpdateMessage(playerId, group);
        MessageBuilder<PlayerProfileUpdateMessage> builder = new MessageBuilder<>();
        Message<PlayerProfileUpdateMessage> message = builder
                .type(PlayerProfileUpdateMessage.class)
                .channel(MessageChannel.PLAYER_PROFILE_UPDATE)
                .source(getServerId())
                .global()
                .body(body)
                .build();

        // Publish message
        plugin.getBedRock().getRedis().publish(message.getHeader().getChannel(), message);
    }


    public String getServerId() {
        return plugin.getConfig().getString(ConfigKey.DEINSYNC_SERVER_ID);
    }

    public String getServerGroup() {
        return plugin.getConfig().getString(ConfigKey.DEINSYNC_SERVER_GROUP);
    }

}
