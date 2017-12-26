package bz.dcr.deinsync.sync;

import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.player.PlayerProfile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class PersistenceManager {

    private DeinSyncPlugin plugin;

    private MongoCollection<PlayerProfile> playerProfileCollection;


    public PersistenceManager(DeinSyncPlugin plugin) {
        this.plugin = plugin;

        playerProfileCollection = plugin.getMongo().getMongoDatabase()
                .getCollection(PlayerProfile.COLLECTION_NAME, PlayerProfile.class);
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
        if (profile.getId() == null) {
            // Create new document
            playerProfileCollection.insertOne(profile);
        } else {
            // Update existing document
            playerProfileCollection.replaceOne(Filters.eq("_id", profile.getId()), profile);
        }
    }

}
