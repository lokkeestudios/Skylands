package com.lokkeestudios.skylands.core.utils;

import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
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

        /**
         * The reset {@link Component}.
         */
        public static final @NonNull Component RESET_DEFAULTS = Component.text().color(TextColor.color(255, 255, 255)).decoration(TextDecoration.ITALIC, false).build();
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
            public static final @NonNull String TEXTURE_VALUE = "eyJ0aW1lc3RhbXAiOjE1NjgzNDg5ODc3NDgsInByb2ZpbGVJZCI6IjQ0MDNkYzU0NzViYzRiMTVhNTQ4Y2ZkYTZiMGViN2Q5IiwicHJvZmlsZU5hbWUiOiJHR0dhbWVyc1lUIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mM2NkZmE3YjBkNThhN2EzNWM3NDhkMTkzYjdlNGJjZjNkNzE3NjgzYjgyZjRiNGFlZDIzMTc4Y2U0OWNjYThiIn19fQ==";

            /**
             * The texture signature String of the default npc player skin.
             */
            public static final @NonNull String TEXTURE_SIGNATURE = "Q6bvo8JiPKr9OrHZHCwtnW6XZ2wtXJXFqqPGoBnrRiu31NbS/qpDljqaoiSh3RgaWQY4MTt5MN1KsuretXq16DZPvwI0tpSsbKDna47NbQ2kH1PDQ1kj4nKf5hCcCW59C4r2+nwF7TUzPbbg7EGGMTu2O4tg9BPlwGz9H/nW8QaWGD7I1UPCRvXwk4MCflhVdNzptEDeKY/IRXkA5xr+AKBpNNXpsVLG11sHcLtgCcI/CZWj08gSzC/+iBqQKR2AkvwpsHD8b4zLn4830RRLu0WQXHzRvRF3LJ7skQS8azYygaKuJ2G5L4g8syv+fvyNP5uuVtkwvLgtBfuEkI6T/K7n6iY6KL0cDf57zDOUHsBCoqeGMv+ANJaQSWDKhB048cAzVC7AVv5wSMx/d2FQAqu8+ZdXJuYKQMSFT5Nx4mmrIwigsUvkrRXwvTkfuwiEcJC31OMjBU5w01WpZCILfxN7hCzBP+DGwyRBz02y309KaulrQ7vbe4rCa2gN9znx+TgTCN0zMDmZLD3+PaPgUsBZbokL8leMcihzK6d+8oPFu/N/GqSd7eOJRMWa9Oje+cyfLoCBbQdbNPEtNASynnDyKOq7oiMwOUuwaAz1qlxrHng5lo9DhMJhUhkfL/zmdSfahRBbPeOndp9mEQWWOArrYTCeJG9/FSZgCGsGy9c=";
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
