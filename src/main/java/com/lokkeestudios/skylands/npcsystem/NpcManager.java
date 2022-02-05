package com.lokkeestudios.skylands.npcsystem;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.netty.channel.ChannelAbstract;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.lokkeestudios.skylands.core.database.DatabaseManager;
import com.lokkeestudios.skylands.core.utils.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The manager for everything npc related.
 */
public final class NpcManager {

    /**
     * The String name of the npc team.
     */
    private final @NonNull String teamName;

    /**
     * The {@link WrapperPlayServerTeams.ScoreBoardTeamInfo} of the npc team.
     */
    private final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo;

    /**
     * The main {@link NpcRegistry} instance.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * The main {@link DatabaseManager} instance.
     */
    private final @NonNull DatabaseManager databaseManager;

    /**
     * Constructs a {@link NpcManager}.
     *
     * @param npcRegistry     the main {@link NpcRegistry} instance
     * @param databaseManager the main {@link DatabaseManager} instance
     */
    public NpcManager(
            final @NonNull NpcRegistry npcRegistry,
            final @NonNull DatabaseManager databaseManager
    ) {
        this.npcRegistry = npcRegistry;
        this.databaseManager = databaseManager;

        this.teamName = UUID.randomUUID().toString();
        this.teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(), null, null, WrapperPlayServerTeams.NameTagVisibility.NEVER,
                WrapperPlayServerTeams.CollisionRule.NEVER, NamedTextColor.WHITE, WrapperPlayServerTeams.OptionData.NONE
        );
        this.setupDataTables();
    }

    /**
     * Sets up the required tables for the {@link NpcRegistry} data, if needed.
     */
    public void setupDataTables() {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement createNpcDataStatement =
                        connection.prepareStatement(
                                " CREATE TABLE IF NOT EXISTS npc " +
                                        "(id VARCHAR(30) not NULL, " +
                                        " npc_type VARCHAR(30) not NULL, " +
                                        " npc_texture_value VARCHAR(1000) not NULL, " +
                                        " npc_texture_signature VARCHAR(1000) not NULL, " +
                                        " npc_name VARCHAR(30) not NULL, " +
                                        " npc_title VARCHAR(16) not NULL, " +
                                        " npc_world VARCHAR(30) not NULL, " +
                                        " npc_x DOUBLE not NULL, " +
                                        " npc_y DOUBLE not NULL, " +
                                        " npc_z DOUBLE not NULL, " +
                                        " npc_yaw FLOAT not NULL, " +
                                        " npc_pitch FLOAT not NULL, " +
                                        " PRIMARY KEY ( id ))"
                        )
        ) {
            createNpcDataStatement.execute();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves all {@link Npc}s which are stored
     * in the {@link NpcRegistry} to the database.
     */
    public void saveNpcs() {
        for (final @NonNull Npc npc : npcRegistry.getNpcs()) {
            this.saveNpc(npc);
        }
    }

    /**
     * Disables the Npc System and saves all {@link Npc}s
     * which are stored in the {@link NpcRegistry} to the database.
     */
    public void disable() {
        for (final @NonNull Npc npc : npcRegistry.getNpcs()) {
            this.saveNpc(npc);
            npc.remove();
        }
    }

    /**
     * Saves a {@link Npc}s which is stored
     * in the {@link NpcRegistry} to the database.
     *
     * @param npc the Npc which is to be saved
     */
    public void saveNpc(final @NonNull Npc npc) {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement saveNpcStatement =
                        connection.prepareStatement(
                                "UPDATE npc SET npc_type = ?, npc_texture_value = ?, npc_texture_signature = ?, npc_name = ?, npc_title = ?, " +
                                        "npc_world = ?, npc_x = ?, npc_y = ?, npc_z = ?, npc_yaw = ?, npc_pitch = ? WHERE id = ?"
                        )
        ) {
            final @NonNull Location npcLocation = npc.getLocation();

            saveNpcStatement.setString(1, npc.getType().name());
            saveNpcStatement.setString(2, npc.getTextureValue());
            saveNpcStatement.setString(3, npc.getTextureSignature());
            saveNpcStatement.setString(4, npc.getName());
            saveNpcStatement.setString(5, npc.getTitle());
            saveNpcStatement.setString(6, npcLocation.getWorld().getName());
            saveNpcStatement.setDouble(7, npcLocation.getX());
            saveNpcStatement.setDouble(8, npcLocation.getY());
            saveNpcStatement.setDouble(9, npcLocation.getZ());
            saveNpcStatement.setFloat(10, npcLocation.getYaw());
            saveNpcStatement.setFloat(11, npcLocation.getPitch());
            saveNpcStatement.setString(12, npc.getId());
            saveNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link Npc} and registers
     * it in the {@link NpcRegistry}.
     *
     * @param id       the unique String id of the Npc
     * @param type     the type of the Npc
     * @param name     the name of the Npc
     * @param location the location of the Npc
     */
    public void createNpc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String name,
            final @NonNull Location location
    ) {
        final @NonNull String textureValue = Constants.Textures.NpcDefault.TEXTURE_VALUE;
        final @NonNull String textureSignature = Constants.Textures.NpcDefault.TEXTURE_SIGNATURE;
        final @NonNull String title = Constants.Text.NPC_TITLE_DEFAULT;

        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement insertNpcStatement =
                        connection.prepareStatement(
                                "INSERT INTO npc (id, npc_type, npc_texture_value, npc_texture_signature, npc_name, npc_title, " +
                                        "npc_world, npc_x, npc_y, npc_z, npc_yaw, npc_pitch) " +
                                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )
        ) {
            insertNpcStatement.setString(1, id);
            insertNpcStatement.setString(2, type.toString());
            insertNpcStatement.setString(3, textureValue);
            insertNpcStatement.setString(4, textureSignature);
            insertNpcStatement.setString(5, name);
            insertNpcStatement.setString(6, title);
            insertNpcStatement.setString(7, location.getWorld().getName());
            insertNpcStatement.setDouble(8, location.getX());
            insertNpcStatement.setDouble(9, location.getY());
            insertNpcStatement.setDouble(10, location.getZ());
            insertNpcStatement.setFloat(11, location.getYaw());
            insertNpcStatement.setFloat(12, location.getPitch());
            insertNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        final @NonNull Npc npc = new Npc(id, type, name, location);

        npcRegistry.registerNpc(npc);
        npc.spawn();
        registerNpcToTeam(npc);
    }

    /**
     * Registers a Npc to the npc team.
     */
    public void registerNpcToTeam(final @NonNull Npc npc) {

        final @NonNull WrapperPlayServerTeams teamsPacket = new WrapperPlayServerTeams(teamName, WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, Optional.of(teamInfo), Collections.singletonList(Integer.toString(npc.getEntity().getEntityId())));

        final @NonNull PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final @NonNull ChannelAbstract channel = playerManager.getChannel(player);

            playerManager.sendPacket(channel, teamsPacket);
        }
    }

    /**
     * Deletes a {@link Npc} and unregisters
     * it from the {@link NpcRegistry}.
     *
     * @param id the id of the Npc which is to be deleted
     */
    public void deleteNpc(final @NonNull String id) {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement deleteNpcStatement =
                        connection.prepareStatement(
                                "DELETE FROM npc WHERE id = ?"
                        )
        ) {
            deleteNpcStatement.setString(1, id);
            deleteNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);
        npc.remove();
        npcRegistry.unregisterNpc(id);
    }

    /**
     * Sets the skin of a {@link Npc} skin.
     *
     * @param id               the id of the Npc
     * @param textureValue     the texture value which is to be set
     * @param textureSignature the texture signature which is to be set
     */
    public void setSkin(final @NonNull String id, final @NonNull String textureValue, final @NonNull String textureSignature) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.remove();
        npc.setTextureValue(textureValue);
        npc.setTextureSignature(textureSignature);
        npc.spawn();
    }

    /**
     * Sets the name of a {@link Npc}.
     *
     * @param id   the id of the Npc
     * @param name the name which is to be set
     */
    public void setName(final @NonNull String id, final @NonNull String name) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.setName(name);
    }

    /**
     * Sets the title of a {@link Npc}.
     *
     * @param id    the id of the Npc
     * @param title the title which is to be set
     */
    public void setTitle(final @NonNull String id, final @NonNull String title) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.setTitle(title);
    }

    /**
     * Sets the {@link Location} of a {@link Npc}.
     *
     * @param id       the id of the Npc
     * @param location the Location which is to be set
     */
    public void setLocation(final @NonNull String id, final @NonNull Location location) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.setLocation(location);
    }

    /**
     * Loads all existing {@link Npc}s from the database into memory
     * and stores them in the {@link NpcRegistry}.
     */
    public void loadNpcs() {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement loadNpcsStatement =
                        connection.prepareStatement(
                                "SELECT * FROM npc"
                        )
        ) {
            if (!loadNpcsStatement.execute()) {
                return;
            }
            try (final @NonNull ResultSet npcsResultSet = loadNpcsStatement.getResultSet()) {

                while (npcsResultSet.next()) {
                    final @NonNull String id = npcsResultSet.getString("id");
                    final @NonNull NpcType type = NpcType.valueOf(npcsResultSet.getString("npc_type"));
                    final @NonNull String textureValue = npcsResultSet.getString("npc_texture_value");
                    final @NonNull String textureSignature = npcsResultSet.getString("npc_texture_signature");
                    final @NonNull String name = npcsResultSet.getString("npc_name");
                    final @NonNull String title = npcsResultSet.getString("npc_title");

                    final @NonNull World world = Objects.requireNonNull(Bukkit.getWorld(npcsResultSet.getString("npc_world")));
                    final double x = npcsResultSet.getDouble("npc_x");
                    final double y = npcsResultSet.getDouble("npc_y");
                    final double z = npcsResultSet.getDouble("npc_z");
                    final float yaw = npcsResultSet.getFloat("npc_yaw");
                    final float pitch = npcsResultSet.getFloat("npc_pitch");

                    final @NonNull Location location = new Location(world, x, y, z, yaw, pitch);

                    final @NonNull Npc npc = new Npc(id, type, textureValue, textureSignature, name, title, location);

                    npcRegistry.registerNpc(npc);
                    npc.spawn();
                    registerNpcToTeam(npc);
                }
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the {@link Npc} related to an {@link LivingEntity}.
     *
     * @param entity the entity of the Npc which is wanted
     * @return the Npc associated to the id
     */
    public @NonNull Npc getNpcFromEntity(final @NonNull LivingEntity entity) {
        final @NonNull PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        final @NonNull String id = Objects.requireNonNull(dataContainer.get(Constants.NamespacedKeys.KEY_ID, PersistentDataType.STRING));

        return npcRegistry.getNpcFromId(id);
    }

    /**
     * Checks whether an {@link LivingEntity} corresponds to a {@link Npc}.
     *
     * @param entity the entity for which is to be checked
     * @return whether the entity id is related
     */
    public boolean isEntityNpc(final @NonNull LivingEntity entity) {
        final @NonNull PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        final String id = dataContainer.get(Constants.NamespacedKeys.KEY_ID, PersistentDataType.STRING);

        if (id == null) return false;

        return npcRegistry.isIdValid(id);
    }

    /**
     * Gets the team name of the Npc team.
     *
     * @return the team name
     */
    public @NotNull String getTeamName() {
        return teamName;
    }

    /**
     * Gets the {@link  WrapperPlayServerTeams.ScoreBoardTeamInfo} of the Npc team.
     *
     * @return the scoreboard team info
     */
    public WrapperPlayServerTeams.ScoreBoardTeamInfo getTeamInfo() {
        return teamInfo;
    }
}
