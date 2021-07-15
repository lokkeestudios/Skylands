package com.lokkeestudios.skylands.core.utils;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Base64;

/**
 * Serializes an {@link ItemStack} to a {@link Base64}
 * String or deserializes it back.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class ItemSerializer {

    /**
     * Serializes an {@link ItemStack} to a {@link Base64} String.
     *
     * @param itemStack the ItemStack to be encoded to a Base64 String
     * @return a Base64 string of the provided ItemStack
     */
    public static @NonNull String ItemStackToBase64(final @NonNull ItemStack itemStack) {
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    /**
     * Deserializes a {@link Base64} String to an {@link ItemStack}.
     *
     * @param data the Base64 string to be decoded to an ItemStack
     * @return the ItemStack created from the Base64 String
     */
    public static @NonNull ItemStack ItemStackFromBase64(final @NonNull String data) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(data));
    }
}
