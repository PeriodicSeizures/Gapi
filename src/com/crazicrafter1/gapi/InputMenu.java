package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public abstract class InputMenu extends Menu {

    private final ItemStack left;

    public InputMenu(String title, ItemStack left) {
        super(title);
        this.left = left;
    }

    @Override
    void onMenuClick(InventoryClickEvent event) {

        if (event.getSlot() == 2) {
            // get name of item
            ItemStack out = event.getClickedInventory().getItem(2);
            if (out != null) {
                this.onInputClick(out);
            }
        }
    }

    // no implementation
    @Override
    void setupMenu() {

    }

    @Override
    void createInventory() {
        inventory = Bukkit.createInventory(null, InventoryType.ANVIL, getTitle());
        inventory.setItem(0, left);
    }

    @Override
    void fillInventory() {
        inventory.setItem(0, left);
    }

    protected abstract void onInputClick(ItemStack output);
}
