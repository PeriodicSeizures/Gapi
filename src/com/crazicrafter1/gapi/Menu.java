package com.crazicrafter1.gapi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public abstract class Menu {

    /**
     * Flow of menu:
     *  - instantiation
     *  - calling show will prepare inventory contents
     */

    static final HashMap<UUID, Menu> openMenus = new HashMap<>();
    private String title;
    Inventory inventory; // package-private access
    private Class<Menu> previousMenuClass; // Can be null

    boolean justOpened = false;

    Menu(String title) {
        this(title, null);
    }

    Menu(String title, Class<Menu> previousMenuClass) {
        this.title = title;
        this.previousMenuClass = previousMenuClass;
    }

    abstract void onMenuClick(InventoryClickEvent event);

    /**
     * Component init
     */
    abstract void setupMenu();

    /**
     * Inventory init
     */
    abstract void initInventory();

    /**
     * Draw items onto inventory
     */
    abstract void setupInventory();

    public final void show(Player player) {
        show(player, true);
    }

    final void show(Player player, boolean setupMenus) {
        Menu.openMenus.put(player.getUniqueId(), this);

        if (setupMenus)
            setupMenu(); // Component assign or whatever

        initInventory(); // new inventory
        setupInventory(); // assign inventory items

        justOpened = true;
        player.openInventory(inventory);
        justOpened = false;
        player.updateInventory();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes(
                '&', title);
    }
}
