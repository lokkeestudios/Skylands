package me.lokkee.skylands.core.utils.itembuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The main ItemBuilder.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 0.1
 */
public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

    /**
     * Constructs an {@link ItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the ItemBuilder
     */
    public ItemBuilder(
            final @NonNull ItemStack itemStack
    ) {
        super(itemStack);
    }

    /**
     * Creates an {@link ItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} to base the builder off of
     * @return the instance of the ItemBuilder
     */
    public static @NonNull ItemBuilder from(
            final @NonNull ItemStack itemStack
    ) {
        return new ItemBuilder(itemStack);
    }

    /**
     * Creates an {@link ItemBuilder}.
     * <p>
     * Alternative method to create an ItemBuilder.
     *
     * @param material the {@link Material} to base the builder off of
     * @return the instance of the ItemBuilder
     */
    public static @NonNull ItemBuilder from(
            final @NonNull Material material
    ) {
        return ItemBuilder.from(new ItemStack(material));
    }

    /**
     * Creates a {@link HeadItemBuilder} which has {@link Material#PLAYER_HEAD} specific methods
     *
     * @return the instance of the{@link HeadItemBuilder}
     */
    public static @NonNull HeadItemBuilder head() {
        return new HeadItemBuilder();
    }

    /**
     * Creates a {@link HeadItemBuilder} which has {@link Material#PLAYER_HEAD} specific methods
     *
     * @param itemStack An existing PLAYER_HEAD {@link ItemStack}
     * @return the instance of the{@link HeadItemBuilder}
     * @throws IllegalArgumentException if the {@link Material} of the ItemStack is not a PLAYER_HEAD
     */
    public static @NonNull HeadItemBuilder head(@NonNull final ItemStack itemStack) {
        return new HeadItemBuilder(itemStack);
    }
}
