package bz.dcr.deinsync;

import bz.dcr.bedrock.spigot.BedRockPlugin;
import bz.dcr.dccore.commons.db.Mongo;
import bz.dcr.dccore.commons.db.codec.UUIDCodec;
import bz.dcr.dccore.commons.db.codec.UUIDCodecProvider;
import bz.dcr.dccore.db.codec.ItemStackCodec;
import bz.dcr.dccore.db.codec.ItemStackCodecProvider;
import bz.dcr.dccore.db.codec.PotionEffectCodec;
import bz.dcr.dccore.db.codec.PotionEffectCodecProvider;
import bz.dcr.deinsync.cmd.DeinSyncCommand;
import bz.dcr.deinsync.config.ConfigKey;
import bz.dcr.deinsync.db.codec.PlayerInventoryCodec;
import bz.dcr.deinsync.db.codec.PlayerInventoryCodecProvider;
import bz.dcr.deinsync.db.codec.PlayerProfileCodecProvider;
import bz.dcr.deinsync.listener.JoinQuitListener;
import bz.dcr.deinsync.listener.LockListener;
import bz.dcr.deinsync.listener.packet.*;
import bz.dcr.deinsync.logging.LogManager;
import bz.dcr.deinsync.sync.PersistenceManager;
import bz.dcr.deinsync.sync.SyncManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeinSyncPlugin extends JavaPlugin {

    public static final String LOCK_PLAYER_TAG = "deinsync_locked";

    private ExecutorService executorService;

    private LogManager logManager;

    private Mongo mongoDB;
    private SyncManager syncManager;
    private PersistenceManager persistenceManager;
    private BedRockPlugin bedRock;

    private ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        executorService = Executors.newCachedThreadPool();

        logManager = new LogManager(this);

        loadBedRock();
        loadConfig();
        setupDatabase();
        setupPacketManager();

        syncManager = new SyncManager(this);
        persistenceManager = new PersistenceManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);

        // Register lock listener
        if (getConfig().getBoolean(ConfigKey.DEINSYNC_SECURITY_LOCK_ENABLED)) {
            getServer().getPluginManager().registerEvents(new LockListener(this), this);
        }

        // Register deinSync command
        getCommand("deinsync").setExecutor(new DeinSyncCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving players...");

        // Save profiles of all remaining players
        Bukkit.getOnlinePlayers().forEach(player -> getSyncManager().savePlayer(player));

        getLogger().info("Successfully saved players.");

        // Disconnect from database
        if(mongoDB != null) {
            mongoDB.disconnect();
        }
    }


    private void loadBedRock() {
        final Plugin bedRockPlugin = getServer().getPluginManager().getPlugin("bedRock");

        // bedRock is not installed
        if(bedRockPlugin == null) {
            getLogManager().warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            getLogManager().warning("! Could not find bedRock! Disabling deinSync... !");
            getLogManager().warning("! INVENTORIES WILL NOT BE SYNCHRONIZED          !");
            getLogManager().warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        bedRock = (BedRockPlugin) bedRockPlugin;
    }

    private void loadConfig() {
        getConfig().addDefault(ConfigKey.DEINSYNC_SERVER_ID, "server_" + getServer().getPort());
        getConfig().addDefault(ConfigKey.DEINSYNC_SERVER_GROUP, "main");
        getConfig().addDefault(ConfigKey.MONGODB_URI, "mongodb://127.0.0.1:27017/" + getName().toLowerCase());
        getConfig().addDefault(ConfigKey.DEINSYNC_SECURITY_LOCK_ENABLED, true);
        getConfig().addDefault(ConfigKey.DEINSYNC_SECURITY_LOCK_DURATION, 40);
        getConfig().addDefault(ConfigKey.DEINSYNC_DEBUG, false);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void setupDatabase() {
        final CodecRegistry customRegistry = CodecRegistries.fromProviders(
                new UUIDCodecProvider(),
                new ItemStackCodecProvider(),
                new PotionEffectCodecProvider(),
                new PlayerInventoryCodecProvider(new ItemStackCodec()),
                new PlayerProfileCodecProvider(new UUIDCodec(), new PlayerInventoryCodec(new ItemStackCodec()), new PotionEffectCodec())
        );
        final CodecRegistry registry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), customRegistry);

        final MongoClientURI uri = new MongoClientURI(
                getConfig().getString(ConfigKey.MONGODB_URI),
                MongoClientOptions.builder().codecRegistry(registry)
        );

        try {
            mongoDB = new Mongo(uri, uri.getDatabase());
            mongoDB.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
            getLogManager().warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            getLogManager().warning("! Could not connect to MongoDB! Disabling deinSync... !");
            getLogManager().warning("! INVENTORIES WILL NOT BE SYNCHRONIZED                !");
            getLogManager().warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupPacketManager() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new EntityEffectPacketListener(this));
        protocolManager.addPacketListener(new ExperiencePacketListener(this));
        protocolManager.addPacketListener(new HealthPacketListener(this));
        protocolManager.addPacketListener(new InventoryPacketListener(this));
        protocolManager.addPacketListener(new GameModePacketListener(this));
    }


    public ExecutorService getExecutorService() {
        return executorService;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public BedRockPlugin getBedRock() {
        return bedRock;
    }

    public Mongo getMongo() {
        return mongoDB;
    }

    public SyncManager getSyncManager() {
        return syncManager;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

}
