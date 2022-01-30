package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.lokkeestudios.skylands.npcsystem.NpcManager;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class RegisterNpcTeamListener implements Listener {

    /**
     * The main {@link NpcRegistry} instance,
     * which is used for event functionality.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * The main {@link NpcManager} instance,
     * which is used for event functionality.
     */
    private final @NonNull NpcManager npcManager;

    /**
     * Constructs the {@link RegisterNpcTeamListener}.
     *
     * @param npcRegistry the main {@link NpcRegistry} instance
     * @param npcManager  the main {@link NpcManager} instance
     */
    public RegisterNpcTeamListener(
            final @NonNull NpcRegistry npcRegistry,
            final @NonNull NpcManager npcManager
    ) {
        this.npcRegistry = npcRegistry;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerJoin(final @NonNull PlayerJoinEvent event) {
        final @NonNull Player player = event.getPlayer();

        handleRegisterNpcTeam(player);
    }

    private void handleRegisterNpcTeam(final @NonNull Player player) {
        final @NonNull Collection<String> entities = new ArrayList<>();
        npcRegistry.getEntityIds().forEach(entityId -> entities.add(Integer.toString(entityId)));

        final @NonNull WrapperPlayServerTeams teamsPacket = new WrapperPlayServerTeams(npcManager.getTeamName(), WrapperPlayServerTeams.TeamMode.CREATE, Optional.of(npcManager.getTeamInfo()), entities);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

        playerManager.sendPacket(channel, teamsPacket);
    }
}
