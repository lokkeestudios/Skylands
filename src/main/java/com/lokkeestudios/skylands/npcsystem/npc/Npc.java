package com.lokkeestudios.skylands.npcsystem.npc;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The base npc with all common fields and methods.
 * <p>
 * The heart and the core of the entire NpcSystem
 */
public class Npc {

    public static @NonNull Map<Integer, String> entityPlayers = new HashMap<>();

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
     * The skin id of the Npc.
     */
    protected @NonNull String skinId;

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
    protected @NonNull ArmorStand entityPlayer;

    /**
     * Constructs a Npc.
     *
     * @param id       the unique String id of the Npc
     * @param type     the {@link NpcType} of the Npc
     * @param skinId   the skin id of the Npc
     * @param name     the name of the Npc
     * @param title    the title of the Npc
     * @param location the location of the Npc
     */
    public Npc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String skinId,
            final @NonNull String name,
            final @NonNull String title,
            final @NonNull Location location
    ) {
        this.id = id;
        this.type = type;
        this.skinId = skinId;
        this.name = name;
        this.title = title;
        this.location = location;

        entityPlayer = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entityPlayer.setGravity(false);
        entityPlayer.setCanPickupItems(false);
        entityPlayer.setCustomNameVisible(true);
        entityPlayer.setInvulnerable(true);
        entityPlayer.customName(MiniMessage.get().parse(name));

        entityPlayers.put(entityPlayer.getEntityId(), id);
    }

    /**
     * Removes the {@link ArmorStand} entity representing the Npc player.
     */
    public void remove() {
        entityPlayer.remove();
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
     * Gets the skin id of the Npc.
     *
     * @return the Npcs skin id
     */
    public @NonNull String getSkinId() {
        return skinId;
    }

    /**
     * Sets the skin id of the Npc.
     *
     * @param skinId the skin id to be set
     */
    public void setSkinId(final @NonNull String skinId) {
        this.skinId = skinId;
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
        entityPlayer.teleportAsync(location);
        this.location = location;
    }
}