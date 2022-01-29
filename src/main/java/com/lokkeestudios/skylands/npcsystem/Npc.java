package com.lokkeestudios.skylands.npcsystem;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.lokkeestudios.skylands.core.utils.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

/**
 * The base npc with all common fields and methods.
 * <p>
 * The heart and the core of the entire NpcSystem
 */
public class Npc {

    /**
     * The String name of the npc team.
     */
    public static String teamName = UUID.randomUUID().toString();

    /**
     * The {@link WrapperPlayServerTeams.ScoreBoardTeamInfo} of the npc team.
     */
    public static WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(Component.empty(), null, null, WrapperPlayServerTeams.NameTagVisibility.NEVER, WrapperPlayServerTeams.CollisionRule.NEVER, NamedTextColor.WHITE, WrapperPlayServerTeams.OptionData.NONE);

    /**
     * A registry of every {@link Npc} entity
     * <p>
     * Key - the entity id
     * <p>
     * Value - the corresponding Npc instance
     */
    public static @NonNull Map<Integer, Npc> entities = new HashMap<>();

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
     * The location of the Npc.
     */
    protected @NonNull Location location;

    /**
     * The id of the Npc entity.
     */
    protected int entityId;

    /**
     * The entity representing the Npc
     */
    protected LivingEntity entity;

    /**
     * The entity hologram displaying the player name
     */
    protected ArmorStand entityDisplayName;

    /**
     * The entity hologram displaying the player title
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

        this.textureValue = Constants.Skins.NpcDefault.TEXTURE_VALUE;
        this.textureSignature = Constants.Skins.NpcDefault.TEXTURE_SIGNATURE;
        this.title = Constants.Text.NPC_TITLE_DEFAULT;

        spawn();
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

        spawn();
    }

    /**
     * Spawns the {@link ArmorStand} entity representing the Npc player.
     */
    public void spawn() {
        final @NonNull Consumer<ArmorStand> function = entity -> {
            this.entityId = entity.getEntityId();
            entities.put(entityId, this);
        };

        entity = location.getWorld().spawn(location, ArmorStand.class, false, function);

        entity.setGravity(false);
        entity.setCanPickupItems(false);
        entity.setCustomNameVisible(false);

        final @NonNull Location displayTitleLocation = new Location(location.getWorld(), location.getX(), (location.getY() - 0.2), location.getZ());

        entityDisplayTitle = (ArmorStand) location.getWorld().spawnEntity(displayTitleLocation, EntityType.ARMOR_STAND);

        entityDisplayTitle.setGravity(false);
        entityDisplayTitle.setCanPickupItems(false);
        entityDisplayTitle.setInvulnerable(true);
        entityDisplayTitle.setVisible(false);
        entityDisplayTitle.setCustomNameVisible(true);
        entityDisplayTitle.customName(Component.text(title).color(Constants.Text.COLOR_DEFAULT));

        final @NonNull Location displayNameLocation = new Location(location.getWorld(), location.getX(), (location.getY() + 0.1), location.getZ());

        entityDisplayName = (ArmorStand) location.getWorld().spawnEntity(displayNameLocation, EntityType.ARMOR_STAND);

        entityDisplayName.setGravity(false);
        entityDisplayName.setCanPickupItems(false);
        entityDisplayName.setInvulnerable(true);
        entityDisplayName.setVisible(false);
        entityDisplayName.setCustomNameVisible(true);
        entityDisplayName.customName(MiniMessage.get().parse(name));
    }

    /**
     * Removes the {@link ArmorStand} entity representing the Npc player.
     */
    public void remove() {
        entities.remove(entityId);
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

        final @NonNull WrapperPlayServerEntityHeadLook headLookPacket = new WrapperPlayServerEntityHeadLook(entityId, yaw);
        final @NonNull WrapperPlayServerEntityRotation rotationPacket = new WrapperPlayServerEntityRotation(entityId, yaw, pitch, true);

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

        playerManager.sendPacket(channel, headLookPacket);
        playerManager.sendPacket(channel, rotationPacket);
    }

    /**
     * Registers the Npc to the team.
     */
    public void registerToTeam() {
        final @NonNull WrapperPlayServerTeams teamsPacket = new WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, Optional.of(teamInfo), Collections.singletonList(Integer.toString(entityId)));

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

            playerManager.sendPacket(channel, teamsPacket);
        }
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
        final @NonNull Location displayLocation = location.clone().add(0, 0.1, 0);

        entity.teleportAsync(location);
        entityDisplayName.teleportAsync(displayLocation);
        entityDisplayTitle.teleportAsync(displayLocation);
        this.location = location;
    }
}