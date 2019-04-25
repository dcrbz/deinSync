package bz.dcr.deinsync.db.codec;

import bz.dcr.deinsync.player.PlayerProfile;
import bz.dcr.deinsync.player.data.PlayerInventory;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class PlayerProfileCodecProvider implements CodecProvider {

    private Codec<UUID> uuidCodec;
    private Codec<PlayerInventory> playerInventoryCodec;
    private Codec<PotionEffect> potionEffectCodec;


    public PlayerProfileCodecProvider(Codec<UUID> uuidCodec, Codec<PlayerInventory> playerInventoryCodec, Codec<PotionEffect> potionEffectCodec) {
        this.uuidCodec = uuidCodec;
        this.playerInventoryCodec = playerInventoryCodec;
        this.potionEffectCodec = potionEffectCodec;
    }


    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == PlayerProfile.class) {
            return (Codec<T>) new PlayerProfileCodec(uuidCodec, playerInventoryCodec, potionEffectCodec);
        }

        return null;
    }

}
