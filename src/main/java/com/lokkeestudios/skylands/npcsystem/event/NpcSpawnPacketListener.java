package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.npcsystem.Npc;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import org.bukkit.entity.LivingEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link PacketSendEvent} listener, for everything related to spawning {@link Npc}s.
 */
public class NpcSpawnPacketListener implements PacketListener {

    /**
     * The main plugin instance of {@link Skylands}.
     */
    final @NonNull Skylands skylands;

    /**
     * The main {@link NpcRegistry} instance,
     * which is used for event functionality.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * Constructs the {@link NpcSpawnPacketListener}.
     *
     * @param skylands    the main plugin instance of {@link Skylands}
     * @param npcRegistry the main {@link NpcRegistry} instance
     */
    public NpcSpawnPacketListener(
            final @NonNull Skylands skylands,
            final @NonNull NpcRegistry npcRegistry
    ) {
        this.skylands = skylands;
        this.npcRegistry = npcRegistry;
    }


    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket = new WrapperPlayServerSpawnLivingEntity(event);

            if (!spawnLivingEntityPacket.getEntityType().equals(EntityTypes.ARMOR_STAND)) return;
            if (!npcRegistry.isEntityNpc(spawnLivingEntityPacket.getEntityId())) return;

            event.setCancelled(true);

            sendNpcSpawnPackets(event.getChannel(), spawnLivingEntityPacket);

        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(event);
            final int entityId = spawnPlayerPacket.getEntityId();

            if (!npcRegistry.isEntityNpc(entityId)) return;

            sendNpcModificationPackets(event.getChannel(), entityId);
        }
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param channel                 the {@link ChannelAbstract} event channel
     * @param spawnLivingEntityPacket the {@link WrapperPlayServerSpawnLivingEntity} packet for accessing the entity
     */
    private void sendNpcSpawnPackets(final @NonNull ChannelAbstract channel, final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket) {
        final int entityId = spawnLivingEntityPacket.getEntityId();
        final @NonNull Npc npc = npcRegistry.getNpcFromEntityId(entityId);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, npc.getPlayerData());

        final @NonNull Location location = new Location(spawnLivingEntityPacket.getPosition(), spawnLivingEntityPacket.getYaw(), spawnLivingEntityPacket.getPitch());

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entityId, spawnLivingEntityPacket.getEntityUUID(), location);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);
    }

    /**
     * Sends the Npc modification packets to the client.
     *
     * @param channel  the {@link ChannelAbstract} event channel
     * @param entityId the entity id of the spawned entity player
     */
    private void sendNpcModificationPackets(
            final @NonNull ChannelAbstract channel,
            final int entityId
    ) {
        final @NonNull Npc npc = npcRegistry.getNpcFromEntityId(entityId);
        final @NonNull LivingEntity entity = npc.getEntity();

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, npc.getPlayerData());

        final @NonNull Location location = new Location(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entity.getEntityId(), entity.getUniqueId(), location);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);
    }
}
