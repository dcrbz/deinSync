package bz.dcr.deinsync.config;

public interface ConfigKey {

    String MONGODB_URI = "MongoDB.Uri";
    String DEINSYNC_SERVER_ID = "DeinSync.Server-ID";
    String DEINSYNC_SERVER_GROUP = "DeinSync.Server-Group";
    String DEINSYNC_WORKER_THREADS = "DeinSync.Worker-Threads";
    String DEINSYNC_DEBUG = "DeinSync.Debug";
    String DEINSYNC_SECURITY_LOCK_ENABLED = "DeinSync.Security.Lock-Players.Enabled";
    String DEINSYNC_SECURITY_LOCK_DURATION = "DeinSync.Security.Lock-Players.Duration";

}
