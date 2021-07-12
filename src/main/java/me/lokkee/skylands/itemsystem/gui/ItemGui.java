package me.lokkee.skylands.itemsystem.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import me.lokkee.skylands.core.Rarity;
import me.lokkee.skylands.core.utils.Constants;
import me.lokkee.skylands.core.utils.TextUtil;
import me.lokkee.skylands.core.utils.itembuilder.ItemBuilder;
import me.lokkee.skylands.itemsystem.Item;
import me.lokkee.skylands.itemsystem.ItemRegistry;
import me.lokkee.skylands.itemsystem.ItemType;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
     * With filter parameters,  <b>only</b> for internal use
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
        gui = new ChestGui(6, ComponentHolder.of(TextUtil.toBoldComponentWithSystemGradient("Items Menu")));

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

        final @NonNull ItemStack infoItem = ItemBuilder.from(Material.OAK_SIGN)
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

        final @NonNull ItemStack closeItem = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Close", Constants.Text.STYLE_ALERT))
                .build();

        closePane.addItem(new GuiItem(closeItem, event -> event.getWhoClicked().closeInventory()), 0, 0);

        gui.addPane(closePane);

        final @NonNull StaticPane backPane = new StaticPane(0, 5, 1, 1, Pane.Priority.HIGHEST);
        final @NonNull StaticPane forwardPane = new StaticPane(8, 5, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack backItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_BACK_ARROW)
                .name(Component.text("Go Back", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(Component
                        .text("Page: ", Constants.Text.STYLE_DEFAULT)
                        .append(Component.text(0 + "/" + itemsPane.getPages(),
                                Constants.Text.STYLE_HIGHLIGHTED
                        ))
                ).build();

        final @NonNull ItemStack forwardItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_FORWARD_ARROW)
                .name(Component.text("Go Forward", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(Component
                        .text("Page: ", Constants.Text.STYLE_DEFAULT)
                        .append(Component.text(2 + "/" + itemsPane.getPages(),
                                Constants.Text.STYLE_HIGHLIGHTED
                        ))
                ).build();

        backPane.addItem(new GuiItem(backItem, event -> {
            itemsPane.setPage(itemsPane.getPage() - 1);

            Objects.requireNonNull(backItem.lore()).set(0, Component
                    .text("Page: ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text(itemsPane.getPage() + "/" + itemsPane.getPages(),
                            Constants.Text.STYLE_HIGHLIGHTED
                    ))
            );

            if (itemsPane.getPage() == 0) {
                backPane.setVisible(false);
            }

            forwardPane.setVisible(true);
            gui.update();
        }), 0, 0);

        backPane.setVisible(false);
        if (itemsPane.getPages() <= 1) forwardPane.setVisible(false);

        forwardPane.addItem(new GuiItem(forwardItem, event -> {
            itemsPane.setPage(itemsPane.getPage() + 1);

            Objects.requireNonNull(backItem.lore()).set(0, Component
                    .text("Page: ", Constants.Text.STYLE_DEFAULT)
                    .append(Component.text((itemsPane.getPage() + 2) + "/" + itemsPane.getPages(),
                            Constants.Text.STYLE_HIGHLIGHTED
                    ))
            );

            if (itemsPane.getPage() == itemsPane.getPages() - 1) {
                forwardPane.setVisible(false);
            }

            backPane.setVisible(true);
            gui.update();
        }), 0, 0);

        gui.addPane(backPane);
        gui.addPane(forwardPane);

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
                .from(Material.WRITABLE_BOOK)
                .name(Component.text("Search", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(searchFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(searchFilterItem, event -> {
            final @NonNull Player player = (Player) event.getWhoClicked();

            if (event.isRightClick()) {
                new ItemGui(null, sortFilter, typeFilter, rarityFilter, itemRegistry).open(player);
                return;
            }
            openSearchGui(player);
        }), 2, 0);

        final List<Component> sortFilterLore = new ArrayList<>();
        sortFilterLore.add(Component.empty());
        sortFilterLore.add(Component.text(
                sortFilter == null ? "◆ None" : "◇ None",
                sortFilter == null ? Constants.Text.STYLE_DOWNLIGHTED : Constants.Text.STYLE_DEFAULT
        ));
        for (final @NonNull SortFilter current : SortFilter.values()) {
            sortFilterLore.add(Component.text(
                    current == sortFilter ? "◆ " + current.getName() : "◇ " + current.getName(),
                    current == sortFilter ? Constants.Text.STYLE_HIGHLIGHTED : Constants.Text.STYLE_DEFAULT
            ));

        }
        sortFilterLore.add(Component.empty());
        sortFilterLore.add(Component.text("Right-Click to go backwards!", Constants.Text.STYLE_INFO));
        sortFilterLore.add(Component.text("Click to switch sort!", Constants.Text.STYLE_INFO));

        final @NonNull ItemStack sortFilterItem = ItemBuilder
                .from(Material.HOPPER)
                .name(Component.text("Sort", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(sortFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(sortFilterItem, event -> {
            final @NonNull List<SortFilter> values = Arrays.stream(SortFilter.values()).toList();

            final int index = sortFilter == null
                    ? 0
                    : values.indexOf(sortFilter) + (event.isRightClick() ? -1 : 1);

            final @Nullable SortFilter newSortFilter;

            if (index == values.size()) newSortFilter = values.get(0);
            else if (index == -1) newSortFilter = values.get(values.size() - 1);
            else newSortFilter = values.get(index);

            new ItemGui(searchFilter, newSortFilter, typeFilter, rarityFilter, itemRegistry).open((Player) event.getWhoClicked());
        }), 3, 0);

        final @NonNull List<Component> typeFilterLore = new ArrayList<>();
        typeFilterLore.add(Component.empty());
        typeFilterLore.add(Component.text(
                typeFilter == null ? "◆ None" : "◇ None",
                typeFilter == null ? Constants.Text.STYLE_DOWNLIGHTED : Constants.Text.STYLE_DEFAULT
        ));
        for (final @NonNull ItemType current : ItemType.values()) {
            typeFilterLore.add(Component.text(
                    current == typeFilter ? "◆ " + current.getName() : "◇ " + current.getName(),
                    current == typeFilter ? Constants.Text.STYLE_HIGHLIGHTED : Constants.Text.STYLE_DEFAULT
            ));
        }
        typeFilterLore.add(Component.empty());
        typeFilterLore.add(Component.text("Right-Click to go backwards!", Constants.Text.STYLE_INFO));
        typeFilterLore.add(Component.text("Click to switch type!", Constants.Text.STYLE_INFO));

        final @NonNull ItemStack typeFilterItem = ItemBuilder
                .from(Material.ARMOR_STAND)
                .name(Component.text("Type", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(typeFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(typeFilterItem, event -> {
            final @NonNull List<ItemType> values = Arrays.stream(ItemType.values()).toList();

            final int index = typeFilter == null
                    ? 0
                    : values.indexOf(typeFilter) + (event.isRightClick() ? -1 : 1);

            final @Nullable ItemType newTypeFilter;

            if (index == values.size()) newTypeFilter = values.get(0);
            else if (index == -1) newTypeFilter = values.get(values.size() - 1);
            else newTypeFilter = values.get(index);

            new ItemGui(searchFilter, sortFilter, newTypeFilter, rarityFilter, itemRegistry).open((Player) event.getWhoClicked());
        }), 5, 0);

        final @NonNull List<Component> rarityFilterLore = new ArrayList<>();
        rarityFilterLore.add(Component.empty());
        rarityFilterLore.add(Component.text(
                rarityFilter == null ? "◆ None" : "◇ None",
                rarityFilter == null ? Constants.Text.STYLE_DOWNLIGHTED : Constants.Text.STYLE_DEFAULT
        ));
        for (final @NonNull Rarity current : Rarity.values()) {
            final @NonNull Component line = Component.text(
                    current == rarityFilter ? "◆ " + current.getName() : "◇ " + current.getName(),
                    Constants.Text.STYLE_DEFAULT
            );
            rarityFilterLore.add(current == rarityFilter ? current.applyColor(line) : line);
        }
        rarityFilterLore.add(Component.empty());
        rarityFilterLore.add(Component.text("Right-Click to go backwards!", Constants.Text.STYLE_INFO));
        rarityFilterLore.add(Component.text("Click to switch rarity!", Constants.Text.STYLE_INFO));

        final @NonNull ItemStack rarityFilterItem = ItemBuilder
                .from(Material.ENDER_EYE)
                .name(Component.text("Rarity", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(rarityFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(rarityFilterItem, event -> {
            final @NonNull List<Rarity> values = Arrays.stream(Rarity.values()).toList();

            final int index = rarityFilter == null
                    ? 0
                    : values.indexOf(rarityFilter) + (event.isRightClick() ? -1 : 1);

            final @Nullable Rarity newRarityFilter;

            if (index == values.size()) newRarityFilter = null;
            else if (index == -1) newRarityFilter = values.get(values.size() - 1);
            else newRarityFilter = values.get(index);

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
        final @NonNull AnvilGui searchGui = new AnvilGui(
                ComponentHolder.of(((ComponentHolder) gui.getTitleHolder()).getComponent())
        );
        searchGui.setOnGlobalClick(event -> event.setCancelled(true));

        final @NonNull ItemStack backItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_BACK_ARROW)
                .name(Component.text("Go Back", Constants.Text.STYLE_ALERT))
                .build();

        final @NonNull ItemStack confirmItem = ItemBuilder
                .from(Material.LIME_STAINED_GLASS_PANE)
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
     * <p>
     * Outputs a {@link List} of the Items {@link Item#getBuildItemStack()}s.
     *
     * @param items the list of Items to be filtered and sorted
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
    private enum SortFilter {

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
