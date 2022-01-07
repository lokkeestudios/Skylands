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
import com.github.retrooper.packetevents.protocol.player.GameProfile;
import com.github.retrooper.packetevents.protocol.player.SkinSection;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.Skylands;
import com.lokkeestudios.skylands.npcsystem.Npc;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket = new WrapperPlayServerSpawnLivingEntity(event);

            if (!spawnLivingEntityPacket.getEntityType().equals(EntityTypes.ARMOR_STAND)) return;
            if (!Npc.entityPlayers.containsKey(spawnLivingEntityPacket.getEntityId())) return;

            event.setCancelled(true);

            sendNpcSpawnPackets(event.getChannel(), spawnLivingEntityPacket);
        }
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param channel                 the {@link ChannelAbstract} event channel
     * @param spawnLivingEntityPacket the {@link WrapperPlayServerSpawnLivingEntity} packet for accessing the entity
     */
    private void sendNpcSpawnPackets(final @NonNull ChannelAbstract channel, final @NonNull WrapperPlayServerSpawnLivingEntity spawnLivingEntityPacket) {
        final @NonNull Npc npc = Npc.entityPlayers.get(spawnLivingEntityPacket.getEntityId());

        final @NonNull UUID uuid = spawnLivingEntityPacket.getEntityUUID();
        final @NonNull String name = npc.getName();

        final @NonNull GameProfile gameProfile = new GameProfile(uuid, MiniMessage.get().stripTokens(name));
        final @NonNull TextureProperty texture = new TextureProperty("textures", npc.getTextureValue(), npc.getTextureSignature());
        gameProfile.getTextureProperties().add(texture);

        final WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(null, gameProfile, GameMode.SURVIVAL, 10);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, playerData);

        final @NonNull Location location = new Location(spawnLivingEntityPacket.getPosition(), spawnLivingEntityPacket.getYaw(), spawnLivingEntityPacket.getPitch());
        final int entityId = spawnLivingEntityPacket.getEntityId();

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entityId, uuid, location);

        final @NonNull EntityDataProvider dataProvider = PlayerDataProvider.builderPlayer().skinParts(SkinSection.getAllSections()).customNameVisible(false).build();

        final @NonNull WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(entityId, dataProvider.encode());

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);
        playerManager.sendPacket(channel, entityMetadataPacket);

        new BukkitRunnable() {
            public void run() {
                final @NonNull WrapperPlayServerPlayerInfo playerInfoRemovePacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER, playerData);

                playerManager.sendPacket(channel, playerInfoRemovePacket);
            }
        }.runTaskAsynchronously(skylands);
    }
}
