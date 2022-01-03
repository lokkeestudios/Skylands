package com.lokkeestudios.skylands.npcsystem.npc.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.npcsystem.npc.Npc;
import com.lokkeestudios.skylands.npcsystem.npc.NpcManager;
import com.lokkeestudios.skylands.npcsystem.npc.NpcRegistry;
import com.lokkeestudios.skylands.npcsystem.npc.NpcType;
import com.lokkeestudios.skylands.npcsystem.npc.gui.NpcGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * A command for everything {@link Npc} related.
 * <p>
 * Makes the {@link NpcManager} methods accessible to users.
 */
public final class NpcCommand {

    /**
     * The main {@link NpcRegistry} instance,
     * which is used for command functionality.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * The main {@link NpcManager} instance,
     * which is used for command functionality.
     */
    private final @NonNull NpcManager npcManager;

    /**
     * Constructs a {@link NpcCommand}.
     *
     * @param npcRegistry the main {@link NpcRegistry} instance
     * @param npcManager  the main {@link NpcManager} instance
     */
    public NpcCommand(
            final @NonNull NpcRegistry npcRegistry,
            final @NonNull NpcManager npcManager
    ) {
        this.npcRegistry = npcRegistry;
        this.npcManager = npcManager;
    }

    /**
     * Registers the root command and all its sub-commands.
     *
     * @param manager the main {@link CommandManager} instance
     */
    public void register(
            final @NonNull CommandManager<CommandSender> manager
    ) {
        final Command.Builder<CommandSender> builder = manager.commandBuilder("npc", "npcs", "npcsystem")
                .senderType(Player.class);

        manager.command(builder.handler(this::processOpenMenu));

        manager.command(builder
                .literal("open", ArgumentDescription.of("Opens the npcs menu"))
                .argument(PlayerArgument.optional("player"))
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".open")
                .handler(this::processOpenMenu)
        );

        manager.command(builder
                .literal("create", ArgumentDescription.of("Creates a npc"))
                .argument(StringArgument.of("id"))
                .argument(EnumArgument.of(NpcType.class, "type"))
                .argument(StringArgument.greedy("name"))
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".create")
                .handler(this::processCreateNpc)
        );

        final Command.@NonNull Builder<CommandSender> setSubCommand = builder.literal("set");

        manager.command(setSubCommand
                .literal("skinid", ArgumentDescription.of("Sets the skin id of a npc"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::npcIdSuggestions)
                        .build()
                )
                .argument(StringArgument.of("skinid"))
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".set.skinid")
                .handler(this::processSetSkinId)
        );

        manager.command(setSubCommand
                .literal("name", ArgumentDescription.of("Sets the name of a npc"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::npcIdSuggestions)
                        .build()
                )
                .argument(StringArgument.greedy("name"))
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".set.name")
                .handler(this::processSetName)
        );

        manager.command(setSubCommand
                .literal("title", ArgumentDescription.of("Sets the title of a npc"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::npcIdSuggestions)
                        .build()
                )
                .argument(StringArgument.greedy("title"))
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".set.title")
                .handler(this::processSetTitle)
        );

        manager.command(setSubCommand
                .literal("location", ArgumentDescription.of("Sets the location of a npc"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::npcIdSuggestions)
                        .build()
                )
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".set.location")
                .handler(this::processSetLocation)
        );

        manager.command(builder
                .literal("delete", ArgumentDescription.of("Deletes a npc"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::npcIdSuggestions)
                        .build()
                )
                .permission(Constants.Permissions.ROOT_NPCSYSTEM + ".delete")
                .handler(this::processDeleteNpc)
        );
    }

    /**
     * Suggests all {@link Npc} ids,
     * which exist in the {@link NpcRegistry}.
     *
     * @param context the {@link CommandContext} of the current command
     * @param input   the current argument input
     */
    private @NonNull List<String> npcIdSuggestions(
            final @NonNull CommandContext<CommandSender> context,
            final @NonNull String input
    ) {
        final @NonNull List<String> ids = npcRegistry.getIds();
        ids.removeIf(id -> !id.startsWith(input));

        return ids;
    }

    /**
     * Opens a {@link NpcGui}.
     *
     * @param context the context of the given command
     */
    private void processOpenMenu(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        @NonNull Player target = player;

        if (context.contains("player")) {
            target = context.get("player");

            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Opened the npcs menu for ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text(target.getName(), Constants.Text.STYLE_HIGHLIGHTED))
            ));
        }
        new NpcGui(npcRegistry).open(target);
    }

    /**
     * Creates a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processCreateNpc(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull NpcType type = context.get("type");
        final @NonNull String name = context.get("name");
        final @NonNull Location location = player.getLocation();

        if (MiniMessage.get().stripTokens(name).length() > 16) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The name of a npc may not be longer than 16 characters.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (npcRegistry.getIds().contains(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is already an existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (id.length() > 30) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The id of a npc may not be longer than 30 characters.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.createNpc(id, type, name, location);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Successfully created the npc ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the {@link Location} of a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processSetLocation(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull Location location = player.getLocation();

        if (!npcRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.setLocation(id, location);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the location of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the skin id of a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processSetSkinId(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull String skinId = context.get("skinid");

        if (!npcRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.setSkinId(id, skinId);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the skin id of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the name of a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processSetName(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull String name = context.get("name");

        if (MiniMessage.get().stripTokens(name).length() > 16) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The name of a npc may not be longer than 16 characters.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (!npcRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.setName(id, name);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the name of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
                .append(Component.text(" to ", Constants.Text.STYLE_DEFAULT))
                .append(Component.text(name, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the title of a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processSetTitle(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull String title = context.get("title");

        if (MiniMessage.get().stripTokens(title).length() > 16) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The title of a npc may not be longer than 16 characters.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (!npcRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.setTitle(id, title);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the title of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
                .append(Component.text(" to ", Constants.Text.STYLE_DEFAULT))
                .append(Component.text(title, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Deletes a {@link Npc}.
     *
     * @param context the context of the given command
     */
    private void processDeleteNpc(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");

        if (!npcRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing npc with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        npcManager.deleteNpc(id);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Successfully deleted the npc ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_ALERT))
        ));
    }
}
