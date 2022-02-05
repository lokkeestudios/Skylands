package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.lokkeestudios.skylands.npcsystem.NpcManager;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public void onServerLoad(final @NonNull ServerLoadEvent event) {
        for (final @NonNull Player player : Bukkit.getOnlinePlayers())
            handleRegisterNpcTeam(player, Collections.emptyList());
    }

    @EventHandler
    public void onPlayerJoin(final @NonNull PlayerJoinEvent event) {
        final @NonNull Player player = event.getPlayer();

        final @NonNull Collection<String> entities = new ArrayList<>();
        npcRegistry.getNpcs().forEach(npc -> entities.add(Integer.toString(npc.getEntity().getEntityId())));

        handleRegisterNpcTeam(player, entities);
    }

    private void handleRegisterNpcTeam(final @NonNull Player player, final @NonNull Collection<@NonNull String> entities) {


        final @NonNull WrapperPlayServerTeams teamsPacket = new WrapperPlayServerTeams(npcManager.getTeamName(), WrapperPlayServerTeams.TeamMode.CREATE, Optional.of(npcManager.getTeamInfo()), entities);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

        playerManager.sendPacket(channel, teamsPacket);
    }
}
