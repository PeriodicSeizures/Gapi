package com.crazicrafter1.gapi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("SameParameterValue")
public abstract class Menu {

    /**
     * Plugin only access (package-private)
     */
    final static HashMap<UUID, Menu> openMenus = new HashMap<>();
    Inventory inventory;
    boolean justOpened = false; // prevents menu from breaking on reopen
    //Runnable runnable;
    //Consumer<Player> closeListener

    /**
     * Menu only access (private)
     */
    private String title;

    Menu(String title) {
        setTitle(title);
    }

    abstract void onMenuClick(InventoryClickEvent event);
    abstract void setupMenu();
    abstract void createInventory();
    abstract void fillInventory();

    final public void show(Player player) {
        openMenus.put(player.getUniqueId(), this);

        setupMenu(); // Component assign or whatever
        createInventory();
        fillInventory(); // assign inventory items

        //player.closeInventory();

        //new BukkitRunnable() {
        //    @Override
        //    public void run() {
                justOpened = true;
                player.openInventory(inventory);
                justOpened = false;
        //    }
        //}.runTaskLater(Main.getInstance(), 1);
        //player.updateInventory();
    }

    final String getTitle() {
        return title;
    }

    final void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes(
                '&', title);
    }

    //protected final void onClose(Runnable runnable) {
    //    this.runnable = runnable;
    //}

}
