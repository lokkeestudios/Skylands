package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.lokkeestudios.skylands.npcsystem.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class RegisterNpcTeamListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final @NonNull PlayerJoinEvent event) {
        final @NonNull Player player = event.getPlayer();

        handleRegisterNpcTeam(player);
    }

    @EventHandler
    public void onPlayerJoin(final @NonNull ServerLoadEvent event) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            handleRegisterNpcTeam(player);
        }
    }

    private void handleRegisterNpcTeam(final @NonNull Player player) {
        final @NonNull Collection<String> entities = new ArrayList<>();

        Npc.entities.keySet().forEach(entityId -> entities.add(Integer.toString(entityId)));

        final @NonNull WrapperPlayServerTeams teamsPacket = new WrapperPlayServerTeams(Npc.teamName, WrapperPlayServerTeams.TeamMode.CREATE, Optional.of(Npc.teamInfo), entities);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

        playerManager.sendPacket(channel, teamsPacket);
    }
}
