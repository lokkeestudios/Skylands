package com.lokkeestudios.skylands.npcsystem.event;

import com.lokkeestudios.skylands.npcsystem.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link PlayerMoveEvent} listener, for everything player movement related.
 */
public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(final @NonNull PlayerMoveEvent event) {
        for (final int entityId : Npc.entityPlayers.keySet()) {


        }
    }
}
