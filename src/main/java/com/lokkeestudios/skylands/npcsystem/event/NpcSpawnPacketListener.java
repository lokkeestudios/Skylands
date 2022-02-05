package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.entity.data.provider.EntityDataProvider;
import com.github.retrooper.packetevents.protocol.entity.data.provider.PlayerDataProvider;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.SkinSection;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.npcsystem.Npc;
import com.lokkeestudios.skylands.npcsystem.NpcManager;
import io.github.retrooper.packetevents.utils.SpigotReflectionUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
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
     * The main {@link NpcManager} instance,
     * which is used for event functionality.
     */
    private final @NonNull NpcManager npcManager;

    /**
     * Constructs the {@link NpcSpawnPacketListener}.
     *
     * @param skylands   the main plugin instance of {@link Skylands}
     * @param npcManager the main {@link NpcManager} instance
     */
    public NpcSpawnPacketListener(
            final @NonNull Skylands skylands,
            final @NonNull NpcManager npcManager
    ) {
        this.skylands = skylands;
        this.npcManager = npcManager;
    }


    @Override
    public void onPacketSend(PacketSendEvent event) {
        final @NonNull ChannelAbstract channel = event.getChannel();

        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket = new WrapperPlayServerSpawnLivingEntity(event);
            final LivingEntity entity = (LivingEntity) SpigotReflectionUtil.getEntityById(spawnLivingEntityPacket.getEntityId());

            if (entity == null) return;
            if (!entity.getType().equals(EntityType.ARMOR_STAND)) return;
            if (!npcManager.isEntityNpc(entity)) return;

            event.setCancelled(true);

            sendNpcSpawnPackets(channel, entity);

        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(event);
            final LivingEntity entity = (LivingEntity) SpigotReflectionUtil.getEntityById(spawnPlayerPacket.getEntityId());

            if (entity == null) return;
            if (!npcManager.isEntityNpc(entity)) return;

            sendNpcModificationPackets(channel, entity);
        }
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param channel the {@link ChannelAbstract} event channel
     * @param entity  the {@link LivingEntity} of the spawned Npc
     */
    private void sendNpcSpawnPackets(final @NonNull ChannelAbstract channel, final @NonNull LivingEntity entity) {
        final @NonNull Npc npc = npcManager.getNpcFromEntity(entity);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(
                WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, npc.generatePlayerData()
        );

        final @NonNull Location location = new Location(
                entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(),
                entity.getLocation().getYaw(), entity.getLocation().getPitch()
        );

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entity.getEntityId(), entity.getUniqueId(), location);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);
    }

    /**
     * Sends the Npc modification packets to the client.
     *
     * @param channel the {@link ChannelAbstract} event channel
     * @param entity  the {@link LivingEntity} of the spawned Npc
     */
    private void sendNpcModificationPackets(
            final @NonNull ChannelAbstract channel,
            final @NonNull LivingEntity entity
    ) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final @NonNull Npc npc = npcManager.getNpcFromEntity(entity);

                final @NonNull WrapperPlayServerPlayerInfo playerInfoRemovePacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, npc.generatePlayerData());

                final @NonNull EntityDataProvider dataProvider = PlayerDataProvider.builderPlayer().skinParts(SkinSection.getAllSections()).build();
                final @NonNull WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(entity.getEntityId(), dataProvider.encode());

                final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

                playerManager.sendPacket(channel, playerInfoRemovePacket);
                playerManager.sendPacket(channel, entityMetadataPacket);

                npcManager.registerNpcToTeam(npc);
            }
        }.runTaskLater(this.skylands, 20L);
    }
}
