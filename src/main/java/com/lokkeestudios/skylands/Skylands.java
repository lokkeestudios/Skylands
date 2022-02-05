package com.lokkeestudios.skylands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.paper.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.lokkeestudios.skylands.core.command.CommandExceptionHandler;
import com.lokkeestudios.skylands.core.database.DatabaseManager;
import com.lokkeestudios.skylands.core.event.ServerLoadListener;
import com.lokkeestudios.skylands.core.tasks.SaveDataManager;
import com.lokkeestudios.skylands.itemsystem.ItemManager;
import com.lokkeestudios.skylands.itemsystem.ItemRegistry;
import com.lokkeestudios.skylands.itemsystem.command.ItemCommand;
import com.lokkeestudios.skylands.npcsystem.NpcManager;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import com.lokkeestudios.skylands.npcsystem.command.NpcCommand;
import com.lokkeestudios.skylands.npcsystem.event.NpcInteractListener;
import com.lokkeestudios.skylands.npcsystem.event.NpcLookCloseListener;
import com.lokkeestudios.skylands.npcsystem.event.NpcSpawnPacketListener;
import com.lokkeestudios.skylands.npcsystem.event.RegisterNpcTeamListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
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
     * The main {@link NpcRegistry}.
     */
    private NpcRegistry npcRegistry;

    /**
     * The main {@link NpcManager}.
     */
    private NpcManager npcManager;

    /**
     * The main {@link SaveDataManager}.
     */
    private SaveDataManager saveDataTaskManager;

    /**
     * Handles everything which needs to be done,
     * when the {@link Skylands} plugin is being loaded.
     */
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        WrapperPlayServerChatMessage.HANDLE_JSON = false;
    }

    /**
     * Handles everything which needs to be done,
     * when the {@link Skylands} plugin is being enabled.
     */
    @Override
    public synchronized void onEnable() {
        getLogger().info("""
                 
                 _______  ___   _  __   __  ___      _______  __    _  ______   _______
                |       ||   | | ||  | |  ||   |    |   _   ||  |  | ||      | |       |
                |  _____||   |_| ||  |_|  ||   |    |  |_|  ||   |_| ||  _    ||  _____|
                | |_____ |      _||       ||   |    |       ||       || | |   || |_____
                |_____  ||     |_ |_     _||   |___ |       ||  _    || |_|   ||_____  |
                 _____| ||    _  |  |   |  |       ||   _   || | |   ||       | _____| |
                |_______||___| |_|  |___|  |_______||__| |__||_|  |__||______| |_______|""" + " v." + getDescription().getVersion());

        try {
            this.commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (final @NonNull Exception exception) {
            getLogger().severe("Failed to initialize the command manager.");
            this.setEnabled(false);
            return;
        }

        final @NonNull DatabaseManager databaseManager = new DatabaseManager(this);

        this.itemRegistry = new ItemRegistry();
        this.itemManager = new ItemManager(itemRegistry, databaseManager);

        this.npcRegistry = new NpcRegistry();
        this.npcManager = new NpcManager(npcRegistry, databaseManager);

        final long saveDataInterval = 20L * 60 * 10;
        this.saveDataTaskManager = new SaveDataManager(this, npcManager, itemManager);
        this.saveDataTaskManager.startTask(saveDataInterval);

        registerEvents();
        registerCommands();

        PacketEvents.getAPI().init();
        registerPacketEvents();
    }

    /**
     * Handles everything which needs to be done,
     * when the {@link Skylands} plugin is being disabled.
     */
    @Override
    public synchronized void onDisable() {
        this.saveDataTaskManager.disableSystems();
        PacketEvents.getAPI().terminate();
    }

    /**
     * Registers and sets up all events.
     */
    private void registerEvents() {
        final @NonNull PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        pluginManager.registerEvents(new ServerLoadListener(npcManager), this);
        pluginManager.registerEvents(new NpcLookCloseListener(this, npcRegistry), this);
        pluginManager.registerEvents(new NpcInteractListener(npcManager), this);
        pluginManager.registerEvents(new RegisterNpcTeamListener(npcRegistry, npcManager), this);
    }

    /**
     * Registers and sets up all {@link PacketListener}s.
     */
    private void registerPacketEvents() {
        final @NonNull EventManager eventManager = PacketEvents.getAPI().getEventManager();

        eventManager.registerListener(new NpcSpawnPacketListener(this, npcManager), PacketListenerPriority.LOW, true);
    }

    /**
     * Registers and sets up all commands.
     */
    private void registerCommands() {
        final @NonNull ItemCommand itemCommand = new ItemCommand(itemRegistry, itemManager);
        itemCommand.register(commandManager);

        final @NonNull NpcCommand npcCommand = new NpcCommand(npcRegistry, npcManager);
        npcCommand.register(commandManager);

        new CommandExceptionHandler<CommandSender>().apply(commandManager, AudienceProvider.nativeAudience());
    }
}
