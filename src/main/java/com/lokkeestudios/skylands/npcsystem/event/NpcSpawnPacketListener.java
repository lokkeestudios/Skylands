package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.chat.component.BaseComponent;
import com.github.retrooper.packetevents.protocol.chat.component.serializer.ComponentSerializer;
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
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.npcsystem.Npc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.UUID;

public class NpcSpawnPacketListener implements PacketListener {

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

        final @NonNull String jsonName = GsonComponentSerializer.gson().serialize(Component.text(npc.getTitle()).color(Constants.Text.COLOR_DEFAULT));
        final @NonNull BaseComponent baseName = ComponentSerializer.parseJsonComponent(jsonName);

        final @NonNull GameProfile gameProfile = new GameProfile(uuid, MiniMessage.get().stripTokens(npc.getTitle()));
        final @NonNull TextureProperty texture = new TextureProperty("textures", npc.getTextureValue(), npc.getTextureSignature());
        gameProfile.getTextureProperties().add(texture);

        final WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(baseName, gameProfile, GameMode.SURVIVAL, 10);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAddPacket = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, playerData);

        final @NonNull Location location = new Location(spawnLivingEntityPacket.getPosition(), spawnLivingEntityPacket.getYaw(), spawnLivingEntityPacket.getPitch());
        final int entityId = spawnLivingEntityPacket.getEntityId();

        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayerPacket = new WrapperPlayServerSpawnPlayer(entityId, uuid, location, Collections.emptyList());

        final @NonNull EntityDataProvider dataProvider = PlayerDataProvider.builderPlayer().skinParts(SkinSection.getAllSections()).build();

        final @NonNull WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(entityId, dataProvider.encode());

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAddPacket);
        playerManager.sendPacket(channel, spawnPlayerPacket);
        playerManager.sendPacket(channel, entityMetadataPacket);
    }
}
