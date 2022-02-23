package com.crazicrafter1.gapi;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

class ResultParent extends Result {

    @Override
    void invoke(AbstractMenu menu, InventoryClickEvent event) {
        //if (menu.status == AbstractMenu.Status.OPEN
        //        || menu.status == AbstractMenu.Status.CLOSED) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    menu.status = AbstractMenu.Status.REROUTING;
                    menu.builder.parentMenuBuilder.open(menu.player);
                }
            }.runTaskLater(Main.getInstance(), 0);
        //}
    }
}
