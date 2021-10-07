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

        AbstractMenu menu = AbstractMenu.openMenus.get(event.getWhoClicked().getUniqueId());

        if (menu != null) {
            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                return;
            }

            menu.onInventoryClick(event);
        }
    }

    @EventHandler
    public void event(InventoryCloseEvent event) {
        AbstractMenu menu = AbstractMenu.openMenus.get(event.getPlayer().getUniqueId());

        // If the event is stupid
        if (menu != null) {
            menu.onInventoryClose(event);
        }
    }

    @EventHandler
    public void event(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (AbstractMenu.openMenus.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

}
