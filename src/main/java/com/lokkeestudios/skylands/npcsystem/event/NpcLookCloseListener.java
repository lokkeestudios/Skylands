package com.lokkeestudios.skylands.npcsystem.event;

import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.npcsystem.Npc;
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
     * Constructs the {@link NpcLookCloseListener}.
     *
     * @param skylands the main plugin instance of {@link Skylands}
     */
    public NpcLookCloseListener(final @NonNull Skylands skylands) {
        this.skylands = skylands;
    }

    @EventHandler
    public void onPlayerMove(final @NonNull PlayerMoveEvent event) {
        final @NonNull Player player = event.getPlayer();
        final @NonNull Location playerLocation = player.getLocation();

        for (final int entityId : Npc.entities.keySet()) {
            final @NonNull Npc npc = Npc.entities.get(entityId);
            final @NonNull Location npcLocation = npc.getLocation();

            if (npcLocation.getWorld() != playerLocation.getWorld()) continue;

            if (npcLocation.distance(playerLocation) > Constants.Variables.NPC_DISTANCE_LOOK_CLOSE) continue;

            new BukkitRunnable() {
                public void run() {
                    npc.facePlayer(player);
                }
            }.runTaskAsynchronously(skylands);
        }
    }
}
