package bz.dcr.deinsync.player.data;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInventory {

    private ItemStack[] armorItems;
    private ItemStack[] mainInventoryItems;
    private ItemStack[] extraContents;
    private ItemStack[] enderChestContents;
    private ItemStack offHandItem;


    //region Constructors
    public PlayerInventory() {
    }

    public PlayerInventory(ItemStack[] armorItems, ItemStack[] mainInventoryItems, ItemStack[] extraContents, ItemStack[] enderChestContents, ItemStack offHandItem) {
        this();
        this.armorItems = armorItems;
        this.mainInventoryItems = mainInventoryItems;
        this.extraContents = extraContents;
        this.enderChestContents = enderChestContents;
        this.offHandItem = offHandItem;
    }
    //endregion


    //region Getters and setters
    public ItemStack[] getArmorItems() {
        return armorItems;
    }

    public void setArmorItems(ItemStack[] armorItems) {
        this.armorItems = armorItems;
    }

    public ItemStack[] getMainInventoryItems() {
        return mainInventoryItems;
    }

    public void setMainInventoryItems(ItemStack[] mainInventoryItems) {
        this.mainInventoryItems = mainInventoryItems;
    }

    public ItemStack[] getExtraContents() {
        return extraContents;
    }

    public void setExtraContents(ItemStack[] extraContents) {
        this.extraContents = extraContents;
    }

    public ItemStack[] getEnderChestContents() {
        return enderChestContents;
    }

    public void setEnderChestContents(ItemStack[] enderChestContents) {
        this.enderChestContents = enderChestContents;
    }

    public ItemStack getOffHandItem() {
        return offHandItem;
    }

    public void setOffHandItem(ItemStack offHandItem) {
        this.offHandItem = offHandItem;
    }
    //endregion


    public void apply(Player player) {
        player.getInventory().setArmorContents(armorItems);
        player.getInventory().setContents(mainInventoryItems);
        player.getInventory().setExtraContents(extraContents);
        player.getEnderChest().setContents(enderChestContents);
        player.getInventory().setItemInOffHand(offHandItem);
    }


    public static PlayerInventory fromPlayer(Player player) {
        return new PlayerInventory(
                player.getInventory().getArmorContents(),
                player.getInventory().getContents(),
                player.getInventory().getExtraContents(),
                player.getEnderChest().getContents(),
                player.getInventory().getItemInOffHand()
        );
    }

}
