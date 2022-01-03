package com.lokkeestudios.skylands.npcsystem.npc.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.chat.component.BaseComponent;
import com.github.retrooper.packetevents.protocol.chat.component.serializer.ComponentSerializer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.gameprofile.GameProfile;
import com.github.retrooper.packetevents.protocol.gameprofile.TextureProperty;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.npcsystem.npc.Npc;
import com.lokkeestudios.skylands.npcsystem.npc.NpcRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.UUID;

public class NpcSpawnPacketListener implements PacketListener {

    /**
     * The main {@link NpcRegistry}.
     */
    private final NpcRegistry npcRegistry;

    /**
     * Constructs a {@link NpcSpawnPacketListener}.
     *
     * @param npcRegistry the main {@link NpcRegistry} instance
     */
    public NpcSpawnPacketListener(final @NonNull NpcRegistry npcRegistry) {
        this.npcRegistry = npcRegistry;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            final @NonNull WrapperPlayServerSpawnLivingEntity spawnEntity = new WrapperPlayServerSpawnLivingEntity(event);

            if (!spawnEntity.getEntityType().equals(EntityTypes.ARMOR_STAND)) return;
            if (!Npc.entityPlayers.containsKey(spawnEntity.getEntityId())) return;

            event.setCancelled(true);

            sendNpcSpawnPackets(event, spawnEntity);
        }
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param event       the {@link PacketSendEvent} for accessing the event
     * @param spawnEntity the {@link WrapperPlayServerSpawnLivingEntity} for accessing the entity
     */
    private void sendNpcSpawnPackets(final @NonNull PacketSendEvent event, final @NonNull WrapperPlayServerSpawnLivingEntity spawnEntity) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(Npc.entityPlayers.get(spawnEntity.getEntityId()));

        final @NonNull String jsonName = GsonComponentSerializer.gson().serialize(MiniMessage.get().parse(npc.getName()));
        BaseComponent baseName = ComponentSerializer.parseJsonComponent(jsonName);

        final Location location = new Location(spawnEntity.getPosition(), spawnEntity.getYaw(), spawnEntity.getPitch());

        final @NonNull TextureProperty texture = new TextureProperty("textures", npc.getSkinId());
        final @NonNull GameProfile gameProfile = new GameProfile(UUID.randomUUID(), MiniMessage.get().stripTokens(npc.getName()));
        gameProfile.getTextureProperties().add(texture);

        final WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(baseName, gameProfile, GameMode.SURVIVAL, 10);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAdd = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, UUID.randomUUID(), playerData);
        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer(spawnEntity.getEntityId(), UUID.randomUUID(), location, Collections.emptyList());

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(event.getChannel(), playerInfoAdd);
        playerManager.sendPacket(event.getChannel(), spawnPlayer);
    }
}
