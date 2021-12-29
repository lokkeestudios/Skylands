package com.lokkeestudios.skylands.npcsystem.npc;

import com.lokkeestudios.skylands.core.database.DatabaseManager;
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

        loadNpcs();
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
                                    "UPDATE npc_data SET type = ?, skinid = ?, name = ?, world = ?, x = ?, y = ?, z = ? WHERE id = ?"
                            )
            ) {
                final @NonNull Location npcLocation = npc.getLocation();

                saveNpcStatement.setString(1, npc.getType().toString());
                saveNpcStatement.setString(2, npc.getSkinId());
                saveNpcStatement.setString(3, npc.getType().name());
                saveNpcStatement.setString(4, npcLocation.getWorld().getName());
                saveNpcStatement.setDouble(5, npcLocation.getX());
                saveNpcStatement.setDouble(6, npcLocation.getY());
                saveNpcStatement.setDouble(7, npcLocation.getZ());
                saveNpcStatement.setString(8, npc.getId());
                saveNpcStatement.executeUpdate();
            } catch (final @NonNull SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates a {@link Npc} and registers
     * it in the {@link NpcRegistry}.
     *
     * @param id     the unique String id of the Npc
     * @param type   the type of the Npc
     * @param skinId the skin id of the Npc
     * @param name   the name of the Npc
     */
    public void createNpc(
            final @NonNull String id,
            final @NonNull NpcType type,
            final @NonNull String skinId,
            final @NonNull String name,
            final @NonNull Location location
    ) {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement insertNpcStatement =
                        connection.prepareStatement(
                                "INSERT INTO npc_data (id, type, skinid, name) VALUES(?, ?, ?, ?)"
                        )
        ) {
            insertNpcStatement.setString(1, id);
            insertNpcStatement.setString(2, type.toString());
            insertNpcStatement.setString(3, skinId);
            insertNpcStatement.setString(4, name);
            insertNpcStatement.setString(4, location.getWorld().getName());
            insertNpcStatement.setDouble(5, location.getX());
            insertNpcStatement.setDouble(6, location.getY());
            insertNpcStatement.setDouble(7, location.getZ());
            insertNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        final @NonNull Npc npc = new Npc(id, type, skinId, name, location);

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
                                "DELETE FROM npc_data WHERE id = ?"
                        )
        ) {
            deleteNpcStatement.setString(1, id);
            deleteNpcStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
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

        npc.setSkinId(skinId);
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
                                "SELECT * FROM npc_data"
                        )
        ) {
            if (!loadNpcsStatement.execute()) {
                return;
            }
            try (final @NonNull ResultSet npcsResultSet = loadNpcsStatement.getResultSet()) {

                while (npcsResultSet.next()) {
                    final @NonNull String id = npcsResultSet.getString("id");
                    final @NonNull NpcType type = NpcType.valueOf(npcsResultSet.getString("type"));
                    final @NonNull String skinId = npcsResultSet.getString("skinid");
                    final @NonNull String name = npcsResultSet.getString("name");

                    final @NonNull World world = Objects.requireNonNull(Bukkit.getWorld(npcsResultSet.getString("world")));
                    final double x = npcsResultSet.getDouble("x");
                    final double y = npcsResultSet.getDouble("y");
                    final double z = npcsResultSet.getDouble("z");

                    final @NonNull Location location = new Location(world, x, y, z);

                    final @NonNull Npc npc = new Npc(id, type, skinId, name, location);

                    npcRegistry.registerNpc(npc);
                }
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
