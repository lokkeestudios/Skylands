package com.lokkeestudios.skylands.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.RGBLike;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.PrimitiveIterator;

/**
 * The util for everything {@link Component} related.
 *
 * @author LOKKEE
 * @version 0.1
 */
public final class TextUtil {

    /**
     * Applies a gradient between the {@link RGBLike} colors a and b to a {@link Component}.
     *
     * @param component the Component to be modified
     * @param a         the first color of the gradient
     * @param b         the second color of the gradient
     * @return a Component with the given gradient
     */
    public static @NonNull Component applyGradient(final @NonNull Component component, final @NonNull RGBLike a, final @NonNull RGBLike b) {
        final @NonNull String content = PlainTextComponentSerializer.plainText().serialize(component);

        final TextComponent.@NonNull Builder builder = Component.text();
        final char[] holder = new char[2];
        final float step = 1f / content.length();
        float interpolation = step;
        int charSize;

        for (final PrimitiveIterator.@NonNull OfInt it = content.codePoints().iterator(); it.hasNext(); ) {
            charSize = Character.toChars(it.nextInt(), holder, 0);

            final @NonNull TextColor currentColor = TextColor.lerp(
                    interpolation,
                    a,
                    b
            );
            interpolation += step;
            builder.append(Component.text(new String(holder, 0, charSize)).color(currentColor));
        }
        return builder.build();
    }

    /**
     * Turns text to a {@link Component} with the gradient of the system.
     *
     * @param text the text of the Component
     * @return the Component with the system gradient
     */
    public static @NonNull Component toComponentWithSystemGradient(final @NonNull String text) {
        return TextUtil.applyGradient(
                Component.text(text),
                TextColor.color(238, 0, 255),
                TextColor.color(0, 200, 255)
        ).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Turns text to a {@link Component} with the gradient of the system and a bold text decoration.
     *
     * @param text the text of the Component
     * @return the bold Component with the system gradient
     */
    public static @NonNull Component toBoldComponentWithSystemGradient(final @NonNull String text) {
        return toComponentWithSystemGradient(text).decoration(TextDecoration.BOLD, true);
    }
}
