package me.lokkee.skylands.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lokkee.skylands.Skylands;
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

        final @NonNull HikariConfig config = new HikariConfig(skylands.getDataFolder() + "/database.properties");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Sets up the database properties {@link File} and loads it.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupProperties() {
        final @NonNull File propertiesFile = new File(skylands.getDataFolder(), "database.properties");

        if (!propertiesFile.exists()) {
            propertiesFile.getParentFile().mkdirs();
            skylands.saveResource("database.properties", false);
        }
    }

    /**
     * Gets the {@link Connection} of the {@link HikariDataSource}.
     *
     * @return the Connection
     */
    public @NonNull Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
