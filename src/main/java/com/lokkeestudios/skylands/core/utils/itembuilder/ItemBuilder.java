package com.lokkeestudios.skylands.core.utils.itembuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * The main item builder.
 * <p>
 * Modifies {@link ItemStack}s that have an ItemMeta type of {@link ItemMeta}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class ItemBuilder extends BaseItemBuilder<ItemBuilder, ItemMeta> {

    /**
     * Constructs an {@link ItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the ItemBuilder
     * @param itemMeta  the {@link ItemMeta} of the ItemBuilder
     */
    private ItemBuilder(
            final @NonNull ItemStack itemStack,
            final @Nullable ItemMeta itemMeta
    ) {
        super(itemStack, itemMeta != null
                ? itemMeta
                : Objects.requireNonNull(Bukkit.getItemFactory().getItemMeta(itemStack.getType()))
        );
    }

    /**
     * Creates an {@link ItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} to base the builder off of
     * @return the instance of the ItemBuilder
     */
    public static @NonNull ItemBuilder of(
            final @NonNull ItemStack itemStack
    ) {
        return new ItemBuilder(itemStack, itemStack.getItemMeta());
    }

    /**
     * Creates an {@link ItemBuilder}.
     * <p>
     * Alternative method to create an ItemBuilder.
     *
     * @param material the {@link Material} to base the builder off of
     * @return the instance of the ItemBuilder
     */
    public static @NonNull ItemBuilder of(
            final @NonNull Material material
    ) throws IllegalArgumentException {
        return ItemBuilder.of(getItem(material));
    }
}
