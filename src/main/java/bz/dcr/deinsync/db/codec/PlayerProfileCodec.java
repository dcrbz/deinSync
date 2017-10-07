package bz.dcr.deinsync.db.codec;

import bz.dcr.deinsync.player.PlayerProfile;
import bz.dcr.deinsync.player.data.PlayerInventory;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class PlayerProfileCodec implements Codec<PlayerProfile> {

    private Codec<UUID> uuidCodec;
    private Codec<PlayerInventory> playerInventoryCodec;
    private Codec<PotionEffect> potionEffectCodec;


    public PlayerProfileCodec(Codec<UUID> uuidCodec, Codec<PlayerInventory> playerInventoryCodec, Codec<PotionEffect> potionEffectCodec) {
        this.uuidCodec = uuidCodec;
        this.potionEffectCodec = potionEffectCodec;
        this.playerInventoryCodec = playerInventoryCodec;
    }


    @Override
    public PlayerProfile decode(BsonReader reader, DecoderContext decoderContext) {
        PlayerProfile profile = new PlayerProfile();

        reader.readStartDocument();

        profile.setId(reader.readObjectId("_id"));
        profile.setPlayerId(readUuid(reader, decoderContext, "playerId"));
        profile.setGroup(reader.readString("group"));
        profile.setInventory(readPlayerInventory(reader, decoderContext, "inventory"));
        profile.setPotionEffects(readPotionEffectArray(reader, decoderContext, "potionEffects"));
        profile.setHealth(reader.readDouble("health"));
        profile.setHealthScaled(reader.readBoolean("healthScaled"));
        profile.setHealthScale(reader.readDouble("healthScale"));
        profile.setFoodLevel(reader.readInt32("foodLevel"));
        profile.setSaturation(new Float(reader.readDouble("saturation")));
        profile.setExhaustion(new Float(reader.readDouble("exhaustion")));
        profile.setExp(new Float(reader.readDouble("exp")));
        profile.setLevel(reader.readInt32("level"));
        profile.setFlySpeed(new Float(reader.readDouble("flySpeed")));
        profile.setFireTicks(reader.readInt32("fireTicks"));
        profile.setGameMode(GameMode.valueOf(reader.readString("gameMode")));

        reader.readEndDocument();

        return profile;
    }

    @Override
    public void encode(BsonWriter writer, PlayerProfile profile, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writeUuid(writer, profile.getPlayerId(), encoderContext, "playerId");
        writer.writeString("group", profile.getGroup());
        writePlayerInventory(writer, profile.getInventory(), encoderContext, "inventory");
        writePotionEffectArray(writer, profile.getPotionEffects(), encoderContext, "potionEffects");
        writer.writeDouble("health", profile.getHealth());
        writer.writeBoolean("healthScaled", profile.getHealthScaled());
        writer.writeDouble("healthScale", profile.getHealthScale());
        writer.writeInt32("foodLevel", profile.getFoodLevel());
        writer.writeDouble("saturation", profile.getSaturation());
        writer.writeDouble("exhaustion", profile.getExhaustion());
        writer.writeDouble("exp", profile.getExp());
        writer.writeInt32("level", profile.getLevel());
        writer.writeDouble("flySpeed", profile.getFlySpeed());
        writer.writeInt32("fireTicks", profile.getFireTicks());
        writer.writeString("gameMode", profile.getGameMode().toString());

        writer.writeEndDocument();
    }

    @Override
    public Class<PlayerProfile> getEncoderClass() {
        return null;
    }


    private UUID readUuid(BsonReader reader, DecoderContext decoderContext, String name) {
        reader.readName(name);
        return uuidCodec.decode(reader, decoderContext);
    }

    private PlayerInventory readPlayerInventory(BsonReader reader, DecoderContext decoderContext, String name) {
        reader.readName(name);
        return playerInventoryCodec.decode(reader, decoderContext);
    }

    private Collection<PotionEffect> readPotionEffectArray(BsonReader reader, DecoderContext decoderContext, String name) {
        Collection<PotionEffect> potionEffects = new ArrayList<>();

        reader.readName(name);
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            potionEffects.add(potionEffectCodec.decode(reader, decoderContext));
        }
        reader.readEndArray();

        return potionEffects;
    }


    private void writeUuid(BsonWriter writer, UUID uuid, EncoderContext encoderContext, String name) {
        writer.writeName(name);
        uuidCodec.encode(writer, uuid, encoderContext);
    }

    private void writePlayerInventory(BsonWriter writer, PlayerInventory inventory, EncoderContext encoderContext, String name) {
        writer.writeName(name);
        playerInventoryCodec.encode(writer, inventory, encoderContext);
    }

    private void writePotionEffectArray(BsonWriter writer, Collection<PotionEffect> potionEffects, EncoderContext encoderContext, String name) {
        writer.writeName(name);
        writer.writeStartArray();
        for (PotionEffect potionEffect : potionEffects) {
            potionEffectCodec.encode(writer, potionEffect, encoderContext);
        }
        writer.writeEndArray();
    }

}
