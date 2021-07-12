package me.lokkee.skylands.core;

import me.lokkee.skylands.core.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;


/**
 * Holds all the rarities.
 */
public enum Rarity {

    /**
     * The common rarity.
     */
    COMMON("Common", 0) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return component.color(TextColor.color(222, 222, 222)).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The uncommon rarity.
     */
    UNCOMMON("Uncommon", 1) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return component.color(TextColor.color(67, 252, 61)).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The rare rarity.
     */
    RARE("Rare", 2) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return TextUtil.applyGradient(
                    component,
                    TextColor.color(81, 81, 237),
                    TextColor.color(89, 128, 255)
            ).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The epic rarity.
     */
    EPIC("Epic", 3) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return TextUtil.applyGradient(
                    component,
                    TextColor.color(175, 13, 184),
                    TextColor.color(255, 41, 255)
            ).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The legendary rarity.
     */
    LEGENDARY("Legendary", 4) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return TextUtil.applyGradient(
                    component,
                    TextColor.color(255, 149, 0),
                    TextColor.color(252, 197, 30)
            ).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The mythic rarity.
     */
    MYTHIC("Mythic", 5) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return TextUtil.applyGradient(
                    component,
                    TextColor.color(245, 88, 240),
                    TextColor.color(142, 61, 255)
            ).decoration(TextDecoration.ITALIC, false);
        }
    },
    /**
     * The special rarity.
     */
    SPECIAL("Special", 6) {
        public @NotNull Component applyColor(final @NonNull Component component) {
            return TextUtil.applyGradient(
                    component,
                    TextColor.color(252, 121, 121),
                    TextColor.color(245, 59, 124)
            ).decoration(TextDecoration.ITALIC, false);
        }
    };

    /**
     * The String name of the rarity.
     */
    private final @NonNull String name;

    /**
     * The weight of the rarity.
     * <p>
     * Indicates the order of the rarities - from worst to best.
     */
    private final int weight;

    /**
     * Constructs a {@link Rarity}.
     *
     * @param name   the name of the rarity
     * @param weight the weight of the rarity
     */
    Rarity(final @NonNull String name, final int weight) {
        this.name = name;
        this.weight = weight;
    }

    /**
     * Gets the String name of the rarity.
     *
     * @return the name of the rarity
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Gets the name of the rarity.
     *
     * @return the name of the rarity
     */
    public int getWeight() {
        return weight;
    }

    /**
     * The applyColor method of the rarity.
     * <p>
     * applyColor - Applies the color of the rarity to a {@link Component}.
     *
     * @return the Component with applied color
     */
    public abstract @NonNull Component applyColor(final @NonNull Component component);
}
