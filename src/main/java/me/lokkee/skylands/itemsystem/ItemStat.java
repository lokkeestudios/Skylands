package me.lokkee.skylands.itemsystem;

import me.lokkee.skylands.core.utils.Constants;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Holds all the item stats.
 */
public enum ItemStat {

    /**
     * The attack damage item stat.
     */
    ATTACK_DAMAGE("Attack Damage", "", Constants.NamespacedKeys.KEY_ATTACK_DAMAGE),

    /**
     * The attack speed item stat.
     */
    ATTACK_SPEED("Attack Speed", "", Constants.NamespacedKeys.KEY_ATTACK_SPEED),

    /**
     * The magic damage item stat.
     */
    MAGIC_DAMAGE("Magic Damage", "%", Constants.NamespacedKeys.KEY_MAGIC_DAMAGE),

    /**
     * The defense item stat.
     */
    DEFENSE("Defense", "", Constants.NamespacedKeys.KEY_DEFENSE),

    /**
     * The health item stat.
     */
    HEALTH("Health", "", Constants.NamespacedKeys.KEY_HEALTH),

    /**
     * The mana item stat.
     */
    MANA("Mana", "", Constants.NamespacedKeys.KEY_MANA),

    /**
     * The crit chance item stat.
     */
    CRIT_CHANCE("Crit Chance", "%", Constants.NamespacedKeys.KEY_CRIT_CHANCE),

    /**
     * The crit damage item stat.
     */
    CRIT_DAMAGE("Crit Damage", "%", Constants.NamespacedKeys.KEY_CRIT_DAMAGE),

    /**
     * The speed item stat.
     */
    SPEED("Speed", "", Constants.NamespacedKeys.KEY_SPEED);

    /**
     * The String name of the item stat.
     */
    private final @NonNull String name;

    /**
     * The String suffix of the item stat.
     * <p>
     * The suffix is being appended to the end
     * of the item stat line, in the {@link Item} lore.
     */
    private final @NonNull String suffix;

    /**
     * The {@link NamespacedKey} of the item stat.
     */
    private final @NonNull NamespacedKey namespacedKey;

    /**
     * Constructs an {@link ItemStat}.
     *
     * @param name          the name of the item stat
     * @param suffix        the suffix of the item stat
     * @param namespacedKey {@link NamespacedKey} of the item stat
     */
    ItemStat(
            final @NonNull String name,
            final @NonNull String suffix,
            final @NonNull NamespacedKey namespacedKey
    ) {
        this.name = name;
        this.suffix = suffix;
        this.namespacedKey = namespacedKey;
    }

    /**
     * Gets the String name of the item stat.
     *
     * @return the name of the item stat
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Gets the String suffix of the item stat.
     *
     * @return the suffix of the item stat
     */
    public @NonNull String getSuffix() {
        return suffix;
    }

    /**
     * Gets the {@link NamespacedKey} of the item stat.
     *
     * @return the NamespacedKey of the item stat
     */
    public @NonNull NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
