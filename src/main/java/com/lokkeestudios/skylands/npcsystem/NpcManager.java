package com.lokkeestudios.skylands.npcsystem;

import com.lokkeestudios.skylands.core.database.DatabaseManager;
import com.lokkeestudios.skylands.core.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * The manager for everything npc related.
 */
public final class NpcManager {

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

        setupDataTables();
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
                                        " npc_skin_id VARCHAR(1000) not NULL, " +
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
            try (
                    final @NonNull Connection connection = databaseManager.getConnection();
                    final @NonNull PreparedStatement saveNpcStatement =
                            connection.prepareStatement(
                                    "UPDATE npc SET npc_type = ?, npc_skin_id = ?, npc_name = ?, npc_title = ?, " +
                                            "npc_world = ?, npc_x = ?, npc_y = ?, npc_z = ?, npc_yaw = ?, npc_pitch = ? WHERE id = ?"
                            )
            ) {
                final @NonNull Location npcLocation = npc.getLocation();

                saveNpcStatement.setString(1, npc.getType().name());
                saveNpcStatement.setString(2, npc.getTextureValue());
                saveNpcStatement.setString(3, npc.getName());
                saveNpcStatement.setString(4, npc.getTitle());
                saveNpcStatement.setString(5, npcLocation.getWorld().getName());
                saveNpcStatement.setDouble(6, npcLocation.getX());
                saveNpcStatement.setDouble(7, npcLocation.getY());
                saveNpcStatement.setDouble(8, npcLocation.getZ());
                saveNpcStatement.setFloat(9, npcLocation.getYaw());
                saveNpcStatement.setFloat(10, npcLocation.getPitch());
                saveNpcStatement.setString(11, npc.getId());
                saveNpcStatement.executeUpdate();
                npc.remove();
            } catch (final @NonNull SQLException e) {
                throw new RuntimeException(e);
            }
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
        final @NonNull String skinId = Constants.Skins.SKIN_NPC_DEFAULT;
        final @NonNull String title = Constants.Text.NPC_TITLE_DEFAULT;

        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement insertNpcStatement =
                        connection.prepareStatement(
                                "INSERT INTO npc (id, npc_type, npc_skin_id, npc_name, npc_title, " +
                                        "npc_world, npc_x, npc_y, npc_z, npc_yaw, npc_pitch) " +
                                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )
        ) {
            insertNpcStatement.setString(1, id);
            insertNpcStatement.setString(2, type.toString());
            insertNpcStatement.setString(3, skinId);
            insertNpcStatement.setString(4, name);
            insertNpcStatement.setString(5, title);
            insertNpcStatement.setString(6, location.getWorld().getName());
            insertNpcStatement.setDouble(7, location.getX());
            insertNpcStatement.setDouble(8, location.getY());
            insertNpcStatement.setDouble(9, location.getZ());
            insertNpcStatement.setFloat(10, location.getYaw());
            insertNpcStatement.setFloat(11, location.getPitch());
            insertNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        final @NonNull Npc npc = new Npc(id, type, skinId, name, title, location);

        npcRegistry.registerNpc(npc);
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
        removeNpc(id);
        npcRegistry.unregisterNpc(id);
    }

    /**
     * Sets the skin id of a {@link Npc}.
     *
     * @param id     the id of the Npc
     * @param skinId the skin id which is to be set
     */
    public void setSkinId(final @NonNull String id, final @NonNull String skinId) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.setTextureValue(skinId);
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
                    final @NonNull String skinId = npcsResultSet.getString("npc_skin_id");
                    final @NonNull String name = npcsResultSet.getString("npc_name");
                    final @NonNull String title = npcsResultSet.getString("npc_title");

                    final @NonNull World world = Objects.requireNonNull(Bukkit.getWorld(npcsResultSet.getString("npc_world")));
                    final double x = npcsResultSet.getDouble("npc_x");
                    final double y = npcsResultSet.getDouble("npc_y");
                    final double z = npcsResultSet.getDouble("npc_z");
                    final float yaw = npcsResultSet.getFloat("npc_yaw");
                    final float pitch = npcsResultSet.getFloat("npc_pitch");

                    final @NonNull Location location = new Location(world, x, y, z, yaw, pitch);

                    final @NonNull Npc npc = new Npc(id, type, skinId, name, title, location);

                    npcRegistry.registerNpc(npc);
                }
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the entity player of a {@link Npc}.
     *
     * @param id the id of the Npc
     */
    public void removeNpc(final @NonNull String id) {
        final @NonNull Npc npc = npcRegistry.getNpcFromId(id);

        npc.remove();
    }
}
