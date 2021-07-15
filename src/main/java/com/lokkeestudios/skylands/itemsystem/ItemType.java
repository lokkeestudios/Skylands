package com.lokkeestudios.skylands.itemsystem;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Holds all the item types.
 */
public enum ItemType implements ItemFilter<ItemType> {

    /**
     * The melee weapon item type.
     */
    MELEE_WEAPON("Melee Weapon"),

    /**
     * The ranged weapon item type.
     */
    RANGED_WEAPON("Ranged Weapon"),

    /**
     * The magic weapon item type.
     */
    MAGIC_WEAPON("Magic Weapon"),

    /**
     * The helmet item type.
     */
    HELMET("Helmet"),

    /**
     * The chestplate item type.
     */
    CHESTPLATE("Chestplate"),

    /**
     * The leggings item type.
     */
    LEGGINGS("Leggings"),

    /**
     * The boots item type.
     */
    BOOTS("Boots");

    /**
     * The String name of the item type.
     */
    private final @NonNull String name;

    /**
     * Constructs an {@link ItemType}.
     *
     * @param name the name of the item type
     */
    ItemType(final @NonNull String name) {
        this.name = name;
    }

    /**
     * Gets the String name of the item type.
     *
     * @return the name of the item type
     */
    public @NonNull String getName() {
        return name;
    }
}
