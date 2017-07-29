package bz.dcr.deinsync.player;

import bz.dcr.deinsync.player.data.PlayerInventory;
import org.bson.types.ObjectId;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PlayerProfile {

    public static final String COLLECTION_NAME = "PlayerProfile";

    private ObjectId id;

    private UUID playerId;
    private String group;

    private PlayerInventory inventory;
    private Collection<PotionEffect> potionEffects;
    private Double health;
    private Boolean healthScaled;
    private Double healthScale;
    private Integer foodLevel;
    private Float saturation;
    private Float exhaustion;
    private Float exp;
    private Integer level;
    private Float flySpeed;
    private Integer fireTicks;
    private GameMode gameMode;


    public PlayerProfile() {
        potionEffects = new HashSet<>();
    }


    //region Getters and setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void setInventory(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(Collection<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public Double getHealth() {
        return health;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public Boolean getHealthScaled() {
        return healthScaled;
    }

    public void setHealthScaled(Boolean healthScaled) {
        this.healthScaled = healthScaled;
    }

    public Double getHealthScale() {
        return healthScale;
    }

    public void setHealthScale(Double healthScale) {
        this.healthScale = healthScale;
    }

    public Integer getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(Integer foodLevel) {
        this.foodLevel = foodLevel;
    }

    public Float getSaturation() {
        return saturation;
    }

    public void setSaturation(Float saturation) {
        this.saturation = saturation;
    }

    public Float getExhaustion() {
        return exhaustion;
    }

    public void setExhaustion(Float exhaustion) {
        this.exhaustion = exhaustion;
    }

    public Float getExp() {
        return exp;
    }

    public void setExp(Float exp) {
        this.exp = exp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Float getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(Float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public Integer getFireTicks() {
        return fireTicks;
    }

    public void setFireTicks(Integer fireTicks) {
        this.fireTicks = fireTicks;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    //endregion


    public void apply(Player player) {
        inventory.apply(player);

        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.addPotionEffects(getPotionEffects());

        player.setHealth(getHealth());

        player.setHealthScaled(getHealthScaled());

        player.setHealthScale(getHealthScale());

        player.setFoodLevel(getFoodLevel());

        player.setSaturation(getSaturation());

        player.setExhaustion(getExhaustion());

        player.setLevel(getLevel());

        player.setExp(getExp());

        player.setFlySpeed(getFlySpeed());

        player.setFireTicks(getFireTicks());

        player.setGameMode(getGameMode());

        player.updateInventory();
    }


    public static PlayerProfile update(Player player, String group, PlayerProfile profile) {
        profile.setPlayerId(player.getUniqueId());
        profile.setGroup(group);
        profile.setInventory(
                PlayerInventory.fromPlayer(player)
        );
        profile.setPotionEffects(player.getActivePotionEffects());
        profile.setHealth(player.getHealth());
        profile.setHealthScaled(player.isHealthScaled());
        profile.setHealthScale(player.getHealthScale());
        profile.setFoodLevel(player.getFoodLevel());
        profile.setSaturation(player.getSaturation());
        profile.setExhaustion(player.getExhaustion());
        profile.setExp(player.getExp());
        profile.setLevel(player.getLevel());
        profile.setFlySpeed(player.getFlySpeed());
        profile.setFireTicks(player.getFireTicks());
        profile.setGameMode(player.getGameMode());

        return profile;
    }

    public static PlayerProfile fromPlayer(Player player, String group) {
        return PlayerProfile.update(player, group, new PlayerProfile());
    }

}
