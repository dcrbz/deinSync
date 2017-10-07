package bz.dcr.deinsync.sync;

import bz.dcr.deinsync.DeinSyncPlugin;
import bz.dcr.deinsync.player.PlayerProfile;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PersistenceManager {

    private DeinSyncPlugin plugin;

    private LinkedBlockingQueue<PlayerProfile> profileQueue;
    private Map<Long, Thread> activeWorkers;


    public PersistenceManager(DeinSyncPlugin plugin) {
        this.plugin = plugin;
        this.profileQueue = new LinkedBlockingQueue<>();
        this.activeWorkers = new ConcurrentHashMap<>();
    }


    /**
     * Create a single worker {@link Thread}
     * @return The ID of the created worker {@link Thread}
     */
    public Long addWorker() {
        Thread worker = new Thread(() -> {
            PlayerProfile profile;

            do {
                try {
                    profile = profileQueue.take();

                    // Queue is empty
                    if(profile == null) {
                        continue;
                    }

                    // Save profile to database
                    savePlayerProfileDirectly(profile);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (activeWorkers.containsKey(Thread.currentThread().getId()));
        });
        activeWorkers.put(worker.getId(), worker);
        worker.start();

        return worker.getId();
    }

    /**
     * Create the specified amount of worker threads
     * @param amount The amount of worker threads to create
     */
    public void addWorkers(int amount) {
        for (int i = 0; i < amount; i++) {
            addWorker();
        }
    }

    /**
     * Remove a single worker thread with the specified ID
     * @param workerId The ID of the worker {@link Thread} to remove
     */
    public void removeWorker(Long workerId) {
        activeWorkers.remove(workerId);
    }

    /**
     * Remove a single worker {@link Thread}
     */
    public void removeWorker() {
        for (Long workerId : activeWorkers.keySet()) {
            removeWorker(workerId);
            return;
        }
    }

    /**
     * Remove the specified amount of worker threads
     * @param amount The amount of worker threads to remove
     */
    public void removeWorkers(int amount) {
        for (int i = 0; i < amount; i++) {
            removeWorker();
        }
    }


    /**
     * Add {@link PlayerProfile} to saving queue
     * @param profile The {@link PlayerProfile} to save
     */
    public void savePlayerProfile(PlayerProfile profile) {
        this.profileQueue.offer(profile);
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

    /**
     * Save all remaining {@link PlayerProfile} from the queue
     */
    public void saveAllSynchronously() {
        PlayerProfile profile;

        while ((profile = profileQueue.poll()) != null) {
            savePlayerProfileDirectly(profile);
        }
    }


    /**
     * Save all remaining {@link PlayerProfile} and wait for every worker to finish
     * @throws InterruptedException gets thrown when joining a worker {@link Thread} throws and Exception
     */
    public void close() {
        // Log entry
        plugin.getLogger().info("Saving players...");

        // Get all active workers
        final Set<Map.Entry<Long, Thread>> workers = new HashSet<>(activeWorkers.entrySet());

        // Clear active workers
        activeWorkers.clear();

        // Wait until every worker has finished
        for (Map.Entry<Long, Thread> worker : workers) {
            // Thread has finished/died
            if(!worker.getValue().isAlive()) {
                continue;
            }

            // Join worker thread
            try {
                worker.getValue().join(3000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                plugin.getLogger().warning("Failed to join worker thread #" + worker.getKey().toString() + "!");
            }
        }

        // Save all remaining profiles synchronously
        saveAllSynchronously();
    }

}
