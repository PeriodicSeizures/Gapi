package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventListener implements Listener {

    public EventListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void event(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Menu menu = Menu.openMenus.getOrDefault(
                p.getUniqueId(), null);

        if (menu != null) {
            if (e.getClickedInventory() == menu.inventory) {
                menu.onMenuClick(e);
            }
        }
    }

    @EventHandler
    public void event(InventoryCloseEvent e) {
        Menu menu = Menu.openMenus.get(e.getPlayer().getUniqueId());

        // If the event is stupid
        //if (menu != null) {
        //    if (!menu.justOpened) {
        //        Menu.openMenus.remove(e.getPlayer().getUniqueId());
        //        if (menu.runnable != null)
        //            menu.runnable.run();
        //    }
        //}
    }

    @EventHandler
    public void event(InventoryDragEvent e) {
        Player p = (Player)e.getWhoClicked();
        if (Menu.openMenus.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
