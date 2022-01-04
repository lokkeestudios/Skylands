package com.lokkeestudios.skylands.core.event;

import com.lokkeestudios.skylands.npcsystem.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ServerLoadListener implements Listener {

    /**
     * The main {@link NpcManager}.
     */
    private final NpcManager npcManager;

    /**
     * Constructs a {@link ServerLoadListener}.
     *
     * @param npcManager the main {@link NpcManager} instance
     */
    public ServerLoadListener(final @NonNull NpcManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onServerLoad(final @NonNull ServerLoadEvent event) {
        npcManager.loadNpcs();
    }
}
