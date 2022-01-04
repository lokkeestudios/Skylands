package com.lokkeestudios.skylands.itemsystem.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.itemsystem.*;
import com.lokkeestudios.skylands.core.Rarity;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
import com.lokkeestudios.skylands.itemsystem.gui.ItemGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A command for everything {@link Item} related.
 * <p>
 * Makes the {@link ItemManager} methods accessible to users.
 */
public final class ItemCommand {

    /**
     * The main {@link ItemRegistry} instance,
     * which is used for command functionality.
     */
    private final @NonNull ItemRegistry itemRegistry;

    /**
     * The main {@link ItemManager} instance,
     * which is used for command functionality.
     */
    private final @NonNull ItemManager itemManager;

    /**
     * Constructs an {@link ItemCommand}.
     *
     * @param itemRegistry the main {@link ItemRegistry} instance
     * @param itemManager  the main {@link ItemManager} instance
     */
    public ItemCommand(
            final @NonNull ItemRegistry itemRegistry,
            final @NonNull ItemManager itemManager
    ) {
        this.itemRegistry = itemRegistry;
        this.itemManager = itemManager;
    }

    /**
     * Registers the root command and all its sub-commands.
     *
     * @param manager the main {@link CommandManager} instance
     */
    public void register(
            final @NonNull CommandManager<CommandSender> manager
    ) {
        final Command.Builder<CommandSender> builder = manager.commandBuilder("item", "items", "itemsystem");

        manager.command(builder.handler(this::processOpenMenu).senderType(Player.class));

        manager.command(builder
                .literal("open", ArgumentDescription.of("Opens the items menu"))
                .argument(PlayerArgument.optional("player"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".open")
                .handler(this::processOpenMenu)
        );

        manager.command(builder
                .literal("give", ArgumentDescription.of("Gives the build itemstack of an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .argument(PlayerArgument.optional("player"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".give")
                .handler(this::processGive)
        );

        manager.command(builder
                .literal("create", ArgumentDescription.of("Creates an item"))
                .argument(StringArgument.of("id"))
                .argument(EnumArgument.of(ItemType.class, "type"))
                .argument(EnumArgument.of(Rarity.class, "rarity"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".create")
                .handler(this::processCreateItem)
        );

        final Command.@NonNull Builder<CommandSender> modifySubCommand = builder.literal("modify");

        manager.command(modifySubCommand
                .literal("name", ArgumentDescription.of("Sets the name of an itemstack"))
                .argument(StringArgument.greedy("name"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".modify.name")
                .handler(this::processModifyName)
        );

        manager.command(modifySubCommand
                .literal("addlore", ArgumentDescription.of("Adds a line to the lore of an itemstack"))
                .argument(StringArgument.greedy("text"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".modify.addlore")
                .handler(this::processModifyAddLore)
        );

        manager.command(modifySubCommand
                .literal("removelore", ArgumentDescription.of("Removes a line from the lore of an itemstack"))
                .argument(IntegerArgument.of("index"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".modify.removelore")
                .handler(this::processModifyRemoveLore)
        );

        manager.command(builder
                .literal("get")
                .literal("itemstack", ArgumentDescription.of("Gets the internal itemstack of an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".get.itemstack")
                .handler(this::processGetItemStack)
        );

        final Command.@NonNull Builder<CommandSender> setSubCommand = builder.literal("set");

        manager.command(setSubCommand
                .literal("rarity", ArgumentDescription.of("Sets the rarity of an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .argument(EnumArgument.of(Rarity.class, "rarity"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".set.rarity")
                .handler(this::processSetRarity)
        );

        manager.command(setSubCommand
                .literal("itemstack", ArgumentDescription.of("Sets the itemstack of an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".set.itemstack")
                .handler(this::processSetItemStack)
        );

        manager.command(setSubCommand
                .literal("stat", ArgumentDescription.of("Sets the value of of an stat of an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .argument(EnumArgument.of(ItemStat.class, "stat"))
                .argument(DoubleArgument.of("value"))
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".set.stat")
                .handler(this::processSetStat)
        );

        manager.command(builder
                .literal("delete", ArgumentDescription.of("Deletes an item"))
                .argument(StringArgument
                        .<CommandSender>newBuilder("id")
                        .withSuggestionsProvider(this::itemIdSuggestions)
                        .build()
                )
                .permission(Constants.Permissions.ROOT_ITEMSYSTEM + ".delete")
                .handler(this::processDeleteItem)
        );
    }

    /**
     * Suggests all {@link Item} ids,
     * which exist in the {@link ItemRegistry}.
     *
     * @param context the {@link CommandContext} of the current command
     * @param input   the current argument input
     */
    private @NonNull List<String> itemIdSuggestions(
            final @NonNull CommandContext<CommandSender> context,
            final @NonNull String input
    ) {
        final @NonNull List<String> ids = itemRegistry.getIds();
        ids.removeIf(id -> !id.startsWith(input));

        return ids;
    }

    /**
     * Opens an {@link ItemGui}.
     *
     * @param context the context of the given command
     */
    private void processOpenMenu(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        @NonNull Player target = player;

        if (context.contains("player")) {
            target = context.get("player");

            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Opened the items menu for ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text(target.getName(), Constants.Text.STYLE_HIGHLIGHTED))
            ));
        }
        new ItemGui(itemRegistry).open(target);
    }

    /**
     * Gives the build itemstack of an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processGive(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");

        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        @NonNull Player target = player;

        if (context.contains("player")) {
            target = context.get("player");

            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Gave the build itemstack of ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
                    .append(Component.text(" to ", Constants.Text.STYLE_DEFAULT))
                    .append(Component.text(target.getName(), Constants.Text.STYLE_HIGHLIGHTED))
            ));
        }
        target.getInventory().addItem(itemRegistry.getItemFromId(id).getBuildItemStack());
    }

    /**
     * Creates an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processCreateItem(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull ItemType type = context.get("type");
        final @NonNull Rarity rarity = context.get("rarity");
        final @NonNull ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Hold an itemstack for the item in your hand.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (itemStack.getItemMeta().displayName() == null) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The itemstack may need to have a display name.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (itemRegistry.getIds().contains(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is already an existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (id.length() > 30) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The id of an item may not be longer than 30 characters.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        itemManager.createItem(id, type, rarity, itemStack);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Successfully created the item ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Modifies the name of an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processModifyName(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull Component name = Component.text((String) context.get("name"));
        final @NonNull ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Hold the to be modified itemstack in your hand.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        player.getInventory().setItemInMainHand(ItemBuilder.from(itemStack).name(name).build());

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the name to ", Constants.Text.STYLE_DEFAULT)
                .append(name.style(Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Modifies the lore of an {@link ItemStack} by adding a line.
     *
     * @param context the context of the given command
     */
    private void processModifyAddLore(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull Component text = MiniMessage.get().parse(context.get("text"));
        final @NonNull ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Hold the to be modified itemstack in your hand.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        final @NonNull ItemMeta itemMeta = itemStack.getItemMeta();

        final @NonNull List<Component> lore = itemMeta.hasLore()
                ? Objects.requireNonNull(itemMeta.lore())
                : new ArrayList<>();
        lore.add(text);

        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItemInMainHand(itemStack);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Added the line ", Constants.Text.STYLE_DEFAULT)
                .append(text)
        ));
    }

    /**
     * Modifies the lore of an {@link ItemStack} by removing a line.
     *
     * @param context the context of the given command
     */
    private void processModifyRemoveLore(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final int index = context.get("index");
        final @NonNull ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Hold the to be modified itemstack in your hand.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        final @NonNull ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasLore()
                || Objects.requireNonNull(itemMeta.lore()).size() <= index
                || 0 > index
        ) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing line at index ", Constants.Text.STYLE_ALERT)
                    .append(Component.text(index, Constants.Text.STYLE_DEFAULT))
            ));
            return;
        }
        final @NonNull List<Component> lore = Objects.requireNonNull(itemMeta.lore());
        lore.remove(index);

        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);

        player.getInventory().setItemInMainHand(itemStack);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Removed the line at index ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(index, Constants.Text.STYLE_ALERT))
        ));
    }

    /**
     * Gets the {@link ItemStack} of an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processGetItemStack(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");

        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        final @NonNull Item item = itemRegistry.getItemFromId(id);
        player.getInventory().addItem(item.getItemStack());

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("You were given the internal itemstack of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the {@link ItemStack} of an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processSetItemStack(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("Hold an itemstack for the item in your hand.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (itemStack.getItemMeta().displayName() == null) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("The itemstack may need to have a display name.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        itemManager.setItemStack(id, itemStack);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the internal itemstack of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the {@link Rarity} of an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processSetRarity(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull Rarity rarity = context.get("rarity");

        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        itemManager.setRarity(id, rarity);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the rarity of ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
                .append(Component.text(" to ", Constants.Text.STYLE_DEFAULT))
                .append(Component.text(rarity.getName(), Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Sets the value of an {@link ItemStat} from an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processSetStat(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");
        final @NonNull ItemStat stat = context.get("stat");
        final double value = context.get("value");

        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        itemManager.setStat(id, stat, value);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Set the ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(stat.getName(), Constants.Text.STYLE_HIGHLIGHTED))
                .append(Component.text(" value of ", Constants.Text.STYLE_DEFAULT))
                .append(Component.text(id, Constants.Text.STYLE_HIGHLIGHTED))
                .append(Component.text(" to ", Constants.Text.STYLE_DEFAULT))
                .append(Component.text(value, Constants.Text.STYLE_HIGHLIGHTED))
        ));
    }

    /**
     * Deletes an {@link Item}.
     *
     * @param context the context of the given command
     */
    private void processDeleteItem(final @NonNull CommandContext<CommandSender> context) {
        final @NonNull Player player = (Player) context.getSender();

        final @NonNull String id = context.get("id");

        if (!itemRegistry.isIdValid(id)) {
            player.sendMessage(Constants.Text.PREFIX.append(Component
                    .text("There is no existing item with such an id.", Constants.Text.STYLE_ALERT)
            ));
            return;
        }
        itemManager.deleteItem(id);

        player.sendMessage(Constants.Text.PREFIX.append(Component
                .text("Successfully deleted the item ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(id, Constants.Text.STYLE_ALERT))
        ));
    }
}
