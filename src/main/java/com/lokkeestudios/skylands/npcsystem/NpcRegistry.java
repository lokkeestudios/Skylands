package com.lokkeestudios.skylands.npcsystem;

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
     * Registers an {@link Npc} by adding it to the registry.
     *
     * @param npc the Npc which is to be registered
     */
    public void registerNpc(final @NonNull Npc npc) {
        npcs.put(npc.getId(), npc);
    }

    /**
     * Unregisters an {@link Npc} by removing it from the registry.
     *
     * @param id the id of the Npc which is to be unregistered
     */
    public void unregisterNpc(final @NonNull String id) {
        npcs.remove(id);
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
     * Checks whether a String id is a valid registry key.
     *
     * @param id the id for which is to be checked
     * @return whether the id is a valid key in the registry
     */
    public @NonNull Boolean isIdValid(final @NonNull String id) {
        return getIds().contains(id);
    }
}
