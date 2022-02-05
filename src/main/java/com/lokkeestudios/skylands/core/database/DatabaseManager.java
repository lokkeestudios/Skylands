package com.lokkeestudios.skylands.core.database;

import com.lokkeestudios.skylands.Skylands;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The manager for everything database related.
 */
public final class DatabaseManager {

    /**
     * The main plugin instance of {@link Skylands}.
     */
    private final @NonNull Skylands skylands;

    /**
     * The main {@link HikariDataSource}.
     */
    private final @NonNull HikariDataSource dataSource;

    /**
     * Constructs the {@link DatabaseManager}.
     *
     * @param skylands the main plugin instance of {@link Skylands}
     */
    public DatabaseManager(final @NonNull Skylands skylands) {
        this.skylands = skylands;

        setupProperties();

        final @NonNull HikariConfig config = new HikariConfig(this.skylands.getDataFolder() + "/database.properties");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Sets up the database properties {@link File} and loads it.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupProperties() {
        final @NonNull File propertiesFile = new File(this.skylands.getDataFolder(), "database.properties");

        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            this.skylands.saveResource("database.properties", false);
        }
    }

    /**
     * Gets the {@link Connection} of the {@link HikariDataSource}.
     *
     * @return the Connection
     */
    public @NonNull Connection getConnection() throws @NonNull SQLException {
        return this.dataSource.getConnection();
    }
}
