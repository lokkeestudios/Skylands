package com.lokkeestudios.skylands.npcsystem;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.lokkeestudios.skylands.core.utils.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The base npc with all common fields and methods.
 * <p>
 * The heart and the core of the entire NpcSystem
 */
public class Npc {

    /**
     * The unique String id of the Npc.
     * <p>
     * This Npc field is final and thus cannot be changed.
     */
    protected final @NonNull String id;

    /**
     * The {@link NpcType} of the Npc.
     * <p>
     * This Item field is final and thus cannot be changed.
     */
    protected final @NonNull NpcType type;

    /**
     * The texture value of the Npc skin.
     */
    protected @NonNull String textureValue;

    /**
     * The texture signature of the Npc skin.
     */
    protected @NonNull String textureSignature;

    /**
     * The name of the Npc.
     */
    protected @NonNull String name;

    /**
     * The title of the Npc.
     */
    protected @NonNull String title;

    /**
     * The {@link Location} of the Npc.
     */
    protected @NonNull Location location;

    /**
     * The {@link WrapperPlayServerPlayerInfo.PlayerData} of the Npc.
     */
    protected WrapperPlayServerPlayerInfo.PlayerData playerData;

    /**
     * The {@link LivingEntity} representing the Npc
     */
    protected LivingEntity entity;

    /**
     * The {@link ArmorStand} hologram displaying the player name
     */
    protected ArmorStand entityDisplayName;

    /**
     * The {@link ArmorStand} hologram displaying the player title
     */
    protected ArmorStand entityDisplayTitle;

    /**
     * Constructs a Npc.
     *
     * @param id       the unique String id of the Npc
     * @param type     the {@link NpcType} of the Npc
     * @param name     the name of the Npc
     * @param location the location of the Npc
     */
    public Npc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String name,
            final @NonNull Location location
    ) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.location = location;

        this.textureValue = Constants.Textures.NpcDefault.TEXTURE_VALUE;
        this.textureSignature = Constants.Textures.NpcDefault.TEXTURE_SIGNATURE;
        this.title = Constants.Text.NPC_TITLE_DEFAULT;
    }

    /**
     * Constructs a Npc.
     *
     * @param id               the unique String id of the Npc
     * @param type             the {@link NpcType} of the Npc
     * @param textureValue     the texture value of the Npc skin
     * @param textureSignature the texture signature of the Npc skin
     * @param name             the name of the Npc
     * @param title            the title of the Npc
     * @param location         the location of the Npc
     */
    public Npc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String textureValue,
            final @NonNull String textureSignature,
            final @NonNull String name,
            final @NonNull String title,
            final @NonNull Location location
    ) {
        this.id = id;
        this.type = type;
        this.textureValue = textureValue;
        this.textureSignature = textureSignature;
        this.name = name;
        this.title = title;
        this.location = location;
    }

    /**
     * Spawns the {@link ArmorStand} entity representing the Npc player.
     */
    public void spawn() {
        this.entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        this.entity.setGravity(false);
        this.entity.setCanPickupItems(false);
        this.entity.setCustomNameVisible(false);

        final @NonNull Location displayTitleLocation = new Location(location.getWorld(), location.getX(), (location.getY() - 0.2), location.getZ());

        this.entityDisplayTitle = (ArmorStand) location.getWorld().spawnEntity(displayTitleLocation, EntityType.ARMOR_STAND);

        this.entityDisplayTitle.setGravity(false);
        this.entityDisplayTitle.setCanPickupItems(false);
        this.entityDisplayTitle.setInvulnerable(true);
        this.entityDisplayTitle.setVisible(false);
        this.entityDisplayTitle.setCustomNameVisible(true);
        this.entityDisplayTitle.customName(Component.text(title).color(Constants.Text.COLOR_DEFAULT));

        final @NonNull Location displayNameLocation = new Location(location.getWorld(), location.getX(), (location.getY() + 0.1), location.getZ());

        this.entityDisplayName = (ArmorStand) location.getWorld().spawnEntity(displayNameLocation, EntityType.ARMOR_STAND);

        this.entityDisplayName.setGravity(false);
        this.entityDisplayName.setCanPickupItems(false);
        this.entityDisplayName.setInvulnerable(true);
        this.entityDisplayName.setVisible(false);
        this.entityDisplayName.setCustomNameVisible(true);
        this.entityDisplayName.customName(MiniMessage.get().parse(name));

        setupPlayerData();
    }

    /**
     * Sets up the {@link WrapperPlayServerPlayerInfo.PlayerData} of the Npc.
     */
    protected void setupPlayerData() {
        final @NonNull UserProfile userProfile = new UserProfile(entity.getUniqueId(), Integer.toString(entity.getEntityId()));
        final @NonNull TextureProperty texture = new TextureProperty("textures", textureValue, textureSignature);
        userProfile.getTextureProperties().add(texture);

        this.playerData = new WrapperPlayServerPlayerInfo.PlayerData(null, userProfile, GameMode.SURVIVAL, 1);
    }

    /**
     * Removes the {@link ArmorStand} entity representing the Npc player.
     */
    public void remove() {
        entity.remove();
        entityDisplayTitle.remove();
        entityDisplayName.remove();
    }

    /**
     * Faces a given {@link Player}.
     *
     * @param player the player to face
     */
    public void facePlayer(final @NonNull Player player) {
        final @NonNull Location npcLocation = location.clone();
        final @NonNull Location playerLocation = player.getLocation();

        npcLocation.setDirection(playerLocation.subtract(npcLocation).toVector());

        final byte yaw = (byte) ((npcLocation.getYaw() % 360) * 256 / 360);
        final byte pitch = (byte) ((npcLocation.getPitch() % 360) * 256 / 360);

        final @NonNull WrapperPlayServerEntityHeadLook headLookPacket = new WrapperPlayServerEntityHeadLook(entity.getEntityId(), yaw);
        final @NonNull WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entity.getEntityId(), yaw, pitch, true);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

        playerManager.sendPacket(channel, headLookPacket);
        playerManager.sendPacket(channel, rotationPacket);
    }

    /**
     * Gets the unique String id of the Npc.
     *
     * @return the Npcs id
     */
    public @NonNull String getId() {
        return id;
    }

    /**
     * Gets the {@link NpcType} of the Npc.
     *
     * @return the Npcs NpcType
     */
    public @NonNull NpcType getType() {
        return type;
    }

    /**
     * Gets the texture value of the Npc skin.
     *
     * @return the Npcs texture value
     */
    public @NonNull String getTextureValue() {
        return textureValue;
    }

    /**
     * Sets the texture value of the Npc skin.
     *
     * @param textureValue the texture value to be set
     */
    public void setTextureValue(final @NonNull String textureValue) {
        this.textureValue = textureValue;
    }

    /**
     * Gets the texture signature of the Npc skin.
     *
     * @return the Npcs texture signature
     */
    public @NonNull String getTextureSignature() {
        return textureSignature;
    }

    /**
     * Sets the texture signature of the Npc skin.
     *
     * @param textureSignature the texture signature to be set
     */
    public void setTextureSignature(final @NonNull String textureSignature) {
        this.textureSignature = textureSignature;
    }

    /**
     * Gets the name of the Npc.
     *
     * @return the Npcs name
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Sets the name of the Npc.
     *
     * @param name the name to be set
     */
    public void setName(final @NonNull String name) {
        this.entityDisplayName.customName(MiniMessage.get().parse(name));
        this.name = name;
    }

    /**
     * Gets the title of the Npc.
     *
     * @return the Npcs title
     */
    public @NonNull String getTitle() {
        return title;
    }

    /**
     * Sets the title of the Npc.
     *
     * @param title the title to be set
     */
    public void setTitle(final @NonNull String title) {
        this.entityDisplayTitle.customName(Component.text(title).color(Constants.Text.COLOR_DEFAULT));
        this.title = title;
    }

    /**
     * Gets the {@link Location} of the Npc.
     *
     * @return the Npcs location
     */
    public @NonNull Location getLocation() {
        return location;
    }

    /**
     * Sets the {@link Location} of the Npc.
     *
     * @param location the location to be set
     */
    public void setLocation(final @NonNull Location location) {
        final @NonNull Location displayTitleLocation = new Location(location.getWorld(), location.getX(), (location.getY() - 0.2), location.getZ());
        final @NonNull Location displayNameLocation = new Location(location.getWorld(), location.getX(), (location.getY() + 0.1), location.getZ());

        entity.teleportAsync(location);
        entityDisplayTitle.teleportAsync(displayTitleLocation);
        entityDisplayName.teleportAsync(displayNameLocation);

        this.location = location;
    }

    /**
     * Gets the {@link LivingEntity} representing the Npc.
     *
     * @return the entity representing the Npc
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Gets the {@link WrapperPlayServerPlayerInfo.PlayerData} of the Npc.
     *
     * @return the Npcs player data
     */
    public WrapperPlayServerPlayerInfo.PlayerData getPlayerData() {
        return playerData;
    }
}