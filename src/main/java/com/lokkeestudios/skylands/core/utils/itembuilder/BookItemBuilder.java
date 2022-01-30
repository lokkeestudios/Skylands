package com.lokkeestudios.skylands.core.utils.itembuilder;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Modifies {@link ItemStack}s that have an ItemMeta type of {@link BookMeta}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class BookItemBuilder extends BaseItemBuilder<BookItemBuilder, BookMeta> {

    /**
     * Constructs a {@link BookItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the BookItemBuilder
     * @param itemMeta  the {@link BookMeta} of the BookItemBuilder
     */
    private BookItemBuilder(
            final @NonNull ItemStack itemStack,
            final @NonNull BookMeta itemMeta
    ) {
        super(itemStack, itemMeta);
    }

    /**
     * Creates a {@link BookItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} to base the builder off of
     * @return the instance of the BookItemBuilder
     */
    public static @NonNull BookItemBuilder of(final @NonNull ItemStack itemStack) throws IllegalArgumentException {
        return new BookItemBuilder(itemStack, castMeta(itemStack.getItemMeta(), BookMeta.class));
    }

    /**
     * Creates a {@link BookItemBuilder}.
     * <p>
     * Alternative method to create a BookItemBuilder.
     *
     * @param material the {@link Material} to base the builder off of
     * @return the instance of the BookItemBuilder
     */
    public static @NonNull BookItemBuilder of(final @NonNull Material material) throws IllegalArgumentException {
        return BookItemBuilder.of(getItem(material));
    }

    /**
     * Adds pages to the book of the {@link ItemStack}.
     *
     * @param pages the pages to add to the book
     * @return the {@link BookItemBuilder}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NonNull BookItemBuilder pages(final @NonNull List<Component> pages) {
        this.itemMeta.pages(pages);
        return this;
    }

    /**
     * Adds pages to the book of the {@link ItemStack}.
     *
     * @param pages the pages to add to the book
     * @return the {@link BookItemBuilder}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NonNull BookItemBuilder pages(final @NonNull Component... pages) {
        this.itemMeta.pages(pages);
        return this;
    }
}
