package com.lokkeestudios.skylands.npcsystem;

import com.lokkeestudios.skylands.core.utils.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The base npc with all common fields and methods.
 * <p>
 * The heart and the core of the entire NpcSystem
 */
public class Npc {

    public static @NonNull Map<Integer, Npc> entityPlayers = new HashMap<>();

    /**
     * The unique String id of the Npc.
     * <p>
     * This Npc field is final and thus cannot be changed.
     */
    protected final @NonNull String id;

    /**
     * The {@link NpcType} of the Npc.
     * <p>
     * This Item field is final and thus cannot be changed.
     */
    protected final @NonNull NpcType type;

    /**
     * The texture value of the Npc skin.
     */
    protected @NonNull String textureValue;

    /**
     * The texture signature of the Npc skin.
     */
    protected @NonNull String textureSignature;

    /**
     * The name of the Npc.
     */
    protected @NonNull String name;

    /**
     * The title of the Npc.
     */
    protected @NonNull String title;

    /**
     * The location of the Npc.
     */
    protected @NonNull Location location;

    /**
     * The entity representing the Npc
     */
    protected ArmorStand entityPlayer;

    /**
     * The entity hologram displaying the player name
     */
    protected ArmorStand entityDisplayName;

    /**
     * The entity hologram displaying the player title
     */
    protected ArmorStand entityDisplayTitle;

    /**
     * Constructs a Npc.
     *
     * @param id               the unique String id of the Npc
     * @param type             the {@link NpcType} of the Npc
     * @param textureValue     the texture value of the Npc skin
     * @param textureSignature the texture signature of the Npc skin
     * @param name             the name of the Npc
     * @param title            the title of the Npc
     * @param location         the location of the Npc
     */
    public Npc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String textureValue,
            final @NonNull String textureSignature,
            final @NonNull String name,
            final @NonNull String title,
            final @NonNull Location location
    ) {
        this.id = id;
        this.type = type;
        this.textureValue = textureValue;
        this.textureSignature = textureSignature;
        this.name = name;
        this.title = title;
        this.location = location;

        spawn();
    }

    /**
     * Spawns the {@link ArmorStand} entity representing the Npc player.
     */
    public void spawn() {
        final @NonNull Consumer<ArmorStand> function = entity -> entityPlayers.put(entity.getEntityId(), this);

        entityPlayer = location.getWorld().spawn(location, ArmorStand.class, false, function);

        entityPlayer.setGravity(false);
        entityPlayer.setCanPickupItems(false);
        entityPlayer.setInvulnerable(true);
        entityPlayer.setCustomNameVisible(false);

        final @NonNull Location displayTitleLocation = location.clone().add(0, -0.2, 0);

        entityDisplayTitle = (ArmorStand) location.getWorld().spawnEntity(displayTitleLocation, EntityType.ARMOR_STAND);

        entityDisplayTitle.setGravity(false);
        entityDisplayTitle.setCanPickupItems(false);
        entityDisplayTitle.setInvulnerable(true);
        entityDisplayTitle.setVisible(false);
        entityDisplayTitle.setCustomNameVisible(true);
        entityDisplayTitle.customName(Component.text(title).color(Constants.Text.COLOR_DEFAULT));

        final @NonNull Location displayNameLocation = location.clone().add(0, 0.1, 0);

        entityDisplayName = (ArmorStand) location.getWorld().spawnEntity(displayNameLocation, EntityType.ARMOR_STAND);

        entityDisplayName.setGravity(false);
        entityDisplayName.setCanPickupItems(false);
        entityDisplayName.setInvulnerable(true);
        entityDisplayName.setVisible(false);
        entityDisplayName.setCustomNameVisible(true);
        entityDisplayName.customName(MiniMessage.get().parse(name));
    }

    /**
     * Removes the {@link ArmorStand} entity representing the Npc player.
     */
    public void remove() {
        entityPlayers.remove(entityPlayer.getEntityId());
        entityPlayer.remove();
        entityDisplayTitle.remove();
        entityDisplayName.remove();
    }

    /**
     * Gets the unique String id of the Npc.
     *
     * @return the Npcs id
     */
    public @NonNull String getId() {
        return id;
    }

    /**
     * Gets the {@link NpcType} of the Npc.
     *
     * @return the Npcs NpcType
     */
    public @NonNull NpcType getType() {
        return type;
    }

    /**
     * Gets the texture value of the Npc skin.
     *
     * @return the Npcs texture value
     */
    public @NonNull String getTextureValue() {
        return textureValue;
    }

    /**
     * Sets the texture value of the Npc skin.
     *
     * @param textureValue the texture value to be set
     */
    public void setTextureValue(final @NonNull String textureValue) {
        this.textureValue = textureValue;
    }

    /**
     * Gets the texture signature of the Npc skin.
     *
     * @return the Npcs texture signature
     */
    public @NonNull String getTextureSignature() {
        return textureSignature;
    }

    /**
     * Sets the texture signature of the Npc skin.
     *
     * @param textureSignature the texture signature to be set
     */
    public void setTextureSignature(final @NonNull String textureSignature) {
        this.textureSignature = textureSignature;
    }

    /**
     * Gets the name of the Npc.
     *
     * @return the Npcs name
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Sets the name of the Npc.
     *
     * @param name the name to be set
     */
    public void setName(final @NonNull String name) {
        this.entityDisplayName.customName(MiniMessage.get().parse(name));
        this.name = name;
    }

    /**
     * Gets the title of the Npc.
     *
     * @return the Npcs title
     */
    public @NonNull String getTitle() {
        return title;
    }

    /**
     * Sets the title of the Npc.
     *
     * @param title the title to be set
     */
    public void setTitle(final @NonNull String title) {
        this.entityDisplayTitle.customName(Component.text(title).color(Constants.Text.COLOR_DEFAULT));
        this.title = title;
    }

    /**
     * Gets the {@link Location} of the Npc.
     *
     * @return the Npcs location
     */
    public @NonNull Location getLocation() {
        return location;
    }

    /**
     * Sets the {@link Location} of the Npc.
     *
     * @param location the location to be set
     */
    public void setLocation(final @NonNull Location location) {
        final @NonNull Location displayLocation = location.clone().add(0, 0.1, 0);

        entityPlayer.teleportAsync(location);
        entityDisplayTitle.teleportAsync(displayLocation);
        this.location = location;
    }
}