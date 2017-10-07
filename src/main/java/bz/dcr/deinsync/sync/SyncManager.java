package bz.dcr.deinsync.sync;

import bz.dcr.bedrock.common.pubsub.BedRockSubscriber;
import bz.dcr.bedrock.common.pubsub.Message;
import bz.dcr.bedrock.common.pubsub.MessageBuilder;
import bz.dcr.bedrock.common.pubsub.messages.BungeeCordJoinEvent;
import bz.dcr.bedrock.common.pubsub.messages.ServerJoinEvent;
import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.commons.message.MessageChannel;
import bz.dcr.deinsync.commons.message.PlayerProfileUpdateMessage;
import bz.dcr.deinsync.config.ConfigKey;
import bz.dcr.deinsync.player.PlayerProfile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SyncManager {

    private DeinSyncPlugin plugin;

    private Set<UUID> pendingLogins;


    public SyncManager(DeinSyncPlugin plugin) {
        this.plugin = plugin;
        this.pendingLogins = new HashSet<>();
    }


    public void initSubscribers() {
        // Subscribe to player profile updates
        plugin.getBedRock().getRedis().subscribe(new BedRockSubscriber<PlayerProfileUpdateMessage>() {
            @Override
            public void onMessage(Message<PlayerProfileUpdateMessage> message) {
                plugin.getLogger().info("Received PlayerProfileUpdateMessage");

                // Wrong server group
                if(!message.getBody().getGroup().equals(getServerGroup())) {
                    plugin.getLogger().info("Received profile update but group was invalid (" + message.getBody().getGroup() + ").");
                    return;
                }

                // Run synchronously
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Get player
                    final Player player = Bukkit.getPlayer(message.getBody().getPlayerId());

                    // Player not online
                    if(player == null) {
                        plugin.getLogger().info("The player " + message.getBody().getPlayerId().toString() + " is currently not online.");
                        return;
                    }

                    // Load profile and apply to player
                    loadPlayer(player);

                    plugin.getLogger().info("Successfully loaded data of " + message.getBody().getPlayerId().toString() + "!");
                });
            }
        }, MessageChannel.PLAYER_PROFILE_UPDATE);

        // Subscribe to BungeeCord join event
        plugin.getBedRock().getRedis().subscribe(new BedRockSubscriber<BungeeCordJoinEvent>() {
            @Override
            public void onMessage(Message<BungeeCordJoinEvent> message) {
                plugin.getLogger().info("Player " + message.getBody().getPlayerId().toString() + " joined Proxy!");
                pendingLogins.add(message.getBody().getPlayerId());
            }
        }, bz.dcr.bedrock.common.pubsub.MessageChannel.BUNGEECORD_JOIN_QUIT);

        // Subscribe to server join event
        plugin.getBedRock().getRedis().subscribe(new BedRockSubscriber<ServerJoinEvent>() {
            @Override
            public void onMessage(Message<ServerJoinEvent> message) {
                plugin.getLogger().info("Player " + message.getBody().getPlayerId().toString() + " joined server!");

                // Player joined on other server
                if(Bukkit.getPlayer(message.getBody().getPlayerId()) == null) {
                    pendingLogins.remove(message.getBody().getPlayerId());
                }
            }
        }, bz.dcr.bedrock.common.pubsub.MessageChannel.SERVER_JOIN_QUIT);
    }


    public void savePlayer(Player player) {
        PlayerProfile profile = fetchPlayerProfile(player.getUniqueId());

        if(profile != null) {
            // Update existing profile
            plugin.getLogger().info("Updating existing profile of " + player.getUniqueId().toString() + "...");
            profile = PlayerProfile.update(player, getServerGroup(), profile);
        } else {
            // Create new profile
            plugin.getLogger().info("Creating new profile for " + player.getUniqueId().toString() + "...");
            profile = createPlayerProfile(player);
        }

        // Save profile
        plugin.getPersistenceManager().savePlayerProfile(profile);
    }

    public void loadPlayer(Player player) {
        final PlayerProfile profile = fetchOrCreatePlayerProfile(player);
        profile.apply(player);
    }


    public PlayerProfile fetchOrCreatePlayerProfile(Player player) {
        PlayerProfile profile = fetchPlayerProfile(player.getUniqueId());

        if(profile != null) {
            plugin.getLogger().info("Found player profile of " + player.getUniqueId().toString() + ".");
        } else {
            plugin.getLogger().info("Creating new profile for " + player.getUniqueId().toString() + "...");
            profile = createPlayerProfile(player);
        }

        return profile;
    }

    public PlayerProfile fetchPlayerProfile(UUID playerId) {
        final MongoCollection<PlayerProfile> collection = plugin.getMongo().getMongoDatabase().getCollection(PlayerProfile.COLLECTION_NAME, PlayerProfile.class);

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

    public boolean checkAndDeletePendingLogin(UUID uuid) {
        return pendingLogins.remove(uuid);
    }


    public String getServerId() {
        return plugin.getConfig().getString(ConfigKey.DEINSYNC_SERVER_ID);
    }

    public String getServerGroup() {
        return plugin.getConfig().getString(ConfigKey.DEINSYNC_SERVER_GROUP);
    }

    public Set<UUID> getPendingLogins() {
        return pendingLogins;
    }

}
