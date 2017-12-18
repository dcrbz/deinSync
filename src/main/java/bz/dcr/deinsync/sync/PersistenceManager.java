package bz.dcr.deinsync.sync;

import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.player.PlayerProfile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class PersistenceManager {

    private DeinSyncPlugin plugin;


    public PersistenceManager(DeinSyncPlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Add {@link PlayerProfile} to saving queue
     * @param profile The {@link PlayerProfile} to save
     */
    public void savePlayerProfile(PlayerProfile profile) {
        savePlayerProfileDirectly(profile);
    }

    /**
     * Save {@link PlayerProfile} to database
     * @param profile The {@link PlayerProfile} to save
     */
    private void savePlayerProfileDirectly(PlayerProfile profile) {
        // Save profile
        MongoCollection<PlayerProfile> collection = plugin.getMongo().getMongoDatabase().getCollection(PlayerProfile.COLLECTION_NAME, PlayerProfile.class);

        if(profile.getId() == null) {
            // Create new document
            collection.insertOne(profile);
        } else {
            // Update existing document
            collection.replaceOne(Filters.eq("_id", profile.getId()), profile);
        }

        // Broadcast profile update
        plugin.getSyncManager().broadcastProfileUpdate(profile.getPlayerId(), plugin.getSyncManager().getServerGroup());
    }

}
