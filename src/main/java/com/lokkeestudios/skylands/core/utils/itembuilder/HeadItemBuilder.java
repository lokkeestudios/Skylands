package com.lokkeestudios.skylands.core.utils.itembuilder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Base64;
import java.util.UUID;

/**
 * An ItemBuilder purely designed for {@link Material#PLAYER_HEAD}s.
 * <p>
 * Implemented in the main {@link ItemBuilder}.
 * <p>
 * Extends the {@link BaseItemBuilder}.
 *
 * @author LOKKEE
 * @version 0.1
 */
public final class HeadItemBuilder extends BaseItemBuilder<HeadItemBuilder> {

    /**
     * Constructs a {@link HeadItemBuilder}.
     */
    HeadItemBuilder() {
        super(new ItemStack(Material.PLAYER_HEAD));
    }

    /**
     * Constructs a {@link HeadItemBuilder}.
     * <p>
     * Alternative constructor.
     */
    HeadItemBuilder(final @NonNull ItemStack itemStack) {
        super(itemStack);
        if (itemStack.getType() != Material.PLAYER_HEAD) {
            throw new IllegalArgumentException("ItemStack requires the material to be a PLAYER_HEAD.");
        }
    }

    /**
     * Sets the head texture of the {@link ItemStack} via a {@link Base64} String.
     * <p>
     * <b>Requires</b> the {@link Material} of the ItemStack to be a {@link Material#PLAYER_HEAD}.
     *
     * @param base64 the Base64 String of the head texture
     * @return the {@link ItemBuilder}
     */
    public HeadItemBuilder base64(final @NonNull String base64) {
        if (getItemStack().getType() != Material.PLAYER_HEAD) return this;

        final UUID uuid = new UUID(base64.hashCode(), base64.hashCode());

        final @NonNull SkullMeta skullMeta = (SkullMeta) getMeta();
        final @NonNull PlayerProfile profile = Bukkit.createProfile(uuid);

        profile.setProperty(new ProfileProperty("textures", base64));
        skullMeta.setPlayerProfile(profile);

        setMeta(skullMeta);
        return this;
    }

    /**
     * Sets the head owner of the {@link ItemStack} to a {@link Player}.
     * <p>
     * <b>Requires</b> the {@link Material} of the ItemStack to be a {@link Material#PLAYER_HEAD}.
     *
     * @param player the Player owner of the head
     * @return the {@link ItemBuilder}
     */
    public HeadItemBuilder owner(final @NonNull Player player) {
        if (getItemStack().getType() != Material.PLAYER_HEAD) return this;

        final @NonNull SkullMeta skullMeta = (SkullMeta) getMeta();

        skullMeta.setOwningPlayer(player);

        setMeta(skullMeta);
        return this;
    }
}
