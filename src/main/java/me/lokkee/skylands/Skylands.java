package me.lokkee.skylands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.paper.PaperCommandManager;
import me.lokkee.skylands.core.command.CommandExceptionHandler;
import me.lokkee.skylands.core.database.DatabaseManager;
import me.lokkee.skylands.itemsystem.ItemManager;
import me.lokkee.skylands.itemsystem.ItemRegistry;
import me.lokkee.skylands.itemsystem.command.ItemCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public final class Skylands extends JavaPlugin {

    /**
     * The main {@link CommandManager}.
     */
    private CommandManager<CommandSender> commandManager;

    /**
     * The main {@link ItemRegistry}.
     */
    private ItemRegistry itemRegistry;

    /**
     * The main {@link ItemManager}.
     */
    private ItemManager itemManager;

    /**
     * Handles everything which needs to be done,
     * when the {@link Skylands} plugin is being enabled.
     */
    @Override
    public void onEnable() {
        getLogger().info("""
                 
                 _______  ___   _  __   __  ___      _______  __    _  ______   _______
                |       ||   | | ||  | |  ||   |    |   _   ||  |  | ||      | |       |
                |  _____||   |_| ||  |_|  ||   |    |  |_|  ||   |_| ||  _    ||  _____|
                | |_____ |      _||       ||   |    |       ||       || | |   || |_____
                |_____  ||     |_ |_     _||   |___ |       ||  _    || |_|   ||_____  |
                 _____| ||    _  |  |   |  |       ||   _   || | |   ||       | _____| |
                |_______||___| |_|  |___|  |_______||__| |__||_|  |__||______| |_______| v0.1
                """);

        try {
            commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the command manager.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final @NonNull DatabaseManager databaseManager = new DatabaseManager(this);

        itemRegistry = new ItemRegistry();
        itemManager = new ItemManager(itemRegistry, databaseManager);

        registerCommands();
    }

    /**
     * Handles everything which needs to be done,
     * when the {@link Skylands} plugin is being disabled.
     */
    @Override
    public void onDisable() {
        saveData();
    }

    /**
     * Registers and sets up all commands.
     */
    private void registerCommands() {
        final @NonNull ItemCommand itemCommand = new ItemCommand(itemRegistry, itemManager);
        itemCommand.register(commandManager);

        new CommandExceptionHandler<CommandSender>().apply(commandManager, AudienceProvider.nativeAudience());
    }

    /**
     * Saves the data of all systems.
     */
    private void saveData() {
        itemManager.saveItems();
    }
}
