package com.lokkeestudios.skylands.core.utils;

import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
import com.lokkeestudios.skylands.npcsystem.Npc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Base64;

/**
 * Stores all sorts of constant values.
 *
 * @author LOKKEE
 * @version 1.0
 */
public final class Constants {

    public static final class Text {
        /**
         * The default {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_DEFAULT = TextColor.color(162, 169, 176);

        /**
         * The highlighted {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_HIGHLIGHTED = TextColor.color(203, 92, 255);

        /**
         * The downlighted {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_DOWNLIGHTED = TextColor.color(72, 77, 84);

        /**
         * The info {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_INFO = TextColor.color(85, 255, 244);

        /**
         * The alert {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_ALERT = TextColor.color(255, 85, 105);

        /**
         * The success {@link TextColor}.
         */
        public static final @NonNull TextColor COLOR_SUCCESS = TextColor.color(127, 202, 73);

        /**
         * The default {@link Style}.
         */
        public static final @NonNull Style STYLE_DEFAULT = Style.style(COLOR_DEFAULT).decoration(TextDecoration.ITALIC, false);

        /**
         * The highlighted {@link Style}.
         */
        public static final @NonNull Style STYLE_HIGHLIGHTED = Style.style(COLOR_HIGHLIGHTED).decoration(TextDecoration.ITALIC, false);

        /**
         * The downlighted {@link Style}.
         */
        public static final @NonNull Style STYLE_DOWNLIGHTED = Style.style(COLOR_DOWNLIGHTED).decoration(TextDecoration.ITALIC, false);

        /**
         * The info {@link Style}.
         */
        public static final @NonNull Style STYLE_INFO = Style.style(COLOR_INFO).decoration(TextDecoration.ITALIC, false);

        /**
         * The alert {@link Style}.
         */
        public static final @NonNull Style STYLE_ALERT = Style.style(COLOR_ALERT).decoration(TextDecoration.ITALIC, false);

        /**
         * The success {@link Style}.
         */
        public static final @NonNull Style STYLE_SUCCESS = Style.style(COLOR_SUCCESS).decoration(TextDecoration.ITALIC, false);

        /**
         * The prefix {@link Component}.
         */
        public static final @NonNull Component PREFIX = Component.empty().append(TextUtil.toBoldComponentWithSystemGradient("[Skylands] "));

        /**
         * The reset {@link Component}.
         */
        public static final @NonNull Component RESET_DEFAULTS = Component.text().color(TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false).build();

        /**
         * The UTF symbol for arrow right.
         */
        public static final @NonNull String SYMBOL_ARROW_RIGHT = "→";

        /**
         * The UTF symbol for arrow left.
         */
        public static final @NonNull String SYMBOL_ARROW_LEFT = "←";

        /**
         * The default npc title.
         */
        public static final @NonNull String NPC_TITLE_DEFAULT = "Npc";
    }

    /**
     * Stores constant permissions {@link String}s.
     */
    public static final class Permissions {
        /**
         * The Plugin permission root.
         */
        public static final @NonNull String ROOT_PLUGIN = "skylands";

        /**
         * The ItemSystem permission root.
         */
        public static final @NonNull String ROOT_ITEMSYSTEM = ROOT_PLUGIN + ".itemsystem";

        /**
         * The NpcSystem permission root.
         */
        public static final @NonNull String ROOT_NPCSYSTEM = ROOT_PLUGIN + ".npcsystem";
    }

    /**
     * Stores constant {@link ItemStack}s.
     */
    public static final class Items {
        /**
         * The menu background {@link ItemStack}.
         */
        public static final @NonNull ItemStack MENU_BACKGROUND = ItemBuilder
                .from(Material.PURPLE_STAINED_GLASS_PANE)
                .name(Component.empty())
                .build();
    }

    /**
     * Stores constant variables.
     */
    public static final class Variables {
        /**
         * The distance at which a {@link Npc} starts facing players.
         */
        public static final int NPC_DISTANCE_LOOK_CLOSE = 6;
    }

    /**
     * Stores constant player head {@link Base64} Strings.
     */
    public static final class Heads {
        /**
         * The {@link Base64} String of the arrow right player head.
         */
        public static final @NonNull String BASE64_ARROW_RIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";

        /**
         * The {@link Base64} String of the arrow left player head.
         */
        public static final @NonNull String BASE64_ARROW_LEFT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
    }

    /**
     * Stores constant player skins in form of their texture values.
     */
    public static final class Skins {
        /**
         * The default npc player skin.
         */
        public static final class NpcDefault {
            /**
             * The texture value String of the default npc player skin.
             */
            public static final @NonNull String TEXTURE_VALUE = "eyJ0aW1lc3RhbXAiOjE1NjgzNDg5MDEwNjMsInByb2ZpbGVJZCI6ImMxYWYxODI5MDYwZTQ0OGRhNjYwOWRmZGM2OGEzOWE4IiwicHJvZmlsZU5hbWUiOiJCQVJLeDQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhhNzMyODFiNDQyMjBlYTI5ZGE3OTBkNDE4ZjY4YWVmMzE2OGQwYzkzYjhhZGFkZjg0NTFlNTk5NzUxMjJlN2YifX19";

            /**
             * The texture signature String of the default npc player skin.
             */
            public static final @NonNull String TEXTURE_SIGNATURE = "xdcaPiVlhJJkCshslagshClk3EX/sC52TQGB0yQ2xYdta7ARKuxtnUrx3vQ/IcAy10VCU8gqXkc01RIU01SyszMatkcobuVBIy0f5XLkQmJQ0W5xiOjIciLDJPBdcOkLyu9j9szrrawLPZ3rL9AuIqm5RxjUrh7iARpABUDPZeEkJ6G5b+lLw9HS5va54AERRFKyu5FPtfZU+hQkXFzLx+opmBfRq+ks1eJwPqzx3TJ7CSOCXPxZpk3BZqTfftybB9bsV8Kxgj3itEevpis94Myd/fEdUz2lHKhshXoFH9XObS00Ci60H2V99npU0ck/YtQ3kZGIC4ItWwRKJhpg6w2Sta6eC3XoMByor0kyKp2rQfa9jIc3E4sMWYl+EkXxNJQQR6+y0CBd1TUGYpBkrWlDz/hWN0FskqrGUMTPtvu9DKqbnXKa6fqgKzfrFsbyQXW3e7wIlug+Gkyg1OZRQJpZ+xJzhstt9Sr12LzM+Iglq/pI6qwjR+FICsGdUPVSk3E92/Vr4T/J748dE4t0p6pDC+OzkCaEFWGwY0w3YnDZ0FcB6JNL4E3Zteukj7GAjC2P6uyYbO6It9iBLoHTqLuX/mk11xtas5MeOurSYFJ6XA9DULSF9vpEryefUkjR/Z1c1iAhpD/KW7nlwbfVFJBkTdzMBDw7wobf1KtjgkU=";
        }
    }

    /**
     * Stores {@link NamespacedKey}s for {@link PersistentDataContainer}s.
     */
    public static final class NamespacedKeys {
        /**
         * The id {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_ID = new NamespacedKey(Skylands.getPlugin(Skylands.class), "id");

        /**
         * The type {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_TYPE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "type");

        /**
         * The rarity {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_RARITY = new NamespacedKey(Skylands.getPlugin(Skylands.class), "rarity");

        /**
         * The defense {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_DEFENSE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "defense");

        /**
         * The magic damage {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_MAGIC_DAMAGE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "magicdamage");

        /**
         * The attack damage {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_ATTACK_DAMAGE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "attackdamage");

        /**
         * The attack speed {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_ATTACK_SPEED = new NamespacedKey(Skylands.getPlugin(Skylands.class), "attackspeed");

        /**
         * The health {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_HEALTH = new NamespacedKey(Skylands.getPlugin(Skylands.class), "health");

        /**
         * The mana {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_MANA = new NamespacedKey(Skylands.getPlugin(Skylands.class), "mana");

        /**
         * The crit chance {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_CRIT_CHANCE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "critchance");

        /**
         * The crit damage {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_CRIT_DAMAGE = new NamespacedKey(Skylands.getPlugin(Skylands.class), "critdamage");

        /**
         * The speed {@link NamespacedKey}.
         */
        public static final @NonNull NamespacedKey KEY_SPEED = new NamespacedKey(Skylands.getPlugin(Skylands.class), "speed");
    }
}
