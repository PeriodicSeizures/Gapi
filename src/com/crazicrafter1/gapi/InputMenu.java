package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;

public abstract class InputMenu extends Menu {

    InputMenu(String title) {
        super(title);
        //Bukkit.createInventory(null, InventoryType.ANVIL, title)
    }

    @Override
    final void onMenuClick(InventoryClickEvent event) {
        /*
         * Just print the slot for test
         */
        Main.getInstance().info("slot: " + event.getRawSlot());
    }
}
