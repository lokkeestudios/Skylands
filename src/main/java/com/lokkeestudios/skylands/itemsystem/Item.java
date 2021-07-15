package com.lokkeestudios.skylands.itemsystem;

import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.core.Rarity;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * The base item with all common fields and methods.
 * <p>
 * The heart and the core of the entire ItemSystem
 */
public final class Item {

    /**
     * The unique String id of the Item.
     * <p>
     * This Item field is final and thus cannot be changed.
     */
    protected final @NonNull String id;

    /**
     * The {@link ItemType} of the Item.
     * <p>
     * This Item field is final and thus cannot be changed.
     */
    protected final @NonNull ItemType type;

    /**
     * A {@link Map}, holding all the {@link ItemStat}s
     * of the Item and its values.
     * <p>
     * Key - The ItemStat Enum reference
     * <p>
     * Value - The value of the ItemStat
     */
    protected final @NonNull Map<ItemStat, Double> stats = new HashMap<>();

    /**
     * The {@link Rarity} of the Item.
     */
    protected @NonNull Rarity rarity;

    /**
     * The {@link ItemStack} of the Item.
     */
    protected @NonNull ItemStack itemStack;

    /**
     * Constructs an Item.
     *
     * @param id        the unique String id of the Item
     * @param type      the {@link ItemType} of the Item
     * @param rarity    the {@link Rarity} of the Item
     * @param itemStack the {@link ItemStack} of the Item
     */
    public Item(final @NonNull String id, final @NonNull ItemType type, final @NonNull Rarity rarity, final @NonNull ItemStack itemStack) {
        this.id = id;
        this.type = type;
        this.rarity = rarity;
        this.itemStack = itemStack;
    }

    /**
     * Gets the final build {@link ItemStack} with all the data and styles.
     */
    public final @NonNull ItemStack getBuildItemStack() {
        final @NonNull ItemBuilder item = ItemBuilder.from(itemStack);

        item.name(rarity.applyColor(Objects.requireNonNull(itemStack.getItemMeta().displayName())));

        final @Nullable List<Component> description = itemStack.getItemMeta().lore();
        final @NonNull List<Component> lore = new ArrayList<>();

        lore.add(Component.text(type.getName(), Constants.Text.STYLE_DOWNLIGHTED));
        lore.add(Component.empty());

        for (final Map.@NonNull Entry<ItemStat, Double> entry : stats.entrySet()) {
            lore.add(Component
                    .text(entry.getKey().getName() + ": ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text(
                            (entry.getValue() > 0 ? "+" : "")
                                    + entry.getValue()
                                    + entry.getKey().getSuffix(), Constants.Text.STYLE_HIGHLIGHTED
                    ))
            );
            item.data(entry.getKey().getNamespacedKey(), PersistentDataType.DOUBLE, entry.getValue());
        }

        if (stats.size() > 0) {
            lore.add(Component.empty());
        }

        if (description != null) {
            lore.addAll(description);
            lore.add(Component.empty());
        }

        lore.add(rarity
                .applyColor(Component.text(rarity.name()))
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        );

        item.lore(lore);

        item.unbreakable(true);
        item.flags(ItemFlag.values());

        item.data(Constants.NamespacedKeys.KEY_ID, PersistentDataType.STRING, id);
        item.data(Constants.NamespacedKeys.KEY_TYPE, PersistentDataType.STRING, type.name());
        item.data(Constants.NamespacedKeys.KEY_RARITY, PersistentDataType.STRING, rarity.name());

        return item.build();
    }

    /**
     * Gets the unique String id of the Item.
     *
     * @return the Items id
     */
    public @NonNull String getId() {
        return id;
    }

    /**
     * Gets the {@link ItemType} of the Item.
     *
     * @return the Items ItemType
     */
    public @NonNull ItemType getType() {
        return type;
    }

    /**
     * Gets the {@link Rarity} of the Item.
     *
     * @return the Items Rarity
     */
    public @NonNull Rarity getRarity() {
        return rarity;
    }

    /**
     * Sets the {@link Rarity} of the Item.
     *
     * @param rarity the Rarity to be set
     */
    public void setRarity(final @NonNull Rarity rarity) {
        this.rarity = rarity;
    }

    /**
     * Gets the {@link ItemStack} of the Item.
     *
     * @return the Items ItemStack
     */
    public @NonNull ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Sets the {@link ItemStack} of the Item.
     *
     * @param itemStack the ItemStack to be set
     */
    public void setItemStack(final @NonNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Gets all {@link ItemStat}s of the Item.
     *
     * @return the {@link List} of ItemStats
     */
    public @NonNull List<ItemStat> getStats() {
        return stats.keySet().stream().toList();
    }

    /**
     * Gets the value of an specific {@link ItemStat}.
     *
     * @param stat the stat of which the value is wanted
     * @return the value of the specified ItemStat
     */
    public double getStat(final @NonNull ItemStat stat) {
        return stats.get(stat);
    }

    /**
     * Removes an {@link ItemStat} from the Item.
     *
     * @param stat the ItemStat which is to be removed
     */
    public void removeStat(final @NonNull ItemStat stat) {
        stats.remove(stat);
    }

    /**
     * Checks whether an {@link ItemStat} is present on the Item.
     *
     * @param stat the ItemStat for which is to be checked
     * @return whether the ItemStat is present
     */
    public boolean hasStat(final @NonNull ItemStat stat) {
        return stats.containsKey(stat);
    }

    /**
     * Sets the values of an {@link ItemStat} of the Item.
     *
     * @param stat  the ItemStat of which the value is to be set
     * @param value the value which is to be assigned
     */
    public void setStat(final @NonNull ItemStat stat, final double value) {
        stats.put(stat, value);
    }

    /**
     * Gets the plain String name of the internal {@link ItemStat}.
     *
     * @return the plain String name
     */
    public @NonNull String getName() {
        return PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(itemStack.getItemMeta().displayName()));
    }
}
