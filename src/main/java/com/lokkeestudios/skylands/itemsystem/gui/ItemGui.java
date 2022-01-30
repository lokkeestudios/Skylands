package com.lokkeestudios.skylands.itemsystem.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import com.lokkeestudios.skylands.core.Rarity;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.core.utils.TextUtil;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
import com.lokkeestudios.skylands.core.utils.itembuilder.SkullItemBuilder;
import com.lokkeestudios.skylands.itemsystem.Item;
import com.lokkeestudios.skylands.itemsystem.ItemFilter;
import com.lokkeestudios.skylands.itemsystem.ItemRegistry;
import com.lokkeestudios.skylands.itemsystem.ItemType;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * The item gui - an interactive way to manage and oversee all {@link Item}s at once.
 */
public final class ItemGui {

    /**
     * The main {@link ItemRegistry} instance,
     * which is used for gui functionality.
     */
    private final @NonNull ItemRegistry itemRegistry;

    /**
     * The filter for {@link ItemType}.
     * <p>
     * {@link Item}s of any other ItemType are being removed.
     */
    private final @Nullable ItemType typeFilter;

    /**
     * The filter for {@link Rarity}.
     * <p>
     * {@link Item}s of any other Rarity are being removed.
     */
    private final @Nullable Rarity rarityFilter;

    /**
     * The {@link SortFilter}.
     * <p>
     * Indicates on what data {@link Item}s are being sorted.
     */
    private final @Nullable SortFilter sortFilter;

    /**
     * The String filter.
     * <p>
     * {@link Item}s whose name does not contain
     * the filter are being removed.
     */
    private final @Nullable String searchFilter;

    /**
     * The title of the item gui.
     */
    private final @NonNull ComponentHolder title = ComponentHolder.of(TextUtil.toBoldComponentWithSystemGradient("Items Menu"));

    /**
     * The main gui object.
     */
    private ChestGui gui;

    /**
     * Constructs an {@link ItemGui}.
     *
     * @param itemRegistry the main {@link ItemRegistry} instance
     */
    public ItemGui(
            final @NonNull ItemRegistry itemRegistry
    ) {
        searchFilter = null;
        sortFilter = null;
        typeFilter = null;
        rarityFilter = null;

        this.itemRegistry = itemRegistry;

        constructGui();
    }

    /**
     * Constructs an {@link ItemGui}.
     * <p>
     * With filter parameters, <b>only</b> for internal use
     *
     * @param searchFilter the active search filter String of the gui
     * @param sortFilter   the active {@link SortFilter} of the gui
     * @param typeFilter   the active type filter {@link ItemType} of the gui
     * @param rarityFilter the active rarity filter {@link Rarity} of the gui
     * @param itemRegistry the main {@link ItemRegistry} instance
     */
    private ItemGui(
            final @Nullable String searchFilter,
            final @Nullable SortFilter sortFilter,
            final @Nullable ItemType typeFilter,
            final @Nullable Rarity rarityFilter,
            final @NonNull ItemRegistry itemRegistry
    ) {
        this.searchFilter = searchFilter;
        this.sortFilter = sortFilter;
        this.typeFilter = typeFilter;
        this.rarityFilter = rarityFilter;

        this.itemRegistry = itemRegistry;

        constructGui();
    }

    /**
     * Constructs the gui and all its buttons and functionality.
     */
    private void constructGui() {
        gui = new ChestGui(6, title);

        gui.setOnGlobalClick(event -> event.setCancelled(true));

        final @NonNull OutlinePane backgroundPane = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);

        backgroundPane.addItem(new GuiItem(Constants.Items.MENU_BACKGROUND));

        backgroundPane.applyMask(new Mask(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111111111"
        ));
        backgroundPane.setRepeat(true);

        gui.addPane(backgroundPane);

        final @NonNull PaginatedPane itemsPane = new PaginatedPane(1, 1, 7, 4);

        itemsPane.setOnClick(event -> {
            if (event.getCurrentItem() != null)
                event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        });

        itemsPane.populateWithItemStacks(getFilteredItemStacks(itemRegistry.getItems()));

        gui.addPane(itemsPane);

        final @NonNull StaticPane infoPane = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack infoItem = ItemBuilder.of(Material.OAK_SIGN)
                .name(TextUtil.toComponentWithSystemGradient("Items Menu"))
                .lore(
                        Component.text("A database of every single existing item.", Constants.Text.STYLE_DEFAULT),
                        Component.empty(),
                        Component.text("Currently existing items: ", Constants.Text.STYLE_DEFAULT)
                                .append(Component.text(itemRegistry.getItems().size(), Constants.Text.STYLE_HIGHLIGHTED))
                ).build();

        infoPane.addItem(new GuiItem(infoItem), 0, 0);

        gui.addPane(infoPane);

        final @NonNull StaticPane closePane = new StaticPane(4, 5, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack closeItem = ItemBuilder.of(Material.BARRIER)
                .name(Component.text("Close", Constants.Text.STYLE_ALERT))
                .build();

        closePane.addItem(new GuiItem(closeItem, event -> event.getWhoClicked().closeInventory()), 0, 0);

        gui.addPane(closePane);

        final @NonNull StaticPane previousPane = new StaticPane(0, 5, 1, 1, Pane.Priority.HIGHEST);
        final @NonNull StaticPane nextPane = new StaticPane(8, 5, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack previousItem = SkullItemBuilder.of()
                .textures(Constants.Textures.ArrowLeft.TEXTURE_VALUE)
                .name(Component.text(
                        Constants.Text.SYMBOL_ARROW_LEFT + " Previous Page", Constants.Text.STYLE_HIGHLIGHTED
                ))
                .build();
        updateNavigationDisplay(previousItem, 0, itemsPane.getPages());

        final @NonNull ItemStack nextItem = SkullItemBuilder.of()
                .textures(Constants.Textures.ArrowRight.TEXTURE_VALUE)
                .name(Component.text(
                        "Next Page " + Constants.Text.SYMBOL_ARROW_RIGHT, Constants.Text.STYLE_HIGHLIGHTED
                ))
                .build();
        updateNavigationDisplay(nextItem, 2, itemsPane.getPages());

        previousPane.addItem(new GuiItem(previousItem, event -> {
            itemsPane.setPage(itemsPane.getPage() - 1);

            updateNavigationDisplay(previousItem, itemsPane.getPage(), itemsPane.getPages());
            updateNavigationDisplay(nextItem, (itemsPane.getPage() + 2), itemsPane.getPages());

            if (itemsPane.getPage() == 0) {
                previousPane.setVisible(false);
            }

            nextPane.setVisible(true);
            gui.update();
        }), 0, 0);

        previousPane.setVisible(false);
        if (itemsPane.getPages() <= 1) nextPane.setVisible(false);

        nextPane.addItem(new GuiItem(nextItem, event -> {
            itemsPane.setPage(itemsPane.getPage() + 1);

            updateNavigationDisplay(previousItem, itemsPane.getPage(), itemsPane.getPages());
            updateNavigationDisplay(nextItem, (itemsPane.getPage() + 2), itemsPane.getPages());

            if (itemsPane.getPage() == itemsPane.getPages() - 1) {
                nextPane.setVisible(false);
            }

            previousPane.setVisible(true);
            gui.update();
        }), 0, 0);

        gui.addPane(previousPane);
        gui.addPane(nextPane);

        final @NonNull StaticPane toolsPane = new StaticPane(0, 5, 9, 1, Pane.Priority.HIGHEST);

        final @NonNull List<Component> searchFilterLore;

        if (searchFilter == null) {
            searchFilterLore = Arrays.asList(
                    Component.empty(),
                    Component.text("Click to search!", Constants.Text.STYLE_INFO)
            );
        } else {
            searchFilterLore = Arrays.asList(
                    Component.empty(),
                    Component.text("Filter: ", Constants.Text.STYLE_DEFAULT)
                            .append(Component.text(searchFilter, Constants.Text.STYLE_HIGHLIGHTED)
                            ),
                    Component.empty(),
                    Component.text("Right-Click to clear!", Constants.Text.STYLE_INFO),
                    Component.text("Click to edit filter!", Constants.Text.STYLE_INFO)
            );
        }

        final @NonNull ItemStack searchFilterItem = ItemBuilder
                .of(Material.WRITABLE_BOOK)
                .name(Component.text("Search", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(searchFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(searchFilterItem, event -> {
            final @NonNull Player player = (Player) event.getWhoClicked();

            if (event.isRightClick() && searchFilter != null) {
                new ItemGui(null, sortFilter, typeFilter, rarityFilter, itemRegistry).open(player);
                return;
            }
            openSearchGui(player);
        }), 2, 0);

        final @NonNull List<Component> sortFilterLore = getFilterItemLore(sortFilter, SortFilter.values());

        final @NonNull ItemStack sortFilterItem = ItemBuilder
                .of(Material.HOPPER)
                .name(Component.text("Sort", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(sortFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(sortFilterItem, event -> {
            final @Nullable SortFilter newSortFilter = getNewFilter(sortFilter, SortFilter.values(), event);

            new ItemGui(searchFilter, newSortFilter, typeFilter, rarityFilter, itemRegistry).open((Player) event.getWhoClicked());
        }), 3, 0);

        final @NonNull List<Component> typeFilterLore = getFilterItemLore(typeFilter, ItemType.values());

        final @NonNull ItemStack typeFilterItem = ItemBuilder
                .of(Material.ARMOR_STAND)
                .name(Component.text("Type", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(typeFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(typeFilterItem, event -> {
            final @Nullable ItemType newTypeFilter = getNewFilter(typeFilter, ItemType.values(), event);

            new ItemGui(searchFilter, sortFilter, newTypeFilter, rarityFilter, itemRegistry).open((Player) event.getWhoClicked());
        }), 5, 0);

        final @NonNull List<Component> rarityFilterLore = getFilterItemLore(rarityFilter, Rarity.values());

        final @NonNull ItemStack rarityFilterItem = ItemBuilder
                .of(Material.ENDER_EYE)
                .name(Component.text("Rarity", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(rarityFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(rarityFilterItem, event -> {
            final @Nullable Rarity newRarityFilter = getNewFilter(rarityFilter, Rarity.values(), event);

            new ItemGui(searchFilter, sortFilter, typeFilter, newRarityFilter, itemRegistry).open((Player) event.getWhoClicked());
        }), 6, 0);

        gui.addPane(toolsPane);
    }

    /**
     * Opens the search gui for a {@link Player}.
     * <p>
     * This serves as a user input, to set the search filter String.
     *
     * @param player the player for which the gui should be opened.
     */
    private void openSearchGui(final @NonNull Player player) {
        final @NonNull AnvilGui searchGui = new AnvilGui(title);
        searchGui.setOnGlobalClick(event -> event.setCancelled(true));

        final @NonNull ItemStack backItem = SkullItemBuilder.of()
                .textures(Constants.Textures.ArrowLeft.TEXTURE_VALUE)
                .name(Component.text("Go Back", Constants.Text.STYLE_ALERT))
                .build();

        final @NonNull ItemStack confirmItem = ItemBuilder
                .of(Material.LIME_STAINED_GLASS_PANE)
                .name(Component.text("Confirm Search", Constants.Text.STYLE_SUCCESS))
                .build();

        final @NonNull StaticPane backPane = new StaticPane(0, 0, 1, 1);

        backPane.addItem(new GuiItem(backItem, event ->
                new ItemGui(searchFilter, sortFilter, typeFilter, rarityFilter, itemRegistry).open(player)
        ), 0, 0);

        final @NonNull StaticPane backgroundPane = new StaticPane(0, 0, 1, 1);

        backgroundPane.addItem(new GuiItem(Constants.Items.MENU_BACKGROUND), 0, 0);

        final @NonNull StaticPane confirmPane = new StaticPane(0, 0, 1, 1);

        confirmPane.addItem(new GuiItem(confirmItem, event -> {
            final @NonNull String newSearchFilter = searchGui.getRenameText();

            new ItemGui(newSearchFilter, sortFilter, typeFilter, rarityFilter, itemRegistry).open(player);
        }), 0, 0);

        searchGui.getFirstItemComponent().addPane(backgroundPane);
        searchGui.getSecondItemComponent().addPane(backPane);
        searchGui.getResultComponent().addPane(confirmPane);

        searchGui.show(player);
    }

    /**
     * Filters out and sorts the {@link Item}s based on the active filters.
     *
     * @param items the list of Items to be filtered and sorted
     * @return {@link List} of the Items' {@link Item#getBuildItemStack}s.
     */
    private @NonNull List<ItemStack> getFilteredItemStacks(final @NonNull List<Item> items) {
        if (searchFilter != null) {
            items.removeIf(item -> !StringUtils.containsIgnoreCase(item.getName(), searchFilter));
        }
        if (typeFilter != null) {
            items.removeIf(item -> item.getType() != typeFilter);
        }
        if (rarityFilter != null) {
            items.removeIf(item -> item.getRarity() != rarityFilter);
        }
        if (sortFilter != null) {
            items.sort(sortFilter.compareFunction.apply(items));
        }
        final @NonNull List<ItemStack> itemStacks = new ArrayList<>();

        items.forEach(item -> itemStacks.add(item.getBuildItemStack()));

        return itemStacks;
    }

    /**
     * Updates the display of a navigation arrow {@link ItemStack}.
     * <p>
     * This is done by updating the lore, which says what page it navigates to.
     *
     * @param navigationItem the navigation arrow which is to be updated
     * @param navigationPage the updated page the navigation arrow navigates to
     * @param pagesSize      the size of existing pages
     */
    private void updateNavigationDisplay(
            final @NonNull ItemStack navigationItem,
            final int navigationPage,
            final int pagesSize
    ) {
        final @NonNull ItemMeta navigationItemMeta = navigationItem.getItemMeta();
        navigationItemMeta.lore(Collections.singletonList(Component
                .text("Page: ", Constants.Text.STYLE_DEFAULT)
                .append(Component.text(navigationPage + "/" + pagesSize,
                        Constants.Text.STYLE_HIGHLIGHTED
                ))));
        navigationItem.setItemMeta(navigationItemMeta);
    }

    /**
     * Gets the lore for an {@link ItemFilter} {@link ItemStack}.
     *
     * @param filter       the filter variable, which has the current filter assigned
     * @param filterValues the array of all values of the filter
     * @param <T>          the type of the filter
     * @return the lore for the ItemStack
     */
    private <T extends ItemFilter<?>> List<Component> getFilterItemLore(
            final @Nullable T filter,
            final @NonNull T[] filterValues
    ) {
        final List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());

        lore.add(Component.text(
                filter == null ? "◆ None" : "◇ None",
                filter == null ? Constants.Text.STYLE_DOWNLIGHTED : Constants.Text.STYLE_DEFAULT
        ));
        for (final @NonNull T current : filterValues) {
            lore.add(Component.text(
                    current == filter ? "◆ " + current.getName() : "◇ " + current.getName(),
                    current == filter ? Constants.Text.STYLE_HIGHLIGHTED : Constants.Text.STYLE_DEFAULT
            ));
        }
        lore.add(Component.empty());
        lore.add(Component.text("Right-Click to go backwards!", Constants.Text.STYLE_INFO));
        lore.add(Component.text("Click to switch sort!", Constants.Text.STYLE_INFO));

        return lore;
    }

    /**
     * Gets the new {@link ItemFilter} based on what happens in the
     * {@link InventoryClickEvent} when the filter {@link ItemStack} is clicked.
     *
     * @param filter       the filter variable, which has the current filter assigned
     * @param filterValues the array of all values of the filter
     * @param event        the InventoryClickEvent, invoked from clicking the filter ItemStack
     * @param <T>          the type of the filter
     * @return the new filter
     */
    private <T extends ItemFilter<?>> T getNewFilter(
            final @Nullable T filter,
            final @NonNull T[] filterValues,
            final @NonNull InventoryClickEvent event
    ) {
        final @NonNull List<T> values = new ArrayList<>();
        values.add(null);
        values.addAll(Arrays.asList(filterValues));

        if (values.indexOf(filter) == 0 && event.isRightClick()) {
            return values.get(0);
        } else if (values.indexOf(filter) == (values.size() - 1) && !event.isRightClick()) {
            return values.get(values.size() - 1);
        } else {
            return values.get(values.indexOf(filter) + (event.isRightClick() ? -1 : 1));
        }
    }

    /**
     * Opens and renders the {@link ItemGui} for a {@link Player}.
     *
     * @param player the player for which the gui should be shown
     */
    public void open(Player player) {
        gui.show(player);
    }

    /**
     * Holds all the sort filters.
     */
    private enum SortFilter implements ItemFilter<SortFilter> {

        /**
         * The name sort filter.
         */
        NAME("Name", items -> Comparator.comparing(Item::getName)),

        /**
         * The rarity sort filter.
         */
        RARITY("Rarity", items -> Comparator.comparing((Item item) -> item.getRarity().getWeight())),

        /**
         * The type sort filter.
         */
        TYPE("Type", items -> Comparator.comparing(Item::getType));

        /**
         * The name of the sort filter.
         */
        private final @NonNull String name;

        /**
         * The compare {@link Function} of the sort filter.
         * <p>
         * This function is used for sorting the {@link Item}s
         * based on the active SortFilter.
         */
        private final @NonNull Function<List<Item>, Comparator<Item>> compareFunction;

        /**
         * Constructs a {@link SortFilter}.
         *
         * @param name            the name of the sort filter
         * @param compareFunction the compare {@link Function} of the sort filter
         */
        SortFilter(final @NonNull String name, final @NonNull Function<List<Item>, Comparator<Item>> compareFunction) {
            this.name = name;
            this.compareFunction = compareFunction;
        }

        /**
         * Gets the name of the sort filter.
         *
         * @return the name of the sort filter
         */
        public @NonNull String getName() {
            return name;
        }
    }
}
