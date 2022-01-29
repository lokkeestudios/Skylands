package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.entity.data.provider.EntityDataProvider;
import com.github.retrooper.packetevents.protocol.entity.data.provider.PlayerDataProvider;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.SkinSection;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.npcsystem.Npc;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * A {@link PacketSendEvent} listener, for everything related to spawning {@link Npc}s.
 */
public class NpcSpawnPacketListener implements PacketListener {

    /**
     * The main plugin instance of {@link Skylands}.
     */
    final @NonNull Skylands skylands;

    /**
     * Constructs the {@link NpcSpawnPacketListener}.
     *
     * @param skylands the main plugin instance of {@link Skylands}
     */
    public NpcSpawnPacketListener(final @NonNull Skylands skylands) {
        this.skylands = skylands;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SPAWN_LIVING_ENTITY) return;

        final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket = new WrapperPlayServerSpawnLivingEntity(event);

        if (!spawnLivingEntityPacket.getEntityType().equals(EntityTypes.ARMOR_STAND)) return;
        if (!Npc.entities.containsKey(spawnLivingEntityPacket.getEntityId())) return;

        event.setCancelled(true);

        sendNpcSpawnPackets(event.getChannel(), spawnLivingEntityPacket);
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param channel                 the {@link ChannelAbstract} event channel
     * @param spawnLivingEntityPacket the {@link WrapperPlayServerSpawnLivingEntity} packet for accessing the entity
     */
    private void sendNpcSpawnPackets(final @NonNull ChannelAbstract channel, final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket) {
        final int entityId = spawnLivingEntityPacket.getEntityId();
        final @NonNull Npc npc = Npc.entities.get(entityId);

        final @NonNull UUID uuid = spawnLivingEntityPacket.getEntityUUID();

        final @NonNull UserProfile userProfile = new UserProfile(uuid, Integer.toString(entityId));
        final @NonNull TextureProperty texture = new TextureProperty("textures", npc.getTextureValue(), npc.getTextureSignature());
        userProfile.getTextureProperties().add(texture);

        final WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(null, userProfile, GameMode.SURVIVAL, 10);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, playerData);

        final @NonNull Location location = new Location(spawnLivingEntityPacket.getPosition(), spawnLivingEntityPacket.getYaw(), spawnLivingEntityPacket.getPitch());

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entityId, uuid, location);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);

        new BukkitRunnable() {
            public void run() {
                final @NonNull WrapperPlayServerPlayerInfo playerInfoRemovePacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, playerData);

                final @NonNull EntityDataProvider dataProvider = PlayerDataProvider.builderPlayer().skinParts(SkinSection.getAllSections()).build();
                final @NonNull WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(entityId, dataProvider.encode());

                playerManager.sendPacket(channel, playerInfoRemovePacket);
                playerManager.sendPacket(channel, entityMetadataPacket);
            }
        }.runTaskLaterAsynchronously(skylands, 10L);
    }
}
