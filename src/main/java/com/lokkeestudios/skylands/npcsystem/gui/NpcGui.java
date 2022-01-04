package com.lokkeestudios.skylands.npcsystem.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import com.lokkeestudios.skylands.core.utils.Constants;
import com.lokkeestudios.skylands.core.utils.TextUtil;
import com.lokkeestudios.skylands.core.utils.itembuilder.ItemBuilder;
import com.lokkeestudios.skylands.itemsystem.Item;
import com.lokkeestudios.skylands.npcsystem.Npc;
import com.lokkeestudios.skylands.npcsystem.NpcFilter;
import com.lokkeestudios.skylands.npcsystem.NpcRegistry;
import com.lokkeestudios.skylands.npcsystem.NpcType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

/**
 * The npc gui - an interactive way to manage and oversee all {@link Npc}s at once.
 */
public final class NpcGui {

    /**
     * The main {@link NpcRegistry} instance,
     * which is used for gui functionality.
     */
    private final @NonNull NpcRegistry npcRegistry;

    /**
     * The filter for {@link NpcType}.
     * <p>
     * {@link Npc}s of any other NpcType are being removed.
     */
    private final @Nullable NpcType typeFilter;

    /**
     * The {@link SortFilter}.
     * <p>
     * Indicates on what data {@link Npc}s are being sorted.
     */
    private final @Nullable SortFilter sortFilter;

    /**
     * The String filter.
     * <p>
     * {@link Npc}s whose name does not contain
     * the filter are being removed.
     */
    private final @Nullable String searchFilter;

    /**
     * The title of the npc gui.
     */
    private final @NonNull ComponentHolder title = ComponentHolder.of(TextUtil.toBoldComponentWithSystemGradient("Npcs Menu"));

    /**
     * The main gui object.
     */
    private ChestGui gui;

    /**
     * Constructs a {@link NpcGui}.
     *
     * @param npcRegistry the main {@link NpcRegistry} instance
     */
    public NpcGui(
            final @NonNull NpcRegistry npcRegistry
    ) {
        searchFilter = null;
        sortFilter = null;
        typeFilter = null;

        this.npcRegistry = npcRegistry;

        constructGui();
    }

    /**
     * Constructs a {@link NpcGui}.
     * <p>
     * With filter parameters, <b>only</b> for internal use
     *
     * @param searchFilter the active search filter String of the gui
     * @param sortFilter   the active {@link SortFilter} of the gui
     * @param typeFilter   the active type filter {@link NpcType} of the gui
     * @param npcRegistry  the main {@link NpcRegistry} instance
     */
    private NpcGui(
            final @Nullable String searchFilter,
            final @Nullable SortFilter sortFilter,
            final @Nullable NpcType typeFilter,
            final @NonNull NpcRegistry npcRegistry
    ) {
        this.searchFilter = searchFilter;
        this.sortFilter = sortFilter;
        this.typeFilter = typeFilter;

        this.npcRegistry = npcRegistry;

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

        final @NonNull PaginatedPane npcsPane = new PaginatedPane(1, 1, 7, 4);

        npcsPane.populateWithGuiItems(getFilteredGuiItems(npcRegistry.getNpcs()));

        gui.addPane(npcsPane);

        final @NonNull StaticPane infoPane = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack infoItem = ItemBuilder.from(Material.OAK_SIGN)
                .name(TextUtil.toComponentWithSystemGradient("Npcs Menu"))
                .lore(
                        Component.text("A database of every single existing npc.", Constants.Text.STYLE_DEFAULT),
                        Component.empty(),
                        Component.text("Currently existing npcs: ", Constants.Text.STYLE_DEFAULT)
                                .append(Component.text(npcRegistry.getNpcs().size(), Constants.Text.STYLE_HIGHLIGHTED))
                ).build();

        infoPane.addItem(new GuiItem(infoItem), 0, 0);

        gui.addPane(infoPane);

        final @NonNull StaticPane closePane = new StaticPane(4, 5, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack closeItem = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Close", Constants.Text.STYLE_ALERT))
                .build();

        closePane.addItem(new GuiItem(closeItem, event -> event.getWhoClicked().closeInventory()), 0, 0);

        gui.addPane(closePane);

        final @NonNull StaticPane previousPane = new StaticPane(0, 5, 1, 1, Pane.Priority.HIGHEST);
        final @NonNull StaticPane nextPane = new StaticPane(8, 5, 1, 1, Pane.Priority.HIGHEST);

        final @NonNull ItemStack previousItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_ARROW_LEFT)
                .name(Component.text(
                        Constants.Text.SYMBOL_ARROW_LEFT + " Previous Page", Constants.Text.STYLE_HIGHLIGHTED
                ))
                .build();
        updateNavigationDisplay(previousItem, 0, npcsPane.getPages());

        final @NonNull ItemStack nextItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_ARROW_RIGHT)
                .name(Component.text(
                        "Next Page " + Constants.Text.SYMBOL_ARROW_RIGHT, Constants.Text.STYLE_HIGHLIGHTED
                ))
                .build();
        updateNavigationDisplay(nextItem, 2, npcsPane.getPages());

        previousPane.addItem(new GuiItem(previousItem, event -> {
            npcsPane.setPage(npcsPane.getPage() - 1);

            updateNavigationDisplay(previousItem, npcsPane.getPage(), npcsPane.getPages());
            updateNavigationDisplay(nextItem, (npcsPane.getPage() + 2), npcsPane.getPages());

            if (npcsPane.getPage() == 0) {
                previousPane.setVisible(false);
            }

            nextPane.setVisible(true);
            gui.update();
        }), 0, 0);

        previousPane.setVisible(false);
        if (npcsPane.getPages() <= 1) nextPane.setVisible(false);

        nextPane.addItem(new GuiItem(nextItem, event -> {
            npcsPane.setPage(npcsPane.getPage() + 1);

            updateNavigationDisplay(previousItem, npcsPane.getPage(), npcsPane.getPages());
            updateNavigationDisplay(nextItem, (npcsPane.getPage() + 2), npcsPane.getPages());

            if (npcsPane.getPage() == npcsPane.getPages() - 1) {
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
                .from(Material.WRITABLE_BOOK)
                .name(Component.text("Search", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(searchFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(searchFilterItem, event -> {
            final @NonNull Player player = (Player) event.getWhoClicked();

            if (event.isRightClick() && searchFilter != null) {
                new NpcGui(null, sortFilter, typeFilter, npcRegistry).open(player);
                return;
            }
            openSearchGui(player);
        }), 2, 0);

        final @NonNull List<Component> sortFilterLore = getFilterItemLore(sortFilter, SortFilter.values());

        final @NonNull ItemStack sortFilterItem = ItemBuilder
                .from(Material.HOPPER)
                .name(Component.text("Sort", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(sortFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(sortFilterItem, event -> {
            final @Nullable SortFilter newSortFilter = getNewFilter(sortFilter, SortFilter.values(), event);

            new NpcGui(searchFilter, newSortFilter, typeFilter, npcRegistry).open((Player) event.getWhoClicked());
        }), 3, 0);

        final @NonNull List<Component> typeFilterLore = getFilterItemLore(typeFilter, NpcType.values());

        final @NonNull ItemStack typeFilterItem = ItemBuilder
                .from(Material.ARMOR_STAND)
                .name(Component.text("Type", Constants.Text.STYLE_HIGHLIGHTED))
                .lore(typeFilterLore)
                .build();

        toolsPane.addItem(new GuiItem(typeFilterItem, event -> {
            final @Nullable NpcType newTypeFilter = getNewFilter(typeFilter, NpcType.values(), event);

            new NpcGui(searchFilter, sortFilter, newTypeFilter, npcRegistry).open((Player) event.getWhoClicked());
        }), 5, 0);

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

        final @NonNull ItemStack backItem = ItemBuilder.head()
                .base64(Constants.Heads.BASE64_ARROW_LEFT)
                .name(Component.text("Go Back", Constants.Text.STYLE_ALERT))
                .build();

        final @NonNull ItemStack confirmItem = ItemBuilder
                .from(Material.LIME_STAINED_GLASS_PANE)
                .name(Component.text("Confirm Search", Constants.Text.STYLE_SUCCESS))
                .build();

        final @NonNull StaticPane backPane = new StaticPane(0, 0, 1, 1);

        backPane.addItem(new GuiItem(backItem, event ->
                new NpcGui(searchFilter, sortFilter, typeFilter, npcRegistry).open(player)
        ), 0, 0);

        final @NonNull StaticPane backgroundPane = new StaticPane(0, 0, 1, 1);

        backgroundPane.addItem(new GuiItem(Constants.Items.MENU_BACKGROUND), 0, 0);

        final @NonNull StaticPane confirmPane = new StaticPane(0, 0, 1, 1);

        confirmPane.addItem(new GuiItem(confirmItem, event -> {
            final @NonNull String newSearchFilter = searchGui.getRenameText();

            new NpcGui(newSearchFilter, sortFilter, typeFilter, npcRegistry).open(player);
        }), 0, 0);

        searchGui.getFirstItemComponent().addPane(backgroundPane);
        searchGui.getSecondItemComponent().addPane(backPane);
        searchGui.getResultComponent().addPane(confirmPane);

        searchGui.show(player);
    }

    /**
     * Filters out and sorts the {@link Npc}s based on the active filters.
     *
     * @param npcs the list of Npcs to be filtered and sorted
     * @return {@link List} of the Npcs' {@link #getNpcGuiItem}s.
     */
    private @NonNull List<GuiItem> getFilteredGuiItems(final @NonNull List<Npc> npcs) {
        if (searchFilter != null) {
            npcs.removeIf(npc -> !StringUtils.containsIgnoreCase(npc.getName(), searchFilter));
        }
        if (typeFilter != null) {
            npcs.removeIf(npc -> npc.getType() != typeFilter);
        }
        if (sortFilter != null) {
            npcs.sort(sortFilter.compareFunction.apply(npcs));
        }
        final @NonNull List<GuiItem> guiItems = new ArrayList<>();

        npcs.forEach(npc -> guiItems.add(getNpcGuiItem(npc)));

        return guiItems;
    }

    /**
     * Constructs the displayed {@link GuiItem} of a {@link Npc}.
     *
     * @param npc the to be displayed Npc
     * @return the constructed ItemStack.
     */
    private @NonNull GuiItem getNpcGuiItem(final @NonNull Npc npc) {
        final @NonNull Location location = npc.getLocation();

        final @NonNull DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        final @NonNull List<Component> guiItemLore = new ArrayList<>();

        if (!npc.getTitle().equals(" ")) {
            guiItemLore.add(TextUtil.resetDefaults(MiniMessage.get().parse(npc.getTitle())));
        }
        guiItemLore.addAll(Arrays.asList(
                Component.empty(),
                Component.text("Type: ", Constants.Text.STYLE_DEFAULT)
                        .append(Component.text(npc.getType().getName(), Constants.Text.STYLE_HIGHLIGHTED)
                        ),
                Component.empty(),
                Component.text("World: ", Constants.Text.STYLE_DEFAULT)
                        .append(Component.text(location.getWorld().getName(), Constants.Text.STYLE_HIGHLIGHTED)
                        ),
                Component.text("Coords: ", Constants.Text.STYLE_DEFAULT)
                        .append(Component.text(
                                decimalFormat.format(location.getX()) + ", "
                                        + decimalFormat.format(location.getY()) + ", "
                                        + decimalFormat.format(location.getZ()),
                                Constants.Text.STYLE_HIGHLIGHTED)
                        ),
                Component.empty(),
                Component.text("Click to teleport to Npc!", Constants.Text.STYLE_INFO)
        ));

        final @NonNull GuiItem guiItem = new GuiItem(ItemBuilder.head()
                .base64(npc.getTextureValue())
                .name(TextUtil.resetDefaults(MiniMessage.get().parse(npc.getName())))
                .lore(guiItemLore)
                .build(), event -> {
            final @NonNull Player player = (Player) event.getWhoClicked();
            player.teleportAsync(location);
        });


        return guiItem;
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
     * Gets the lore for a {@link NpcFilter} {@link ItemStack}.
     *
     * @param filter       the filter variable, which has the current filter assigned
     * @param filterValues the array of all values of the filter
     * @param <T>          the type of the filter
     * @return the lore for the ItemStack
     */
    private <T extends NpcFilter<?>> List<Component> getFilterItemLore(
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
     * Gets the new {@link NpcFilter} based on what happens in the
     * {@link InventoryClickEvent} when the filter {@link ItemStack} is clicked.
     *
     * @param filter       the filter variable, which has the current filter assigned
     * @param filterValues the array of all values of the filter
     * @param event        the InventoryClickEvent, invoked from clicking the filter ItemStack
     * @param <T>          the type of the filter
     * @return the new filter
     */
    private <T extends NpcFilter<?>> T getNewFilter(
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
     * Opens and renders the {@link NpcGui} for a {@link Player}.
     *
     * @param player the player for which the gui should be shown
     */
    public void open(Player player) {
        gui.show(player);
    }

    /**
     * Holds all the sort filters.
     */
    private enum SortFilter implements NpcFilter<SortFilter> {

        /**
         * The name sort filter.
         */
        NAME("Name", npcs -> Comparator.comparing(Npc::getName)),

        /**
         * The type sort filter.
         */
        TYPE("Type", npcs -> Comparator.comparing(Npc::getType)),

        /**
         * The world sort filter.
         */
        WORLD("World", npcs -> Comparator.comparing((Npc npc) -> npc.getLocation().getWorld().getName()));

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
        private final @NonNull Function<List<Npc>, Comparator<Npc>> compareFunction;

        /**
         * Constructs a {@link SortFilter}.
         *
         * @param name            the name of the sort filter
         * @param compareFunction the compare {@link Function} of the sort filter
         */
        SortFilter(final @NonNull String name, final @NonNull Function<List<Npc>, Comparator<Npc>> compareFunction) {
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
