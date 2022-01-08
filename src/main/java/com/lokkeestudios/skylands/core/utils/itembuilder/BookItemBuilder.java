package com.lokkeestudios.skylands.core.utils.itembuilder;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An ItemBuilder purely designed for {@link Material#WRITTEN_BOOK}s.
 * <p>
 * Implemented in the main {@link ItemBuilder}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 0.1
 */
public final class BookItemBuilder extends BaseItemBuilder<BookItemBuilder> {

    /**
     * Constructs a {@link BookItemBuilder}.
     */
    BookItemBuilder() {
        super(new ItemStack(Material.WRITTEN_BOOK));
    }

    /**
     * Constructs a {@link BookItemBuilder}.
     * <p>
     * Alternative constructor.
     */
    BookItemBuilder(final @NonNull ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != Material.WRITTEN_BOOK) {
            throw new IllegalArgumentException("ItemStack requires the material to be a WRITTEN_BOOK.");
        }
    }

    /**
     * Adds pages to the book of the {@link ItemStack}.
     * <p>
     * <b>Requires</b> the {@link Material} of the ItemStack to be a {@link Material#WRITTEN_BOOK}.
     *
     * @param components the pages to add to the {@link BookMeta}
     * @return the {@link ItemBuilder}
     */
    public BookItemBuilder pages(final @NonNull Component... components) {
        if (getItemStack().getType() != Material.WRITTEN_BOOK) return this;

        final @NonNull BookMeta bookMeta = (BookMeta) getMeta();

        bookMeta.addPages(components);

        setMeta(bookMeta);
        return this;
    }
}
