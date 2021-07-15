package me.lokkee.skylands.core.utils.itembuilder;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
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

import java.util.List;

/**
 * Contains all common methods of ItemBuilders
 *
 * @param <T> The ItemBuilder type so the methods can cast to the subtype
 * @author LOKKEE
 * @version 0.1
 */
public abstract class BaseItemBuilder<T extends BaseItemBuilder<T>> {

    /**
     * The {@link ItemStack} of the {@link ItemBuilder}
     */
    private @NonNull ItemStack itemStack;

    /**
     * The {@link ItemMeta} of the {@link ItemBuilder}
     */
    private @NonNull ItemMeta itemMeta;

    /**
     * Constructs an {@link BaseItemBuilder}.
     *
     * @param itemStack the {@link ItemStack} of the ItemBuilder
     * @throws NullPointerException if the ItemStack is null
     */
    public BaseItemBuilder(
            final @NonNull ItemStack itemStack
    ) {
        Validate.notNull(itemStack, "The ItemStack cannot be null.");

        this.itemStack = itemStack.clone();
        this.itemMeta = itemStack.hasItemMeta()
                ? itemStack.getItemMeta()
                : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    /**
     * Sets the name of the {@link ItemStack}.
     *
     * @param component the display name of the ItemStack
     * @return the {@link ItemBuilder}
     */
    public @NonNull T name(
            final @NonNull Component component
    ) {
        this.itemMeta.displayName(component);
        return (T) this;
    }

    /**
     * Sets the lore of the {@link ItemStack}.
     *
     * @param components the lines to set the ItemStacks lore to
     * @return the {@link ItemBuilder}
     */
    public @NonNull T lore(
            final @NonNull List<Component> components
    ) {
        this.itemMeta.lore(components);
        return (T) this;
    }

    /**
     * Sets the lore of the {@link ItemStack}.
     *
     * @param components the lines to set the ItemStacks lore to
     * @return the {@link ItemBuilder}
     */
    public @NonNull T lore(
            final @NonNull Component... components
    ) {
        this.itemMeta.lore(List.of(components));
        return (T) this;
    }

    /**
     * Sets the quantity of the {@link ItemStack}.
     *
     * @param amount the quantity of the ItemStack
     * @return the {@link ItemBuilder}
     */
    public @NonNull T amount(
            final int amount
    ) {
        this.itemStack.setAmount(amount);
        return (T) this;
    }

    /**
     * Sets the unbreakability state of the {@link ItemStack}.
     *
     * @param state if the ItemStack is unbreakable or not
     * @return the {@link ItemBuilder}
     */
    public @NonNull T unbreakable(
            final boolean state
    ) {
        this.itemMeta.setUnbreakable(state);
        return (T) this;
    }

    /**
     * Adds an {@link AttributeModifier} to the {@link ItemStack}.
     *
     * @param attribute the {@link Attribute} to be modified
     * @param modifier  the AttributeModifier to be added
     * @return the {@link ItemBuilder}
     */
    public @NonNull T attributeModifier(
            final @NonNull Attribute attribute,
            final @NonNull AttributeModifier modifier
    ) {
        this.itemMeta.addAttributeModifier(attribute, modifier);
        return (T) this;
    }

    /**
     * Removes an {@link AttributeModifier} from the {@link ItemStack}.
     *
     * @param attribute the {@link Attribute} whose AttributeModifier is to be removed
     * @return the {@link ItemBuilder}
     */
    public @NonNull T removeAttributeModifier(
            final @NonNull Attribute attribute
    ) {
        this.itemMeta.removeAttributeModifier(attribute);
        return (T) this;
    }

    /**
     * Saves data to the {@link PersistentDataContainer} of the {@link ItemStack}.
     *
     * @param key   the {@link NamespacedKey} of the data
     * @param type  the {@link PersistentDataType} of the data
     * @param value the data which is to be saved
     * @return the {@link ItemBuilder}
     */
    public @NonNull <T0, Z> T data(
            final @NonNull NamespacedKey key,
            final @NonNull PersistentDataType<T0, Z> type,
            final @NonNull Z value
    ) {
        this.itemMeta.getPersistentDataContainer().set(key, type, value);
        return (T) this;
    }

    /**
     * Removes data from the {@link PersistentDataContainer} of the {@link ItemStack}.
     *
     * @param key the {@link NamespacedKey} of the data which is to be removed
     * @return the {@link ItemBuilder}
     */
    public @NonNull T removeData(
            final @NonNull NamespacedKey key
    ) {
        this.itemMeta.getPersistentDataContainer().remove(key);
        return (T) this;
    }

    /**
     * Adds one or multiple {@link ItemFlag} to the {@link ItemStack}.
     *
     * @param flags the ItemFlags which are to be added
     * @return the {@link ItemBuilder}
     */
    public @NonNull T flags(
            final @NonNull ItemFlag... flags
    ) {
        this.itemMeta.addItemFlags(flags);
        return (T) this;
    }

    /**
     * Removes one or multiple {@link ItemFlag} from the {@link ItemStack}.
     *
     * @param flags the ItemFlags which are to be removed
     * @return the {@link ItemBuilder}
     */
    public @NonNull T removeFlags(
            final @NonNull ItemFlag... flags
    ) {
        this.itemMeta.removeItemFlags(flags);
        return (T) this;
    }

    /**
     * Adds an {@link Enchantment} to the {@link ItemStack}.
     *
     * @param enchantment the Enchantment which is to be added
     * @param level       the level of the Enchantment
     * @return the {@link ItemBuilder}
     */
    public @NonNull T enchant(
            final @NonNull Enchantment enchantment,
            final int level
    ) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return (T) this;
    }

    /**
     * Removes an {@link Enchantment} from the {@link ItemStack}.
     *
     * @param enchantment the Enchantment which is to be removed
     * @return the {@link ItemBuilder}
     */
    public @NonNull T removeEnchant(
            final @NonNull Enchantment enchantment
    ) {
        this.itemMeta.removeEnchant(enchantment);
        return (T) this;
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

    /**
     * Gets the {@link ItemStack} of the {@link ItemBuilder}
     *
     * @return the ItemStack of the ItemBuilder
     */
    protected @NonNull ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Sets the {@link ItemStack} of the {@link ItemBuilder}
     *
     * @param itemStack the ItemStack to be assigned to the ItemBuilder
     */
    protected void setItemStack(@NonNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Gets the {@link ItemMeta} of the {@link ItemBuilder}
     *
     * @return the ItemMeta of the ItemBuilder
     */
    protected @NonNull ItemMeta getMeta() {
        return this.itemMeta;
    }

    /**
     * Sets the {@link ItemMeta} of the {@link ItemBuilder}
     *
     * @param itemMeta the ItemMeta to be assigned to the ItemBuilder
     */
    protected void setMeta(@NonNull final ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }
}
