package bz.dcr.deinsync.db.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInventoryCodecProvider implements CodecProvider {

    private Codec<ItemStack> itemStackCodec;


    public PlayerInventoryCodecProvider(Codec<ItemStack> itemStackCodec) {
        this.itemStackCodec = itemStackCodec;
    }


    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if(clazz == PlayerInventory.class) {
            return (Codec<T>) new PlayerInventoryCodec(itemStackCodec);
        }

        return null;
    }

}
