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
        Main.getInstance().debug("InventoryClickEvent()");

        AbstractMenu menu = AbstractMenu.openMenus.get(event.getWhoClicked().getUniqueId());

        if (menu != null) {
            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                return;
            }

            if (event.getClickedInventory() != menu.inventory)
                return;

            menu.onInventoryClick(event);
        }
    }

    @EventHandler
    public void event(InventoryCloseEvent event) {
        Main.getInstance().debug("InventoryCloseEvent()");

        AbstractMenu menu = AbstractMenu.openMenus.get(event.getPlayer().getUniqueId());

        // If the event is stupid
        // in the case of where the menu was
        // closed completely or a new menu was opened
        if (menu != null) {
            menu.onInventoryClose(event);
        }
    }

    @EventHandler
    public void event(InventoryDragEvent event) {
        Player p = (Player) event.getWhoClicked();

        AbstractMenu menu = AbstractMenu.openMenus.get(p.getUniqueId());
        if (menu != null) {
            if (event.getInventory().equals(menu.inventory))
                menu.onInventoryDrag(event);
        }
    }

}
