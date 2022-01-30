package com.lokkeestudios.skylands.core.utils.itembuilder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The item builder purely designed for {@link Material#LEATHER_HELMET}s, {@link Material#LEATHER_CHESTPLATE}s,
 * {@link Material#LEATHER_LEGGINGS}s and {@link Material#LEATHER_BOOTS}s.
 * <p>
 * Modifies {@link ItemStack}s that have an ItemMeta type of {@link LeatherArmorMeta}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class LeatherArmorItemBuilder extends BaseItemBuilder<LeatherArmorItemBuilder, LeatherArmorMeta> {

    /**
     * Constructs a {@link LeatherArmorItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the LeatherArmorItemBuilder
     * @param itemMeta  the {@link LeatherArmorMeta} of the LeatherArmorItemBuilder
     */
    private LeatherArmorItemBuilder(
            final @NonNull ItemStack itemStack,
            final @NonNull LeatherArmorMeta itemMeta
    ) {
        super(itemStack, itemMeta);
    }

    /**
     * Creates a {@link LeatherArmorItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} to base the builder off of
     * @return the instance of the LeatherArmorItemBuilder
     */
    public static @NonNull LeatherArmorItemBuilder of(final @NonNull ItemStack itemStack) throws IllegalArgumentException {
        return new LeatherArmorItemBuilder(itemStack, castMeta(itemStack.getItemMeta(), LeatherArmorMeta.class));
    }

    /**
     * Creates a {@link LeatherArmorItemBuilder}.
     * <p>
     * Alternative method to create a LeatherArmorItemBuilder.
     *
     * @param material the {@link Material} to base the builder off of
     * @return the instance of the LeatherArmorItemBuilder
     */
    public static @NonNull LeatherArmorItemBuilder of(final @NonNull Material material) throws IllegalArgumentException {
        return LeatherArmorItemBuilder.of(getItem(material));
    }

    /**
     * Sets the leather color of the {@link ItemStack}.
     *
     * @param color the leather color to be set
     * @return the builder {@link LeatherArmorItemBuilder}
     */
    public @NonNull LeatherArmorItemBuilder color(final @NonNull Color color) {
        this.itemMeta.setColor(color);
        return this;
    }
}
