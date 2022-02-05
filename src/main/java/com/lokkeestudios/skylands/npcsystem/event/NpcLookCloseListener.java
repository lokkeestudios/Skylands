package com.lokkeestudios.skylands.npcsystem.event;

import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.npcsystem.Npc;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link PlayerMoveEvent} listener, for everything player movement related.
 */
public class NpcLookCloseListener implements Listener {

    /**
     * The main plugin instance of {@link Skylands}.
     */
    final @NonNull Skylands skylands;

    /**
     * The main {@link NpcRegistry} instance.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * Constructs the {@link NpcLookCloseListener}.
     *
     * @param skylands    the main plugin instance of {@link Skylands}
     * @param npcRegistry the main {@link NpcRegistry} instance
     */
    public NpcLookCloseListener(
            final @NonNull Skylands skylands,
            final @NonNull NpcRegistry npcRegistry
    ) {
        this.skylands = skylands;
        this.npcRegistry = npcRegistry;
    }

    @EventHandler
    public void onPlayerMove(final @NonNull PlayerMoveEvent event) {
        final @NonNull Player player = event.getPlayer();
        final @NonNull Location playerLocation = player.getLocation();

        for (final @NonNull Npc npc : npcRegistry.getNpcs()) {
            final @NonNull Location npcLocation = npc.getLocation();

            if (npcLocation.getWorld() != playerLocation.getWorld()) continue;

            if (npcLocation.distance(playerLocation) > Constants.Variables.NPC_DISTANCE_LOOK_CLOSE) continue;

            new BukkitRunnable() {
                @Override
                public void run() {
                    npc.facePlayer(player);
                }
            }.runTaskAsynchronously(skylands);
        }
    }
}
