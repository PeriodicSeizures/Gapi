package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class EventListener implements Listener {

    public EventListener(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void event(InventoryClickEvent event) {

        Menu menu = Menu.openMenus.get(event.getWhoClicked().getUniqueId());

        if (menu != null) {
            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                return;
            }

            menu.handleInventoryClick(event);
        }
    }

    @EventHandler
    public void event(InventoryCloseEvent event) {
        Menu menu = Menu.openMenus.get(event.getPlayer().getUniqueId());

        // If the event is stupid
        if (menu != null) {
            menu.handleInventoryClose(event);
        }
    }

    @EventHandler
    public void event(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (Menu.openMenus.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

}
