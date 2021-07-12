package me.lokkee.skylands.itemsystem;

import me.lokkee.skylands.core.Rarity;
import me.lokkee.skylands.core.database.DatabaseManager;
import me.lokkee.skylands.core.utils.ItemSerializer;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

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

        loadItems();
    }

    /**
     * Saves all {@link Item}s which are stored
     * in the {@link ItemRegistry} to the database.
     */
    public void saveItems() {
        for (final @NonNull Item item : itemRegistry.getItems()) {
            try (final @NonNull PreparedStatement ps =
                         databaseManager.getConnection().prepareStatement(
                                 "UPDATE item_data SET type = ?, rarity = ?, itemstack = ? WHERE id = ?"
                         )
            ) {
                ps.setString(1, item.getType().toString());
                ps.setString(2, item.getRarity().toString());
                ps.setString(3, ItemSerializer.ItemStackToBase64(item.getItemStack()));
                ps.setString(4, item.getId());
                ps.executeUpdate();

                for (final @NonNull ItemStat current : item.getStats()) {
                    try (final @NonNull PreparedStatement ps2 =
                                 databaseManager.getConnection().prepareStatement(
                                         "UPDATE itemstat_data SET value = ? WHERE id = ? AND stat = ?"
                                 )
                    ) {
                        ps2.setDouble(1, item.getStat(current));
                        ps2.setString(2, item.getId());
                        ps2.setString(3, current.toString());
                    } catch (final @NonNull SQLException e) {
                        throw new RuntimeException(e);
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
        try (final @NonNull PreparedStatement ps =
                     databaseManager.getConnection().prepareStatement(
                             "INSERT INTO item_data (id, type, rarity, itemstack) VALUES(?, ?, ?, ?)"
                     )
        ) {
            ps.setString(1, id);
            ps.setString(2, type.toString());
            ps.setString(3, rarity.toString());
            ps.setString(4, ItemSerializer.ItemStackToBase64(itemStack));
            ps.executeUpdate();
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
        try (final @NonNull PreparedStatement ps =
                     databaseManager.getConnection().prepareStatement(
                             "DELETE FROM item_data WHERE id = ?"
                     )
        ) {
            ps.setString(1, id);
            ps.executeUpdate();
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
            try (final @NonNull PreparedStatement ps =
                         databaseManager.getConnection().prepareStatement(
                                 "DELETE FROM itemstat_data WHERE id = ? AND stat = ?"
                         )
            ) {
                ps.setString(1, id);
                ps.setString(2, stat.toString());
                ps.executeUpdate();
            } catch (final @NonNull SQLException e) {
                throw new RuntimeException(e);
            }
            item.removeStat(stat);
            return;
        }
        if (value == 0) {
            return;
        }
        try (final @NonNull PreparedStatement ps =
                     databaseManager.getConnection().prepareStatement(
                             "INSERT INTO itemstat_data (id, stat, value) VALUES(?, ?, ?)"
                     )
        ) {
            ps.setString(1, id);
            ps.setString(2, stat.toString());
            ps.setDouble(3, value);
            ps.executeUpdate();
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
        try (final @NonNull PreparedStatement ps =
                     databaseManager.getConnection().prepareStatement(
                             "SELECT * FROM item_data"
                     )
        ) {
            if (!ps.execute()) {
                return;
            }
            final @NonNull ResultSet rs = ps.getResultSet();

            while (rs.next()) {
                final @NonNull String id = rs.getString("id");
                final @NonNull ItemType type = ItemType.valueOf(rs.getString("type"));
                final @NonNull Rarity rarity = Rarity.valueOf(rs.getString("rarity"));
                final @NonNull ItemStack itemStack = ItemSerializer.ItemStackFromBase64(rs.getString("itemstack"));

                final @NonNull Item item = new Item(id, type, rarity, itemStack);

                try (final @NonNull PreparedStatement ps2 =
                             databaseManager.getConnection().prepareStatement(
                                     "SELECT * FROM itemstat_data WHERE id = ?"
                             )
                ) {
                    ps2.setString(1, id);

                    if (!ps2.execute()) {
                        break;
                    }
                    final @NonNull ResultSet rs2 = ps2.getResultSet();

                    while (rs.next()) {
                        final @NonNull ItemStat stat = ItemStat.valueOf(rs2.getString("stat"));
                        final double value = rs2.getDouble("value");

                        item.setStat(stat, value);
                    }
                } catch (final @NonNull SQLException e) {
                    throw new RuntimeException(e);
                }
                itemRegistry.registerItem(item);
            }
        } catch (final @NonNull SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
