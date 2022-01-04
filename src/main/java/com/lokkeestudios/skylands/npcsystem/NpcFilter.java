package com.lokkeestudios.skylands.npcsystem;

import com.lokkeestudios.skylands.core.StringName;

/**
 * An Interface, which marks a class as an {@link NpcFilter}.
 * <p>
 * Marked classes may be used to filter {@link Npc}s.
 * <p>
 * Thus they either have to be a field on the Npc class,
 * or contain Npc field types as records, acting as a way to sort Npcs.
 */
public interface NpcFilter<T extends Enum<T>> extends StringName {
}















