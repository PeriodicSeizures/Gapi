package com.crazicrafter1.gapi;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Fixed size menu
 */
public abstract class SimplexMenu extends ComponentMenu {
    private final ItemStack background;

    @SuppressWarnings("unused")
    public SimplexMenu(String title, int columns) {
        this(title, columns, null);
    }

    public SimplexMenu(String title, int columns, ItemStack background) {
        this(title, columns, background, null);
    }

    public SimplexMenu(String title, int columns, ItemStack background, Class<Menu> previousMenuClass) {
        super(title, columns, previousMenuClass);
        this.background = background;
    }

    // No implementation
    @Override
    final void setupMenu() { }

    @Override
    final void onMenuClick(InventoryClickEvent event) {
        Component component = components.get(event.getRawSlot());

        super.onComponentClick(event, component);
    }

    @Override
    final void setupInventory() {
        // Set background then set components
        if (background != null)
            for (int i=0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }

        super.setupInventory();
    }
}
