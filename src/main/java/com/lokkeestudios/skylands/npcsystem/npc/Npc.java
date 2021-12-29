package com.lokkeestudios.skylands.npcsystem.npc;

import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The base npc with all common fields and methods.
 * <p>
 * The heart and the core of the entire NpcSystem
 */
public class Npc {
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
     * The location of the Npc.
     */
    protected @NonNull Location location;

    /**
     * Constructs a Npc.
     *
     * @param id       the unique String id of the Npc
     * @param type     the {@link NpcType} of the Npc
     * @param skinId   the skin id of the Npc
     * @param name     the name of the Npc
     * @param location the location of the Npc
     */
    public Npc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String skinId,
            final @NonNull String name,
            final @NonNull Location location
    ) {
        this.id = id;
        this.type = type;
        this.skinId = skinId;
        this.name = name;
        this.location = location;
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
        this.location = location;
    }
}
