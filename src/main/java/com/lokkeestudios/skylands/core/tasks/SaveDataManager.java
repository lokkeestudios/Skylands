package com.lokkeestudios.skylands.core.tasks;

import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.itemsystem.ItemManager;
import com.lokkeestudios.skylands.npcsystem.NpcManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A Manager which solemn purpose is
 * to save the cached data of all systems.
 */
public class SaveDataManager {

    /**
     * The main plugin instance of {@link Skylands}.
     */
    final @NonNull Skylands skylands;
    /**
     * The main {@link NpcManager}.
     */
    final @NonNull NpcManager npcManager;
    /**
     * The main {@link ItemManager}.
     */
    final @NonNull ItemManager itemManager;
    /**
     * The repeating save {@link BukkitTask}.
     */
    private BukkitTask repeatingTask;

    /**
     * Constructs the {@link SaveDataManager}.
     *
     * @param skylands the main plugin instance of {@link Skylands}
     */
    public SaveDataManager(
            final @NonNull Skylands skylands,
            final @NonNull NpcManager npcManager,
            final @NonNull ItemManager itemManager
    ) {
        this.skylands = skylands;
        this.npcManager = npcManager;
        this.itemManager = itemManager;
    }

    /**
     * Starts the save data task.
     * This task repeats until cancelled.
     *
     * @param interval the interval in which the {@link BukkitTask} is repeated in ticks.
     */
    public void startTask(final long interval) {
        if (this.repeatingTask != null) this.cancel();

        this.repeatingTask = new BukkitRunnable() {
            @Override
            public void run() {
                npcManager.saveNpcs();
                itemManager.saveItems();
            }
        }.runTaskTimerAsynchronously(this.skylands, interval, interval);
    }

    /**
     * Cancels the repeating save data task.
     *
     * @throws IllegalStateException if there is no active repeating task
     */
    public void cancel() throws @NonNull IllegalStateException {
        try {
            this.repeatingTask.cancel();
        } catch (final @NonNull IllegalStateException exception) {
            throw new IllegalStateException("There is no actively repeating task.");
        }
    }

    /**
     * Disables all systems and stores their
     * cached data to the database.
     */
    public void disableSystems() {
        this.npcManager.disable();
        this.itemManager.disable();
    }
}
