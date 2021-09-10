package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
        if (menu != null) {
            if (!menu.justOpened)
                Menu.openMenus.remove(e.getPlayer().getUniqueId());
        }
    }
}
