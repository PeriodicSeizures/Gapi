package com.crazicrafter1.gapi.DEPRECATED;

import org.bukkit.inventory.ItemStack;

/**
 * Fixed size menu
 */
public class SimplexMenu extends ComponentMenu {
    private final ItemStack background;

    @SuppressWarnings("unused")
    public SimplexMenu(String title, int columns) {
        this(title, columns, null);
    }

    public SimplexMenu(String title, int columns, ItemStack background) {
        super(title, columns);
        this.background = background;
    }

    // No implementation
    @Override
    final void setupMenu() { }

    //@Override
    //final void onMenuClick(InventoryClickEvent event) {
    //    super.onMenuClick(event);
    //}

    @Override
    final void fillInventory() {
        // Set background then set components
        if (background != null)
            for (int i=0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }

        super.fillInventory();
    }



}
