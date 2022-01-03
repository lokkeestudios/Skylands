package com.lokkeestudios.skylands.npcsystem.npc;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Holds all the npc types.
 */
public enum NpcType implements NpcFilter<NpcType> {

    /**
     * The quest giver npc type.
     */

    QUEST_GIVER("Quest Giver"),

    /**
     * The merchant npc type.
     */
    MERCHANT("Merchant");

    /**
     * The String name of the npc type.
     */
    private final @NonNull String name;

    /**
     * Constructs a {@link NpcType}.
     *
     * @param name the name of the npc type
     */
    NpcType(final @NonNull String name) {
        this.name = name;
    }

    /**
     * Gets the String name of the npc type.
     *
     * @return the name of the npc type
     */
    public @NonNull String getName() {
        return name;
    }
}
