package com.lokkeestudios.skylands.npcsystem;

import net.minecraft.world.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for all existing {@link Npc}s.
 */
public final class NpcRegistry {

    /**
     * The {@link Map} holding the {@link Npc}s of the registry.
     * <p>
     * Key - The unique String id of the Npc.
     * <p>
     * Value - The instance of the Npc.
     */
    private final @NonNull Map<String, Npc> npcs = new LinkedHashMap<>();

    /**
     * The {@link Map} holding the npcs related to their entities.
     * <p>
     * Key - The entity id of the {@link LivingEntity}.
     * <p>
     * Value - The instance of the Npc.
     */
    private final @NonNull Map<Integer, Npc> entities = new LinkedHashMap<>();

    /**
     * Registers a {@link Npc} by adding it to the registry.
     *
     * @param npc the Npc which is to be registered
     */
    public void registerNpc(final @NonNull Npc npc) {
        npcs.put(npc.getId(), npc);
    }

    /**
     * Registers an entity and its related {@link Npc} by adding it to the registry.
     *
     * @param entityId the entity id of the Npc which is to be registered
     * @param npc      the Npc which is to be registered
     */
    public void registerNpcEntity(final int entityId, final @NonNull Npc npc) {
        entities.put(entityId, npc);
    }

    /**
     * Unregisters a {@link Npc} by removing it from the registry.
     *
     * @param id the id of the Npc which is to be unregistered
     */
    public void unregisterNpc(final @NonNull String id) {
        npcs.remove(id);
    }

    /**
     * Unregisters a {@link Npc} by removing it from the registry.
     *
     * @param entityId the id of the Npc which is to be unregistered
     */
    public void unregisterNpcEntity(final int entityId) {
        entities.remove(entityId);
    }

    /**
     * Gets the entry of an id and its {@link Npc}.
     *
     * @param id the id of the Npc which is wanted
     * @return the Npc associated to the id
     */
    public @NonNull Npc getNpcFromId(final @NonNull String id) {
        return npcs.get(id);
    }

    /**
     * Gets the {@link Npc} related to an entity id.
     *
     * @param entityId the entity id of the Npc which is wanted
     * @return the Npc associated to the id
     */
    public @NonNull Npc getNpcFromEntityId(final int entityId) {
        return entities.get(entityId);
    }

    /**
     * Gets all registered {@link Npc}s.
     *
     * @return a {@link List} of all registered Npcs
     */
    public @NonNull ArrayList<Npc> getNpcs() {
        return new ArrayList<>(npcs.values());
    }

    /**
     * Gets all registry keys.
     *
     * @return a {@link List} of all registry keys
     */
    public @NonNull ArrayList<String> getIds() {
        return new ArrayList<>(npcs.keySet());
    }

    /**
     * Gets all entity id registry keys.
     *
     * @return a {@link List} of all entity id registry keys
     */
    public @NonNull ArrayList<Integer> getEntityIds() {
        return new ArrayList<>(entities.keySet());
    }

    /**
     * Checks whether a String id is a valid registry key.
     *
     * @param id the id for which is to be checked
     * @return whether the id is a valid key in the registry
     */
    public @NonNull Boolean isIdValid(final @NonNull String id) {
        return getIds().contains(id);
    }

    /**
     * Checks whether an entity id corresponds to a {@link Npc}.
     *
     * @param entityId the entity id for which is to be checked
     * @return whether the entity id is related
     */
    public @NonNull Boolean isEntityNpc(final int entityId) {
        return getEntityIds().contains(entityId);
    }
}
