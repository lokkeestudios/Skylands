package com.lokkeestudios.skylands.itemsystem;

import com.lokkeestudios.skylands.core.Rarity;
import com.lokkeestudios.skylands.core.database.DatabaseManager;
import com.lokkeestudios.skylands.core.utils.ItemSerializer;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The manager for everything item related.
 */
public final class ItemManager {

    /**
     * The main {@link ItemRegistry} instance.
     */
    private final @NonNull ItemRegistry itemRegistry;

    /**
     * The main {@link DatabaseManager} instance.
     */
    private final @NonNull DatabaseManager databaseManager;

    /**
     * Constructs an {@link ItemManager}.
     *
     * @param itemRegistry    the main {@link ItemRegistry} instance
     * @param databaseManager the main {@link DatabaseManager} instance
     */
    public ItemManager(
            final @NonNull ItemRegistry itemRegistry,
            final @NonNull DatabaseManager databaseManager
    ) {
        this.itemRegistry = itemRegistry;
        this.databaseManager = databaseManager;

        setupDataTables();
        loadItems();
    }

    /**
     * Sets up the required tables for the {@link ItemRegistry} data, if needed.
     */
    public void setupDataTables() {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement createItemDataStatement =
                        connection.prepareStatement(
                                " CREATE TABLE IF NOT EXISTS item " +
                                        "(id VARCHAR(30) not NULL, " +
                                        " item_type VARCHAR(30) not NULL, " +
                                        " item_rarity VARCHAR(30) not NULL, " +
                                        " item_item_stack VARCHAR(1000) not NULL, " +
                                        " PRIMARY KEY ( id ))"
                        )
        ) {
            createItemDataStatement.execute();
            try (
                    final @NonNull Connection connection2 = databaseManager.getConnection();
                    final @NonNull PreparedStatement createItemStatDataStatement =
                            connection2.prepareStatement(
                                    " CREATE TABLE IF NOT EXISTS item_stat " +
                                            "(item_id VARCHAR(30) not NULL, " +
                                            " item_stat_type VARCHAR(30) not NULL, " +
                                            " item_stat_value DOUBLE not NULL, " +
                                            " FOREIGN KEY( item_id ) REFERENCES item ( id ))"
                            )
            ) {
                createItemStatDataStatement.execute();
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves all {@link Item}s which are stored
     * in the {@link ItemRegistry} to the database.
     */
    public void saveItems() {
        for (final @NonNull Item item : itemRegistry.getItems()) {
            try (
                    final @NonNull Connection connection = databaseManager.getConnection();
                    final @NonNull PreparedStatement saveItemStatement =
                            connection.prepareStatement(
                                    "UPDATE item SET item_type = ?, item_rarity = ?, item_item_stack = ? WHERE id = ?"
                            )
            ) {
                saveItemStatement.setString(1, item.getType().toString());
                saveItemStatement.setString(2, item.getRarity().toString());
                saveItemStatement.setString(3, ItemSerializer.ItemStackToBase64(item.getItemStack()));
                saveItemStatement.setString(4, item.getId());
                saveItemStatement.executeUpdate();

                for (final @NonNull ItemStat current : item.getStats()) {
                    try (
                            final @NonNull Connection connection2 = databaseManager.getConnection();
                            final @NonNull PreparedStatement saveItemStatStatement =
                                    connection2.prepareStatement(
                                            "UPDATE item_stat SET item_stat_value = ? WHERE item_id = ? AND item_stat_type = ?"
                                    )
                    ) {
                        saveItemStatStatement.setDouble(1, item.getStat(current));
                        saveItemStatStatement.setString(2, item.getId());
                        saveItemStatStatement.setString(3, current.toString());
                    }
                }
            } catch (final @NonNull SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates an {@link Item} and registers
     * it in the {@link ItemRegistry}.
     *
     * @param id        the unique String id of the Item
     * @param type      the {@link ItemType} of the Item
     * @param rarity    the {@link Rarity} of the Item
     * @param itemStack the {@link ItemStat} of the Item
     */
    public void createItem(
            final @NonNull String id,
            final @NonNull ItemType type,
            final @NonNull Rarity rarity,
            final @NonNull ItemStack itemStack
    ) {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement insertItemStatement =
                        connection.prepareStatement(
                                "INSERT INTO item (id, item_type, item_rarity, item_item_stack) VALUES(?, ?, ?, ?)"
                        )
        ) {
            insertItemStatement.setString(1, id);
            insertItemStatement.setString(2, type.toString());
            insertItemStatement.setString(3, rarity.toString());
            insertItemStatement.setString(4, ItemSerializer.ItemStackToBase64(itemStack));
            insertItemStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        final @NonNull Item item = new Item(id, type, rarity, itemStack);

        itemRegistry.registerItem(item);
    }

    /**
     * Deletes an {@link Item} and unregisters
     * it from the {@link ItemRegistry}.
     *
     * @param id the id of the Item which is to be deleted
     */
    public void deleteItem(final @NonNull String id) {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement deleteItemStatement =
                        connection.prepareStatement(
                                "DELETE FROM item WHERE id = ?"
                        )
        ) {
            deleteItemStatement.setString(1, id);
            deleteItemStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        itemRegistry.unregisterItem(id);
    }

    /**
     * Sets the {@link Rarity} of an {@link Item}.
     *
     * @param id     the id of the Item
     * @param rarity the Rarity which is to be set
     */
    public void setRarity(final @NonNull String id, final @NonNull Rarity rarity) {
        final @NonNull Item item = itemRegistry.getItemFromId(id);

        item.setRarity(rarity);
    }

    /**
     * Sets the {@link ItemStack} of an {@link Item}.
     *
     * @param id        the id of the Item
     * @param itemStack the ItemStack which is to be set
     */
    public void setItemStack(final @NonNull String id, final @NonNull ItemStack itemStack) {
        final @NonNull Item item = itemRegistry.getItemFromId(id);

        item.setItemStack(itemStack);
    }

    /**
     * Sets the value of an {@link ItemStat} of an {@link Item}.
     *
     * @param id    the id of the Item
     * @param stat  the ItemStat which is to be changed
     * @param value the value which is to be set
     */
    public void setStat(final @NonNull String id, final @NonNull ItemStat stat, final double value) {
        final @NonNull Item item = itemRegistry.getItemFromId(id);

        if (item.hasStat(stat)) {
            if (value != 0) {
                item.setStat(stat, value);
                return;
            }
            try (
                    final @NonNull Connection connection = databaseManager.getConnection();
                    final @NonNull PreparedStatement deleteItemStatStatement =
                            connection.prepareStatement(
                                    "DELETE FROM item_stat WHERE item_id = ? AND item_stat_type = ?"
                            )
            ) {
                deleteItemStatStatement.setString(1, id);
                deleteItemStatStatement.setString(2, stat.toString());
                deleteItemStatStatement.executeUpdate();
            } catch (final @NonNull SQLException e) {
                throw new RuntimeException(e);
            }
            item.removeStat(stat);
            return;
        }
        if (value == 0) {
            return;
        }
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement insertItemStatStatement =
                        connection.prepareStatement(
                                "INSERT INTO item_stat (item_id, item_stat_type, item_stat_value) VALUES(?, ?, ?)"
                        )
        ) {
            insertItemStatStatement.setString(1, id);
            insertItemStatStatement.setString(2, stat.toString());
            insertItemStatStatement.setDouble(3, value);
            insertItemStatStatement.executeUpdate();
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
        item.setStat(stat, value);
    }

    /**
     * Loads all existing {@link Item}s from the database into memory
     * and stores them in the {@link ItemRegistry}.
     */
    public void loadItems() {
        try (
                final @NonNull Connection connection = databaseManager.getConnection();
                final @NonNull PreparedStatement loadItemsStatement =
                        connection.prepareStatement(
                                "SELECT * FROM item"
                        )
        ) {
            if (!loadItemsStatement.execute()) {
                return;
            }
            try (final @NonNull ResultSet itemsResultSet = loadItemsStatement.getResultSet()) {

                while (itemsResultSet.next()) {
                    final @NonNull String id = itemsResultSet.getString("id");
                    final @NonNull ItemType type = ItemType.valueOf(itemsResultSet.getString("item_type"));
                    final @NonNull Rarity rarity = Rarity.valueOf(itemsResultSet.getString("item_rarity"));
                    final @NonNull ItemStack itemStack = ItemSerializer.ItemStackFromBase64(itemsResultSet.getString("item_item_stack"));

                    final @NonNull Item item = new Item(id, type, rarity, itemStack);

                    try (
                            final @NonNull Connection connection2 = databaseManager.getConnection();
                            final @NonNull PreparedStatement loadItemStatsStatement =
                                    connection2.prepareStatement(
                                            "SELECT * FROM item_stat WHERE item_id = ?"
                                    )
                    ) {
                        loadItemStatsStatement.setString(1, id);

                        if (!loadItemStatsStatement.execute()) {
                            break;
                        }
                        try (final @NonNull ResultSet itemStatsResultSet = loadItemStatsStatement.getResultSet()) {

                            while (itemStatsResultSet.next()) {
                                final @NonNull ItemStat stat = ItemStat.valueOf(itemStatsResultSet.getString("item_stat_type"));
                                final double value = itemStatsResultSet.getDouble("item_stat_value");

                                item.setStat(stat, value);
                            }
                        }
                    }
                    itemRegistry.registerItem(item);
                }
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
