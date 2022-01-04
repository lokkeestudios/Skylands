package com.lokkeestudios.skylands.npcsystem.event;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.impl.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.chat.component.BaseComponent;
import com.github.retrooper.packetevents.protocol.chat.component.serializer.ComponentSerializer;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.GameProfile;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.MojangAPIUtil;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.lokkeestudios.skylands.npcsystem.Npc;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
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

            sendNpcSpawnPackets(event.getChannel(), spawnEntity);
        }
    }

    /**
     * Sends the Npc {@link WrapperPlayServerSpawnPlayer} packets to the client.
     *
     * @param channel     the {@link ChannelAbstract} event channel
     * @param spawnEntity the {@link WrapperPlayServerSpawnLivingEntity} for accessing the entity
     */
    private void sendNpcSpawnPackets(final @NonNull ChannelAbstract channel, final @NonNull WrapperPlayServerSpawnLivingEntity spawnEntity) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(Npc.entityPlayers.get(spawnEntity.getEntityId()));

        final @NonNull String jsonName = GsonComponentSerializer.gson().serialize(MiniMessage.get().parse(npc.getName()));
        final @NonNull BaseComponent baseName = ComponentSerializer.parseJsonComponent(jsonName);

        final @NonNull Location location = new Location(spawnEntity.getPosition(), spawnEntity.getYaw(), spawnEntity.getPitch());
        final @NonNull UUID uuid = spawnEntity.getEntityUUID();

        UUID textureUuid = MojangAPIUtil.requestPlayerUUID("lokkee");

        final @NonNull List<TextureProperty> textures = MojangAPIUtil.requestPlayerTextureProperties(textureUuid);
        final @NonNull GameProfile gameProfile = new GameProfile(uuid, MiniMessage.get().stripTokens(npc.getName()), textures);

        final WrapperPlayServerPlayerInfo.PlayerData playerData = new WrapperPlayServerPlayerInfo.PlayerData(baseName, gameProfile, GameMode.SURVIVAL, 10);

        final @NonNull WrapperPlayServerPlayerInfo playerInfoAdd = new WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, uuid, playerData);
        final @NonNull WrapperPlayServerSpawnPlayer spawnPlayer = new WrapperPlayServerSpawnPlayer(spawnEntity.getEntityId(), uuid, location, Collections.emptyList());

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        playerManager.sendPacket(channel, playerInfoAdd);
        playerManager.sendPacket(channel, spawnPlayer);
    }
}
