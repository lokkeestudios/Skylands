package com.lokkeestudios.skylands.itemsystem;

import com.lokkeestudios.skylands.core.StringName;

/**
 * An Interface, which marks a class as an {@link ItemFilter}.
 * <p>
 * Marked classes may be used to filter {@link Item}s.
 * <p>
 * Thus they either have to be a field on the Item class,
 * or contain Item field types as records, acting as a way to sort Items.
 */
public interface ItemFilter<T extends Enum<T>> extends StringName {
}
