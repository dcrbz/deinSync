package bz.dcr.deinsync.db.codec;

import bz.dcr.deinsync.player.data.PlayerInventory;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryCodec implements Codec<PlayerInventory> {

    private Codec<ItemStack> itemStackCodec;


    public PlayerInventoryCodec(Codec<ItemStack> itemStackCodec) {
        this.itemStackCodec = itemStackCodec;
    }


    @Override
    public PlayerInventory decode(BsonReader reader, DecoderContext decoderContext) {
        PlayerInventory inventory = new PlayerInventory();

        reader.readStartDocument();

        // Armor Items
        inventory.setArmorItems(
                readItemStackArray(reader, decoderContext, "armorItems")
        );

        // Main Inventory Items
        inventory.setMainInventoryItems(
                readItemStackArray(reader, decoderContext, "mainInventoryItems")
        );

        // Extra Contents
        inventory.setExtraContents(
                readItemStackArray(reader, decoderContext, "extraContents")
        );

        // Enderchest Contents
        inventory.setEnderChestContents(
                readItemStackArray(reader, decoderContext, "enderChestContents")
        );

        // Off-Hand Item
        inventory.setOffHandItem(
                readItemStack(reader, decoderContext, "offHandItem")
        );

        reader.readEndDocument();

        return inventory;
    }

    @Override
    public void encode(BsonWriter writer, PlayerInventory inventory, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writeItemStackArray(writer, inventory.getArmorItems(), encoderContext, "armorItems");
        writeItemStackArray(writer, inventory.getMainInventoryItems(), encoderContext, "mainInventoryItems");
        writeItemStackArray(writer, inventory.getExtraContents(), encoderContext, "extraContents");
        writeItemStackArray(writer, inventory.getEnderChestContents(), encoderContext, "enderChestContents");
        writeItemStack(writer, inventory.getOffHandItem(), encoderContext, "offHandItem");

        writer.writeEndDocument();
    }


    @Override
    public Class<PlayerInventory> getEncoderClass() {
        return PlayerInventory.class;
    }


    private ItemStack readItemStack(BsonReader reader, DecoderContext decoderContext) {
        return itemStackCodec.decode(reader, decoderContext);
    }

    private ItemStack readItemStack(BsonReader reader, DecoderContext decoderContext, String name) {
        reader.readName(name);
        return readItemStack(reader, decoderContext);
    }

    private ItemStack[] readItemStackArray(BsonReader reader, DecoderContext decoderContext, String name) {
        List<ItemStack> items = new ArrayList<>();

        reader.readName(name);
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            items.add(readItemStack(reader, decoderContext));
        }
        reader.readEndArray();

        return items.toArray(new ItemStack[items.size()]);
    }


    private void writeItemStack(BsonWriter writer, ItemStack item, EncoderContext encoderContext) {
        itemStackCodec.encode(writer, item, encoderContext);
    }

    private void writeItemStack(BsonWriter writer, ItemStack item, EncoderContext encoderContext, String name) {
        writer.writeName(name);
        itemStackCodec.encode(writer, item, encoderContext);
    }

    private void writeItemStackArray(BsonWriter writer, ItemStack[] items, EncoderContext encoderContext, String name) {
        writer.writeName(name);
        writer.writeStartArray();
        for (ItemStack item : items) {
            writeItemStack(writer, item, encoderContext);
        }
        writer.writeEndArray();
    }

}

