package com.lokkeestudios.skylands.core.utils.itembuilder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Modifies {@link ItemStack}s that have an ItemMeta type of {@link SkullMeta}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class SkullItemBuilder extends BaseItemBuilder<@NonNull SkullItemBuilder, @NonNull SkullMeta> {

    /**
     * Constructs a {@link SkullItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the SkullItemBuilder
     * @param itemMeta  the {@link SkullMeta} of the SkullItemBuilder
     */
    private SkullItemBuilder(
            final @NonNull ItemStack itemStack,
            final @NonNull SkullMeta itemMeta
    ) {
        super(itemStack, itemMeta);
    }

    /**
     * Creates a {@link SkullItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} to base the builder off of
     * @return the instance of the SkullItemBuilder
     * @throws IllegalArgumentException if the item is not of the correct item meta
     */
    public static @NonNull SkullItemBuilder of(final @NonNull ItemStack itemStack) throws @NonNull IllegalArgumentException {
        return new SkullItemBuilder(itemStack, castMeta(itemStack.getItemMeta(), SkullMeta.class));
    }

    /**
     * Creates a {@link SkullItemBuilder}.
     * <p>
     * Alternative method to create a SkullItemBuilder.
     *
     * @param material the {@link Material} to base the builder off of
     * @return the instance of the SkullItemBuilder
     * @throws IllegalArgumentException if the Material is not an obtainable item,
     *                                  or if the Material's ItemMeta is not of the correct type
     */
    public static @NonNull SkullItemBuilder of(final @NonNull Material material) throws @NonNull IllegalArgumentException {
        return SkullItemBuilder.of(getItem(material));
    }

    /**
     * Creates a {@link SkullItemBuilder} of type {@link Material#PLAYER_HEAD}.
     * <p>
     * Alternative method to create a SkullItemBuilder.
     *
     * @return the instance of the SkullItemBuilder
     * @throws IllegalArgumentException if the {@link Material} is not an obtainable item,
     *                                  or if the Material's ItemMeta is not of the correct type
     */
    public static @NonNull SkullItemBuilder of() throws @NonNull IllegalArgumentException {
        return of(Material.PLAYER_HEAD);
    }

    /**
     * Sets the textures' data of the {@link ItemStack}.
     *
     * @param data the textures' data of the head texture
     * @return the {@link SkullItemBuilder}
     */
    public @NonNull SkullItemBuilder textures(final @NonNull String data) {
        final UUID uuid = new UUID(data.hashCode(), data.hashCode());

        final @NonNull PlayerProfile profile = Optional
                .ofNullable(this.itemMeta.getPlayerProfile())
                .orElse(Bukkit.createProfile(uuid));
        profile.setProperty(new ProfileProperty("textures", data));

        this.itemMeta.setPlayerProfile(profile);
        return this;
    }

    /**
     * Sets the {@link PlayerProfile} of the {@link ItemStack}.
     *
     * @param profile the PlayerProfile of the head
     * @return the {@link SkullItemBuilder}
     */
    public @NonNull SkullItemBuilder playerProfile(final @NonNull PlayerProfile profile) {
        this.itemMeta.setPlayerProfile(profile);
        return this;
    }

    /**
     * Sets the head owner of the {@link ItemStack} to a {@link Player}.
     *
     * @param player the Player owner of the head
     * @return the {@link SkullItemBuilder}
     */
    public @NonNull SkullItemBuilder owner(final @NonNull Player player) {
        this.itemMeta.setOwningPlayer(player);
        return this;
    }
}
