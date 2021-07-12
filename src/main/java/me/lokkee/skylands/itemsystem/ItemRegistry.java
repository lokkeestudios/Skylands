package me.lokkee.skylands.itemsystem;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for all existing {@link Item}s.
 */
public final class ItemRegistry {

    /**
     * The {@link Map} holding the {@link Item}s of the registry.
     * <p>
     * Key - The unique String id of the Item.
     * <p>
     * Value - The instance of the Item.
     */
    private final @NonNull Map<String, Item> items = new LinkedHashMap<>();

    /**
     * Registers an {@link Item} by adding it to the registry.
     *
     * @param item the Item which is to be registered
     */
    public void registerItem(final @NonNull Item item) {
        items.put(item.getId(), item);
    }

    /**
     * Unregisters an {@link Item} by removing it from the registry.
     *
     * @param id the id of the Item which is to be unregistered
     */
    public void unregisterItem(final @NonNull String id) {
        items.remove(id);
    }

    /**
     * Gets the entry of an id and its {@link Item}.
     *
     * @param id the id of the Item which is wanted
     * @return the Item associated to the id
     */
    public @NonNull Item getItemFromId(final @NonNull String id) {
        return items.get(id);
    }

    /**
     * Gets all registered {@link Item}s.
     *
     * @return a {@link List} of all registered Items
     */
    public @NonNull ArrayList<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Gets all registry keys.
     *
     * @return a {@link List} of all registry keys
     */
    public @NonNull ArrayList<String> getIds() {
        return new ArrayList<>(items.keySet());
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
