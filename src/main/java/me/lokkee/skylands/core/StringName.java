package me.lokkee.skylands.core;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An Interface which forces Classes to implement
 * a getName() method, which returns a String name.
 */
public interface StringName {

    /**
     * Gets the String name.
     *
     * @return the String name
     */
    @NonNull String getName();
}
