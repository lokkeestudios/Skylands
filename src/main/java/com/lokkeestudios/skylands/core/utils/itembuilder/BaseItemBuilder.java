package com.lokkeestudios.skylands.core.utils.itembuilder;

import com.google.common.collect.Multimap;
import com.lokkeestudios.skylands.core.utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains all common methods of ItemBuilders
 *
 * @param <B> The ItemBuilder type so the methods can cast to the subtype
 * @param <M> The ItemMeta type
 * @author LOKKEE
 * @version 1.0
 */
@SuppressWarnings({"unchecked"})
public abstract class BaseItemBuilder<B extends BaseItemBuilder<B, M>, M extends ItemMeta> {

    /**
     * The {@link ItemStack} of the builder
     */
    protected final @NonNull ItemStack itemStack;

    /**
     * The item meta of the builder
     */
    protected final @NonNull M itemMeta;

    /**
     * Constructs an {@link BaseItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the ItemBuilder
     * @throws NullPointerException if the ItemStack is null
     */
    protected BaseItemBuilder(
            final @NonNull ItemStack itemStack,
            final @NonNull M itemMeta
    ) {
        this.itemStack = itemStack.clone();
        this.itemMeta = itemMeta;
    }

    /**
     * Attempts to cast the item meta to the expected type,
     * returning the result if successful.
     *
     * @param itemMeta     the item meta to be cast
     * @param expectedType the class of the expected type
     * @param <T>          the expected type
     * @return the item meta cast to the expected type
     * @throws IllegalArgumentException if the item meta is not the expected type
     */
    protected static <T extends ItemMeta> T castMeta(final @NonNull ItemMeta itemMeta, final @NonNull Class<T> expectedType)
            throws IllegalArgumentException {
        try {
            return expectedType.cast(itemMeta);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("The ItemMeta must be of type "
                    + expectedType.getSimpleName()
                    + " but received ItemMeta of type "
                    + itemMeta.getClass().getSimpleName());
        }
    }

    /**
     * Returns an {@link ItemStack} of {@link Material} if it is an obtainable item,
     * else throws an exception.
     *
     * @param material the material of the to be returned ItemStack
     * @return an ItemStack of the given Material
     * @throws IllegalArgumentException if the Material is not an item
     */
    protected static @NonNull ItemStack getItem(final @NonNull Material material) throws IllegalArgumentException {
        if (!material.isItem()) throw new IllegalArgumentException("The Material must be an obtainable item.");

        return new ItemStack(material);
    }

    /**
     * Sets the name of the {@link ItemStack}.
     *
     * @param name the display name of the ItemStack
     * @return the builder
     */
    public @NonNull B name(
            final @NonNull Component name
    ) {
        this.itemMeta.displayName(TextUtil.resetDefaults(name));
        return (B) this;
    }

    /**
     * Sets the lore of the {@link ItemStack}.
     *
     * @param lines the lines to set the ItemStacks lore to
     * @return the builder
     */
    public @NonNull B lore(
            final @NonNull List<Component> lines
    ) {
        final @NonNull List<Component> resetLines = new ArrayList<>(lines);
        resetLines.replaceAll(TextUtil::resetDefaults);

        this.itemMeta.lore(resetLines);
        return (B) this;
    }

    /**
     * Sets the lore of the {@link ItemStack}.
     *
     * @param lines the lines to set the ItemStacks lore to
     * @return the builder
     */
    public @NonNull B lore(
            final @NonNull Component... lines
    ) {
        this.lore(List.of(lines));
        return (B) this;
    }

    /**
     * Sets the material of the {@link ItemStack}.
     *
     * @param material the material of the ItemStack
     * @return the builder
     */
    public @NonNull B material(
            final @NonNull Material material
    ) {
        this.itemStack.setType(material);
        return (B) this;
    }

    /**
     * Sets the quantity of the {@link ItemStack}.
     *
     * @param amount the quantity of the ItemStack
     * @return the builder
     */
    public @NonNull B amount(
            final int amount
    ) {
        this.itemStack.setAmount(amount);
        return (B) this;
    }

    /**
     * Sets the unbreakability state of the {@link ItemStack}.
     *
     * @param unbreakable whether the ItemStack is unbreakable or not
     * @return the builder
     */
    public @NonNull B unbreakable(
            final boolean unbreakable
    ) {
        this.itemMeta.setUnbreakable(unbreakable);
        return (B) this;
    }

    /**
     * Sets the {@link AttributeModifier} of the {@link ItemStack}.
     *
     * @param attributeModifiers the AttributeModifiers to be modified
     * @return the builder
     */
    public @NonNull B attributeModifier(
            final @NonNull Multimap<Attribute, AttributeModifier> attributeModifiers
    ) {
        this.itemMeta.setAttributeModifiers(attributeModifiers);
        return (B) this;
    }

    /**
     * Adds an {@link AttributeModifier} to the {@link ItemStack}.
     *
     * @param attribute the {@link Attribute} to be modified
     * @param modifier  the AttributeModifier to be added
     * @return the builder
     */
    public @NonNull B attributeModifier(
            final @NonNull Attribute attribute,
            final @NonNull AttributeModifier modifier
    ) {
        this.itemMeta.addAttributeModifier(attribute, modifier);
        return (B) this;
    }

    /**
     * Saves data to the {@link PersistentDataContainer} of the {@link ItemStack}.
     *
     * @param key   the {@link NamespacedKey} of the data
     * @param type  the {@link PersistentDataType} of the data
     * @param value the data which is to be saved
     * @param <T>   the primary object type of the data
     * @param <Z>   the retrieve object type of the data
     * @return the builder
     */
    public <T, Z> @NonNull B data(
            final @NonNull NamespacedKey key,
            final @NonNull PersistentDataType<T, Z> type,
            final @NonNull Z value
    ) {
        this.itemMeta.getPersistentDataContainer().set(key, type, value);
        return (B) this;
    }

    /**
     * Sets {@link ItemFlag}s of the {@link ItemStack}.
     *
     * @param flags the ItemFlags which are to be added
     * @return the builder
     */
    public @NonNull B flags(
            final @NonNull List<ItemFlag> flags
    ) {
        this.itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
        return (B) this;
    }

    /**
     * Adds one or multiple {@link ItemFlag} to the {@link ItemStack}.
     *
     * @param flags the ItemFlags which are to be added
     * @return the builder
     */
    public @NonNull B flags(
            final @NonNull ItemFlag... flags
    ) {
        this.itemMeta.addItemFlags(flags);
        return (B) this;
    }

    /**
     * Sets the {@link Enchantment}s of the {@link ItemStack}.
     *
     * @param enchantments the Enchantments which is are to be added
     * @return the builder
     */
    public @NonNull B enchants(
            final @NonNull Map<Enchantment, Integer> enchantments
    ) {
        this.itemStack.addEnchantments(enchantments);
        return (B) this;
    }

    /**
     * Adds an {@link Enchantment} to the {@link ItemStack}.
     *
     * @param enchantment the Enchantment which is to be added
     * @param level       the level of the Enchantment
     * @return the builder
     */
    public @NonNull B enchant(
            final @NonNull Enchantment enchantment,
            final int level
    ) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return (B) this;
    }

    /**
     * Removes an {@link Enchantment} from the {@link ItemStack}.
     *
     * @param enchantment the Enchantment which is to be removed
     * @return the builder
     */
    public @NonNull B removeEnchant(
            final @NonNull Enchantment enchantment
    ) {
        this.itemMeta.removeEnchant(enchantment);
        return (B) this;
    }

    /**
     * Sets the custom model data of the {@link ItemStack}.
     *
     * @param data the custom model data which is to be set
     * @return the builder
     */
    public @NonNull B customModelData(
            final @NonNull Integer data
    ) {
        this.itemMeta.setCustomModelData(data);
        return (B) this;
    }

    /**
     * Builds the {@link ItemStack} from set properties.
     *
     * @return the built ItemStack
     */
    public @NonNull ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack.clone();
    }
}
